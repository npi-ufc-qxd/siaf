package ufc.quixada.npi.afastamento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ufc.quixada.npi.afastamento.model.Acao;
import ufc.quixada.npi.afastamento.model.Historico;
import ufc.quixada.npi.afastamento.model.Reserva;

@Repository
public interface HistoricoRepository extends JpaRepository<Historico, Long> {
	
	Historico findFirstByReservaAndAcaoOrderByDataDesc(Reserva reserva, Acao acao);

}
