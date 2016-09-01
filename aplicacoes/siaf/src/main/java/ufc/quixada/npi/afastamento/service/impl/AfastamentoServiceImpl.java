package ufc.quixada.npi.afastamento.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import ufc.quixada.npi.afastamento.model.Afastamento;
import ufc.quixada.npi.afastamento.model.Reserva;
import ufc.quixada.npi.afastamento.service.AfastamentoService;
import br.ufc.quixada.npi.enumeration.QueryType;
import br.ufc.quixada.npi.repository.GenericRepository;
import br.ufc.quixada.npi.service.impl.GenericServiceImpl;

@Named
public class AfastamentoServiceImpl extends GenericServiceImpl<Afastamento> implements AfastamentoService {
	
	@Inject
	private GenericRepository<Afastamento> afastamentoRepository;
	
	@Override
	public List<Afastamento> getAfastamentosAnteriores(Reserva reserva) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cpf", reserva.getProfessor().getUsuario().getCpf());
		params.put("anoInicio", reserva.getAnoInicio());
		params.put("semestreInicio", reserva.getSemestreInicio());
		List<Afastamento> afs = afastamentoRepository.find(QueryType.JPQL, "from Afastamento where reserva.professor.usuario.cpf = :cpf "
				+ "and (reserva.anoInicio < :anoInicio or (reserva.anoInicio = :anoInicio and reserva.semestreInicio < :semestreInicio))", params);
		return afs;
	}

	@Override
	public Afastamento getByReserva(Reserva reserva) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", reserva.getId());
		List<Afastamento> afastamentos = afastamentoRepository.find(QueryType.JPQL, "from Afastamento where reserva.id = :id", params);
		if(afastamentos != null && !afastamentos.isEmpty()) {
			return afastamentos.get(0);
		}
		return null;
	}


}
