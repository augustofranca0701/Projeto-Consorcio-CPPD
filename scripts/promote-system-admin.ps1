# Promote user to SYSTEM_ADMIN
# Project: CONSORCIAR
# Author: Augusto Franca
# Description:
# Promotes an existing user to SYSTEM_ADMIN by email.
# Must be executed with Docker containers running.

Clear-Host
Write-Host "==============================================" -ForegroundColor Cyan
Write-Host " PROMOTE USER TO SYSTEM_ADMIN" -ForegroundColor Cyan
Write-Host "==============================================" -ForegroundColor Cyan
Write-Host ""

# =============================
# CONFIG
# =============================
$DB_CONTAINER = "consorcio_db"
$DB_USER      = "postgres"
$DB_NAME      = "consorciar_dev"

# =============================
# INPUT
# =============================
$email = Read-Host "Enter user email to promote"

if ([string]::IsNullOrWhiteSpace($email)) {
    Write-Host "Invalid email. Operation aborted." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Promoting user to SYSTEM_ADMIN" -ForegroundColor Yellow
Write-Host "Email: $email"
Write-Host ""

# =============================
# SQL
# =============================
$sql = @"
UPDATE users
SET system_role = 'SYSTEM_ADMIN'
WHERE email = '$email'
RETURNING email, system_role;
"@

# =============================
# EXECUTION
# =============================
$result = docker exec -i $DB_CONTAINER `
    psql -U $DB_USER -d $DB_NAME -t -A `
    -c "$sql"

# =============================
# RESULT
# =============================
if ([string]::IsNullOrWhiteSpace($result)) {
    Write-Host ""
    Write-Host "ERROR: No user was promoted." -ForegroundColor Red
    Write-Host "Check the following:" -ForegroundColor Yellow
    Write-Host "- Email is correct" -ForegroundColor Yellow
    Write-Host "- User exists in database '$DB_NAME'" -ForegroundColor Yellow
    Write-Host "- Container '$DB_CONTAINER' is running" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "SUCCESS: User promoted:" -ForegroundColor Green
Write-Host $result -ForegroundColor Green
Write-Host ""
