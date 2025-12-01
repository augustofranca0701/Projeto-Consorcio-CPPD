Write-Host "Iniciando ambiente de produção..." -ForegroundColor Cyan
& "$PSScriptRoot/check-env.ps1"

docker compose --profile prod up -d --build
docker compose ps
Write-Host "Ambiente PROD rodando." -ForegroundColor Green