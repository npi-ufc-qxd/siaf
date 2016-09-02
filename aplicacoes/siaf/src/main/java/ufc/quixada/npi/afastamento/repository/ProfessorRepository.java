package ufc.quixada.npi.afastamento.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ufc.quixada.npi.afastamento.model.Professor;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
	
	List<Professor> findByUsuarioHabilitado();
	
	Professor findByUsuarioCpf(String cpf);
	
	Integer countByUsuarioHabilitado();

}
