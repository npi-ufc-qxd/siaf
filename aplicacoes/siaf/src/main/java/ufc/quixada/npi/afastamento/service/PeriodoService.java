package ufc.quixada.npi.afastamento.service;

import java.util.List;

import ufc.quixada.npi.afastamento.model.Periodo;
import ufc.quixada.npi.afastamento.util.SiafException;

public interface PeriodoService {
	
	Periodo getPeriodo(Integer ano, Integer semestre);
	
	Periodo getPeriodoAtual();
	
	Periodo getPeriodoAnterior(Periodo periodo);
	
	Periodo getPeriodoPosterior(Periodo periodo);
	
	Periodo findById(Long id);
	
	void atualizar(Periodo periodo) throws SiafException;
	
	List<Periodo> getPeriodosPosteriores(Periodo periodo);
	
	List<Periodo> getPeriodoAbertos();
	
	List<Periodo> getAll();
	
	void encerrarPeriodo(Periodo periodo);
}
