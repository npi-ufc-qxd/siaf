package ufc.quixada.npi.afastamento.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import ufc.quixada.npi.afastamento.model.Acao;
import ufc.quixada.npi.afastamento.model.AutorAcao;
import ufc.quixada.npi.afastamento.model.Historico;
import ufc.quixada.npi.afastamento.model.Periodo;
import ufc.quixada.npi.afastamento.model.Professor;
import ufc.quixada.npi.afastamento.model.Reserva;
import ufc.quixada.npi.afastamento.model.StatusPeriodo;
import ufc.quixada.npi.afastamento.model.StatusReserva;
import ufc.quixada.npi.afastamento.service.PeriodoService;
import ufc.quixada.npi.afastamento.service.ProfessorService;
import ufc.quixada.npi.afastamento.service.ReservaService;
import br.ufc.quixada.npi.enumeration.QueryType;
import br.ufc.quixada.npi.repository.GenericRepository;
import br.ufc.quixada.npi.service.impl.GenericServiceImpl;

@Named
public class ReservaServiceImpl extends GenericServiceImpl<Reserva> implements ReservaService {

	@Inject
	private GenericRepository<Reserva> reservaRepository;

	@Inject
	private PeriodoService periodoService;

	@Inject
	private ProfessorService professorService;
	
	@Inject
	private GenericRepository<Historico> historicoRepository;

	@Override
	public void salvar(Reserva reserva) {
		int vagas = professorService.findAtivos().size();
		for (int ano = reserva.getAnoInicio(); ano <= reserva.getAnoTermino(); ano++) {
			Periodo periodo = new Periodo();
			periodo.setVagas((int) (vagas * 0.15));
			periodo.setAno(ano);
			periodo.setStatus(StatusPeriodo.ABERTO);
			if (ano == reserva.getAnoInicio() && reserva.getSemestreInicio() == 2) {
				periodo.setSemestre(2);
				if (periodoService.getPeriodo(periodo.getAno(), periodo.getSemestre()) == null) {
					periodoService.save(periodo);
				}
				continue;
			}
			if (ano == reserva.getAnoTermino() && reserva.getSemestreTermino() == 1) {
				periodo.setSemestre(1);
				if (periodoService.getPeriodo(periodo.getAno(), periodo.getSemestre()) == null) {
					periodoService.save(periodo);
				}
				break;
			}
			periodo.setSemestre(1);
			if (periodoService.getPeriodo(periodo.getAno(), periodo.getSemestre()) == null) {
				periodoService.save(periodo);
			}

			periodo = new Periodo();
			periodo.setAno(ano);
			periodo.setSemestre(2);
			periodo.setVagas((int) (vagas * 0.15));
			periodo.setStatus(StatusPeriodo.ABERTO);
			if (periodoService.getPeriodo(periodo.getAno(), periodo.getSemestre()) == null) {
				periodoService.save(periodo);
			}
		}
		reservaRepository.save(reserva);
	}

	@Override
	public List<Reserva> getReservasByProfessor(Professor professor) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cpf", professor.getUsuario().getCpf());
		return reservaRepository.find(QueryType.JPQL,
				"from Reserva where professor.usuario.cpf = :cpf order by dataSolicitacao DESC", params);
	}

	@Override
	public boolean hasReservaEmAberto(Professor professor) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cpf", professor.getUsuario().getCpf());
		return reservaRepository.find(QueryType.JPQL, "from Reserva where status = 'ABERTO' and professor.usuario.cpf = :cpf",
				params).size() > 0;
	}

	@Override
	public Reserva getReservaById(Long id) {
		return reservaRepository.find(Reserva.class, id);
	}

	@Override
	public List<Reserva> getReservasAnterioresComPunicao(Professor professor, Periodo periodo) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cpf", professor.getUsuario().getCpf());
		params.put("ano", periodo.getAno());
		params.put("semestre", periodo.getSemestre());
		params.put("status", StatusReserva.CANCELADO_COM_PUNICAO);
		return reservaRepository.find(QueryType.JPQL, "from Reserva where status = :status and professor.usuario.cpf = :cpf and (anoTermino < :ano or (anoTermino = :ano and semestreTermino < :semestre))", params);
	}

	@Override
	public void atualizar(Reserva reserva) {
		reservaRepository.update(reserva);
	}

	@Override
	public List<Reserva> getReservasByStatus(StatusReserva status) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", status);
		return reservaRepository.find(QueryType.JPQL,
				"from Reserva where status = :status order by anoInicio DESC, semestreInicio DESC", params);
	}

	@Override
	public List<Reserva> getReservasByStatusReservaAndPeriodo(StatusReserva statusReserva, Periodo periodo) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", statusReserva);
		params.put("ano", periodo.getAno());
		params.put("semestre", periodo.getSemestre());
		return reservaRepository.find(QueryType.JPQL, "from Reserva where status = :status and anoInicio = :ano and semestreInicio = :semestre order by anoInicio, semestreInicio", params);
	}

	@Override
	public List<Reserva> getAllReservas() {
		return reservaRepository.find(Reserva.class);
	}

	@Override
	public List<Reserva> getReservasByStatusReservaAndProfessor(
			StatusReserva statusReserva, Professor professor) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", statusReserva);
		params.put("id", professor.getId());
		return reservaRepository.find(QueryType.JPQL,
				"from Reserva where professor.id = :id and status = :status", params);
	}

	@Override
	public void salvarHistorico(Reserva reserva, Acao acao, AutorAcao autor, String comentario) {
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
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", reserva.getId());
		params.put("acao", acao);
		return historicoRepository.findFirst(QueryType.JPQL,
				"from Historico where reserva.id = :id and acao = :acao order by data desc", params, 0);
	}

}
