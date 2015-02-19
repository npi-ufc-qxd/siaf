$(document).ready(function() {
	
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
	});	

	$('#professores').on('click', '.salvar', function() {
		$("#contentProfessores td.editProf").css("width", "50px");
		$("#contentProfessores td.editAcao").css("width", "40px");

	    var $btn = $(this);

	    var id = $btn.data("id");
		var semestre = $( "select option:selected" ).val();
		var ano = $("input").val();
		
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
				},
				error: function(error) {
				}		
			});
		}
	    $btn.closest('tr').find('.editable').editable('hide');
	    $("options" +id).removeClass( "show" ).addClass('hide').siblings('.edit').show();
	});
	
	$('#professores').on('click', '.cancel', function() {

		$("#contentProfessores td.editProf").css("width", "50px");
		$("#contentProfessores td.editAcao").css("width", "40px");
	    var $btn = $(this);
	    var id = $btn.data("id");
	    $(".options" +id).removeClass( "show" ).addClass('hide').siblings('.edit').show();
	    $btn.closest('tr').find('.editable').editable('hide');
	});
	
	$('[data-toggle="tooltip"]').tooltip();
	
	function showMessage(result) {
		$("#wrapper").html($(result).find("#wrapper"));
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
            },
            conceito:{
                required:"Campo obrigatório",
            },
            instituicao:{
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
	
	$('#anterior').click(function(){
		getRanking($('#anoAnterior').val(), $('#semestreAnterior').val());
	});
	
	$('#posterior').click(function(){
		getRanking($('#anoPosterior').val(), $('#semestrePosterior').val());
	});
	
	$("#viewPeriodo").hide();
	$("#warning-ranking").hide();
	showPeriodoPost();
	
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
	
	$("#encerramento").mask("99/99/9999");

	$('#excluir-reserva').on('show.bs.modal', function(e) {
		$(this).find('.modal-body').text('Tem certeza de que deseja excluir a reserva para o período \"' + $(e.relatedTarget).data('name') + '\"?');
		$(this).find('.btn-danger').attr('href', $(e.relatedTarget).data('href'));
	});
	

	var editVagas = false;
	var editEncerramento = false;

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
			console.log(result.editEncerramento);
			console.log(result.editVagas);
			
			editVagas = result.editVagas;
			editEncerramento = result.editEncerramento;
			$('#options'+id).attr('data-encerramento', result.editEncerramento);
			$('#options'+id).attr('data-vagas', result.editVagas);

			if(result.editEncerramento){
			    encerramento = $('#encerramento'+id ).text();
			    
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
				$('#vagas'+id).empty();
				$('#vagas'+id).append('<input name="inputVagas" class="form-control" size="2" value="'+vagas+'"/>');
			}
			
			if(result.editEncerramento || result.editVagas){
			    $('#tablePeriodos').find('#options' + id).removeClass( 'hide' ).addClass('show');
			    $('#editPeriodo'+id).removeClass( 'show' ).addClass('hide');
			}

			if(!result.editEncerramento && !result.editVagas){
				$("#info-periodo .modal-body b").text('Não é possível editar as informações deste periodo.');
				$("#info-periodo").modal('show');
				
			}
			
		
		});
		event.stopPropagation();
	})

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
			$('#encerramento'+id).text(encerramento);
		}
		
		if($('#options' + id).data('vagas')){
			var vagas = $('#vagas'+id+' input').val();
			$('#vagas'+id).empty();
			$('#vagas'+id).text(vagas);
		}

		$('#options' + id).removeClass( 'show' ).addClass('hide');
		$('#editPeriodo'+id).removeClass( 'hide' ).addClass('show');

		event.stopPropagation();
	});
	
	
});

function getRanking(ano, semestre) {
	$("tbody").remove();
	$("#warning-ranking").hide();
	$('#load-siaf').show();
	$.ajax({
		type: "POST",
		url: '/siaf/reserva/ranking.json',
		data: {
        	ano : ano,
        	semestre : semestre
		}
	})
	.success(function(result) {
		
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
		$('#vagas').text("Vagas: " + result.periodoAtual.vagas);
		if(result.periodoAtual.encerramento != null) {
			$('#encerramento').text("Encerramento: " + moment(result.periodoAtual.encerramento, 'YYYY-MM-DD').format('DD/MM/YYYY'));
		} else {
			$('#encerramento').text("Encerramento: -");
		}
		
		$('#load-siaf').hide();
		loadTable(result.ranking.tuplas, "ranking");
		
		$('table#ranking').removeClass('animated zoomIn').addClass('animated zoomIn').one('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function(){
		      $(this).removeClass('animated zoomIn');
	    });
		
	});
}

function loadTable(result, table) {
	$("tbody").remove();
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
            $('<td class=\"align-center\">').text(getPrograma(item.reserva.programa)),
            $('<td class=\"pontuacao align-center\">').text(item.pontuacao)
        ).appendTo('tbody');
    });
	if(result.length == 0) {
		$("#warning-ranking").show();
	}
}

function getPrograma(programa) {
	if(programa == 'POS_DOUTORADO') {
		return "PÓS DOUTORADO";
	}
	return programa;
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
	$(".messages #erro").removeClass( "show" ).addClass('hide');
	$(".messages #info").removeClass( "show" ).addClass('hide');

	if(result.erro && result.erro.length > 0){
		alert(result.erro);
		$(".messages #erro p").text(result.erro);
		$(".messages #erro").removeClass( "hide" ).addClass('show');
		$('#periodo' + result.periodo.id + ' td').css("background", "#f2dede");
	} 
	
	if(result.info &&  result.info.length > 0){
		alert(result.info);
		$(".messages #info p").text(result.info);
		$(".messages #info").removeClass( "hide" ).addClass('show');
		$('#periodo' + result.periodo.id + ' td').css("background", "#d9edf7");
		alert(result.info);
	} 
	
}
