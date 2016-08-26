package ufc.quixada.npi.afastamento.service;

import java.util.List;
import java.util.Map;

import ufc.quixada.npi.afastamento.model.Periodo;
import ufc.quixada.npi.afastamento.model.RelatorioPeriodo;
import ufc.quixada.npi.afastamento.model.StatusReserva;
import ufc.quixada.npi.afastamento.model.TuplaRanking;


public interface RankingService {

	List<TuplaRanking> getRanking(Periodo periodo, boolean simulador);
	List<TuplaRanking> getTuplas(List<StatusReserva> status, Periodo periodo);
	Map<TuplaRanking, List<RelatorioPeriodo>> getRelatorio(Periodo periodo);
}
