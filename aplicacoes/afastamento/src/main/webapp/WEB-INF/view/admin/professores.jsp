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
	<div class="agroup">
		<div id="wrapper">
			<jsp:include page="../modulos/header.jsp" />

			<div id="content">						

				<div class="container" id="message">
					<c:if test="${not empty erro}">
						<div class="alert alert-danger alert-dismissible margin-top"
							role="alert">
							<button type="button" class="close" data-dismiss="alert">
								<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
							</button>
							<c:out value="${erro}"></c:out>
						</div>
					</c:if>
					<c:if test="${not empty info}">
						<div class="alert alert-info alert-dismissible margin-top"
							role="alert">
							<button type="button" class="close" data-dismiss="alert">
								<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
							</button>
							<c:out value="${info}"></c:out>
						</div>
					</c:if>
				</div>

				<div class="controls">
					<a id= "atualizarLista" href="<c:url value="/administracao/atualizar-professores" />" class="btn btn-siaf">Atualizar Lista</a>	
				</div>
				<br>
				<div class="container">

						<table id="professores" class="table table-striped">
							<thead>
								<tr class="afas-tr-left">
									<th>#</th>
									<th class="afas-tr-left">Siape</th>
									<th id="orderName">Nome</th>
									<th>E-mail</th>
									<th>Admissão</th>
									<th></th>
								</tr>
							</thead>
							<tbody id="contentProfessores">
								<c:forEach items="${professores}" var="professor"
									varStatus="count">
									<input type="hidden" value="${professor.semestreAdmissao }" id="semestreAdmissao"/>
									<tr>
										<td>${count.count}</td>
										<td>${professor.siape}</td>
										<td>${professor.nome}</td>
										<td>${professor.email}</td>
										<td id="editProf${professor.id }" class="editProf" style="width: 10px;"><span id = "anoAdmissao${professor.id }"
											class="anoEdit" data-name="anoAdmissaoEdit">${professor.anoAdmissao}</span>.<span id="semestreAdmissao${professor.id }"
											class="semestreEdit"
											data-value="${professor.semestreAdmissao}">${professor.semestreAdmissao}</span>
										</td>
										<td id="editAcao${professor.id }" class="editAcao" style="width: 40px;" align="right">
											<button class="btn edit" data-id="${professor.id}">
												<i class="fa fa-pencil "></i>
											</button>
											<div class="options options${professor.id} hide">
												<button class="btn salvar btn-primary"
													data-id="${professor.id}">salvar</button>
												<button class="btn cancel btn-danger"
													data-id="${professor.id}">
													<i class="fa fa-times "></i>
												</button>
											</div>
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>

				
		</div>
		<jsp:include page="../modulos/footer.jsp" />
	</div>

	</div>
		</div>

	<script type="text/javascript">
		$('#menu-professores').addClass('active');
	</script>
</body>
</html>
