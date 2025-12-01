param([string]$Service = "")

if ($Service -ne "") {
    Write-Host "Logs do serviço: $Service" -ForegroundColor Cyan
    docker compose logs -f --tail 200 $Service
}
else {
    Write-Host "Logs de todos os serviços..." -ForegroundColor Cyan
    docker compose logs -f --tail 200
}
