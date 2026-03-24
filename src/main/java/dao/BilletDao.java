package dao;

import entities.Billet;
import java.util.List;

/**
 * Interface DAO pour la gestion des billets.
 */
public interface BilletDao {
    void save(Billet billet);
    List<Billet> findAll();
    Billet findById(Long id);
    void delete(Long id);
    List<Billet> findByClient(Long clientId);
    void update(Billet billet);
}
