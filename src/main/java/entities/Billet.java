package entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entité représentant un billet acheté par un client.
 */
@Entity
@Table(name = "billet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Billet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String codeQR; // Représentation Base64 du QR Code

    @Column(nullable = false, length = 20)
    private String statut; // e.g., "Valide", "Utilisé", "Annulé"

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_achat", nullable = false)
    private Date dateAchat;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categorie_id", nullable = false)
    private CategorieBillet categorieBillet;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "vendeur_id")
    private Employe vendeur;
    
    @jakarta.persistence.PrePersist
    protected void onCreate() {
        if (dateAchat == null) {
            dateAchat = new Date();
        }
        if (statut == null) {
            statut = "Valide";
        }
    }
}
