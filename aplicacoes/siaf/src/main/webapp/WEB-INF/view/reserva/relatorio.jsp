<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
	<jsp:include page="../modulos/header-estrutura.jsp" />
	<title>Relatório</title>
</head>
<body>
	<fmt:setLocale value="pt"/>
	<div id="wrapper" class="container">
		<jsp:include page="../modulos/header.jsp" />
		<div id="content">
			<div id="relatorio-header" class="title">
				<label>Ranking ${periodoAtual.ano }.${periodoAtual.semestre } para afastamento em ${proximoPeriodo.ano }.${proximoPeriodo.semestre }</label>
				<span>gerado em <fmt:formatDate pattern="dd/MM/yyyy - HH:mm:ss" value="${dataAtual }" /></span>
			</div>
			<table id="tableRelatorio" class="display nowrap order-column">
				<thead>
					<tr>
						<th class="nosort">Status</th>
						<th>Nome</th>
						<th>Pont.</th>
						<c:forEach items="${periodos}" var="periodo" varStatus="cont">
							<th class="nosort">${periodo.ano }.${periodo.semestre }<br/>(${periodo.vagas })</th>
						</c:forEach>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${relatorio}" var="r">
						<tr class="${r.key.status }">
							<td>
								<c:if test="${r.key.status == 'CLASSIFICADO'}">
									<i class="fa fa-check"></i>
								</c:if>
								<c:if test="${r.key.status == 'AFASTADO'}">
									<i class="fa fa-check-square"></i>
								</c:if>
								<c:if test="${r.key.status == 'DESCLASSIFICADO'}">
									<i class="fa fa-times"></i>
								</c:if>
							</td>
							<td class="align-left">
								${r.key.professor }<br/>
								${r.key.reserva.anoInicio }.${r.key.reserva.semestreInicio } a ${r.key.reserva.anoTermino }.${r.key.reserva.semestreTermino }<br/>
								${r.key.reserva.programa.descricao }<br/>
							</td>
							<td>
								<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${r.key.pontuacao }" />
							</td>
							<c:forEach items="${periodos}" var="periodo">
								<c:forEach items="${r.value }" var="rp">
									<c:if test="${periodo.ano eq rp.ano and periodo.semestre eq rp.semestre}">
										<c:set var="teste" value="${rp.status.descricao }"></c:set>
									</c:if>
								</c:forEach>
								<td>
									<c:if test="${teste == 'CLASSIFICADO'}">
										<i class="fa fa-check"></i>
									</c:if>
									<c:if test="${teste == 'AFASTADO'}">
										<i class="fa fa-check-square"></i>
									</c:if>
									<c:if test="${teste == 'DESCLASSIFICADO' }">
										<i class="fa fa-times"></i>
									</c:if>
									<c:if test="${teste == '' }">
										<span>-</span>
									</c:if>
								</td>
								<c:set var="teste" value=""></c:set>
							</c:forEach>
		           		</tr>
		           	</c:forEach>
				</tbody>
			</table>
			<div id="relatorio-legenda">
				<label><i class="fa fa-check"></i><span> CLASSIFICADO</span></label>
				<label><span><i class="fa fa-times"></i> NÃO CLASSIFICADO</span></label>
				<label><span><i class="fa fa-check-square"></i> AFASTADO</span></label>
			</div>
		</div>

		<jsp:include page="../modulos/footer.jsp" />

	</div>
</body>
<script type="text/javascript">
	$('#menu-mapa-ranking').addClass('active');
	
</script>
</html>

