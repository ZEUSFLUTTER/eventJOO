package dao;

import entities.Evenement;
import java.util.List;

/**
 * Interface DAO pour la gestion des événements.
 */
public interface EvenementDao {
    void save(Evenement evenement);
    List<Evenement> findAll();
    List<Evenement> findPublicEvents();
    Evenement findById(Long id);
    void delete(Long id);
    List<Evenement> findByOrganisateur(Long organisateurId);
    void update(Evenement evenement);
    long countUniqueClientsByOrganisateur(Long organisateurId);
}
