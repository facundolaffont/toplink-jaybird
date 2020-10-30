import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.NamedQueries;

@Entity
@Table
@NamedQueries({
	@NamedQuery(name="cliente.todos", query="SELECT c FROM Cliente c"),
	@NamedQuery(name="cliente.porID", query="SELECT c FROM Cliente c WHERE c.id = :id")
})
public class Cliente {
	
	@Id
	@Column
	private int id;
	
	@Column
	private String descr;
	
	public Cliente() {}
	
	public int getID() {
		return id;
	}
	
	public String getDescr() {
		return descr;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public void setDescr(String descr) {
		this.descr = descr;
	}
	
	public String toString() {
		return "Código: " + id + "\n" + "Descripción: " + descr;
	}
}