# GARAGIX API - Postman

Base URL:

```text
http://localhost:8383
```

## Authentification mobile/Postman

L'application web, Postman et le mobile utilisent le même principe côté API:

```text
Authorization: Bearer <accessToken>
```

Le login web appelle aussi `/api/auth/login`, stocke le token dans le navigateur, puis l'envoie sur les appels API.

## Rôles

Un utilisateur possède un seul rôle.

```text
ADMIN        : admin plateforme, accès global et gestion des utilisateurs
ADMIN_GARAGE : admin d'une structure garage, gestion des clients/véhicules/mécaniciens/réparations
MECANICIEN   : rôle métier mécanicien
CLIENT       : rôle client mobile/futur espace client
```

### Login

`POST /api/auth/login`

Body JSON:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

Réponse:

```json
{
  "accessToken": "...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "username": "admin",
  "roles": ["ROLE_ADMIN"]
}
```

Dans Postman, utiliser ensuite:

```text
Authorization: Bearer <accessToken>
```

### Utilisateur connecté

`GET /api/auth/me`

## Endpoints métier

Tous ces endpoints nécessitent le Bearer token.

### Clients

```text
GET    /api/clients
GET    /api/clients/{id}
POST   /api/clients
PUT    /api/clients/{id}
DELETE /api/clients/{id}
```

Exemple `POST /api/clients`:

```json
{
  "nom": "DIALLO",
  "prenom": "Abdoul Karim",
  "telephone": "664346363",
  "adresse": "Coleah"
}
```

### Véhicules

```text
GET    /api/vehicules
GET    /api/vehicules/{id}
POST   /api/vehicules
PUT    /api/vehicules/{id}
DELETE /api/vehicules/{id}
```

Exemple:

```json
{
  "immatriculation": "RC-1234-AC",
  "marque": "Toyota",
  "modele": "Corolla",
  "annee": 2020,
  "clientId": 1
}
```

### Mécaniciens

```text
GET    /api/mecaniciens
GET    /api/mecaniciens/{id}
POST   /api/mecaniciens
PUT    /api/mecaniciens/{id}
DELETE /api/mecaniciens/{id}
```

Exemple:

```json
{
  "nom": "CAMARA",
  "prenom": "Ibrahima",
  "telephone": "620000001",
  "specialite": "Moteur diesel"
}
```

### Réparations

```text
GET    /api/reparations
GET    /api/reparations/{id}
POST   /api/reparations
PUT    /api/reparations/{id}
DELETE /api/reparations/{id}
```

Statuts acceptés par la base:

```text
planifiee
en_cours
terminee
annulee
```

Exemple:

```json
{
  "dateReparation": "2026-06-15",
  "description": "Diagnostic moteur",
  "cout": 250000,
  "statut": "en_cours",
  "vehiculeId": 1,
  "mecanicienId": 1
}
```

### Utilisateurs

Réservé au rôle `ADMIN`.

```text
GET    /api/utilisateurs
GET    /api/utilisateurs/{id}
POST   /api/utilisateurs
PUT    /api/utilisateurs/{id}
DELETE /api/utilisateurs/{id}
```

Exemple:

```json
{
  "nom": "BAH",
  "prenom": "Admin Garage",
  "telephone": "620000002",
  "username": "garage-admin",
  "password": "admin123",
  "role": "ADMIN_GARAGE"
}
```
