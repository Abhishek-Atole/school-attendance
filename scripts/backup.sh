#!/bin/bash

# Database Backup Script for School Attendance Management System
# Usage: ./backup.sh [backup_name]

set -e

BACKUP_NAME=${1:-"backup_$(date +%Y%m%d_%H%M%S)"}
BACKUP_DIR="./backups"
CONTAINER_NAME="school-attendance-db"

echo "💾 Creating database backup: $BACKUP_NAME"

# Create backup directory if it doesn't exist
mkdir -p $BACKUP_DIR

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

# Create backup
echo "📦 Creating backup..."
docker exec $CONTAINER_NAME mysqldump \
    --single-transaction \
    --routines \
    --triggers \
    -u ${DB_USER:-attendance_user} \
    -p${DB_PASSWORD:-attendance_pass} \
    ${DB_NAME:-school_attendance} > "$BACKUP_DIR/$BACKUP_NAME.sql"

# Compress backup
echo "🗜️  Compressing backup..."
gzip "$BACKUP_DIR/$BACKUP_NAME.sql"

# Calculate backup size
BACKUP_SIZE=$(du -h "$BACKUP_DIR/$BACKUP_NAME.sql.gz" | cut -f1)

echo "✅ Backup completed successfully!"
echo "📄 Backup file: $BACKUP_DIR/$BACKUP_NAME.sql.gz"
echo "📏 Backup size: $BACKUP_SIZE"

# Clean up old backups (keep last 10)
echo "🧹 Cleaning up old backups (keeping last 10)..."
cd $BACKUP_DIR
ls -t *.sql.gz 2>/dev/null | tail -n +11 | xargs -r rm
cd - > /dev/null

echo "🎉 Backup process completed!"
echo ""
echo "📝 To restore this backup, use:"
echo "   ./scripts/restore.sh $BACKUP_NAME"
