# scripts\logs.ps1
param([string]$Service = "")

$composeCmd = "docker compose"
try { & $composeCmd version | Out-Null } catch { $composeCmd = "docker-compose" }

if ($Service -and $Service.Trim() -ne "") {
    Write-Host "Logs do servico: $Service" -ForegroundColor Cyan
    & $composeCmd logs -f --tail 200 $Service
} else {
    Write-Host "Logs de todos os servicos..." -ForegroundColor Cyan
    & $composeCmd logs -f --tail 200
}
