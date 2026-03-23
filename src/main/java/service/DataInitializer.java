/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.PersonneDao;
import entities.Client;
import entities.Gerant;
import entities.Organisateur;
import entities.Personne.Role;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;

/**
 * Initialise les données de test au démarrage de l'application
 * @author COMLAN
 */
@Singleton
@Startup
public class DataInitializer {

    @Inject
    private PersonneDao personneDao;

    @PostConstruct
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void init() {
        System.out.println("=== DÉMARRAGE INITIALISATION DONNÉES ===");
        
        // Créer le gérant par défaut
        creerGerant();
        
        // Créer les utilisateurs de test
        creerUtilisateursTest();
        
        // Vérifier ce qui a été créé
        verifierDonnees();
        
        System.out.println("=== FIN INITIALISATION DONNÉES ===");
    }
    
    private void creerGerant() {
        if (!personneDao.emailExiste("admin@event.com")) {
            Gerant admin = new Gerant();
            admin.setNom("System");
            admin.setPrenom("Admin");
            admin.setEmail("admin@event.com");
            admin.setMotDePasse("admin123");
            admin.setRole(Role.GERANT);
            personneDao.enregistrer(admin);
            System.out.println("✓ Gérant créé: admin@event.com");
        } else {
            System.out.println("✓ Gérant existe déjà");
        }
    }
    
    private void creerUtilisateursTest() {
        // 3 Organisateurs
        creerOrganisateur("Dupont", "Jean", "orga1@event.com");
        creerOrganisateur("Martin", "Sophie", "orga2@event.com");
        creerOrganisateur("Bernard", "Pierre", "orga3@event.com");
        
        // 5 Clients
        creerClient("Dubois", "Marie", "client1@event.com");
        creerClient("Leroy", "Thomas", "client2@event.com");
        creerClient("Moreau", "Julie", "client3@event.com");
        creerClient("Simon", "Lucas", "client4@event.com");
        creerClient("Laurent", "Emma", "client5@event.com");
    }
    
    private void creerOrganisateur(String nom, String prenom, String email) {
        if (!personneDao.emailExiste(email)) {
            Organisateur orga = new Organisateur();
            orga.setNom(nom);
            orga.setPrenom(prenom);
            orga.setEmail(email);
            orga.setMotDePasse("password123");
            orga.setRole(Role.ORGANISATEUR);
            personneDao.enregistrer(orga);
            System.out.println("✓ Organisateur créé: " + prenom + " " + nom);
        }
    }
    
    private void creerClient(String nom, String prenom, String email) {
        if (!personneDao.emailExiste(email)) {
            Client client = new Client();
            client.setNom(nom);
            client.setPrenom(prenom);
            client.setEmail(email);
            client.setMotDePasse("password123");
            client.setRole(Role.CLIENT);
            personneDao.enregistrer(client);
            System.out.println("✓ Client créé: " + prenom + " " + nom);
        }
    }
    
    private void verifierDonnees() {
        try {
            long nbOrga = personneDao.findByRole(Role.ORGANISATEUR).size();
            long nbClient = personneDao.findByRole(Role.CLIENT).size();
            long nbGerant = personneDao.findByRole(Role.GERANT).size();
            
            System.out.println("=== VÉRIFICATION BASE DE DONNÉES ===");
            System.out.println("Gérants: " + nbGerant);
            System.out.println("Organisateurs: " + nbOrga);
            System.out.println("Clients: " + nbClient);
            System.out.println("TOTAL: " + (nbGerant + nbOrga + nbClient));
        } catch (Exception e) {
            System.err.println("Erreur vérification: " + e.getMessage());
        }
    }
}