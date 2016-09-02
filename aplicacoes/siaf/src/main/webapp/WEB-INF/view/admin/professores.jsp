<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
<jsp:include page="../modulos/header-estrutura.jsp" />
<title>Professores</title>
</head>
<body>
	<div id="wrapper">
		<jsp:include page="../modulos/header.jsp" />

		<div id="content">						
			<div class="container" id="message">
				<c:if test="${not empty erro}">
					<div class="alert alert-danger alert-dismissible" role="alert">
						<button type="button" class="close" data-dismiss="alert">
							<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
						</button>
						<c:out value="${erro}"></c:out>
					</div>
				</c:if>
				<c:if test="${not empty info}">
					<div class="alert alert-info alert-dismissible"
						role="alert">
						<button type="button" class="close" data-dismiss="alert">
							<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
						</button>
						<c:out value="${info}"></c:out>
					</div>
				</c:if>
			</div>

			<%-- <div>
				<a id="atualizarLista" href="<c:url value="/administracao/atualizar-professores" />" class="btn btn-siaf">Atualizar Lista</a>	
			</div> --%>
			<br>
			<div class="container">
				<table id="tableProfessores" class="table table-striped">
					<thead>
						<tr>
							<th>Siape</th>
							<th>Nome</th>
							<th>Email</th>
							<th>Nascimento</th>
							<th>Admissão</th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${professores}" var="professor">
							<input type="hidden" value="${professor.semestreAdmissao }" id="semestreAdmissao"/>
							<tr>
								<td class="align-center">${professor.usuario.siape}</td>
								<td>${professor.usuario.nome}</td>
								<td>${professor.usuario.email}</td>
								<td class="align-center">
									<fmt:formatDate pattern="dd/MM/yyyy" value="${professor.usuario.nascimento }" />
								</td>
								<td class="align-center">
									<c:if test="${empty professor.anoAdmissao}">-</c:if>
									<c:if test="${not empty professor.anoAdmissao}">
										${professor.anoAdmissao}.${professor.semestreAdmissao}
									</c:if>
								</td>
								<td  class="align-center">
									<a title="Editar" href="#">
										<button class="btn btn-default editar-admissao" data-id="${professor.id }" 
											data-nome="${professor.usuario.nome }" data-ano="${professor.anoAdmissao }" 
											data-semestre="${professor.semestreAdmissao }">
											<i class="fa fa-pencil"></i>
										</button>
									</a>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	
	<jsp:include page="../modulos/footer.jsp" />
	
	<!-- Modal Editar Admissão -->
	<div class="modal fade" id="editar-admissao" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="cancelarModalLabel">Editar admissão</h4>
				</div>
				<form id="formEditarAdmissao" action="/siaf/administracao/editar-admissao" method="POST">
					<div class="modal-body form-horizontal">
						<input type="hidden" id="id" name="id" value=""/>
						<div class="form-group">
							<label class="col-sm-2 control-label">Professor:</label>
							<div class="col-sm-8">
								<label id="professor" class="control-label value-label"></label>
							</div>
						</div>
						<div class="form-group">
							<div class="form-item">
								<label for="ano" class="col-sm-2 control-label">Admissão:</label>
								<div id="div-semestre" class="col-sm-8">
									<input id="ano" name="ano" type="text" class="form-control ano-all" size="10" placeholder="ano" value="" required="required"/>
									<select id="semestre" name="semestre" class="form-control selectpicker">
										<option value="1">1</option>
										<option value="2">2</option>
									</select>
								</div>
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="submit" class="btn btn-primary">Salvar</button>
						<button type="button" class="btn btn-default" data-dismiss="modal">cancelar</button>
					</div>
				</form>
			</div>
		</div>
	</div>
	
	<script type="text/javascript">
		$('#menu-professores').addClass('active');
	</script>
</body>
</html>
