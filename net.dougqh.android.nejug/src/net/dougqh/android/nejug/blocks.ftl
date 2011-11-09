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

#graph {
	width: 600px;
	height: 400px;
}
</style>
</head>
<body>
[#escape x as x?html]
	<ul id="examples">
		<li><a href='#'>Placeholder</a></li>
	[#list blockExamples as example]
		<li><a href='#' title='${example.code}'>${example.name}</a></li>
	[/#list]
	</ul>
	
	<section id="content">
		<form id="compileForm">
			<textarea id="code" name="code"></textarea>
			<div class="controls">
				<input name="submit" type="image" src="/images/gear.png" title="Compile" width="32" height="32">
				<label for="submit">Compile</label>
			</div>
		</form>
		
		<div id="graph"></div>
	</section>
[/#escape]

<script src="/js/jquery-1.6.4.js"></script>
<script src="/js/jit.js"></script>

<script>
[#escape x as x?js_string]
$.fn.render = function(blockGraph) {
	var nodes = [];
	for (var id in blockGraph) {
		var block = blockGraph[id];
		var nextIds = block.nextIds || [];
		
		var adjacencies = [];
		for (var i = 0, len = nextIds.length; i < len; ++i ) {
			adjacencies.push({
				nodeFrom: id,
				nodeTo: nextIds[i],
				data: {}
			});
		}
		
		var node = {
			id: id,
			name: block.code,
			data: {},
			adjacencies: adjacencies
		};
		node.data.entry = block.entry;
		node.data.exit = block.exit;
		
		nodes.push(node);
	}

	var graph = new $jit.ForceDirected({  
		injectInto: this.toArray()[0],
		
		Navigation: { enabled: false },
		Node: {
			overrideable: true,
			type: 'rectangle',
			width: 100,
			height: 50,
			color: '#44aa44',
			align: 'center'
		},
		Edge: {
			overrideable: false,
			color: '#d0d0d0',
			lineWidth: 1,
			type: 'arrow'
		},
		Label: {
		 	type: 'Native',
		 	size: 15,
		 	style: 'bold'
		},
		Tips: { enabled: false },
		 
		iterations: 200,  
  		levelDistance: 130,
  		 
  		onBeforePlotNode: function(node) {
  			if (node.data.entry) {
  				node.setPos(new $jit.Complex(0, -150));	
  			} else if (node.data.exit) {
  				node.setPos(new $jit.Complex(0, 150));
  			} 	
  		}
	});
	
	graph.loadJSON(nodes);

	graph.computeIncremental({
		iter: 10,
	    property: 'end',
	    onComplete: function(){
	    	graph.animate({
	    		modes: ['linear'],
	        	transition: $jit.Trans.Elastic.easeOut,
	        	duration: 0
	      	});
	    }
	});
}

$(function() {
	var blocks = { 
		'entry': {
			code: 'foo',
			entry: true,
			nextIds: ['if', 'else']
		},
		'if': {
			code: 'baz',
			nextIds: ['exit']
		},
		'else': {
			code: 'quux',
			nextIds: ['exit']
		},
		'exit': {
			code: 'bar',
			exit: true
		}
	};
	$('#graph').render(blocks);
	
	$('#compileForm').submit(function(e) {
		e.preventDefault();
		
		$('.code-section code').hide();
		
		var promise = $.post('${path}', $(this).serialize(), 'json');
		promise.success(function(response) {
			window.alert('Success!');
		});
		promise.error(function() {
			window.alert('Failed to Compile!');
		});
	});
	
	$('body').keydown(function(e) {
		//Command + B
		if ( e.which === 66 && e.metaKey ) {
			e.preventDefault();
			$('#compileForm').submit();
		}
	});
	
	$('#examples a').click(function(e) {
		e.preventDefault();
		
		var name = $(this).text();
		var code = $(this).attr('title');
		
		$('#code').val(code);
		$('#compileForm').submit();
	});
});
[/#escape]
</script>

</body>
</html>