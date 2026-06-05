# Projet Mesure Qualité et Performance Logicielle
**Auteur : Séga Diallo | L3 Génie Logiciel | Module : Qualité Continue**

---

## Structure du projet

```
stock-quality-project/
├── src/
│   ├── main/java/com/sega/stock/
│   │   ├── StockApplication.java          # Point d'entrée Spring Boot
│   │   ├── StockController.java           # ⚠️ Version AVANT (avec défauts)
│   │   └── StockControllerOptimized.java  # ✅ Version APRÈS (optimisée)
│   └── test/java/com/sega/stock/
│       └── StockControllerTest.java       # Tests unitaires
├── k6/
│   └── load-test.js                       # Script test de charge (50 users)
├── .github/workflows/
│   └── ci-cd.yml                          # Pipeline GitHub Actions
├── docker-compose.yml                     # SonarQube via Docker
├── sonar-project.properties               # Config SonarQube
└── pom.xml                                # Maven + JaCoCo + Sonar
```

---

## ÉTAPE 1 — Lancer SonarQube

```bash
# Démarrer SonarQube
docker-compose up -d

# Attendre ~1 minute puis ouvrir :
# http://localhost:9000
# Login : admin / admin (puis changer le mot de passe)
```

### Configurer la Quality Gate dans SonarQube :
1. Aller dans **Quality Gates** > **Create**
2. Nom : `QG-Sega-Diallo`
3. Ajouter condition : **Cyclomatic Complexity > 10** → FAILED
4. Ajouter condition : **Technical Debt Ratio > 5%** → FAILED
5. Cliquer **Set as Default**

---

## ÉTAPE 2 — Scan initial (AVANT corrections)

```bash
# Compiler le projet
mvn clean package -DskipTests

# Créer un token dans SonarQube : My Account > Security > Generate Token
# Puis lancer le scan :
mvn sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=TON_TOKEN_ICI

# → La Quality Gate doit ÉCHOUER (capture d'écran requise !)
```

---

## ÉTAPE 3 — Tests unitaires + couverture

```bash
mvn clean verify
# Le rapport JaCoCo sera dans : target/site/jacoco/index.html
```

---

## ÉTAPE 4 — Test de charge K6

```bash
# Installer K6 sur Ubuntu/Debian :
sudo apt-get install k6

# Ou sur macOS :
brew install k6

# Démarrer l'application
mvn spring-boot:run &

# Lancer le test de charge (50 utilisateurs)
mkdir -p k6/results
k6 run k6/load-test.js

# Le rapport HTML sera dans : k6/results/summary.html
```

---

## ÉTAPE 5 — Appliquer les corrections

Remplacer le contenu de `StockController.java` par celui de `StockControllerOptimized.java`.

**Résumé des corrections :**

| Défaut | Avant | Après |
|--------|-------|-------|
| Complexité Cyclomatique | CC = 5 (if imbriqués) | CC = 2 (méthode privée) |
| Mémoire processData() | 1 000 000 éléments chargés | Max 1 000 éléments (pagination) |
| Latence p95 | ~15 000 ms | < 200 ms |

---

## ÉTAPE 6 — Relancer le scan APRÈS corrections

```bash
mvn clean verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=TON_TOKEN_ICI

# → La Quality Gate doit PASSER cette fois (capture d'écran requise !)
```

---

## ÉTAPE 7 — Configurer GitHub Actions

1. Pousser le projet sur GitHub
2. Aller dans **Settings > Secrets > Actions**
3. Ajouter :
   - `SONAR_TOKEN` → ton token SonarQube
   - `SONAR_HOST_URL` → `http://ton-ip:9000` (ou SonarCloud URL)
4. Faire un `git push` → le pipeline se déclenche automatiquement

---

## Captures d'écran requises pour le rapport

- [ ] Quality Gate ÉCHOUÉ (avant corrections)
- [ ] Quality Gate RÉUSSI (après corrections)
- [ ] Rapport JaCoCo (couverture de tests)
- [ ] Graphe K6 (latence + taux d'erreur)
- [ ] Pipeline GitHub Actions passant de Failed → Success
