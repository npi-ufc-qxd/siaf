package ufc.quixada.npi.afastamento.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.cache.annotation.Cacheable;

import ufc.quixada.npi.afastamento.model.Professor;
import ufc.quixada.npi.afastamento.service.ProfessorService;
import br.ufc.quixada.npi.enumeration.QueryType;
import br.ufc.quixada.npi.ldap.model.Constants;
import br.ufc.quixada.npi.repository.GenericRepository;
import br.ufc.quixada.npi.service.impl.GenericServiceImpl;

@Named
public class ProfessorServiceImpl extends GenericServiceImpl<Professor> implements ProfessorService {

	@Inject
	private GenericRepository<Professor> professorRepository;
	
	@Override
	@Cacheable("professores")
	public List<Professor> findAtivos() {
		List<Professor> professores = professorRepository.find(Professor.class);
		List<Professor> ativos = new ArrayList<Professor>();
		for(Professor professor : professores) {
			SimpleDateFormat format = new SimpleDateFormat(br.ufc.quixada.npi.ldap.model.Constants.FORMATO_DATA_AFILIACAO);
			try {
				if(professor.getDataSaida() == null) {
					ativos.add(professor);
					continue;
				}
				Date saida = format.parse(format.format(professor.getDataSaida()));
				Date hoje = format.parse(format.format(new Date()));
				if(!saida.before(hoje)) {
					ativos.add(professor);
				}
			} catch (ParseException e) {
				continue;
			}
		}
		return ativos;
	}

	@Override
	public Professor getByCpf(String cpf) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cpf", cpf);
		return professorRepository.findFirst(QueryType.JPQL, "select p from Professor p where cpf = :cpf", params, -1);
	}

}
