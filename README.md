# Distributed Systems Project

Application distribuée basée sur une architecture micro-services avec Spring Boot et Spring Cloud. La première met en place les services métier (clients et comptes bancaires), le service de découverte et la gateway. La seconde ajoute un agent conversationnel basé sur Spring AI, qui exploite les fonctionnalités des micro-services via le protocole MCP et qui est accessible depuis un client REST, Telegram et Discord.

## Structure du dépôt

```
Distributed-Systems-Project
├── discovery-service    Serveur Eureka
├── customer-service     Gestion des clients
├── ebank-sevice         Gestion des comptes bancaires
├── gateway-service      Spring Cloud Gateway
├── ebank-bot            Agent IA (Spring AI) et serveur/client MCP
└── angular-front        Interface web
```

## Architecture

![Architecture globale](screenshots/architecture.png)

---

# Partie 1 : Micro-services

## Discovery Service

Serveur Eureka démarré sur le port `<8761>` avec l'annotation `@EnableEurekaServer`. Tous les autres services s'y enregistrent.

```yaml
server:
  port: <8761>
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

![Dashboard Eureka](screenshots/eureka-dashboard.png)

## Customer-Service

Service de gestion des clients (port `<8056>`), avec Spring Data JPA et une base `<H2>`.

| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/customers` | Liste des clients |
| GET | `/customers/{id}` | Consultation d'un client |
| POST | `/customers` | Ajout d'un client |

![Structure de customer-service](screenshots/customer-structure.png)

![Base de données de customer-service](screenshots/customer-db.png)

## EBank-Service

Service de gestion des comptes bancaires (port `<8057>`). Chaque compte est rattaché à un client par `customerId`.

| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/accounts` | Liste des comptes |
| GET | `/accounts/{id}` | Consultation d'un compte avec son client |
| POST | `/accounts` | Création d'un compte |

![Structure de ebank-service](screenshots/ebank-structure.png)

![Base de données de ebank-service](screenshots/ebank-db.png)

## Communication avec OpenFeign

EBank-Service appelle Customer-Service par son nom Eureka grâce à un client Feign déclaré sous forme d'interface.

```java
@FeignClient(name = "customer-service")
public interface CustomerRestClient {
    @GetMapping("/customers/{id}")
    Customer getCustomerById(@PathVariable Long id);
}
```

![Résultat de l'appel Feign](screenshots/feign-result.png)

## Tolérance aux pannes avec Resilience4J

L'appel au service client est protégé par un circuit breaker. Lorsque Customer-Service est indisponible, une méthode de repli renvoie une réponse par défaut.

```java
    @CircuitBreaker(name = "customerService", fallbackMethod = "getDefaultCustomer")
```

Cas normal :

![Circuit breaker normal](screenshots/cb-normal.png)

Customer-Service arrêté, la méthode de repli répond :

![Circuit breaker fallback](screenshots/cb-fallback.png)

## Gateway

Spring Cloud Gateway sur le port `<9999>`. Elle sert de point d'entrée unique et propose deux modes de routage : statique et dynamique.

### Routage statique

Les routes sont déclarées dans application.yml. Chaque route associe un chemin à un service enregistré dans Eureka (`lb://` délègue le choix de l'instance au load balancer).

```yaml
spring:
  cloud:
    gateway:
      server:
        webflux:
          routes:
            - id : r1
              uri : http://localhost:8056
              predicates:
                - Path= /customers/**
            - id: r2
              uri: http://localhost:8057
              predicates:
                - Path= /accounts/**
```

### Routage dynamique

Avec le locator Eureka, la gateway crée automatiquement une route pour chaque service enregistré, sans déclaration manuelle. Le nom du service sert de préfixe dans l'URL.

Exemple : `http://localhost:9999/CUSTOMER-SERVICE/customers`

![Routage dynamique](screenshots/gateway-dynamic.png)
## Tests des services backend

### Postman

![Postman GET](screenshots/postman-get.png)

![Postman POST](screenshots/postman-post.png)


### Swagger-UI

La documentation est générée avec Springdoc OpenAPI et accessible sur `/swagger-ui.html` pour chaque service.

![Swagger Customer-Service](screenshots/swagger-customer.png)

![Swagger EBank-Service](screenshots/swagger-ebank.png)