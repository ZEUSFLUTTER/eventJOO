package controller;

import entities.Organisateur;
import entities.Personne;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Controller pour la page d'accueil publique (index.xhtml)
 * Affiche la liste des événements disponibles
 * @author COMLAN
 */
@Named("indexController")
@ViewScoped
@Data
public class IndexController implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private service.EvenementService evenementService;
    
    @Inject
    private AuthController authController;

    private List<Evenement> evenements;
    private Evenement selectedEvent;
    private String rechercheTexte;
    private String filtreAccueil;
    
    @PostConstruct
    public void init() {
        filtreAccueil = "TOUS";
        chargerEvenements();
    }
    
    /**
     * Charge la liste des événements de la base de données
     */
    private void chargerEvenements() {
        Personne utilisateur = authController != null ? authController.getUtilisateurConnecte() : null;
        boolean organisateurConnecte = utilisateur instanceof Organisateur;
        boolean modeMesEvenements = "MES".equals(filtreAccueil) && organisateurConnecte;
        List<entities.Evenement> realEvents;

        if (modeMesEvenements) {
            realEvents = evenementService.getEvenementsByOrganisateur(utilisateur.getId());
            realEvents.removeIf(e -> !isPublicStatus(e.getStatut()));
        } else {
            realEvents = evenementService.getPublicEvents();
            if (organisateurConnecte) {
                Long organisateurId = utilisateur.getId();
                realEvents.sort(
                    Comparator
                        .comparing((entities.Evenement e) -> !organisateurId.equals(e.getOrganisateur().getId()))
                        .thenComparing(entities.Evenement::getDateEvenement, Comparator.nullsLast(Comparator.naturalOrder()))
                );
            }
        }

        evenements = new ArrayList<>();
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd\nMMM", java.util.Locale.FRENCH);
        
        for (entities.Evenement e : realEvents) {
            String prixStr = calculatePriceRange(e);
            String dateStr = e.getDateEvenement() != null ? sdf.format(e.getDateEvenement()).toUpperCase() : "--\n---";
            String icone = determineIcon(e);
            
            evenements.add(new Evenement(
                e.getTitre(),
                e.getLieu(),
                prixStr,
                e.getDescription(),
                dateStr,
                icone,
                e
            ));
        }
    }

    public void afficherTousLesEvenements() {
        filtreAccueil = "TOUS";
        chargerEvenements();
    }

    public void afficherMesEvenements() {
        if (isOrganisateurConnecte()) {
            filtreAccueil = "MES";
        } else {
            filtreAccueil = "TOUS";
        }
        chargerEvenements();
    }

    public boolean isFiltreTous() {
        return !"MES".equals(filtreAccueil);
    }

    public boolean isFiltreMes() {
        return "MES".equals(filtreAccueil);
    }

    public boolean isOrganisateurConnecte() {
        return authController != null && authController.getUtilisateurConnecte() instanceof Organisateur;
    }

    private boolean isPublicStatus(String statut) {
        if (statut == null) {
            return false;
        }
        String normalized = statut.trim().toLowerCase();
        return !normalized.equals("brouillon")
                && !normalized.equals("annulé")
                && !normalized.equals("annule")
                && !normalized.equals("draft")
                && !normalized.equals("cancelled");
    }

    private String calculatePriceRange(entities.Evenement e) {
        if (e.getCategoriesBillets() == null || e.getCategoriesBillets().isEmpty()) {
            return "Gratuit";
        }
        
        double min = Double.MAX_VALUE;
        double max = 0;
        
        for (entities.CategorieBillet cat : e.getCategoriesBillets()) {
            if (cat.getPrix() < min) min = cat.getPrix();
            if (cat.getPrix() > max) max = cat.getPrix();
        }
        
        if (min == 0 && max == 0) return "Gratuit";
        if (min == max) return String.format("%,.0f FCFA", min);
        return String.format("%,.0f - %,.0f FCFA", min, max);
    }

    private String determineIcon(entities.Evenement e) {
        String titre = e.getTitre().toLowerCase();
        if (titre.contains("concert") || titre.contains("music") || titre.contains("jazz")) return "pi pi-music";
        if (titre.contains("tech") || titre.contains("conf") || titre.contains("innovation")) return "pi pi-desktop";
        if (titre.contains("sport") || titre.contains("marathon") || titre.contains("course")) return "pi pi-flag";
        if (titre.contains("livre") || titre.contains("salon")) return "pi pi-book";
        if (titre.contains("gastro") || titre.contains("cuisine") || titre.contains("chef")) return "pi pi-star";
        return "pi pi-calendar"; // Default icon
    }
    
    /**
     * Méthode de recherche (simulée)
     */
    public void rechercher() {
        // Pour l'instant, on ne filtre pas vraiment
        // Dans une vraie application, on filtrerait selon rechercheTexte
        System.out.println("Recherche pour: " + rechercheTexte);
    }
    
    /**
     * Voir les détails d'un événement (simulé)
     */
    public void voirDetails(Evenement event) {
        // Pour l'instant, on affiche juste un message
        // Dans une vraie application, on redirigerait vers une page de détails
        System.out.println("Voir détails pour: " + event.getTitre());
    }
    
    /**
     * Classe interne pour représenter un événement
     */
    @Data
    public static class Evenement implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String titre;
        private String lieu;
        private String prix;
        private String description;
        private String date;
        private String icone;
        private entities.Evenement realEvenement;
        
        public Evenement(String titre, String lieu, String prix, String description, String date, String icone, entities.Evenement realEvenement) {
            this.titre = titre;
            this.lieu = lieu;
            this.prix = prix;
            this.description = description;
            this.date = date;
            this.icone = icone;
            this.realEvenement = realEvenement;
        }
    }
    public Evenement getSelectedEvent() { return selectedEvent; }
    public void setSelectedEvent(Evenement selectedEvent) { this.selectedEvent = selectedEvent; }
}
