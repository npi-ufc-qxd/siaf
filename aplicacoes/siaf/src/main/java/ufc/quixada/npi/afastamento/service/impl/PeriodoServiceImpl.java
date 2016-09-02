package ufc.quixada.npi.afastamento.service.impl;

import java.util.Date;
import java.util.List;

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
import ufc.quixada.npi.afastamento.repository.PeriodoRespository;
import ufc.quixada.npi.afastamento.repository.ReservaRepository;
import ufc.quixada.npi.afastamento.service.NotificacaoService;
import ufc.quixada.npi.afastamento.service.PeriodoService;
import ufc.quixada.npi.afastamento.service.RankingService;
import ufc.quixada.npi.afastamento.service.ReservaService;
import ufc.quixada.npi.afastamento.util.Constants;
import ufc.quixada.npi.afastamento.util.SiafException;

@Named
public class PeriodoServiceImpl implements PeriodoService {

	@Inject
	private PeriodoRespository periodoRepository;
	
	@Inject
	private RankingService rankingService;
	
	@Inject
	private ReservaService reservaService;
	
	@Inject
	private ReservaRepository reservaRepository;
	
	@Inject
	private NotificacaoService notificacaoService;

	@Override
	public Periodo getPeriodo(Integer ano, Integer semestre) {
		return periodoRepository.findByAnoAndSemestre(ano, semestre);
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
		return periodoRepository.findFirstByStatusOrderByAnoAscSemestreAsc(StatusPeriodo.ABERTO);
	}

	@Override
	public List<Periodo> getPeriodosPosteriores(Periodo periodo) {
		return periodoRepository.findPeriodosPosteriores(periodo.getAno(), periodo.getSemestre());
	}

	@Override
	public List<Periodo> getPeriodoAbertos() {
		return periodoRepository.findByStatusOrderByAnoAscSemestreAsc(StatusPeriodo.ABERTO);
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
				reservaRepository.save(reserva);
				reservaService.salvarHistorico(reserva, Acao.ENCERRAMENTO, AutorAcao.SISTEMA, null);
			}
		}
		periodo.setStatus(StatusPeriodo.ENCERRADO);
		periodoRepository.save(periodo);
		ranking = rankingService.getRanking(this.getPeriodoPosterior(periodo), false);
		for (TuplaRanking tupla : ranking) {
			if(tupla.getStatus().equals(StatusTupla.DESCLASSIFICADO)) {
				Reserva reserva = tupla.getReserva();
				reserva.setStatus(StatusReserva.NAO_ACEITO);
				reservaRepository.save(reserva);
				reservaService.salvarHistorico(reserva, Acao.NAO_ACEITACAO, AutorAcao.SISTEMA, null);
			}
		}
		List<Reserva> reservasEmEspera = reservaService.getReservasByStatus(StatusReserva.EM_ESPERA);
		List<Reserva> reservasEmAberto = reservaService.getReservasByStatus(StatusReserva.ABERTO);
		
		for (Reserva reservaEspera : reservasEmEspera) {
			for (Reserva reservaAberto : reservasEmAberto) {
				if (reservaEspera.getProfessor().equals(reservaAberto.getProfessor())) {
					reservaAberto.setStatus(StatusReserva.CANCELADO);
					reservaRepository.save(reservaAberto);
					reservaService.salvarHistorico(reservaAberto, Acao.CANCELAMENTO, AutorAcao.SISTEMA, Constants.MSG_CANCELAMENTO_AUTOMATICO);
					notificacaoService.notificar(reservaAberto, Notificacao.RESERVA_CANCELADA, AutorAcao.SISTEMA);
					break;
				}
			}
		}
		
		for(Reserva reserva : reservasEmEspera) {
			reserva.setStatus(StatusReserva.ABERTO);
			reservaRepository.save(reserva);
			reservaService.salvarHistorico(reserva, Acao.INCLUSAO_RANKING, AutorAcao.SISTEMA, null);
			notificacaoService.notificar(reserva, Notificacao.RESERVA_INCLUIDA_RANKING, AutorAcao.SISTEMA);
		}
		
	}

	@Override
	public List<Periodo> getAll() {
		return periodoRepository.findAll();
	}

	@Override
	public Periodo findById(Long id) {
		return periodoRepository.findOne(id);
	}

	@Override
	public void atualizar(Periodo periodo) throws SiafException {
		if (periodo.getEncerramento().before(new Date())) {
			throw new SiafException(Constants.MSG_DATA_FUTURA);
		}
		periodoRepository.save(periodo);
	}

}

