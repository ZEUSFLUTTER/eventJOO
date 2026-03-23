package entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entité représentant une catégorie de billet (ex: VIP, Standard) pour un événement.
 */
@Entity
@Table(name = "categorie_billet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategorieBillet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    @Column(nullable = false, length = 50)
    private String nom; // e.g., "VIP", "Standard"

    @NotNull(message = "Le prix est obligatoire")
    @Min(value = 0, message = "Le prix ne peut pas être négatif")
    @Column(nullable = false)
    private Double prix;

    @NotNull(message = "La quantité totale est obligatoire")
    @Min(value = 1, message = "La quantité totale doit être d'au moins 1")
    @Column(name = "quantite_totale", nullable = false)
    private Integer quantiteTotale;

    @NotNull(message = "La quantité disponible est obligatoire")
    @Min(value = 0, message = "La quantité disponible ne peut pas être négative")
    @Column(name = "quantite_disponible", nullable = false)
    private Integer quantiteDisponible;

    @ManyToOne(optional = false)
    @JoinColumn(name = "evenement_id", nullable = false)
    private Evenement evenement;
}
