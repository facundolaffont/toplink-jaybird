import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.NamedQueries;

@Entity
@Table
@NamedQueries({
	@NamedQuery(name="factura.todas", query="SELECT f FROM Factura f"),
	@NamedQuery(name="factura.porNro", query="SELECT f FROM Factura f WHERE f.nro = :nro"),
	@NamedQuery(name="factura.porIdCliente", query="SELECT f FROM Factura f WHERE f.idCliente = :id")
})
public class Factura {
	
	@Id
	@Column
	private int nro;
	
	@Column(name="id")
	private int idCliente;
	
	@Column
	private float importe;
	
	public Factura() {}
	
	public int getNro() {
		return nro;
	}
	
	public int getIdCliente() {
		return idCliente;
	}
	
	public float getImporte() {
		return importe;
	}
	
	public void setNro(int nro) {
		this.nro = nro;
	}
	
	public void setIdCliente(int idCliente) {
		this.idCliente = idCliente;
	}
	
	public void setImporte(float importe) {
		this.importe = importe;
	}
	
	public String toString() {
		return "Número: " + nro + "\n" + "ID de cliente: " + idCliente + "\n" + "Importe: " + importe;
	}
}