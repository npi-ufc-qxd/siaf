package ufc.quixada.npi.afastamento.service;

import java.util.List;

import ufc.quixada.npi.afastamento.model.TuplaRanking;


public interface RankingService {

	List<TuplaRanking> visualizarRanking(Integer ano, Integer semestre);
	
}
