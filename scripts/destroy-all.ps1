# scripts/destroy-all.ps1
<#
Destroy-all (robusto)
Remove containers, redes, (opcional) volumes e imagens relacionadas a um projeto.
Por seguranca, usa filtro por nome de projeto (padrao: "consorcio") para nao apagar recursos alheios.
#>

param(
    [string] $ProjectFilter = "consorcio",
    [switch] $RemoveVolumes,
    [switch] $RemoveImages,
    [switch] $Force
)

# Funcoes de log (usando verbos aprovados: Write-*)
function Write-Info {
    param([string] $m)
    Write-Host "[INFO]  $m" -ForegroundColor Cyan
}
function Write-Warn {
    param([string] $m)
    Write-Host "[WARN]  $m" -ForegroundColor Yellow
}
function Write-ErrorCustom {
    param([string] $m)
    Write-Host "[ERROR] $m" -ForegroundColor Red
}
function Write-Success {
    param([string] $m)
    Write-Host "[OK]    $m" -ForegroundColor Green
}

# Detecta docker compose CLI vs docker-compose
$composeCmd = "docker compose"
try { & $composeCmd version > $null 2>&1 } catch { $composeCmd = "docker-compose" }

Write-Info ("Usando: {0}" -f $composeCmd)
Write-Info ("Filtro de projeto: '{0}'" -f $ProjectFilter)

if (-not $Force) {
    Write-Host ""
    $confirmProceed = Read-Host "Voce tem certeza que quer prosseguir com a limpeza para filtro '$ProjectFilter'? (Digite 'SIM' para confirmar)"
    if ($confirmProceed -ne "SIM") {
        Write-Warn "Operacao cancelada pelo usuario."
        exit 0
    }
}

# Helper: executar comando e capturar saida com tratamento
function Exec-Cmd {
    param(
        [string] $Cmd,
        [string[]] $Args
    )

    try {
        & $Cmd @Args 2>&1 | ForEach-Object { Write-Host $_ }
        return $true
    } catch {
        Write-Warn ("Falha ao executar '{0} {1}': {2}" -f $Cmd, ($Args -join ' '), $_)
        return $false
    }
}

# 1) Tentar docker compose down --remove-orphans (ponto de partida)
Write-Info "Executando: $composeCmd down --remove-orphans (tenta derrubar stack compose)"
Exec-Cmd -Cmd $composeCmd -Args @("down","--remove-orphans")

Start-Sleep -Milliseconds 500

# 2) Remover containers relacionados (por nome)
Write-Info "Listando containers com nome contendo '$ProjectFilter'..."
$containersRaw = docker ps -a --format "{{.ID}} {{.Names}} {{.Status}}" 2>$null
$containers = @()
if ($containersRaw) {
    foreach ($line in $containersRaw) {
        if ($line -match $ProjectFilter) {
            $parts = $line -split "\s+"
            $id = $parts[0]
            $name = $parts[1]
            $containers += [PSCustomObject]@{ ID = $id; Name = $name; Raw = $line }
        }
    }
}

if ($containers.Count -gt 0) {
    Write-Warn ("{0} container(s) relacionados encontrados:" -f $containers.Count)
    $containers | ForEach-Object { Write-Host "  $($_.Raw)" }
    if (-not $Force) {
        $ok = Read-Host "Remover esses containers? (Digite 'SIM' para confirmar)"
        if ($ok -ne "SIM") {
            Write-Warn "Pulando remocao de containers por confirmacao."
        } else {
            foreach ($c in $containers) {
                try {
                    Write-Info ("Removendo container {0} ({1})..." -f $c.ID, $c.Name)
                    docker rm -f $c.ID 2>&1 | ForEach-Object { Write-Host $_ }
                } catch {
                    Write-Warn ("Falha ao remover container {0}: {1}" -f $c.ID, $_)
                }
            }
        }
    } else {
        foreach ($c in $containers) {
            try {
                Write-Info ("Removendo container {0} ({1})..." -f $c.ID, $c.Name)
                docker rm -f $c.ID 2>&1 | ForEach-Object { Write-Host $_ }
            } catch {
                Write-Warn ("Falha ao remover container {0}: {1}" -f $c.ID, $_)
            }
        }
    }
} else {
    Write-Info "Nenhum container relacionado encontrado."
}

Start-Sleep -Milliseconds 300

# 3) Remover redes relacionadas
Write-Info "Procurando redes com nome contendo '$ProjectFilter'..."
$networksRaw = docker network ls --format "{{.ID}} {{.Name}}" 2>$null
$networks = @()
if ($networksRaw) {
    foreach ($line in $networksRaw) {
        if ($line -match $ProjectFilter) {
            $parts = $line -split "\s+"
            $nid = $parts[0]
            $nname = $parts[1]
            $networks += [PSCustomObject]@{ ID = $nid; Name = $nname; Raw = $line }
        }
    }
}

if ($networks.Count -gt 0) {
    Write-Warn ("{0} rede(s) relacionadas encontradas:" -f $networks.Count)
    $networks | ForEach-Object { Write-Host "  $($_.Raw)" }
    foreach ($n in $networks) {
        try {
            Write-Info ("Tentando remover rede {0} ..." -f $n.Name)
            docker network rm $n.Name 2>&1 | ForEach-Object { Write-Host $_ }
        } catch {
            Write-Warn ("Nao foi possivel remover rede {0}: {1}" -f $n.Name, $_)
        }
    }
} else {
    Write-Info "Nenhuma rede relacionada encontrada."
}

Start-Sleep -Milliseconds 300

# 4) Volumes (opcional)
if ($RemoveVolumes) {
    Write-Info "Procurando volumes com nome contendo '$ProjectFilter' ou 'postgres'..."
    $volumesRaw = docker volume ls --format "{{.Name}}" 2>$null
    $volumes = @()
    if ($volumesRaw) {
        foreach ($v in $volumesRaw) {
            if ($v -match $ProjectFilter -or $v -match "postgres") {
                $volumes += $v
            }
        }
    }

    if ($volumes.Count -gt 0) {
        Write-Warn ("{0} volume(s) possivelmente relacionados:" -f $volumes.Count)
        $volumes | ForEach-Object { Write-Host "  $_" }
        if (-not $Force) {
            $okv = Read-Host "Remover esses volumes? Isso apaga dados persistentes (Digite 'SIM' para confirmar)"
        } else {
            $okv = "SIM"
        }

        if ($okv -eq "SIM") {
            foreach ($v in $volumes) {
                try {
                    Write-Info ("Removendo volume {0}..." -f $v)
                    docker volume rm $v 2>&1 | ForEach-Object { Write-Host $_ }
                } catch {
                    Write-Warn ("Falha ao remover volume {0}: {1}" -f $v, $_)
                }
            }
        } else {
            Write-Warn "Pulando remocao de volumes por confirmacao."
        }
    } else {
        Write-Info "Nenhum volume relacionado encontrado."
    }
}

Start-Sleep -Milliseconds 300

# 5) Imagens (opcional)
if ($RemoveImages) {
    Write-Warn "Procurando imagens com repo/tag contendo '$ProjectFilter'..."
    $imagesRaw = docker images --format "{{.Repository}}:{{.Tag}} {{.ID}}" 2>$null
    $images = @()
    if ($imagesRaw) {
        foreach ($line in $imagesRaw) {
            if ($line -match $ProjectFilter) {
                $parts = $line -split "\s+"
                $imgRef = $parts[0]
                $imgID  = $parts[1]
                $images += [PSCustomObject]@{ Ref = $imgRef; ID = $imgID; Raw = $line }
            }
        }
    }

    if ($images.Count -gt 0) {
        Write-Warn ("{0} imagem(ns) encontradas:" -f $images.Count)
        $images | ForEach-Object { Write-Host "  $($_.Raw)" }
        if (-not $Force) {
            $okI = Read-Host "Remover essas imagens? (Digite 'SIM' para confirmar)"
        } else {
            $okI = "SIM"
        }

        if ($okI -eq "SIM") {
            foreach ($img in $images) {
                try {
                    Write-Info ("Removendo imagem {0} ({1})..." -f $img.Ref, $img.ID)
                    docker rmi -f $img.Ref 2>&1 | ForEach-Object { Write-Host $_ }
                } catch {
                    Write-Warn ("Falha ao remover imagem {0}: {1}" -f $img.Ref, $_)
                }
            }
        } else {
            Write-Warn "Pulando remocao de imagens por confirmacao."
        }
    } else {
        Write-Info "Nenhuma imagem relacionada encontrada."
    }
}

# 6) limpeza final: containers/parciais/restart policies
Write-Info "Verificacao final - containers e redes restantes (filtro = '$ProjectFilter'):"

$remainingContainers = docker ps -a --format "{{.ID}} {{.Names}} {{.Status}}" | Where-Object { $_ -match $ProjectFilter }
if ($remainingContainers) {
    Write-Warn "Containers ainda presentes:"
    $remainingContainers | ForEach-Object { Write-Host "  $_" }
} else {
    Write-Success "Nenhum container restante com o filtro."
}

$remainingNets = docker network ls --format "{{.ID}} {{.Name}}" | Where-Object { $_ -match $ProjectFilter }
if ($remainingNets) {
    Write-Warn "Redes ainda presentes:"
    $remainingNets | ForEach-Object { Write-Host "  $_" }
} else {
    Write-Success "Nenhuma rede restante com o filtro."
}

Write-Success "`nOperacao destroy-all concluida."
