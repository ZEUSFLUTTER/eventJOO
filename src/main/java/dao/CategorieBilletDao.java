package dao;

import entities.CategorieBillet;
import java.util.List;

public interface CategorieBilletDao {
    void save(CategorieBillet categorie);
    void update(CategorieBillet categorie);
    void delete(Long id);
    CategorieBillet findById(Long id);
    List<CategorieBillet> findByEvenement(Long evenementId);
    List<CategorieBillet> findByOrganisateur(Long organisateurId);
}
