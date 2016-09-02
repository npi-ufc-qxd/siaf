package ufc.quixada.npi.afastamento.controller;

import java.util.Date;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ufc.quixada.npi.afastamento.model.Periodo;
import ufc.quixada.npi.afastamento.service.PeriodoService;
import ufc.quixada.npi.afastamento.service.RankingService;

@Controller
@RequestMapping("/relatorio")
public class RelatorioController {

	@Inject
	private PeriodoService periodoService;
	
	@Inject
	private RankingService rankingService;

	@RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
	public String getRelatorio(Model model) {
		Periodo periodo = periodoService.getPeriodoAtual();
		if (periodo != null) {
			model.addAttribute("relatorio", rankingService.getRelatorio(periodo));
			model.addAttribute("periodos", periodoService.getPeriodoAbertos());
			Periodo periodoAtual = periodoService.getPeriodoAtual();
			model.addAttribute("periodoAtual", periodoAtual);
			model.addAttribute("proximoPeriodo", periodoService.getPeriodoPosterior(periodoAtual));
			model.addAttribute("dataAtual", new Date());
		}
		return "reserva/relatorio";
	}
}
