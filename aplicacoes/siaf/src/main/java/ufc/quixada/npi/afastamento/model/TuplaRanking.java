package ufc.quixada.npi.afastamento.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TuplaRanking {
	
	private String professor;
	
	// Número de semestres desde a contratação na UFC até o início do afastamento
	private Integer t;
	
	//Número de semestres em que o docente já esteve afastado
	private Integer a;
	
	//Número de semestres do afastamento reservado/solicitado. (No caso de primeiro afastamento, a variável S terá valor dois (2)).
	private Integer s;
	
	//Número de semestres que faltam para o docente completar três (3) anos de contratação na UFC Quixadá (vale zero se já cumpriu este período).
	private Integer p;
	
	// Número de semestres solicitados
	private Integer ss;
	
	private Float pontuacao;
	
	private StatusTupla status;
	
	@JsonIgnore
	private Periodo periodo;
	
	private Reserva reserva;

	public StatusTupla getStatus() {
		return status;
	}

	public void setStatus(StatusTupla status) {
		this.status = status;
	}

	public Integer getT() {
		return t;
	}

	public void setT(Integer t) {
		this.t = t;
	}

	public Integer getA() {
		return a;
	}

	public void setA(Integer a) {
		this.a = a;
	}

	public Integer getS() {
		return s;
	}

	public void setS(Integer s) {
		this.s = s;
	}

	public Float getPontuacao() {
		return pontuacao;
	}

	public void setPontuacao(Float pontuacao) {
		this.pontuacao = pontuacao;
	}

	public Periodo getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Periodo periodo) {
		this.periodo = periodo;
	}

	public Reserva getReserva() {
		return reserva;
	}

	public void setReserva(Reserva reserva) {
		this.reserva = reserva;
	}

	public String getProfessor() {
		return professor;
	}

	public void setProfessor(String professor) {
		this.professor = professor;
	}

	public Integer getSs() {
		return ss;
	}

	public void setSs(Integer ss) {
		this.ss = ss;
	}

	public Integer getP() {
		return p;
	}

	public void setP(Integer p) {
		this.p = p;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((periodo == null) ? 0 : periodo.hashCode());
		result = prime * result + ((reserva == null) ? 0 : reserva.hashCode());
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
		TuplaRanking other = (TuplaRanking) obj;
		if (periodo == null) {
			if (other.periodo != null)
				return false;
		} else if (!periodo.equals(other.periodo))
			return false;
		if (reserva == null) {
			if (other.reserva != null)
				return false;
		} else if (!reserva.equals(other.reserva))
			return false;
		return true;
	}

	
	
	

}
