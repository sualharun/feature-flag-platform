# Feature Flag & Experimentation Platform

A production-grade feature flag service built with Java 17, Spring Boot, AWS DynamoDB, and Redis. This platform enables safe feature releases, gradual rollouts, instant rollbacks, and A/B testing capabilities.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![AWS](https://img.shields.io/badge/AWS-DynamoDB%20%7C%20ElastiCache-yellow.svg)](https://aws.amazon.com/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)

## 🚀 Features

- **Feature Flag Management** - Create, update, delete, and retrieve feature flags
- **Percentage-Based Rollouts** - Gradual rollout from 0% to 100% with deterministic hashing
- **Instant Kill Switch** - Emergency disable any feature instantly
- **Redis Caching** - Low-latency evaluation with distributed caching
- **Health Checks** - Comprehensive monitoring and observability
- **Production-Ready** - Complete error handling, validation, and logging
- **AWS Native** - Built for deployment on AWS with DynamoDB and ElastiCache
- **API Documentation** - Interactive Swagger UI documentation

## 📋 Architecture

```
┌─────────────────────┐
│ Client Application  │
└──────────┬──────────┘
           │
           ↓
┌─────────────────────────┐
│ Feature Flag SDK (Java) │
└──────────┬──────────────┘
           │
           ↓
┌──────────────────────────────┐
│ Redis Cache (ElastiCache)    │
└──────────┬───────────────────┘
           │
           ↓
┌──────────────────────────────┐
│ Flag Service (Spring Boot)   │
└──────────┬───────────────────┘
           │
           ↓
┌──────────────────────────────┐
│ DynamoDB                     │
└──────────────────────────────┘
```

## 🛠️ Tech Stack

- **Backend**: Java 17, Spring Boot 4.0.1
- **Database**: AWS DynamoDB
- **Cache**: Redis (AWS ElastiCache)
- **Containerization**: Docker, Docker Compose
- **Cloud**: AWS (ECS, EC2, CloudWatch)
- **API Documentation**: OpenAPI 3.0 (Swagger)
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **Build Tool**: Maven

## 📦 Prerequisites

- Java 17 or higher
- Maven 3.8+
- Docker & Docker Compose (for local development)
- AWS CLI (for deployment)
- AWS Account with appropriate permissions

## 🚀 Quick Start

### Local Development with Docker Compose

1. **Clone the repository**
```bash
git clone https://github.com/sualharun/feature-flag-platform.git
cd feature-flag-platform
```

2. **Build and run with Docker Compose**
```bash
docker-compose up --build
```

This will start:
- Feature Flag Service on `http://localhost:8080`
- Redis on `localhost:6379`
- DynamoDB Local on `localhost:8000`

3. **Create the DynamoDB table**
```bash
aws dynamodb create-table \
    --table-name feature-flags-local \
    --attribute-definitions AttributeName=flagName,AttributeType=S \
    --key-schema AttributeName=flagName,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST \
    --endpoint-url http://localhost:8000
```

4. **Access the API**
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs
- Health Check: http://localhost:8080/health

### Run Locally without Docker

1. **Start Redis** (requires Redis installed locally)
```bash
redis-server
```

2. **Start DynamoDB Local**
```bash
docker run -p 8000:8000 amazon/dynamodb-local
```

3. **Run the application**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

## 📚 API Endpoints

### Feature Flag Management

#### Create Feature Flag
```bash
POST /flags
Content-Type: application/json

{
  "flagName": "new_checkout",
  "enabled": true,
  "rolloutPercentage": 10,
  "description": "New checkout flow"
}
```

#### Get Feature Flag
```bash
GET /flags/{flagName}
```

#### Update Feature Flag
```bash
PUT /flags/{flagName}
Content-Type: application/json

{
  "enabled": true,
  "rolloutPercentage": 50
}
```

#### Delete Feature Flag
```bash
DELETE /flags/{flagName}
```

#### Evaluate Flag for User
```bash
GET /flags/{flagName}/evaluate?userId=user123
```

### Example Requests

```bash
# Create a flag
curl -X POST http://localhost:8080/flags \
  -H "Content-Type: application/json" \
  -d '{
    "flagName": "new_ui",
    "enabled": true,
    "rolloutPercentage": 25,
    "description": "New UI design"
  }'

# Get a flag
curl http://localhost:8080/flags/new_ui

# Evaluate flag for user
curl http://localhost:8080/flags/new_ui/evaluate?userId=user123

# Update rollout percentage
curl -X PUT http://localhost:8080/flags/new_ui \
  -H "Content-Type: application/json" \
  -d '{"rolloutPercentage": 50}'
```

## 🧪 Testing

### Run all tests
```bash
./mvnw test
```

### Run specific test class
```bash
./mvnw test -Dtest=FeatureFlagServiceTest
```

### Generate test coverage report
```bash
./mvnw jacoco:report
```

## 🏗️ Build

### Build JAR
```bash
./mvnw clean package
```

### Build Docker image
```bash
docker build -t feature-flag-service .
```

## ☁️ AWS Deployment

### Prerequisites
- AWS CLI configured with appropriate credentials
- VPC with subnets configured
- ECR repository created

### Deploy Infrastructure

1. **Deploy CloudFormation stack**
```bash
aws cloudformation create-stack \
  --stack-name feature-flag-infrastructure \
  --template-body file://aws/cloudformation-template.json \
  --parameters \
    ParameterKey=Environment,ParameterValue=prod \
    ParameterKey=VpcId,ParameterValue=vpc-xxxxx \
    ParameterKey=SubnetIds,ParameterValue=subnet-xxxxx,subnet-yyyyy \
  --capabilities CAPABILITY_IAM
```

2. **Create DynamoDB table**
```bash
./aws/create-dynamodb-table.sh feature-flags-prod us-east-1
```

3. **Build and push Docker image to ECR**
```bash
./aws/deploy-to-ecr.sh v1.0.0
```

### Environment Variables for Production

```bash
SPRING_PROFILES_ACTIVE=prod
AWS_REGION=us-east-1
DYNAMODB_TABLE_NAME=feature-flags-prod
REDIS_HOST=your-redis-endpoint.cache.amazonaws.com
REDIS_PORT=6379
```

## 📊 Monitoring

### Actuator Endpoints

- Health: `http://localhost:8080/actuator/health`
- Metrics: `http://localhost:8080/actuator/metrics`
- Prometheus: `http://localhost:8080/actuator/prometheus`
- Info: `http://localhost:8080/actuator/info`

### CloudWatch Integration

The service automatically publishes logs to CloudWatch when deployed on AWS ECS.

### Prometheus & Grafana

The application exposes Prometheus metrics at `/actuator/prometheus` for monitoring:
- Request rates
- Error rates
- Response times
- Cache hit/miss ratios

## 🔧 Configuration

### Application Profiles

- `local` - Local development with DynamoDB Local
- `prod` - Production configuration for AWS

### Key Configuration Properties

See `application.yml`, `application-local.yml`, and `application-prod.yml` for detailed configuration.

## 🏆 Key Design Decisions

### Why DynamoDB?
- Low-latency reads (< 10ms)
- Auto-scaling capabilities
- No connection pool management
- Pay-per-request pricing

### Why Redis Cache?
- Reduces DynamoDB load
- Shares state across services
- Sub-millisecond read latency
- Graceful degradation on failure

### Deterministic Hashing
- Uses MurmurHash3 for consistent user assignment
- Same user always gets same experience
- Enables A/B testing without state storage

## 📈 Performance Characteristics

- **Flag Evaluation Latency**: < 10ms (cache hit)
- **Cache TTL**: 5 minutes (configurable)
- **Database Operations**: Read-optimized with DynamoDB
- **Horizontal Scalability**: Stateless service design

## 🔒 Security Best Practices

- IAM-based access control for AWS resources
- No sensitive data in feature flags
- Proper error handling without information leakage
- Input validation on all endpoints
- Security groups for network isolation

## 📝 Project Structure

```
feature-flag-service/
├── src/
│   ├── main/
│   │   ├── java/com/example/featureflag/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/             # Data transfer objects
│   │   │   ├── exception/       # Custom exceptions
│   │   │   ├── model/           # Domain models
│   │   │   ├── repository/      # Data access layer
│   │   │   └── service/         # Business logic
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-local.yml
│   │       └── application-prod.yml
│   └── test/                    # Unit and integration tests
├── aws/                         # AWS deployment scripts
├── docs/                        # Documentation
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👤 Author

**Sual Harun**
- GitHub: [@sualharun](https://github.com/sualharun)

## 🙏 Acknowledgments

- Inspired by feature flag systems at Amazon, Google, and Microsoft
- Built as a showcase for production-grade system design skills

---

**Ready for Big Tech Interviews** ✨

This project demonstrates:
- ✅ Production-grade system design
- ✅ Scalable cloud-native architecture
- ✅ Performance optimization (caching, low-latency)
- ✅ Reliability engineering (fault tolerance, graceful degradation)
- ✅ Testing best practices
- ✅ AWS deployment expertise
- ✅ Clean code and documentation
