# ===============================
# Painel Principal - CONSORCIAR (com animacoes)
# ===============================

# --- Funcoes utilitarias de animacao -----------------

function Write-Typewriter {
    param(
        [string]$Text,
        [int]$DelayMs = 6
    )
    foreach ($c in $Text.ToCharArray()) {
        Write-Host -NoNewline $c
        Start-Sleep -Milliseconds $DelayMs
    }
    Write-Host ""
}

# spinner simples
function Show-SpinnerFrame {
    param([int]$i)
    $frames = @('|','/','-','\')
    return $frames[$i % $frames.Length]
}

# Executa um ScriptBlock em job e mostra spinner enquanto roda
function Invoke-WithSpinner {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory=$true)]
        [ScriptBlock]$ScriptBlock,

        [string]$Message = "Aguarde...",

        [int]$SleepMs = 80
    )

    # inicia job em background
    $job = Start-Job -ScriptBlock $ScriptBlock

    $i = 0
    Write-Host -NoNewline "$Message "
    while ($job.State -eq 'Running') {
        $frame = Show-SpinnerFrame -i $i
        Write-Host -NoNewline "`b$frame"
        Start-Sleep -Milliseconds $SleepMs
        $i++
    }

    # job terminou: coleta saida e erros
    $output = Receive-Job -Job $job -ErrorAction SilentlyContinue
    $exitState = $job.State

    # limpa job
    Remove-Job -Job $job -Force -ErrorAction SilentlyContinue

    # sobrescreve o spinner com um check/ X
    if ($exitState -eq 'Completed') {
        Write-Host "`b" -NoNewline
        Write-Host "✓" -ForegroundColor Green
    } else {
        Write-Host "`b" -NoNewline
        Write-Host "✗" -ForegroundColor Red
    }

    # exibe output (se houver)
    if ($null -ne $output -and $output.Count -gt 0) {
        Write-Host "`n--- Output ---" -ForegroundColor Cyan
        $output | ForEach-Object { Write-Host $_ }
    }

    return $exitState
}

# Versao mais simples para comandos rapidos que nao precisam de job
function Invoke-WithDots {
    param(
        [ScriptBlock]$ScriptBlock,
        [string]$Message = "Aguarde",
        [int]$Repeat = 3
    )
    Write-Host -NoNewline "$Message"
    for ($i=0; $i -lt $Repeat; $i++) {
        Start-Sleep -Milliseconds 350
        Write-Host -NoNewline "."
    }
    Write-Host ""
    & $ScriptBlock
}

# -----------------------------------------------------

function Show-Header {
    Clear-Host
    # Write-Typewriter pode ser lento; comente se preferir sem animacao
    Write-Typewriter "[====         CONSORCIAR - Painel Docker        ====]" 2
    Write-Host "[===================================================]" -ForegroundColor Cyan
    Write-Host ""
}

function Wait-ForEnter {
    param(
        [string]$Message = "Pressione ENTER para continuar..."
    )
    Write-Host ""
    Read-Host $Message | Out-Null
}

# variavel de controle do loop
$running = $true

while ($running) {
    Show-Header

    Write-Host "[1] Rodar ambiente de Desenvolvimento" -ForegroundColor White
    Write-Host "[2] Rodar ambiente de Producao" -ForegroundColor White
    Write-Host "[3] Logs (todos os servicos)" -ForegroundColor White
    Write-Host "[4] Logs (servico especifico)" -ForegroundColor White
    Write-Host "[5] Derrubar containers" -ForegroundColor White
    Write-Host "[6] Derrubar containers + volumes" -ForegroundColor White
    Write-Host "[7] Rebuild completo (--no-cache)" -ForegroundColor White
    Write-Host "[0] Sair" -ForegroundColor Red
    Write-Host ""

    $op = Read-Host "Selecione uma opcao"

    switch ($op) {

        "1" {
            Invoke-WithSpinner -Message "Subindo ambiente DEV" -ScriptBlock {
                & (Join-Path $PSScriptRoot "run-dev.ps1") 2>&1
            } | Out-Null
            Wait-ForEnter
        }

        "2" {
            Invoke-WithSpinner -Message "Subindo ambiente PROD" -ScriptBlock {
                & (Join-Path $PSScriptRoot "run-prod.ps1") 2>&1
            } | Out-Null
            Wait-ForEnter
        }

        "3" {
            & (Join-Path $PSScriptRoot "logs.ps1")
            Wait-ForEnter
        }

        "4" {
            $svc = Read-Host "Nome do servico (ex: api-consorcio)"
            & (Join-Path $PSScriptRoot "logs.ps1") -Service $svc
            Wait-ForEnter
        }

        "5" {
            Invoke-WithSpinner -Message "Derrubando containers" -ScriptBlock {
                & (Join-Path $PSScriptRoot "down.ps1") 2>&1
            } | Out-Null
            Wait-ForEnter
        }

        "6" {
            Invoke-WithSpinner -Message "Derrubando containers e volumes" -ScriptBlock {
                & (Join-Path $PSScriptRoot "down.ps1") -RemoveVolumes 2>&1
            } | Out-Null
            Wait-ForEnter
        }

        "7" {
            Invoke-WithSpinner -Message "Forcando rebuild (no-cache)" -ScriptBlock {
                & (Join-Path $PSScriptRoot "rebuild.ps1") 2>&1
            } | Out-Null
            Wait-ForEnter
        }

        "0" {
            Write-Host "Saindo..." -ForegroundColor Red
            $running = $false
        }

        default {
            Write-Host "Opcao invalida!" -ForegroundColor Red
            Wait-ForEnter
        }
    }
}

Write-Host "Menu encerrado." -ForegroundColor Yellow
