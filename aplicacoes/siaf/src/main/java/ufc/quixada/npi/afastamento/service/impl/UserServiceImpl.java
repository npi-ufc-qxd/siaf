package ufc.quixada.npi.afastamento.service.impl;

import javax.inject.Inject;
import javax.inject.Named;

import ufc.quixada.npi.afastamento.model.Usuario;
import ufc.quixada.npi.afastamento.repository.UsuarioRepository;
import ufc.quixada.npi.afastamento.service.UserService;

@Named
public class UserServiceImpl implements UserService {
	
	@Inject
	private UsuarioRepository usuarioRepository;
	
	@Override
	public Usuario getByCpf(String cpf) {
		return usuarioRepository.getByCpf(cpf);
	}

}
