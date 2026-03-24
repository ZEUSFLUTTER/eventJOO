package dao;

import entities.Billet;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class BilletDaoImpl implements BilletDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void save(Billet billet) {
        em.persist(billet);
    }

    @Override
    public List<Billet> findAll() {
        return em.createQuery("SELECT b FROM Billet b", Billet.class).getResultList();
    }

    @Override
    public Billet findById(Long id) {
        return em.find(Billet.class, id);
    }

    @Override
    public void delete(Long id) {
        Billet billet = findById(id);
        if (billet != null) {
            em.remove(billet);
        }
    }

    @Override
    public List<Billet> findByClient(Long clientId) {
        return em.createQuery("SELECT b FROM Billet b WHERE b.client.id = :clientId", Billet.class)
                .setParameter("clientId", clientId)
                .getResultList();
    }

    @Override
    public void update(Billet billet) {
        em.merge(billet);
    }
}
