package dao;

import entities.AttributionBillet;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
public class AttributionBilletDao {

    @PersistenceContext(unitName = "EventPU")
    private EntityManager em;

    public void enregistrer(AttributionBillet a) {
        em.persist(a);
    }

    public AttributionBillet mettreAJour(AttributionBillet a) {
        return em.merge(a);
    }

    public void supprimer(Long id) {
        AttributionBillet a = em.find(AttributionBillet.class, id);
        if (a != null) {
            em.remove(a);
        }
    }

    public AttributionBillet trouverParId(Long id) {
        return em.find(AttributionBillet.class, id);
    }

    public List<AttributionBillet> findByEmploye(Long employeId) {
        TypedQuery<AttributionBillet> query = em.createQuery(
            "SELECT a FROM AttributionBillet a WHERE a.employe.id = :empId", AttributionBillet.class);
        query.setParameter("empId", employeId);
        return query.getResultList();
    }
    
    public List<AttributionBillet> findByCategorie(Long categorieId) {
        TypedQuery<AttributionBillet> query = em.createQuery(
            "SELECT a FROM AttributionBillet a WHERE a.categorieBillet.id = :catId", AttributionBillet.class);
        query.setParameter("catId", categorieId);
        return query.getResultList();
    }
}
