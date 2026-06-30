# Statement Analysis - Spring Boot

A robust Spring Boot application designed for secure document uploading and text analysis using AWS services. The application provides REST APIs for user authentication via AWS Cognito and PDF document processing using AWS S3 and Textract. It is containerized and configured for serverless deployment on AWS Lambda using the AWS Lambda Web Adapter.

## 🚀 Features

* **Authentication:** Secure user login, password reset, and logout flows powered by AWS Cognito Identity Provider.
* **Document Upload & Validation:** Accepts PDF file uploads, performing strict validation on file type and content.
* **AWS Integration:** * **Amazon S3:** Securely stores uploaded PDF statements.
  * **Amazon Textract:** Initiates asynchronous text detection on uploaded documents.
* **Serverless Ready:** Configured with `aws-lambda-adapter` in the Dockerfile to run seamlessly as an AWS Lambda function.
* **CI/CD Pipeline:** Includes GitHub Actions workflows for building, containerizing, pushing to Amazon ECR, and updating the AWS Lambda function.
* **Code Quality:** Automated scanning integrated via Qodana and CodeRabbit AI.

## 🛠️ Tech Stack

* **Java:** 25
* **Framework:** Spring Boot (v4.0.6)
* **AWS SDK:** v2 (Cognito, S3, Textract)
* **Build Tool:** Maven
* **Containerization:** Docker
* **Deployment:** AWS Lambda, Amazon ECR
* **CI/CD:** GitHub Actions

## ⚙️ Prerequisites

To run this project locally, you will need:
* Java 25 installed
* Maven installed
* An active AWS Account with the following configured:
  * AWS Cognito User Pool & Client
  * AWS S3 Bucket
  * AWS IAM permissions for Textract, S3, and Cognito

## 🌍 Environment Variables

The application requires the following environment variables to run. These can be set in your IDE, terminal, or `.env` file during local development.

| Variable | Description |
|----------|-------------|
| `COGNITO_USER_POOL_ID` | Your AWS Cognito User Pool ID |
| `COGNITO_CLIENT_ID` | Your AWS Cognito App Client ID |
| `COGNITO_CLIENT_SECRET` | Your AWS Cognito App Client Secret |
| `AWS_S3_BUCKET_NAME` | The name of the S3 bucket where PDFs will be uploaded |
| `AWS_REGION` | Assumed to be `af-south-1` for Cognito and `eu-west-1` for S3/Textract as per config classes |
| `AWS_ACCESS_KEY_ID` | Your AWS access key (for local testing) |
| `AWS_SECRET_ACCESS_KEY`| Your AWS secret key (for local testing) |

## 🔌 API Endpoints

### Authentication (`/auth/api/v1`)
* `POST /login-with-password`: Authenticate a user and receive JWT tokens.
* `POST /force-password-change`: Handle the `NEW_PASSWORD_REQUIRED` Cognito challenge.
* `POST /logout`: Invalidate the user's current session globally.

### Statement Analysis (`/statement-analysis/api/v1`)
* `POST /upload-document`: Upload a valid PDF file. The file is saved to S3, and an AWS Textract Job is started. (Requires `multipart/form-data`)
* `POST /analyse-document`: Endpoint to handle subsequent document analysis (Placeholder).

## 🐳 Docker & Deployment

The application uses a multi-stage `Dockerfile` based on `amazoncorretto:25-al2023`. It includes the AWS Lambda Web Adapter (`public.ecr.aws/awsguru/aws-lambda-adapter:1.0.0-rc1`), allowing the standard Spring Boot web app to be invoked via API Gateway/Lambda without modifying the application code.

[![My Skills](https://skillicons.dev/icons?i=aws,terraform,java,spring,git,githubactions&perline=4)](https://skillicons.dev)
