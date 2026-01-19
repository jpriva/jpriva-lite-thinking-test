import { SESClient, SendEmailCommand } from "@aws-sdk/client-ses";
import { S3Client, GetObjectCommand } from "@aws-sdk/client-s3";
import { getSignedUrl } from "@aws-sdk/s3-request-presigner";

const config = {
    region: process.env.AWS_REGION || "us-east-1"
};

if (process.env.AWS_ENDPOINT) {
    config.endpoint = process.env.AWS_ENDPOINT;
}

const sesClient = new SESClient(config);

const s3Client = new S3Client({
    ...config,
    forcePathStyle: !!process.env.AWS_ENDPOINT
});

export const handler = async (event) => {
    for (const record of event.Records) {
        try {
            const { fileName, email, companyName } = JSON.parse(record.body);

            const command = new GetObjectCommand({
                Bucket: process.env.S3_BUCKET_NAME || "inventory-report",
                Key: fileName
            });

            const signedUrl = await getSignedUrl(s3Client, command, { expiresIn: 3600 });

            const emailCommand = new SendEmailCommand({
                Source: "jpriva@outlook.com",
                Destination: {
                    ToAddresses: [email],
                },
                Message: {
                    Subject: {
                        Data: `Inventory Report - ${companyName}`,
                        Charset: "UTF-8"
                    },
                    Body: {
                        Html: {
                            Data: `
                                <div style="font-family: sans-serif; padding: 20px;">
                                    <h2>Hello,</h2>
                                    <p>The inventory report for <strong>${companyName}</strong> is ready.</p>
                                    <p>You can download the PDF document by clicking the button below:</p>
                                    <div style="margin: 25px 0;">
                                        <a href="${signedUrl}" 
                                           style="background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">
                                            Download Inventory Report
                                        </a>
                                    </div>
                                    <p style="color: #666; font-size: 0.9em;">This link will expire in 1 hour.</p>
                                    <hr>
                                    <p>Regards,<br>${companyName} Team</p>
                                </div>
                            `,
                            Charset: "UTF-8"
                        }
                    }
                }
            });

            await sesClient.send(emailCommand);
            console.log(`Email sent to ${email} for file ${fileName}`);
        } catch (error) {
            console.error("Error processing SQS record:", error);
            throw error;
        }
    }
};