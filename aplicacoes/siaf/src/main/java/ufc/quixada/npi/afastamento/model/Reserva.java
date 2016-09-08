package ufc.quixada.npi.afastamento.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Reserva {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Integer anoInicio;
	
	private Integer semestreInicio;
	
	private Integer anoTermino;
	
	private Integer semestreTermino;
	
	@Enumerated(EnumType.STRING)
	private Programa programa;
	
	private Integer conceitoPrograma;
	
	private String instituicao;
	
	private Date dataSolicitacao;
	
	@JsonIgnore
	@ManyToOne
	private Professor professor;
	
	@Enumerated(EnumType.STRING)
	private StatusReserva status;
	
	@JsonIgnore
	@OrderBy(value = "data")
	@OneToMany(mappedBy = "reserva", cascade = CascadeType.REMOVE)
	private List<Historico> historicos;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getAnoInicio() {
		return anoInicio;
	}

	public void setAnoInicio(Integer anoInicio) {
		this.anoInicio = anoInicio;
	}

	public Integer getSemestreInicio() {
		return semestreInicio;
	}

	public void setSemestreInicio(Integer semestreInicio) {
		this.semestreInicio = semestreInicio;
	}

	public Integer getAnoTermino() {
		return anoTermino;
	}

	public void setAnoTermino(Integer anoTermino) {
		this.anoTermino = anoTermino;
	}

	public Integer getSemestreTermino() {
		return semestreTermino;
	}

	public void setSemestreTermino(Integer semestreTermino) {
		this.semestreTermino = semestreTermino;
	}

	public Programa getPrograma() {
		return programa;
	}

	public void setPrograma(Programa programa) {
		this.programa = programa;
	}

	public Professor getProfessor() {
		return professor;
	}

	public void setProfessor(Professor professor) {
		this.professor = professor;
	}

	public StatusReserva getStatus() {
		return status;
	}

	public void setStatus(StatusReserva status) {
		this.status = status;
	}

	public Integer getConceitoPrograma() {
		return conceitoPrograma;
	}

	public void setConceitoPrograma(Integer conceitoPrograma) {
		this.conceitoPrograma = conceitoPrograma;
	}

	public String getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(String instituicao) {
		this.instituicao = instituicao;
	}
	
	public List<Historico> getHistoricos() {
		return historicos;
	}

	public void setHistoricos(List<Historico> historicos) {
		this.historicos = historicos;
	}

	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}

	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
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
		Reserva other = (Reserva) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
