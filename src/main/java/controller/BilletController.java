package controller;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller pour la gestion des billets (données simulées pour l'instant)
 * @author COMLAN
 */
@Named("billetController")
@RequestScoped
@Data
public class BilletController implements Serializable {
    
    private List<BilletDTO> billets;
    
    @Inject
    private AuthController authController;
    
    @PostConstruct
    public void init() {
        chargerBillets();
    }
    
    /**
     * Charge les billets du client connecté (données simulées)
     */
    public void chargerBillets() {
        billets = new ArrayList<>();
        
        if (authController.getUtilisateurConnecte() != null) {
            // Données simulées pour la démonstration
            billets.add(new BilletDTO(
                1L, 
                "Concert Jazz Festival 2026", 
                "Paris - Zenith", 
                LocalDateTime.of(2026, 6, 15, 20, 0),
                "VIP",
                "B-001234",
                "Confirmé",
                89.99
            ));
            
            billets.add(new BilletDTO(
                2L, 
                "Conférence Tech Summit", 
                "Lyon - Centre des Congrès", 
                LocalDateTime.of(2026, 4, 22, 9, 0),
                "Standard",
                "B-001235",
                "Confirmé",
                45.00
            ));
            
            billets.add(new BilletDTO(
                3L, 
                "Festival Électro Night", 
                "Marseille - Stade Vélodrome", 
                LocalDateTime.of(2026, 7, 10, 18, 0),
                "Premium",
                "B-001236",
                "En attente",
                120.00
            ));
            
            billets.add(new BilletDTO(
                4L, 
                "Théâtre - Le Malade Imaginaire", 
                "Bordeaux - Grand Théâtre", 
                LocalDateTime.of(2026, 5, 5, 19, 30),
                "Balcon",
                "B-001237",
                "Confirmé",
                35.50
            ));
        }
    }
    
    /**
     * Retourne les billets
     */
    public List<BilletDTO> getBillets() {
        if (billets == null) {
            chargerBillets();
        }
        return billets;
    }
    
    /**
     * Action pour voir les détails d'un billet
     */
    public void voirBillet(BilletDTO billet) {
        if (billet != null) {
            addMessage("Information", 
                "Affichage des détails du billet " + billet.getNumeroBillet() + 
                " pour l'événement : " + billet.getNomEvenement(), 
                FacesMessage.SEVERITY_INFO);
        }
    }
    
    /**
     * Action pour télécharger un billet
     */
    public void telechargerBillet(BilletDTO billet) {
        if (billet != null) {
            addMessage("Téléchargement", 
                "Téléchargement du billet " + billet.getNumeroBillet() + " en cours...", 
                FacesMessage.SEVERITY_INFO);
            
            // TODO: Implémenter la génération et le téléchargement du PDF
            System.out.println("Téléchargement du billet: " + billet.getNumeroBillet());
        }
    }
    
    /**
     * Ajoute un message FacesMessage
     */
    private void addMessage(String summary, String detail, FacesMessage.Severity severity) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }
    
    /**
     * Classe DTO pour représenter un billet (en attendant l'entité réelle)
     */
    @Data
    public static class BilletDTO implements Serializable {
        private Long id;
        private String nomEvenement;
        private String lieu;
        private LocalDateTime dateEvenement;
        private String categorie;
        private String numeroBillet;
        private String statut;
        private Double prix;
        
        public BilletDTO(Long id, String nomEvenement, String lieu, LocalDateTime dateEvenement, 
                        String categorie, String numeroBillet, String statut, Double prix) {
            this.id = id;
            this.nomEvenement = nomEvenement;
            this.lieu = lieu;
            this.dateEvenement = dateEvenement;
            this.categorie = categorie;
            this.numeroBillet = numeroBillet;
            this.statut = statut;
            this.prix = prix;
        }
        
        public String getDateFormatee() {
            return dateEvenement.getDayOfMonth() + "/" + 
                   dateEvenement.getMonthValue() + "/" + 
                   dateEvenement.getYear() + " à " + 
                   String.format("%02d:%02d", dateEvenement.getHour(), dateEvenement.getMinute());
        }
    }
}
