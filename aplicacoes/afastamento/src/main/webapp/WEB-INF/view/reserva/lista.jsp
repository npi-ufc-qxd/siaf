<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<jsp:include page="../modulos/header-estrutura.jsp" />
<title>Minhas reservas</title>
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

			<form id="minhasReservas" class="form-horizontal">
				<div class="title">Suas reservas :</div>
				<span class="line"></span>
				<div>
					<a href="<c:url value="/reserva/incluir" />" class="btn btn-siaf">Incluir reserva</a>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">Nome:</label>
					<div class="col-sm-4">
						<label class="control-label value-label">${professor.nome }</label>
					</div>
					<label class="col-sm-2 control-label">Siape:</label>
					<div class="col-sm-4">
						<label class="control-label value-label">${professor.siape }</label>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">Admissão:</label>
					<div class="col-sm-4">
						<label class="control-label value-label">${professor.anoAdmissao }.${professor.semestreAdmissao }</label>
					</div>
					<label class="col-sm-2 control-label">Data de Nascimento:</label>
					<div class="col-sm-4">
						<label class="control-label value-label"><fmt:formatDate pattern="dd/MM/yyyy"
								value="${professor.dataNascimento }" /></label>
					</div>
				</div>

				<c:if test="${empty reservas }">
					<div class="alert alert-warning alert-dismissible" role="alert">Você não possui nenhuma
						reserva ou solicitação de afastamento.</div>
				</c:if>

				<c:if test="${not empty reservas }">
					<table id="reservas" class="table">
						<thead>
							<tr>
								<th>Período</th>
								<th>Programa</th>
								<th>Conceito</th>
								<th>Instituição</th>
								<th>Status</th>
								<th></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${reservas }" var="reserva">
								<tr class="${reserva.status}">
									<td>${reserva.anoInicio}.${reserva.semestreInicio} a
										${reserva.anoTermino}.${reserva.semestreTermino}</td>
									<td>${reserva.programa.descricao }</td>
									<td>${reserva.conceitoPrograma eq 0 ? "-" : reserva.conceitoPrograma}</td>
									<td>${reserva.instituicao }</td>
									<td>${reserva.status.descricao }</td>
									<td><c:if test="${reserva.status eq 'ABERTO'}">

											<a id="excluir" data-id="${reserva.id }" data-toggle="modal" data-target="#modal-excluir-reserva" href="#"
												data-href="<c:url value="/reserva/${reserva.id}/excluir"></c:url>"
												
												class="btn btn-danger">Excluir</a>

										</c:if> &nbsp;&nbsp; <c:if test="${periodo.status eq 'ABERTO' }">
											<a href="<c:url value="/reserva/editar/${reserva.id }" />" class="btn btn-primary">Editar</a>
										</c:if>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					<div id="legenda">
						<label><span class="afastado">&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;Afastado</label> <label><span
							class="desclassificado">&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;Não classificado</label> <label><span
							class="encerrado">&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;Encerrado</label> <label><span
							class="cancelado">&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;Cancelado</label><br />
					</div>
				</c:if>
			</form>
		</div>

		<!-- Modal Excluir Reserva -->
		<div class="modal fade" id="modal-excluir-reserva" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title" id="excluirModalLabel">Excluir</h4>
						
					</div>
					<div class="modal-body"><p>Tem certeza que deseja excluir a reserva?</p></div>
					<div class="modal-footer">
						<a  id="btn-confirma-excluir-reserva" class="btn btn-danger">Excluir</a>
						<button type="button" class="btn btn-default" data-dismiss="modal">Cancelar</button>
					</div>
				</div>
			</div>
		</div>

		<jsp:include page="../modulos/footer.jsp" />

	</div>
	<script type="text/javascript">
		$('#menu-minhas-reservas').addClass('active');
	</script>
</body>
</html>
