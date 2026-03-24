package service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import dao.BilletDao;
import entities.Billet;
import entities.CategorieBillet;
import entities.Client;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Stateless
public class BilletService {

    @Inject
    private BilletDao billetDao;

    @PersistenceContext
    private EntityManager em;

    /**
     * Effectue l'achat d'un billet pour un client dans une catégorie donnée.
     */
    public Billet acheterBillet(Client client, CategorieBillet categorie) {
        // Recharger la catégorie pour éviter les problèmes de concurrence et avoir les dernières données
        CategorieBillet managedCategorie = em.find(CategorieBillet.class, categorie.getId());
        
        if (managedCategorie.getQuantiteDisponible() <= 0) {
            throw new IllegalStateException("Plus de billets disponibles pour cette catégorie.");
        }

        // Création du billet
        Billet billet = new Billet();
        billet.setClient(client);
        billet.setCategorieBillet(managedCategorie);
        billet.setStatut("Valide");
        
        // Génération d'un code unique pour le QR
        String uniqueContent = "TICKET-" + UUID.randomUUID().toString() + "-" + managedCategorie.getEvenement().getId();
        String qrCodeBase64 = generateQRCodeBase64(uniqueContent);
        billet.setCodeQR(qrCodeBase64);

        // Décrémenter la quantité
        managedCategorie.setQuantiteDisponible(managedCategorie.getQuantiteDisponible() - 1);
        em.merge(managedCategorie);

        // Sauvegarder le billet
        billetDao.save(billet);
        
        return billet;
    }

    public List<Billet> getBilletsByClient(Long clientId) {
        return billetDao.findByClient(clientId);
    }

    /**
     * Génère un QR Code en Base64 à partir d'une chaîne de caractères.
     */
    private String generateQRCodeBase64(String text) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 300, 300);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();
            
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(pngData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
