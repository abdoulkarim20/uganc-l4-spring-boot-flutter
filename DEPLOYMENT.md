# Deploiement GARAGIX V1

## Prerequis

- Java 17 ou Docker
- PostgreSQL accessible depuis le serveur
- Variables d'environnement de production configurees

## Variables de production

Copier `.env.example`, puis renseigner les vraies valeurs cote serveur.

Variables importantes :

- `SPRING_PROFILES_ACTIVE=prod`
- `DATABASE_URL`
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION_SECONDS=600`
- `BOOTSTRAP_ADMIN_ENABLED=false`

## Deploiement Docker

```bash
docker build -t garagix:1.0.0 .
docker run -d --name garagix --env-file .env -p 8383:8383 garagix:1.0.0
```

## Acces

- Application : `http://SERVEUR:8383`
- Swagger : `http://SERVEUR:8383/swagger-ui/index.html`
- API docs : `http://SERVEUR:8383/v3/api-docs`

## Point securite V1

Ne pas publier `application.properties` avec les vrais acces de production.
En production, utiliser uniquement le profil `prod` et les variables d'environnement.
