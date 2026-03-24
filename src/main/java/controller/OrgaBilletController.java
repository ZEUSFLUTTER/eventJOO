package controller;

import dao.CategorieBilletDao;
import entities.CategorieBillet;
import entities.Evenement;
import entities.Organisateur;
import service.EvenementService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.List;
import org.primefaces.PrimeFaces;

@Named("orgaBilletController")
@ViewScoped
@Getter
@Setter
public class OrgaBilletController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private CategorieBilletDao categorieBilletDao;

    @Inject
    private EvenementService evenementService;

    @Inject
    private AuthController authController;

    private List<Evenement> mesEvenements;
    private List<CategorieBillet> mesCategories;
    private CategorieBillet categorieSelectionnee;
    private CategorieBillet nouvelleCategorie = new CategorieBillet();
    private Long evenementFiltreId;
    private Long selectedEvenementId; // Pour la création

    @PostConstruct
    public void init() {
        chargerDonnees();
    }

    public void chargerDonnees() {
        try {
            Organisateur orga = (Organisateur) authController.getUtilisateurConnecte();
            if (orga != null) {
                mesEvenements = evenementService.getEvenementsByOrganisateur(orga.getId());
                if (evenementFiltreId != null && evenementFiltreId != 0) {
                    mesCategories = categorieBilletDao.findByEvenement(evenementFiltreId);
                } else {
                    mesCategories = categorieBilletDao.findByOrganisateur(orga.getId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void preparerModification(CategorieBillet cat) {
        this.categorieSelectionnee = cat;
    }

    public void enregistrerModification() {
        try {
            if (categorieSelectionnee != null) {
                categorieBilletDao.update(categorieSelectionnee);
                PrimeFaces.current().executeScript("Swal.fire('Succès', 'Le billet a été mis à jour.', 'success');");
                chargerDonnees();
            }
        } catch (Exception e) {
            e.printStackTrace();
            PrimeFaces.current().executeScript("Swal.fire('Erreur', 'Impossible de mettre à jour le billet.', 'error');");
        }
    }

    public void preparerNouvelleCategorie() {
        nouvelleCategorie = new CategorieBillet();
        selectedEvenementId = null;
    }

    public void ajouterCategorie() {
        try {
            if (selectedEvenementId == null) {
                PrimeFaces.current().executeScript("Swal.fire('Erreur', 'Veuillez sélectionner un événement.', 'error');");
                return;
            }
            Evenement ev = evenementService.trouverParId(selectedEvenementId);
            if (ev != null) {
                nouvelleCategorie.setEvenement(ev);
                nouvelleCategorie.setQuantiteDisponible(nouvelleCategorie.getQuantiteTotale());
                categorieBilletDao.save(nouvelleCategorie);
                PrimeFaces.current().executeScript("Swal.fire('Succès', 'Nouvelle catégorie ajoutée.', 'success');");
                chargerDonnees();
                preparerNouvelleCategorie();
            }
        } catch (Exception e) {
            e.printStackTrace();
            PrimeFaces.current().executeScript("Swal.fire('Erreur', 'Impossible d\\'ajouter la catégorie.', 'error');");
        }
    }
    
    public void supprimerCategorie(CategorieBillet cat) {
        try {
            System.out.println(">>> Tentative de suppression de la catégorie: " + (cat != null ? cat.getId() : "null"));
            categorieBilletDao.delete(cat.getId());
            System.out.println(">>> SUCCÈS: Catégorie supprimée");
            PrimeFaces.current().executeScript("Swal.fire('Supprimé', 'La catégorie a été supprimée.', 'success');");
            chargerDonnees();
        } catch (Exception e) {
            System.err.println(">>> ERREUR lors de la suppression de catégorie: " + e.getMessage());
            e.printStackTrace();
            PrimeFaces.current().executeScript("Swal.fire('Erreur', 'Impossible de supprimer la catégorie.', 'error');");
        }
    }
}
