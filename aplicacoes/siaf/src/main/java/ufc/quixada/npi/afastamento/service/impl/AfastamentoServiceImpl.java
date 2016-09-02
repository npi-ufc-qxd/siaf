package ufc.quixada.npi.afastamento.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import ufc.quixada.npi.afastamento.model.Afastamento;
import ufc.quixada.npi.afastamento.model.Reserva;
import ufc.quixada.npi.afastamento.repository.AfastamentoRepository;
import ufc.quixada.npi.afastamento.service.AfastamentoService;

@Named
public class AfastamentoServiceImpl implements AfastamentoService {
	
	@Inject
	private AfastamentoRepository afastamentoRepository;
	
	@Override
	public List<Afastamento> getAfastamentosAnteriores(Reserva reserva) {
		return afastamentoRepository.getAfastamentosAnteriores(reserva.getProfessor().getUsuario().getCpf(), reserva.getAnoInicio(), reserva.getSemestreInicio());
	}

	@Override
	public Afastamento getByReserva(Reserva reserva) {
		return afastamentoRepository.getByReserva(reserva);
	}

	@Override
	public void excluir(Afastamento afastamento) {
		afastamentoRepository.delete(afastamento);
	}

	@Override
	public void salvar(Afastamento afastamento) {
		afastamentoRepository.save(afastamento);
	}


}
