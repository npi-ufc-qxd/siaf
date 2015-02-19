package ufc.quixada.npi.afastamento.util;

public class Constants {
	
	public static final String USUARIO_LOGADO = "usuario";
	
	public static final String PROFESSOR_LOGADO = "professor";
	
	public static final String INFO = "info";
	
	public static final String ERRO = "erro";
	
	
	/** Páginas */
	
	public static final String PAGINA_CADASTRAR_PROJETO = "projeto/cadastrar";
	
	public static final String PAGINA_LISTAR_PROFESSORES = "admin/professores";
	
	public static final String PAGINA_GERENCIAR_RESERVAS = "admin/reservas";
	
	public static final String PAGINA_LISTAR_PERIODOS = "admin/periodo";
	
	public static final String PAGINA_RANKING = "reserva/ranking";
	
	public static final String PAGINA_INCLUIR_RESERVA = "reserva/inclusao";
	
	public static final String PAGINA_LISTAR_RESERVA = "reserva/lista";
	
	
	
	/** Redirecionamentos */
	
	public static final String REDIRECT_PAGINA_LISTAR_PROJETO = "redirect:/projeto/listar";
	
	public static final String REDIRECT_PAGINA_GERENCIAR_RESERVAS = "redirect:/administracao/reservas";
	
	public static final String REDIRECT_PAGINA_INCLUIR_RESERVAS = "redirect:/reserva/incluir";
	
	public static final String REDIRECT_PAGINA_LISTAR_RESERVAS = "redirect:/reserva/listar";
	
	public static final String REDIRECT_PAGINA_LISTAR_PERIODOS = "redirect:/administracao/periodo";
	
	
	/** Mensagens */
	
	public static final String MSG_RESERVAS_ATUALIZADAS = "Reservas atualizadas com sucesso.";
	
	public static final String MSG_RESERVA_INCLUIDA = "Reserva incluída com sucesso.";
	
	public static final String MSG_RESERVA_EXCLUIDA = "Reserva excluída com sucesso.";
	
	public static final String MSG_CAMPOS_OBRIGATORIOS = "É necessário preencher todas as informações.";
	
	public static final String MSG_SOLICITACAO_FORA_DO_PRAZO = "Sua solicitação está fora do prazo permitido.";
	
	public static final String MSG_TEMPO_MAXIMO_MESTRADO = "O tempo máximo para mestrado ou pós-doutorado é de 4 semestres.";
	
	public static final String MSG_TEMPO_MAXIMO_DOUTORADO = "O tempo máximo para doutorado é de 8 semestres.";
	
	public static final String MSG_RESERVA_EM_ABERTO = "Já há uma solicitação de reserva em aberto.";
	
	public static final String MSG_PERMISSAO_NEGADA = "Você não tem permissão para executar essa ação.";
	
	public static final String MSG_DATA_FUTURA = "Informe uma data futura.";
	
	public static final String MSG_ERRO_ATUALIZAR_PERIODO = "Ocorreu um erro na atualização do período.";
	
	
}
