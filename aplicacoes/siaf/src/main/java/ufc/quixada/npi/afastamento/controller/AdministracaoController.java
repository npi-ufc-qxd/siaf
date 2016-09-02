package ufc.quixada.npi.afastamento.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ufc.quixada.npi.afastamento.model.Acao;
import ufc.quixada.npi.afastamento.model.AutorAcao;
import ufc.quixada.npi.afastamento.model.Notificacao;
import ufc.quixada.npi.afastamento.model.Periodo;
import ufc.quixada.npi.afastamento.model.Professor;
import ufc.quixada.npi.afastamento.model.Programa;
import ufc.quixada.npi.afastamento.model.Reserva;
import ufc.quixada.npi.afastamento.model.StatusReserva;
import ufc.quixada.npi.afastamento.model.TuplaRanking;
import ufc.quixada.npi.afastamento.service.NotificacaoService;
import ufc.quixada.npi.afastamento.service.PeriodoService;
import ufc.quixada.npi.afastamento.service.ProfessorService;
import ufc.quixada.npi.afastamento.service.RankingService;
import ufc.quixada.npi.afastamento.service.ReservaService;
import ufc.quixada.npi.afastamento.util.Constants;
import ufc.quixada.npi.afastamento.util.SiafException;

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
	private NotificacaoService notificacaoService;

	@RequestMapping(value = "/professores", method = RequestMethod.GET)
	public String listarProfessores(Model model) {
		model.addAttribute("professores", professorService.findAll());
		return Constants.PAGINA_LISTAR_PROFESSORES;
	}

	/*@RequestMapping(value = "/atualizar-professores", method = RequestMethod.GET)
	public String atualizaProfessores(Model model, RedirectAttributes redirect, HttpSession session) {

		List<Usuario> usuarios = usuarioService.getByAffiliation(Constants.AFFILIATION_DOCENTE);
		for (Usuario usuario : usuarios) {
			Professor professor = professorService.findByCpf(usuario.getCpf());
			if (professor == null) {
				ufc.quixada.npi.afastamento.model.Usuario usuarioSiaf = new ufc.quixada.npi.afastamento.model.Usuario();
				usuarioSiaf.setCpf(usuario.getCpf());
				usuarioSiaf.setHabilitado(true);
				professor = new Professor();
				professor.setUsuario(usuarioSiaf);
				professorService.salvar(professor);
			}
		}

		atualizaVagas();
		redirect.addFlashAttribute(Constants.INFO, Constants.MSG_LISTA_PROFESSORES_ATUALIZADO);
		return Constants.REDIRECT_PAGINA_LISTAR_PROFESSORES;
	}*/
	
	@RequestMapping(value = "/homologacao", method = RequestMethod.GET)
	public String getHomologacao(Model model) {
		Periodo periodo = periodoService.getPeriodoAtual();
		if (periodo != null) {
			periodo = periodoService.getPeriodoPosterior(periodo);
			List<TuplaRanking> tuplasCanceladasNegadas = rankingService.getTuplas(
					Arrays.asList(StatusReserva.CANCELADO, StatusReserva.CANCELADO_COM_PUNICAO, StatusReserva.NEGADO), periodo);

			model.addAttribute("tuplasCanceladasNegadas", tuplasCanceladasNegadas);
			model.addAttribute("ranking", rankingService.getRankingHomologacao(periodo));
		}

		return Constants.PAGINA_HOMOLOGAR_RESERVAS;
	}
	
	@RequestMapping(value = "/homologar-reserva", method = RequestMethod.POST)
	public String atualizarStatusReserva(@RequestParam("idReserva") Long id, @RequestParam("status") String status, 
			@RequestParam(value = "motivo", required = false) String motivo, Model model, RedirectAttributes redirect) {
	
		Reserva reserva = reservaService.getReservaById(id);
		StatusReserva statusReserva = StatusReserva.valueOf(status);
		reservaService.homologar(reserva, statusReserva);
		
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
		model.addAttribute("periodos", periodoService.getAll());
		model.addAttribute("periodoAtual", periodoService.getPeriodoAtual());
		return Constants.PAGINA_LISTAR_PERIODOS;
	}
	
	@RequestMapping(value = "/atualizar-periodo", method = RequestMethod.POST)
	public String atualizarPeriodo(@RequestParam("periodoId") Long periodoId, @RequestParam("encerramento") String encerramento, 
			@RequestParam("vagas") Integer vagas, RedirectAttributes redirect, Model model) {
		Periodo periodo = periodoService.findById(periodoId);
		try {
			if (encerramento != null && !encerramento.isEmpty()) {
				SimpleDateFormat format = new SimpleDateFormat(br.ufc.quixada.npi.ldap.model.Constants.FORMATO_DATA_NASCIMENTO);
				periodo.setEncerramento(format.parse(encerramento));
			}
			if (vagas != null) {
				periodo.setVagas(vagas);
			}
			periodoService.atualizar(periodo);
		} catch (ParseException e) {
			redirect.addFlashAttribute(Constants.ERRO, Constants.MSG_ERRO_ATUALIZAR_PERIODO);
			return Constants.REDIRECT_PAGINA_LISTAR_PERIODOS;
		} catch (SiafException exception) {
			redirect.addFlashAttribute(Constants.ERRO, exception.getMessage());
			return Constants.REDIRECT_PAGINA_LISTAR_PERIODOS;
		}
		
		redirect.addFlashAttribute(Constants.INFO, Constants.MSG_PERIODO_ATUALIZADO);
		return Constants.REDIRECT_PAGINA_LISTAR_PERIODOS;
	}
	
	/*@RequestMapping(value = "/editar-admissao/{id}", method = RequestMethod.GET)
	public String editarAdmissao(@PathVariable("id") Long id, Model model) {
		Professor professor = professorService.findById(id);
		if(professor == null) {
			model.addAttribute(Constants.ERRO, Constants.MSG_PERMISSAO_NEGADA);
			model.addAttribute("professores", professorService.findAll());
			return Constants.PAGINA_LISTAR_PROFESSORES;
		}
		model.addAttribute("professor", professor);
		return Constants.PAGINA_EDITAR_ADMISSAO;
	}*/

	@RequestMapping(value = "/editar-admissao", method = RequestMethod.POST)
	public String editarAdmissao(@RequestParam("id") Long id, @RequestParam("ano") Integer ano,
			@RequestParam("semestre") Integer semestre, Model model) {

		if (id == null || ano == null || semestre == null) {
			model.addAttribute(Constants.ERRO, Constants.MSG_CAMPOS_OBRIGATORIOS);
			return Constants.PAGINA_EDITAR_ADMISSAO;
		}

		Professor professor = professorService.findById(id);
		professor.setAnoAdmissao(ano);
		professor.setSemestreAdmissao(semestre);

		professorService.atualizar(professor);

		model.addAttribute("professores", professorService.findAll());
		model.addAttribute("info", "Data de admissão do(a) Prof(a) " + professor.getUsuario().getNome() + " atualizada com sucesso.");

		Reserva reserva = new Reserva();
		reserva.setProfessor(professor);
		notificacaoService.notificar(reserva, Notificacao.ADMISSAO_ALTERADA, AutorAcao.ADMINISTRADOR);
		
		return Constants.PAGINA_LISTAR_PROFESSORES;
	}
	
	@RequestMapping(value = "/editar-reserva/{id}", method = RequestMethod.GET)
	public String editarReserva(@PathVariable("id") Long id, Model model, HttpSession session, RedirectAttributes redirect) {
		Reserva reserva = reservaService.getReservaById(id);
		if (reserva == null || (!reserva.getStatus().equals(StatusReserva.ABERTO) && !reserva.getStatus().equals(StatusReserva.EM_ESPERA))) {
			redirect.addFlashAttribute(Constants.ERRO, Constants.MSG_PERMISSAO_NEGADA);
			return Constants.REDIRECT_PAGINA_LISTAR_RESERVAS;
		}
		model.addAttribute("reserva", reserva);
		model.addAttribute("programa", Programa.values());
		return Constants.PAGINA_ADMIN_EDITAR_RESERVA;
	}
	
	@RequestMapping(value = "/editar-reserva", method = RequestMethod.POST)
	public String editarReserva(@ModelAttribute("reserva") Reserva reserva, Model model, RedirectAttributes redirect, Authentication auth) {
		
		Reserva reservaAtual = reservaService.getReservaById(reserva.getId());
		Professor professor = professorService.findByCpf(auth.getName());
		if (reserva == null || !reserva.getProfessor().equals(professor)) {
			redirect.addFlashAttribute(Constants.ERRO, Constants.MSG_PERMISSAO_NEGADA);
			return Constants.REDIRECT_PAGINA_MINHAS_RESERVAS;
		}
		reservaAtual.setAnoInicio(reserva.getAnoInicio());
		reservaAtual.setSemestreInicio(reserva.getSemestreInicio());
		reservaAtual.setAnoTermino(reserva.getAnoTermino());
		reservaAtual.setSemestreTermino(reserva.getSemestreTermino());
		reservaAtual.setConceitoPrograma(reserva.getConceitoPrograma());
		reservaAtual.setInstituicao(reserva.getInstituicao());
		reservaAtual.setPrograma(reserva.getPrograma());
		
		try {
			reservaService.atualizar(reservaAtual, professor);
		} catch (SiafException exception) {
			model.addAttribute("reserva", reservaAtual);
			model.addAttribute("professor", professor);
			model.addAttribute("programa", Programa.values());
			model.addAttribute(Constants.ERRO, exception.getMessage());
			return Constants.PAGINA_EDITAR_RESERVA;
		}

		reservaService.salvarHistorico(reserva, Acao.EDICAO, AutorAcao.ADMINISTRADOR);
		notificacaoService.notificar(reserva, Notificacao.RESERVA_ALTERADA, AutorAcao.ADMINISTRADOR);
		redirect.addFlashAttribute(Constants.INFO, Constants.MSG_RESERVA_ATUALIZADA);
		return Constants.REDIRECT_PAGINA_LISTAR_RESERVAS;
		
	}
	
	@RequestMapping(value = "/excluir-reserva/{id}", method = RequestMethod.GET)
	public String excluir(@PathVariable("id") Long id, RedirectAttributes redirect) {
		Reserva reserva = reservaService.getReservaById(id);
		try {
			reservaService.excluir(reserva);
		} catch (SiafException exception) {
			redirect.addFlashAttribute(Constants.ERRO, exception.getMessage());
			return Constants.REDIRECT_PAGINA_MINHAS_RESERVAS;
		}
		notificacaoService.notificar(reserva, Notificacao.RESERVA_EXCLUIDA, AutorAcao.ADMINISTRADOR);
		redirect.addFlashAttribute(Constants.INFO, Constants.MSG_RESERVA_EXCLUIDA);
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
		try {
			reservaService.cancelar(reserva);
		} catch (SiafException exception) {
			redirect.addFlashAttribute(Constants.ERRO, exception.getMessage());
			return Constants.REDIRECT_PAGINA_MINHAS_RESERVAS;
		}
		reservaService.salvarHistorico(reserva, Acao.CANCELAMENTO, AutorAcao.ADMINISTRADOR, motivo);
		notificacaoService.notificar(reserva, Notificacao.RESERVA_CANCELADA, AutorAcao.ADMINISTRADOR);
		redirect.addFlashAttribute(Constants.INFO, Constants.MSG_RESERVA_CANCELADA);
		return Constants.REDIRECT_PAGINA_LISTAR_RESERVAS;
	}

}
