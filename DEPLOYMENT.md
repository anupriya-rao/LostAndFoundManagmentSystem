# Deployment Guide - Lost and Found Management System

This guide provides step-by-step instructions for deploying the Lost and Found Management System using Docker, with frontend hosting options.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Local Development Deployment](#local-development-deployment)
3. [Production Deployment](#production-deployment)
4. [Frontend Deployment Options](#frontend-deployment-options)
5. [Database Management](#database-management)
6. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### Required Software

- **Docker**: [Install Docker](https://docs.docker.com/get-docker/)
- **Docker Compose**: [Install Docker Compose](https://docs.docker.com/compose/install/)
- **Git**: For cloning the repository
- **Java 11+**: For local development (optional)
- **Maven**: For building the project locally (optional)

### System Requirements

- **Disk Space**: At least 5GB free
- **RAM**: Minimum 2GB (4GB recommended)
- **Port Availability**: Ports 80, 3306, 8080 should be available

---

## Local Development Deployment

### Step 1: Clone the Repository

```bash
git clone https://github.com/anupriya-rao/LostAndFoundManagmentSystem.git
cd LostAndFoundManagmentSystem
```

### Step 2: Build and Start Services

```bash
# Build and start all services (backend, MySQL, frontend)
docker-compose up --build

# Or run in background
docker-compose up -d --build
```

### Step 3: Verify Services

```bash
# Check running containers
docker-compose ps

# View logs
docker-compose logs -f backend
docker-compose logs -f mysql
```

### Step 4: Access the Application

- **Frontend**: http://localhost (or http://localhost:80)
- **Backend API**: http://localhost:8080/api
- **Health Check**: http://localhost:8080/api/health

### Step 5: Stop Services

```bash
# Stop all services
docker-compose down

# Stop and remove data
docker-compose down -v
```

---

## Production Deployment

### Option 1: Deploy on AWS EC2

#### 1.1 Launch EC2 Instance

```bash
# Recommended: Ubuntu 20.04 LTS, t2.medium or larger
# Security Group: Allow ports 22, 80, 443, 3306
```

#### 1.2 Install Docker and Docker Compose

```bash
sudo apt update
sudo apt install -y docker.io docker-compose git
sudo usermod -aG docker $USER
```

#### 1.3 Clone and Deploy

```bash
git clone https://github.com/anupriya-rao/LostAndFoundManagmentSystem.git
cd LostAndFoundManagmentSystem
docker-compose up -d --build
```

### Option 2: Deploy on Azure Container Instances

#### 2.1 Create Azure Container Registry

```bash
az acr create --resource-group myResourceGroup \
  --name myRegistry --sku Basic
```

#### 2.2 Build and Push Image

```bash
docker build -t myregistry.azurecr.io/lost-and-found:latest .
docker push myregistry.azurecr.io/lost-and-found:latest
```

#### 2.3 Deploy Container Group

```bash
az container create --resource-group myResourceGroup \
  --name lost-and-found \
  --image myregistry.azurecr.io/lost-and-found:latest \
  --cpu 1 --memory 1.5 \
  --registry-login-server myregistry.azurecr.io \
  --registry-username <username> \
  --registry-password <password> \
  --ports 80 8080
```

### Option 3: Deploy on Google Cloud Run

#### 3.1 Push Image to Google Container Registry

```bash
docker build -t gcr.io/PROJECT_ID/lost-and-found .
docker push gcr.io/PROJECT_ID/lost-and-found
```

#### 3.2 Deploy using gcloud CLI

```bash
gcloud run deploy lost-and-found \
  --image gcr.io/PROJECT_ID/lost-and-found \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated
```

---

## Frontend Deployment Options

### Option 1: GitHub Pages

#### 1.1 Create gh-pages Branch

```bash
git checkout -b gh-pages
git checkout main -- frontend/
git mv frontend/* .
git add .
git commit -m "Deploy frontend to GitHub Pages"
git push origin gh-pages
```

#### 1.2 Enable GitHub Pages

1. Go to repository Settings → Pages
2. Select "Deploy from a branch"
3. Select `gh-pages` branch
4. Save

#### 1.3 Update API URL in frontend

Edit `frontend/js/script.js`:

```javascript
const API_BASE_URL = 'https://your-backend-domain.com/api';
```

### Option 2: Netlify

#### 2.1 Connect Repository

1. Go to [Netlify](https://netlify.com)
2. Click "New site from Git"
3. Select your GitHub repository
4. Choose `main` branch

#### 2.2 Configure Build Settings

- **Build command**: `echo "Frontend is ready"`
- **Publish directory**: `frontend`

#### 2.3 Set Environment Variables

In Netlify settings, add:

```
API_URL=https://your-backend-domain.com/api
```

### Option 3: Vercel

#### 3.1 Deploy with Vercel CLI

```bash
npm install -g vercel
vercel
```

#### 3.2 Configure vercel.json

Create `vercel.json`:

```json
{
  "buildCommand": "echo 'Frontend deployed'",
  "outputDirectory": "frontend",
  "env": {
    "API_BASE_URL": "https://your-backend-domain.com/api"
  }
}
```

### Option 4: AWS S3 + CloudFront

#### 4.1 Create S3 Bucket

```bash
aws s3 mb s3://lost-and-found-frontend
aws s3 sync frontend/ s3://lost-and-found-frontend/
```

#### 4.2 Create CloudFront Distribution

1. Create distribution pointing to S3 bucket
2. Set default root object to `index.html`
3. Configure error handling (404 → index.html)

---

## Database Management

### Backup MySQL Database

```bash
# Using Docker
docker-compose exec mysql mysqldump -u root -proot lost_and_found > backup.sql

# Restore from backup
docker-compose exec -T mysql mysql -u root -proot lost_and_found < backup.sql
```

### Connect to MySQL

```bash
# Using Docker CLI
docker-compose exec mysql mysql -u root -proot lost_and_found

# Using MySQL client locally
mysql -h 127.0.0.1 -u root -proot lost_and_found
```

### Initialize Database Schema

```bash
# Automatically done by Hibernate with ddl-auto: update
# For production, use: ddl-auto: validate
```

---

## Environment Variables

### Production Configuration

Update environment variables for production:

```bash
# .env (create this file in the root directory)
MYSQL_ROOT_PASSWORD=secure_password_here
MYSQL_DATABASE=lost_and_found
MYSQL_USER=lfuser
MYSQL_PASSWORD=secure_password_here
SPRING_DATASOURCE_PASSWORD=secure_password_here
JWT_SECRET=your-very-secure-secret-key
SPRING_PROFILES_ACTIVE=prod
```

Reference in docker-compose.yml:

```yaml
services:
  mysql:
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
  backend:
    environment:
      SPRING_DATASOURCE_PASSWORD: ${SPRING_DATASOURCE_PASSWORD}
```

---

## SSL/HTTPS Setup

### Using Let's Encrypt with Nginx

```bash
# Install Certbot
sudo apt install certbot python3-certbot-nginx

# Generate certificate
sudo certbot certonly --standalone -d yourdomain.com

# Update nginx.conf with SSL configuration
```

### Update nginx.conf for HTTPS

```nginx
server {
    listen 443 ssl http2;
    server_name yourdomain.com;
    
    ssl_certificate /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;
    
    # ... rest of configuration
}

# Redirect HTTP to HTTPS
server {
    listen 80;
    server_name yourdomain.com;
    return 301 https://$server_name$request_uri;
}
```

---

## Monitoring and Logs

### View Container Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
docker-compose logs -f mysql

# Last N lines
docker-compose logs --tail=100 backend
```

### Health Checks

```bash
# Backend health
curl http://localhost:8080/api/health

# Check service status
docker-compose ps
```

### Performance Monitoring

```bash
# View resource usage
docker stats

# Check specific container
docker stats lost-and-found-backend
```

---

## Troubleshooting

### Issue: Containers won't start

**Solution:**
```bash
# Check logs
docker-compose logs

# Rebuild images
docker-compose down
docker-compose up --build --no-cache

# Check port conflicts
lsof -i :8080
lsof -i :3306
```

### Issue: MySQL connection refused

**Solution:**
```bash
# Wait for MySQL to be ready
docker-compose exec backend curl http://mysql:3306

# Restart MySQL
docker-compose restart mysql

# Check MySQL logs
docker-compose logs mysql
```

### Issue: Frontend API calls failing

**Solution:**
1. Check API_BASE_URL in `frontend/js/script.js`
2. Verify CORS settings in backend
3. Check network connectivity: `curl http://backend:8080/api/health`

### Issue: Out of disk space

**Solution:**
```bash
# Remove unused images
docker image prune -a

# Clean up volumes
docker volume prune

# Check disk usage
docker system df
```

---

## Scaling and Performance

### Increase Resources

```bash
# Update docker-compose.yml resources
services:
  backend:
    deploy:
      resources:
        limits:
          cpus: '1'
          memory: 1G
        reservations:
          cpus: '0.5'
          memory: 512M
```

### Enable Database Connection Pooling

Update `application.yml`:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
```

---

## Security Best Practices

1. **Change Default Passwords**: Update `MYSQL_ROOT_PASSWORD` and JWT secret
2. **Enable HTTPS**: Use Let's Encrypt certificates
3. **Restrict Database Access**: Only allow backend to access MySQL
4. **Update Dependencies**: Regularly update packages
5. **Set Resource Limits**: Configure CPU and memory limits
6. **Regular Backups**: Implement automated backup strategy
7. **Monitor Logs**: Track and analyze application logs

---

## Support and Documentation

- [Docker Documentation](https://docs.docker.com/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [Nginx Documentation](https://nginx.org/en/docs/)

---

## Next Steps

1. Test application locally using Docker Compose
2. Set up production environment with SSL/HTTPS
3. Configure automated backups
4. Set up monitoring and alerting
5. Implement CI/CD pipeline with GitHub Actions

---

**Last Updated**: April 2026
**Version**: 1.0.0
