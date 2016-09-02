package ufc.quixada.npi.afastamento.model;

import javax.persistence.PostLoad;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import br.ufc.quixada.npi.ldap.service.UsuarioService;

public class UsuarioEntityListener implements ApplicationContextAware {
	
	private static ApplicationContext context;
	
	@PostLoad
	public void loadProfessor(Usuario usuario) {
		UsuarioService usuarioService = (UsuarioService) context.getBean(UsuarioService.class);
		br.ufc.quixada.npi.ldap.model.Usuario usuarioLdap = usuarioService.getByCpf(usuario.getCpf());
		usuario.setNome(usuarioLdap.getNome().toUpperCase());
		usuario.setEmail(usuarioLdap.getEmail());
		usuario.setNascimento(usuarioLdap.getNascimento());
		usuario.setSiape(usuarioLdap.getSiape());
	}
	
	public ApplicationContext getApplicationContext() {
        return context;
    }
 
    @Override
    public void setApplicationContext(ApplicationContext ctx) {
        context = ctx;
    }

}
