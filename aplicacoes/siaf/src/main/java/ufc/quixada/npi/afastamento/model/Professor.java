package ufc.quixada.npi.afastamento.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Professor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer anoAdmissao;
	
	private Integer semestreAdmissao;

	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private Usuario usuario;
	
	@OneToMany(mappedBy = "professor", cascade = CascadeType.REMOVE)
	private List<Reserva> reservas;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getAnoAdmissao() {
		return anoAdmissao;
	}

	public void setAnoAdmissao(Integer anoAdmissao) {
		this.anoAdmissao = anoAdmissao;
	}

	public Integer getSemestreAdmissao() {
		return semestreAdmissao;
	}

	public void setSemestreAdmissao(Integer semestreAdmissao) {
		this.semestreAdmissao = semestreAdmissao;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Reserva> getReservas() {
		return reservas;
	}

	public void setReservas(List<Reserva> reservas) {
		this.reservas = reservas;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Professor other = (Professor) obj;
		if (usuario.getCpf() == null) {
			if (other.getUsuario().getCpf() != null)
				return false;
		} else if (!usuario.getCpf().equals(other.getUsuario().getCpf()))
			return false;
		return true;
	}

	

}
