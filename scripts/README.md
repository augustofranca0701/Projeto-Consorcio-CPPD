# Scripts do Projeto CONSORCIAR

Esta pasta contém scripts auxiliares para desenvolvimento, teste e operação
do ambiente Docker do projeto **CONSORCIAR**.

Eles fornecem uma interface padronizada para subir containers, derrubar,
verificar logs e reconstruir imagens, além de um menu interativo que facilita
o uso no dia a dia.

## ✔ Estrutura dos arquivos

```powershell
scripts/
│
├── menu.ps1          → Painel interativo principal (recomendado)
|── run-dev.ps1       → Sobe ambiente de desenvolvimento
├── run-prod.ps1      → Sobe ambiente de produção
├── rebuild.ps1       → Build completo (sem cache)
├── logs.ps1          → Logs gerais ou de um serviço específico
├── down.ps1          → Derruba containers (com opção de remover volumes)
└── check-env.ps1     → Valida dependências como Docker e Docker Compose
```

---

## ✔ Requisitos

- Windows  
- PowerShell 5.1 ou 7+  
- Docker Desktop instalado  
- `docker compose` funcionando  
Política de execução permitindo scripts locais:

```powershell
Set-ExecutionPolicy -Scope CurrentUser RemoteSigned
```

---

## ✔ Como usar o menu principal

O menu é a forma mais simples de operar o ambiente:

```powershell
.\scripts\menu.ps1
```

Funções disponíveis no menu:

- Rodar DEV  
- Rodar PROD  
- Ver todos os logs  
- Ver logs de um container específico  
- Derrubar containers  
- Derrubar containers + volumes  
- Rebuild completo  
- Sair  

O menu também possui animações de carregamento e um cabeçalho estilizado
para facilitar leitura.

---

## ✔ Scripts individuais

### `run-dev.ps1`

Sobe containers com o perfil de desenvolvimento:

```powershell
docker compose --profile dev up -d
```

### `run-prod.ps1`

Sobe containers usando o perfil de produção:

```powershell
docker compose --profile prod up -d
```

### `rebuild.ps1`

Força rebuild sem cache:

```powershell
docker compose build --no-cache
docker compose up -d
```

### `logs.ps1`

Exibe logs:

- Todos os serviços:

```powershell
.\logs.ps1
```

- Serviço específico:

```powershell
.\logs.ps1 -Service api-consorcio
```

### `down.ps1`

Derruba containers:

```powershell
docker compose down
```

Remover volumes:

```powershell
.\down.ps1 -RemoveVolumes
```

### `check-env.ps1`

Verifica:

- Docker instalado  
- Docker rodando  
- Docker Compose disponível  
- Versões mínimas recomendadas  

---

## ✔ Boas práticas

- Não versione o arquivo `.env` na raiz do projeto.  
- Mantenha o arquivo `env.example` atualizado.  
- Execute o `check-env.ps1` ao configurar uma máquina nova.  
- Evite rodar scripts como Administrador sem necessidade.  

---

## ✔ Colaborando

Ao alterar ou adicionar scripts:

1. Use verbos aprovados pelo PowerShell (`Test-`, `Get-`, `Write-`, etc.).  
2. Garanta compatibilidade com PowerShell 5.1 e 7.  
3. Teste os scripts tanto pelo menu quanto individualmente.  
4. Atualize este README quando necessário.  

---

## ✔ Licença

Uso interno do projeto **CONSORCIAR**.
