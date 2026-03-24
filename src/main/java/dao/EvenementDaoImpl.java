package dao;

import java.util.List;

import entities.Evenement;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

/**
 * Implémentation du DAO pour les événements.
 */
@Stateless
public class EvenementDaoImpl implements EvenementDao {

    @PersistenceContext(unitName = "EventPU")
    private EntityManager em;

    @Override
    public void save(Evenement evenement) {
        em.persist(evenement);
    }

    @Override
    public List<Evenement> findAll() {
        return em.createQuery(
                "SELECT DISTINCT e FROM Evenement e " +
                "LEFT JOIN FETCH e.categoriesBillets " +
                "ORDER BY e.dateCreation DESC",
                Evenement.class)
                .getResultList();
    }

    @Override
    public List<Evenement> findPublicEvents() {
        return em.createQuery(
                "SELECT DISTINCT e FROM Evenement e " +
                "LEFT JOIN FETCH e.categoriesBillets " +
                "WHERE e.statut IS NOT NULL " +
                "AND LOWER(e.statut) NOT IN ('brouillon', 'annulé', 'annule', 'draft', 'cancelled') " +
                "ORDER BY e.dateEvenement ASC",
                Evenement.class)
                .getResultList();
    }

    @Override
    public Evenement findById(Long id) {
        return em.find(Evenement.class, id);
    }

    @Override
    public void delete(Long id) {
        Evenement evenement = findById(id);
        if (evenement != null) {
            em.remove(evenement);
        }
    }

    @Override
    public List<Evenement> findByOrganisateur(Long organisateurId) {
        TypedQuery<Evenement> query = em.createQuery(
                "SELECT DISTINCT e FROM Evenement e " +
                "LEFT JOIN FETCH e.categoriesBillets " +
                "WHERE e.organisateur.id = :orgaId " +
                "ORDER BY e.dateCreation DESC",
                Evenement.class);
        query.setParameter("orgaId", organisateurId);
        return query.getResultList();
    }

    @Override
    public void update(Evenement evenement) {
        em.merge(evenement);
    }

    @Override
    public long countUniqueClientsByOrganisateur(Long organisateurId) {
        return em.createQuery(
            "SELECT COUNT(DISTINCT b.client.id) FROM Billet b WHERE b.categorieBillet.evenement.organisateur.id = :orgId", 
            Long.class)
            .setParameter("orgId", organisateurId)
            .getSingleResult();
    }
}
