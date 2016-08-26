$(document).ready(function() {
	
	// Geral
	
	$('.ano').mask('9999', {placeholder:" "});
	$('.conceito').mask('9',{placeholder:" "});
	
	$('[data-toggle="popover"]').popover({ trigger: "hover" });
	
	$('.selectpicker').selectpicker();
	
	$(".ano").datepicker({
        format: " yyyy",
        language: 'pt-BR',
		autoclose: true,
        viewMode: "years", 
        minViewMode: "years",
        startDate: 'today'
    });
	
	$(".ano-all").datepicker({
        format: " yyyy",
        language: 'pt-BR',
		autoclose: true,
        viewMode: "years", 
        minViewMode: "years",
    });
		
	
	$(".data").datepicker({
		language: 'pt-BR',
		autoclose: true,
		format: "dd/mm/yyyy"
	});
	
	$(".file").fileinput({
		showUpload: false,
		overwriteInitial: false,
		initialCaption: "Selecione...",
		browseLabel: "Buscar",
		browseClass: "btn btn-default",
		removeLabel: "Excluir",
		msgSelected: "{n} arquivos selecionados",
		msgLoading: "Carregando arquivo {index} de {files} &hellip;"
	});
	
	
	$("#encerramento").mask("99/99/9999");
	
//_____________________________________________________________________________________________________	
	
	// Página de Gerenciar Professores
	
	$('.editar-admissao').click(function(e){
		e.preventDefault();
		$('#editar-admissao').modal('show');
		$('#editar-admissao').find('#id').val($(this).data('id'));
		$('#editar-admissao').find('#professor').text($(this).data('nome'));
		if($(this).data('ano') != '') {
			$('#editar-admissao').find('#ano').val($(this).data('ano'));
			$('#editar-admissao').find('#semestre').val($(this).data('semestre'));
			$('#div-semestre').find('.bootstrap-select .filter-option').text($('#editar-admissao').find('#semestre').val());
		} else {
			$('#editar-admissao').find('#ano').val('');
		}
		
	});
	
	$('#tableProfessores').DataTable({
		"pageLength" : 10,
		"order" : [[ 1, 'asc' ]],
		"columnDefs" : [ 
            {"targets" : 0, "orderable" : false},
            {"targets" : 2, "orderable" : false},
            {"targets" : 3, "orderable" : false},
            {"targets" : 5, "orderable" : false},
		],

		"language" : {
			"sEmptyTable" : "Nenhum registro encontrado.",
			"sInfo" : "Mostrando de _START_ até _END_ de _TOTAL_ registros",
			"sInfoEmpty" : "Mostrando 0 até 0 de 0 registros",
			"sInfoFiltered" : "(Filtrados de _MAX_ registros)",
			"sInfoPostFix" : "",
			"sInfoThousands" : ".",
			"sLengthMenu" : "Resultados por página:  _MENU_",
			"sLoadingRecords" : "Carregando...",
			"sProcessing" : "Processando...",
			"sZeroRecords" : "Nenhum registro encontrado.",
			"sSearch" : "",
			"searchPlaceholder": "Pesquisar...",
			"oPaginate" : {
				"sNext" : "Próximo",
				"sPrevious" : "Anterior",
				"sFirst" : "Primeiro",
				"sLast" : "Último"
			},
			"oAria" : {
				"sSortAscending" : ": Ordenar colunas de forma ascendente",
				"sSortDescending" : ": Ordenar colunas de forma descendente"
			}
		}
	});

//_____________________________________________________________________________________________________		
	
	
	// Página de homologação de reserva
	
	$('.homologar').click(function(e){
		e.preventDefault();
		var idReserva = $(this).data('id');
		var status = $('#homologar-reserva-' + idReserva).find('#status-' + idReserva).val();
		if (status.contains('CANCELADO') || status.contains('NEGADO')) {
			$('#cancelar-reserva').modal('show');
			$('#cancelar-reserva').find('#status').val(status);
			$('#cancelar-reserva').find('#idReserva').val(idReserva);
		} else {
			$('#homologar-reserva-' + $(this).data('id')).submit();
		}
	});
	
	$('#tableRelatorio').DataTable({
		'aoColumnDefs': [
		{
			'bSortable': false,
			'aTargets': ['nosort']
		},
		{ "sType": "numeric-comma", "aTargets": [ 2 ] }
		],
		"order": [ 1, 'asc' ],
	  	"paging": false,
	  	"searching": false,
	  	"scrollX": true,
	  	"language": {
		    "sEmptyTable": "Nenhum registro encontrado.",
		    "sInfo": "Total de _TOTAL_ registros",
		    "sInfoEmpty": "Mostrando 0 até 0 de 0 registros",
		    "sInfoFiltered": "(Filtrados de _MAX_ registros)",
		    "sInfoPostFix": "",
		    "sInfoThousands": ".",
		    "sLengthMenu": "Resultados por página: _MENU_",
		    "sLoadingRecords": "Carregando...",
		    "sProcessing": "Processando...",
		    "sZeroRecords": "Nenhum registro encontrado.",
		    "sSearch": "",
		    "searchPlaceholder": "Pesquisar...",
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
	
//_____________________________________________________________________________________________________	
	
		
	// Página de inclusão e edição de reserva
	
	$('#solicitarAfastamento, #formEditarReserva, #formEditarAdmissao, #formCancelarReserva, #formEditarAdmissao').validate({
        rules: {
            
        },
        highlight: function(element) {
            $(element).closest('.form-item').addClass('has-error');
        },
        unhighlight: function(element) {
            $(element).closest('.form-item').removeClass('has-error');
        },
        errorElement: 'span',
        errorClass: 'help-block',
        errorPlacement: function(error, element) {
            error.insertAfter(element.parent().children().last());
        },
        messages:{
        	anoInicio:{
                required:"Campo obrigatório",
            },
            anoTermino:{
                required:"Campo obrigatório",
            },
            motivo:{
                required:"Campo obrigatório",
            },
            ano:{
                required:"Campo obrigatório",
            }
        }
    });
	
//____________________________________________________________________________________________________________________________________________________	
	
	
	// Página do Ranking
	
	$("#anoBuscado").datepicker({
        format: " yyyy", 
           viewMode: "years", 
           minViewMode: "years",
           startDate: $('#ano').val()
    });
	
	$("#warning-buscar-periodo").hide();
	/*$('#anoBuscado').click(function(e){
		e.preventDefault();
		$("#warning-buscar-periodo").hide();
	});
	$('#semestreBuscado').click(function(e){
		e.preventDefault();
		$("#warning-buscar-periodo").hide();
	});*/
	
	$('#buscar').click(function(e){
		e.preventDefault();
		$("#warning-buscar-periodo").hide();
		var anoBuscado = parseInt($('#anoBuscado').val());
		var periodoAtualAno = parseInt($('#anoAtual').val());
		var semestreBuscado = parseInt($('#semestreBuscado').val());
		var periodoAtualSemestre = parseInt($('#semestreAtual').val());
		if (anoBuscado > periodoAtualAno) {
			getRanking($('#anoBuscado').val(), $('#semestreBuscado').val(), false);			
		} else if (anoBuscado == periodoAtualAno) {
			if (semestreBuscado >= periodoAtualSemestre) {
				getRanking($('#anoBuscado').val(), $('#semestreBuscado').val(), false);
				
			} else {
				$("#warning-buscar-periodo").show();
			}
		} else {
			$("#warning-buscar-periodo").show();
		}
	});
	
	$('#anterior').click(function(e){
		e.preventDefault();
		$("#warning-buscar-periodo").hide();
		getRanking($('#anoAnterior').val(), $('#semestreAnterior').val(), false);
	});
	
	$('#posterior').click(function(e){
		e.preventDefault();
		$("#warning-buscar-periodo").hide();
		getRanking($('#anoPosterior').val(), $('#semestrePosterior').val(), false);
	});
	
	$("#ranking-full").hide();
	
	// Simulador
	
	$('#buscar-simulador').click(function(e){
		e.preventDefault();
		$("#warning-buscar-periodo").hide();
		var anoBuscado = parseInt($('#anoBuscado').val());
		var periodoAtualAno = parseInt($('#anoAtual').val());
		var semestreBuscado = parseInt($('#semestreBuscado').val());
		var periodoAtualSemestre = parseInt($('#semestreAtual').val());
		if (anoBuscado > periodoAtualAno) {
			getRanking($('#anoBuscado').val(), $('#semestreBuscado').val(), true);			
		} else if (anoBuscado == periodoAtualAno) {
			if (semestreBuscado >= periodoAtualSemestre) {
				getRanking($('#anoBuscado').val(), $('#semestreBuscado').val(), true);
				
			} else {
				$("#warning-buscar-periodo").show();
			}
		} else {
			$("#warning-buscar-periodo").show();
		}
	});
	
	$('#anterior-simulador').click(function(e){
		e.preventDefault();
		$("#warning-buscar-periodo").hide();
		getRanking($('#anoAnterior').val(), $('#semestreAnterior').val(), true);
	});
	
	$('#posterior-simulador').click(function(e){
		e.preventDefault();
		$("#warning-buscar-periodo").hide();
		getRanking($('#anoPosterior').val(), $('#semestrePosterior').val(), true);
	});
	
//____________________________________________________________________________________________________________________________________________________	

	// Página minhas reservas
	
	$('#excluir-reserva').on('show.bs.modal', function(e) {
		$(this).find('.modal-body').text('Tem certeza de que deseja excluir a reserva para o período \"' + $(e.relatedTarget).data('name') + '\"?');
		$(this).find('.btn-danger').attr('href', $(e.relatedTarget).data('href'));
	});
	
	$('#cancelar-reserva').on('show.bs.modal', function(e) {
		$(this).find('#reservaId').val($(e.relatedTarget).data('id'));
	});
	
	$('#anoInicioReserva').datepicker({
        format: " yyyy", 
   	    viewMode: "years", 
   	    minViewMode: "years"
	});

	$('#anoTerminoReserva').datepicker({
        format: " yyyy", 
   	    viewMode: "years", 
   	    minViewMode: "years"
	});

//_____________________________________________________________________________________________________
	
	// Página Gerenciar Reservas
	
	var table = $('#tableReservas').DataTable({
				"pageLength" : 10,
				"order" : [[ 0, 'asc' ], [ 2, 'asc' ]],
				"columnDefs" : [ 
	                {"targets" : 5, "orderable" : false},
				],

				"language" : {
					"sEmptyTable" : "Nenhum registro encontrado.",
					"sInfo" : "Mostrando de _START_ até _END_ de _TOTAL_ registros",
					"sInfoEmpty" : "Mostrando 0 até 0 de 0 registros",
					"sInfoFiltered" : "(Filtrados de _MAX_ registros)",
					"sInfoPostFix" : "",
					"sInfoThousands" : ".",
					"sLengthMenu" : "Resultados por página:  _MENU_",
					"sLoadingRecords" : "Carregando...",
					"sProcessing" : "Processando...",
					"sZeroRecords" : "Nenhum registro encontrado.",
					"sSearch" : "",
					"searchPlaceholder": "Pesquisar...",
					"oPaginate" : {
						"sNext" : "Próximo",
						"sPrevious" : "Anterior",
						"sFirst" : "Primeiro",
						"sLast" : "Último"
					},
					"oAria" : {
						"sSortAscending" : ": Ordenar colunas de forma ascendente",
						"sSortDescending" : ": Ordenar colunas de forma descendente"
					}
				}
			});
	
	$("#tableReservas tfoot th").each( function ( i ) {
		if(i == 0 || i == 3 || i == 4) {
			var select = $('<select class="select-search '+i+'" title="TODOS"><option value="">TODOS</option></select>')
	        .appendTo( $(this).empty() )
	        .on( 'change', function () {
	            var val = $(this).val();
	
	            table.column( i ) //Only the first column
	                .search( val ? '^'+$(this).val()+'$' : val, true, false )
	                .draw();
	        });
	
		    table.column( i ).data().unique().sort().each( function ( d, j ) {
		    	if(i == 0) {
		    		var t = $.parseHTML( d );
		    		d = $(t).html();
		    	}
		        select.append( '<option value="'+d+'">'+d+'</option>' );
		    });
		}
    });
	
	$('#tableHistorico').DataTable({
		"ordering": false,
		"paging": false,
		"language": {
            "url": "/siaf/resources/js/Portuguese-Brasil.json"
        }
	});
	
	$('#admin-excluir-reserva').on('show.bs.modal', function(e) {
		$(this).find('.modal-body').text('Tem certeza de que deseja excluir a reserva de ' + $(e.relatedTarget).data('professor') + ' para o período \"' + $(e.relatedTarget).data('name') + '\"?');
		$(this).find('.btn-danger').attr('href', $(e.relatedTarget).data('href'));
	});
	
//_____________________________________________________________________________________________________
	
	// Página gerenciar períodos

	$('#tablePeriodos').DataTable({
		 "pageLength": 10,
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
		    "sEmptyTable": "Nenhum registro encontrado.",
		    "sInfo": "Mostrando de _START_ até _END_ de _TOTAL_ registros",
		    "sInfoEmpty": "Mostrando 0 até 0 de 0 registros",
		    "sInfoFiltered": "(Filtrados de _MAX_ registros)",
		    "sInfoPostFix": "",
		    "sInfoThousands": ".",
		    "sLengthMenu": "Resultados por página: _MENU_",
		    "sLoadingRecords": "Carregando...",
		    "sProcessing": "Processando...",
		    "sZeroRecords": "Nenhum registro encontrado.",
		    "sSearch": "",
		    "searchPlaceholder": "Pesquisar...",
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
	
	$('.salvar-periodo').click(function(e){
		e.preventDefault();
		var id = $(this).data("id");
		$('#periodoId').val(id);
		$('#encerramento').val($('#encerramento-' + id).val());
		$('#vagas').val($('#vagas-' + id).val());
		
		$('#atualizarPeriodo').submit();
	});

	$('input').addClass('form-control');
	$('select').selectpicker();
		
});

//_____________________________________________________________________________________________________


//Funções

function getRanking(ano, semestre, simulador) {
	$("tbody").remove();
	$("#ranking-full").hide();
	$('#load-siaf').show();
	$.ajax({
		type: "GET",
		url: '/siaf/reserva/ranking.json',
		data: {
        	ano : ano.trim(),
        	semestre : semestre.trim(),
        	simulador : simulador
		}
	})
	.success(function(result) {
		$('#count-ranking').text('Estimativa de vagas restantes: ' + (result.periodoAtual.vagas - result.afastados.length));
		loadAfastados(result.afastados);
			
		$('i#anterior, i#anterior-simulador').show();
		$('i#posterior, i#posterior-simulador').show();
		if(result.periodoAnterior == null) {
			$('i#anterior, i#anterior-simulador').hide();
		} else {
			$('#anoAnterior').val(result.periodoAnterior.ano);
			$('#semestreAnterior').val(result.periodoAnterior.semestre);
		}
		
		if(result.periodoPosterior == null) {
			$('i#posterior, i#posterior-simulador').hide();
		} else {
			$('#anoPosterior').val(result.periodoPosterior.ano);
			$('#semestrePosterior').val(result.periodoPosterior.semestre);
		}
		
		$('#ano').val(result.periodoAtual.ano);
		$('#semestre').val(result.periodoAtual.semestre);
		
		$('#anoBuscado').val(result.periodoAtual.ano);
		$('#semestreBuscado').val(result.periodoAtual.semestre);
		$('#semestreBuscado').selectpicker('refresh');
		
		$('#periodoLabel').text(result.periodoAtual.ano + "." + result.periodoAtual.semestre);
		$('#vagas').text(result.periodoAtual.vagas);
		if(result.periodoAtual.encerramento != null) {
			$('#encerramento').text(moment(result.periodoAtual.encerramento, 'YYYY-MM-DD').format('DD/MM/YYYY'));
		} else {
			$('#encerramento').text('a definir');
		}
		$('#encerramento').attr('data-content', 'Data limite para solicitação de afastamento com início para o período ' + (result.periodoAtual.ano + 1) + '.' + result.periodoAtual.semestre);
		
		$('#load-siaf').hide();
		loadRanking(result.ranking.tuplas);
		
		$('table#ranking, table#afastados').removeClass('animated zoomIn').addClass('animated zoomIn').one('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function(){
		      $(this).removeClass('animated zoomIn');
	    });
		$("#ranking-full").show();
		
	})
	.error(function(error) {
		window.location.replace("/siaf/login");
	});
}

function loadRanking(result) {
	$('#ranking tbody').remove();
	$('#ranking').append('<tbody>');
	$.each(result, function(i, item) {
        var $tr = $('<tr class="' + item.status + '">').append(
        	$('<td class=\"align-center\">').text(i+1),
        	$('<td>').text(item.professor),
            $('<td class=\"align-center\">').text(item.t),
            $('<td class=\"align-center\">').text(item.a),
            $('<td class=\"align-center\">').text(item.s),
            $('<td class=\"align-center\">').text(item.p),
            $('<td class=\"align-center\">').text(item.ss),
            $('<td class=\"align-center\">').text(item.reserva.anoInicio + "." + item.reserva.semestreInicio + " a " + item.reserva.anoTermino + "." + item.reserva.semestreTermino),
            $('<td class=\"align-center\">').text(getPrograma(item.reserva.programa) + " / " + getConceito(item.reserva.conceitoPrograma)),
            $('<td class=\"pontuacao align-center\">').text(item.pontuacao.toFixed(2))
        ).appendTo('#ranking tbody');
    });
	
	if(result.length == 0) {
		$("#warning-ranking").show();
		$("#ranking").hide();
	} else {
		$("#warning-ranking").hide();
		$("#ranking").show();
	}
	
}

function loadAfastados(afastados) {
	$('#count-afastados').text('Quantidade de professores afastados: ' + afastados.length);
	$("#afastados tbody").remove();
	$('#afastados').append('<tbody>');
	$.each(afastados, function(i, tupla) {
        var $tr = $('<tr>').append(
        	$('<td>').text(tupla.professor),
            $('<td class=\"align-center\">').text(tupla.reserva.anoInicio + "." + tupla.reserva.semestreInicio + " a " + tupla.reserva.anoTermino + "." + tupla.reserva.semestreTermino),
            $('<td class=\"align-center\">').text(getPrograma(tupla.reserva.programa))
        ).appendTo('#afastados tbody');
    });
	if(afastados.length == 0) {
		$("#warning-afastados").show();
		$("#afastados").hide();
	} else {
		$("#warning-afastados").hide();
		$("#afastados").show();
	}
}

function getPrograma(programa) {
	if(programa == 'POS_DOUTORADO') {
		return "PÓS DOUTORADO";
	}
	return programa;
}

function getStatus(reserva) {
	if(reserva == 'EM_ESPERA') {
		return "EM ESPERA";
	} else if(reserva == 'NAO_ACEITO') {
		return "NÃO ACEITO";
	} else if(reserva == 'CANCELADO_COM_PUNICAO') {
		return "CANCELADO COM PUNIÇÃO";
	}
	return reserva;
}

function getConceito(conceito) {
	if(conceito == 0) {
		return "-";
	}
	return conceito;
}

