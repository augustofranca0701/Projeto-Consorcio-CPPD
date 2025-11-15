# Front-end – Sistema de Consórcios

Este é o Front-end do projeto de gerenciamento de consórcios, desenvolvido em Angular.
A aplicação se comunica com o Back-end (API REST em Spring Boot), permitindo a criação e gerenciamento e grupos, pagamentos e usuários.

## Tecnologias Utilizadas

- Angular
- TypeScript
- HTML5 / CSS3
- RxJS
- Angular Material ou Bootstrap (dependendo da versão usada)
- API REST integrada (localhost:8080)

## Estrutura Geral do Projeto

```
src/
├── app/
│   ├── components/        # Componentes visuais (telas e seções)
│   ├── services/          # Comunicação com o back-end (ApiService)
│   ├── models/            # Interfaces e modelos de dados
│   ├── pages/             # Páginas principais (login, dashboard etc.)
│   ├── app-routing.module.ts  # Configuração das rotas
│   └── app.module.ts          # Módulo principal da aplicação
├── assets/                # Ícones, imagens e recursos estáticos
├── environments/
│   ├── environment.ts
│   └── environment.development.ts  # URL da API configurada aqui
└── index.html             # Ponto de entrada do app
```

## Pré-requisitos

Antes de rodar o projeto, verifique se você possui instalado:

- Node.js
- npm
- Angular CLI

Para instalar o Angular CLI globalmente:

```
npm install -g @angular/cli
```

## Configuração da API

O front se conecta ao back-end usando o arquivo:

```
src/environments/environment.development.ts
```

Certifique-se de que o endereço está correto:

```
export const environment = {
  api: 'http://localhost:8080',
};
```
## Comandos Principais

### 1. Instalar dependências

```
npm install
```

### 2. Rodar o servidor de desenvolvimento

```
npm start
```

ou

```
ng serve
```

Depois abra no navegador:
http://localhost:4200

### 3. Gerar build de produção

```
ng build --configuration production
```

### 4. Verificar e corrigir vulnerabilidades (opcional)

```
npm audit fix
```

## Conceito de Funcionamento

O front-end utiliza o ApiService, localizado em:

```
src/app/services/api.service.ts
```

Esse serviço realiza todas as requisições HTTP ao Back-end:

- /users/login
- /groups
- /payments/:userId

O retorno é em JSON e o front processa para exibir na interface.

## Dicas de Desenvolvimento

- Use console.log com moderação para depurar respostas.
- Componentes devem consumir o ApiService, não fazer requisições diretas.
- Models ajudam a controlar o formato esperado dos dados.
- Se mudar o endereço do Back-end, atualize environment.ts.

## Comandos úteis do Angular CLI

| Comando                  | Função                               |
|--------------------------|----------------------------------------|
| ng g c nome-componente   | Cria um novo componente                |
| ng g s nome-servico      | Cria um novo serviço                   |
| ng g m nome-modulo       | Cria um novo módulo                    |
| ng build                 | Gera a build de produção               |
| ng lint                  | Verifica problemas de código           |

## Observações

- O Back-end deve estar rodando para que o front funcione normalmente.
- Se ocorrer erro de CORS, configure o Back-end para aceitar requests de http://localhost:4200.

## Licença

Este projeto é de uso livre para fins educacionais e demonstração técnica.
