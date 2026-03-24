package entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entité représentant un événement créé par un organisateur.
 */
@Entity
@Table(name = "evenement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Evenement implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Column(nullable = false, length = 100)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Le lieu est obligatoire")
    @Column(nullable = false, length = 150)
    private String lieu;

    @NotNull(message = "La date de l'événement est obligatoire")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_evenement", nullable = false)
    private Date dateEvenement;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_creation", nullable = false)
    private Date dateCreation;

    @Column(nullable = false, length = 20)
    private String statut; // e.g. "Brouillon", "Publié", "Annulé"

    @Column(length = 50)
    private String type; // e.g. "CONCERT", "CONFERENCE", "SPORT"

    @Column(name = "est_public")
    private boolean estPublic = true;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organisateur_id", nullable = false)
    private Organisateur organisateur;

    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategorieBillet> categoriesBillets = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = new Date();
        }
        if (statut == null || statut.trim().isEmpty()) {
            statut = "Brouillon";
        }
    }

    public void addCategorieBillet(CategorieBillet categorie) {
        categoriesBillets.add(categorie);
        categorie.setEvenement(this);
    }

    public void removeCategorieBillet(CategorieBillet categorie) {
        categoriesBillets.remove(categorie);
        categorie.setEvenement(null);
    }
}
