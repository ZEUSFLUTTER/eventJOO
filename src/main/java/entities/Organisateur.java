package entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author COMLAN
 */
@Entity
@Getter
@Setter
public class Organisateur extends Personne {
    
    /*@OneToMany(mappedBy = "organisateur", cascade = CascadeType.ALL)
    private List<Evenement> evenements = new ArrayList<>();*/
    
    @OneToMany(mappedBy = "employeur", cascade = CascadeType.ALL)
    private List<Employe> employes = new ArrayList<>();
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organisateur that = (Organisateur) o;
        return Objects.equals(getId(), that.getId());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}    

