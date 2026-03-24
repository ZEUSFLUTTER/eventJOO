package controller;

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

    private static final long serialVersionUID = 1L;

    @Inject
    private EvenementService evenementService;

    @Inject
    private AuthController authController;

    private List<Evenement> mesEvenements;
    private Evenement nouvelEvenement;
    private Evenement evenementEnEdition;

    @PostConstruct
    public void init() {
        nouvelEvenement = new Evenement();
        evenementEnEdition = new Evenement();
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
     * Enregistre le nouvel événement en base de données.
     */
    public String sauvegarderEvenement() {
        try {
            Personne utilisateur = authController.getUtilisateurConnecte();
            if (utilisateur instanceof Organisateur) {
                nouvelEvenement.setOrganisateur((Organisateur) utilisateur);
                nouvelEvenement.setStatut("Publié"); 

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

    public void preparerModification(Evenement evenement) {
        if (!estProprietaire(evenement)) {
            showMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Vous ne pouvez pas modifier cet événement.");
            return;
        }
        evenementEnEdition = new Evenement();
        evenementEnEdition.setId(evenement.getId());
        evenementEnEdition.setTitre(evenement.getTitre());
        evenementEnEdition.setDescription(evenement.getDescription());
        evenementEnEdition.setLieu(evenement.getLieu());
        evenementEnEdition.setDateEvenement(evenement.getDateEvenement());
        evenementEnEdition.setStatut(evenement.getStatut());
        evenementEnEdition.setOrganisateur(evenement.getOrganisateur());
        evenementEnEdition.setCategoriesBillets(evenement.getCategoriesBillets());
        evenementEnEdition.setDateCreation(evenement.getDateCreation());
    }

    public void sauvegarderModification() {
        try {
            if (!estProprietaire(evenementEnEdition)) {
                showMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Modification non autorisée.");
                return;
            }
            evenementService.modifierEvenement(evenementEnEdition);
            chargerMesEvenements();
            showMessage(FacesMessage.SEVERITY_INFO, "Succès", "Événement modifié avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de modifier l'événement: " + e.getMessage());
        }
    }

    public void supprimerEvenement(Evenement evenement) {
        try {
            System.out.println(">>> Tentative de suppression de l'événement: " + (evenement != null ? evenement.getId() : "null"));
            if (!estProprietaire(evenement)) {
                System.out.println(">>> ÉCHEC: Pas propriétaire");
                org.primefaces.PrimeFaces.current().executeScript("Swal.fire('Erreur', 'Suppression non autorisée.', 'error');");
                return;
            }
            evenementService.supprimerEvenement(evenement.getId());
            System.out.println(">>> SUCCÈS: Événement supprimé");
            chargerMesEvenements();
            org.primefaces.PrimeFaces.current().executeScript("Swal.fire('Succès', 'L\\'événement a été supprimé.', 'success');");
        } catch (Exception e) {
            System.err.println(">>> ERREUR lors de la suppression: " + e.getMessage());
            e.printStackTrace();
            org.primefaces.PrimeFaces.current().executeScript("Swal.fire('Erreur SQL/Système', 'Impossible de supprimer l\\'événement. Vérifiez qu\\'aucun billet n\\'a été vendu.', 'error');");
        }
    }

    public String resumeBilletterie(Evenement evenement) {
        if (evenement == null || evenement.getCategoriesBillets() == null || evenement.getCategoriesBillets().isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int limit = Math.min(2, evenement.getCategoriesBillets().size());
        for (int i = 0; i < limit; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(evenement.getCategoriesBillets().get(i).getNom());
        }
        if (evenement.getCategoriesBillets().size() > 2) {
            sb.append("...");
        }
        return sb.toString();
    }

    private boolean estProprietaire(Evenement evenement) {
        if (evenement == null || evenement.getOrganisateur() == null || evenement.getId() == null) {
            return false;
        }
        Personne utilisateur = authController.getUtilisateurConnecte();
        return utilisateur instanceof Organisateur
                && utilisateur.getId() != null
                && utilisateur.getId().equals(evenement.getOrganisateur().getId());
    }

    private void showMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}
