#!/bin/bash
# =====================================================
# BusApp - Import Database Script
# =====================================================
#
# Chạy TRƯỚC KHI start Spring Boot app
#
# Cách dùng:
#   chmod +x import-db.sh
#   ./import-db.sh
#
# =====================================================

set -e

DB_NAME="busapp_db"
DB_USER="root"
DB_PASS="trine1234@"

echo "==============================================="
echo "  BusApp Database Import Script"
echo "==============================================="

# 1. Kiểm tra / cài MySQL client
echo ""
echo "[1/5] Kiểm tra MySQL client..."
if ! command -v mysql &> /dev/null; then
    echo "  MySQL client chưa có, đang cài..."
    if command -v brew &> /dev/null; then
        brew install mysql-client > /dev/null 2>&1
        export PATH="$(brew --prefix)/opt/mysql-client/bin:$PATH"
        echo "  ✓ Đã cài mysql-client"
    else
        echo "Lỗi: Không tìm thấy MySQL client và Homebrew."
        echo "Hãy cài MySQL client thủ công."
        exit 1
    fi
else
    echo "  ✓ MySQL client đã có"
fi

# Export PATH cho mysql-client
if [ -d "$(brew --prefix)/opt/mysql-client/bin" ]; then
    export PATH="$(brew --prefix)/opt/mysql-client/bin:$PATH"
fi

# 2. Kiểm tra MySQL server đang chạy
echo ""
echo "[2/5] Kiểm tra MySQL server..."
if mysql -u"$DB_USER" -p"$DB_PASS" -e "SELECT 1;" > /dev/null 2>&1; then
    echo "  ✓ MySQL server đang chạy"
else
    echo "  MySQL server có thể chưa chạy."
    echo ""
    echo "  Hãy khởi động MySQL:"
    echo "  - macOS (brew services): brew services start mysql"
    echo "  - macOS (launchd):       mysql.server start"
    echo "  - Docker:                docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=$DB_PASS mysql:8.0"
    echo "  - MySQL Workbench:       Mở app và start server"
    exit 1
fi

# 3. Tạo database
echo ""
echo "[3/5] Tạo database '$DB_NAME'..."
mysql -u"$DB_USER" -p"$DB_PASS" -e "CREATE DATABASE IF NOT EXISTS $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
echo "  ✓ Database sẵn sàng"

# 4. Import schema
echo ""
echo "[4/5] Import schema.sql (tables)..."
mysql -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" < src/main/resources/schema.sql
echo "  ✓ Schema created"

# 5. Import seed data
echo ""
echo "[5/5] Import data.sql (63 tỉnh thành)..."
mysql -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" < src/main/resources/data.sql
echo "  ✓ Seed data imported"

# Kiểm tra kết quả
echo ""
echo "==============================================="
echo "  Kiểm tra dữ liệu:"
echo "==============================================="
echo ""
PROVINCES=$(mysql -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" -N -e "SELECT COUNT(*) FROM provinces;")
VEHICLE_TYPES=$(mysql -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" -N -e "SELECT COUNT(*) FROM vehicle_types;")
OPERATORS=$(mysql -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" -N -e "SELECT COUNT(*) FROM bus_operators;")
ROLES=$(mysql -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" -N -e "SELECT COUNT(*) FROM roles;")

echo "  ✓ Tỉnh/thành: $PROVINCES"
echo "  ✓ Loại xe: $VEHICLE_TYPES"
echo "  ✓ Nhà xe: $OPERATORS"
echo "  ✓ Roles: $ROLES"

if [ "$PROVINCES" -ge 60 ] && [ "$VEHICLE_TYPES" -ge 5 ] && [ "$ROLES" -ge 3 ]; then
    echo ""
    echo "==============================================="
    echo "  Import THÀNH CÔNG!"
    echo "==============================================="
    echo ""
    echo "Bây giờ chạy app: ./mvnw spring-boot:run"
else
    echo ""
    echo "Có vấn đề với dữ liệu, kiểm tra lại."
fi
