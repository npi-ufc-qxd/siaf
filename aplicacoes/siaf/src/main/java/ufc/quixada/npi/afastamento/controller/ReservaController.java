package ufc.quixada.npi.afastamento.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ufc.quixada.npi.afastamento.model.Periodo;
import ufc.quixada.npi.afastamento.model.Ranking;
import ufc.quixada.npi.afastamento.model.Reserva;
import ufc.quixada.npi.afastamento.model.StatusPeriodo;
import ufc.quixada.npi.afastamento.model.StatusReserva;
import ufc.quixada.npi.afastamento.model.TuplaRanking;
import ufc.quixada.npi.afastamento.service.PeriodoService;
import ufc.quixada.npi.afastamento.service.RankingService;
import ufc.quixada.npi.afastamento.service.ReservaService;
import ufc.quixada.npi.afastamento.util.Constants;

@Controller
@RequestMapping("reserva")
public class ReservaController {

	@Inject
	private ReservaService reservaService;

	@Inject
	private RankingService rankingService;

	@Inject
	private PeriodoService periodoService;

	@RequestMapping(value = "/ranking", method = RequestMethod.GET)
	public String getRanking(Model model, HttpSession session) {
		Periodo periodoAtual = periodoService.getPeriodoAtual();
		model.addAttribute("periodoAtual", periodoAtual);
		model.addAttribute("periodoPosterior", periodoService.getPeriodoPosterior(periodoAtual));
		return Constants.PAGINA_RANKING;
	}
	
	@RequestMapping(value = "/previa-ranking", method = RequestMethod.GET)
	public String getSimulador(Model model, HttpSession session) {
		Periodo periodoAtual = periodoService.getPeriodoAtual();
		model.addAttribute("periodoAtual", periodoAtual);
		model.addAttribute("periodoPosterior", periodoService.getPeriodoPosterior(periodoAtual));
		return Constants.PAGINA_PREVIA_RANKING;
	}

	@RequestMapping(value = {"/ranking.json"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Model ranking(HttpServletRequest request, Model model) {
		Ranking ranking = new Ranking();
		boolean simulador = Boolean.valueOf(request.getParameter("simulador"));
		ranking.setPeriodo(periodoService.getPeriodo(Integer.valueOf(request.getParameter("ano")),
				Integer.valueOf(request.getParameter("semestre"))));

		ranking.setTuplas(rankingService.getRanking(ranking.getPeriodo(), simulador));

		List<TuplaRanking> tuplas = new ArrayList<TuplaRanking>();
		List<TuplaRanking> afastados = new ArrayList<TuplaRanking>();
		for (TuplaRanking tupla : ranking.getTuplas()) {
			if (tupla.getReserva().getStatus().equals(StatusReserva.AFASTADO)) {
				afastados.add(tupla);
			} else {
				tuplas.add(tupla);
			}
		}
		Collections.sort(afastados, new Comparator<TuplaRanking>() {

			@Override
			public int compare(TuplaRanking tupla1, TuplaRanking tupla2) {
				return tupla1.getProfessor().compareTo(tupla2.getProfessor());
			}
		});
		model.addAttribute("afastados", afastados);
		ranking.setTuplas(tuplas);
		model.addAttribute("ranking", ranking);
		model.addAttribute("periodoAtual", ranking.getPeriodo());
		Periodo periodoAnterior = periodoService.getPeriodoAnterior(ranking.getPeriodo());
		if (periodoAnterior.getStatus().equals(StatusPeriodo.ENCERRADO)) {
			model.addAttribute("periodoAnterior", null);
		} else {
			model.addAttribute("periodoAnterior", periodoAnterior);
		}
		model.addAttribute("periodoPosterior", periodoService.getPeriodoPosterior(ranking.getPeriodo()));

		
		return model;
	}
	
	@RequestMapping(value = {"/detalhes/{id}"}, method = RequestMethod.GET)
	public String detalhes(@PathVariable("id") Long id, RedirectAttributes redirect, Model model) {
		Reserva reserva = reservaService.find(Reserva.class, id);
		if (reserva == null) {
			redirect.addFlashAttribute(Constants.ERRO, Constants.MSG_PERMISSAO_NEGADA);
			return Constants.REDIRECT_PAGINA_LISTAR_RESERVAS;
		}
		model.addAttribute("reserva", reserva);
		return Constants.PAGINA_DETALHE_RESERVA;
	}

	
	
	@RequestMapping(value = "/listar", method = RequestMethod.GET)
	public String getReservas(Model model) {
		List<Reserva> reservas = reservaService.getAllReservas();
		model.addAttribute("reservas", reservas);
		return Constants.PAGINA_LISTAR_RESERVAS;
	}

}
