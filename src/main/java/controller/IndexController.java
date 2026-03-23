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
    
    private List<Evenement> evenements;
    private String rechercheTexte;
    
    @PostConstruct
    public void init() {
        chargerEvenements();
    }
    
    /**
     * Charge la liste des événements (simulée)
     */
    private void chargerEvenements() {
        evenements = new ArrayList<>();
        
        evenements.add(new Evenement(
            "Festival Jazz 2026",
            "Paris, France",
            "15€ - 45€",
            "Un festival de jazz exceptionnel avec les plus grands artistes internationaux.",
            "15\nMAR",
            "pi pi-music"
        ));
        
        evenements.add(new Evenement(
            "Conférence Tech Innovation",
            "Lyon, France",
            "Gratuit",
            "Découvrez les dernières innovations technologiques avec des experts du secteur.",
            "22\nMAR",
            "pi pi-desktop"
        ));
        
        evenements.add(new Evenement(
            "Marathon de Paris",
            "Paris, France",
            "25€",
            "Participez au plus grand marathon de France dans les rues de la capitale.",
            "05\nAVR",
            "pi pi-flag"
        ));
        
        evenements.add(new Evenement(
            "Salon du Livre",
            "Bordeaux, France",
            "8€ - 12€",
            "Rencontrez vos auteurs préférés et découvrez les dernières nouveautés littéraires.",
            "12\nAVR",
            "pi pi-book"
        ));
        
        evenements.add(new Evenement(
            "Festival Gastronomique",
            "Nice, France",
            "20€ - 35€",
            "Dégustez les spécialités culinaires de la région avec les meilleurs chefs.",
            "18\nAVR",
            "pi pi-star"
        ));
        
        evenements.add(new Evenement(
            "Concert Rock Summer",
            "Marseille, France",
            "30€ - 80€",
            "Une soirée rock inoubliable avec les groupes les plus populaires du moment.",
            "25\nAVR",
            "pi pi-volume-up"
        ));
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