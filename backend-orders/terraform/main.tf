provider "aws" {
  region     = var.aws_region
  access_key = var.is_local ? "test" : null
  secret_key = var.is_local ? "test" : null

  skip_credentials_validation = var.is_local
  skip_metadata_api_check     = var.is_local
  skip_requesting_account_id  = var.is_local
  s3_use_path_style           = var.is_local

  dynamic "endpoints" {
    for_each = var.is_local ? [1] : []
    content {
      s3     = var.localstack_endpoint
      sqs    = var.localstack_endpoint
      lambda = var.localstack_endpoint
      ses    = var.localstack_endpoint
      iam    = var.localstack_endpoint
      sts    = var.localstack_endpoint
    }
  }
}

resource "aws_s3_bucket" "inventory_reports" {
  bucket = "inventory-report"
}

resource "aws_sqs_queue" "notification_queue_dlq" {
  name = "notification-pdf-queue-dlq"
}

resource "aws_sqs_queue" "notification_queue" {
  name                       = "notification-pdf-queue"
  visibility_timeout_seconds = 30
  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.notification_queue_dlq.arn
    maxReceiveCount     = 3
  })
}

resource "null_resource" "lambda_dependencies" {
  provisioner "local-exec" {
    command = "cd ${path.module}/lambda && npm install"
  }

  triggers = {
    dependencies_json = filemd5("${path.module}/lambda/package.json")
  }
}

data "archive_file" "lambda_zip" {
  type        = "zip"
  source_dir  = "${path.module}/lambda"
  output_path = "${path.module}/lambda_function.zip"
  depends_on = [null_resource.lambda_dependencies]
}

resource "aws_iam_role" "lambda_exec" {
  name = "lambda_exec_role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = { Service = "lambda.amazonaws.com" }
    }]
  })
}

resource "aws_lambda_function" "email_processor" {
  function_name    = "EmailProcessor"
  role             = aws_iam_role.lambda_exec.arn
  handler          = "index.handler"
  runtime          = var.node_runtime
  filename         = data.archive_file.lambda_zip.output_path
  source_code_hash = data.archive_file.lambda_zip.output_base64sha256

  environment {
    variables = {
      AWS_ENDPOINT = var.is_local ? "http://localstack:4566" : ""
      AWS_REGION   = var.aws_region
    }
  }
}

resource "aws_lambda_event_source_mapping" "sqs_trigger" {
  event_source_arn = aws_sqs_queue.notification_queue.arn
  function_name    = aws_lambda_function.email_processor.arn
  batch_size       = 1
}

resource "aws_ses_email_identity" "verified_email" {
  count = var.is_local ? 1 : 0
  email = "jpriva@outlook.com"
}