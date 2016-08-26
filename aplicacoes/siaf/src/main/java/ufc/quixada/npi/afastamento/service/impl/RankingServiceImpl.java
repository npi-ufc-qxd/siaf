package ufc.quixada.npi.afastamento.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import ufc.quixada.npi.afastamento.model.Afastamento;
import ufc.quixada.npi.afastamento.model.Periodo;
import ufc.quixada.npi.afastamento.model.Programa;
import ufc.quixada.npi.afastamento.model.RelatorioPeriodo;
import ufc.quixada.npi.afastamento.model.Reserva;
import ufc.quixada.npi.afastamento.model.StatusReserva;
import ufc.quixada.npi.afastamento.model.StatusTupla;
import ufc.quixada.npi.afastamento.model.TuplaRanking;
import ufc.quixada.npi.afastamento.service.AfastamentoService;
import ufc.quixada.npi.afastamento.service.PeriodoService;
import ufc.quixada.npi.afastamento.service.RankingService;
import ufc.quixada.npi.afastamento.service.ReservaService;

@Named
public class RankingServiceImpl implements RankingService {

	@Inject
	private ReservaService reservaService;

	@Inject
	private AfastamentoService afastamentoService;

	@Inject
	private PeriodoService periodoService;
	
	
	@Override
	public Map<TuplaRanking, List<RelatorioPeriodo>> getRelatorio(Periodo periodo) {
		List<Periodo> periodos = periodoService.getPeriodoAbertos();
		Map<Periodo, List<TuplaRanking>> rankings = new HashMap<Periodo, List<TuplaRanking>>();
		Map<TuplaRanking, List<RelatorioPeriodo>> relatorio = new HashMap<TuplaRanking, List<RelatorioPeriodo>>();
		for (Periodo p : periodos) {
			rankings.put(p, new ArrayList<TuplaRanking>());
		}

		List<TuplaRanking> tuplas = new ArrayList<TuplaRanking>();
		List<Reserva> reservas = new ArrayList<Reserva>();
		List<Reserva> reservasEmAberto = reservaService.getReservasByStatus(StatusReserva.ABERTO);
		reservas.addAll(reservasEmAberto);
		reservas.addAll(reservaService.getReservasByStatus(StatusReserva.AFASTADO));

		tuplas.addAll(calculaPontuacao(reservas, periodo));

		Collections.sort(tuplas, new Comparator<TuplaRanking>() {
			@Override
			public int compare(TuplaRanking tupla1, TuplaRanking tupla2) {
				if (tupla1.getPontuacao().compareTo(tupla2.getPontuacao()) == 0.0f) {
					if (tupla1.getReserva().getPrograma().equals(tupla2.getReserva().getPrograma())) {
						if (tupla1.getReserva().getConceitoPrograma().equals(tupla2.getReserva().getConceitoPrograma())) {
							
							return tupla1.getReserva().getProfessor().getDataNascimento()
									.compareTo(tupla2.getReserva().getProfessor().getDataNascimento());
						}
						return tupla2.getReserva().getConceitoPrograma().compareTo(tupla1.getReserva().getConceitoPrograma());
					}
					if (tupla1.getReserva().getPrograma().equals(Programa.MESTRADO)) {
						return -1;
					}
					if (tupla2.getReserva().getPrograma().equals(Programa.MESTRADO)) {
						return 1;
					}
					if (tupla1.getReserva().getPrograma().equals(Programa.DOUTORADO)) {
						return -1;
					}
					if (tupla2.getReserva().getPrograma().equals(Programa.DOUTORADO)) {
						return 1;
					}
				}
				return tupla2.getPontuacao().compareTo(tupla1.getPontuacao());
			}
		});

		// Coloca primeiramente nos períodos os que já estão afastados
		for (TuplaRanking tupla : tuplas) {
			relatorio.put(tupla, new ArrayList<RelatorioPeriodo>());
			if (tupla.getReserva().getStatus().equals(StatusReserva.AFASTADO)) {
				Periodo periodoInicio = periodoService
						.getPeriodo(tupla.getReserva().getAnoInicio(), tupla.getReserva().getSemestreInicio());
				Periodo periodoTermino = periodoService.getPeriodo(tupla.getReserva().getAnoTermino(), tupla.getReserva()
						.getSemestreTermino());
				for (; periodoInicio != null && !periodoInicio.equals(periodoService.getPeriodoPosterior(periodoTermino)); periodoInicio = periodoService
						.getPeriodoPosterior(periodoInicio)) {
					RelatorioPeriodo relatorioPeriodo = new RelatorioPeriodo();
					relatorioPeriodo.setAno(periodoInicio.getAno());
					relatorioPeriodo.setSemestre(periodoInicio.getSemestre());
					relatorioPeriodo.setStatus(StatusTupla.AFASTADO);
					List<RelatorioPeriodo> r = relatorio.get(tupla);
					r.add(relatorioPeriodo);
					relatorio.put(tupla, r);
					if (rankings.containsKey(periodoInicio)) {
						tupla.setStatus(StatusTupla.AFASTADO);
						List<TuplaRanking> tuplaPeriodo = rankings.get(periodoInicio);
						tuplaPeriodo.add(tupla);
						rankings.put(periodoInicio, tuplaPeriodo);
					}
				}
			}
		}

		for (TuplaRanking tupla : tuplas) {
			if (!tupla.getReserva().getStatus().equals(StatusReserva.AFASTADO)) {
				boolean classificado = true;
				Periodo periodoInicio = periodoService
						.getPeriodo(tupla.getReserva().getAnoInicio(), tupla.getReserva().getSemestreInicio());
				Periodo periodoTermino = periodoService.getPeriodo(tupla.getReserva().getAnoTermino(), tupla.getReserva()
						.getSemestreTermino());
				for (; periodoInicio != null && !periodoInicio.equals(periodoService.getPeriodoPosterior(periodoTermino)); periodoInicio = periodoService
						.getPeriodoPosterior(periodoInicio)) {
					RelatorioPeriodo relatorioPeriodo = new RelatorioPeriodo();
					relatorioPeriodo.setAno(periodoInicio.getAno());
					relatorioPeriodo.setSemestre(periodoInicio.getSemestre());
					int vagas = periodoInicio.getVagas();
					for (TuplaRanking t : rankings.get(periodoInicio)) {
						if (t.getStatus().equals(StatusTupla.AFASTADO) || t.getStatus().equals(StatusTupla.CLASSIFICADO)) {
							vagas--;
						}
					}
					if (vagas <= 0) {
						tupla.setStatus(StatusTupla.DESCLASSIFICADO);
						relatorioPeriodo.setStatus(StatusTupla.DESCLASSIFICADO);
						classificado = false;
						//break;
					} else {
						relatorioPeriodo.setStatus(StatusTupla.CLASSIFICADO);
					}
					List<RelatorioPeriodo> r = relatorio.get(tupla);
					r.add(relatorioPeriodo);
					relatorio.put(tupla, r);
				}
				if (classificado) {
					tupla.setStatus(StatusTupla.CLASSIFICADO);
				}
				periodoInicio = periodoService.getPeriodo(tupla.getReserva().getAnoInicio(), tupla.getReserva().getSemestreInicio());
				for (; periodoInicio != null && !periodoInicio.equals(periodoService.getPeriodoPosterior(periodoTermino)); periodoInicio = periodoService
						.getPeriodoPosterior(periodoInicio)) {
					List<TuplaRanking> tuplaPeriodo = rankings.get(periodoInicio);
					tuplaPeriodo.add(tupla);
					rankings.put(periodoInicio, tuplaPeriodo);
				}
			}
		}

		return relatorio;
	}

	@Override
	public List<TuplaRanking> getRanking(Periodo periodo, boolean simulador) {
		List<Periodo> periodos = periodoService.getPeriodoAbertos();
		Map<Periodo, List<TuplaRanking>> rankings = new HashMap<Periodo, List<TuplaRanking>>();
		for (Periodo p : periodos) {
			rankings.put(p, new ArrayList<TuplaRanking>());
		}

		List<TuplaRanking> tuplas = new ArrayList<TuplaRanking>();
		List<Reserva> reservas = new ArrayList<Reserva>();
		List<Reserva> reservasEmAberto = reservaService.getReservasByStatus(StatusReserva.ABERTO);
		reservas.addAll(reservasEmAberto);
		reservas.addAll(reservaService.getReservasByStatus(StatusReserva.AFASTADO));
		if(simulador) {
			List<Reserva> reservasEmEspera = reservaService.getReservasByStatus(StatusReserva.EM_ESPERA);
			reservas.addAll(reservasEmEspera);
			for (Reserva reservaEspera : reservasEmEspera) {
				for (Reserva reservaAberta : reservasEmAberto) {
					if (reservaAberta.getProfessor().equals(reservaEspera.getProfessor())) {
						reservas.remove(reservaAberta);
					}
				}
			}
		}

		tuplas.addAll(calculaPontuacao(reservas, periodo));

		Collections.sort(tuplas, new Comparator<TuplaRanking>() {
			@Override
			public int compare(TuplaRanking tupla1, TuplaRanking tupla2) {
				if (tupla1.getPontuacao().compareTo(tupla2.getPontuacao()) == 0.0f) {
					if (tupla1.getReserva().getPrograma().equals(tupla2.getReserva().getPrograma())) {
						if (tupla1.getReserva().getConceitoPrograma().equals(tupla2.getReserva().getConceitoPrograma())) {
							
							return tupla1.getReserva().getProfessor().getDataNascimento()
									.compareTo(tupla2.getReserva().getProfessor().getDataNascimento());
						}
						return tupla2.getReserva().getConceitoPrograma().compareTo(tupla1.getReserva().getConceitoPrograma());
					}
					if (tupla1.getReserva().getPrograma().equals(Programa.MESTRADO)) {
						return -1;
					}
					if (tupla2.getReserva().getPrograma().equals(Programa.MESTRADO)) {
						return 1;
					}
					if (tupla1.getReserva().getPrograma().equals(Programa.DOUTORADO)) {
						return -1;
					}
					if (tupla2.getReserva().getPrograma().equals(Programa.DOUTORADO)) {
						return 1;
					}
				}
				return tupla2.getPontuacao().compareTo(tupla1.getPontuacao());
			}
		});

		// Coloca primeiramente nos períodos os que já estão afastados
		for (TuplaRanking tupla : tuplas) {
			if (tupla.getReserva().getStatus().equals(StatusReserva.AFASTADO)) {
				Periodo periodoInicio = periodoService
						.getPeriodo(tupla.getReserva().getAnoInicio(), tupla.getReserva().getSemestreInicio());
				Periodo periodoTermino = periodoService.getPeriodo(tupla.getReserva().getAnoTermino(), tupla.getReserva()
						.getSemestreTermino());
				for (; periodoInicio != null && !periodoInicio.equals(periodoService.getPeriodoPosterior(periodoTermino)); periodoInicio = periodoService
						.getPeriodoPosterior(periodoInicio)) {
					if (rankings.containsKey(periodoInicio)) {
						tupla.setStatus(StatusTupla.AFASTADO);
						List<TuplaRanking> tuplaPeriodo = rankings.get(periodoInicio);
						tuplaPeriodo.add(tupla);
						rankings.put(periodoInicio, tuplaPeriodo);
					}
				}
			}
		}

		for (TuplaRanking tupla : tuplas) {
			if (!tupla.getReserva().getStatus().equals(StatusReserva.AFASTADO)) {
				boolean classificado = true;
				Periodo periodoInicio = periodoService
						.getPeriodo(tupla.getReserva().getAnoInicio(), tupla.getReserva().getSemestreInicio());
				Periodo periodoTermino = periodoService.getPeriodo(tupla.getReserva().getAnoTermino(), tupla.getReserva()
						.getSemestreTermino());
				for (; periodoInicio != null && !periodoInicio.equals(periodoService.getPeriodoPosterior(periodoTermino)); periodoInicio = periodoService
						.getPeriodoPosterior(periodoInicio)) {
					int vagas = periodoInicio.getVagas();
					for (TuplaRanking t : rankings.get(periodoInicio)) {
						if (t.getStatus().equals(StatusTupla.AFASTADO) || t.getStatus().equals(StatusTupla.CLASSIFICADO)) {
							vagas--;
						}
					}
					if (vagas <= 0) {
						tupla.setStatus(StatusTupla.DESCLASSIFICADO);
						classificado = false;
						break;
					}
				}
				if (classificado) {
					tupla.setStatus(StatusTupla.CLASSIFICADO);
				}
				periodoInicio = periodoService.getPeriodo(tupla.getReserva().getAnoInicio(), tupla.getReserva().getSemestreInicio());
				for (; periodoInicio != null && !periodoInicio.equals(periodoService.getPeriodoPosterior(periodoTermino)); periodoInicio = periodoService
						.getPeriodoPosterior(periodoInicio)) {
					List<TuplaRanking> tuplaPeriodo = rankings.get(periodoInicio);
					tuplaPeriodo.add(tupla);
					rankings.put(periodoInicio, tuplaPeriodo);
				}
			}
		}

		return rankings.get(periodo);
	}

	@Override
	public List<TuplaRanking> getTuplas(List<StatusReserva> status, Periodo periodo) {
		List<TuplaRanking> tuplas = new ArrayList<TuplaRanking>();
		List<Reserva> reservas = new ArrayList<Reserva>();

		for (StatusReserva statusReserva : status) {
			reservas = reservaService.getReservasByStatusReservaAndPeriodo(statusReserva, periodo);
			for (Reserva reserva : reservas) {
				TuplaRanking tupla = new TuplaRanking();
				tupla.setReserva(reserva);
				tupla.setProfessor(reserva.getProfessor().getNome());
				tupla.setPeriodo(periodo);
				if (StatusReserva.CANCELADO.equals(statusReserva))
					tupla.setStatus(StatusTupla.CANCELADO);
				else if (StatusReserva.NEGADO.equals(statusReserva))
					tupla.setStatus(StatusTupla.NEGADO);
				else if (StatusReserva.CANCELADO_COM_PUNICAO.equals(statusReserva))
					tupla.setStatus(StatusTupla.CANCELADO_COM_PUNICAO);
				else if(StatusReserva.AFASTADO.equals(statusReserva))
					tupla.setStatus(StatusTupla.AFASTADO);
				tuplas.add(tupla);
			}
		}
		return tuplas;
	}

	private List<TuplaRanking> calculaPontuacao(List<Reserva> reservas, Periodo periodo) {
		List<TuplaRanking> tuplas = new ArrayList<TuplaRanking>();
		for (Reserva reserva : reservas) {
			TuplaRanking tupla = new TuplaRanking();
			tupla.setReserva(reserva);
			tupla.setPeriodo(periodo);
			tupla.setProfessor(reserva.getProfessor().getNome());
			tupla.setSs(getSemestresSolicitados(reserva));
			tupla.setT(calculaSemestres(reserva.getProfessor().getAnoAdmissao(), reserva.getProfessor().getSemestreAdmissao(),
					reserva.getAnoInicio(), reserva.getSemestreInicio()) - 1);
			tupla.setA(getSemestresAfastados(reserva));
			Float t = Float.valueOf(tupla.getT());
			Float a = Float.valueOf(tupla.getA());
			Float s = a == 0.0 ? 2 : Float.valueOf(tupla.getSs());
			tupla.setS(s.intValue());
			Integer semContratacao = calculaSemestres(reserva.getProfessor().getAnoAdmissao(),
					reserva.getProfessor().getSemestreAdmissao(), reserva.getAnoInicio(), reserva.getSemestreInicio()) - 1;
			Float p = semContratacao >= 6.0f ? 0.0f : (6.0f - semContratacao);
			tupla.setP(p.intValue());
			Float pontuacao = (t - a) / (5.0f * a + s + p);
			tupla.setPontuacao(pontuacao);

			tuplas.add(tupla);
		}
		return tuplas;
	}

	private Integer getSemestresSolicitados(Reserva reserva) {
		return calculaSemestres(reserva.getAnoInicio(), reserva.getSemestreInicio(), reserva.getAnoTermino(), reserva.getSemestreTermino());
	}

	private Integer getSemestresAfastados(Reserva reserva) {
		List<Afastamento> afastamentos = afastamentoService.getAfastamentosAnteriores(reserva);
		Periodo periodo = periodoService.getPeriodo(reserva.getAnoInicio(), reserva.getSemestreInicio());
		Integer semestresAfastado = 0;
		for (Afastamento afastamento : afastamentos) {
			semestresAfastado = semestresAfastado
					+ calculaSemestres(afastamento.getReserva().getAnoInicio(), afastamento.getReserva().getSemestreInicio(), afastamento
							.getReserva().getAnoTermino(), afastamento.getReserva().getSemestreTermino());
		}
		Integer punicao = reservaService.getReservasAnterioresComPunicao(reserva.getProfessor(), periodo).size();
		semestresAfastado = semestresAfastado + punicao;
		return semestresAfastado;
	}

	private Integer calculaSemestres(Integer anoInicio, Integer semestreInicio, Integer anoTermino, Integer semestreTermino) {
		return ((anoTermino - anoInicio) * 2) + (semestreTermino - semestreInicio) + 1;
	}

}
