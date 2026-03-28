package controller;

import entities.AttributionBillet;
import entities.Billet;
import entities.Client;
import entities.Employe;
import entities.Personne;
import entities.Personne.Role;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import service.AttributionBilletService;
import service.BilletService;
import service.PersonneService;
import java.io.Serializable;
import java.util.List;

@Named("dashboardEmployeController")
@ViewScoped
@Getter
@Setter
public class DashboardEmployeController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private AuthController authController;

    @Inject
    private AttributionBilletService attributionService;

    @Inject
    private BilletService billetService;

    @Inject
    private PersonneService personneService;

    private List<AttributionBillet> myAttributions;
    private AttributionBillet selectedAttribution;
    
    // Stats
    private int totalAssigne = 0;
    private int totalVendu = 0;
    private int totalRestant = 0;

    // Vente
    private String clientEmail;
    private String clientNom;
    private String clientPrenom;

    @PostConstruct
    public void init() {
        if (authController.getUtilisateurConnecte() instanceof Employe) {
            chargerDonnees();
        }
    }

    public void chargerDonnees() {
        Employe emp = (Employe) authController.getUtilisateurConnecte();
        myAttributions = attributionService.findByEmploye(emp.getId());
        
        // Calculer les stats
        totalAssigne = 0;
        totalVendu = 0;
        for (AttributionBillet a : myAttributions) {
            totalAssigne += a.getQuantiteAssignee();
            totalVendu += a.getQuantiteVendue();
        }
        totalRestant = totalAssigne - totalVendu;
    }

    public void preparerVente(AttributionBillet a) {
        this.selectedAttribution = a;
        this.clientEmail = "";
        this.clientNom = "";
        this.clientPrenom = "";
    }

    public void onEmailBlur() {
        if (clientEmail != null && !clientEmail.isEmpty()) {
            Personne pFound = personneService.trouverParEmail(clientEmail);
            if (pFound instanceof Client) {
                Client c = (Client) pFound;
                this.clientNom = c.getNom();
                this.clientPrenom = c.getPrenom();
            }
        }
    }

    public void effectuerVente() {
        try {
            if (selectedAttribution == null || clientEmail == null || clientEmail.isEmpty()) {
                addMessage("Erreur", "Données de vente incomplètes.", FacesMessage.SEVERITY_ERROR);
                return;
            }

            if (selectedAttribution.getReste() <= 0) {
                addMessage("Quota épuisé", "Vous n'avez plus de billets à vendre dans cette catégorie.", FacesMessage.SEVERITY_WARN);
                return;
            }

            // Trouver ou Créer le client
            Personne pExist = personneService.trouverParEmail(clientEmail);
            Client clientObj;
            if (pExist instanceof Client) {
                clientObj = (Client) pExist;
            } else {
                // Créer un nouveau client
                clientObj = new Client();
                clientObj.setEmail(clientEmail);
                clientObj.setNom(this.clientNom != null && !this.clientNom.isEmpty() ? this.clientNom : "Client");
                clientObj.setPrenom(this.clientPrenom != null && !this.clientPrenom.isEmpty() ? this.clientPrenom : "Passant");
                clientObj.setMotDePasse("pass123"); 
                clientObj.setRole(Role.CLIENT);
                personneService.save(clientObj);
            }

            // Enregistrer la vente dans Billet (décrémente stock global)
            Employe empVendeur = (Employe) authController.getUtilisateurConnecte();
            billetService.vendreBilletParEmploye(empVendeur, selectedAttribution.getCategorieBillet(), clientObj);

            // Enregistrer la vente dans Attribution (décrémente quota employé)
            attributionService.enregistrerVente(selectedAttribution.getId(), 1);

            // Recharger
            chargerDonnees();
            
            org.primefaces.PrimeFaces.current().executeScript("Swal.fire('Succès', 'Billet vendu avec succès !', 'success');");
            org.primefaces.PrimeFaces.current().executeScript("PF('venteDialog').hide();");

        } catch (Exception e) {
            e.printStackTrace();
            addMessage("Erreur", "Impossible d'effectuer la vente : " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    private void addMessage(String summary, String detail, FacesMessage.Severity severity) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}
