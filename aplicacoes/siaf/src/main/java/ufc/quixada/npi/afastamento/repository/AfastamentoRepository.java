package ufc.quixada.npi.afastamento.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ufc.quixada.npi.afastamento.model.Afastamento;
import ufc.quixada.npi.afastamento.model.Reserva;

@Repository
public interface AfastamentoRepository extends JpaRepository<Afastamento, Long> {
	
	Afastamento getByReserva(Reserva reserva);
	
	@Query(value = "from Afastamento where reserva.professor.usuario.cpf = :cpf and (reserva.anoInicio < :anoInicio or (reserva.anoInicio = :anoInicio and reserva.semestreInicio < :semestreInicio))")
	List<Afastamento> getAfastamentosAnteriores(@Param("cpf") String cpf, @Param("anoInicio") Integer ano, @Param("semestreInicio") Integer semestre);

}
