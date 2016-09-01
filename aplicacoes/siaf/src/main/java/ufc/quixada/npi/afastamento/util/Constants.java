package ufc.quixada.npi.afastamento.util;

public class Constants {
	
	public static final String USUARIO_LOGADO = "usuario";
	
	public static final String PROFESSOR_LOGADO = "professor";
	
	public static final String INFO = "info";
	
	public static final String ERRO = "erro";
	
	public static final String SUCESSO = "sucesso";
	
	
	/** Páginas */
	
	public static final String PAGINA_LISTAR_PROFESSORES = "admin/professores";
	
	public static final String PAGINA_HOMOLOGAR_RESERVAS = "admin/homologacao";
	
	public static final String PAGINA_LISTAR_PERIODOS = "admin/periodos";
	
	public static final String PAGINA_RANKING = "reserva/ranking";
	
	public static final String PAGINA_PREVIA_RANKING = "reserva/previaRanking";
	
	public static final String PAGINA_INCLUIR_RESERVA = "reserva/incluir";
	
	public static final String PAGINA_EDITAR_RESERVA = "reserva/editar";
	
	public static final String PAGINA_MINHAS_RESERVAS = "reserva/minhasReservas";
	
	public static final String PAGINA_LISTAR_RESERVAS = "reserva/listar";
	
	public static final String PAGINA_ADMIN_EDITAR_RESERVA = "admin/editarReserva";
	
	public static final String PAGINA_DETALHE_RESERVA = "reserva/detalhes";
	
	public static final String PAGINA_EDITAR_ADMISSAO = "admin/editarAdmissao";
	
	
	
	/** Redirecionamentos */
	
	public static final String REDIRECT_PAGINA_LISTAR_PROJETO = "redirect:/projeto/listar";
	
	public static final String REDIRECT_PAGINA_HOMOLOGAR_RESERVAS = "redirect:/administracao/homologacao";
	
	public static final String REDIRECT_PAGINA_INCLUIR_RESERVAS = "redirect:/docente/incluir";
	
	public static final String REDIRECT_PAGINA_MINHAS_RESERVAS = "redirect:/docente/minhas-reservas";
	
	public static final String REDIRECT_PAGINA_EDITAR_RESERVAS = "redirect:/docente/editar";
	
	public static final String REDIRECT_PAGINA_LISTAR_PERIODOS = "redirect:/administracao/periodos";
	
	public static final String REDIRECT_PAGINA_LISTAR_PROFESSORES = "redirect:/administracao/professores";
	
	public static final String REDIRECT_PAGINA_LISTAR_RESERVAS = "redirect:/reserva/listar";
	
	public static final String REDIRECT_PAGINA_ADMIN_EDITAR_RESERVA = "redirect:/administracao/editar-reserva";
	
	
	/** Mensagens */
	
	public static final String MSG_RESERVAS_ATUALIZADAS = "Reservas atualizadas com sucesso.";
	
	public static final String MSG_RESERVA_INCLUIDA = "Reserva incluída com sucesso.";
	
	public static final String MSG_RESERVA_ATUALIZADA = "Reserva atualizada com sucesso.";
	
	public static final String MSG_RESERVA_EXCLUIDA = "Reserva excluída com sucesso.";
	
	public static final String MSG_RESERVA_CANCELADA = "Reserva cancelada com sucesso.";
	
	public static final String MSG_CAMPOS_OBRIGATORIOS = "É necessário preencher todas as informações.";
	
	public static final String MSG_PERIODO_INVALIDO = "O período de término do afastamento deve ser posterior ao período de início.";
	
	public static final String MSG_SOLICITACAO_FORA_DO_PRAZO = "Sua solicitação está fora do prazo permitido.";
	
	public static final String MSG_TEMPO_MAXIMO_MESTRADO = "O tempo máximo para mestrado ou pós-doutorado é de 4 semestres.";
	
	public static final String MSG_TEMPO_MAXIMO_DOUTORADO = "O tempo máximo para doutorado é de 8 semestres.";
	
	public static final String MSG_RESERVA_EM_ESPERA = "Já há uma solicitação de reserva em espera.";
	
	public static final String MSG_PERMISSAO_NEGADA = "Você não tem permissão para executar essa ação.";
	
	public static final String MSG_DATA_FUTURA = "Informe uma data futura.";
	
	public static final String MSG_ERRO_ATUALIZAR_PERIODO = "Ocorreu um erro na atualização do período.";
	
	public static final String MSG_RESERVA_HOMOLOGADA = "Reserva homologada com sucesso.";
	
	public static final String MSG_PERIODO_ATUALIZADO = "Período atualizado com sucesso.";
	
	public static final String MSG_LISTA_PROFESSORES_ATUALIZADO = "Lista de professores atualizada com sucesso.";
	
	public static final String MSG_CANCELAMENTO_AUTOMATICO = "Existia uma reserva em espera para ser incluída no ranking. Só é permitido ter uma única reserva no ranking.";
	
	
	/** Afiliações */

	public static final String AFFILIATION_DOCENTE = "DOCENTE";
	
	public static final String AFFILIATION_ADMIN_SIAF = "ADMINISTRACAO";
	
}
