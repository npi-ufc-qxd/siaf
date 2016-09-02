package ufc.quixada.npi.afastamento.service.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.MessagingException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import br.ufc.quixada.npi.model.Email;
import br.ufc.quixada.npi.service.EmailService;
import ufc.quixada.npi.afastamento.model.Acao;
import ufc.quixada.npi.afastamento.model.Historico;
import ufc.quixada.npi.afastamento.model.Notificacao;
import ufc.quixada.npi.afastamento.model.Reserva;
import ufc.quixada.npi.afastamento.model.StatusReserva;
import ufc.quixada.npi.afastamento.service.NotificacaoService;
import ufc.quixada.npi.afastamento.service.ReservaService;

@Named
public class NotificacaoServiceImpl implements NotificacaoService {

	@Inject
	private EmailService emailService;
	
	@Inject
	private ReservaService reservaService;

	private String ASSUNTO = "email.assunto";
	
	private String RESERVA_INCLUIDA = "email.corpo.reserva_incluida";
	private String RESERVA_INCLUIDA_RANKING = "email.corpo.reserva_incluida_ranking";
	private String RESERVA_EXCLUIDA = "email.corpo.reserva_excluida";
	private String RESERVA_ALTERADA = "email.corpo.reserva_alterada";
	private String RESERVA_CANCELADA = "email.corpo.reserva_cancelada";
	private String RESERVA_HOMOLOGADA = "email.corpo.reserva_homologada";
	private String ADMISSAO_ALTERADA = "email.corpo.admissao_alterada";
	
	private String PROFESSOR = "#PROFESSOR#";
	private String INICIO_PERIODO = "#INICIO_PERIODO#";
	private String TERMINO_PERIODO = "#TERMINO_PERIODO#";
	private String PROGRAMA = "#PROGRAMA#";
	private String CONCEITO = "#CONCEITO#";
	private String INSTITUICAO = "#INSTITUICAO#";
	private String EXCLUSAO = "#EXCLUSAO#";
	private String INCLUSAO = "#INCLUSAO#";
	private String ALTERACAO = "#ALTERACAO#";
	private String STATUS = "#STATUS#";
	private String ANO_ADMISSAO = "#ANO_ADMISSAO#";
	private String SEMESTRE_ADMISSAO = "#SEMESTRE_ADMISSAO#";
	private String SOLICITACAO = "#SOLICITACAO#";
	private String CANCELAMENTO = "#CANCELAMENTO#";
	private String AUTOR = "#AUTOR#";
	private String MOTIVO = "#MOTIVO#";
	
	@Override
	public void notificar(Reserva res, Notificacao notificacao, String autorAcao) {
		
		Resource resource = new ClassPathResource("/email.properties");
		try {
			final Properties properties = PropertiesLoaderUtils.loadProperties(resource);
		
			final Reserva reserva = res;
			final Notificacao tipoNotificacao = notificacao;
			final String autor = autorAcao;
	
			if (properties.getProperty("email.ativo").equals("true")) {
				
				Runnable enviarEmail = new Runnable() {
					@Override
					public void run() {
						try {
							Email email = new Email();
							email.setFrom("naoresponda@siaf.com");
							email.setSubject(properties.getProperty(ASSUNTO));
							
							String inicioPeriodo = reserva.getAnoInicio() + "."	+ reserva.getSemestreInicio();
							String terminoPeriodo = reserva.getAnoTermino() + "." + reserva.getSemestreTermino();
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				
							if (tipoNotificacao == Notificacao.RESERVA_INCLUIDA) {
								String texto = properties.getProperty(RESERVA_INCLUIDA).replaceAll(PROFESSOR, reserva.getProfessor().getUsuario().getNome())
										.replaceAll(INICIO_PERIODO, inicioPeriodo).replaceAll(TERMINO_PERIODO, terminoPeriodo)
										.replaceAll(PROGRAMA, reserva.getPrograma().getDescricao())
										.replaceAll(CONCEITO, reserva.getConceitoPrograma().toString())
										.replaceAll(SOLICITACAO, dateFormat.format(reserva.getDataSolicitacao()));
								if (reserva.getInstituicao() == null || reserva.getInstituicao().isEmpty()) {
									texto = texto.replaceAll(INSTITUICAO, "-");
								} else {
									texto = texto.replaceAll(INSTITUICAO, reserva.getInstituicao());
								}
								email.setText(texto);
								email.setTo(reserva.getProfessor().getUsuario().getEmail());
								emailService.sendEmail(email);
							} else if (tipoNotificacao == Notificacao.RESERVA_EXCLUIDA) {
								String texto = properties.getProperty(RESERVA_EXCLUIDA).replaceAll(PROFESSOR, reserva.getProfessor().getUsuario().getNome())
										.replaceAll(INICIO_PERIODO, inicioPeriodo).replaceAll(TERMINO_PERIODO, terminoPeriodo)
										.replaceAll(PROGRAMA, reserva.getPrograma().getDescricao())
										.replaceAll(CONCEITO, reserva.getConceitoPrograma().toString())
										.replaceAll(EXCLUSAO, dateFormat.format(new Date()))
										.replaceAll(SOLICITACAO, dateFormat.format(reserva.getDataSolicitacao()));
								if (reserva.getInstituicao() == null || reserva.getInstituicao().isEmpty()) {
									texto = texto.replaceAll(INSTITUICAO, "-");
								} else {
									texto = texto.replaceAll(INSTITUICAO, reserva.getInstituicao());
								}
								texto = texto.replaceAll(AUTOR, autor);
								email.setText(texto);
								email.setTo(reserva.getProfessor().getUsuario().getEmail());
								emailService.sendEmail(email);
							} else if (tipoNotificacao == Notificacao.RESERVA_ALTERADA) {
								String texto = properties.getProperty(RESERVA_ALTERADA).replaceAll(PROFESSOR, reserva.getProfessor().getUsuario().getNome())
										.replaceAll(INICIO_PERIODO, inicioPeriodo).replaceAll(TERMINO_PERIODO, terminoPeriodo)
										.replaceAll(PROGRAMA, reserva.getPrograma().getDescricao())
										.replaceAll(CONCEITO, reserva.getConceitoPrograma().toString())
										.replaceAll(ALTERACAO, dateFormat.format(new Date()))
										.replaceAll(SOLICITACAO, dateFormat.format(reserva.getDataSolicitacao()));
								if (reserva.getInstituicao() == null || reserva.getInstituicao().isEmpty()) {
									texto = texto.replaceAll(INSTITUICAO, "-");
								} else {
									texto = texto.replaceAll(INSTITUICAO, reserva.getInstituicao());
								}
								texto = texto.replaceAll(AUTOR, autor);
								email.setText(texto);
								email.setTo(reserva.getProfessor().getUsuario().getEmail());
								emailService.sendEmail(email);
							} else if (tipoNotificacao == Notificacao.RESERVA_INCLUIDA_RANKING) {
								Historico historico = reservaService.getUltimaAcao(reserva, Acao.INCLUSAO_RANKING);
								String texto = properties.getProperty(RESERVA_INCLUIDA_RANKING).replaceAll(PROFESSOR, reserva.getProfessor().getUsuario().getNome())
										.replaceAll(INICIO_PERIODO, inicioPeriodo).replaceAll(TERMINO_PERIODO, terminoPeriodo)
										.replaceAll(PROGRAMA, reserva.getPrograma().getDescricao())
										.replaceAll(CONCEITO, reserva.getConceitoPrograma().toString())
										.replaceAll(INCLUSAO, dateFormat.format(historico.getData()))
										.replaceAll(SOLICITACAO, dateFormat.format(reserva.getDataSolicitacao()));
								if (reserva.getInstituicao() == null || reserva.getInstituicao().isEmpty()) {
									texto = texto.replaceAll(INSTITUICAO, "-");
								} else {
									texto = texto.replaceAll(INSTITUICAO, reserva.getInstituicao());
								}
								email.setText(texto);
								email.setTo(reserva.getProfessor().getUsuario().getEmail());
								emailService.sendEmail(email);
							} else if (tipoNotificacao == Notificacao.RESERVA_CANCELADA) {
								Historico historico = reservaService.getUltimaAcao(reserva, Acao.CANCELAMENTO);
								String texto = properties.getProperty(RESERVA_CANCELADA).replaceAll(PROFESSOR, reserva.getProfessor().getUsuario().getNome())
										.replaceAll(INICIO_PERIODO, inicioPeriodo).replaceAll(TERMINO_PERIODO, terminoPeriodo)
										.replaceAll(PROGRAMA, reserva.getPrograma().getDescricao())
										.replaceAll(CONCEITO, reserva.getConceitoPrograma().toString())
										.replaceAll(CANCELAMENTO, dateFormat.format(historico.getData()))
										.replaceAll(MOTIVO, historico.getComentario())
										.replaceAll(SOLICITACAO, dateFormat.format(reserva.getDataSolicitacao()));
								if (reserva.getInstituicao() == null || reserva.getInstituicao().isEmpty()) {
									texto = texto.replaceAll(INSTITUICAO, "-");
								} else {
									texto = texto.replaceAll(INSTITUICAO, reserva.getInstituicao());
								}
								texto = texto.replaceAll(AUTOR, autor);
								email.setText(texto);
								email.setTo(reserva.getProfessor().getUsuario().getEmail());
								emailService.sendEmail(email);
							} else if (tipoNotificacao == Notificacao.RESERVA_HOMOLOGADA) {
								String texto = properties.getProperty(RESERVA_HOMOLOGADA).replaceAll(PROFESSOR, reserva.getProfessor().getUsuario().getNome())
										.replaceAll(INICIO_PERIODO, inicioPeriodo).replaceAll(TERMINO_PERIODO, terminoPeriodo)
										.replaceAll(PROGRAMA, reserva.getPrograma().getDescricao())
										.replaceAll(CONCEITO, reserva.getConceitoPrograma().toString())
										.replaceAll(STATUS, reserva.getStatus().getDescricao())
										.replaceAll(SOLICITACAO, dateFormat.format(reserva.getDataSolicitacao()));
								if (reserva.getInstituicao() == null || reserva.getInstituicao().isEmpty()) {
									texto = texto.replaceAll(INSTITUICAO, "-");
								} else {
									texto = texto.replaceAll(INSTITUICAO, reserva.getInstituicao());
								}
								if (reserva.getStatus().isCancelado()) {
									Acao acao = Acao.CANCELAMENTO;
									if(reserva.getStatus().equals(StatusReserva.CANCELADO_COM_PUNICAO)) {
										acao = Acao.CANCELAMENTO_COM_PUNICAO;
									} else if (reserva.getStatus().equals(StatusReserva.NEGADO)) {
										acao = Acao.NEGACAO;
									}
									Historico historico = reservaService.getUltimaAcao(reserva, acao);
									texto = texto.replaceAll(CANCELAMENTO, "<p>Data de cancelamento: " + 
											dateFormat.format(historico.getData()) + "</p>" +
											"<p>Motivo do cancelamento: " + historico.getComentario() + "</p>");
								} else {
									texto = texto.replaceAll(CANCELAMENTO, "");
								}
								email.setText(texto);
								email.setTo(reserva.getProfessor().getUsuario().getEmail());
								emailService.sendEmail(email);
							} else if (tipoNotificacao == Notificacao.ADMISSAO_ALTERADA) {
								String texto = properties.getProperty(ADMISSAO_ALTERADA).replaceAll(PROFESSOR, reserva.getProfessor().getUsuario().getNome())
										.replaceAll(ANO_ADMISSAO, reserva.getProfessor().getAnoAdmissao().toString())
										.replaceAll(SEMESTRE_ADMISSAO, reserva.getProfessor().getSemestreAdmissao().toString());
								email.setText(texto);
								email.setTo(reserva.getProfessor().getUsuario().getEmail());
								emailService.sendEmail(email);
							}
						} catch (MessagingException e) {
							e.printStackTrace();
						}
					}
				};
				Thread threadEnviarEmail = new Thread(enviarEmail);
				threadEnviarEmail.start();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

}
