# Statement Analysis - Spring Boot

A robust Spring Boot application designed for secure document uploading and text analysis using AWS services. The application provides REST APIs for user authentication via AWS Cognito, user and account management backed by Aurora, and PDF document processing using AWS S3 and Textract. It is built as a Spring Modulith, containerized, and configured for serverless deployment on AWS Lambda using the AWS Lambda Web Adapter.

## 🚀 Features

* **Authentication:** Secure sign-up, login, password reset, and logout flows powered by AWS Cognito Identity Provider.
* **User Provisioning:** A user's profile is provisioned in the database on first successful login, decoupled from authentication via a Spring application event.
* **Account Management:** Create, update, and list financial accounts linked to the authenticated user.
* **Document Upload & Validation:** Accepts PDF file uploads, validating both file size and actual file content (not just filename/content-type).
* **AWS Integration:**
  * **Amazon S3** (with client-side KMS encryption): Securely stores uploaded PDF statements.
  * **Amazon Textract:** Initiates asynchronous text/table detection on uploaded documents, notified via SNS on completion.
  * **Amazon Aurora (RDS Data API):** Stores user and account records without a JDBC connection pool, which suits the Lambda deployment model.
* **Serverless Ready:** Configured with `aws-lambda-adapter` in the Dockerfile to run seamlessly as an AWS Lambda function.
* **CI/CD Pipeline:** Includes GitHub Actions workflows for building, containerizing, pushing to Amazon ECR, and updating the AWS Lambda function.
* **Code Quality:** Automated scanning integrated via Qodana and CodeRabbit AI.

## 🏗️ Architecture

The codebase is organized as a [Spring Modulith](https://spring.io/projects/spring-modulith), with each top-level package forming a verified module (`ModularityTests` runs `ApplicationModules.verify()` in CI/tests):

| Module | Owns | Notes |
|---|---|---|
| `authentication` | Cognito login/signup/logout | Publishes a `UserAuthenticated` event on successful login instead of writing to the database directly |
| `user` | User profile, persistence | Listens for `UserAuthenticated` to provision/update the user's DB row |
| `account` | Financial accounts | Owns `Account` entity, repository, and `AccountType` |
| `statement` | Document upload & analysis | Talks to S3 and Textract |
| `shared` | Config, request-scoped `UserContext`, exception handling, validation, RDS Data API helpers | An open module — every other module may depend on it freely |

Each module exposes only its root package (controller + DTOs) as public API; entities, repositories, and service implementations live under `<module>/internal` and are inaccessible from other modules.

## 🛠️ Tech Stack

* **Java:** 25
* **Framework:** Spring Boot (v4.0.6), Spring Modulith
* **AWS SDK:** v2 (Cognito, S3, S3 Encryption Client, Textract, RDS Data)
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
  * AWS S3 Bucket and a KMS key for encryption
  * An Aurora Serverless cluster with the RDS Data API enabled, plus a Secrets Manager secret for its credentials
  * An SNS topic (and a role Textract can assume to publish to it) for Textract job completion notifications
  * AWS IAM permissions for Textract, S3, Cognito, RDS Data, and SNS

## 🌍 Environment Variables

The application requires the following environment variables to run. These can be set in your IDE, terminal, or `.env` file during local development. All of them are bound into a single typed `app.*` configuration properties record (see `ApplicationConfigurationProperties`).

| Variable | Description |
|----------|-------------|
| `COGNITO_USER_POOL_ID` | Your AWS Cognito User Pool ID |
| `COGNITO_CLIENT_ID` | Your AWS Cognito App Client ID |
| `COGNITO_CLIENT_SECRET` | Your AWS Cognito App Client Secret |
| `AWS_S3_BUCKET_NAME` | The name of the S3 bucket where PDFs will be uploaded |
| `AWS_KMS_KEY_ALIAS` | KMS key used by the S3 encryption client to encrypt uploaded PDFs |
| `AWS_SNS_TOPIC` | SNS topic ARN that Textract notifies when document analysis completes |
| `AWS_SNS_ROLE` | IAM role ARN Textract assumes to publish to the SNS topic |
| `DB_NAME` | Aurora database name |
| `DB_CLUSTER_ARN` | Aurora cluster ARN (used by the RDS Data API) |
| `DB_SECRET_ARN` | Secrets Manager ARN holding the Aurora credentials |
| `AWS_ACCESS_KEY_ID` | Your AWS access key (for local testing) |
| `AWS_SECRET_ACCESS_KEY`| Your AWS secret key (for local testing) |

AWS region is not environment-configured — it's hardcoded per client in the `shared.config` classes: `af-south-1` for Cognito and RDS Data, `eu-west-1` for S3 and Textract.

## 🔌 API Endpoints

### Authentication (`/api/auth/v1`)
* `POST /sign-up`: Register a new Cognito user.
* `POST /login-with-password`: Authenticate a user and receive JWT tokens.
* `POST /force-password-change`: Handle the `NEW_PASSWORD_REQUIRED` Cognito challenge.
* `POST /logout`: Invalidate the user's current session globally.

### User (`/api/user/v1`)
* `GET /`: Return the authenticated user's profile, derived from their JWT claims.

### Account (`/api/account/v1`)
* `GET /`: List the authenticated user's accounts.
* `GET /{accountId}`: Get a single account by ID.
* `POST /create-account`: Create a new account for the authenticated user.
* `PUT /{accountId}`: Update an existing account.

### Statement Analysis (`/api/statement-analysis/v1`)
* `POST /upload-document`: Upload a valid PDF file. The file is saved to S3, and an AWS Textract Job is started. (Requires `multipart/form-data`)
* `POST /analyse-document`: Endpoint to handle subsequent document analysis (Placeholder).

## 🐳 Docker & Deployment

The application uses a multi-stage `Dockerfile` based on `amazoncorretto:25-al2023`. It includes the AWS Lambda Web Adapter (`public.ecr.aws/awsguru/aws-lambda-adapter:1.0.0-rc1`), allowing the standard Spring Boot web app to be invoked via API Gateway/Lambda without modifying the application code.

[![My Skills](https://skillicons.dev/icons?i=aws,terraform,java,spring,git,githubactions)](https://skillicons.dev)
