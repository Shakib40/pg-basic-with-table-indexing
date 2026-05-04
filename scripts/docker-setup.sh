#!/bin/bash

# Docker Setup Script for PG CRUD with Indexing
# This script helps set up the Docker environment for development and production

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "${BLUE}================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}================================${NC}"
}

# Check if Docker is installed
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi
    
    print_status "Docker and Docker Compose are installed"
}

# Create necessary directories
create_directories() {
    print_status "Creating necessary directories..."
    
    mkdir -p nginx/ssl
    mkdir -p nginx/logs
    mkdir -p backups
    mkdir -p monitoring
    mkdir -p logs
    
    print_status "Directories created successfully"
}

# Generate SSL certificates for development
generate_ssl() {
    if [ ! -f "nginx/ssl/cert.pem" ] || [ ! -f "nginx/ssl/key.pem" ]; then
        print_status "Generating SSL certificates for development..."
        
        openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
            -keyout nginx/ssl/key.pem \
            -out nginx/ssl/cert.pem \
            -subj "/C=US/ST=State/L=City/O=Organization/CN=localhost"
        
        print_status "SSL certificates generated successfully"
    else
        print_warning "SSL certificates already exist"
    fi
}

# Setup environment file
setup_environment() {
    if [ ! -f ".env" ]; then
        print_status "Creating .env file from template..."
        cp .env.example .env
        print_warning "Please edit .env file with your configuration"
    else
        print_warning ".env file already exists"
    fi
}

# Development setup
setup_development() {
    print_header "Development Setup"
    
    check_docker
    create_directories
    generate_ssl
    setup_environment
    
    print_status "Starting development environment..."
    docker-compose up -d
    
    print_status "Waiting for services to be ready..."
    sleep 30
    
    # Check if services are healthy
    if docker-compose ps | grep -q "healthy"; then
        print_status "Development environment is ready!"
        echo ""
        echo "Services:"
        echo "  - Application: http://localhost:8080"
        echo "  - Swagger UI: http://localhost:8080/api/swagger-ui.html"
        echo "  - PostgreSQL: localhost:5432"
        echo "  - Redis: localhost:6379"
        echo "  - Nginx: http://localhost"
    else
        print_error "Some services are not healthy. Check logs with 'docker-compose logs'"
    fi
}

# Production setup
setup_production() {
    print_header "Production Setup"
    
    check_docker
    create_directories
    
    if [ ! -f ".env" ]; then
        print_error "Please create .env file first with production values"
        exit 1
    fi
    
    print_status "Starting production environment..."
    docker-compose -f docker-compose.prod.yml up -d
    
    print_status "Waiting for services to be ready..."
    sleep 45
    
    # Check if services are healthy
    if docker-compose -f docker-compose.prod.yml ps | grep -q "healthy"; then
        print_status "Production environment is ready!"
        echo ""
        echo "Services:"
        echo "  - Application: https://localhost"
        echo "  - PostgreSQL: localhost:5432"
        echo "  - Redis: localhost:6379"
    else
        print_error "Some services are not healthy. Check logs with 'docker-compose -f docker-compose.prod.yml logs'"
    fi
}

# Setup with monitoring
setup_monitoring() {
    print_header "Production Setup with Monitoring"
    
    check_docker
    create_directories
    
    if [ ! -f ".env" ]; then
        print_error "Please create .env file first with production values"
        exit 1
    fi
    
    print_status "Starting production environment with monitoring..."
    docker-compose -f docker-compose.prod.yml --profile monitoring up -d
    
    print_status "Waiting for services to be ready..."
    sleep 60
    
    print_status "Production environment with monitoring is ready!"
    echo ""
    echo "Services:"
    echo "  - Application: https://localhost"
    echo "  - Prometheus: http://localhost:9090"
    echo "  - Grafana: http://localhost:3000"
    echo "  - PostgreSQL: localhost:5432"
    echo "  - Redis: localhost:6379"
}

# Cleanup function
cleanup() {
    print_header "Cleanup"
    
    print_warning "This will remove all containers, networks, and volumes"
    read -p "Are you sure? (y/N): " -n 1 -r
    echo
    
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        print_status "Stopping and removing containers..."
        docker-compose down -v --remove-orphans
        docker-compose -f docker-compose.prod.yml down -v --remove-orphans
        
        print_status "Removing unused images..."
        docker image prune -f
        
        print_status "Cleanup completed"
    else
        print_status "Cleanup cancelled"
    fi
}

# Show logs
show_logs() {
    print_header "Logs"
    
    if [ "$1" = "prod" ]; then
        docker-compose -f docker-compose.prod.yml logs -f
    else
        docker-compose logs -f
    fi
}

# Main menu
main_menu() {
    echo ""
    print_header "PG CRUD with Indexing - Docker Setup"
    echo "1. Development Setup"
    echo "2. Production Setup"
    echo "3. Production Setup with Monitoring"
    echo "4. Show Logs (Development)"
    echo "5. Show Logs (Production)"
    echo "6. Cleanup"
    echo "7. Exit"
    echo ""
    
    read -p "Choose an option (1-7): " choice
    
    case $choice in
        1)
            setup_development
            ;;
        2)
            setup_production
            ;;
        3)
            setup_monitoring
            ;;
        4)
            show_logs
            ;;
        5)
            show_logs prod
            ;;
        6)
            cleanup
            ;;
        7)
            print_status "Goodbye!"
            exit 0
            ;;
        *)
            print_error "Invalid option"
            main_menu
            ;;
    esac
}

# Check for command line arguments
if [ $# -eq 0 ]; then
    main_menu
else
    case $1 in
        "dev")
            setup_development
            ;;
        "prod")
            setup_production
            ;;
        "monitoring")
            setup_monitoring
            ;;
        "logs")
            show_logs $2
            ;;
        "cleanup")
            cleanup
            ;;
        *)
            echo "Usage: $0 [dev|prod|monitoring|logs|cleanup]"
            echo "  dev       - Setup development environment"
            echo "  prod      - Setup production environment"
            echo "  monitoring - Setup production with monitoring"
            echo "  logs      - Show logs (optional: prod)"
            echo "  cleanup   - Clean up all containers and volumes"
            exit 1
            ;;
    esac
fi
