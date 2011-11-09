[#ftl]
<!DOCTYPE HTML>

<html>
<head>
<title>Byte Code</title>

<link rel="stylesheet" type="text/css" href="styles/main.css">

<style>
#compileForm {
	position: relative;
	width: 640px;
}

#compileForm .controls {
	position: absolute;
	top: 5px;
	right: 15px;
	color: #303030;
	
	background: #d0d0d0;
	-webkit-border-radius: 5px;
	padding: 2px;
}

/* DQH - ugly, positioning hack */
#compileForm input[name="optimizeDex"],
#compileForm label[for="optimizeDex"] {
	position: relative;
	left: 15px;
}

#compileForm label[for="submit"] {
	position: relative;
	top: -10px;
	padding: 2px;
}

#compileForm label[for="submit"]:hover {
	cursor: pointer;
}

#content {
	float: left;
	padding: 10px;
	width: 640px;
}

#code {
	width: 98%;
	height: 6em;
	font-size: x-large;
	-webkit-border-radius: 5px;
}

.code-section {
	display: inline-block;
	width: 300px;
	height: 400px;
	border: 2px solid green;
	-webkit-border-radius: 5px;
	
	float: left;
	padding-left: 5px;
	margin: 5px;
}

.code-section h1 {
	font-size: x-large;
}

code {
	display: block;
	white-space: pre;
	
	font-size: x-large;
	max-height: 375px;
	overflow: auto;
}
</style>
</head>
<body>
[#escape x as x?html]
	<ul id="examples">
	[#list codeExamples as codeExample]
		<li><a href='#' title='${codeExample.code}'>${codeExample.name}</a></li>
	[/#list]
	</ul>
	
	<section id="content">
		<form id="compileForm">
			<textarea id="code" name="code"></textarea>
			<div class="controls">
				<input name="submit" type="image" src="/images/gear.png" title="Compile" width="32" height="32">
				<label for="submit">Compile</label>
				<br>
				<input type="checkbox" name="optimizeDex" value="true" checked>
				<label for="optimizeDex">Optimize</label>
			</div>
		</form>
		
		<div class="code-section">
			<h1>JVM Bytecode</h1>
			<code id="jvm-bytecode"></code>
		</div>
		
		<div class="code-section">
			<h1>Dalvik Bytecode</h1>
			<code id="dalvik-bytecode"></code>
		</div>
	</section>
[/#escape]

<script src="/js/jquery-1.6.4.js"></script>

<script>
[#escape x as x?js_string]
$( function() {
	$('#compileForm').submit( function(e) {
		e.preventDefault();
		
		$('.code-section code').hide();
		
		var promise = $.post('${path}', $(this).serialize(), 'json');
		promise.success( function(response) {
			$('#jvm-bytecode').text(response.byteCode);
			$('#dalvik-bytecode').text(response.dalvikCode);
			
			$('.code-section code').fadeIn(500);
		} );
		promise.error( function() {
			window.alert('Failed to Compile!');
		} );
	} );
	
	$('body').keydown( function(e) {
		//Command + B
		if ( e.which === 66 && e.metaKey ) {
			e.preventDefault();
			$('#compileForm').submit();
		}
	} );
	
	$('#examples a').click( function(e) {
		e.preventDefault();
		
		var name = $(this).text();
		var code = $(this).attr('title');
		
		$('#code').val(code);
		$('#compileForm').submit();
	} );
} );
[/#escape]
</script>

</body>
</html>