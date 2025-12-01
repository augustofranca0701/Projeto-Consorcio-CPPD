Write-Host "Iniciando ambiente de desenvolvimento..." -ForegroundColor Cyan
& "$PSScriptRoot/check-env.ps1"

docker compose --profile dev up -d --build
docker compose ps
Write-Host "Ambiente DEV rodando." -ForegroundColor Green
