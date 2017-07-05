<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
<jsp:include page="../modulos/header-estrutura.jsp" />
<title>Prévia do Ranking</title>
</head>
<body>
	<div id="wrapper">
		<jsp:include page="../modulos/header.jsp" />
		<div id="content">
			<c:if test="${not empty erro}">
				<div class="alert alert-danger alert-dismissible" role="alert">
					<button type="button" class="close" data-dismiss="alert">
						<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
					</button>
					<c:out value="${erro}"></c:out>
				</div>
			</c:if>
			<c:if test="${not empty info}">
				<div class="alert alert-info alert-dismissible" role="alert">
					<button type="button" class="close" data-dismiss="alert">
						<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
					</button>
					<c:out value="${info}"></c:out>
				</div>
			</c:if>

			<input type="hidden" id="ano" value="${periodoAtual.ano }" />
			<input type="hidden" id="semestre" value="${periodoAtual.semestre }" />
			<input type="hidden" id="anoAtual" value="${periodoAtual.ano }" />
			<input type="hidden" id="semestreAtual" value="${periodoAtual.semestre }" />
			<input type="hidden" id="anoPosterior" value="${periodoPosterior.ano }" />
			<input type="hidden" id="semestrePosterior" value="${periodoPosterior.semestre }" />
			<input type="hidden" id="anoAnterior" value="${periodoAnterior.ano }" />
			<input type="hidden" id="semestreAnterior" value="${periodoAnterior.semestre }" />
			
			<div id="ranking-full">
				<div id="warning-buscar-periodo" class="alert alert-warning" role="alert">
					Não é possivel realizar busca por períodos passados.
				</div>
				<div id="wrap-periodo">			
					<div role="main">
						<div class="row">
							<div role="main" class="col-md-6 col-md-push-3">
								<div id="buscarRanking" class="align-center">
									<i id="anterior-simulador" class="fa fa-chevron-circle-left fa-2x" title="Anterior"></i>
									<input id="anoBuscado" name="anoBuscado" type="text" name="ano" class="ano form-control" size="10" value="${periodoAtual.ano }"/>
									<select id="semestreBuscado" name="semestreBuscado" class="selectpicker">
										<option value="1" ${periodoAtual.semestre == 1 ? 'selected' : ''}>1</option>
										<option value="2" ${periodoAtual.semestre == 2 ? 'selected' : ''}>2</option>
									</select>
									<input id="buscar-simulador" name="buscar" type="submit" class="btn btn-siaf" value="Buscar" />
									<i id="posterior-simulador" class="fa fa-chevron-circle-right fa-2x" title="Próximo"></i>	
								</div>
								<div id="dados-periodo" class="align-center">
									<label>Data de encerramento: <span id="encerramento" class="value-label" data-toggle="popover" data-placement="right" data-content=""></span></label>
									| <label>Total estimado de vagas: <span id="vagas" class="badge"></span></label>
								</div>
							</div>
						</div>
					</div>
				</div>
				
				<div class="titulo-ranking title" role="navigation">
					<a data-toggle="collapse" href="#collapseRanking"
						aria-controls="#collapseRanking">Prévia - Solicitações de Reserva</a><br> <span
						id="count-ranking" class="badge"></span>
				</div>
				
				<div class="collapse in" id="collapseRanking">
					<label><span class="classificado">&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;Classificado</label>
					<label><span class="desclassificado">&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;Desclassificado</label><br>
					<table id="ranking" class="table">
						<thead>
							<tr>
								<th>#</th>
								<th>Nome</th>
								<th><span data-toggle="popover" data-placement="top"
									data-content="Número de semestres desde a contratação na UFC até o início do afastamento">T</span></th>
								<th><span data-toggle="popover" data-placement="top"
									data-content="Número de semestres em que o docente já esteve afastado">A</span></th>
								<th><span data-toggle="popover" data-placement="top"
									data-content="Número de semestres do afastamento reservado/solicitado. No caso de primeiro afastamento, a variável S terá valor dois (2)">S</span></th>
								<th><span data-toggle="popover" data-placement="top"
									data-content="Número de semestres que faltam para o docente completar três (3) anos de contratação">P</span></th>
								<th><span data-toggle="popover" data-placement="top"
									data-content="Número de semestres solicitados">SS</span></th>
								<th>Período</th>
								<th>Programa / Conceito</th>
								<th>Pontuação</th>
							</tr>
						</thead>
					</table>

					<div id="warning-ranking" class="alert warning alert-warning alert-dismissible" role="alert">
						Não há nenhuma reserva em aberto para esse período.</div>
				</div>
				
				<div class="titulo-ranking title">
					<a data-toggle="collapse" href="#collapseAfastados" aria-expanded="false" aria-controls="#collapseAfastados">
						Prévia - Afastamentos/Em Processo de Afastamento</a><br>
					<span id="count-afastados" class="badge"></span>
				</div>

				<div class="collapse in" id="collapseAfastados">
					<label><span class="afastado">&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;Afastado</label>
					<label><span class="classificado">&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;Em processo de afastamento</label><br>
					<table id="afastados" class="table">
						<thead>
							<tr>
								<th>Nome</th>
								<th>Período</th>
								<th>Programa</th>
							</tr>
						</thead>
					</table>

					<div id="warning-afastados" class="alert warning alert-warning alert-dismissible" role="alert">
						Ainda não há nenhum docente com afastamento confirmado para esse período.
					</div>
				</div>

				<div id="legenda">
					<label><span>Fórmula:</span> R = (T – A) / (5 x A + S + P)</label><br />
					<label><span>T:</span> Número de semestres desde a
						contratação na UFC até o início do afastamento, iniciando no
						primeiro semestre em que o solicitante teve disciplina alocada no
						Campus Quixadá.</label><br /> <label><span>A:</span> Número de
						semestres em que o docente já esteve afastado para programas de
						pós-graduação stricto sensu ou pós-doutorados.</label><br /> <label><span>S:</span>
						Número de semestres do afastamento reservado/solicitado. (No caso
						de primeiro afastamento, a variável S terá valor dois (2)
						independente da duração do período reservado/solicitado).</label><br /> <label><span>P:</span>
						Número de semestres que faltam para o docente completar três (3)
						anos de contratação na UFC Quixadá (vale zero se já cumpriu este
						período).</label><br /> <label><span>SS:</span> Número de
						semestres do afastamento reservado/solicitado. </label> <label><span>Obs:</span>
						Será considerado como um afastamento com duração de um (1)
						semestre, para efeito de cálculo da ordem de prioridade das
						reservas/solicitações, a solicitação cancelada de acordo com os
						critérios da <a
						href="<c:url value="/resources/files/Resolucao_01-2014.pdf" />">resolução</a>.</label><br>
					<br> <label>Em caso de empate na ordem de prioridade,
						serão considerados os critérios abaixo, na ordem indicada:</label> <label><span>I:</span>
						Mestrado tem maior prioridade que doutorado, e doutorado tem maior
						prioridade que pós-doutorado.</label><br> <label><span>II:</span>
						Prioridade para programas com melhor conceito.</label><br> <label><span>III:</span>
						Prioridade para o candidato mais velho.</label>
				</div>
			</div>
			<div id="load-siaf">
				<div class="title">Carregando ranking...</div>
				<div class=loading-siaf></div>
				<div class="load-s"></div>
			</div>
		</div>

		<jsp:include page="../modulos/footer.jsp" />

	</div>
</body>
<script type="text/javascript">
	$('i#anterior').hide();
	getRanking($('#ano').val(), $('#semestre').val(), true);
	$('#menu-previa-ranking').addClass('active');
</script>
</html>

