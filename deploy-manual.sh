#!/bin/bash
set -e

echo "Manual deployment script for Creator Ledger"
echo "==========================================="

# Configuration
ORACLE_HOST="132.145.52.30"
SSH_KEY="$HOME/oracle-cloud-key.pem"
DEPLOY_DIR="/home/ubuntu/creator-ledger"

# Get latest image tag
LATEST_TAG=$(git rev-parse --short HEAD)
IMAGE_NAME="ghcr.io/robbiemcarthur/creator-ledger:master-sha-${LATEST_TAG}"

echo "Deploying image: ${IMAGE_NAME}"
echo ""

# SSH into Oracle Cloud and deploy
ssh -i "${SSH_KEY}" ubuntu@${ORACLE_HOST} << EOF
set -e

cd ${DEPLOY_DIR}

# Update .env if needed
echo "Using existing .env configuration"

# Update docker-compose to use specific tag
export IMAGE_TAG="master-sha-${LATEST_TAG}"

# Pull latest image
echo "Pulling latest image..."
docker compose -f docker-compose.external-db.yml pull app

# Start the application
echo "Starting application..."
docker compose -f docker-compose.external-db.yml up -d app

# Check logs
echo "Container started. Checking logs..."
sleep 5
docker logs creator-ledger-app --tail 50

echo ""
echo "Deployment complete! Check status with:"
echo "  docker ps"
echo "  docker logs -f creator-ledger-app"
EOF

echo ""
echo "Testing health endpoint..."
sleep 10
curl -f http://${ORACLE_HOST}:8080/actuator/health || echo "Health check failed - application may still be starting"

echo ""
echo "Deployment complete!"
