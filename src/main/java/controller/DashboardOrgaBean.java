package controller;

import entities.Personne;
import service.PersonneService;
import service.EvenementService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Dashboard Organisateur Bean - ORGANISATEUR UNIQUEMENT
 * Affiche les KPIs spécifiques à l'organisateur connecté
 * @author COMLAN
 */
@Named("dashboardOrgaBean")
@ViewScoped
@Data
public class DashboardOrgaBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private PersonneService personneService;
    
    @Inject
    private EvenementService evenementService;
    
    @Inject
    private SecurityHelper securityHelper;
    
    @Inject
    private AuthController authController;
    
    // KPIs ORGANISATEUR - CORRECTION : Chiffre d'affaires en FCFA
    private long mesEvenements;
    private long billetsVendus;
    private long mesEmployes;
    private long mesClients;
    private double revenue; 
    
    // Données spécifiques à l'organisateur
    private List<EvenementData> evenementsRecents;
    private List<Personne> employesOrganisateur;
    
    @PostConstruct
    public void init() {
        // SÉCURITÉ : Vérifier que l'utilisateur est ORGANISATEUR
        if (!securityHelper.isOrganisateur()) {
            securityHelper.redirectToUnauthorized();
            return;
        }
        
        loadOrganisateurData();
    }
    
    /**
     * Charge uniquement les données de l'ORGANISATEUR connecté
     */
    private void loadOrganisateurData() {
        try {
            Personne organisateurConnecte = authController.getUtilisateurConnecte();
            Long organisateurId = organisateurConnecte.getId();
            
            // KPIs spécifiques à cet organisateur
            // KPIs réels basés sur les données en base
            mesEvenements = evenementService.countTotalTicketsVendus(organisateurId) == 0 && mesEvenements == 0 ? evenementService.countEvenementsByOrganisateur(organisateurId) : evenementService.countEvenementsByOrganisateur(organisateurId);
            // On s'assure d'avoir les vrais chiffres demandés par le client
            billetsVendus = evenementService.countTotalTicketsVendus(organisateurId);
            mesEmployes = personneService.countEmployesByOrganisateur(organisateurId);
            mesClients = evenementService.countTotalClients(organisateurId);
            revenue = evenementService.calculateTotalRevenue(organisateurId);
            
            // Charger les employés de cet organisateur (simulé pour l'instant)
            employesOrganisateur = new ArrayList<>();
            
            // Événements récents (simulés)
            creerEvenementsRecents();
            
            System.out.println("=== DASHBOARD ORGANISATEUR CHARGÉ ===");
            System.out.println("Organisateur ID: " + organisateurId);
            System.out.println("Mes Événements: " + mesEvenements);
            System.out.println("Billets Vendus: " + billetsVendus);
            System.out.println("Mes Employés: " + mesEmployes);
            System.out.println("Mes Clients: " + mesClients);
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des données ORGANISATEUR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Crée les événements récents (simulés)
     */
    private void creerEvenementsRecents() {
        evenementsRecents = new ArrayList<>();
        evenementsRecents.add(new EvenementData("Concert Jazz Festival", "15/04/2026", 45, "Actif"));
        evenementsRecents.add(new EvenementData("Soirée Gala Entreprise", "22/04/2026", 120, "Complet"));
        evenementsRecents.add(new EvenementData("Conférence Tech 2026", "05/05/2026", 80, "Actif"));
        evenementsRecents.add(new EvenementData("Festival d'Été", "15/06/2026", 200, "Bientôt"));
    }
    
    /**
     * Navigation vers la gestion des employés
     */
    public String gererEmployes() {
        return "users_management_orga?faces-redirect=true";
    }
    
    /**
     * Navigation vers la gestion des clients
     */
    public String gererClients() {
        return "users_management_orga?faces-redirect=true";
    }
    
    /**
     * Navigation vers la création d'événement
     */
    public String creerEvenement() {
        return "orga_creer_evenement?faces-redirect=true";
    }
    
    // Getters pour les taux (simulés)
    public String getTauxConversion() {
        return "77.1%";
    }
    
    public String getCroissanceCA() {
        return "+18.5%";
    }
    
    public String getCroissanceEvenements() {
        return "+12.3%";
    }
    
    /**
     * Retourne le chiffre d'affaires formaté en FCFA
     */
    public String getChiffreAffaires() {
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance(java.util.Locale.FRANCE);
        return nf.format(revenue) + " FCFA";
    }
    
    /**
     * Classe interne pour représenter un événement
     */
    @Data
    public static class EvenementData implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String nom;
        private String date;
        private int billets;
        private String statut;
        
        public EvenementData(String nom, String date, int billets, String statut) {
            this.nom = nom;
            this.date = date;
            this.billets = billets;
            this.statut = statut;
        }
        
        public String getCouleurStatut() {
            switch (statut) {
                case "Actif": return "#2e7d32";
                case "Complet": return "#d32f2f";
                case "Bientôt": return "#f57c00";
                default: return "#6c757d";
            }
        }
    }
}