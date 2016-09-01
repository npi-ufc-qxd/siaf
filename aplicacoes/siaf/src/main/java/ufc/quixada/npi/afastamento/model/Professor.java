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

	/*@Size(min = 11, message="Minino 11 dígitos")
	private String cpf;
	
	@Transient
	private String nome;
	
	@Transient
	private String email;
	
	@Transient
	private String siape;*/

	/*@Transient
	private Date dataNascimento;*/
	
	private Integer anoAdmissao;
	
	private Integer semestreAdmissao;

	/*@Transient
	private Date dataAdmissao;

	@Transient
	private Date dataSaida;*/
	
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private Usuario usuario;
	
	@OneToMany(mappedBy = "professor", cascade = CascadeType.REMOVE)
	private List<Reserva> reservas;

	/*public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}*/

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/*public String getSiape() {
		return siape;
	}

	public void setSiape(String siape) {
		this.siape = siape;
	}*/

	/*public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}*/

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

	/*public Date getDataSaida() {
		return dataSaida;
	}

	public void setDataSaida(Date dataSaida) {
		this.dataSaida = dataSaida;
	}

	public Date getDataAdmissao() {
		return dataAdmissao;
	}

	public void setDataAdmissao(Date dataAdmissao) {
		this.dataAdmissao = dataAdmissao;
	}*/

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

	/*public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}*/

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
