#!/bin/bash

# Build and push Docker image to AWS ECR

set -e

AWS_REGION=${AWS_REGION:-us-east-1}
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
ECR_REPOSITORY=feature-flag-service
IMAGE_TAG=${1:-latest}

echo "Building Docker image..."
docker build -t $ECR_REPOSITORY:$IMAGE_TAG .

echo "Logging into ECR..."
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com

echo "Creating ECR repository if it doesn't exist..."
aws ecr describe-repositories --repository-names $ECR_REPOSITORY --region $AWS_REGION || \
aws ecr create-repository --repository-name $ECR_REPOSITORY --region $AWS_REGION

echo "Tagging image..."
docker tag $ECR_REPOSITORY:$IMAGE_TAG $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPOSITORY:$IMAGE_TAG

echo "Pushing image to ECR..."
docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPOSITORY:$IMAGE_TAG

echo "Image pushed successfully!"
echo "Image URI: $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPOSITORY:$IMAGE_TAG"
