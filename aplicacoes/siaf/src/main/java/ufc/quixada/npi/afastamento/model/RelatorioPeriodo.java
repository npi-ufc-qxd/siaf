package ufc.quixada.npi.afastamento.model;

public class RelatorioPeriodo {
	
	private StatusTupla status;
	
	private Integer ano;
	
	private Integer semestre;

	public StatusTupla getStatus() {
		return status;
	}

	public void setStatus(StatusTupla status) {
		this.status = status;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getSemestre() {
		return semestre;
	}

	public void setSemestre(Integer semestre) {
		this.semestre = semestre;
	}
	
	

}
