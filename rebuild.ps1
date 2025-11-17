Write-Host "Forçando rebuild de todas as imagens..."
docker compose down
docker compose build --no-cache
docker compose up -d
docker compose ps
