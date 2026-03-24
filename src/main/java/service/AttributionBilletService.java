package service;

import dao.AttributionBilletDao;
import entities.AttributionBillet;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;

@Stateless
public class AttributionBilletService {

    @Inject
    private AttributionBilletDao attributionDao;

    public void enregistrer(AttributionBillet a) {
        attributionDao.enregistrer(a);
    }

    public AttributionBillet mettreAJour(AttributionBillet a) {
        return attributionDao.mettreAJour(a);
    }

    public void supprimer(Long id) {
        attributionDao.supprimer(id);
    }

    public AttributionBillet trouverParId(Long id) {
        return attributionDao.trouverParId(id);
    }

    public List<AttributionBillet> findByEmploye(Long employeId) {
        return attributionDao.findByEmploye(employeId);
    }
    
    public List<AttributionBillet> findByCategorie(Long categorieId) {
        return attributionDao.findByCategorie(categorieId);
    }
    
    public void save(AttributionBillet a) {
        if (a.getId() == null) {
            enregistrer(a);
        } else {
            mettreAJour(a);
        }
    }
}
