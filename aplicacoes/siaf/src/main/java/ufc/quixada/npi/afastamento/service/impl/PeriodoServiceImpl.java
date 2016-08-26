package ufc.quixada.npi.afastamento.service.impl;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import ufc.quixada.npi.afastamento.model.Acao;
import ufc.quixada.npi.afastamento.model.AutorAcao;
import ufc.quixada.npi.afastamento.model.Notificacao;
import ufc.quixada.npi.afastamento.model.Periodo;
import ufc.quixada.npi.afastamento.model.Reserva;
import ufc.quixada.npi.afastamento.model.StatusPeriodo;
import ufc.quixada.npi.afastamento.model.StatusReserva;
import ufc.quixada.npi.afastamento.model.StatusTupla;
import ufc.quixada.npi.afastamento.model.TuplaRanking;
import ufc.quixada.npi.afastamento.service.NotificacaoService;
import ufc.quixada.npi.afastamento.service.PeriodoService;
import ufc.quixada.npi.afastamento.service.RankingService;
import ufc.quixada.npi.afastamento.service.ReservaService;
import ufc.quixada.npi.afastamento.util.Constants;
import br.ufc.quixada.npi.enumeration.QueryType;
import br.ufc.quixada.npi.repository.GenericRepository;
import br.ufc.quixada.npi.service.impl.GenericServiceImpl;

@Named
public class PeriodoServiceImpl extends GenericServiceImpl<Periodo> implements PeriodoService {

	@Inject
	private GenericRepository<Periodo> periodoRepository;
	
	@Inject
	private RankingService rankingService;
	
	@Inject
	private ReservaService reservaService;
	
	@Inject
	private NotificacaoService notificacaoService;

	@Override
	public Periodo getPeriodo(Integer ano, Integer semestre) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ano", ano);
		params.put("semestre", semestre);
		return periodoRepository.findFirst(QueryType.JPQL, "select p from Periodo p where p.ano = :ano and p.semestre = :semestre", params, -1);
	}

	@Override
	public Periodo getPeriodoByEncerramento(Date encerramento) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("encerramento", encerramento);
		return periodoRepository.findFirst(QueryType.JPQL, "from Periodo p where encerramento = :encerramento", params, -1);
	}
	
	@Override
	public Periodo getPeriodoAnterior(Periodo periodo) {
		if(periodo.getSemestre() == 2) {
			return getPeriodo(periodo.getAno(), 1);
		}
		return getPeriodo(periodo.getAno() - 1, 2);
	}

	@Override
	public Periodo getPeriodoPosterior(Periodo periodo) {
		if(periodo.getSemestre() == 1) {
			return getPeriodo(periodo.getAno(), 2);
		}
		return getPeriodo(periodo.getAno() + 1, 1);
	}
	
	@Override
	public Periodo getPeriodoAtual() {
		return periodoRepository.findFirst(QueryType.JPQL, "from Periodo p where status = 'ABERTO' order by ano ASC, semestre ASC", null, -1);
	}
	
	@Override
	public Integer getSemestreAtual() {
		Calendar calendar = Calendar.getInstance();
		if(calendar.get(Calendar.MONTH) < 6) {
			return 1;
		}
		return 2;
	}
	
	@Override
	public Integer getAnoAtual() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.YEAR);
	}

	@Override
	public Periodo getUltimoPeriodoEncerrado() {
		return periodoRepository.findFirst(QueryType.JPQL, "from Periodo p where status = '" + StatusPeriodo.ENCERRADO + "' order by ano DESC, semestre DESC", null, -1);
	}

	@Override
	public List<Periodo> getAll() {
		return periodoRepository.find(QueryType.JPQL, "from Periodo order by ano ASC, semestre ASC", null);
	}

	@Override
	public List<Periodo> getPeriodosPosteriores(Periodo periodo) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ano", periodo.getAno());
		params.put("semestre", periodo.getSemestre());
		return periodoRepository.find(QueryType.JPQL, "from Periodo p where ano > :ano or (ano = :ano and semestre >= :semestre)", params);
	}

	@Override
	public List<Periodo> getPeriodoAbertos() {
		return periodoRepository.find(QueryType.JPQL, "from Periodo p where status = '" + StatusPeriodo.ABERTO + "' order by ano ASC, semestre ASC", null);
	}

	@Override
	public void encerrarPeriodo(Periodo periodo) {
		List<TuplaRanking> ranking = rankingService.getRanking(periodo, false);
		for (TuplaRanking tupla : ranking) {
			if(tupla.getReserva().getStatus().equals(StatusReserva.AFASTADO)
					&& tupla.getReserva().getAnoTermino().equals(periodo.getAno())
					&& tupla.getReserva().getSemestreTermino().equals(periodo.getSemestre())) {
				Reserva reserva = tupla.getReserva();
				reserva.setStatus(StatusReserva.ENCERRADO);
				reservaService.update(reserva);
				reservaService.salvarHistorico(reserva, Acao.ENCERRAMENTO, AutorAcao.SISTEMA, null);
			}
		}
		periodo.setStatus(StatusPeriodo.ENCERRADO);
		this.update(periodo);
		ranking = rankingService.getRanking(this.getPeriodoPosterior(periodo), false);
		for (TuplaRanking tupla : ranking) {
			if(tupla.getStatus().equals(StatusTupla.DESCLASSIFICADO)) {
				Reserva reserva = tupla.getReserva();
				reserva.setStatus(StatusReserva.NAO_ACEITO);
				reservaService.update(reserva);
				reservaService.salvarHistorico(reserva, Acao.NAO_ACEITACAO, AutorAcao.SISTEMA, null);
			}
		}
		List<Reserva> reservasEmEspera = reservaService.getReservasByStatus(StatusReserva.EM_ESPERA);
		List<Reserva> reservasEmAberto = reservaService.getReservasByStatus(StatusReserva.ABERTO);
		
		for (Reserva reservaEspera : reservasEmEspera) {
			for (Reserva reservaAberto : reservasEmAberto) {
				if (reservaEspera.getProfessor().equals(reservaAberto.getProfessor())) {
					reservaAberto.setStatus(StatusReserva.CANCELADO);
					reservaService.update(reservaAberto);
					reservaService.salvarHistorico(reservaAberto, Acao.CANCELAMENTO, AutorAcao.SISTEMA, Constants.MSG_CANCELAMENTO_AUTOMATICO);
					notificacaoService.notificar(reservaAberto, Notificacao.RESERVA_CANCELADA, AutorAcao.SISTEMA);
					break;
				}
			}
		}
		
		for(Reserva reserva : reservasEmEspera) {
			reserva.setStatus(StatusReserva.ABERTO);
			reservaService.update(reserva);
			reservaService.salvarHistorico(reserva, Acao.INCLUSAO_RANKING, AutorAcao.SISTEMA, null);
			notificacaoService.notificar(reserva, Notificacao.RESERVA_INCLUIDA_RANKING, AutorAcao.SISTEMA);
		}
		
	}

}

