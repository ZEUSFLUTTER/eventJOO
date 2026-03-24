package dao;

import entities.CategorieBillet;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class CategorieBilletDaoImpl implements CategorieBilletDao {

    @PersistenceContext(unitName = "EventPU")
    private EntityManager em;

    @Override
    @Transactional
    public void save(CategorieBillet categorie) {
        em.persist(categorie);
    }

    @Override
    @Transactional
    public void update(CategorieBillet categorie) {
        em.merge(categorie);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        CategorieBillet cat = em.find(CategorieBillet.class, id);
        if (cat != null) {
            em.remove(cat);
        }
    }

    @Override
    public CategorieBillet findById(Long id) {
        return em.find(CategorieBillet.class, id);
    }

    @Override
    public List<CategorieBillet> findByEvenement(Long evenementId) {
        return em.createQuery("SELECT c FROM CategorieBillet c WHERE c.evenement.id = :eventId", CategorieBillet.class)
                .setParameter("eventId", evenementId)
                .getResultList();
    }

    @Override
    public List<CategorieBillet> findByOrganisateur(Long organisateurId) {
        return em.createQuery("SELECT c FROM CategorieBillet c WHERE c.evenement.organisateur.id = :orgId", CategorieBillet.class)
                .setParameter("orgId", organisateurId)
                .getResultList();
    }
}
