package controller;

import entities.Billet;
import entities.CategorieBillet;
import entities.Client;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Data;
import service.BilletService;
import java.io.Serializable;
import java.util.List;

/**
 * Controller pour la gestion des billets.
 */
@Named("billetController")
@SessionScoped
@Data
public class BilletController implements Serializable {
    
    @Inject
    private BilletService billetService;
    
    @Inject
    private AuthController authController;
    
    private List<Billet> billets;
    private Billet billetSelectionne;
    
    @PostConstruct
    public void init() {
        chargerBillets();
    }
    
    /**
     * Charge les billets du client connecté.
     */
    public void chargerBillets() {
        if (authController.getUtilisateurConnecte() != null && authController.getUtilisateurConnecte() instanceof Client) {
            billets = billetService.getBilletsByClient(authController.getUtilisateurConnecte().getId());
        }
    }
    
    /**
     * Action pour acheter un billet.
     */
    public String acheterBillet(CategorieBillet categorie) {
        if (authController.getUtilisateurConnecte() == null) {
            addMessage("Connexion requise", "Veuillez vous connecter pour acheter un billet.", FacesMessage.SEVERITY_WARN);
            return "login?faces-redirect=true";
        }
        
        if (!(authController.getUtilisateurConnecte() instanceof Client)) {
            addMessage("Erreur", "Seuls les clients peuvent acheter des billets.", FacesMessage.SEVERITY_ERROR);
            return null;
        }
        
        try {
            Client client = (Client) authController.getUtilisateurConnecte();
            Billet nouveauBillet = billetService.acheterBillet(client, categorie);
            addMessage("Succès", "Billet acheté avec succès pour " + categorie.getEvenement().getTitre(), FacesMessage.SEVERITY_INFO);
            chargerBillets();
            return "dashboard_client?faces-redirect=true";
        } catch (Exception e) {
            addMessage("Erreur lors de l'achat", e.getMessage(), FacesMessage.SEVERITY_ERROR);
            return null;
        }
    }
    
    public void voirBillet(Billet billet) {
        this.billetSelectionne = billet;
    }
    
    private void addMessage(String summary, String detail, FacesMessage.Severity severity) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }
}
