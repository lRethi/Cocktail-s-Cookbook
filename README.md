# Cocktail's Cookbook

Sistema de gerenciamento e consulta de receitas de bebidas, desenvolvido em Java com Hibernate ORM e MySQL.

O **Cocktail's Cookbook** permite cadastrar bebidas, organizar seus ingredientes, classificá-las por categorias e tags, além de realizar pesquisas utilizando diferentes critérios.

## Integrantes

- **Frederico**
- **Livia**

## Tecnologias utilizadas

- **Java 21** — linguagem de programação.
- **Hibernate ORM 7** — mapeamento objeto-relacional e persistência de dados.
- **MySQL / Percona Server** — sistema de gerenciamento de banco de dados.
- **Maven** — gerenciamento de dependências e construção do projeto.
- **HQL (Hibernate Query Language)** — consultas orientadas às entidades Java.

## Funcionalidades

### Gerenciamento de bebidas

- Cadastrar novas bebidas.
- Consultar bebidas por ID ou nome.
- Listar bebidas cadastradas.
- Atualizar informações de bebidas.
- Excluir bebidas.
- Duplicar registros de bebidas.

### Gerenciamento de ingredientes

- Cadastrar ingredientes.
- Consultar ingredientes.
- Atualizar informações.
- Excluir ingredientes, respeitando seus relacionamentos com as bebidas.

### Relacionamentos

- Associar múltiplos ingredientes a uma bebida.
- Registrar quantidade, unidade de medida e preparação de cada ingrediente.
- Associar múltiplas tags a uma bebida.
- Organizar bebidas por categorias e características.

### Sistema de pesquisas

O projeto implementa consultas utilizando HQL, permitindo:

- Pesquisar bebidas pelo nome.
- Encontrar bebidas que utilizam determinado ingrediente.
- Pesquisar por quantidade mínima de um ingrediente.
- Pesquisar por intervalos de quantidade.
- Pesquisar por quantidade máxima.
- Encontrar bebidas que não possuem determinado ingrediente.
- Combinar múltiplas condições em uma única pesquisa.

## Modelagem do banco de dados

O sistema utiliza entidades Java mapeadas pelo Hibernate ORM.

| Entidade | Responsabilidade |
|---|---|
| `Drink` | Armazena informações das bebidas. |
| `Ingredient` | Armazena os ingredientes disponíveis. |
| `DrinkIngredient` | Representa o relacionamento entre bebidas e ingredientes, incluindo quantidade, unidade e preparação. |
| `Tag` | Armazena classificações e características das bebidas. |

### Relacionamentos

- **Drink e Ingredient:** relacionamento muitos-para-muitos implementado por meio da entidade associativa `DrinkIngredient`.
- **Drink e Tag:** relacionamento muitos-para-muitos implementado com uma tabela intermediária.

## Estrutura do projeto

```text
cocktails-cookbook/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── cocktails/
│   │   │           ├── dao/
│   │   │           ├── model/
│   │   │           ├── service/
│   │   │           ├── util/
│   │   │           └── Main.java
│   │   └── resources/
│   └── test/
│       └── java/
├── pom.xml
└── README.md
```

### Organização das camadas

- **Model:** contém as entidades e seus respectivos mapeamentos ORM.
- **DAO:** concentra as operações de acesso e manipulação dos dados.
- **Service:** destinada à implementação das regras de negócio.
- **Util:** contém utilitários, incluindo a configuração e inicialização do Hibernate.
- **Main:** ponto de entrada da aplicação.

## Configuração e execução

### Pré-requisitos

- JDK 21 ou compatível com a configuração do Maven.
- Maven.
- MySQL ou Percona Server.
- Banco de dados configurado para a aplicação.

### 1. Criar o banco de dados

Execute no cliente MySQL:

```sql
CREATE DATABASE cocktails_cookbook;
```

### 2. Configurar as variáveis de ambiente

A aplicação utiliza as seguintes variáveis:

```text
DB_URL=jdbc:mysql://localhost:3306/cocktails_cookbook
DB_USER=cocktails_app
DB_PASSWORD=sua_senha
```

Configure os valores de acordo com seu ambiente. Não compartilhe credenciais reais nem as versione no repositório.

### 3. Executar o projeto

Na raiz do repositório, execute:

```bash
mvn clean compile exec:java -Dexec.mainClass="com.cocktails.Main"
```

O Maven realizará a compilação e executará a classe principal da aplicação.

## Persistência de dados

O Hibernate ORM realiza o mapeamento entre as entidades Java e as tabelas do banco de dados.

As operações de persistência são realizadas por meio dos DAOs, utilizando sessões e transações do Hibernate.

O projeto utiliza HQL para realizar consultas sobre as entidades mapeadas, permitindo trabalhar com objetos e relacionamentos sem depender diretamente dos nomes físicos das tabelas.

## Objetivo acadêmico

O Cocktail's Cookbook foi desenvolvido como projeto acadêmico com o objetivo de aplicar conceitos de desenvolvimento Java e Banco de Dados, especialmente:

- Mapeamento objeto-relacional (ORM).
- Persistência de dados.
- Relacionamentos entre entidades.
- Operações CRUD.
- Consultas utilizando HQL.
- Organização em camadas com o padrão DAO.
- Integração entre Java e MySQL.

---

**Cocktail's Cookbook**  
Projeto acadêmico desenvolvido por Frederico e Livia.
