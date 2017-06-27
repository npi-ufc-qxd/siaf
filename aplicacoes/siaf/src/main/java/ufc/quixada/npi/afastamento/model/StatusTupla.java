package ufc.quixada.npi.afastamento.model;

public enum StatusTupla {
	
	ENCERRADO("ENCERRADO"), CLASSIFICADO("CLASSIFICADO"), DESCLASSIFICADO("DESCLASSIFICADO"), AFASTADO("AFASTADO"), 
	NAO_ACEITO("NÃO ACEITO"), CANCELADO("CANCELADO"), CANCELADO_COM_PUNICAO("CANCELADO COM PUNIÇAO"), NEGADO("NEGADO"),
	APTO_AFASTAMENTO("APTO PARA AFASTAMENTO");
	
	private String descricao;

	private StatusTupla(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return this.descricao;
	}
	

}
