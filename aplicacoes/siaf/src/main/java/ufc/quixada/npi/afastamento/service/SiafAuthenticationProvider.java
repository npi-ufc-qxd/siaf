package ufc.quixada.npi.afastamento.service;

import static br.ufc.quixada.npi.ldap.model.Constants.LOGIN_INVALIDO;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import br.ufc.quixada.npi.ldap.service.UsuarioService;
import ufc.quixada.npi.afastamento.model.Usuario;

@Named
public class SiafAuthenticationProvider implements AuthenticationProvider {
	
	@Inject
	private UsuarioService usuarioService;
	
	@Inject
	private UserService userService;
	
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {

		String username = authentication.getName();
        String password = (String) authentication.getCredentials();
 
        Usuario user = userService.getByCpf(username);
 
        if (user == null || !user.isHabilitado() || !usuarioService.autentica(username, password) || user.getPapeis().isEmpty()) {
            throw new BadCredentialsException(LOGIN_INVALIDO);
        }
 
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
 
        return new UsernamePasswordAuthenticationToken(user, password, authorities);
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return true;
	}

}
