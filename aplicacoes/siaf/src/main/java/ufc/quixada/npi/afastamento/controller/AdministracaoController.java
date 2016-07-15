package ufc.quixada.npi.afastamento.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.ufc.quixada.npi.ldap.model.Usuario;
import br.ufc.quixada.npi.ldap.service.UsuarioService;
import ufc.quixada.npi.afastamento.model.Acao;
import ufc.quixada.npi.afastamento.model.Afastamento;
import ufc.quixada.npi.afastamento.model.AutorAcao;
import ufc.quixada.npi.afastamento.model.Notificacao;
import ufc.quixada.npi.afastamento.model.Periodo;
import ufc.quixada.npi.afastamento.model.Professor;
import ufc.quixada.npi.afastamento.model.Programa;
import ufc.quixada.npi.afastamento.model.Ranking;
import ufc.quixada.npi.afastamento.model.Reserva;
import ufc.quixada.npi.afastamento.model.StatusReserva;
import ufc.quixada.npi.afastamento.model.TuplaRanking;
import ufc.quixada.npi.afastamento.service.AfastamentoService;
import ufc.quixada.npi.afastamento.service.NotificacaoService;
import ufc.quixada.npi.afastamento.service.PeriodoService;
import ufc.quixada.npi.afastamento.service.ProfessorService;
import ufc.quixada.npi.afastamento.service.RankingService;
import ufc.quixada.npi.afastamento.service.ReservaService;
import ufc.quixada.npi.afastamento.util.Constants;

@Controller
@RequestMapping("administracao")
public class AdministracaoController {

	@Inject
	private ProfessorService professorService;

	@Inject
	private RankingService rankingService;

	@Inject
	private PeriodoService periodoService;

	@Inject
	private ReservaService reservaService;

	@Inject
	private UsuarioService usuarioService;

	@Inject
	private NotificacaoService notificacaoService;
	
	@Inject
	private AfastamentoService afastamentoService;

	@RequestMapping(value = "/professores", method = RequestMethod.GET)
	public String listarProfessores(Model model) {
		model.addAttribute("professores", professorService.findAtivos());
		return Constants.PAGINA_LISTAR_PROFESSORES;
	}

	@RequestMapping(value = "/atualizar-professores", method = RequestMethod.GET)
	public String atualizaProfessores(Model model, RedirectAttributes redirect, HttpSession session) {

		List<Usuario> usuarios = usuarioService.getByAffiliation(Constants.AFFILIATION_DOCENTE);
		for (Usuario usuario : usuarios) {
			Professor professor = professorService.getByCpf(usuario.getCpf());
			if (professor == null) {
				professor = new Professor();
				professor.setCpf(usuario.getCpf());
				professorService.save(professor);
				Reserva reserva = new Reserva();
				reserva.setProfessor(professor);
			}
		}

		atualizaVagas();
		redirect.addFlashAttribute(Constants.INFO, Constants.MSG_LISTA_PROFESSORES_ATUALIZADO);
		return Constants.REDIRECT_PAGINA_LISTAR_PROFESSORES;
	}
	
	@RequestMapping(value = "/homologacao", method = RequestMethod.GET)
	public String getHomologacao(Model model) {
		Periodo periodo = periodoService.getPeriodoAtual();
		if (periodo != null) {
			periodo = periodoService.getPeriodoPosterior(periodo);
			Ranking ranking = new Ranking();
			ranking.setPeriodo(periodo);
			List<TuplaRanking> tuplas = new ArrayList<TuplaRanking>();
			for(TuplaRanking tupla : rankingService.getRanking(periodo, false)) {
				if (tupla.getReserva().getStatus().equals(StatusReserva.ABERTO)) {
					if(tupla.getReserva().getAnoInicio().equals(periodo.getAno())
						&& tupla.getReserva().getSemestreInicio().equals(periodo.getSemestre())) {
						tuplas.add(tupla);
					}
				} else {
					tuplas.add(tupla);
				}
			}
			ranking.setTuplas(tuplas);

			List<TuplaRanking> tuplasCanceladasNegadas = rankingService.getTuplas(
					Arrays.asList(StatusReserva.CANCELADO, StatusReserva.CANCELADO_COM_PUNICAO, StatusReserva.NEGADO), periodo);

			model.addAttribute("tuplasCanceladasNegadas", tuplasCanceladasNegadas);
			model.addAttribute("ranking", ranking);
		}

		return Constants.PAGINA_HOMOLOGAR_RESERVAS;
	}
	
	@RequestMapping(value = "/homologar-reserva", method = RequestMethod.POST)
	public String atualizarStatusReserva(@RequestParam("idReserva") Long id, @RequestParam("status") String status, 
			@RequestParam(value = "motivo", required = false) String motivo, Model model, RedirectAttributes redirect) {
	
		Reserva reserva = reservaService.find(Reserva.class, id);
		StatusReserva statusReserva = StatusReserva.valueOf(status);
		if(reserva.getStatus().equals(StatusReserva.AFASTADO) && !statusReserva.equals(StatusReserva.AFASTADO)) {
			afastamentoService.delete(afastamentoService.getByReserva(reserva));
		}
		if (statusReserva.equals(StatusReserva.AFASTADO) && !reserva.getStatus().equals(StatusReserva.AFASTADO)) {
			Afastamento afastamento = new Afastamento(reserva);
			afastamentoService.save(afastamento);
		}
		
		reserva.setStatus(statusReserva);
		reservaService.update(reserva);
		Acao acao = Acao.getByStatusReserva(statusReserva);
		if (acao != null) {
			reservaService.salvarHistorico(reserva, acao, AutorAcao.ADMINISTRADOR, motivo);
		}
		notificacaoService.notificar(reserva, Notificacao.RESERVA_HOMOLOGADA, AutorAcao.ADMINISTRADOR);
		redirect.addFlashAttribute(Constants.INFO, Constants.MSG_RESERVA_HOMOLOGADA);
		return Constants.REDIRECT_PAGINA_HOMOLOGAR_RESERVAS;
	}
	
	
	

	@RequestMapping(value = "/periodos", method = RequestMethod.GET)
	public String listarPeriodos(Model model) {
		model.addAttribute("periodos", periodoService.find(Periodo.class));
		model.addAttribute("periodoAtual", periodoService.getPeriodoAtual());
		return Constants.PAGINA_LISTAR_PERIODOS;
	}
	
	@RequestMapping(value = "/atualizar-periodo", method = RequestMethod.POST)
	public String atualizarPeriodo(@RequestParam("periodoId") Long periodoId, @RequestParam("encerramento") String encerramento, 
			@RequestParam("vagas") Integer vagas, RedirectAttributes redirect, Model model) {
		SimpleDateFormat format = new SimpleDateFormat(br.ufc.quixada.npi.ldap.model.Constants.FORMATO_DATA_NASCIMENTO);
		Date dataEncerramento = null;
		try {
			if (encerramento != null && !encerramento.isEmpty()) {
				dataEncerramento = format.parse(encerramento);
				if(encerramento != null) {
					Date today = format.parse(format.format(new Date()));
					if (dataEncerramento.before(today)) {
						redirect.addFlashAttribute(Constants.ERRO, Constants.MSG_DATA_FUTURA);
						return Constants.REDIRECT_PAGINA_LISTAR_PERIODOS;
					}
				}
			}
		} catch (ParseException e) {
			redirect.addFlashAttribute(Constants.ERRO, Constants.MSG_ERRO_ATUALIZAR_PERIODO);
			return Constants.REDIRECT_PAGINA_LISTAR_PERIODOS;
		}
		
		Periodo periodo = periodoService.find(Periodo.class, periodoId);
		periodo.setEncerramento(dataEncerramento);
		if (vagas != null) {
			periodo.setVagas(vagas);
		}
		
		periodoService.update(periodo);
		redirect.addFlashAttribute(Constants.INFO, Constants.MSG_PERIODO_ATUALIZADO);
		
		return Constants.REDIRECT_PAGINA_LISTAR_PERIODOS;
	}
	
	@RequestMapping(value = "/editar-admissao/{id}", method = RequestMethod.GET)
	public String editarAdmissao(@PathVariable("id") Long id, Model model) {
		Professor professor = professorService.find(Professor.class, id);
		if(professor == null) {
			model.addAttribute(Constants.ERRO, Constants.MSG_PERMISSAO_NEGADA);
			model.addAttribute("professores", professorService.findAtivos());
			return Constants.PAGINA_LISTAR_PROFESSORES;
		}
		model.addAttribute("professor", professor);
		return Constants.PAGINA_EDITAR_ADMISSAO;
	}

	@RequestMapping(value = "/editar-admissao", method = RequestMethod.POST)
	public String editarAdmissao(@RequestParam("id") Long id, @RequestParam("ano") Integer ano,
			@RequestParam("semestre") Integer semestre, Model model) {

		if (id == null || ano == null || semestre == null) {
			model.addAttribute(Constants.ERRO, Constants.MSG_CAMPOS_OBRIGATORIOS);
			return Constants.PAGINA_EDITAR_ADMISSAO;
		}

		Professor professor = professorService.find(Professor.class, id);
		professor.setAnoAdmissao(ano);
		professor.setSemestreAdmissao(semestre);

		professorService.update(professor);

		model.addAttribute("professores", professorService.findAtivos());
		model.addAttribute("info", "Data de admissão do(a) Prof(a) " + professor.getNome() + " atualizada com sucesso.");

		Reserva reserva = new Reserva();
		reserva.setProfessor(professor);
		notificacaoService.notificar(reserva, Notificacao.ADMISSAO_ALTERADA, AutorAcao.ADMINISTRADOR);
		
		return Constants.PAGINA_LISTAR_PROFESSORES;
	}
	
	@RequestMapping(value = "/editar-reserva/{id}", method = RequestMethod.GET)
	public String editarReserva(@PathVariable("id") Long id, Model model, HttpSession session, RedirectAttributes redirect) {
		Reserva reserva = reservaService.find(Reserva.class, id);
		if (reserva == null || (!reserva.getStatus().equals(StatusReserva.ABERTO) && !reserva.getStatus().equals(StatusReserva.EM_ESPERA))) {
			redirect.addFlashAttribute(Constants.ERRO, Constants.MSG_PERMISSAO_NEGADA);
			return Constants.REDIRECT_PAGINA_LISTAR_RESERVAS;
		}
		model.addAttribute("reserva", reserva);
		model.addAttribute("programa", Programa.values());
		return Constants.PAGINA_ADMIN_EDITAR_RESERVA;
	}
	
	@RequestMapping(value = "/editar-reserva", method = RequestMethod.POST)
	public String editarReserva(@ModelAttribute("reserva") Reserva reserva, Model model, RedirectAttributes redirect, HttpSession session) {
		
		Reserva reservaAtual = reservaService.find(Reserva.class, reserva.getId());
		reservaAtual.setAnoInicio(reserva.getAnoInicio());
		reservaAtual.setSemestreInicio(reserva.getSemestreInicio());
		reservaAtual.setAnoTermino(reserva.getAnoTermino());
		reservaAtual.setSemestreTermino(reserva.getSemestreTermino());
		reservaAtual.setConceitoPrograma(reserva.getConceitoPrograma());
		reservaAtual.setInstituicao(reserva.getInstituicao());
		reservaAtual.setPrograma(reserva.getPrograma());
		
		model.addAttribute("reserva", reservaAtual);
		model.addAttribute("programa", Programa.values());

		if (reservaAtual.getAnoInicio() == null || reservaAtual.getAnoTermino() == null) {
			model.addAttribute(Constants.ERRO, Constants.MSG_CAMPOS_OBRIGATORIOS);
			return Constants.PAGINA_EDITAR_RESERVA;
		}
		if (reservaAtual.getAnoTermino() < reservaAtual.getAnoInicio() || (reservaAtual.getAnoInicio().equals(reservaAtual.getAnoTermino()) 
				&& reservaAtual.getSemestreTermino() < reservaAtual.getSemestreInicio())) {
			model.addAttribute(Constants.ERRO, Constants.MSG_PERIODO_INVALIDO);
			return Constants.PAGINA_EDITAR_RESERVA;
		}

		Periodo periodo = periodoService.getPeriodoAtual();
		Integer diferenca = calculaSemestres(periodo.getAno(), periodo.getSemestre(), reservaAtual.getAnoInicio(), reservaAtual.getSemestreInicio());

		if (diferenca < 2) {
			model.addAttribute(Constants.ERRO, Constants.MSG_SOLICITACAO_FORA_DO_PRAZO);
			return Constants.PAGINA_EDITAR_RESERVA;
		}
		if ((reservaAtual.getPrograma() == Programa.MESTRADO || reservaAtual.getPrograma() == Programa.POS_DOUTORADO)
				&& calculaSemestres(reservaAtual.getAnoInicio(), reservaAtual.getSemestreInicio(), reservaAtual.getAnoTermino(), reservaAtual.getSemestreTermino()) + 1 > 4) {
			model.addAttribute(Constants.ERRO, Constants.MSG_TEMPO_MAXIMO_MESTRADO);
			return Constants.PAGINA_EDITAR_RESERVA;
		}
		if (reservaAtual.getPrograma() == Programa.DOUTORADO && calculaSemestres(reservaAtual.getAnoInicio(), 
				reservaAtual.getSemestreInicio(), reservaAtual.getAnoTermino(), reservaAtual.getSemestreTermino()) + 1 > 8) {
			model.addAttribute(Constants.ERRO, Constants.MSG_TEMPO_MAXIMO_DOUTORADO);
			return Constants.PAGINA_EDITAR_RESERVA;
		}
		
		reservaService.atualizar(reservaAtual);
		reservaService.salvarHistorico(reservaAtual, Acao.EDICAO, AutorAcao.ADMINISTRADOR, null);
		
		notificacaoService.notificar(reservaAtual, Notificacao.RESERVA_ALTERADA, AutorAcao.ADMINISTRADOR);

		redirect.addFlashAttribute(Constants.INFO, Constants.MSG_RESERVA_ATUALIZADA);
		
		return Constants.REDIRECT_PAGINA_LISTAR_RESERVAS;
	}
	
	@RequestMapping(value = "/excluir-reserva/{id}", method = RequestMethod.GET)
	public String excluir(@PathVariable("id") Long id, RedirectAttributes redirect) {
		Reserva reserva = reservaService.getReservaById(id);
		if (reserva == null || !reserva.getStatus().equals(StatusReserva.EM_ESPERA)) {
			redirect.addFlashAttribute(Constants.ERRO, Constants.MSG_PERMISSAO_NEGADA);
		} else {
			reservaService.delete(reserva);
			notificacaoService.notificar(reserva, Notificacao.RESERVA_EXCLUIDA, AutorAcao.ADMINISTRADOR);
			redirect.addFlashAttribute(Constants.INFO, Constants.MSG_RESERVA_EXCLUIDA);
		}
		return Constants.REDIRECT_PAGINA_LISTAR_RESERVAS;
	}
	
	@RequestMapping(value = "/detalhe-reserva/{id}", method = RequestMethod.GET)
	public String verDetalhes(@PathVariable("id") Long id, Model model) {
		Reserva reserva = reservaService.getReservaById(id);
		if (reserva == null) {
			model.addAttribute(Constants.ERRO, Constants.MSG_PERMISSAO_NEGADA);
		} else {
			model.addAttribute("reserva", reserva);
		}
		return Constants.PAGINA_DETALHE_RESERVA;
	}
	
	@RequestMapping(value = "/cancelar-reserva", method = RequestMethod.POST)
	public String cancelar(@RequestParam("id") Long id, @RequestParam("motivo") String motivo, HttpSession session, RedirectAttributes redirect) {
		Reserva reserva = reservaService.getReservaById(id);
		if (reserva == null || !reserva.getStatus().equals(StatusReserva.ABERTO)) {
			redirect.addFlashAttribute(Constants.ERRO, Constants.MSG_PERMISSAO_NEGADA);
		} else {
			reserva.setStatus(StatusReserva.CANCELADO);
			reservaService.update(reserva);
			reservaService.salvarHistorico(reserva, Acao.CANCELAMENTO, AutorAcao.ADMINISTRADOR, motivo);
			notificacaoService.notificar(reserva, Notificacao.RESERVA_CANCELADA, AutorAcao.ADMINISTRADOR);
			redirect.addFlashAttribute(Constants.INFO, Constants.MSG_RESERVA_CANCELADA);
		}
		return Constants.REDIRECT_PAGINA_LISTAR_RESERVAS;
	}

	private Integer calculaSemestres(Integer anoInicio, Integer semestreInicio, Integer anoTermino, Integer semestreTermino) {
		return ((anoTermino - anoInicio) * 2) + (semestreTermino - semestreInicio);
	}
	
	private void atualizaVagas() {
		Periodo periodoAtual = periodoService.getPeriodoPosterior(periodoService.getPeriodoPosterior(periodoService.getPeriodoAtual()));
		List<Periodo> periodos = periodoService.getPeriodosPosteriores(periodoAtual);
		int vagas = (int) (professorService.findAtivos().size() * 0.15);
		for (Periodo periodo : periodos) {
			periodo.setVagas(vagas);
			periodoService.update(periodo);
		}
	}

}
