# scripts/run-dev.ps1
param()

# Run environment dev using docker compose profile 'dev'
$compose = "docker compose"
try { & $compose version > $null 2>&1 } catch { $compose = "docker-compose" }

Write-Host "Iniciando ambiente de desenvolvimento (profile: dev)..." -ForegroundColor Cyan

# opcional: remover volumes antes? Não solicitaremos aqui (menu faz isso)
# Sobe com build
& $compose --profile dev up --build -d 2>&1 | ForEach-Object { Write-Host $_ }

# lista serviços configurados no compose (para debug)
$services = & $compose config --services 2>$null
if ($services) {
    Write-Host "`nServicos declarados no compose:" -ForegroundColor Cyan
    $services | ForEach-Object { Write-Host " - $_" }
} else {
    Write-Host "Nao foi possivel obter a lista de services do compose (ou nenhum service declarado)." -ForegroundColor Yellow
}

# verifica se frontend_dev foi iniciado (nome do service no compose: frontend_dev)
$svcName = "consorcio_frontend_dev"
$found = & docker ps --filter "name=$svcName" --format "{{.Names}}" 2>$null
if ($found -and $found.Trim() -ne "") {
    Write-Host "`nFrontend dev iniciado: $found" -ForegroundColor Green
} else {
    Write-Host "`nFrontend_dev nao aparece como iniciado. Verifique logs do compose ou execute 'docker compose ps --all'." -ForegroundColor Yellow
    # mostra resumo do compose ps para ajudar debug
    & $compose ps --all 2>&1 | ForEach-Object { Write-Host $_ }
    Write-Host "`nSe o service 'frontend_dev' nao existir no seu docker-compose.yml, ajuste o arquivo ou use a opcao apropriada no menu." -ForegroundColor Yellow
}

Write-Host "`nAmbiente DEV - processo finalizado." -ForegroundColor Green
