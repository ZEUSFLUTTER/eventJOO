package service;

import java.util.List;

import dao.EvenementDao;
import entities.Evenement;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

/**
 * Service de gestion des événements métiers.
 */
@Stateless
public class EvenementService {

    @Inject
    private EvenementDao evenementDao;

    /**
     * Crée un nouvel événement et persiste ses catégories de billets.
     * @throws IllegalArgumentException si un événement existe déjà à la même date et au même lieu.
     */
    public void creerEvenement(Evenement evenement) {
        System.out.println("Création de l'événement: " + evenement.getTitre());
        if (evenementDao.existsAtDateAndLocation(evenement.getDateEvenement(), evenement.getLieu())) {
            throw new IllegalArgumentException("Un événement existe déjà à cette date et dans ce lieu.");
        }
        evenementDao.save(evenement);
    }

    /**
     * Récupère tous les événements de la plateforme.
     */
    public List<Evenement> getAllEvents() {
        return evenementDao.findAll();
    }

    public List<Evenement> getPublicEvents() {
        return evenementDao.findPublicEvents();
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

    public void modifierEvenement(Evenement evenement) {
        evenementDao.update(evenement);
    }

    /**
     * Trouve un événement par son ID.
     */
    public Evenement trouverParId(Long id) {
        return evenementDao.findById(id);
    }

    /**
     * Compte le nombre total de billets vendus pour un organisateur.
     */
    public long countTotalTicketsVendus(Long organisateurId) {
        List<Evenement> evts = getEvenementsByOrganisateur(organisateurId);
        long totalVendu = 0;
        for (Evenement e : evts) {
            for (entities.CategorieBillet cat : e.getCategoriesBillets()) {
                totalVendu += (cat.getQuantiteTotale() - cat.getQuantiteDisponible());
            }
        }
        return totalVendu;
    }

    /**
     * Calcule le chiffre d'affaires total pour un organisateur.
     */
    public double calculateTotalRevenue(Long organisateurId) {
        List<Evenement> evts = getEvenementsByOrganisateur(organisateurId);
        double totalRevenue = 0;
        for (Evenement e : evts) {
            for (entities.CategorieBillet cat : e.getCategoriesBillets()) {
                totalRevenue += (cat.getQuantiteTotale() - cat.getQuantiteDisponible()) * cat.getPrix();
            }
        }
        return totalRevenue;
    }
    
    /**
     * Compte le nombre de clients uniques ayant acheté des billets chez cet organisateur.
     * Note: Actuellement simulé par le nombre de billets vendus / 1.5 car pas d'entité Billet directe.
     */
    public long countTotalClients(Long organisateurId) {
        return evenementDao.countUniqueClientsByOrganisateur(organisateurId);
    }

    /**
     * Ajoute une catégorie de billet à un événement de manière transactionnelle.
     */
    public void ajouterCategorie(Long evenementId, entities.CategorieBillet categorie) {
        Evenement ev = evenementDao.findById(evenementId);
        if (ev != null) {
            // Sécurité: Initialiser la quantité disponible si elle ne l'est pas
            if (categorie.getQuantiteDisponible() == null || categorie.getQuantiteDisponible() == 0) {
                categorie.setQuantiteDisponible(categorie.getQuantiteTotale());
            }
            ev.addCategorieBillet(categorie);
            evenementDao.update(ev);
        }
    }
}
