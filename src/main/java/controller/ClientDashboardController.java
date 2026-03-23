package controller;

import entities.Personne;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller pour le dashboard client
 * Affiche les billets et événements du client
 * @author COMLAN
 */
@Named("clientDashboardController")
@ViewScoped
@Data
public class ClientDashboardController implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private AuthController authController;
    
    private List<Billet> mesBillets;
    private int totalBillets;
    private int billetsUtilises;
    private int billetsEnAttente;
    
    @PostConstruct
    public void init() {
        // Sécurité RBAC - Seuls les CLIENT peuvent accéder à cette page
        if (!SecurityHelper.checkRole(authController, Personne.Role.CLIENT)) {
            return;
        }
        
        chargerMesBillets();
        calculerStatistiques();
    }
    
    /**
     * Charge les billets du client (simulé)
     */
    private void chargerMesBillets() {
        mesBillets = new ArrayList<>();
        
        mesBillets.add(new Billet(
            "Festival Jazz 2026",
            "15 Mars 2026",
            "Paris, France",
            "45€",
            "Confirmé",
            "success"
        ));
        
        mesBillets.add(new Billet(
            "Conférence Tech Innovation",
            "22 Mars 2026",
            "Lyon, France",
            "Gratuit",
            "Confirmé",
            "success"
        ));
        
        mesBillets.add(new Billet(
            "Concert Rock Summer",
            "25 Avril 2026",
            "Marseille, France",
            "65€",
            "En attente",
            "warning"
        ));
    }
    
    /**
     * Calcule les statistiques des billets
     */
    private void calculerStatistiques() {
        totalBillets = mesBillets.size();
        billetsUtilises = 0; // Simulé
        billetsEnAttente = (int) mesBillets.stream()
            .filter(b -> "En attente".equals(b.getStatut()))
            .count();
    }
    
    /**
     * Classe interne pour représenter un billet
     */
    @Data
    public static class Billet implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String evenement;
        private String date;
        private String lieu;
        private String prix;
        private String statut;
        private String statutType; // success, warning, error
        
        public Billet(String evenement, String date, String lieu, String prix, String statut, String statutType) {
            this.evenement = evenement;
            this.date = date;
            this.lieu = lieu;
            this.prix = prix;
            this.statut = statut;
            this.statutType = statutType;
        }
    }
}