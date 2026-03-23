# EventPlatform - Plateforme de Gestion d'Événements

## 📋 Description du Projet

EventPlatform est une application web de gestion d'événements développée avec **Jakarta EE 10** et **PrimeFaces 15**. Cette plateforme permet la gestion complète des utilisateurs, événements et billets avec une architecture multi-rôles (Gérant, Organisateur, Client, Employé).

## 🛠️ Prérequis Techniques

### Environnement de Développement
- **JDK** : OpenJDK 11+ (recommandé : JDK 17 ou 21)
- **IDE** : NetBeans 21+ ou IntelliJ IDEA Ultimate
- **Serveur d'Application** : GlassFish 7.x
- **Base de Données** : MySQL 8.0+
- **Build Tool** : Maven 3.8+

### Technologies Utilisées
- **Jakarta EE 10** (CDI, JPA, JSF, EJB)
- **PrimeFaces 15.0.13** (Jakarta)
- **Lombok 1.18.44** (Annotations)
- **EclipseLink** (JPA Provider)
- **Bootstrap 5** + **SweetAlert2** (UI/UX)

## 🗄️ Configuration de la Base de Données

### 1. Création de la Base de Données MySQL

```sql
-- Connexion en tant que root
mysql -u root -p

-- Création de la base de données
CREATE DATABASE event_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Création de l'utilisateur dédié
CREATE USER 'eventuser'@'localhost' IDENTIFIED BY 'eventpass123';
GRANT ALL PRIVILEGES ON event_platform.* TO 'eventuser'@'localhost';
FLUSH PRIVILEGES;

-- Vérification
USE event_platform;
SHOW TABLES;
```

### 2. Configuration du Pool de Connexions GlassFish

#### Étape 1 : Créer le Pool de Connexions JDBC
1. Accédez à la console d'administration GlassFish : `http://localhost:4848`
2. Naviguez vers **Resources > JDBC > JDBC Connection Pools**
3. Cliquez sur **New** et configurez :

```
Pool Name: EventPool
Resource Type: javax.sql.DataSource
Database Driver Vendor: MySQL
```

#### Étape 2 : Propriétés du Pool
Ajoutez les propriétés suivantes :

```
serverName: localhost
portNumber: 3306
databaseName: event_platform
user: eventuser
password: eventpass123
useSSL: false
allowPublicKeyRetrieval: true
```

#### Étape 3 : Créer la Ressource JNDI
1. Naviguez vers **Resources > JDBC > JDBC Resources**
2. Cliquez sur **New** et configurez :

```
JNDI Name: jdbc/jpa
Pool Name: EventPool
```

#### Étape 4 : Test de Connexion
- Retournez au pool **EventPool**
- Cliquez sur **Ping** pour tester la connexion

## 🚀 Instructions de Lancement

### 1. Clonage du Projet

```bash
# Cloner le repository
git clone [URL_DU_REPOSITORY]
cd eventProjectGlsi

# Vérifier la structure
ls -la
```

### 2. Configuration dans NetBeans

#### Import du Projet
1. Ouvrez NetBeans
2. **File > Open Project**
3. Sélectionnez le dossier `eventProjectGlsi`
4. Cliquez sur **Open Project**

#### Configuration du Serveur
1. Clic droit sur le projet > **Properties**
2. **Run** > Server : Sélectionnez **GlassFish Server**
3. **Apply** et **OK**

### 3. Build et Déploiement

```bash
# Via NetBeans (Recommandé)
# Clic droit sur le projet > Clean and Build
# Puis : Run Project (F6)

# Via Maven (Alternative)
mvn clean compile
mvn package
```

### 4. Déploiement Manuel (si nécessaire)

1. Copiez le fichier `target/eventProjectGlsi-1.0-SNAPSHOT.war`
2. Dans GlassFish Admin Console : **Applications > Deploy**
3. Sélectionnez le fichier WAR et déployez

### 5. Accès à l'Application

```
URL: http://localhost:8080/eventProjectGlsi-1.0-SNAPSHOT/
```

## 👤 Comptes par Défaut

### Gérant (Administrateur)
```
Email: admin@event.com
Mot de passe: admin123
```

### Comptes de Test
- **Organisateurs** : `orga1@event.com`, `orga2@event.com`, `orga3@event.com`
- **Clients** : `client1@event.com`, `client2@event.com`, etc.
- **Mot de passe universel** : `password123`

## 🏗️ Structure du Projet

```
src/main/java/
├── entities/           # Entités JPA (Personne, Client, Organisateur, etc.)
├── dao/               # Data Access Objects (PersonneDao, PersonneDaoImpl)
├── service/           # Services métier (PersonneService, DataInitializer)
├── controller/        # ManagedBeans JSF (AuthController, UserAdminController)
└── com.mycompany.eventprojectglsi/
    └── resources/     # Configuration JAX-RS

src/main/webapp/
├── WEB-INF/
│   ├── template.xhtml      # Template principal
│   ├── public_template.xhtml # Template public (login)
│   └── web.xml            # Configuration web
├── resources/css/         # Feuilles de style
├── dashboard_*.xhtml      # Pages de tableau de bord
├── users_management*.xhtml # Gestion des utilisateurs
└── *.xhtml               # Autres pages

src/main/resources/
└── META-INF/
    └── persistence.xml    # Configuration JPA
```

### Architecture en Couches

1. **Entities** : Modèles de données avec annotations JPA
2. **DAO** : Accès aux données avec interface et implémentation
3. **Service** : Logique métier et transactions
4. **Controller** : ManagedBeans pour l'interface utilisateur

## 👨‍💻 Guide de Développement

### Ajouter un Nouveau Module (ex: Gestion des Billets)

#### 1. Créer l'Entité

```java
@Entity
@Getter
@Setter
public class Billet extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String titre;
    
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    
    // Autres propriétés...
}
```

#### 2. Créer le DAO

```java
// Interface
public interface BilletDao {
    void save(Billet billet);
    List<Billet> findAll();
    Billet findById(Long id);
    void delete(Long id);
}

// Implémentation
@Stateless
public class BilletDaoImpl implements BilletDao {
    @PersistenceContext(unitName = "EventPU")
    private EntityManager em;
    
    // Implémentation des méthodes...
}
```

#### 3. Créer le Service

```java
@Stateless
public class BilletService {
    
    @Inject
    private BilletDao billetDao;
    
    public void creerBillet(Billet billet) {
        billetDao.save(billet);
    }
    
    // Autres méthodes métier...
}
```

#### 4. Créer le ManagedBean

```java
@Named("billetController")
@ViewScoped
@Getter
@Setter
public class BilletController implements Serializable {
    
    @Inject
    private BilletService billetService;
    
    private List<Billet> billets;
    private Billet nouveauBillet = new Billet();
    
    @PostConstruct
    public void init() {
        chargerBillets();
    }
    
    // Méthodes d'action...
}
```

#### 5. Créer la Page XHTML

```xhtml
<?xml version='1.0' encoding='UTF-8' ?>
<!DOCTYPE html>
<ui:composition xmlns="http://www.w3.org/1999/xhtml"
                xmlns:h="jakarta.faces.html"
                xmlns:p="http://primefaces.org/ui"
                xmlns:ui="jakarta.faces.facelets"
                template="/WEB-INF/template.xhtml">
    
    <ui:define name="title">Gestion des Billets</ui:define>
    
    <ui:define name="pageIcon">
        <i class="pi pi-ticket"></i>
    </ui:define>
    
    <ui:define name="pageTitle">Mes Billets</ui:define>
    
    <ui:define name="content">
        <!-- Votre contenu ici -->
    </ui:define>
</ui:composition>
```

### Bonnes Pratiques

1. **Utilisez toujours le template** : `template="/WEB-INF/template.xhtml"`
2. **Sécurité** : Vérifiez les rôles avec `SecurityHelper`
3. **Transactions** : Utilisez `@Transactional` dans les services
4. **Validation** : Annotations Bean Validation sur les entités
5. **Logs** : `System.out.println()` pour le debug

## 🐛 Gestion des Erreurs Fréquentes

### ViewExpiredException

**Symptôme** : Session expirée, page blanche

**Solution** :
```bash
# Nettoyer le projet
rm -rf target/
mvn clean compile

# Dans NetBeans
# Clic droit > Clean and Build
```

### Erreurs de Déploiement

**Symptôme** : Erreur au déploiement sur GlassFish

**Solutions** :
1. Vérifiez que GlassFish est démarré
2. Undeploy l'ancienne version
3. Redémarrez GlassFish si nécessaire

```bash
# Redémarrer GlassFish
asadmin stop-domain domain1
asadmin start-domain domain1
```

### Problèmes de Base de Données

**Symptôme** : Erreur de connexion JDBC

**Solutions** :
1. Vérifiez que MySQL est démarré
2. Testez la connexion dans GlassFish Admin Console
3. Vérifiez les credentials dans le pool JDBC

### Erreurs de Compilation Lombok

**Symptôme** : Getters/Setters non reconnus

**Solution** :
```bash
# Installer Lombok dans l'IDE
# NetBeans : Tools > Plugins > Available Plugins > Lombok
# Redémarrer l'IDE
```

## 📚 Ressources Utiles

- [Documentation Jakarta EE 10](https://jakarta.ee/specifications/platform/10/)
- [PrimeFaces Showcase](https://www.primefaces.org/showcase/)
- [GlassFish Documentation](https://eclipse-ee4j.github.io/glassfish/)
- [MySQL Documentation](https://dev.mysql.com/doc/)

## 🤝 Contribution

1. Créez une branche pour votre fonctionnalité
2. Respectez l'architecture existante
3. Testez vos modifications
4. Documentez les nouvelles fonctionnalités

## 📄 Licence

Ce projet est développé dans le cadre académique - GLSI.

---

**Développé par** : GOUPE1

**Version** : 1.0-SNAPSHOT  
**Dernière mise à jour** : Mars 2026
