$envPath = Join-Path $PSScriptRoot "..\.env"
$envExample = Join-Path $PSScriptRoot "..\api-consorcio\env.example"

if (-not (Test-Path $envPath)) {
    Write-Host ".env não encontrado. Criando a partir de env.example..." -ForegroundColor Yellow

    if (-not (Test-Path $envExample)) {
        Write-Host "ERRO: env.example não encontrado. Crie o .env manualmente." -ForegroundColor Red
        exit 1
    }

    Copy-Item $envExample $envPath
}

Write-Host ".env OK." -ForegroundColor Green
