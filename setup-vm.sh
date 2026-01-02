#!/bin/bash
set -e

echo "Setting up Oracle Cloud VM for Creator Ledger"
echo "=============================================="

ORACLE_HOST="132.145.52.30"
SSH_KEY="$HOME/oracle-cloud-key.pem"
DEPLOY_DIR="/home/ubuntu/creator-ledger"

echo "Creating directory structure on VM..."

ssh -i "${SSH_KEY}" ubuntu@${ORACLE_HOST} << 'EOF'
# Create deployment directory
mkdir -p /home/ubuntu/creator-ledger
cd /home/ubuntu/creator-ledger

echo "Directory created: $(pwd)"
EOF

echo ""
echo "Copying docker-compose file to VM..."
scp -i "${SSH_KEY}" docker-compose.external-db.yml ubuntu@${ORACLE_HOST}:${DEPLOY_DIR}/

echo ""
echo "Setting up .env file on VM..."
echo "IMPORTANT: You need to provide your Neon database credentials"
echo ""

# Prompt for database credentials
read -p "Enter SPRING_DATASOURCE_URL (from Neon): " DB_URL
read -p "Enter SPRING_DATASOURCE_USERNAME: " DB_USER
read -sp "Enter SPRING_DATASOURCE_PASSWORD: " DB_PASS
echo ""

# Create .env file
cat > /tmp/creator-ledger.env << ENVFILE
# Spring Profile
SPRING_PROFILES_ACTIVE=prod

# Database Configuration (Neon PostgreSQL)
SPRING_DATASOURCE_URL=${DB_URL}
SPRING_DATASOURCE_USERNAME=${DB_USER}
SPRING_DATASOURCE_PASSWORD=${DB_PASS}

# Application Configuration
APP_PORT=8080

# Docker Image
GITHUB_REPOSITORY=robbiemcarthur/creator-ledger
IMAGE_TAG=latest
ENVFILE

# Copy .env to VM
echo ""
echo "Copying .env to VM..."
scp -i "${SSH_KEY}" /tmp/creator-ledger.env ubuntu@${ORACLE_HOST}:${DEPLOY_DIR}/.env
rm /tmp/creator-ledger.env

echo ""
echo "Verifying setup on VM..."
ssh -i "${SSH_KEY}" ubuntu@${ORACLE_HOST} << 'EOF'
cd /home/ubuntu/creator-ledger
echo "Files in deployment directory:"
ls -la
echo ""
echo "docker-compose.external-db.yml exists: $([ -f docker-compose.external-db.yml ] && echo "YES" || echo "NO")"
echo ".env exists: $([ -f .env ] && echo "YES" || echo "NO")"
EOF

echo ""
echo "VM setup complete!"
echo ""
echo "Next steps:"
echo "1. Wait for CI to complete and build the Docker image"
echo "2. Run: ./deploy-manual.sh"
