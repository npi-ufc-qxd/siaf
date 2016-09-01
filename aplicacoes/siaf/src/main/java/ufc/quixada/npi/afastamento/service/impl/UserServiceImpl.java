package ufc.quixada.npi.afastamento.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import br.ufc.quixada.npi.enumeration.QueryType;
import br.ufc.quixada.npi.repository.GenericRepository;
import br.ufc.quixada.npi.service.impl.GenericServiceImpl;
import ufc.quixada.npi.afastamento.model.Usuario;
import ufc.quixada.npi.afastamento.service.UserService;

@Named
public class UserServiceImpl extends GenericServiceImpl<Usuario> implements UserService {
	
	@Inject
	private GenericRepository<Usuario> usuarioRepository;
	
	@Override
	public Usuario getByCpf(String cpf) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cpf", cpf);
		return usuarioRepository.findFirst(QueryType.JPQL, "from Usuario where cpf = :cpf", params, -1);
	}

}
