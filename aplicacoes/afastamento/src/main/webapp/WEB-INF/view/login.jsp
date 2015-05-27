<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html>
<head>
	<title>Login</title>
	<link rel="shortcut icon" href="<c:url value="/resources/images/signo.png" />" />

	<link href="<c:url value="/webjars/bootstrap/3.3.2/css/bootstrap.min.css" />" rel="stylesheet" />
	<link href="<c:url value="/webjars/font-awesome/4.3.0/css/font-awesome.css" />" rel="stylesheet" />
	<link href="<c:url value="/resources/css/hover-min.css" />" rel="stylesheet" />
	<link href="<c:url value="/resources/css/estilo-login.css" />" rel="stylesheet" />

</head>

<body onload='document.f.j_username.focus();'>

	<div class="login-container" align="center">
		<div class="login-header">
			<img alt="Sistema de Afastamento de Professores" src="<c:url value="/resources/images/logo-beta.png" />">
		</div>
		
		<div class="login-form">
			
			<div class="login-text">
				<span id="title">Faça seu login</span>
			</div>
				<form:form role="form" servletRelativeAction="/j_spring_security_check"  method="post" id="login-form" autocomplete="off" class="form-horizontal">
	
					<c:if test="${not empty erro}">
						<div class="login-error"><i class="fa fa-times-circle-o"></i> ${erro}</div>
					</c:if>
		
					<c:if test="${not empty msg}">
						<div class="msg"> <i class="fa fa-info-circle"></i>${msg}</div>
					</c:if>
				
					<div class="form-group">
						<div id="inputLogin" class="form-inline input-group input-login">
						    <span class="input-group-addon"><i class="fa fa-user"></i></span>
							<input type="text" name="j_username" id="cpf" class="form-control" placeholder="cpf" required="required">
						</div>
					</div>
										
					<div class="form-group">
						<div id="inputSenha" class="form-inline input-group input-login">
						    <span class="input-group-addon"><i class="fa fa-lock"></i></span>
						    <input type="password" name="j_password" id="key" class="form-control" placeholder="senha" required="required">
						</div>
					</div>
					
					
						
					<div id="div-captcha-erro">
					    <div id="captcha-login" class=""></div>
					</div>
					 
					<div style="text-align: center;">
						<button class="btn btn-siaf btn-login hvr-icon-forward" name="submit" type="submit" value="Login" value="Login">Login</button>
					</div>
				</form:form>
		</div>
	</div>
	<footer>
		<img id="logo-campus" alt="Campus da UFC em Quixadá" src="<c:url value="/resources/images/logo-npi.png" />">
		<p>Desenvolvido por <a href="http://www.npi.quixada.ufc.br" target="_blank">Núcleo de Práticas em Informática</a></p>
	<p>Universidade Federal do Ceará - Campus Quixadá</p>
	</footer>
	
	<script src="https://www.google.com/recaptcha/api.js" async defer></script>
	<script src="<c:url value="/webjars/jquery/2.1.0/jquery.min.js" />"></script>
	<script src="<c:url value="/webjars/bootstrap/3.3.2/js/bootstrap.min.js" />"></script>
	<script src="<c:url value="/resources/js/jquery.validate.min.js" />"></script>
	<script src="<c:url value="/resources/js/login.js" />"></script>
	
	<script type="text/javascript">
	$(document).ready(function() {
		$("#btn-login").on('click', function(){
			$("#login-form").submit();
		});
		
	});
	</script>

</body>
</html>