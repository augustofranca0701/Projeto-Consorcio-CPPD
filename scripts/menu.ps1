# scripts\menu.ps1
# Painel Principal - CONSORCIAR - Docker
# Autor: Augusto Franca
# Versao final (sincronizada com destroy-all.ps1 robusto)

function Show-Header {
    Clear-Host
    Write-Host "[====         CONSORCIAR - Painel Docker        ====]" -ForegroundColor Cyan
    Write-Host "[===================================================]" -ForegroundColor DarkCyan
    Write-Host ""
}

function Wait-ForEnter {
    param([string]$Message = "Pressione ENTER para continuar...")
    Write-Host ""
    Read-Host $Message | Out-Null
}

# detecta docker compose variant
function Get-ComposeCmd {
    $c = "docker compose"
    try { & $c version > $null 2>&1; return $c } catch { return "docker-compose" }
}

$ScriptsRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition
$running = $true

while ($running) {
    Show-Header
    Write-Host "[1] Rodar ambiente de Desenvolvimento (dev)" -ForegroundColor White
    Write-Host "[2] Rodar ambiente de Producao (prod)" -ForegroundColor White
    Write-Host "[3] Logs (todos os servicos)" -ForegroundColor White
    Write-Host "[4] Logs (servico especifico)" -ForegroundColor White
    Write-Host "[5] Derrubar containers e redes (safe) - usa destroy-all (sem volumes/imagens)" -ForegroundColor White
    Write-Host "[6] Derrubar containers + volumes (dangerous) - usa destroy-all -RemoveVolumes" -ForegroundColor Yellow
    Write-Host "[7] Build completo (API + Front + DB) -- opcional --no-cache" -ForegroundColor White
    Write-Host "[8] DERRUBAR TUDO (FORCE) -> containers, redes, volumes e imagens (muito perigoso)" -ForegroundColor Red
    Write-Host "[9] Promover usuário para SYSTEM_ADMIN" -ForegroundColor Magenta
    Write-Host "[0] Sair" -ForegroundColor DarkRed
    Write-Host ""
    $op = Read-Host "Selecione uma opcao"

    switch ($op) {

        "1" {
            $scriptPath = Join-Path $ScriptsRoot "run-dev.ps1"
            if (Test-Path $scriptPath) {
                & $scriptPath
            } else {
                Write-Host "run-dev.ps1 nao encontrado." -ForegroundColor Yellow
            }
            Wait-ForEnter
        }

        "2" {
            $scriptPath = Join-Path $ScriptsRoot "run-prod.ps1"
            if (Test-Path $scriptPath) {
                & $scriptPath
            } else {
                Write-Host "run-prod.ps1 nao encontrado." -ForegroundColor Yellow
            }
            Wait-ForEnter
        }

        "3" {
            $scriptPath = Join-Path $ScriptsRoot "logs.ps1"
            if (Test-Path $scriptPath) {
                & $scriptPath
            } else {
                Write-Host "logs.ps1 nao encontrado." -ForegroundColor Yellow
            }
            Wait-ForEnter
        }

        "4" {
            $svcName = Read-Host "Nome do servico (ex: api, db, consorcio_frontend_dev)"
            $scriptPath = Join-Path $ScriptsRoot "logs.ps1"
            if (Test-Path $scriptPath) {
                & $scriptPath -Service $svcName
            } else {
                Write-Host "logs.ps1 nao encontrado." -ForegroundColor Yellow
            }
            Wait-ForEnter
        }

        "5" {
            # safe down: chama destroy-all sem remover volumes/imagens, forçando remocao de containers/redes
            $scriptPath = Join-Path $ScriptsRoot "destroy-all.ps1"
            if (Test-Path $scriptPath) {
                Write-Host "Executando limpeza segura: remove containers e redes relacionadas (sem volumes/imagens)..." -ForegroundColor Cyan
                & $scriptPath -ProjectFilter "consorcio" -Force
            } else {
                # fallback simples
                $compose = Get-ComposeCmd
                Write-Host "destroy-all.ps1 nao encontrado. Executando fallback: $compose down --remove-orphans" -ForegroundColor Yellow
                & $compose down --remove-orphans 2>&1 | ForEach-Object { Write-Host $_ }
            }
            Wait-ForEnter
        }

        "6" {
            # down + volumes
            $scriptPath = Join-Path $ScriptsRoot "destroy-all.ps1"
            if (Test-Path $scriptPath) {
                Write-Host "Opcao perigosa: remover volumes. Confirmacao adicional requerida." -ForegroundColor Yellow
                $confirmVolumes = Read-Host "Deseja remover volumes nomeados relacionados também? Digite 'SIM' para confirmar"
                if ($confirmVolumes -eq "SIM") {
                    & $scriptPath -ProjectFilter "consorcio" -RemoveVolumes
                } else {
                    Write-Host "Operacao cancelada." -ForegroundColor Cyan
                }
            } else {
                $compose = Get-ComposeCmd
                Write-Host "destroy-all.ps1 nao encontrado. Executando fallback: $compose down -v --remove-orphans" -ForegroundColor Yellow
                $confirm = Read-Host "Remover volumes nomeados? (Digite 'SIM' para confirmar)"
                if ($confirm -eq "SIM") {
                    & $compose down -v --remove-orphans 2>&1 | ForEach-Object { Write-Host $_ }
                } else {
                    Write-Host "Operacao cancelada." -ForegroundColor Cyan
                }
            }
            Wait-ForEnter
        }

        "7" {
            # build-all: chama build-all.ps1; pergunta sobre no-cache e sobre remover volumes antes
            $scriptPath = Join-Path $ScriptsRoot "build-all.ps1"
            if (-not (Test-Path $scriptPath)) {
                Write-Host "build-all.ps1 nao encontrado." -ForegroundColor Yellow
                Wait-ForEnter
                break
            }

            $useNoCacheInput = Read-Host "Deseja usar --no-cache no build? (s/N)"
            $removeVolumesInput = Read-Host "Deseja remover volumes antes do build? (s/N) - cuidado: apaga dados persistentes"
            $argsToPass = @()

            if ($useNoCacheInput -match '^[sS]') { $argsToPass += "-NoCache" }
            if ($removeVolumesInput -match '^[sS]') { $argsToPass += "-RemoveVolumes" }

            if ($argsToPass.Count -gt 0) {
                & $scriptPath @argsToPass
            } else {
                & $scriptPath
            }
            Wait-ForEnter
        }

        "8" {
            # FORCE TOTAL: volumes + imagens (muito perigoso)
            $scriptPath = Join-Path $ScriptsRoot "destroy-all.ps1"
            Write-Host ""
            Write-Host "!!!! ACAO IRREVERSIVEL !!!!" -ForegroundColor Red
            $confirm1 = Read-Host "Tem certeza? (Digite 'SIM' para prosseguir)"
            if ($confirm1 -ne "SIM") { Write-Host "Operacao cancelada." -ForegroundColor Cyan; Wait-ForEnter; break }

            $confirm2 = Read-Host "Essa acao REMOVERA IMAGENS E VOLUMES. Digite 'CONFIRMO' para prosseguir"
            if ($confirm2 -ne "CONFIRMO") { Write-Host "Operacao cancelada." -ForegroundColor Cyan; Wait-ForEnter; break }

            if (Test-Path $scriptPath) {
                & $scriptPath -ProjectFilter "consorcio" -RemoveVolumes -RemoveImages -Force
            } else {
                Write-Host "destroy-all.ps1 nao encontrado. Rodando fallback: docker compose down -v e removendo imagens manualmente." -ForegroundColor Yellow
                $compose = Get-ComposeCmd
                & $compose down -v --remove-orphans 2>&1 | ForEach-Object { Write-Host $_ }
                # remover imagens com filtro (cuidado)
                $imgs = docker images --format "{{.Repository}}:{{.Tag}}" | Where-Object { $_ -match "consorcio|projeto-consorcio" }
                if ($imgs) {
                    foreach ($i in $imgs) { docker rmi -f $i 2>&1 | ForEach-Object { Write-Host $_ } }
                }
            }
            Wait-ForEnter
        }

"9" {
    $scriptPath = Join-Path $ScriptsRoot "promote-system-admin.ps1"
    if (Test-Path $scriptPath) {
        & $scriptPath
    } else {
        Write-Host "promote-system-admin.ps1 not found." -ForegroundColor Yellow
    }

    Write-Host ""
    Read-Host "Press ENTER to return to menu" | Out-Null
}



        "0" {
            Write-Host "Saindo..." -ForegroundColor DarkRed
            $running = $false
        }

        default {
            Write-Host "Opcao invalida!" -ForegroundColor Red
            Wait-ForEnter
        }
    }
}

Write-Host "Menu encerrado." -ForegroundColor Yellow
