package controller;

import entities.CategorieBillet;
import entities.Evenement;
import entities.Organisateur;
import entities.Personne;
import service.EvenementService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.List;

/**
 * Controller pour la gestion des événements par l'organisateur.
 */
@Named("orgaEvenementController")
@ViewScoped
@Getter
@Setter
public class OrgaEvenementController implements Serializable {

    @Inject
    private EvenementService evenementService;

    @Inject
    private AuthController authController;

    private List<Evenement> mesEvenements;
    private Evenement nouvelEvenement;
    private CategorieBillet nouvelleCategorie;

    @PostConstruct
    public void init() {
        nouvelEvenement = new Evenement();
        nouvelleCategorie = new CategorieBillet();
        chargerMesEvenements();
    }

    /**
     * Charge la liste des événements de l'organisateur connecté.
     */
    public void chargerMesEvenements() {
        Personne utilisateur = authController.getUtilisateurConnecte();
        if (utilisateur instanceof Organisateur) {
            mesEvenements = evenementService.getEvenementsByOrganisateur(utilisateur.getId());
        }
    }

    /**
     * Ajoute une nouvelle catégorie de billet à l'événement en cours de création.
     */
    public void ajouterCategorie() {
        if (nouvelleCategorie.getNom() != null && !nouvelleCategorie.getNom().isEmpty() &&
            nouvelleCategorie.getPrix() != null && nouvelleCategorie.getQuantiteTotale() != null) {
            
            nouvelleCategorie.setQuantiteDisponible(nouvelleCategorie.getQuantiteTotale());
            nouvelEvenement.addCategorieBillet(nouvelleCategorie);
            
            // Réinitialiser le formulaire de catégorie
            nouvelleCategorie = new CategorieBillet();
            showMessage(FacesMessage.SEVERITY_INFO, "Succès", "Catégorie ajoutée.");
        } else {
            showMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Veuillez remplir tous les champs de la catégorie.");
        }
    }

    /**
     * Supprime une catégorie de billet de la liste.
     */
    public void supprimerCategorie(CategorieBillet categorie) {
        nouvelEvenement.removeCategorieBillet(categorie);
        showMessage(FacesMessage.SEVERITY_INFO, "Info", "Catégorie retirée.");
    }

    /**
     * Enregistre le nouvel événement en base de données.
     */
    public String sauvegarderEvenement() {
        try {
            Personne utilisateur = authController.getUtilisateurConnecte();
            if (utilisateur instanceof Organisateur) {
                nouvelEvenement.setOrganisateur((Organisateur) utilisateur);
                nouvelEvenement.setStatut("Publié"); // ou "Brouillon" selon le bouton cliqué

                evenementService.creerEvenement(nouvelEvenement);
                showMessage(FacesMessage.SEVERITY_INFO, "Succès", "L'événement a été créé avec succès.");
                
                // Réinitialiser
                nouvelEvenement = new Evenement();
                chargerMesEvenements();
                return "orga_evenements?faces-redirect=true";
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de créer l'événement: " + e.getMessage());
        }
        return null;
    }

    /**
     * Méthode utilitaire pour afficher des messages JSF.
     */
    private void showMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}
