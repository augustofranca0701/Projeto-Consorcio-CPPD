# scripts\run-prod.ps1
$composeCmd = "docker compose"
try { & $composeCmd version | Out-Null } catch { $composeCmd = "docker-compose" }

Write-Host "Iniciando ambiente de producao (profile: prod)..." -ForegroundColor Cyan

$repoRoot = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Definition)
$envPath = Join-Path $repoRoot ".env"
if (-not (Test-Path $envPath)) {
    Write-Host ".env nao encontrado na raiz. Verifique se precisa criar a partir de api-consorcio/env.example" -ForegroundColor Yellow
}

& $composeCmd --profile prod up -d --build
& $composeCmd ps

Write-Host "Ambiente PROD rodando." -ForegroundColor Green
