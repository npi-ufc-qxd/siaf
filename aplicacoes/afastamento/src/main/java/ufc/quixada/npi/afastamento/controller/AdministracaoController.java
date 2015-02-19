package ufc.quixada.npi.afastamento.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ufc.quixada.npi.afastamento.model.Periodo;
import ufc.quixada.npi.afastamento.model.Professor;
import ufc.quixada.npi.afastamento.model.Ranking;
import ufc.quixada.npi.afastamento.model.Reserva;
import ufc.quixada.npi.afastamento.model.StatusPeriodo;
import ufc.quixada.npi.afastamento.model.StatusReserva;
import ufc.quixada.npi.afastamento.model.TuplaRanking;
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
	
	@RequestMapping(value = "/professores", method = RequestMethod.GET)
	public String listarProfessores(Model model) {
		List<Professor> professores = professorService.findAtivos();
		Collections.sort(professores, new Comparator<Professor>() {
	        @Override
	        public int compare(Professor  professor1, Professor  professor2)
	        {
	            return  professor1.getNome().compareTo(professor2.getNome());
	        }
	    });
		model.addAttribute("professores", professores);
		return Constants.PAGINA_LISTAR_PROFESSORES;
	}
	
	@RequestMapping(value = "/reservas", method = RequestMethod.GET)
	@CacheEvict(value = {"ranking", "visualizarRanking"}, allEntries = true, beforeInvocation = true)
	public String getReservas(Model model) {
		Periodo periodo = periodoService.getUltimoPeriodoEncerrado();
		if(periodo != null) {
			periodo = periodoService.getPeriodoPosterior(periodo);
			if(periodo != null) {
				periodo = periodoService.getPeriodoPosterior(periodo);
				Ranking ranking = rankingService.getRanking(periodo);
				model.addAttribute("ranking", ranking);
			}
		}
		
		return Constants.PAGINA_GERENCIAR_RESERVAS;
	}
	
	@RequestMapping(value = "/atualizar-ranking", method = RequestMethod.POST)
	@CacheEvict(value = {"default", "reservasByProfessor", "periodo", "visualizarRanking", "ranking", "loadProfessor", "professores"}, allEntries = true, beforeInvocation = true)
	public String atualizarRanking(HttpServletRequest request, RedirectAttributes redirect) {
		String[] status = request.getParameterValues("status");
		for(String s : status) {
			String[] valor = s.split("-");
			Reserva reserva = reservaService.find(Reserva.class, Long.parseLong(valor[0]));
			StatusReserva statusReserva = StatusReserva.valueOf(valor[1]);
			reserva.setStatus(statusReserva);
			reservaService.update(reserva);
		}
		Integer ano = Integer.valueOf(request.getParameter("ano"));
		Integer semestre = Integer.valueOf(request.getParameter("semestre"));
		Periodo periodo = periodoService.getPeriodo(ano, semestre);
		Ranking ranking = rankingService.getRanking(periodo);
		int vagas = periodo.getVagas();
		for(TuplaRanking tupla : ranking.getTuplas()) {
			if(tupla.getReserva().getStatus().equals(StatusReserva.ACEITO)) {
				if(vagas == 0) {
					Reserva reserva = tupla.getReserva();
					reserva.setStatus(StatusReserva.NAO_ACEITO);
					reservaService.update(reserva);
				} else {
					vagas--;
				}
			} else if(tupla.getReserva().getStatus().equals(StatusReserva.NAO_ACEITO) && vagas > 0) {
				Reserva reserva = tupla.getReserva();
				reserva.setStatus(StatusReserva.ACEITO);
				reservaService.update(reserva);
				vagas--;
			}
		}
		
		redirect.addFlashAttribute(Constants.INFO, Constants.MSG_RESERVAS_ATUALIZADAS);
		return Constants.REDIRECT_PAGINA_GERENCIAR_RESERVAS;
	}

	@RequestMapping(value = "/periodos.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Periodo> periodos() {
		return periodoService.getAll();
	}
	
	@RequestMapping(value = "/periodo", method = RequestMethod.GET)
	public String listarPeriodos(Model model) {
		model.addAttribute("periodos", periodoService.find(Periodo.class));
		return Constants.PAGINA_LISTAR_PERIODOS;
	}

	private boolean notNull(Object object){
		return object != null ?  true : false;
	}
	
	@RequestMapping(value = "/admissao", method = RequestMethod.POST)
	@CacheEvict(value = {"default", "reservasByProfessor", "periodo", "visualizarRanking", "ranking", "loadProfessor", "professores"}, allEntries = true)
	public String atualizaAdmissao(@RequestParam("id") Long id, @RequestParam("ano") Integer ano, @RequestParam("semestre") Integer semestre, Model model) {

		if (id == null || ano == null || semestre == null) {
			model.addAttribute(Constants.ERRO, "Dados inválidos");
			return Constants.PAGINA_LISTAR_PROFESSORES;
		}
		
		Professor professor = professorService.find(Professor.class, id);
		professor.setAnoAdmissao(ano);
		professor.setSemestreAdmissao(semestre);

		professorService.update(professor);
		
		List<Professor> professors = professorService.findAtivos();
		model.addAttribute("professores", professors);
		model.addAttribute("info", "Data de admissão do(a) Prof(a) " + professor.getNome() + " atualizada com sucesso.");

		return Constants.PAGINA_LISTAR_PROFESSORES;
	}
	
	@RequestMapping(value = "/edit-periodo.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Model permissaoEditPeriodo(Model model, @RequestParam("id") Long id) {

		Periodo periodo = periodoService.find(Periodo.class, id);
		boolean editEncerramento = false;
		boolean editVagas = false;

		if(periodo.getStatus().equals(StatusPeriodo.ABERTO)){
			editEncerramento = true;
		}

		Periodo periodoSolicitacao = periodoService.getPeriodo(periodo.getAno() - 1, periodo.getSemestre());	
		if(notNull(periodoSolicitacao))
		if(periodoSolicitacao.getStatus().equals(StatusPeriodo.ABERTO)){
			editVagas = true;
		}

		model.addAttribute("editEncerramento", editEncerramento);
		model.addAttribute("editVagas", editVagas);

		return model;
	}

	@RequestMapping(value = "/editar-periodo.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public Model editarPeriodo(Model model, 
			@RequestParam("id") Long id, 
			@RequestParam("vagas") Integer vagas, 
			@RequestParam("encerramento") String encerramentoString) {

		boolean permitirUpdateEncerramento = false;
		boolean permitirUpdateVagas = false;

		Periodo periodo = periodoService.find(Periodo.class, id);

		Date encerramento = null;

		if(encerramentoString != null && !encerramentoString.isEmpty()) {
			if(periodo.getStatus().equals(StatusPeriodo.ABERTO)){

				try {
					DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
					DateTime date = dateTimeFormatter.parseDateTime(encerramentoString);
					encerramento = date.toDate();

					SimpleDateFormat format = new SimpleDateFormat(br.ufc.quixada.npi.ldap.model.Constants.FORMATO_DATA_NASCIMENTO);					
					Date today;
 						today = format.parse(format.format(new Date()));
					
					if(encerramento.before(today)) {
						model.addAttribute(Constants.ERRO, Constants.MSG_DATA_FUTURA);
						model.addAttribute("periodo", periodo);
						return model;
					}

					permitirUpdateEncerramento = true;
				} catch (ParseException e) {
					model.addAttribute(Constants.ERRO, Constants.MSG_ERRO_ATUALIZAR_PERIODO);
					return model;
				}
			}
		}
	
		if(vagas == null) {
			vagas = 0;
		}
		
		Periodo periodoSolicitacao = periodoService.getPeriodo(periodo.getAno() - 1, periodo.getSemestre());	
		if(periodoSolicitacao.getStatus().equals(StatusPeriodo.ABERTO)){
			permitirUpdateVagas = true;
		}

		if(permitirUpdateEncerramento){
			periodo.setEncerramento(encerramento);
		}

		if(permitirUpdateVagas){
			periodo.setVagas(vagas);
		}
			
		if(permitirUpdateEncerramento || permitirUpdateVagas){
			periodoService.update(periodo);
			model.addAttribute(Constants.INFO,"Período " +periodo.getAno() + "." + periodo.getSemestre() + " atualizado com sucesso.");
		}

		model.addAttribute("periodo", periodo);
		return model;
	}
	

}
