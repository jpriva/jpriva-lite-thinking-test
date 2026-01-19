variable "is_local" {
  type    = bool
}

variable "aws_region" {
  type    = string
  default = "us-east-1"
}

variable "localstack_endpoint" {
  type    = string
  default = "http://localhost:4566"
}

variable "node_runtime" {
  type    = string
  default = "nodejs20.x"
}