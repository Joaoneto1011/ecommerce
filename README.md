# 🛒 E-commerce API

API REST para gerenciamento de um sistema de e-commerce desenvolvida com Java e Spring Boot.

O projeto foi criado com foco em aprendizado prático de desenvolvimento backend, aplicando conceitos utilizados em aplicações reais do mercado, como arquitetura em camadas, persistência de dados, DTOs, tratamento de exceções e paginação.

O objetivo é simular um ambiente real de loja virtual, com gerenciamento de categorias, produtos, usuários e pedidos.

🚧 Projeto em desenvolvimento 🚧
---

## 🚀 Tecnologias Utilizadas

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Bean Validation
- H2 Database
- ModelMapper
- Lombok
- Maven
- Postman

---

## 📂 Arquitetura do Projeto

O projeto segue a arquitetura em camadas:

```
src/main/java/com/joaoneto/ecommerce

├── config
├── controllers
├── domain
├── dtos
├── exceptions
├── repositories
├── services
└── EcommerceApplication
```

### Camadas

- Controllers → Recebem requisições HTTP
- Services → Regras de negócio
- Repositories → Comunicação com banco de dados
- Domain → Entidades JPA
- DTOs → Transferência de dados
- Exceptions → Tratamento global de erros
- Config → Configurações da aplicação

---

## 📦 Entidades

### Categoria

| Campo | Tipo |
|---------|---------|
| idCategoria | Long |
| nomeCategoria | String |

Relacionamento:

- Uma categoria possui vários produtos.

---

### Produto

| Campo | Tipo |
|---------|---------|
| idProduto | Long |
| nomeProduto | String |
| descricao | String |
| imagem | String |
| quantidade | Integer |
| preco | Double |
| desconto | Double |
| precoEspecial | Double |

Relacionamento:

- Um produto pertence a uma categoria.

---

## ✅ Funcionalidades Implementadas

### Categorias

- Criar categoria
- Buscar categoria por ID
- Listar categorias
- Atualizar categoria
- Remover categoria
- Paginação
- Ordenação

### Produtos

- Criar produto
- Buscar todos os produtos
- Buscar produtos por categoria
- Buscar produtos por palavra-chave
- Atualizar produto
- Remover produto
- Upload de imagem
- Paginação
- Ordenação

---

## 🔍 Recursos da API

### Paginação

Exemplo:

```http
GET /api/categorias?numeroPagina=0&tamanhoPagina=10
```

---

### Ordenação

Exemplo:

```http
GET /api/categorias?ordenarPorCategoria=idCategoria&classificarOrdem=asc
```

---

### Busca por Categoria

```http
GET /api/public/categorias/{idCategoria}/produtos
```

---

### Busca por Palavra-chave

```http
GET /api/public/produtos/keyword/{keyword}
```

---

### Upload de Imagem

```http
PUT /api/produtos/{idProduto}/imagem
```

Utilizando:

```multipart/form-data
imagem: arquivo.png
```

---

## 🛡️ Validações

O projeto utiliza Bean Validation para garantir integridade dos dados.

Exemplos:

### Categoria

- Nome obrigatório
- Mínimo de 5 caracteres

### Produto

- Nome obrigatório
- Mínimo de 3 caracteres
- Descrição obrigatória
- Mínimo de 6 caracteres

---

## ⚠️ Tratamento Global de Exceções

Implementado utilizando:

```java
@RestControllerAdvice
```

Tratamento para:

- Erros de validação
- Recursos não encontrados
- Regras de negócio da aplicação

Retornando mensagens padronizadas para o cliente.

---

## 🗄️ Banco de Dados

Atualmente o projeto utiliza:

```properties
spring.datasource.url=jdbc:h2:mem:test
```

Banco em memória para desenvolvimento e testes.

Console H2 habilitado:

```properties
spring.h2.console.enabled=true
```

---

## ▶️ Como Executar

### Clonar o projeto

```bash
git clone https://github.com/Joaoneto1011/ecommerce-api.git
```

### Entrar na pasta

```bash
cd ecommerce-api
```

### Executar

```bash
mvn spring-boot:run
```

ou

```bash
./mvnw spring-boot:run
```

---

## 📈 Próximas Implementações

- Spring Security
- JWT Authentication
- Cadastro de usuários
- Carrinho de compras
- Sistema de pedidos
- PostgreSQL
- MySQL
- Swagger/OpenAPI
- Testes unitários
- Docker
- Deploy em nuvem
- Logs estruturados

---

## 🎯 Objetivos do Projeto

Este projeto foi desenvolvido para:

- Aprender Spring Boot na prática
- Construir APIs REST profissionais
- Aplicar arquitetura em camadas
- Trabalhar com persistência de dados
- Utilizar boas práticas de desenvolvimento
- Compor portfólio para oportunidades na área de tecnologia

---

## 👨‍💻 Autor

### João Neto

Estudante de Sistemas de Informação e desenvolvedor backend em formação.

GitHub: 
https://github.com/Joaoneto1011

LinkedIn: 
https://www.linkedin.com/in/joao-rodrigues-neto-855757293/

Email: neto31510@gmail.com

Contato: (34) 99891-6565

---

## 📌 Status do Projeto

🚧 Em desenvolvimento ativo

Novas funcionalidades estão sendo adicionadas continuamente conforme a evolução dos estudos e do projeto.
