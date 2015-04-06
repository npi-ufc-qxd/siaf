$(document).ready(function() {

	$("span[for='key']").css({
		margin : "-15px auto 0 auto",
		padding: '0'
	});

	$("span[for='cpf']").css({
		margin : "-15px auto 0 auto",
		padding: '0',
	 });
	
	var widget_login;
	var widget_recuperacao;
	window.onload = function() {
		widget_login = grecaptcha.render('captcha-login', {
          'sitekey' : '6Ld8JwETAAAAAJO7YwQhpEjZOJZphzh0PfvinsZ5',
        });
		widget_recuperacao = grecaptcha.render('captcha-recuperacao', {
          'sitekey' : '6Ld8JwETAAAAAJO7YwQhpEjZOJZphzh0PfvinsZ5',
        });
	}
	$('#login-form').validate({
		rules: {
            
        },
        highlight: function(element) {
            $(element).closest('.form-group').addClass('has-error');
        },
        unhighlight: function(element) {
            $(element).closest('.form-group').removeClass('has-error');
        },
        errorElement: 'span',
        errorClass: 'help-block',
        errorPlacement: function(error, element) {
        	if (element.attr("name") == "j_username") {
        		error.insertAfter("#inputLogin");
        	} else if (element.attr("name") == "j_password") {
        		error.insertAfter("#inputSenha");
        	} else {
                error.insertAfter(element.parent().children().last());
        	}
        	
        },
        messages:{
        	j_username:{
                required:"Campo obrigatório",
            },
            j_password:{
                required:"Campo obrigatório",
            }
        }
    });
	
	$('#login-form').submit(function(){
		/*if(grecaptcha.getResponse() == '') {
			$('#div-captcha-erro').after('<span id="captcha-erro" class="help-block">Selecione a opção "Não sou um robô"</span>');
			return false;
		}*/
	});
	
});
