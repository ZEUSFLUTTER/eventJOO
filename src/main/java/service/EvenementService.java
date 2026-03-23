package service;

import dao.EvenementDao;
import entities.Evenement;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;

/**
 * Service de gestion des événements métiers.
 */
@Stateless
public class EvenementService {

    @Inject
    private EvenementDao evenementDao;

    /**
     * Crée un nouvel événement et persiste ses catégories de billets.
     */
    public void creerEvenement(Evenement evenement) {
        if (evenement.getCategoriesBillets().isEmpty()) {
            throw new IllegalArgumentException("L'événement doit avoir au moins une catégorie de billet.");
        }
        
        System.out.println("Création de l'événement: " + evenement.getTitre());
        evenementDao.save(evenement);
    }

    /**
     * Récupère les événements d'un organisateur spécifique.
     */
    public List<Evenement> getEvenementsByOrganisateur(Long organisateurId) {
        return evenementDao.findByOrganisateur(organisateurId);
    }

    /**
     * Compte le nombre d'événements créés par un organisateur.
     */
    public long countEvenementsByOrganisateur(Long organisateurId) {
        return evenementDao.findByOrganisateur(organisateurId).size();
    }

    /**
     * Supprime un événement par son ID.
     */
    public void supprimerEvenement(Long id) {
        evenementDao.delete(id);
    }

    /**
     * Trouve un événement par son ID.
     */
    public Evenement trouverParId(Long id) {
        return evenementDao.findById(id);
    }
}
