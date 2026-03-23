package controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
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

    private List<Evenement> evenements;
    private String rechercheTexte;
    
    @PostConstruct
    public void init() {
        chargerEvenements();
    }
    
    /**
     * Charge la liste des événements de la base de données
     */
    private void chargerEvenements() {
        List<entities.Evenement> realEvents = evenementService.getAllEvents();
        evenements = new ArrayList<>();
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd\nMMM", java.util.Locale.FRENCH);
        
        for (entities.Evenement e : realEvents) {
            String prixStr = calculatePriceRange(e);
            String dateStr = sdf.format(e.getDateEvenement()).toUpperCase();
            String icone = determineIcon(e);
            
            evenements.add(new Evenement(
                e.getTitre(),
                e.getLieu(),
                prixStr,
                e.getDescription(),
                dateStr,
                icone
            ));
        }
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
        if (min == max) return String.format("%.0f€", min);
        return String.format("%.0f€ - %.0f€", min, max);
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
        
        public Evenement(String titre, String lieu, String prix, String description, String date, String icone) {
            this.titre = titre;
            this.lieu = lieu;
            this.prix = prix;
            this.description = description;
            this.date = date;
            this.icone = icone;
        }
    }
}