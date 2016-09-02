package ufc.quixada.npi.afastamento.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ufc.quixada.npi.afastamento.model.Professor;
import ufc.quixada.npi.afastamento.model.Reserva;
import ufc.quixada.npi.afastamento.model.StatusReserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
	
	List<Reserva> findByProfessor(Professor professor);
	
	List<Reserva> findByStatus(StatusReserva status);
	
	List<Reserva> findByStatusAndProfessor(StatusReserva status, Professor professor);
	
	List<Reserva> findByStatusAndAnoInicioAndSemestreInicio(StatusReserva status, Integer anoInicio, Integer semestreInicio);
	
	@Query(value = "from Reserva where status = :status and professor.usuario.cpf = :cpf and (anoTermino < :ano or (anoTermino = :ano and semestreTermino < :semestre))")
	List<Reserva> findAnterioresComPunicao(@Param("status") StatusReserva status, @Param("cpf") String cpf, @Param("ano") Integer ano, @Param("semestre") Integer semestre);

}
