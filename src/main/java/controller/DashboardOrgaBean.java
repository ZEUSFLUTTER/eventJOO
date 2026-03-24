package controller;

import entities.Personne;
import entities.Evenement;
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
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.optionconfig.title.Title;

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
    
    // KPIs ORGANISATEUR
    private long mesEvenements;
    private long billetsVendus;
    private long mesEmployes;
    private long mesClients;
    private double revenue; 
    
    // Données spécifiques à l'organisateur
    private List<EvenementData> evenementsRecents;
    private List<Personne> employesOrganisateur;
    private BarChartModel barModel;
    
    @PostConstruct
    public void init() {
        barModel = new BarChartModel(); // Initialisation par défaut
        System.out.println(">>> DashboardOrgaBean.init() appelé");
        // SÉCURITÉ : Vérifier que l'utilisateur est ORGANISATEUR
        if (!securityHelper.isOrganisateur()) {
            System.out.println(">>> ACCÈS REFUSÉ: Pas un organisateur");
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
            System.out.println(">>> Chargement des données Organisateur...");
            Personne organisateurConnecte = authController.getUtilisateurConnecte();
            if (organisateurConnecte == null) {
                System.out.println(">>> ERREUR: Utilisateur connecté est NULL");
                return;
            }
            Long organisateurId = organisateurConnecte.getId();
            System.out.println(">>> Organisateur ID: " + organisateurId);
            
            // KPIs réels
            mesEvenements = evenementService.countEvenementsByOrganisateur(organisateurId);
            billetsVendus = evenementService.countTotalTicketsVendus(organisateurId);
            mesEmployes = personneService.countEmployesByOrganisateur(organisateurId);
            mesClients = evenementService.countTotalClients(organisateurId);
            revenue = evenementService.calculateTotalRevenue(organisateurId);
            
            // Événements récents (simulés ou réels)
            creerEvenementsRecents();
            
            // Initialiser le graphique
            initBarModel(organisateurId);
            
            System.out.println("=== DASHBOARD ORGANISATEUR CHARGÉ ===");
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des données ORGANISATEUR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initBarModel(Long organisateurId) {
        barModel = new BarChartModel();
        ChartData data = new ChartData();

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Billets Vendus");

        List<Evenement> evts = evenementService.getEvenementsByOrganisateur(organisateurId);
        List<Object> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> bgColor = new ArrayList<>();
        List<String> borderColor = new ArrayList<>();

        for (Evenement ev : evts) {
            labels.add(ev.getTitre());
            long sold = ev.getCategoriesBillets().stream()
                    .mapToLong(cat -> cat.getQuantiteTotale() - cat.getQuantiteDisponible())
                    .sum();
            values.add(sold);
            
            // Palette premium (bleu)
            bgColor.add("rgba(59, 130, 246, 0.2)");
            borderColor.add("rgb(59, 130, 246)");
        }

        barDataSet.setData(values);
        barDataSet.setBackgroundColor(bgColor);
        barDataSet.setBorderColor(borderColor);
        barDataSet.setBorderWidth(1);

        data.addChartDataSet(barDataSet);
        data.setLabels(labels);
        barModel.setData(data);

        // Options
        BarChartOptions options = new BarChartOptions();
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Ventes par Événement");
        title.setFontSize(16);
        options.setTitle(title);

        barModel.setOptions(options);
    }
    
    /**
     * Crée les événements récents (simulés)
     */
    private void creerEvenementsRecents() {
        evenementsRecents = new ArrayList<>();
        evenementsRecents.add(new EvenementData("Concert Jazz", "15/04/2026", 45, "Actif"));
        evenementsRecents.add(new EvenementData("Gala", "22/04/2026", 120, "Complet"));
    }
    
    public String gererEmployes() {
        return "users_management_orga?faces-redirect=true";
    }
    
    public String gererClients() {
        return "users_management_orga?faces-redirect=true";
    }
    
    public String creerEvenement() {
        return "orga_creer_evenement?faces-redirect=true";
    }
    
    public String getChiffreAffaires() {
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance(java.util.Locale.FRANCE);
        return nf.format(revenue) + " FCFA";
    }
    
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