

$(document).ready(function() {
	

	
	// Página de Gerenciar Professores
	var defaults = {
		    mode: 'inline', 
		    toggle: 'manual',
		    showbuttons: false,
		    onblur: 'ignore',
		    inputclass: 'input-small',
		    savenochange: true,
		    success: function() {
		        return false;
		    }
		};
		$.extend($.fn.editable.defaults, defaults);

		
	$(document).keyup(function(e) {
		if (e.keyCode == 27) {
			$("#contentProfessores td.editProf").css("width", "50px");
			$("#contentProfessores td.editAcao").css("width", "40px");
		    $(".options").removeClass( "show" ).addClass('hide').siblings('.edit').show();
		}
	});
	
	
	$(".anoEdit ").editable({
	    title: 'Ano de Admissão',
    	emptytext : '',
	});

	
	$(".semestreEdit" ).editable({
	    title: 'Semestre de Admissão',
	    type: 'select',
	    emptytext : '',
	    inputclass: 'selectpicker',
	    value: 1,
	    source: [
	        {value: 1, text: '1'},
	        {value: 2, text: '2'},
	    ]
	});	
	
	$('#professores').on('click', '.edit', function(){
	    var $btn = $(this);
	    var id = $btn.data("id");
		$('.options').removeClass( "show" ).addClass('hide');

	    $("#contentProfessores td.editProf").css("width", "160px");
		$("#contentProfessores td.editAcao").css("width", "120px");

	    $('#professores').find('.editable-open').editable('hide');
	    $('#professores').find('.options' +id).removeClass( "show" ).addClass('hide');
	    $('#professores').find('.edit').show();
	    $(this).hide().siblings('.options' +id).removeClass( "hide" ).addClass('show');
	    $(this).closest('tr').find('.editable').editable('show');
	    
	    $('select').selectpicker({}); 
	    $('input').attr("size", "4");
	    $('input').mask('9999', {placeholder:" "});
	    $('input').attr("placeholder", "Ano");
	    $('input').val($('#editProf' + id + ' span.anoEdit').text());
	    
	});	

	$('#professores').on('click', '.salvar', function() {
	    var $btn = $(this);

	    var id = $btn.data("id");
		var semestre = $( "select option:selected" ).val();
		var ano = $("input").val();
		$(".options" +id).removeClass( "show" ).addClass('hide').siblings('.edit').show();
		$btn.closest('tr').find('.editable').editable('hide');
		
		if(ano != '') {
			$.ajax({
				url: '/siaf/administracao/admissao',
				type: "POST",
				dataType: "html",
				data : {
					"id" : id,
					"ano" : ano,
					"semestre" : semestre,
				},
				success: function(result) {
					showMessage(result);
					$('#editProf' + id + ' span.anoEdit').text(ano);
					$('#editProf' + id + ' span.anoEdit').data("value", ano);
					$('#editProf' + id + ' span.semestreEdit').text(semestre);
					$('#editProf' + id + ' span.semestreEdit').data("value", semestre);
				},
				error: function(error) {
				}		
			});
		}
		$("#contentProfessores td.editProf").css("width", "50px");
		$("#contentProfessores td.editAcao").css("width", "40px");
		
	    
	});
	
	
	
	$('#professores').on('click', '.cancel', function() {
		cancelEdit(this);
	});
	
	function cancelEdit(e){
		$("#contentProfessores td.editProf").css("width", "50px");
		$("#contentProfessores td.editAcao").css("width", "40px");
	    var $btn = $(e);
	    var id = $btn.data("id");
	    $(".options" +id).removeClass( "show" ).addClass('hide').siblings('.edit').show();
	    $btn.closest('tr').find('.editable').editable('hide');
	}
	
	$('[data-toggle="tooltip"]').tooltip();
	
	$('[data-toggle="popover"]').popover({
	    trigger: 'hover'
	});
	
	function showMessage(result) {
		$("#wrapper #message").html($(result).find("#wrapper #message"));
	}
	
//____________________________________________________________________________________________________________________________________________________	
	
	
	
	// Página de Gerenciar Períodos
	function loadPeriodo(ano, semestre) {
		var filtro = {
			"ano" : ano,
			"semestre" : semestre,
		};
		
		$.ajax({
			url: '/siaf/administracao/periodo',
			type: "POST",
			dataType: "html",
			data: filtro,
			success: function(result) {
				showPeriodo(result);
			},
			error: function(error) {
				$('viewPeriodos').hide();
			}
		});
	}
	
	var editVagas = false;
	var editEncerramento = false;
	var guardaEncerramento = null;
	var guardaVagas = null;
	
	$('.editPeriodo').on('click', function(event) {
		var id = '';
		if($(this).data('id')){
			id = $(this).data('id');
		}
		$.ajax({
			type: 'POST',
			data: {
				'id': id, 
			},
			url: '/siaf/administracao/edit-periodo.json',
		})
		.success(function(result) {
			editVagas = result.editVagas;
			editEncerramento = result.editEncerramento;
			$('#options'+id).attr('data-encerramento', result.editEncerramento);
			$('#options'+id).attr('data-vagas', result.editVagas);

			if(result.editEncerramento){
			    encerramento = $('#encerramento'+id ).text();
			    guardaEncerramento = encerramento;
			    
				$('#encerramento'+id).empty();
				$('#encerramento'+id).append('<input name="inputEncerramento" class="form-control data" value="'+encerramento+'" />');

				$(".data").datepicker({
					language: 'pt-BR',
					format: "dd/mm/yyyy",	
					autoclose: true,
				});
			}
			
			if(result.editVagas){
				vagas = $('#vagas'+id ).text();
				guardaVagas = vagas;
				
				$('#vagas'+id).empty();
				$('#vagas'+id).append('<input name="inputVagas" class="form-control" size="2" value="'+vagas+'"/>');
			}
			
			if(result.editEncerramento || result.editVagas){
			    $('#tablePeriodos').find('#options' + id).removeClass( 'hide' ).addClass('show');
			    $('#editPeriodo'+id).removeClass( 'show' ).addClass('hide');
			}

			if(!result.editEncerramento && !result.editVagas){
				$("#info-periodo .modal-body b").text('Não é possível editar as informações deste período.');
				$("#info-periodo").modal('show');
				
			}
	
		});
		event.stopPropagation();
	});

	$('.salvarPeriodo').click(function() {
		var id = $(this).data('id');

		var vagas = $('#vagas'+id).text();
		var encerramento = $('#encerramento'+id).text();

		if($('#options' + id).data('encerramento')){
			encerramento = $('#encerramento'+id+' input').val();
		}
		
		if($('#options' + id).data('vagas')){
			vagas = $('#vagas'+id+' input').val();
		}
		
		$.ajax({
			type: 'POST',
			data: {
				'id': id,
				'vagas' : vagas,
				'encerramento' : encerramento,
			},
			url: '/siaf/administracao/editar-periodo.json',
		}).success(function(result) {
			messagePeriodo(result);
		});
		
		$('#encerramento'+id).empty().text(encerramento);
		$('#vagas'+id).empty().text(vagas);

		
		$('.options').removeClass( "show" ).addClass('hide');
		$('.editPeriodo').removeClass( 'hide' ).addClass('show');
	});
	
	
	$('.cancelPeriodo').click(function(event) {
		var id = $(this).data('id');

		if($('#options' + id).data('encerramento')){
			var encerramento = $('#encerramento'+id+' input').val();
			$('#encerramento'+id).empty();
			$('#encerramento'+id).text(guardaEncerramento);
			guardaEncerramento = null;
		}
		
		if($('#options' + id).data('vagas')){
			var vagas = $('#vagas'+id+' input').val();
			$('#vagas'+id).empty();
			$('#vagas'+id).text(guardaVagas);
			guardaVagas = null;
		}

		$('#options' + id).removeClass( 'show' ).addClass('hide');
		$('#editPeriodo'+id).removeClass( 'hide' ).addClass('show');

		event.stopPropagation();
	});
	
	var guardaConceito = null;
$('.editReserva').on('click', function(event) {
		var id = '';
		if($(this).data('id')){
			id = $(this).data('id');
		}
		
		$('#options'+id).attr('data-concept', true);
  
		conceito = $('#concept'+id ).text();
		guardaConceito = conceito;
		$('#concept'+id).empty();
		$('#concept'+id).append('<input name="inputConceito" class="form-control" size="1" value="'+conceito+'" maxlength="1" onKeyUp="validarConceito(this)"/>');

		$('#tableReservas').find('#options' + id).removeClass( 'hide' ).addClass('show');
		$('#editReserva'+id).removeClass( 'show' ).addClass('hide');
		
		event.stopPropagation();
	});

	$(".filtroSemestre").change(function(event) {
		filtroPeriodo();
	});
	
	$("#filtroAno").keyup(function (event) {
	    var maximoDigitosAno = 4;
	    var lengthAno = $(this).val().length;
	    if ( (lengthAno <= maximoDigitosAno || event.keyCode == 13) && !isNaN($(this).val()) ) {
	    	filtroPeriodo();
	    }
	});
	
	
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
	
//____________________________________________________________________________________________________________________________________________________	

		
	// Página de inclusão de reserva
	$('#solicitarAfastamento').validate({
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
            }
        }
    });
	
	$('.ano').mask('9999', {placeholder:" "});
	$('.conceito').mask('9',{placeholder:" "});
	
	$('.selectpicker').selectpicker();
	
	$(".filtroSemestre").selectpicker('refresh');
	
	
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

	
	
//____________________________________________________________________________________________________________________________________________________	
	
	
	// Página do Ranking
	
	$("#anoBuscado").datepicker({
        format: " yyyy", 
           viewMode: "years", 
           minViewMode: "years"
    });
	
	$('#buscar').click(function(){
		var anoBuscado = parseInt($('#anoBuscado').val());
		var periodoAtualAno = parseInt($('#periodoAtualAno').val());
		var semestreBuscado = parseInt($('#semestreBuscado').val());
		var periodoAtualSemestre = parseInt($('#periodoAtualSemestre').val());
		if(anoBuscado > periodoAtualAno){
			getRanking($('#anoBuscado').val(), $('#semestreBuscado').val());			
		}else if(anoBuscado == periodoAtualAno){
			if(semestreBuscado >= periodoAtualSemestre){
				getRanking($('#anoBuscado').val(), $('#semestreBuscado').val());
			}else{
				//Periodo Inválido
			}
		}else{
			//Periodo Inválido
		}
	});
	
	$('#anterior').click(function(){
		getRanking($('#anoAnterior').val(), $('#semestreAnterior').val());
	});
	
	$('#posterior').click(function(){
		getRanking($('#anoPosterior').val(), $('#semestrePosterior').val());
	});
	
	$("#ranking-full").hide();
	
	showPeriodoPost();	

//____________________________________________________________________________________________________________________________________________________	

	//Página atualizar Conceito reserva 	
	var guardaConceito = null;
	$('.editReserva').on('click', function(event) {
			var id = '';
			if($(this).data('id')){
				id = $(this).data('id');
			}
			
			$('#options'+id).attr('data-concept', true);
	  
			conceito = $('#concept'+id ).text();
			guardaConceito = conceito;
			$('#concept'+id).empty();
			$('#concept'+id).append('<input name="inputConceito" class="form-control" size="1" value="'+conceito+'" maxlength="1" onKeyUp="validarConceito(this)"/>');

			$('#tableReservas').find('#options' + id).removeClass( 'hide' ).addClass('show');
			$('#editReserva'+id).removeClass( 'show' ).addClass('hide');
			
			event.stopPropagation();
		});

		$(".filtroSemestre").change(function(event) {
			filtroPeriodo();
		});	
		
		$("#filtroAno").keyup(function (event) {
		    var maximoDigitosAno = 4;
		    var lengthAno = $(this).val().length;
		    if ( (lengthAno <= maximoDigitosAno || event.keyCode == 13) && !isNaN($(this).val()) ) {
		    	filtroPeriodo();
		    }
		});

		
	
	$('.salvarReserva').click(function() {
		var id = $(this).data('id');
		var conceito = $('#concept'+id).text();
		if($('#options' + id).data('concept')){
			conceito = $('#concept'+id+' input').val();
		}
		$.ajax({
			type: 'POST',
			data: {
				'id': id,
				'conceito' : conceito,
			},
			url: '/siaf/administracao/atualizarConceito.json',
		}).success(function(result) {
			messageReservaEmAberto(result);
		});
		
		$('#concept'+id).empty().text(conceito);
		
		$('.options').removeClass( "show" ).addClass('hide');
		$('.editReserva').removeClass( 'hide' ).addClass('show');
	});
	
	$('.cancelReserva').click(function() {
		var id = $(this).data('id');
		
		var conceito = $('#concept'+id+' input').val();
		$('#concept'+id).empty();
		$('#concept'+id).text(guardaConceito);
		guardaConceito = null;
		$('#options' + id).removeClass( 'show' ).addClass('hide');
		$('#editReserva'+id).removeClass( 'hide' ).addClass('show');

		event.stopPropagation();
	});

	
	//Datable Reserva
	$('#tableReservas')
	.DataTable(
			{
				"pageLength" : 50,
				"order" : [ [ 1, 'asc' ], [ 2, 'asc' ] ],
				"columnDefs" : [ {
					"orderable" : false,
					"targets" : 0
				}, {
					"orderData" : [ 1, 2 ],
					"targets" : 1
				}, {
					"orderable" : false,
					"targets" : 2
				}, {
					"orderable" : false,
					"targets" : 3
				}, {
					"orderable" : false,
					"targets" : 4
				}, {
					"orderable" : false,
					"targets" : 5
				}, ],

				"language" : {
					"sEmptyTable" : "Nenhum registro encontrado",
					"sInfo" : "Mostrando de _START_ até _END_ de _TOTAL_ registros",
					"sInfoEmpty" : "Mostrando 0 até 0 de 0 registros",
					"sInfoFiltered" : "(Filtrados de _MAX_ registros)",
					"sInfoPostFix" : "",
					"sInfoThousands" : ".",
					"sLengthMenu" : "resultados por página _MENU_",
					"sLoadingRecords" : "Carregando...",
					"sProcessing" : "Processando...",
					"sZeroRecords" : "Nenhum registro encontrado",
					"sSearch" : "",
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
	
	//identificar Opção Menu Selecionado
	
	$('select').selectpicker();
	$('input').attr('placeholder', 'Pesquisar...');
	$('input').addClass('form-inline form-control');
	
	
	//Pagina Editar Reserva
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
	
	//Pagina listar Reserva - Excluir Reserva

	$('#excluir').on('click', function(e) {
		e.preventDefault();
		var id = $(this).data('id');
		$('#modal-excluir-reserva').data('id', id);	
		$('#modal-excluir-reserva').find('.btn-danger').attr('href', '/siaf/reserva/' + id + '/excluir');	
		});
			
	$('#btn-confirma-excluir-reserva').on('click', function(){
		var id = $('#modal-excluir-reserva').data('id');
		$('#modal-excluir-reserva').modal('hide');
	});
	
	
	
});

function getRanking(ano, semestre) {
	$("tbody").remove();
	$("#ranking-full").hide();
	$('#load-siaf').show();
	$.ajax({
		type: "GET",
		url: '/siaf/reserva/ranking.json',
		data: {
        	ano : ano,
        	semestre : semestre
		}
	})
	.success(function(result) {
		$('#count-ranking').text('Estimativa de vagas restantes: ' + (result.periodoAtual.vagas - result.afastados.length));
		loadAfastados(result.afastados);
			
		$('i#anterior').show();
		$('i#posterior').show();
		if(result.periodoAnterior == null) {
			$('i#anterior').hide();
		} else {
			$('#anoAnterior').val(result.periodoAnterior.ano);
			$('#semestreAnterior').val(result.periodoAnterior.semestre);
		}
		
		if(result.periodoPosterior == null) {
			$('i#posterior').hide();
		} else {
			$('#anoPosterior').val(result.periodoPosterior.ano);
			$('#semestrePosterior').val(result.periodoPosterior.semestre);
		}
		
		$('#ano').val(result.periodoAtual.ano);
		$('#semestre').val(result.periodoAtual.semestre);
		
		$('#periodoLabel').text(result.periodoAtual.ano + "." + result.periodoAtual.semestre);
		$('#vagas').text(result.periodoAtual.vagas);
		if(result.periodoAtual.encerramento != null) {
			$('#encerramento').text(moment(result.periodoAtual.encerramento, 'YYYY-MM-DD').format('DD/MM/YYYY'));
		} else {
			$('#encerramento').text('-');
		}
		$('#help-encerramento').attr('data-content', 'Data limite para solicitação de afastamento com início para o período ' + (result.periodoAtual.ano + 1) + '.' + result.periodoAtual.semestre);
		
		$('#load-siaf').hide();
		loadRanking(result.ranking.tuplas);
		
		$('table#ranking, table#afastados').removeClass('animated zoomIn').addClass('animated zoomIn').one('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function(){
		      $(this).removeClass('animated zoomIn');
	    });
		$("#ranking-full").show();
		
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
	$('#count-afastados').text(afastados.length);
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

function getConceito(conceito) {
	if(conceito == 0) {
		return "-";
	}
	return conceito;
}

function filtroPeriodo(){
	var ano = $("#filtroAno").val();
	var semestre = $("#filtroSemestre").val();

	sessionStorage.setItem("ano", ano);
	sessionStorage.setItem("semestre", semestre);

	if( (ano.length > 3 && !isNaN(ano)) && (!isNaN(semestre) && (semestre == '1' || semestre == '2')) ){
		loadPeriodo(ano, semestre);
	}

}

function loadPeriodo(ano, semestre) {
	var filtro = {
		"ano" : ano,
		"semestre" : semestre,
	};
	
	$.ajax({
		url: '/siaf/administracao/periodo',
		type: "POST",
		dataType: "html",
		data: filtro,
		success: function(result) {
			showPeriodo(result);
		},
		error: function(error) {
			$('viewPeriodos').hide();
		}
	});
}

function showPeriodo(result) {
	$("#viewPeriodo").html($(result).find("#update-periodo"));

	if(isNaN($("#viewPeriodo #update-periodo #chave").val())){

	}else{
		$(".data").datepicker({
			language: 'pt-BR',
			autoclose: true,
			format: "dd/mm/yyyy"
		});		
		
		$("#status").selectpicker();
	}
	$('#periodo-heading').addClass('animated tada');
	$('#periodo-body').addClass('animated zoomIn');	
	
	$("#viewPeriodo").show();
}

function showPeriodoPost() {
	if(isNaN(parseInt($("#viewPeriodo #update-periodo #chave").val()))){
		$("#viewPeriodo").hide();
	}else{
		$(".data").datepicker({
			autoclose: true,
			format: "dd/mm/yyyy"
		});		
		
		$("#status").selectpicker();
		$("#viewPeriodo").show();
	}
}


function getPeriodos() {
	$.ajax({
		type: "GET",
		url: '/siaf/administracao/periodos.json',
	})
	.success(function(result) {
		loadPeriodos(result);
		$("#periodos tbody td").attr("class", "align-center")
	});
}

function loadPeriodos(result) {
	$("#periodos")
		.bootgrid({
			labels: {
	            all: "Todos",
	            infos: "Mostrando {{ctx.start}} - {{ctx.end}} de {{ctx.total}}",
	            loading: "Carregando...",
	            noResults: "Nenhum resultado encontrado!",
	            refresh: "Atualizar",
	            search: "Buscar"
	        },
	        columnSelection: false,
	        caseSensitive: false,
	        formatters: {
	        	"status": function(column, row) {
	        		if(row.status == 'ABERTO'){
	        			return "<span class=\"label label-success\">" + row.status + "</span>";
	        		}else{
	        			return "<span class=\"label label-danger\">" + row.status + "</span>";
	        		}
	        	},
	        }	        
		})
		.bootgrid("clear")
		.bootgrid("append", result);

}

function messagePeriodo(result) {

	if(result.erro && result.erro.length > 0){
		$(".messages #erroDiv").append("<div id=\"info\" " +
				"class=\"alert-danger margin-top hide\" " +
				"role=\"alert\"> " +
				"<button type=\"button\" class=\"close\" data-dismiss=\"alert\">" +
				"<span aria-hidden=\"true\">&times;</span>" +
				"<span class=\"sr-only\">Close</span>" +
				"</button><p></p></div>");
		$(".messages #erro p").text(result.erro);
		$(".messages #erro").removeClass( "hide" ).addClass('show');
	} 
	
	if(result.info &&  result.info.length > 0){
		$(".messages #infoDiv").append("<div id=\"info\" " +
				"class=\"alert alert-info margin-top hide\" " +
				"role=\"alert\"> " +
				"<button type=\"button\" class=\"close\" data-dismiss=\"alert\">" +
				"<span aria-hidden=\"true\">&times;</span>" +
				"<span class=\"sr-only\">Close</span>" +
				"</button><p></p></div>");
		$(".messages #info p").text(result.info);
		$(".messages #info").removeClass( "hide" ).addClass('show');
	} 
}

function messageReservaEmAberto(result) {

	if(result.erro && result.erro.length > 0){
		$(".messages #erroDiv").append("<div id=\"info\" " +
				"class=\"alert alert-danger margin-top hide\" " +
				"role=\"alert\"> " +
				"<button type=\"button\" class=\"close\" data-dismiss=\"alert\">" +
				"<span aria-hidden=\"true\">&times;</span>" +
				"<span class=\"sr-only\">Close</span>" +
				"</button><p></p></div>");
		$(".messages #erroDiv #erro p").text(result.erro);
	} 
	
	if(result.info &&  result.info.length > 0){
		$(".messages #infoDiv").append("<div id=\"info\" " +
				"class=\"alert alert-info margin-top hide\" " +
				"role=\"alert\"> " +
				"<button type=\"button\" class=\"close\" data-dismiss=\"alert\">" +
				"<span aria-hidden=\"true\">&times;</span>" +
				"<span class=\"sr-only\">Close</span>" +
				"</button><p></p></div>");
		$(".messages #infoDiv #info p").text(result.info);
		$(".messages #infoDiv #info").removeClass( "hide" ).addClass('show');
	} 
}

function validarConceito(campo){
	var numero="34567"; //variáveis aceitas na função.
	var campo_temporario; //váriavel responsável pela verificação dos dados.
	for (var i=0;i<campo.value.length;i++){ 
		// A variável campo_temporario verifica se existe algum valor dentro do form.
		campo_temporario=campo.value.substring(i,i+1)
		// Caso esse campo seja diferente da variavel numero, ele irá limpar o form.
		if (numero.indexOf(campo_temporario)==-1){ 
			// Caso contrário, ele irá deixar inserir o número no form.	
			campo.value = campo.value.substring(0,i);
		}
	}
}

function somenteNumeros(campo){
	var numero="0123456789"; //variáveis aceitas na função.
	var campo_temporario; //váriavel responsável pela verificação dos dados.
	for (var i=0;i<campo.value.length;i++){ 
		// A variável campo_temporario verifica se existe algum valor dentro do form.
		campo_temporario=campo.value.substring(i,i+1)
		// Caso esse campo seja diferente da variavel numero, ele irá limpar o form.
		if (numero.indexOf(campo_temporario)==-1){ 
			// Caso contrário, ele irá deixar inserir o número no form.	
			campo.value = campo.value.substring(0,i);
		}
	}
}

