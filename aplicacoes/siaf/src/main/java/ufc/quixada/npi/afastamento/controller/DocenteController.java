package ufc.quixada.npi.afastamento.controller;

import javax.inject.Inject;

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
import ufc.quixada.npi.afastamento.model.Professor;
import ufc.quixada.npi.afastamento.model.Programa;
import ufc.quixada.npi.afastamento.model.Reserva;
import ufc.quixada.npi.afastamento.model.StatusReserva;
import ufc.quixada.npi.afastamento.service.NotificacaoService;
import ufc.quixada.npi.afastamento.service.PeriodoService;
import ufc.quixada.npi.afastamento.service.ProfessorService;
import ufc.quixada.npi.afastamento.service.ReservaService;
import ufc.quixada.npi.afastamento.util.Constants;
import ufc.quixada.npi.afastamento.util.SiafException;

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
		model.addAttribute("professor", professorService.findByCpf(auth.getName()));
		model.addAttribute("programa", Programa.values());
		return Constants.PAGINA_INCLUIR_RESERVA;
	}

	@RequestMapping(value = "/incluir", method = RequestMethod.POST)
	public String incluir(@ModelAttribute("reserva") Reserva reserva, Model model, RedirectAttributes redirect, Authentication auth) {
		try {
			reservaService.incluir(reserva, professorService.findByCpf(auth.getName()));
		} catch (SiafException exception) {
			model.addAttribute("reserva", reserva);
			model.addAttribute("professor", professorService.findByCpf(auth.getName()));
			model.addAttribute("programa", Programa.values());
			model.addAttribute(Constants.ERRO, exception.getMessage());
			return Constants.PAGINA_INCLUIR_RESERVA;
		}

		reservaService.salvarHistorico(reserva, Acao.CRIACAO, AutorAcao.PROFESSOR);
		notificacaoService.notificar(reserva, Notificacao.RESERVA_INCLUIDA, AutorAcao.PROFESSOR);
		redirect.addFlashAttribute(Constants.INFO, Constants.MSG_RESERVA_INCLUIDA);
		return Constants.REDIRECT_PAGINA_MINHAS_RESERVAS;
	}
	
	@RequestMapping(value = "/editar/{id}", method = RequestMethod.GET)
	public String editarForm(@PathVariable("id") Long id, Model model, Authentication auth, RedirectAttributes redirect) {
		Reserva reserva = reservaService.getReservaById(id);
		if (reserva == null || !reserva.getProfessor().equals(professorService.findByCpf(auth.getName())) || !reserva.getStatus().equals(StatusReserva.EM_ESPERA)) {
			redirect.addFlashAttribute(Constants.ERRO, Constants.MSG_PERMISSAO_NEGADA);
			return Constants.REDIRECT_PAGINA_MINHAS_RESERVAS;
		}
		model.addAttribute("reserva", reserva);
		model.addAttribute("programa", Programa.values());
		return Constants.PAGINA_EDITAR_RESERVA;
	}	
	
	@RequestMapping(value = "/editar", method = RequestMethod.POST)
	public String editar(@ModelAttribute("reserva") Reserva reserva, Model model, RedirectAttributes redirect, Authentication auth) {
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

		reservaService.salvarHistorico(reservaAtual, Acao.EDICAO, AutorAcao.PROFESSOR);
		notificacaoService.notificar(reservaAtual, Notificacao.RESERVA_ALTERADA, AutorAcao.PROFESSOR);
		redirect.addFlashAttribute(Constants.INFO, Constants.MSG_RESERVA_ATUALIZADA);
		return Constants.REDIRECT_PAGINA_MINHAS_RESERVAS;
	}

	@RequestMapping(value = "/minhas-reservas", method = RequestMethod.GET)
	public String listar(Model model, Authentication auth) {
		Professor professor = professorService.findByCpf(auth.getName());
		model.addAttribute("periodo", periodoService.getPeriodoAtual());
		model.addAttribute("reservas", reservaService.getReservasByProfessor(professor));
		model.addAttribute("professor", professor);
		return Constants.PAGINA_MINHAS_RESERVAS;
	}
	
	@RequestMapping(value = "/excluir/{id}", method = RequestMethod.GET)
	public String excluir(@PathVariable("id") Long id, Authentication auth, RedirectAttributes redirect) {
		Reserva reserva = reservaService.getReservaById(id);
		Professor professor = professorService.findByCpf(auth.getName());
		if (reserva == null || !reserva.getProfessor().equals(professor)) {
			redirect.addFlashAttribute(Constants.ERRO, Constants.MSG_PERMISSAO_NEGADA);
		} else {
			try {
				reservaService.excluir(reserva);
			} catch (SiafException exception) {
				redirect.addFlashAttribute(Constants.ERRO, exception.getMessage());
				return Constants.REDIRECT_PAGINA_MINHAS_RESERVAS;
			}
			notificacaoService.notificar(reserva, Notificacao.RESERVA_EXCLUIDA, AutorAcao.PROFESSOR);
			redirect.addFlashAttribute(Constants.INFO, Constants.MSG_RESERVA_EXCLUIDA);
		}
		return Constants.REDIRECT_PAGINA_MINHAS_RESERVAS;
	}
	
	@RequestMapping(value = "/cancelar", method = RequestMethod.POST)
	public String cancelar(@RequestParam("id") Long id, @RequestParam("motivo") String motivo, Authentication auth, RedirectAttributes redirect) {
		Reserva reserva = reservaService.getReservaById(id);
		Professor professor = professorService.findByCpf(auth.getName());
		if (reserva == null || !reserva.getProfessor().equals(professor)) {
			redirect.addFlashAttribute(Constants.ERRO, Constants.MSG_PERMISSAO_NEGADA);
		} else {
			try {
				reservaService.cancelar(reserva);
			} catch (SiafException exception) {
				redirect.addFlashAttribute(Constants.ERRO, exception.getMessage());
				return Constants.REDIRECT_PAGINA_MINHAS_RESERVAS;
			}
			reservaService.salvarHistorico(reserva, Acao.CANCELAMENTO, AutorAcao.PROFESSOR, motivo);
			notificacaoService.notificar(reserva, Notificacao.RESERVA_CANCELADA, AutorAcao.PROFESSOR);
			redirect.addFlashAttribute(Constants.INFO, Constants.MSG_RESERVA_CANCELADA);
		}
		return Constants.REDIRECT_PAGINA_MINHAS_RESERVAS;
	}
	
}
