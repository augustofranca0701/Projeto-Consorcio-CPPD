# scripts\check-env.ps1
$repoRoot = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Definition)
$envPath = Join-Path $repoRoot ".env"
$envExample = Join-Path $repoRoot "api-consorcio\env.example"

if (-not (Test-Path $envPath)) {
    if (Test-Path $envExample) {
        Write-Host ".env nao encontrado. Criando a partir de api-consorcio/env.example..." -ForegroundColor Yellow
        Copy-Item $envExample $envPath -Force
        Write-Host ".env criado." -ForegroundColor Green
    } else {
        Write-Host "env.example nao encontrado em api-consorcio. Crie .env manualmente." -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host ".env OK." -ForegroundColor Green
}
