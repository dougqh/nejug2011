[#ftl]
<!DOCTYPE HTML>

<html>
<head>
<title>Benchmarks</title>

<link rel="stylesheet" type="text/css" href="styles/main.css">

<style>
#codeWrapper {
	float: left;
		
	margin: 10px;
	margin-top: 13px;
	
	border: 2px solid white;
	-webkit-border-radius: 5px;
}

#code {
	/*
	DQH - padding is applied here to provide
	better scroll bar placement.
	*/
	
	padding: 0.5em;

	overflow-x: hidden;
	
	width: 450px;
	height: 530px;
}

#graphs {
	float: right;
	width: 180px;
}

#force {
	display: inline-block;
	padding: 0.25em 1em 0.25em 1em;
	
	background: lightgray;
	border: 1px solid white;
	-webkit-border-radius: 5px;
	
	color: #303030;
	text-decoration: none;
}

#force:hover {
	text-decoration: underline;
}

#hotspot h1,
#dalvik h1 {
	display: block;
}

#hotspot-graph,
#dalvik-graph {
	display: block;
	
	width: 160px;
	height: 160px;	
}

/*
DQH - Ugly positioning hacks
Tried floating #graphs right and using display: block for everything to no avail.
The canvas were always out place for some reason.
*/
#force {
	position: relative;
	top: 12px;
}

#hotspot h1 {
	position: relative;
	top: -3px;
}

#hotspot-graph {
	position: fixed;
	left: 623px;
	top: 85px;
}

#dalvik h1 {
	position: relative;
	top: 147px;
}

#dalvik-graph {
	position: fixed;
	left: 623px;
	top: 282px;
}

.waiting {
	margin: 10px;
	padding: 58px;
	border: 1px solid gray;
}

#legend {
	position: fixed;
	left: 624px;
	top: 446px;
}

.legendLabel {
	color: white;
}
</style>
</head>

<body>
[#escape x as x?html]
	<ul id="examples">
	[#list benchmarks as benchmark]
		<li><a id="benchmark-${benchmark_index}" href='#'>${benchmark.name}</a></li>
	[/#list]
	</ul>
	
	<div id='codeWrapper'>
		<code id='code'></code>
	</div>
	
	<section class='graphs'>
		<a id='force' href='#'>Run Benchmark</a>
		
		<div id='hotspot'>
			<h1>HotSpot</h1>
			<div id='hotspot-graph'></div>
		</div>
		
		<div id='dalvik'>
			<h1>Dalvik</h1>
			<div id='dalvik-graph'></div>
		</div>
		
		<div id='legend'></div>
	</section>
[/#escape]

<script src="/js/jquery-1.6.4.js"></script>
<script src="/js/jquery.flot.js"></script>
<script src="/js/jquery.flot.stack.js"></script>

<script>
[#escape x as x?js_string]
$.fn.waiting = function() {
	this.empty();
	$('#legend').hide();
	$('<img class="waiting" src="/images/spinner_white.gif">').appendTo(this);
}

//Flot documentation: http://flot.googlecode.com/svn/trunk/API.txt
$.fn.graph = function(data) {
	//Start at because of the throw-away
	var plotData = [];
	for ( var i = 0, len = data.length; i < len; ++i ) {
		plotData.push( {
			label: data[i].label,
			data: [[i, data[i].nanoTime]]
		} );
	}
	
	this.empty();
	
	$.plot(this, plotData, {
        series: {
            lines: { show: false, fill: true, steps: false },
            bars: { show: true, barWidth: 0.6 }
        },
        xaxis: { show: false },
        yaxis: { show: false },
        legend: { container: '#legend' },
        hoverable: true
    });
    
    $('#legend').show();
};

$.fn.emptyGraph = function() {
	this.graph([]);
}

$(function() {
	//Declared early for use by functions below, but populated at
	//end to generally keep line numbers from the browser matching up.
	var benchmarks = [];

	function runBenchmark(index, force) {
		$('#hotspot-graph').waiting();
		$('#dalvik-graph').waiting();
		
		var data = 'benchmarkIndex=' + index;
		if ( force ) {
			data += '&force=true';
		}
		
		var promise = $.post('${path}', data, 'json');
		promise.success( function( result ) {
			$('#hotspot-graph').graph(result.hotspot);
			$('#dalvik-graph').graph(result.dalvik);
		} );
		promise.error( function() {
			$('#hotspot-graph').emptyGraph();
			$('#dalvik-graph').emptyGraph();
			window.alert( 'Problem running benchmark' );
		} );	
	}
	
    $('#hotspot-graph').emptyGraph();
    $('#dalvik-graph').emptyGraph();
         
	$('#examples a').click( function(e) {
		e.preventDefault();
		
		var index = parseInt(this.id.replace('benchmark-', ''), 10);
		$('#code').fadeOut(250, function() {
			$('#code').text(benchmarks[index]);
			$('#code').scrollTop(0);
			$('#code').fadeIn(250);
		} );
		
		$('#examples a.selected').removeClass('selected');
		$(this).addClass('selected');
		
		runBenchmark(index, false);
	} );
	
	$('#force').click( function(e) {
		e.preventDefault();
		
		var selectedId = $('#examples a.selected').attr( 'id' );
		var selectedIndex = parseInt(selectedId.replace('benchmark-', ''), 10);
		
		runBenchmark(selectedIndex, true);
	} );
	
	[#list benchmarks as benchmark]
	benchmarks[${benchmark_index}]='${benchmark.source}';
	[/#list]
	
});
[/#escape]
</script>

</body>
</html>