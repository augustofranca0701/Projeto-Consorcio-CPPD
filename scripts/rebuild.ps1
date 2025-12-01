Write-Host "Rebuild completo iniciado..." -ForegroundColor Cyan

docker compose down
docker compose build --no-cache
docker compose up -d

docker compose ps
Write-Host "Rebuild finalizado." -ForegroundColor Green
