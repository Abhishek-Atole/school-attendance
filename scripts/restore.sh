#!/bin/bash

# Database Restore Script for School Attendance Management System
# Usage: ./restore.sh <backup_name> [--force]

set -e

BACKUP_NAME=$1
FORCE_RESTORE=$2
BACKUP_DIR="./backups"
CONTAINER_NAME="school-attendance-db"

if [ -z "$BACKUP_NAME" ]; then
    echo "❌ Please specify backup name to restore."
    echo ""
    echo "Available backups:"
    ls -la $BACKUP_DIR/*.sql.gz 2>/dev/null | awk '{print $9}' | xargs -r basename -s .sql.gz || echo "No backups found."
    echo ""
    echo "Usage: $0 <backup_name> [--force]"
    exit 1
fi

BACKUP_FILE="$BACKUP_DIR/$BACKUP_NAME.sql.gz"

if [ ! -f "$BACKUP_FILE" ]; then
    echo "❌ Backup file not found: $BACKUP_FILE"
    exit 1
fi

echo "🔄 Restoring database from backup: $BACKUP_NAME"

# Check if database container is running
if ! docker ps | grep -q $CONTAINER_NAME; then
    echo "❌ Database container '$CONTAINER_NAME' is not running."
    echo "Please start the application first with: docker-compose up -d"
    exit 1
fi

# Get database credentials from environment
if [ -f ".env" ]; then
    source .env
else
    echo "❌ .env file not found. Please ensure the environment file exists."
    exit 1
fi

# Warning about data loss
if [ "$FORCE_RESTORE" != "--force" ]; then
    echo "⚠️  WARNING: This will COMPLETELY REPLACE the current database with the backup data."
    echo "⚠️  ALL CURRENT DATA WILL BE LOST!"
    echo ""
    read -p "Are you sure you want to continue? Type 'yes' to proceed: " confirmation
    
    if [ "$confirmation" != "yes" ]; then
        echo "❌ Restore cancelled."
        exit 1
    fi
fi

# Stop the backend service to prevent connections during restore
echo "🛑 Stopping backend service..."
docker-compose stop backend

# Create a current backup before restore
echo "💾 Creating safety backup before restore..."
SAFETY_BACKUP="safety_backup_before_restore_$(date +%Y%m%d_%H%M%S)"
docker exec $CONTAINER_NAME mysqldump \
    --single-transaction \
    --routines \
    --triggers \
    -u ${DB_USER:-attendance_user} \
    -p${DB_PASSWORD:-attendance_pass} \
    ${DB_NAME:-school_attendance} > "$BACKUP_DIR/$SAFETY_BACKUP.sql"
gzip "$BACKUP_DIR/$SAFETY_BACKUP.sql"
echo "💾 Safety backup created: $SAFETY_BACKUP.sql.gz"

# Drop and recreate database
echo "🗑️  Dropping existing database..."
docker exec $CONTAINER_NAME mysql \
    -u ${DB_USER:-attendance_user} \
    -p${DB_PASSWORD:-attendance_pass} \
    -e "DROP DATABASE IF EXISTS ${DB_NAME:-school_attendance}; CREATE DATABASE ${DB_NAME:-school_attendance};"

# Restore from backup
echo "📥 Restoring from backup..."
gunzip -c "$BACKUP_FILE" | docker exec -i $CONTAINER_NAME mysql \
    -u ${DB_USER:-attendance_user} \
    -p${DB_PASSWORD:-attendance_pass} \
    ${DB_NAME:-school_attendance}

# Start the backend service
echo "🚀 Starting backend service..."
docker-compose start backend

# Wait for backend to start
echo "⏳ Waiting for backend to start..."
sleep 15

# Check if restore was successful
if curl -f http://localhost:8080/actuator/health &> /dev/null; then
    echo "✅ Database restore completed successfully!"
    echo "✅ Backend service is running and healthy."
else
    echo "⚠️  Database restored but backend health check failed."
    echo "   Please check the backend logs: docker-compose logs backend"
fi

echo "🎉 Restore process completed!"
echo ""
echo "📝 Important notes:"
echo "   - Safety backup created: $SAFETY_BACKUP.sql.gz"
echo "   - Original backup restored: $BACKUP_NAME.sql.gz"
echo "   - Check application functionality after restore"
