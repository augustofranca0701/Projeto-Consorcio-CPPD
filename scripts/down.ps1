param(
    [switch]$RemoveVolumes
)

Write-Host "Derrubando containers..." -ForegroundColor Cyan
docker compose down

if ($RemoveVolumes) {
    Write-Host "Removendo volumes nomeados..." -ForegroundColor Yellow
    docker volume rm $(docker volume ls -q) 2>$null
}

docker compose ps
