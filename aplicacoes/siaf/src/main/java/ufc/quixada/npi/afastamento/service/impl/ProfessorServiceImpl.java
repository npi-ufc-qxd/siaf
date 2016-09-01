package ufc.quixada.npi.afastamento.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import br.ufc.quixada.npi.enumeration.QueryType;
import br.ufc.quixada.npi.repository.GenericRepository;
import br.ufc.quixada.npi.service.impl.GenericServiceImpl;
import ufc.quixada.npi.afastamento.model.Professor;
import ufc.quixada.npi.afastamento.service.ProfessorService;

@Named
public class ProfessorServiceImpl extends GenericServiceImpl<Professor> implements ProfessorService {

	@Inject
	private GenericRepository<Professor> professorRepository;
	
	@Override
	public List<Professor> findAtivos() {
		return professorRepository.find(QueryType.JPQL, "select p from Professor p where usuario.habilitado = true", null);
	}

	@Override
	public Professor getByCpf(String cpf) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cpf", cpf);
		return professorRepository.findFirst(QueryType.JPQL, "select p from Professor p where cpf = :cpf", params, -1);
	}

}
