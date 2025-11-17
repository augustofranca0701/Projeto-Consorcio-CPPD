Write-Host "Iniciando ambiente de produção..."
docker compose --profile prod up -d
docker compose ps
