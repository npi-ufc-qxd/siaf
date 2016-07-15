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
				<br />
				<div class="alert alert-danger alert-dismissible" role="alert">
					<button type="button" class="close" data-dismiss="alert">
						<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
					</button>
					<c:out value="${erro}"></c:out>
				</div>
			</c:if>
			<c:if test="${not empty info}">
				<br />
				<div class="alert alert-info alert-dismissible" role="alert">
					<button type="button" class="close" data-dismiss="alert">
						<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
					</button>
					<c:out value="${info}"></c:out>
				</div>
			</c:if>

			<form id="minhasReservas" class="form-horizontal">
				<div>
					<a title="Nova reserva" href="<c:url value="/reserva/incluir" />" class="btn btn-siaf">Nova reserva</a>
				</div>
				<br>
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
					<label class="col-sm-2 control-label">Data de nascimento:</label>
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
								<th>Solicitação</th>
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
									<td><fmt:formatDate value="${reserva.dataSolicitacao}" pattern="dd/MM/yyyy hh:mm"/></td>
									<td>${reserva.status.descricao }</td>
									<td>
										<a class="btn btn-primary btn-sm" title="Detalhes" href="<c:url value="/reserva/detalhes/${reserva.id }"/>">
											<i class="fa fa-info-circle"></i>
										</a>
									   	<c:if test="${reserva.status eq 'ABERTO' }">
											<a title="Cancelar" data-toggle="modal"
												data-target="#cancelar-reserva" href="#"
												data-id="${reserva.id }">
												<button class="btn btn-danger btn-sm">
													<i class="fa fa-times"></i>
												</button>
											</a>
                                        </c:if>
                                        <c:if test="${reserva.status eq 'EM_ESPERA' }">
											<a class="btn btn-default btn-sm" title="Editar" href="<c:url value="/reserva/editar/${reserva.id }"/>">
												<i class="fa fa-pencil"></i>
											</a>
											<a title="Excluir" data-toggle="modal"
												data-target="#excluir-reserva" href="#"
												data-href="<c:url value="/reserva/excluir/${reserva.id}"></c:url>"
												data-name="${reserva.anoInicio}.${reserva.semestreInicio} a ${reserva.anoTermino}.${reserva.semestreTermino}">
												<button class="btn btn-warning btn-sm">
													<i class="fa fa-trash-o"></i>
												</button>
											</a>
                                        </c:if>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:if>
			</form>
		</div>

		<!-- Modal Excluir Reserva -->
		<div class="modal fade" id="excluir-reserva" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title" id="excluirModalLabel">Excluir</h4>
					</div>
					<div class="modal-body"></div>
					<div class="modal-footer">
						<a href="#" class="btn btn-danger">Excluir</a>
						<button type="button" class="btn btn-default" data-dismiss="modal">Cancelar</button>
					</div>
				</div>
			</div>
		</div>
		
		<!-- Modal Cancelar Reserva -->
		<div class="modal fade" id="cancelar-reserva" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title" id="cancelarModalLabel">Cancelar</h4>
					</div>
					<form id="formCancelarReserva" action="/siaf/reserva/cancelar" method="POST">
						<div class="modal-body">
							<span class="msg-alert">ATENÇÃO: essa ação não poderá ser desfeita!</span><br/>
							Tem certeza de que deseja cancelar essa reserva?<br/><br/>
							<input type="hidden" id="reservaId" name="id" value=""/>
							<div class="form-group">
								<div class="form-item">
									<label class="control-label" for="motivo">Motivo:</label>
									<textarea class="form-control" id="motivo" name="motivo" required="required"></textarea>
								</div>
							</div>
						</div>
						<div class="modal-footer">
							<button type="submit" class="btn btn-danger">Sim</button>
							<button type="button" class="btn btn-default" data-dismiss="modal">Não</button>
						</div>
					</form>
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

