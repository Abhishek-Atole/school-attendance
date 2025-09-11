#!/bin/bash

# Deploy School Attendance Management System
# Usage: ./deploy.sh [environment]
# Environments: dev, prod (default: dev)

set -e

ENVIRONMENT=${1:-dev}
PROJECT_NAME="school-attendance"

echo "🚀 Deploying School Attendance Management System - Environment: $ENVIRONMENT"

# Check if Docker and Docker Compose are installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Check if .env file exists
if [ ! -f ".env" ]; then
    echo "⚠️  .env file not found. Creating from template..."
    if [ -f ".env.example" ]; then
        cp .env.example .env
        echo "📝 Please edit .env file with your actual configuration values before running the deployment again."
        exit 1
    else
        echo "❌ Neither .env nor .env.example found. Please create .env file with required configuration."
        exit 1
    fi
fi

# Function to deploy development environment
deploy_dev() {
    echo "🔧 Deploying development environment..."
    
    # Stop existing containers
    docker-compose -f docker-compose.yml -f docker-compose.dev.yml down
    
    # Remove old images (optional)
    read -p "Do you want to rebuild images? (y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "🔨 Rebuilding images..."
        docker-compose -f docker-compose.yml -f docker-compose.dev.yml build --no-cache
    fi
    
    # Start services
    echo "🏃 Starting services..."
    docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
    
    echo "🌐 Development environment is starting..."
    echo "📧 MailHog (Email testing): http://localhost:8025"
    echo "🖥️  Frontend: http://localhost:80"
    echo "🔗 Backend API: http://localhost:8080"
    echo "🗄️  Database: localhost:3306"
}

# Function to deploy production environment
deploy_prod() {
    echo "🏭 Deploying production environment..."
    
    # Validate production requirements
    if grep -q "your-" .env; then
        echo "❌ Please update .env file with actual production values (found placeholder values)."
        exit 1
    fi
    
    # Stop existing containers
    docker-compose down
    
    # Pull latest images if using registry
    echo "📥 Pulling latest images..."
    docker-compose pull
    
    # Start services
    echo "🏃 Starting production services..."
    docker-compose up -d
    
    echo "🏭 Production environment is starting..."
    echo "🖥️  Frontend: http://localhost:80"
    echo "🔗 Backend API: http://localhost:8080"
}

# Function to check service health
check_health() {
    echo "🔍 Checking service health..."
    
    # Wait for services to start
    sleep 10
    
    # Check backend health
    if curl -f http://localhost:8080/actuator/health &> /dev/null; then
        echo "✅ Backend is healthy"
    else
        echo "⚠️  Backend health check failed"
    fi
    
    # Check frontend health
    if curl -f http://localhost:80/health &> /dev/null; then
        echo "✅ Frontend is healthy"
    else
        echo "⚠️  Frontend health check failed"
    fi
    
    # Show running containers
    echo "📋 Running containers:"
    docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
}

# Main deployment logic
case $ENVIRONMENT in
    "dev")
        deploy_dev
        ;;
    "prod")
        deploy_prod
        ;;
    *)
        echo "❌ Invalid environment: $ENVIRONMENT"
        echo "Usage: $0 [dev|prod]"
        exit 1
        ;;
esac

# Check health after deployment
check_health

echo "🎉 Deployment completed!"
echo ""
echo "📝 Next steps:"
echo "1. Check application logs: docker-compose logs -f"
echo "2. Monitor services: docker-compose ps"
echo "3. Stop services: docker-compose down"
echo ""
echo "📚 Documentation:"
echo "- API Documentation: http://localhost:8080/swagger-ui.html"
echo "- Health Endpoints: http://localhost:8080/actuator/health"
