package ufc.quixada.npi.afastamento.service;

import java.util.List;

import ufc.quixada.npi.afastamento.model.Acao;
import ufc.quixada.npi.afastamento.model.Historico;
import ufc.quixada.npi.afastamento.model.Periodo;
import ufc.quixada.npi.afastamento.model.Professor;
import ufc.quixada.npi.afastamento.model.Reserva;
import ufc.quixada.npi.afastamento.model.StatusReserva;
import ufc.quixada.npi.afastamento.util.SiafException;

public interface ReservaService {

	void incluir(Reserva reserva, Professor professor) throws SiafException;
	
	void atualizar(Reserva reserva) throws SiafException;
	
	void excluir(Reserva reserva) throws SiafException;
	
	void cancelar(Reserva reserva) throws SiafException;
	
	void homologar(Reserva reserva, StatusReserva status);
	
	List<Reserva> getReservasByProfessor(Professor professor);
	
	Reserva getReservaById(Long id);
	
	List<Reserva> getReservasAnterioresComPunicao(Professor professor, Periodo periodo);
	
	List<Reserva> getReservasByStatus(StatusReserva status);

	List<Reserva> getReservasByStatusReservaAndPeriodo(StatusReserva statusReserva, Periodo periodo);
	
	List<Reserva> getAllReservas();
	
	List<Reserva> getReservasByStatusReservaAndProfessor(StatusReserva statusReserva, Professor professor);
	
	void salvarHistorico(Reserva reserva, Acao acao, String autor, String comentario);
	
	void salvarHistorico(Reserva reserva, Acao acao, String autor);

	Historico getUltimaAcao(Reserva reserva, Acao acao);
}
