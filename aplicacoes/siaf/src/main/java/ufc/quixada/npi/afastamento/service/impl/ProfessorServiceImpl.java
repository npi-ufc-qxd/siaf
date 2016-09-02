package ufc.quixada.npi.afastamento.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import ufc.quixada.npi.afastamento.model.Professor;
import ufc.quixada.npi.afastamento.repository.ProfessorRepository;
import ufc.quixada.npi.afastamento.service.ProfessorService;

@Named
public class ProfessorServiceImpl implements ProfessorService {

	@Inject
	private ProfessorRepository professorRepository;
	
	@Override
	public List<Professor> findAtivos() {
		return professorRepository.findByUsuarioHabilitadoTrue();
	}

	@Override
	public Professor findByCpf(String cpf) {
		return professorRepository.findByUsuarioCpf(cpf);
	}

	@Override
	public void salvar(Professor professor) {
		professorRepository.save(professor);
	}

	@Override
	public Professor findById(Long id) {
		return professorRepository.findOne(id);
	}

	@Override
	public void atualizar(Professor professor) {
		professorRepository.save(professor);
	}

	@Override
	public Integer countAtivos() {
		return professorRepository.countByUsuarioHabilitadoTrue();
	}

	@Override
	public List<Professor> findAll() {
		return professorRepository.findAll();
	}

}
