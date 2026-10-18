# Guia para agentes de IA

## Visão geral

Este repositório contém apenas o backend REST do Corban Multibancos, uma aplicação Spring Boot para administrar clientes, propostas de empréstimo, bancos, funcionários, equipes, usuários e relatórios. A API é escrita em Java 21 e usa Spring Data JPA, Bean Validation, Spring Security com JWT, Redis para cache e PostgreSQL ou H2 como banco de dados.

## Estrutura do código

O código principal está em `src/main/java/com/corbanmultibancos/business`:

- `controllers`: endpoints REST e transformação de respostas HTTP.
- `services`: regras de negócio, transações, autorização, relatórios e exportação CSV.
- `repositories`: repositórios JPA e consultas especializadas. As consultas de propostas usam Criteria API.
- `entities`: entidades persistidas e relacionamentos JPA.
- `dto`: objetos de entrada e saída da API; mantenha entidades fora do contrato HTTP.
- `mappers`: conversão explícita entre entidades e DTOs.
- `validations`: validações Bean Validation, validadores customizados e grupos de criação/atualização.
- `config`: configuração de segurança, cache, OpenAPI e dependências da aplicação.
- `util`: utilitários e mensagens de erro.

Os testes ficam em `src/test/java/com/corbanmultibancos/business`, organizados principalmente entre testes de serviços e testes de integração dos controllers.

## Fluxo e regras importantes

- Mantenha as responsabilidades separadas: controllers tratam HTTP, services aplicam regras de negócio e repositories acessam os dados.
- Propostas relacionam obrigatoriamente funcionário, cliente e banco. Antes de alterar essas relações, confira entidades, DTOs, mappers, validações, consultas e testes relacionados.
- O papel `GESTOR` tem acesso administrativo; consultas e operações de propostas também restringem dados/operações ao funcionário responsável. Preserve a autorização existente nos serviços ao alterar endpoints ou fluxos.
- Use os DTOs e validadores existentes. A resposta de erros HTTP é centralizada em `controllers/exceptions/ControllerExceptionHandler`.
- Endpoints e autenticação são documentados com anotações OpenAPI; mantenha a documentação atualizada ao mudar o contrato da API.
- Há cache Redis para bancos, equipes e relatórios. Mudanças de leitura ou escrita nesses dados podem exigir revisão das anotações/configurações de cache.
- As exportações CSV têm serviços próprios. Evite duplicar a formatação CSV em controllers ou services de domínio.

## Configuração e execução

- A versão do Java e dependências estão em `pom.xml`; use o Maven Wrapper (`./mvnw`) para builds e testes.
- `application.properties` seleciona o perfil pela variável `APPLICATION_PROFILE` (padrão `test`) e a porta por `PORT` (padrão `8080`).
- O perfil `test` usa H2 em memória e espera Redis em `localhost:6379`.
- O perfil `dev` usa PostgreSQL e Redis; o Compose e `.env.example` definem as variáveis para execução em containers. Inicie com `docker compose up --build -d` e encerre com `docker compose down`.
- **Atenção:** o perfil `dev` configura `spring.jpa.hibernate.ddl-auto=create-drop`; não o utilize com dados que precisam ser preservados.
- O perfil `prod` exige as variáveis de conexão e configuração correspondentes. Não inclua credenciais, chaves JWT ou outros segredos no repositório; use configuração externa.

## Verificação

Use o Maven Wrapper correspondente ao sistema operacional para executar os testes:

```bash
# Linux/macOS
./mvnw test

# Windows
./mvnw.cmd test
```

Para empacotar a aplicação, use o mesmo wrapper com o objetivo `package`:

```bash
# Linux/macOS
./mvnw package

# Windows
./mvnw.cmd package
```

Ao implementar mudanças, adicione ou ajuste testes no padrão já usado: testes de serviço com JUnit 5 e Mockito e testes de integração para endpoints, validações e segurança. Prefira executar primeiro a classe de teste diretamente relacionada e, depois, a suíte necessária para validar interações.

## Cuidados ao trabalhar no repositório

- Leia o código e os testes próximos antes de introduzir novos padrões ou dependências.
- Preserve alterações já existentes no worktree; não reverta mudanças fora do escopo da tarefa.
- Faça mudanças pequenas e alinhadas à estrutura atual, incluindo testes e documentação diretamente afetados.
- Não altere regras de autorização, formato de resposta, persistência ou configuração de ambiente sem verificar as superfícies relacionadas.
