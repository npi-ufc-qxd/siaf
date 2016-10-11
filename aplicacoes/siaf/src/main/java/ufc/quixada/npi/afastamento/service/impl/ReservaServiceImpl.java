package ufc.quixada.npi.afastamento.service.impl;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import ufc.quixada.npi.afastamento.model.Acao;
import ufc.quixada.npi.afastamento.model.Afastamento;
import ufc.quixada.npi.afastamento.model.Historico;
import ufc.quixada.npi.afastamento.model.Periodo;
import ufc.quixada.npi.afastamento.model.Professor;
import ufc.quixada.npi.afastamento.model.Programa;
import ufc.quixada.npi.afastamento.model.Reserva;
import ufc.quixada.npi.afastamento.model.StatusPeriodo;
import ufc.quixada.npi.afastamento.model.StatusReserva;
import ufc.quixada.npi.afastamento.repository.HistoricoRepository;
import ufc.quixada.npi.afastamento.repository.PeriodoRespository;
import ufc.quixada.npi.afastamento.repository.ReservaRepository;
import ufc.quixada.npi.afastamento.service.AfastamentoService;
import ufc.quixada.npi.afastamento.service.PeriodoService;
import ufc.quixada.npi.afastamento.service.ProfessorService;
import ufc.quixada.npi.afastamento.service.ReservaService;
import ufc.quixada.npi.afastamento.util.Constants;
import ufc.quixada.npi.afastamento.util.SiafException;

@Named
public class ReservaServiceImpl implements ReservaService {

	@Inject
	private ReservaRepository reservaRepository;

	@Inject
	private PeriodoService periodoService;
	
	@Inject
	private PeriodoRespository periodoRepository;

	@Inject
	private ProfessorService professorService;
	
	@Inject
	private HistoricoRepository historicoRepository;
	
	@Inject
	private AfastamentoService afastamentoService;
	
	@Override
	public void incluir(Reserva reserva, Professor professor) throws SiafException {
		validaDadosReserva(reserva);
		
		// Verifica se o professor já possui reserva em espera
		List<Reserva> reservas = this.getReservasByStatusReservaAndProfessor(StatusReserva.EM_ESPERA, professor);
		if (reservas != null && !reservas.isEmpty()) {
			throw new SiafException(Constants.MSG_RESERVA_EM_ESPERA);
		}
		
		// Salva a reserva
		reserva.setProfessor(professor);
		reserva.setStatus(StatusReserva.EM_ESPERA);
		reserva.setDataSolicitacao(new Date());
		reservaRepository.save(reserva);
		this.criarPeriodos(reserva);
	}
	
	@Override
	public void atualizar(Reserva reserva) throws SiafException {
		validaDadosReserva(reserva);
		
		if (!reserva.getStatus().equals(StatusReserva.EM_ESPERA)) {
			throw new SiafException(Constants.MSG_PERMISSAO_NEGADA);
		}
		
		reservaRepository.save(reserva);
	}
	
	@Override
	public void excluir(Reserva reserva) throws SiafException {
		if(!reserva.getStatus().equals(StatusReserva.EM_ESPERA)) {
			throw new SiafException(Constants.MSG_PERMISSAO_NEGADA);
		}
		reservaRepository.delete(reserva);
	}
	
	@Override
	public void cancelar(Reserva reserva) throws SiafException {
		if(!reserva.getStatus().equals(StatusReserva.ABERTO)) {
			throw new SiafException(Constants.MSG_PERMISSAO_NEGADA);
		}
		reserva.setStatus(StatusReserva.CANCELADO);
		reservaRepository.save(reserva);
	}
	
	@Override
	public void homologar(Reserva reserva, StatusReserva status) {
		if(reserva.getStatus().equals(StatusReserva.AFASTADO) && !status.equals(StatusReserva.AFASTADO)) {
			afastamentoService.excluir(afastamentoService.getByReserva(reserva));
		}
		if (status.equals(StatusReserva.AFASTADO) && !reserva.getStatus().equals(StatusReserva.AFASTADO)) {
			Afastamento afastamento = new Afastamento(reserva);
			afastamentoService.salvar(afastamento);
		}
		
		reserva.setStatus(status);
		reservaRepository.save(reserva);
	}
	
	@Override
	public List<Reserva> getReservasByProfessor(Professor professor) {
		return reservaRepository.findByProfessor(professor);
	}

	@Override
	public Reserva getReservaById(Long id) {
		return reservaRepository.findOne(id);
	}

	@Override
	public List<Reserva> getReservasAnterioresComPunicao(Professor professor, Periodo periodo) {
		return reservaRepository.findAnterioresComPunicao(StatusReserva.CANCELADO_COM_PUNICAO, professor.getUsuario().getCpf(), periodo.getAno(), periodo.getSemestre());
	}

	@Override
	public List<Reserva> getReservasByStatus(StatusReserva status) {
		return reservaRepository.findByStatus(status);
	}

	@Override
	public List<Reserva> getReservasByStatusReservaAndPeriodo(StatusReserva statusReserva, Periodo periodo) {
		return reservaRepository.findByStatusAndAnoInicioAndSemestreInicio(statusReserva, periodo.getAno(), periodo.getSemestre());
	}

	@Override
	public List<Reserva> getAllReservas() {
		return reservaRepository.findAll();
	}

	@Override
	public List<Reserva> getReservasByStatusReservaAndProfessor(StatusReserva statusReserva, Professor professor) {
		return reservaRepository.findByStatusAndProfessor(statusReserva, professor);
	}

	@Override
	public void salvarHistorico(Reserva reserva, Acao acao, String autor) {
		this.salvarHistorico(reserva, acao, autor, null);
	}
	
	@Override
	public void salvarHistorico(Reserva reserva, Acao acao, String autor, String comentario) {
		Historico historico = new Historico();
		historico.setAcao(acao);
		historico.setComentario(comentario);
		historico.setData(new Date());
		historico.setAutor(autor);
		historico.setReserva(reserva);
		
		historicoRepository.save(historico);
	}

	@Override
	public Historico getUltimaAcao(Reserva reserva, Acao acao) {
		return historicoRepository.findFirstByReservaAndAcaoOrderByDataDesc(reserva, acao);
	}

	private Integer calculaSemestres(Integer anoInicio, Integer semestreInicio, Integer anoTermino, Integer semestreTermino) {
		return ((anoTermino - anoInicio) * 2) + (semestreTermino - semestreInicio);
	}
	
	private void criarPeriodos(Reserva reserva) {
		// Verifica se já existe período para todo o tempo da reserva
		if (periodoService.getPeriodo(reserva.getAnoTermino(), reserva.getSemestreTermino()) != null) {
			return;
		}
		
		int vagas = professorService.countAtivos();
		
		// Criar os períodos inexistentes
		for (int ano = reserva.getAnoTermino(); ; ano--) {
			Periodo periodo = new Periodo(ano, (int) (vagas * 0.15), StatusPeriodo.ABERTO);
			
			if (periodoService.getPeriodo(ano, 2) == null) {
				if (ano != reserva.getAnoTermino() || (ano == reserva.getAnoTermino() && reserva.getSemestreTermino() == 2)) {
					periodo.setSemestre(2);
					periodoRepository.save(periodo);
				}
			} else {
				break;
			}
			if (periodoService.getPeriodo(ano, 1) == null) {
				periodo = new Periodo(ano, (int) (vagas * 0.15), StatusPeriodo.ABERTO);
				periodo.setSemestre(1);
				periodoRepository.save(periodo);
				continue;
			} else {
				break;
			}
		}
	}
	
	private void validaDadosReserva(Reserva reserva) throws SiafException {
		// Valida preenchimento dos dados
		if (reserva.getAnoInicio() == null || reserva.getAnoTermino() == null) {
			throw new SiafException(Constants.MSG_CAMPOS_OBRIGATORIOS);
		}
		
		// Valida início e término
		if (reserva.getAnoTermino() < reserva.getAnoInicio() || (reserva.getAnoInicio().equals(reserva.getAnoTermino()) 
				&& reserva.getSemestreTermino() < reserva.getSemestreInicio())) {
			throw new SiafException(Constants.MSG_PERIODO_INVALIDO);
		}
		
		// Verifica se a solicitação está sendo feita com pelo menos 2 semestres de antecedência
		Periodo periodo = periodoService.getPeriodoAtual();
		Integer diferenca = calculaSemestres(periodo.getAno(), periodo.getSemestre(), reserva.getAnoInicio(), reserva.getSemestreInicio());
		if (diferenca < 2) {
			throw new SiafException(Constants.MSG_SOLICITACAO_FORA_DO_PRAZO);
		}
		
		// Verifica se a quantidade de semestres solicitados está de acordo com o programa
		if ((reserva.getPrograma() == Programa.MESTRADO || reserva.getPrograma() == Programa.POS_DOUTORADO)
				&& calculaSemestres(reserva.getAnoInicio(), reserva.getSemestreInicio(), reserva.getAnoTermino(), reserva.getSemestreTermino()) + 1 > 4) {
			throw new SiafException(Constants.MSG_TEMPO_MAXIMO_MESTRADO_POS_DOUTORADO);
		}
		if (reserva.getPrograma() == Programa.DOUTORADO && calculaSemestres(reserva.getAnoInicio(), 
				reserva.getSemestreInicio(), reserva.getAnoTermino(), reserva.getSemestreTermino()) + 1 > 8) {
			throw new SiafException(Constants.MSG_TEMPO_MAXIMO_DOUTORADO);
		}
	}

}
