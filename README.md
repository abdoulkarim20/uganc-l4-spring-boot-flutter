# GARAGIX

GARAGIX est une application de gestion de garage développée avec Spring Boot.  
Elle permet de gérer les clients, les véhicules, les mécaniciens, les réparations et les utilisateurs à travers une interface web moderne, simple et responsive.

## Fonctionnalités principales

- Gestion des clients
- Gestion des véhicules
- Gestion des mécaniciens
- Gestion des réparations
- Gestion des utilisateurs
- Authentification sécurisée
- Tableau de bord d’administration
- Espace client
- Espace mécanicien
- Landing page de présentation

## Technologies utilisées

### Backend
- Java 17
- Spring Boot
- Spring Web MVC
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT pour l’authentification API
- Lombok

### Frontend
- HTML5
- CSS3
- JavaScript Vanilla
- Bootstrap Icons

### Outils
- Maven
- Postman
- Swagger / OpenAPI

## Architecture fonctionnelle

L’application est organisée autour de plusieurs modules métier :

- Clients
- Véhicules
- Mécaniciens
- Réparations
- Utilisateurs
- Authentification
- Espaces utilisateurs selon le profil connecté

## Gestion des rôles

L’application prend en charge les rôles suivants :

- ADMIN
- ADMIN_GARAGE
- MECANICIEN
- CLIENT

> Actuellement, l’accès principal au dashboard d’administration est prévu pour le profil ADMIN.

## Sécurité

La sécurité repose sur :

- Spring Security
- Authentification par JWT
- Protection des routes selon les rôles
- Gestion du changement de mot de passe
- Gestion centralisée des erreurs métier et techniques

## Base de données

L’application utilise PostgreSQL pour stocker les données métier :

- utilisateurs
- clients
- véhicules
- mécaniciens
- réparations

## Lancement du projet

### Prérequis

- Java 17
- Maven
- PostgreSQL

### Configuration

Mettre à jour le fichier `application.properties` avec vos paramètres :

- URL de la base de données
- nom d’utilisateur PostgreSQL
- mot de passe PostgreSQL
- paramètres JWT si nécessaire
### Accès à l’application
- Landing page : http://localhost:8383
- Page de connexion : http://localhost:8383/login
- Dashboard admin : http://localhost:8383/dashboard
### Documentation API
- La documentation des API REST est disponible via Swagger et OpenAPI.
- URL d’accès à Swagger UI
- http://localhost:8383/swagger-ui/index.html
- URL d’accès à la documentation OpenAPI JSON
- http://localhost:8383/v3/api-docs

### Démarrage

Avec Maven :

```bash
mvn spring-boot:run
