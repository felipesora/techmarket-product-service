<div align="center">

# 📦 TechMarket Product Service

### Serviço responsável pelo catálogo, gerenciamento de produtos e controle de estoque da plataforma TechMarket.

<br/>

[![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge\&logo=openjdk\&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge\&logo=spring-boot\&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge\&logo=springsecurity\&logoColor=white)](https://spring.io/projects/spring-security)
[![MongoDB](https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge\&logo=mongodb\&logoColor=white)](https://www.mongodb.com/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge\&logo=rabbitmq\&logoColor=white)](https://www.rabbitmq.com/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge\&logo=docker\&logoColor=white)](https://www.docker.com/)

</div>

---

## 📋 Índice

- [Sobre o Product Service](#-sobre-o-product-service)
- [Principais Funcionalidades](#️-principais-funcionalidades)
- [Arquitetura e Papel no Sistema](#-arquitetura-e-papel-no-sistema)
- [Tecnologias Utilizadas](#️-tecnologias-utilizadas)
- [Dependências Relevantes](#-dependências-relevantes)
- [Boas Práticas Aplicadas](#-boas-práticas-aplicadas)
- [Integração com Outros Serviços](#-integração-com-outros-serviços)
- [Repositórios](#-repositórios)
- [Autor](#-autor)

---

## 💡 Sobre o Product Service

O **Product Service** é o microsserviço responsável pelo gerenciamento do catálogo de produtos do TechMarket. Ele centraliza todas as operações relacionadas a produtos, incluindo cadastro, atualização, consulta e controle de estoque.

Diferente de outros serviços que utilizam banco relacional, este serviço utiliza **MongoDB**, adotando um modelo orientado a documentos, ideal para estruturas flexíveis de produtos.

Esse serviço foi projetado com foco em:

* **Alta performance em leitura**
* **Flexibilidade de dados**
* **Escalabilidade horizontal**
* **Desacoplamento de domínio**

---

## ⚙️ Principais Funcionalidades

* 📦 Cadastro de produtos
* ✏️ Atualização de dados do produto
* ❌ Remoção de produtos
* 🔍 Listagem de produtos com filtros (nome, categoria, etc.)
* 📄 Consulta detalhada de produto
* 📊 Controle de estoque
* 📢 Publicação de eventos via RabbitMQ (ex: alteração de estoque)
* 🔐 Proteção de endpoints com autenticação JWT
* 🔍 Registro no Eureka (Service Discovery)

---

## 🧱 Arquitetura e Papel no Sistema

O Product Service se posiciona como:

```
Frontend → Gateway → Product Service → MongoDB
                         ↓
                     RabbitMQ → Outros Serviços
```

### Responsabilidades:

| Responsabilidade | Descrição                        |
| ---------------- | -------------------------------- |
| Catálogo         | Gerenciamento de produtos        |
| Persistência     | Armazenamento em MongoDB         |
| Estoque          | Controle de disponibilidade      |
| Eventos          | Publicação de eventos de domínio |
| Segurança        | Validação de JWT                 |
| Integração       | Registro no Eureka               |

---

## 🛠️ Tecnologias Utilizadas

### Backend

* Java 21
* Spring Boot 3.5
* Spring Web
* Spring Validation
* Spring Security

### Banco de Dados

* MongoDB (NoSQL - documentos)

### Mensageria

* RabbitMQ
* Spring AMQP

### Segurança

* JWT (JSON Web Token)
* Biblioteca `java-jwt` (Auth0)

### Cloud & Infra

* Spring Cloud Netflix Eureka Client
* Docker

### Utilitários

* Lombok

---

## 📦 Dependências Relevantes

Principais dependências do projeto:

* `spring-boot-starter-data-mongodb`
* `spring-boot-starter-web`
* `spring-boot-starter-security`
* `spring-boot-starter-validation`
* `spring-boot-starter-amqp`
* `java-jwt`
* `spring-cloud-starter-netflix-eureka-client`

---

## 📊 Boas Práticas Aplicadas

* Arquitetura em camadas (Controller → Service → Repository)
* Separação de responsabilidades (SRP)
* Uso de DTOs para comunicação externa
* Validações com Bean Validation
* Persistência desacoplada com MongoDB
* Comunicação assíncrona orientada a eventos
* Segurança stateless com JWT
* Uso de Service Discovery (Eureka)

---

## 🔗 Integração com Outros Serviços

| Serviço         | Integração                        |
| --------------- | --------------------------------- |
| Gateway         | Roteamento do serviço             |
| Identity        | Validação de autenticação via JWT |
| Order Service   | Consulta de produtos e estoque    |
| Discovery       | Registro via Eureka               |
| RabbitMQ        | Publicação de eventos             |

---

## 📁 Repositórios

O TechMarket é organizado como um **monorepo com submódulos Git**. Cada serviço possui seu próprio repositório:

| Serviço | Descrição | Repositório |
|---------|-----------|-------------|
| 🗂️ **techmarket** | Repositório principal (monorepo + Docker Compose) | [github.com/felipesora/techmarket](https://github.com/felipesora/techmarket) |
| 🔍 **discovery-service** | Eureka Server para service discovery | [github.com/felipesora/techmarket-discovery-service](https://github.com/felipesora/techmarket-discovery-service) |
| 🌐 **gateway-service** | API Gateway com Spring Cloud Gateway | [github.com/felipesora/techmarket-gateway-service](https://github.com/felipesora/techmarket-gateway-service) |
| 🔐 **identity-service** | Autenticação e gerenciamento de usuários (JWT) | [github.com/felipesora/techmarket-identity-service](https://github.com/felipesora/techmarket-identity-service) |
| 📦 **product-service** | Catálogo e gerenciamento de produtos | [github.com/felipesora/techmarket-product-service](https://github.com/felipesora/techmarket-product-service) |
| 🛒 **order-service** | Criação e acompanhamento de pedidos | [github.com/felipesora/techmarket-order-service](https://github.com/felipesora/techmarket-order-service) |
| 💳 **payment-service** | Processamento de pagamentos via mensageria | [github.com/felipesora/techmarket-payment-service](https://github.com/felipesora/techmarket-payment-service) |
| 🖥️ **techmarket-web** | Frontend da plataforma em Angular | [github.com/felipesora/techmarket-web](https://github.com/felipesora/techmarket-web) |

---

## 👨‍💻 Autor

Desenvolvido por **Felipe Sora**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/felipesora)
[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/felipesora)
