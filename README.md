# 🛒 E-commerce API

Projeto de uma API REST para um sistema de e-commerce, desenvolvido com Java e Spring Boot.  
O objetivo é simular um ambiente real de loja virtual, com gerenciamento de categorias, produtos, usuários e pedidos.

🚧 **Projeto em desenvolvimento** 🚧

---

## 📌 Descrição

Esta API está sendo construída com foco em aprendizado prático de desenvolvimento backend, utilizando boas práticas de arquitetura em camadas.

Atualmente, o projeto já possui uma estrutura organizada com Controller, Service e Model, permitindo a criação e listagem de categorias.

No momento, os dados estão sendo armazenados em memória, mas futuramente será integrado com banco de dados relacional.

---

## 🚀 Tecnologias utilizadas

- Java 21
- Spring Boot
- Spring Web (REST APIs)
- Spring Security *(em breve)*
- Spring Data JPA *(em breve)*
- PostgreSQL ou MySQL *(em breve)*
- Maven
- Lombok

---

## 📂 Estrutura do projeto

com.joaoneto.ecommerce
│

├── controllers # Camada responsável pelas requisições HTTP

├── services # Regras de negócio da aplicação

├── model # Representação das entidades

└── EcommerceApplication.java


---

## 🧠 Lógica atual

- Os dados são armazenados em memória (ArrayList)
- Não há persistência em banco de dados ainda
- Não há validação de dados
- Não há autenticação

---

## 🔐 Funcionalidades futuras

- Integração com banco de dados (PostgreSQL ou MySQL)
- Criação de entidade Produto
- Sistema de usuários e autenticação (Spring Security + JWT)
- Relacionamento entre categorias e produtos
- Sistema de pedidos
- Validações com Bean Validation
- Tratamento global de exceções
- Logs estruturados
- Deploy da aplicação

---

## 📊 Status do projeto

📌 Em desenvolvimento inicial  
📈 Evoluindo para um sistema completo de e-commerce

---

## 🎯 Objetivo

Este projeto tem como objetivo:

- Consolidar conhecimentos em Java e Spring Boot  
- Aprender arquitetura de APIs REST  
- Construir um projeto completo para portfólio  
- Simular um sistema real utilizado no mercado  

---

## 🧑‍💻 Autor

Desenvolvido por **João Neto**

- GitHub: https://github.com/Joaoneto1011

---

## 📌 Observações

Este projeto está sendo desenvolvido passo a passo como parte de estudos.  
Novas funcionalidades serão adicionadas conforme a evolução do aprendizado.
