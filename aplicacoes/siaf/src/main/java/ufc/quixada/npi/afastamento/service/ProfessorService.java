package ufc.quixada.npi.afastamento.service;

import java.util.List;

import ufc.quixada.npi.afastamento.model.Professor;

public interface ProfessorService {
	
	List<Professor> findAtivos();
	
	List<Professor> findAll();
	
	Integer countAtivos();
	
	Professor findByCpf(String cpf);
	
	Professor findById(Long id);
	
	void salvar(Professor professor);
	
	void atualizar(Professor professor);

}
