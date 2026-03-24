package entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité représentant l'attribution d'un quota de billets à un employé.
 */
@Entity
@Table(name = "attribution_billet")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributionBillet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categorie_id", nullable = false)
    private CategorieBillet categorieBillet;

    @Column(name = "quantite_assignee", nullable = false)
    private int quantiteAssignee;

    @Column(name = "quantite_vendue", nullable = false)
    private int quantiteVendue = 0;
    
    public int getReste() {
        return quantiteAssignee - quantiteVendue;
    }
}
