/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import entities.Personne;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

/**
 *
 * @author COMLAN
 */
@Stateless
public class PersonneDaoImpl implements PersonneDao {

    @PersistenceContext(unitName = "EventPU")
    private EntityManager em;

    @Override
    public void enregistrer(Personne p) {
        em.persist(p);
         em.flush();
    }
    
    @Override
    public Personne authentifier(String email, String mdp) {
        try {
            TypedQuery<Personne> query = em.createQuery(
                "SELECT p FROM Personne p WHERE p.email = :email AND p.motDePasse = :mdp", Personne.class);
            query.setParameter("email", email);
            query.setParameter("mdp", mdp);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null; // Pas trouvé
        }
    }
    
    @Override
    public boolean emailExiste(String email) {
        try {
            Long count = em.createQuery(
                "SELECT COUNT(p) FROM Personne p WHERE p.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public java.util.List<Personne> findAll() {
        TypedQuery<Personne> query = em.createQuery("SELECT p FROM Personne p", Personne.class);
        return query.getResultList();
    }
    
    @Override
    public java.util.List<Personne> findByRole(Personne.Role role) {
        TypedQuery<Personne> query = em.createQuery(
            "SELECT p FROM Personne p WHERE p.role = :role", Personne.class);
        query.setParameter("role", role);
        return query.getResultList();
    }

    @Override
    public Personne trouverParEmail(String email) {
        try {
            TypedQuery<Personne> query = em.createQuery(
                "SELECT p FROM Personne p WHERE p.email = :email", Personne.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}