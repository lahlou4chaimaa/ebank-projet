# EBank — Application bancaire en microservices avec assistant IA

Application bancaire distribuée développée avec **Spring Boot, Spring Cloud, Angular et Spring AI**.

Le projet met en œuvre une architecture **microservices** permettant de gérer les clients et les comptes bancaires, avec un **assistant conversationnel intelligent** capable d'interroger les services métier grâce au protocole **MCP — Model Context Protocol**.

L'application peut être utilisée depuis une interface web Angular et intègre également des canaux conversationnels via **Discord** et **Telegram**.

---

## Sommaire

- [Présentation](#-présentation)
- [Objectifs](#-objectifs)
- [Fonctionnalités](#-fonctionnalités)
- [Architecture globale](#-architecture-globale)
- [Technologies utilisées](#-technologies-utilisées)
- [Structure du projet](#-structure-du-projet)
- [Description des microservices](#-description-des-microservices)
  - [Discovery Service](#1-discovery-service)
  - [Customer Service](#2-customer-service)
  - [EBank Service](#3-ebank-service)
  - [Gateway Service](#4-gateway-service)
  - [EBank Bot](#5-ebank-bot)
  - [Angular Frontend](#6-angular-frontend)
- [Communication entre microservices](#-communication-entre-microservices)
- [OpenFeign](#-communication-avec-openfeign)
- [Résilience avec Resilience4j](#-résilience-avec-resilience4j)
- [Model Context Protocol MCP](#-model-context-protocol-mcp)
- [Assistant IA](#-assistant-ia)
- [Mémoire conversationnelle](#-mémoire-conversationnelle)
- [Discord](#-intégration-discord)
- [Telegram](#-intégration-telegram)
- [API REST](#-api-rest)
- [Frontend Angular](#-frontend-angular)
- [Configuration](#-configuration)
- [Installation](#-installation)
- [Lancement](#-lancement)
- [Tests](#-tests)
- [Scénarios d'utilisation](#-scénarios-dutilisation)
- [Sécurité](#-sécurité)
- [Problèmes connus](#-problèmes-connus)
- [Améliorations possibles](#-améliorations-possibles)
- [Compétences développées](#-compétences-développées)
- [Conclusion](#-conclusion)
- [Auteur](#-auteur)

---

# Présentation

**EBank** est une application bancaire conçue autour d'une architecture **microservices**.

L'objectif principal est de séparer les différentes responsabilités de l'application afin d'obtenir un système :

- modulaire ;
- maintenable ;
- évolutif ;
- résilient ;
- facilement extensible ;
- adapté à l'intégration de fonctionnalités d'intelligence artificielle.

L'application permet notamment de :

- gérer les clients bancaires ;
- gérer les comptes bancaires ;
- récupérer les informations d'un client ;
- récupérer les comptes d'un client ;
- créer des clients ;
- créer des comptes ;
- communiquer entre microservices ;
- découvrir automatiquement les services disponibles ;
- router les requêtes via une API Gateway ;
- utiliser un assistant IA pour interroger les données bancaires ;
- connecter l'assistant à des outils métier via MCP ;
- conserver le contexte des conversations ;
- utiliser l'assistant depuis une interface Angular ;
- utiliser l'assistant depuis Discord ;
- utiliser l'assistant depuis Telegram ;
- traiter des messages contenant du texte et des images dans Telegram.

---

# Objectifs

Ce projet a été réalisé afin de mettre en pratique plusieurs concepts avancés du développement backend et des systèmes distribués.

## Objectifs techniques

### Architecture microservices

Découper l'application en plusieurs services indépendants :

```text
Customer Service
       |
       |
EBank Service
       |
       |
Gateway
       |
       |
Discovery / Eureka
       |
       |
Frontend / AI Assistant
```

Chaque service possède sa propre responsabilité.

---

### Service Discovery

Utiliser **Netflix Eureka** pour permettre aux microservices de se découvrir dynamiquement.

Au lieu de dépendre uniquement d'adresses IP ou de ports codés en dur, les services s'enregistrent auprès d'un serveur Eureka.

---

### API Gateway

Mettre en place un point d'entrée unique permettant de router les requêtes vers les microservices.

---

### Communication inter-services

Utiliser :

- REST ;
- OpenFeign ;
- Eureka ;
- Resilience4j.

---

### Intelligence artificielle

Intégrer un assistant conversationnel avec :

- Spring AI ;
- OpenAI ;
- ChatClient ;
- Tool Calling ;
- MCP ;
- mémoire conversationnelle.

---

### Interfaces conversationnelles

Permettre à l'utilisateur d'interagir avec l'assistant depuis :

- Angular ;
- Discord ;
- Telegram.

---

# Fonctionnalités

## Gestion des clients

Le `customer-service` permet :

- d'afficher tous les clients ;
- de rechercher un client par identifiant ;
- de créer un client.

Exemples :

```http
GET /customers
```

```http
GET /customers/{id}
```

```http
POST /customers
```

---

## Gestion des comptes bancaires

Le `ebank-service` permet :

- de récupérer tous les comptes ;
- de récupérer les comptes d'un client donné ;
- de rechercher un compte par identifiant ;
- de créer un compte.

Exemples :

```http
GET /accounts
```

```http
GET /accounts?customerId=1
```

```http
GET /accounts/{id}
```

```http
POST /accounts
```

---

## Assistant bancaire IA

L'utilisateur peut poser des questions en langage naturel.

Exemples :

```text
Bonjour
```

```text
Liste tous les clients
```

```text
Donne-moi les comptes du client 1
```

```text
Quel est le compte avec l'identifiant xxx ?
```

L'assistant utilise le modèle IA et peut appeler les outils MCP disponibles afin d'obtenir les informations métier.

---

# Architecture globale

L'architecture du projet peut être représentée ainsi :

```text
                         ┌──────────────────────┐
                         │      Utilisateur     │
                         └──────────┬───────────┘
                                    │
                ┌───────────────────┼───────────────────┐
                │                   │                   │
                ▼                   ▼                   ▼
        ┌──────────────┐     ┌──────────────┐    ┌──────────────┐
        │   Angular    │     │   Discord    │    │   Telegram   │
        │   Frontend   │     │     Bot      │    │     Bot      │
        └──────┬───────┘     └──────┬───────┘    └──────┬───────┘
               │                    │                   │
               └────────────────────┼───────────────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │    EBank Bot        │
                         │                     │
                         │    Spring AI        │
                         │    OpenAI           │
                         │    MCP Client       │
                         │    Chat Memory      │
                         └──────────┬──────────┘
                                    │
                           MCP / Tool Calling
                                    │
                    ┌───────────────┴────────────────┐
                    │                                │
                    ▼                                ▼
          ┌──────────────────┐             ┌──────────────────┐
          │ Customer Service │             │  EBank Service   │
          │                  │             │                  │
          │ Customers        │             │ Bank Accounts    │
          │ JPA              │             │ JPA              │
          │ H2               │             │ H2               │
          │ MCP Server       │             │ MCP Server       │
          └────────┬─────────┘             └────────┬─────────┘
                   │                                │
                   └──────────────┬─────────────────┘
                                  │
                                  ▼
                       ┌─────────────────────┐
                       │   Eureka Server     │
                       │   Service Discovery │
                       │      :8761          │
                       └─────────────────────┘

                         API Gateway :9999
                               │
                 ┌─────────────┼─────────────┐
                 ▼             ▼             ▼
             CUSTOMER       EBANK        EBANK-BOT
              SERVICE       SERVICE        SERVICE
```

---

# Technologies utilisées

## Backend

| Technologie | Utilisation |
|---|---|
| Java 21 | Langage principal |
| Spring Boot 4.x | Framework backend |
| Spring Cloud | Architecture distribuée |
| Spring Cloud Netflix Eureka | Service Discovery |
| Spring Cloud Gateway | API Gateway |
| Spring Data JPA | Persistence |
| H2 Database | Base de données |
| OpenFeign | Communication inter-services |
| Resilience4j | Circuit Breaker |
| Spring AI | Intégration IA |
| OpenAI | Modèle de langage |
| MCP | Exposition et consommation d'outils IA |
| SpringDoc OpenAPI | Documentation API |
| Lombok | Réduction du code boilerplate |
| Maven | Gestion du projet |

## Frontend

| Technologie | Utilisation |
|---|---|
| Angular 21 | Interface web |
| TypeScript | Langage frontend |
| RxJS | Programmation réactive |
| ngx-markdown | Affichage des réponses Markdown |
| Angular HttpClient | Communication HTTP |

## Messagerie / plateformes

| Technologie | Utilisation |
|---|---|
| Discord | Interface conversationnelle |
| Telegram | Interface conversationnelle et images |

---

# Structure du projet

```text
ebank-projet/
│
├── angular-front/
│   ├── src/
│   │   ├── app/
│   │   │   ├── accounts/
│   │   │   ├── bot-ui/
│   │   │   ├── interceptors/
│   │   │   ├── model/
│   │   │   ├── services/
│   │   │   ├── app.config.ts
│   │   │   ├── app.html
│   │   │   ├── app.routes.ts
│   │   │   └── app.ts
│   │   ├── main.ts
│   │   └── styles.css
│   ├── angular.json
│   ├── package.json
│   └── tsconfig.json
│
├── customer-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── org/lahlou/net/customerservice/
│   │   │   │       ├── controllers/
│   │   │   │       ├── entities/
│   │   │   │       ├── repository/
│   │   │   │       ├── service/
│   │   │   │       └── CustomerServiceApplication.java
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   ├── pom.xml
│   ├── mvnw
│   └── mvnw.cmd
│
├── discovery-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   │       └── application.properties
│   └── pom.xml
│
├── ebank-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── org/lahlou/net/ebankservice/
│   │   │   │       ├── controllers/
│   │   │   │       ├── entities/
│   │   │   │       ├── feign/
│   │   │   │       ├── model/
│   │   │   │       ├── repository/
│   │   │   │       ├── services/
│   │   │   │       └── EbankServiceApplication.java
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   └── pom.xml
│
├── gateway-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── application.yml
│   └── pom.xml
│
├── ebank-bot/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── org/lahlou/net/ebankbot/
│   │   │   │       ├── agents/
│   │   │   │       ├── controllers/
│   │   │   │       ├── discord/
│   │   │   │       ├── telegram/
│   │   │   │       └── EbankBotApplication.java
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   └── pom.xml
│
├── pom.xml
└── .gitignore
```

---

# Description des microservices

# 1. Discovery Service

Le `discovery-service` est le serveur **Eureka**.

Son rôle est de maintenir un registre des microservices disponibles.

Port :

```text
8761
```

Configuration :

```properties
spring.application.name=discovery-service
server.port=8761

eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

Le serveur ne s'enregistre donc pas lui-même auprès d'Eureka.

Il fonctionne comme le registre central des services.

---

## Pourquoi Eureka ?

Sans Eureka, un service devrait connaître directement :

```text
http://localhost:8056
http://localhost:8057
```

Avec Eureka, les services peuvent être identifiés par leur nom :

```text
customer-service
ebank-service
ebank-bot
```

Cela permet de réduire le couplage entre les services.

---

# 2. Customer Service

Le `customer-service` est responsable de la gestion des clients.

Port :

```text
8056
```

Base de données :

```text
H2
```

Base :

```text
customer-db
```

---

## Architecture interne

```text
CustomerRestController
        │
        ▼
CustomerService
        │
        ▼
CustomerRepository
        │
        ▼
      H2
```

---

## Entity Customer

L'entité `Customer` représente un client bancaire.

Elle est persistée avec :

```java
@Entity
```

et gérée par Spring Data JPA.

---

## Repository

Le projet utilise :

```java
CustomerRepository
```

pour communiquer avec la base de données.

---

## Service

La classe :

```text
CustomerService
```

contient la logique métier liée aux clients.

---

## Controller

Le contrôleur expose :

### Liste des clients

```http
GET /customers
```

### Client par identifiant

```http
GET /customers/{id}
```

### Création d'un client

```http
POST /customers
```

Exemple :

```json
{
  "name": "Sara",
  "email": "sara@example.com"
}
```

---

## MCP Server

Le `customer-service` possède également un serveur MCP.

Configuration :

```properties
spring.ai.mcp.server.protocol=STREAMABLE
spring.ai.mcp.server.streamable-http.mcp-endpoint=/mcp
```

Cela permet au service de mettre ses capacités métier à disposition d'un agent IA.

---

# 3. EBank Service

Le `ebank-service` est responsable de la gestion des comptes bancaires.

Port :

```text
8057
```

Base de données :

```text
H2
```

Base :

```text
accounts-db
```

---

## Architecture interne

```text
EbankController
       │
       ▼
 EbankService
       │
       ├───────────────► BankAccountRepository
       │
       ▼
CustomerRestClient
       │
       ▼
Customer Service
```

---

## Entité BankAccount

L'entité principale est :

```text
BankAccount
```

Elle contient notamment :

```text
id
balance
createdAt
type
customerId
customer
```

Exemple conceptuel :

```json
{
  "id": "ACC-001",
  "balance": 5000,
  "createdAt": "2026-11-24",
  "type": "CURRENT",
  "customerId": 1
}
```

Le champ `customer` est marqué :

```java
@Transient
```

car les informations du client sont récupérées depuis le `customer-service` plutôt que stockées directement dans cette entité.

---

# API des comptes

## Tous les comptes

```http
GET /accounts
```

---

## Comptes d'un client

```http
GET /accounts?customerId=1
```

---

## Compte par identifiant

```http
GET /accounts/{id}
```

---

## Créer un compte

```http
POST /accounts
```

---

# 4. Gateway Service

Le `gateway-service` constitue le point d'entrée de l'architecture.

Port configuré actuellement :

```text
9999
```

Il utilise :

```text
Spring Cloud Gateway
```

et :

```text
Eureka Discovery
```

---

## Routage dynamique

Le projet utilise :

```java
DiscoveryClientRouteDefinitionLocator
```

pour créer les routes à partir des services découverts.

Le principe est :

```text
Client
   │
   ▼
Gateway :9999
   │
   ▼
Eureka
   │
   ├── CUSTOMER-SERVICE
   ├── EBANK-SERVICE
   └── EBANK-BOT
```

Ainsi, le Gateway peut router les requêtes vers les services enregistrés dans Eureka.

---

# 5. EBank Bot

`ebank-bot` est la partie intelligence artificielle du projet.

Port :

```text
8058
```

Il utilise :

- Spring AI ;
- OpenAI ;
- MCP Client ;
- MCP Server ;
- ChatClient ;
- mémoire conversationnelle ;
- Discord ;
- Telegram.

---

# Architecture de l'assistant IA

```text
                User
                 │
        ┌────────┼─────────┐
        │        │         │
     Angular  Discord   Telegram
        │        │         │
        └────────┼─────────┘
                 │
                 ▼
          EbankAIAgent
                 │
                 ▼
             ChatClient
                 │
                 ▼
              OpenAI
                 │
          Tool Calling
                 │
          ┌──────┴───────┐
          ▼              ▼
   Customer MCP     EBank MCP
          │              │
          ▼              ▼
 Customer Service   EBank Service
```

---

# EbankAIAgent

La classe principale de l'agent est :

```text
EbankAIAgent
```

Elle configure :

- le `ChatClient` ;
- le prompt système ;
- la mémoire ;
- les outils MCP.

---

## Prompt système

L'agent est configuré pour répondre aux questions relatives :

```text
aux clients
aux comptes bancaires
```

Lorsqu'aucun contexte pertinent n'est disponible, le comportement configuré est :

```text
JE NE SAIS PAS
```

Cela permet de limiter les réponses hors domaine.

---

# OpenAI

Le modèle configuré dans le projet est :

```text
gpt-4o
```

Configuration :

```properties
spring.ai.openai.chat.options.model=gpt-4o
spring.ai.openai.api-key=${OPENAI_KEY}
```

La clé API ne doit jamais être écrite directement dans le code source.

---

# Model Context Protocol — MCP

L'un des éléments importants du projet est l'utilisation de **MCP**.

MCP permet à un modèle IA d'utiliser des outils externes de manière structurée.

Dans ce projet :

```text
Customer Service
       │
       ▼
    MCP Server
       │
       ▼
    EBank Bot
       │
       ▼
    MCP Client
       │
       ▼
    OpenAI
```

Le bot peut ainsi utiliser les capacités exposées par les microservices.

---

## Customer MCP

Le `customer-service` expose un serveur MCP sur :

```text
/mcp
```

Le bot le configure avec :

```properties
spring.ai.mcp.client.streamable-http.connections.customer.url=http://localhost:8056
```

---

## EBank MCP

Le `ebank-service` expose également MCP.

Configuration du bot :

```properties
spring.ai.mcp.client.streamable-http.connections.ebank.url=http://localhost:8057
```

---

# Exemple de fonctionnement MCP

L'utilisateur demande :

```text
Liste tous les clients
```

Le processus peut être représenté ainsi :

```text
Utilisateur
     │
     ▼
Question en langage naturel
     │
     ▼
OpenAI / Spring AI
     │
     ▼
Identification du besoin
     │
     ▼
Tool MCP Customer
     │
     ▼
Customer Service
     │
     ▼
CustomerRepository
     │
     ▼
H2
     │
     ▼
Résultat
     │
     ▼
LLM
     │
     ▼
Réponse naturelle
```

L'intérêt est que le modèle ne doit pas connaître directement la structure interne de la base de données.

Il utilise les capacités métier exposées par le service.

---

# Communication avec OpenFeign

Le `ebank-service` doit pouvoir récupérer les informations d'un client.

Pour cela, le projet utilise **OpenFeign**.

Le client est :

```text
CustomerRestClient
```

Il est déclaré avec :

```java
@FeignClient(name = "customer-service")
```

Le service peut donc appeler :

```http
GET /customers/{id}
```

sans écrire manuellement toute la logique HTTP.

---

## Exemple

```text
EBank Service
      │
      │ OpenFeign
      ▼
Customer Service
      │
      ▼
Customer
```

Cela permet à un compte bancaire de retourner également les informations associées au client.

---

# Résilience avec Resilience4j

Le projet utilise **Resilience4j Circuit Breaker** pour éviter qu'une panne du `customer-service` ne provoque automatiquement une panne complète du `ebank-service`.

Le client Feign utilise :

```java
@CircuitBreaker(
    name = "customerService",
    fallbackMethod = "getDefaultCustomer"
)
```

En cas d'échec, une valeur de secours peut être retournée :

```text
Not available
```

Cela introduit une première forme de **fault tolerance**.

---

# Mémoire conversationnelle

L'assistant utilise :

```java
MessageChatMemoryAdvisor
```

pour conserver le contexte de conversation.

L'agent utilise également un identifiant de conversation :

```text
session-utilisateur-1
```

Cela permet au modèle de recevoir l'historique associé à la conversation.

---

## Exemple

Utilisateur :

```text
Bonjour, je m'appelle Sara.
```

Puis :

```text
Quel est mon nom ?
```

L'assistant peut utiliser la mémoire conversationnelle pour conserver le contexte précédent.

---

## Architecture mémoire

```text
User message
     │
     ▼
ChatClient
     │
     ▼
MessageChatMemoryAdvisor
     │
     ▼
Conversation Memory
     │
     ▼
LLM
```

---

# API REST de l'assistant

Le `ebank-bot` expose actuellement :

```http
GET /chat
```

avec le paramètre :

```text
query
```

Exemple :

```http
GET /chat?query=Bonjour
```

Le contrôleur transmet ensuite la question à :

```text
EbankAIAgent
```

---

# Discord

Le projet intègre un bot Discord.

Classe :

```text
DiscordBot
```

Le bot utilise :

```text
spring-boot-starter-discord
```

---

## Fonctionnement

```text
Discord User
      │
      ▼
Discord Bot
      │
      ▼
EbankAIAgent
      │
      ▼
Spring AI
      │
      ▼
OpenAI + MCP
      │
      ▼
Response
      │
      ▼
Discord
```

Le bot ignore également les messages provenant d'autres bots.

---

# Telegram

Le projet intègre également un bot Telegram.

Classe :

```text
TelegramBot
```

La communication utilise le mécanisme **Long Polling**.

---

## Fonctionnalités Telegram

Le bot peut recevoir :

- messages texte ;
- images ;
- légendes associées aux images.

Lorsqu'une image est envoyée, le bot :

1. récupère le fichier Telegram ;
2. construit l'URL du fichier ;
3. charge l'image ;
4. construit un `UserMessage` ;
5. ajoute le média ;
6. transmet le message à l'agent IA ;
7. récupère la réponse ;
8. renvoie la réponse à Telegram.

---

## Flux image

```text
Utilisateur Telegram
        │
        ▼
       Image
        │
        ▼
Telegram Bot
        │
        ▼
Telegram API
        │
        ▼
Téléchargement image
        │
        ▼
Spring AI Media
        │
        ▼
EbankAIAgent
        │
        ▼
OpenAI
        │
        ▼
Réponse
        │
        ▼
Telegram
```

Le bot envoie également une action `typing` pendant le traitement.

---

# Angular Frontend

Le frontend est développé avec **Angular 21**.

Répertoire :

```text
angular-front/
```

---

## Structure frontend

```text
angular-front/
└── src/
    └── app/
        ├── accounts/
        │   ├── accounts.ts
        │   ├── accounts.html
        │   └── accounts.css
        │
        ├── bot-ui/
        │   ├── bot-ui.ts
        │   ├── bot-ui.html
        │   └── bot-ui.css
        │
        ├── interceptors/
        │
        ├── model/
        │   └── account.model.ts
        │
        ├── services/
        │   └── loading.ts
        │
        ├── app.ts
        ├── app.html
        ├── app.css
        ├── app.config.ts
        └── app.routes.ts
```

---

# Page Accounts

Le composant :

```text
Accounts
```

récupère les comptes bancaires avec Angular `HttpClient`.

L'URL actuellement présente dans le code est :

```text
http://localhost:5555/EBANK-SERVICE/accounts
```

Le composant utilise RxJS :

```text
Observable
map
catchError
of
```

pour gérer les données et les erreurs.

---

# Interface du chatbot

Le composant :

```text
BotUi
```

permet d'envoyer une question à l'assistant.

Exemple :

```text
Quel est le solde du compte ?
```

La question est encodée avec :

```typescript
encodeURIComponent()
```

puis envoyée au backend.

Le composant utilise également :

```text
ngx-markdown
```

afin d'afficher correctement les réponses Markdown.

---

# Gestion du chargement

Le projet possède un :

```text
LoadingService
```

basé sur les Angular Signals.

Il expose :

```typescript
isLoading
```

et permet de modifier l'état :

```typescript
setLoading(value)
```

---

# Configuration

Les différentes applications utilisent des fichiers :

```text
application.properties
```

---

## Discovery Service

```properties
spring.application.name=discovery-service
server.port=8761

eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

---

## Customer Service

```properties
spring.application.name=customer-service
server.port=8056

spring.datasource.url=jdbc:h2:mem:customer-db

spring.cloud.discovery.enabled=true
spring.cloud.config.enabled=false

spring.ai.mcp.server.protocol=STREAMABLE
spring.ai.mcp.server.streamable-http.mcp-endpoint=/mcp
```

---

## EBank Service

```properties
spring.application.name=ebank-service
server.port=8057

spring.datasource.url=jdbc:h2:mem:accounts-db
spring.h2.console.enabled=true

spring.cloud.discovery.enabled=true
spring.cloud.config.enabled=false

spring.ai.mcp.server.protocol=STREAMABLE
spring.ai.mcp.server.streamable-http.mcp-endpoint=/mcp
```

---

## Gateway

```properties
spring.application.name=gateway-service
server.port=9999

spring.cloud.discovery.enabled=true
```

---

## EBank Bot

```properties
spring.application.name=ebank-bot
server.port=8058

spring.ai.openai.chat.options.model=gpt-4o
spring.ai.openai.api-key=${OPENAI_KEY}

spring.cloud.discovery.enabled=true
spring.cloud.config.enabled=false

spring.ai.mcp.client.streamable-http.connections.customer.url=http://localhost:8056

spring.ai.mcp.client.streamable-http.connections.ebank.url=http://localhost:8057

discord.token=${DISCORD_TOKEN}

telegram.token=${TELEGRAM_TOKEN}
```

---

# Variables d'environnement

Le projet utilise plusieurs secrets.

Ils doivent être fournis via des variables d'environnement.

## OpenAI

```text
OPENAI_KEY
```

## Discord

```text
DISCORD_TOKEN
```

## Telegram

```text
TELEGRAM_TOKEN
```

Exemple Windows PowerShell :

```powershell
$env:OPENAI_KEY="YOUR_OPENAI_KEY"
$env:DISCORD_TOKEN="YOUR_DISCORD_TOKEN"
$env:TELEGRAM_TOKEN="YOUR_TELEGRAM_TOKEN"
```

Ne jamais commit directement les valeurs réelles dans Git.

---

# Installation

## Prérequis

Installer :

- Java 21 ;
- Maven ;
- Node.js ;
- npm ;
- Angular CLI ;
- un compte OpenAI si l'assistant IA doit être utilisé ;
- un bot Discord si Discord est utilisé ;
- un bot Telegram si Telegram est utilisé.

Vérifier Java :

```powershell
java -version
```

Vérifier Maven :

```powershell
mvn -version
```

Vérifier Node :

```powershell
node -v
```

Vérifier npm :

```powershell
npm -v
```

---

# Cloner le projet

```bash
git clone https://github.com/lahlou4chaimaa/ebank-projet.git
```

Puis :

```bash
cd ebank-projet
```

---

# Lancer les microservices

L'ordre recommandé est le suivant.

## 1. Eureka

```powershell
cd discovery-service
.\mvnw spring-boot:run
```

Eureka sera disponible sur :

```text
http://localhost:8761
```

---

## 2. Customer Service

Dans un autre terminal :

```powershell
cd customer-service
.\mvnw spring-boot:run
```

Port :

```text
8056
```

---

## 3. EBank Service

```powershell
cd ebank-service
.\mvnw spring-boot:run
```

Port :

```text
8057
```

---

## 4. Gateway

```powershell
cd gateway-service
.\mvnw spring-boot:run
```

Port :

```text
9999
```

---

## 5. EBank Bot

Configurer d'abord :

```text
OPENAI_KEY
DISCORD_TOKEN
TELEGRAM_TOKEN
```

Puis :

```powershell
cd ebank-bot
.\mvnw spring-boot:run
```

Port :

```text
8058
```

---

# Lancer Angular

Entrer dans :

```text
angular-front
```

Installer les dépendances :

```bash
npm install
```

Puis :

```bash
npm start
```

ou :

```bash
ng serve
```

Angular sera généralement disponible sur :

```text
http://localhost:4200
```

---

# Vérification du système

Une fois les services démarrés, vérifier Eureka :

```text
http://localhost:8761
```

Les services doivent être enregistrés auprès du serveur Eureka.

On doit notamment retrouver :

```text
CUSTOMER-SERVICE
EBANK-SERVICE
EBANK-BOT
GATEWAY-SERVICE
```

---

# Tests REST

## Customer Service

### Récupérer tous les clients

```http
GET http://localhost:8056/customers
```

---

### Récupérer un client

```http
GET http://localhost:8056/customers/1
```

---

### Créer un client

```http
POST http://localhost:8056/customers
Content-Type: application/json
```

Exemple :

```json
{
  "name": "Sara",
  "email": "sara@example.com"
}
```

---

# Tests EBank

### Tous les comptes

```http
GET http://localhost:8057/accounts
```

### Comptes d'un client

```http
GET http://localhost:8057/accounts?customerId=1
```

### Compte par ID

```http
GET http://localhost:8057/accounts/1
```

### Créer un compte

```http
POST http://localhost:8057/accounts
Content-Type: application/json
```

Exemple :

```json
{
  "id": "ACC-001",
  "balance": 5000,
  "createdAt": "2026-11-24T00:00:00.000+00:00",
  "type": "CURRENT",
  "customerId": 1
}
```

---

# Test de l'assistant IA

Le endpoint principal actuellement exposé est :

```http
GET http://localhost:8058/chat?query=Bonjour
```

Exemple :

```text
http://localhost:8058/chat?query=Liste%20tous%20les%20clients
```

---

# Exemples de questions

## Questions générales

```text
Bonjour
```

```text
Comment ça va ?
```

---

## Questions métier

```text
Liste tous les clients
```

```text
Donne-moi le client 1
```

```text
Quels sont les comptes du client 1 ?
```

```text
Donne-moi les informations du compte ACC-001
```

---

## Questions hors domaine

```text
Quelle est la capitale de la France ?
```

Le comportement attendu selon le prompt système est de ne pas inventer une réponse bancaire et de retourner :

```text
JE NE SAIS PAS
```

---

# Documentation OpenAPI

Le projet utilise SpringDoc.

Les services concernés peuvent exposer une documentation Swagger/OpenAPI.

Exemple :

```text
http://localhost:8056/swagger-ui.html
```

ou selon la version/configuration :

```text
http://localhost:8056/swagger-ui/index.html
```

Même principe pour :

```text
8057
8058
```

---

# H2 Console

Le `ebank-service` active la console H2 :

```properties
spring.h2.console.enabled=true
```

Elle peut être utilisée pour inspecter la base de données en développement.

La base utilisée est :

```text
jdbc:h2:mem:accounts-db
```

Il s'agit d'une base **en mémoire**.

Cela signifie que les données peuvent être perdues lorsque l'application est arrêtée.

---

# Flux complet d'une requête bancaire

Prenons :

```text
Quels sont les comptes du client 1 ?
```

Le flux peut être :

```text
Utilisateur
     │
     ▼
Angular / Discord / Telegram
     │
     ▼
EBank Bot
     │
     ▼
EbankAIAgent
     │
     ▼
Spring AI
     │
     ▼
OpenAI
     │
     ▼
MCP Tool
     │
     ▼
EBank Service
     │
     ▼
BankAccountRepository
     │
     ▼
H2
     │
     ▼
Liste des comptes
     │
     ▼
OpenAI
     │
     ▼
Réponse naturelle
```

---

# Flux de récupération d'un client

Lorsqu'un compte doit être enrichi avec les informations du client :

```text
EBank Service
      │
      ▼
CustomerRestClient
      │
      │ OpenFeign
      ▼
Eureka
      │
      ▼
Customer Service
      │
      ▼
CustomerRestController
      │
      ▼
CustomerService
      │
      ▼
CustomerRepository
      │
      ▼
H2
```

Puis la réponse revient vers `EBank Service`.

---

# Résilience

Si le `customer-service` devient indisponible :

```text
EBank Service
      │
      ▼
OpenFeign
      │
      ▼
Customer Service
      │
      X
   erreur
      │
      ▼
Circuit Breaker
      │
      ▼
Fallback
```

Le fallback retourne un client par défaut avec :

```text
Not available
```

Cela évite qu'une indisponibilité temporaire du service client entraîne nécessairement une erreur complète.

---

# Sécurité

Le projet utilise des variables d'environnement pour les informations sensibles.

Les secrets concernés sont notamment :

```text
OPENAI_KEY
DISCORD_TOKEN
TELEGRAM_TOKEN
```

Ils ne doivent pas être placés directement dans :

```text
application.properties
```

avec leur vraie valeur.

---

## Exemple correct

```properties
spring.ai.openai.api-key=${OPENAI_KEY}
```

## Exemple incorrect

```properties
spring.ai.openai.api-key=sk-xxxxxxxxxxxxxxxx
```

---

# .gitignore

Les fichiers contenant des secrets ou des fichiers générés doivent être exclus du dépôt.

Exemples :

```text
.env
*.env
target/
node_modules/
.idea/
```

---

# Problèmes connus

Cette section documente l'état actuel du dépôt afin que le README corresponde réellement au code.

## 1. Port Gateway

Le Gateway est actuellement configuré sur :

```text
9999
```

dans :

```text
gateway-service/src/main/resources/application.properties
```

avec :

```properties
server.port :9999
```

---

## 2. URLs Angular

Certains appels Angular utilisent actuellement :

```text
http://localhost:5555
```

par exemple :

```text
http://localhost:5555/EBANK-SERVICE/accounts
```

et :

```text
http://localhost:5555/EBANK-BOT/chat
```

Alors que le Gateway du dépôt est configuré sur :

```text
9999
```

Il faut donc harmoniser ces ports avant de considérer le frontend comme entièrement connecté à la configuration actuelle du repository.

---

## 3. Streaming du chatbot

Le frontend contient également un appel vers :

```text
/EBANK-BOT/chatStream
```

Cependant, le contrôleur actuellement présent expose principalement :

```text
/chat
```

Le projet contient bien une méthode `chatStream()` dans `EbankAIAgent`, mais le endpoint correspondant doit être exposé dans le contrôleur pour que le frontend puisse réellement l'utiliser.

---

## 4. Angular

Le dépôt contient encore certains éléments générés par défaut par Angular dans `app.html`.

L'interface métier est donc encore en cours de construction et peut être améliorée pour obtenir une expérience bancaire plus complète.

---

# Améliorations possibles

Le projet constitue une base fonctionnelle pour une application bancaire distribuée avec IA. Plusieurs extensions peuvent être ajoutées.

## Authentification

Ajouter :

```text
Spring Security
JWT
OAuth2
Keycloak
```

---

## Autorisation

Ajouter des rôles :

```text
ADMIN
CUSTOMER
BANK_EMPLOYEE
```

---

## Base de données persistante

Remplacer H2 par :

```text
PostgreSQL
```

ou :

```text
MySQL
```

---

## Transactions bancaires

Ajouter :

```text
Deposit
Withdrawal
Transfer
Transaction history
```

---

## Gestion des cartes

Ajouter :

```text
Bank Card
Credit Card
Debit Card
Card Blocking
Card Limits
```

---

## Notifications

Ajouter :

```text
Email
SMS
Push Notifications
```

---

## Observabilité

Ajouter :

```text
Prometheus
Grafana
Micrometer
Zipkin
OpenTelemetry
```

---

## Conteneurisation

Ajouter :

```text
Docker
Docker Compose
```

afin de lancer toute l'architecture avec une commande :

```bash
docker compose up
```

---

## CI/CD

Ajouter :

```text
GitHub Actions
```

pour automatiser :

```text
Build
Test
Code Quality
Docker Build
Deployment
```

---

## Amélioration de l'agent IA

Ajouter :

```text
RAG
Vector Database
Knowledge Base
Conversation History
User-specific memory
Guardrails
Tool permissions
```

---

## Sécurité de l'IA

Ajouter des mécanismes contre :

```text
Prompt Injection
Data Leakage
Tool Abuse
Unauthorized Tool Calling
Sensitive Information Disclosure
```

---

# Compétences développées

Ce projet permet de mettre en évidence plusieurs compétences techniques.

## Backend

- Java 21
- Spring Boot
- Spring Data JPA
- REST API
- Maven
- H2
- Lombok

## Microservices

- Spring Cloud
- Eureka
- Service Discovery
- API Gateway
- OpenFeign
- Circuit Breaker
- Resilience4j

## Intelligence artificielle

- Spring AI
- OpenAI
- LLM
- Tool Calling
- MCP
- Chat Memory
- Agentic workflows

## Frontend

- Angular
- TypeScript
- RxJS
- HttpClient
- Angular Signals
- Markdown

## Intégrations

- Discord Bot
- Telegram Bot
- Telegram Media
- REST
- MCP

## Architecture

- Microservices
- Separation of Concerns
- Service-to-Service Communication
- Fault Tolerance
- API Gateway Pattern
- Service Discovery Pattern

---

# Architecture logique

Le projet peut être résumé avec les responsabilités suivantes :

| Composant | Responsabilité |
|---|---|
| Discovery Service | Découverte des microservices |
| Gateway Service | Point d'entrée et routage |
| Customer Service | Gestion des clients |
| EBank Service | Gestion des comptes |
| EBank Bot | Intelligence artificielle |
| Angular Frontend | Interface web |
| Discord Bot | Interface conversationnelle |
| Telegram Bot | Interface conversationnelle et multimodale |
| OpenAI | Modèle de langage |
| MCP | Accès de l'IA aux outils métier |
| OpenFeign | Communication Customer ↔ EBank |
| Resilience4j | Tolérance aux pannes |
| H2 | Persistance de développement |

---

# Vue simplifiée des dépendances

```text
                    ┌──────────────┐
                    │   Angular    │
                    └──────┬───────┘
                           │
                           ▼
                    ┌──────────────┐
                    │   Gateway    │
                    │    :9999     │
                    └──────┬───────┘
                           │
                ┌──────────┴──────────┐
                │                     │
                ▼                     ▼
       ┌────────────────┐    ┌────────────────┐
       │    Customer    │    │     EBank      │
       │    Service     │◄───│    Service     │
       │     :8056      │    │     :8057      │
       └───────┬────────┘    └───────┬────────┘
               │                     │
               └──────────┬──────────┘
                          │
                          ▼
                    ┌───────────┐
                    │  Eureka   │
                    │   :8761   │
                    └───────────┘


          ┌───────────────┐
          │  Angular      │
          │  Discord      │
          │  Telegram     │
          └───────┬───────┘
                  │
                  ▼
          ┌────────────────┐
          │   EBank Bot    │
          │     :8058      │
          └───────┬────────┘
                  │
          ┌───────┴────────┐
          │                │
          ▼                ▼
      OpenAI              MCP
                           │
                  ┌────────┴────────┐
                  ▼                 ▼
             Customer MCP       EBank MCP
                  │                 │
                  ▼                 ▼
             Customer           EBank
              Service           Service
```

---

# Exemple de démonstration

Une démonstration complète peut suivre ce scénario.

## Étape 1 — Démarrage

Démarrer :

```text
Eureka
Customer Service
EBank Service
Gateway
EBank Bot
Angular
```

---

## Étape 2 — Vérification Eureka

Ouvrir :

```text
http://localhost:8761
```

Vérifier que les services sont enregistrés.

---

## Étape 3 — Tester les clients

```http
GET /customers
```

---

## Étape 4 — Tester les comptes

```http
GET /accounts
```

---

## Étape 5 — Tester la communication Feign

Demander un compte et vérifier que le compte peut être associé aux informations du client.

---

## Étape 6 — Tester l'IA

Demander :

```text
Liste tous les clients.
```

Le modèle doit utiliser les capacités métier disponibles.

---

## Étape 7 — Tester MCP

Demander :

```text
Quels sont les comptes du client 1 ?
```

Le bot doit être capable d'utiliser le service bancaire comme source de données.

---

## Étape 8 — Tester la mémoire

Envoyer plusieurs messages dans la même conversation et vérifier que le contexte est conservé.

---

## Étape 9 — Tester Discord

Envoyer une question au bot Discord.

---

## Étape 10 — Tester Telegram

Envoyer :

```text
Bonjour
```

puis une image accompagnée d'une question.

---

# Limites du projet

Cette application est principalement un projet académique et de démonstration technique.

Elle ne doit pas être considérée comme une plateforme bancaire destinée à gérer de véritables opérations financières.

Pour un environnement de production, il faudrait notamment ajouter :

- authentification forte ;
- autorisation ;
- chiffrement ;
- gestion des secrets ;
- audit ;
- journalisation sécurisée ;
- validation des opérations ;
- transactions distribuées ;
- base de données persistante ;
- monitoring ;
- tests de charge ;
- tests de sécurité ;
- protection des données personnelles ;
- haute disponibilité ;
- gestion des erreurs avancée ;
- politiques de sécurité pour l'agent IA.

---

# Perspectives

Le projet peut évoluer vers une plateforme bancaire intelligente complète.

Une architecture future pourrait être :

```text
                        ┌─────────────────┐
                        │     Clients     │
                        └────────┬────────┘
                                 │
                     ┌───────────┼───────────┐
                     ▼           ▼           ▼
                 Web App      Mobile      Chatbots
                     │           │           │
                     └───────────┼───────────┘
                                 ▼
                           API Gateway
                                 │
                ┌────────────────┼─────────────────┐
                ▼                ▼                 ▼
           Auth Service    Customer Service   Account Service
                │                │                 │
                ▼                ▼                 ▼
           Transaction      PostgreSQL        PostgreSQL
              Service
                │
                ▼
          Notification Service

                       ┌─────────────────┐
                       │   AI Agent      │
                       └────────┬────────┘
                                │
                    ┌───────────┼───────────┐
                    ▼           ▼           ▼
                   MCP         RAG       Memory
                    │           │           │
                    └───────────┼───────────┘
                                ▼
                              LLM
```

---

# Conclusion

**EBank** est une application qui combine plusieurs concepts modernes du développement logiciel.

Le projet ne se limite pas à une simple API bancaire. Il met en œuvre une architecture distribuée avec :

```text
Spring Boot
+
Spring Cloud
+
Eureka
+
API Gateway
+
OpenFeign
+
Resilience4j
+
Spring AI
+
OpenAI
+
MCP
+
Angular
+
Discord
+
Telegram
```

L'architecture permet de séparer les responsabilités métier tout en donnant à un assistant IA la possibilité d'interagir avec les services bancaires.

Le point central du projet est donc la combinaison entre :

```text
Microservices
      +
Service Discovery
      +
API Gateway
      +
Résilience
      +
Intelligence artificielle
      +
MCP
      +
Interfaces conversationnelles
```

Cette combinaison constitue une base intéressante pour évoluer vers une architecture bancaire intelligente plus complète.

---

# Auteur

**Chaimaa Lahlou**

Projet réalisé dans le cadre d'un projet de développement logiciel / systèmes distribués.

GitHub :

`https://github.com/lahlou4chaimaa`

Repository :

`https://github.com/lahlou4chaimaa/ebank-projet`

---

# Licence

Projet réalisé à des fins académiques et de démonstration.

Aucune utilisation en environnement bancaire réel n'est recommandée sans mise en place préalable des mécanismes de sécurité, de conformité, de persistance, de supervision et de haute disponibilité nécessaires.
