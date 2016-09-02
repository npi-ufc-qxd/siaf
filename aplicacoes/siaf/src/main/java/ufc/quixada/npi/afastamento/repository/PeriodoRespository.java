package ufc.quixada.npi.afastamento.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ufc.quixada.npi.afastamento.model.Periodo;
import ufc.quixada.npi.afastamento.model.StatusPeriodo;

@Repository
public interface PeriodoRespository extends JpaRepository<Periodo, Long> {
	
	Periodo findByAnoAndSemestre(Integer ano, Integer semestre);
	
	Periodo findFirstByStatusOrderByAnoAscSemestreAsc(StatusPeriodo status);
	
	List<Periodo> findByStatusOrderByAnoAscSemestreAsc(StatusPeriodo status);
	
	Periodo findByStatusOrderByAnoDescSemestreDesc(StatusPeriodo status);
	
	@Query(value = "from Periodo where ano > :ano or (ano = :ano and semestre >= :semestre)")
	List<Periodo> findPeriodosPosteriores(@Param("ano") Integer ano, @Param("semestre") Integer semestre);

}
