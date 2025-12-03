# scripts\rebuild.ps1
<#
Rebuild completo da stack:
 - opcionalmente derruba containers e volumes
 - builda todos os services (api, db, frontend_dev, frontend_prod)
 - sobe a stack incluindo profiles dev e prod
Uso:
  .\rebuild.ps1             -> rebuild com cache, sem remover volumes
  .\rebuild.ps1 -NoCache   -> rebuild sem cache
  .\rebuild.ps1 -RemoveVolumes -> faz down -v antes de subir (apaga dados)
#>

param(
    [switch] $NoCache,
    [switch] $RemoveVolumes
)

function Write-Info { param($m) Write-Host $m -ForegroundColor Cyan }
function Write-Warn { param($m) Write-Host $m -ForegroundColor Yellow }
function Write-Err  { param($m) Write-Host $m -ForegroundColor Red }

# detecta docker compose CLI vs docker-compose
$composeCmd = "docker compose"
try { & $composeCmd version | Out-Null } catch { $composeCmd = "docker-compose" }

Write-Info ("[rebuild] usando: {0}" -f $composeCmd)

# 0) down (com ou sem volumes)
try {
    if ($RemoveVolumes) {
        Write-Warn "Parando containers e removendo volumes nomeados (ATENCAO: apaga dados persistentes)..."
        & $composeCmd down -v --remove-orphans 2>&1 | ForEach-Object { Write-Host $_ }
    } else {
        Write-Info "Parando containers (sem remover volumes)..."
        & $composeCmd down --remove-orphans 2>&1 | ForEach-Object { Write-Host $_ }
    }
} catch {
    Write-Warn ("Falha ao executar down: {0}" -f $_)
}

# Helper para build (tenta por grupo)
function Do-Build {
    param(
        [string[]] $Args
    )
    try {
        if ($NoCache) {
            Write-Info ("Executando build (no-cache) para: {0}" -f ($Args -join ", "))
            & $composeCmd build --no-cache @Args 2>&1 | ForEach-Object { Write-Host $_ }
        } else {
            Write-Info ("Executando build para: {0}" -f ($Args -join ", "))
            & $composeCmd build @Args 2>&1 | ForEach-Object { Write-Host $_ }
        }
    } catch {
        Write-Err ("Erro durante build para {0}: {1}" -f ($Args -join ", "), $_)
        throw $_
    }
}

# 1) build dos serviços sem profile (api, db). Mesmo que db seja imagem oficial, chamar build não faz mal.
Do-Build -Args @("api","db")

# 2) build frontend dev (profile dev) - usa compose with profile
try {
    Write-Info "Construindo frontend (profile dev)..."
    if ($NoCache) {
        & $composeCmd --profile dev build --no-cache frontend_dev 2>&1 | ForEach-Object { Write-Host $_ }
    } else {
        & $composeCmd --profile dev build frontend_dev 2>&1 | ForEach-Object { Write-Host $_ }
    }
} catch {
    Write-Warn ("Falha ao buildar frontend_dev via compose: {0}" -f $_)
    Write-Warn "Tentaremos fallback (build manual) mais adiante se necessario."
}

# 3) build frontend prod (profile prod)
try {
    Write-Info "Construindo frontend (profile prod)..."
    if ($NoCache) {
        & $composeCmd --profile prod build --no-cache frontend_prod 2>&1 | ForEach-Object { Write-Host $_ }
    } else {
        & $composeCmd --profile prod build frontend_prod 2>&1 | ForEach-Object { Write-Host $_ }
    }
} catch {
    Write-Warn ("Falha ao buildar frontend_prod via compose: {0}" -f $_)
}

# 4) subir tudo — passando ambos os profiles para incluir frontend_dev e frontend_prod
try {
    Write-Info "Subindo toda a stack (profiles dev e prod ativos)..."
    & $composeCmd --profile dev --profile prod up -d 2>&1 | ForEach-Object { Write-Host $_ }
} catch {
    Write-Err ("Erro no 'compose up': {0}" -f $_)
    throw $_
}

Start-Sleep -Seconds 2

# 5) status e logs resumidos
Write-Info "`nStatus dos containers:"
& $composeCmd ps --all | ForEach-Object { Write-Host $_ }

Write-Info "`nLogs (ultimas 200 linhas) - api e frontend_dev:"
& $composeCmd logs --tail 200 api frontend_dev frontend_prod 2>&1 | ForEach-Object { Write-Host $_ }

Write-Info "`nRebuild completo."
Write-Info "Ambiente rodando."