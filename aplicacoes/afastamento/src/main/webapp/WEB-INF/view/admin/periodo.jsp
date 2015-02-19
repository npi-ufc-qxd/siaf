<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<jsp:include page="../modulos/header-estrutura.jsp" />
<link href="<c:url value="/resources/css/jquery.dataTables.min.css" />" rel="stylesheet" />

<title>SiAf - Período</title>
</head>
<body>
	<div id="wrapper" class="container">
		<jsp:include page="../modulos/header.jsp" />
		
		<div id="content">
					
			<div class="title"> Período : </div>
			<span class="line"></span>
 
			<div class="messages">
				<div id="erro" class="alert alert-danger margin-top hide" role="alert">
					<button type="button" class="close" data-dismiss="alert">
						<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
					</button>
					<p></p>
				</div>

				<div id="info" class="alert alert-info margin-top hide" role="alert">
					<button type="button" class="close" data-dismiss="alert">
						<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
					</button>
					<p></p>
				</div>
			</div>			
			<div>
				<table id="tablePeriodos" class="display" cellspacing="0" width="100%">
					<thead>
						<tr>
							<th>Status</th>
							<th>Ano</th>
							<th>Semestre</th>
							<th>Encerramento</th>
							<th>Vagas</th>
							<th></th>
						</tr>
					</thead>
					<tbody id="bodyPeriodos">
						<c:forEach items="${periodos}" var="periodo" varStatus="cont">
							<tr id="periodo${periodo.id}">
								<fmt:formatDate value="${periodo.encerramento}" pattern="dd/MM/yyyy" var="data"/>
								<td><span class="label ${periodo.status eq 'ABERTO' ? 'label-success':'label-danger'}">${periodo.status}</span></td>
								<td>${periodo.ano}</td>
								<td>${periodo.semestre}</td>
								
								<td id="encerramento${periodo.id}" class="encerramento">${data}</td>
								<td id="vagas${periodo.id}" class="vagas">${periodo.vagas}</td>
								
								<td class="editPeriodo">
									<button class="btn editPeriodo" id="editPeriodo${periodo.id}" data-id="${periodo.id}"><i class="fa fa-pencil "></i></button>
				    	           	<div class="options hide" id="options${periodo.id}">
						                <button class="btn salvarPeriodo btn-primary" data-id="${periodo.id}">salvar</button>
					    	           	<button class="btn cancelPeriodo btn-danger" data-id="${periodo.id}"><i class="fa fa-times "></i></button>									
				    	           	</div>
								</td>
				           </tr>
					</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	
	<div class="modal fade" id="info-periodo" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-body"><b></b></div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">fechar</button>
				</div>
			</div>
		</div>
	</div>
	

	<jsp:include page="../modulos/footer.jsp" />
	<script src="<c:url value="/resources/js/jquery.dataTables.min.js" />"></script>

	<script type="text/javascript">
		getPeriodos();
		$('#menu-periodos').addClass('active');
		
		$('#tablePeriodos').DataTable({
			 "pageLength": 50,
			 "order": [[ 1, 'asc' ], [ 2, 'asc' ]],
			 "columnDefs": [
			               { "orderable": false, "targets": 0 },
			               { "orderData": [ 1, 2 ],    "targets": 1 },
			               { "orderable": false, "targets": 2 },
			               { "orderable": false, "targets": 3 },
			               { "orderable": false, "targets": 4 },
			               { "orderable": false, "targets": 5 },
			],
			
			"language": {
			    "sEmptyTable": "Nenhum registro encontrado",
			    "sInfo": "Mostrando de _START_ até _END_ de _TOTAL_ registros",
			    "sInfoEmpty": "Mostrando 0 até 0 de 0 registros",
			    "sInfoFiltered": "(Filtrados de _MAX_ registros)",
			    "sInfoPostFix": "",
			    "sInfoThousands": ".",
			    "sLengthMenu": "resultados por página _MENU_",
			    "sLoadingRecords": "Carregando...",
			    "sProcessing": "Processando...",
			    "sZeroRecords": "Nenhum registro encontrado",
			    "sSearch": "",
			    "oPaginate": {
			        "sNext": "Próximo",
			        "sPrevious": "Anterior",
			        "sFirst": "Primeiro",
			        "sLast": "Último"
			    },
			    "oAria": {
			        "sSortAscending": ": Ordenar colunas de forma ascendente",
			        "sSortDescending": ": Ordenar colunas de forma descendente"
			    }
			}
		});
		
		
		$('select').selectpicker();
		$('input').attr('placeholder', 'Pesquisar...');
		$('input').addClass('form-inline form-control');
	
	</script>
</body>
</html>