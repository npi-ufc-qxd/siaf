package ufc.quixada.npi.afastamento.model;

public enum StatusReserva {
	
	ABERTO("ABERTO"), ENCERRADO("ENCERRADO"), AFASTADO("AFASTADO"), NAO_ACEITO("NÃO ACEITO"), 
	CANCELADO("CANCELADO"), CANCELADO_COM_PUNICAO("CANCELADO COM PUNIÇÃO"), NEGADO("NEGADO"), EM_ESPERA("EM ESPERA"),
	APTO_AFASTAMENTO("EM PROCESSO DE AFASTAMENTO");
	
	private String descricao;

	private StatusReserva(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return this.descricao;
	}
	
	public boolean isCancelado() {
		return this.equals(CANCELADO) || this.equals(CANCELADO_COM_PUNICAO) || this.equals(NEGADO);
	}
	
}
