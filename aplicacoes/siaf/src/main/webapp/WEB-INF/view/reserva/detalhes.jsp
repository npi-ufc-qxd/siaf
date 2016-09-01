<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<jsp:include page="../modulos/header-estrutura.jsp" />
	<title>Reserva - Detalhes</title>
</head>
<body>
	<div id="wrapper">
		<jsp:include page="../modulos/header.jsp" />
		<div id="content">
			
			<div class="form-horizontal">
				<div class="title">Professor : ${reserva.professor.usuario.nome }</div>
				<span class="line"></span>
				<div class="form-group">
					<label class="col-sm-2 control-label">Período:</label>
					<div class="col-sm-4">
						<label class="control-label value-label">${reserva.anoInicio}.${reserva.semestreInicio } a ${reserva.anoTermino}.${reserva.semestreTermino }</label>
					</div>
					<label class="col-sm-2 control-label">Status:</label>
					<div class="col-sm-4">
						<label class="control-label value-label">${reserva.status.descricao}</label>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">Programa:</label>
					<div class="col-sm-4">
						<label class="control-label value-label">${reserva.programa.descricao }</label>
					</div>
					<label class="col-sm-2 control-label">Conceito programa:</label>
					<div class="col-sm-4">
						<label class="control-label value-label">${reserva.conceitoPrograma}</label>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">Instituição:</label>
					<div class="col-sm-4">
						<c:if test="${empty reserva.instituicao }">
							<label class="control-label value-label">-</label>
						</c:if>
						<c:if test="${not empty reserva.instituicao }">
							<label class="control-label value-label">${reserva.instituicao}</label>
						</c:if>
					</div>
				</div>
				<div class="title">Histórico : </div>
				<span class="line"></span>
				<c:if test="${empty reserva.historicos}">
					<div class="alert warning alert-warning alert-dismissible" role="alert">
						Não há histórico registrado para esta reserva.
					</div>
				</c:if>
				<table id="tableHistorico" class="display">
					<tbody>
						<c:forEach items="${reserva.historicos }" var="historico">
							<tr>
								<td width="13%">
									<span class="label label-warning"><fmt:formatDate pattern="dd/MM/yyyy - HH:mm" value="${historico.data }" /></span>
								</td>
								<td>
									<c:choose>
										<c:when test="${historico.autor == 'SISTEMA' }"><c:set var="autor" value="O Sistema"></c:set></c:when>
										<c:when test="${historico.autor == 'PROFESSOR' }"><c:set var="autor" value="${reserva.professor.usuario.nome }"></c:set> </c:when>
										<c:when test="${historico.autor == 'ADMINISTRADOR' }"><c:set var="autor" value="O Administrador"></c:set> </c:when>
									</c:choose>
									<c:choose>
										<c:when test="${historico.acao == 'CRIACAO' }">
											${autor } criou a reserva.
										</c:when>
										<c:when test="${historico.acao == 'EDICAO' }">
											${autor } alterou a reserva.
										</c:when>
										<c:when test="${historico.acao == 'INCLUSAO_RANKING' }">
											${autor } incluiu a reserva no ranking.
										</c:when>
										<c:when test="${historico.acao == 'CANCELAMENTO' }">
											${autor } cancelou a reserva. MOTIVO: ${historico.comentario }
										</c:when>
										<c:when test="${historico.acao == 'CANCELAMENTO_COM_PUNICAO' }">
											${autor } cancelou a reserva com punição. MOTIVO: ${historico.comentario }
										</c:when>
										<c:when test="${historico.acao == 'NEGACAO' }">
											${autor } negou a solicitação da reserva. MOTIVO: ${historico.comentario }
										</c:when>
										<c:when test="${historico.acao == 'AFASTAMENTO' }">
											${autor } confirmou o afastamento da reserva.
										</c:when>
										<c:when test="${historico.acao == 'NAO_ACEITACAO' }">
											A reserva não foi classificada dentro das vagas no ranking.
										</c:when>
										<c:when test="${historico.acao == 'ENCERRAMENTO' }">
											A reserva foi encerrada.
										</c:when>
									</c:choose>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
				<%-- <c:forEach items="${reserva.historicos }" var="historico">
					<p>
						<span class="label label-warning"><fmt:formatDate pattern="dd/MM/yyyy - HH:mm" value="${historico.data }" /></span>  
						<c:choose>
							<c:when test="${historico.autor == 'SISTEMA' }"><c:set var="autor" value="O Sistema"></c:set></c:when>
							<c:when test="${historico.autor == 'PROFESSOR' }"><c:set var="autor" value="${reserva.professor.nome }"></c:set> </c:when>
							<c:when test="${historico.autor == 'ADMINISTRADOR' }"><c:set var="autor" value="O Administrador"></c:set> </c:when>
						</c:choose>
						<c:choose>
							<c:when test="${historico.acao == 'CRIACAO' }">
								${autor } criou a reserva.
							</c:when>
							<c:when test="${historico.acao == 'EDICAO' }">
								${autor } alterou a reserva.
							</c:when>
							<c:when test="${historico.acao == 'INCLUSAO_RANKING' }">
								${autor } incluiu a reserva no ranking.
							</c:when>
							<c:when test="${historico.acao == 'CANCELAMENTO' }">
								${autor } cancelou a reserva. MOTIVO: ${historico.comentario }
							</c:when>
							<c:when test="${historico.acao == 'CANCELAMENTO_COM_PUNICAO' }">
								${autor } cancelou a reserva com punição. MOTIVO: ${historico.comentario }
							</c:when>
							<c:when test="${historico.acao == 'NEGACAO' }">
								${autor } negou a solicitação da reserva. MOTIVO: ${historico.comentario }
							</c:when>
							<c:when test="${historico.acao == 'AFASTAMENTO' }">
								${autor } confirmou o afastamento da reserva.
							</c:when>
							<c:when test="${historico.acao == 'NAO_ACEITACAO' }">
								A reserva não foi classificada dentro das vagas no ranking.
							</c:when>
							<c:when test="${historico.acao == 'ENCERRAMENTO' }">
								A reserva foi encerrada.
							</c:when>
						</c:choose>
					</p>
				</c:forEach> --%>
							
				
			</div>
		</div>
		
		<jsp:include page="../modulos/footer.jsp" />
		
	</div>
	<script type="text/javascript">
		$('#menu-reservas').addClass('active');
	</script>
</body>
</html>

