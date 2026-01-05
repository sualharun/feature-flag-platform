#!/bin/bash

# AWS DynamoDB Table Creation Script

TABLE_NAME=${1:-feature-flags-prod}
AWS_REGION=${2:-us-east-1}

echo "Creating DynamoDB table: $TABLE_NAME in region: $AWS_REGION"

aws dynamodb create-table \
    --table-name $TABLE_NAME \
    --attribute-definitions \
        AttributeName=flagName,AttributeType=S \
    --key-schema \
        AttributeName=flagName,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST \
    --region $AWS_REGION \
    --tags \
        Key=Environment,Value=production \
        Key=Application,Value=feature-flag-service

echo "Waiting for table to become active..."
aws dynamodb wait table-exists --table-name $TABLE_NAME --region $AWS_REGION

echo "Table created successfully!"

# Enable Point-in-Time Recovery
echo "Enabling Point-in-Time Recovery..."
aws dynamodb update-continuous-backups \
    --table-name $TABLE_NAME \
    --point-in-time-recovery-specification PointInTimeRecoveryEnabled=true \
    --region $AWS_REGION

echo "DynamoDB setup complete!"
