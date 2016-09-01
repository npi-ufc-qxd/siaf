package ufc.quixada.npi.afastamento.controller;

import java.util.Date;
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
import ufc.quixada.npi.afastamento.service.NotificacaoService;
import ufc.quixada.npi.afastamento.service.PeriodoService;
import ufc.quixada.npi.afastamento.service.ProfessorService;
import ufc.quixada.npi.afastamento.service.ReservaService;
import ufc.quixada.npi.afastamento.util.Constants;

@Controller
@RequestMapping("docente")
public class DocenteController {
	
	@Inject
	private PeriodoService periodoService;
	
	@Inject
	private ReservaService reservaService;
	
	@Inject
	private NotificacaoService notificacaoService;
	
	@Inject
	private ProfessorService professorService;
	
	@RequestMapping(value = "/incluir", method = RequestMethod.GET)
	public String incluirForm(Model model, Authentication auth) {
		model.addAttribute("reserva", new Reserva());
		model.addAttribute("professor", professorService.getByCpf(auth.getName()));
		model.addAttribute("programa", Programa.values());
		return Constants.PAGINA_INCLUIR_RESERVA;
	}

	@RequestMapping(value = "/incluir", method = RequestMethod.POST)
	public String incluir(@ModelAttribute("reserva") Reserva reserva, Model model, RedirectAttributes redirect, Authentication auth) {

		model.addAttribute("reserva", reserva);
		model.addAttribute("professor", professorService.getByCpf(auth.getName()));
		model.addAttribute("programa", Programa.values());

		if (reserva.getAnoInicio() == null || reserva.getAnoTermino() == null) {
			model.addAttribute(Constants.ERRO, Constants.MSG_CAMPOS_OBRIGATORIOS);
			return Constants.PAGINA_INCLUIR_RESERVA;
		}
		if (reserva.getAnoTermino() < reserva.getAnoInicio() || (reserva.getAnoInicio().equals(reserva.getAnoTermino()) 
				&& reserva.getSemestreTermino() < reserva.getSemestreInicio())) {
			model.addAttribute(Constants.ERRO, Constants.MSG_PERIODO_INVALIDO);
			return Constants.PAGINA_INCLUIR_RESERVA;
		}

		Periodo periodo = periodoService.getPeriodoAtual();
		Integer diferenca = calculaSemestres(periodo.getAno(), periodo.getSemestre(), reserva.getAnoInicio(), reserva.getSemestreInicio());

		if (diferenca < 2) {
			model.addAttribute(Constants.ERRO, Constants.MSG_SOLICITACAO_FORA_DO_PRAZO);
			return Constants.PAGINA_INCLUIR_RESERVA;
		}
		if ((reserva.getPrograma() == Programa.MESTRADO || reserva.getPrograma() == Programa.POS_DOUTORADO)
				&& calculaSemestres(reserva.getAnoInicio(), reserva.getSemestreInicio(), reserva.getAnoTermino(), reserva.getSemestreTermino()) + 1 > 4) {
			model.addAttribute(Constants.ERRO, Constants.MSG_TEMPO_MAXIMO_MESTRADO);
			return Constants.PAGINA_INCLUIR_RESERVA;
		}
		if (reserva.getPrograma() == Programa.DOUTORADO && calculaSemestres(reserva.getAnoInicio(), 
				reserva.getSemestreInicio(), reserva.getAnoTermino(), reserva.getSemestreTermino()) + 1 > 8) {
			model.addAttribute(Constants.ERRO, Constants.MSG_TEMPO_MAXIMO_DOUTORADO);
			return Constants.PAGINA_INCLUIR_RESERVA;
		}
		
		List<Reserva> reservas = reservaService
				.getReservasByStatusReservaAndProfessor(StatusReserva.EM_ESPERA, professorService.getByCpf(auth.getName()));
		
		if (reservas != null && !reservas.isEmpty()) {
			model.addAttribute(Constants.ERRO, Constants.MSG_RESERVA_EM_ESPERA);
			return Constants.PAGINA_INCLUIR_RESERVA;
		}
		reserva.setProfessor(professorService.getByCpf(auth.getName()));
		reserva.setStatus(StatusReserva.EM_ESPERA);
		reserva.setDataSolicitacao(new Date());

		reservaService.salvar(reserva);
		reservaService.salvarHistorico(reserva, Acao.CRIACAO, AutorAcao.PROFESSOR, null);

		notificacaoService.notificar(reserva, Notificacao.RESERVA_INCLUIDA, AutorAcao.PROFESSOR);

		redirect.addFlashAttribute(Constants.INFO, Constants.MSG_RESERVA_INCLUIDA);

		return Constants.REDIRECT_PAGINA_MINHAS_RESERVAS;
	}
	
	@RequestMapping(value = "/editar/{id}", method = RequestMethod.GET)
	public String editarForm(@PathVariable("id") Long id, Model model, Authentication auth, RedirectAttributes redirect) {
		Reserva reserva = reservaService.find(Reserva.class, id);
		Professor professor = professorService.getByCpf(auth.getName());
		if (reserva == null || !reserva.getProfessor().equals(professor) || !reserva.getStatus().equals(StatusReserva.EM_ESPERA)) {
			redirect.addFlashAttribute(Constants.ERRO, Constants.MSG_PERMISSAO_NEGADA);
			return Constants.REDIRECT_PAGINA_MINHAS_RESERVAS;
		}
		model.addAttribute("reserva", reserva);
		model.addAttribute("programa", Programa.values());
		return Constants.PAGINA_EDITAR_RESERVA;
	}	
	
	@RequestMapping(value = "/editar", method = RequestMethod.POST)
	public String editar(@ModelAttribute("reserva") Reserva reserva, Model model, RedirectAttributes redirect, HttpSession session) {
		
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
		reservaService.salvarHistorico(reservaAtual, Acao.EDICAO, AutorAcao.PROFESSOR, null);
		
		notificacaoService.notificar(reservaAtual, Notificacao.RESERVA_ALTERADA, AutorAcao.PROFESSOR);

		redirect.addFlashAttribute(Constants.INFO, Constants.MSG_RESERVA_ATUALIZADA);
		
		return Constants.REDIRECT_PAGINA_MINHAS_RESERVAS;
	}

	@RequestMapping(value = "/minhas-reservas", method = RequestMethod.GET)
	public String listar(Model model, Authentication auth) {
		Professor professor = professorService.getByCpf(auth.getName());
		Periodo periodo = periodoService.getPeriodoAtual();

		model.addAttribute("periodo", periodo);
		model.addAttribute("reservas", reservaService.getReservasByProfessor(professor));
		model.addAttribute("professor", professor);
		return Constants.PAGINA_MINHAS_RESERVAS;
	}
	
	@RequestMapping(value = "/excluir/{id}", method = RequestMethod.GET)
	public String excluir(@PathVariable("id") Long id, Authentication auth, RedirectAttributes redirect) {
		Reserva reserva = reservaService.getReservaById(id);
		Professor professor = professorService.getByCpf(auth.getName());
		if (reserva == null || !reserva.getProfessor().equals(professor) || !reserva.getStatus().equals(StatusReserva.EM_ESPERA)) {
			redirect.addFlashAttribute(Constants.ERRO, Constants.MSG_PERMISSAO_NEGADA);
		} else {
			reservaService.delete(reserva);
			notificacaoService.notificar(reserva, Notificacao.RESERVA_EXCLUIDA, AutorAcao.PROFESSOR);
			redirect.addFlashAttribute(Constants.INFO, Constants.MSG_RESERVA_EXCLUIDA);
		}
		return Constants.REDIRECT_PAGINA_MINHAS_RESERVAS;
	}
	
	@RequestMapping(value = "/cancelar", method = RequestMethod.POST)
	public String cancelar(@RequestParam("id") Long id, @RequestParam("motivo") String motivo, Authentication auth, RedirectAttributes redirect) {
		Reserva reserva = reservaService.getReservaById(id);
		Professor professor = professorService.getByCpf(auth.getName());
		if (reserva == null || !reserva.getProfessor().equals(professor) || !reserva.getStatus().equals(StatusReserva.ABERTO)) {
			redirect.addFlashAttribute(Constants.ERRO, Constants.MSG_PERMISSAO_NEGADA);
		} else {
			reserva.setStatus(StatusReserva.CANCELADO);
			reservaService.update(reserva);
			reservaService.salvarHistorico(reserva, Acao.CANCELAMENTO, AutorAcao.PROFESSOR, motivo);
			notificacaoService.notificar(reserva, Notificacao.RESERVA_CANCELADA, AutorAcao.PROFESSOR);
			redirect.addFlashAttribute(Constants.INFO, Constants.MSG_RESERVA_CANCELADA);
		}
		return Constants.REDIRECT_PAGINA_MINHAS_RESERVAS;
	}
	
	private Integer calculaSemestres(Integer anoInicio, Integer semestreInicio, Integer anoTermino, Integer semestreTermino) {
		return ((anoTermino - anoInicio) * 2) + (semestreTermino - semestreInicio);
	}
	
}
