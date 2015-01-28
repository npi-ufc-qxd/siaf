package ufc.quixada.npi.afastamento.service.impl;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import ufc.quixada.npi.afastamento.model.Periodo;
import ufc.quixada.npi.afastamento.repository.PeriodoRepository;
import ufc.quixada.npi.afastamento.service.PeriodoService;
import br.ufc.quixada.npi.enumeration.QueryType;
import br.ufc.quixada.npi.service.impl.GenericServiceImpl;

@Named
public class PeriodoServiceImpl extends GenericServiceImpl<Periodo> implements PeriodoService {

	@Inject
	private PeriodoRepository periodoRepository;

	@Override
	public Periodo getPeriodo(Integer ano, Integer semestre) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ano", ano);
		params.put("semestre", semestre);
		return periodoRepository.findFirst(QueryType.JPQL, "select p from Periodo p where p.ano = :ano and p.semestre = :semestre", params, -1);
	}

	@Override
	public Periodo getPeriodo(Date date) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("encerramento", date);
		return periodoRepository.findFirst(QueryType.JPQL, "from Periodo p where encerramento = :encerramento", params, -1);
	}
	
	@Override
	public Periodo getPeriodoAnterior(Periodo periodo) {
		if(periodo.getSemestre() == 2) {
			return getPeriodo(periodo.getAno(), 1);
		}
		return getPeriodo(periodo.getAno() - 1, 2);
	}

	@Override
	public Periodo getPeriodoPosterior(Periodo periodo) {
		if(periodo.getSemestre() == 1) {
			return getPeriodo(periodo.getAno(), 2);
		}
		return getPeriodo(periodo.getAno() + 1, 1);
	}
	
	@Override
	public Periodo getPeriodoAtual() {
		return periodoRepository.findFirst(QueryType.JPQL, "from Periodo p where status = 'ABERTO' order by ano ASC, semestre ASC", null, -1);
	}
	
	@Override
	public Integer getSemestreAtual() {
		Calendar calendar = Calendar.getInstance();
		if(calendar.get(Calendar.MONTH) < 6) {
			return 1;
		}
		return 2;
	}
	
	@Override
	public Integer getAnoAtual() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.YEAR);
	}

	@Override
	public void updateVagas(int vagas) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vagas", vagas);
		periodoRepository.updateVagas("update Periodo p set vagas = :vagas where p.status = 'ABERTO'", params);
	}

}

