var nodes = [];
var links = [];

var svg = d3.select("svg").style("background-color", "white"),
    width,
    height,
    node,
    link;

width = $('#exploreDiv').width();
height = $('#exploreDiv').height();

var g = svg.append("g")
		.attr("transform", function(){return "translate(" + width/2 + "," + height/2 + ") scale(1)"})

var edgeLevel = 5;
var edgeLimit = 10;
var pos = 0;
var colors = ["#ff6600", "#ffc600"];

var zoom = d3.zoom()
.scaleExtent([1/4, 8])
.on("zoom", zoomed);

svg.call(zoom)
		    .on("dblclick.zoom", null);

svg.call(zoom.transform, d3.zoomIdentity.translate(width/2, height/2));

function zoomed() {
	g.attr("transform", d3.event.transform);
	}

$("#exploreBtn").on('click', function () {
  var startNode = $('#headingChoice').text();
  $('#choiceDiv').css("display", "none");
  $('#headingChoice').css('display', 'none');
  $('.content').css('display', 'none');
  $('#exploreDiv').css('display', 'block');
  $("body").css("cursor", "progress");

  pos = 0;
  getNodesData(startNode);
  
});

function getNodesData(nodeId) {
	var selectedOpt = $("#visualizationTypeGroup option:selected" ).text();
// console.log("getNode/" + nodeId.replace(/#/g,"%23") + "/" + selectedOpt);
	  if(selectedOpt !== null && selectedOpt !== '') {
		  d3.json("getNode/" + nodeId.replace(/#/g,"%23") + "/" + selectedOpt).then(function (data) {
			
		    updateData(data, null);
		    update();
		    $("body").css("cursor", "default");
		  });
	  } else {
// getPredicates();
	  }

  
}

var simulation = d3.forceSimulation()
    .force("link", d3.forceLink().id(function (d) { return d.id })
        .distance(200).strength(0.5)
    )
    .force("charge", d3.forceManyBody()
        .strength(-400)
    )
//    .force("center", d3.forceCenter(width / 2, height / 2));


function getNodeRelations(currNode, circ, text, loader) {
    var selectedOpt = $("#visualizationTypeGroup option:selected" ).text();
// console.log("getNodeData/" + nodeId.replace(/#/g,"%23") + "/" + selectedOpt);
    var edgeDirection = $('input[name="edgeDirOpt"]:checked').val();
//    console.log(edgeDirection);
	  if(selectedOpt !== null && selectedOpt !== '') {
		  d3.json("getNodeRelations/" + currNode.id.replace(/#/g,"%23") + "/" + selectedOpt + "/" + edgeDirection + "/" + currNode.edgeOffset * edgeLimit +"/" + edgeLimit).then(function (data) {
				
		    updateData(data, currNode);
		    update();
		    circ
		    	.attr("fill", function(d) {return d.num === 0 ? colors[0] : colors[1]});
		    text
				.style("opacity", 1);
		    loader.remove();
		  	});
	  } else {
// getPredicates();
	  }
}

link = g.append("g").selectAll(".link");
var edgepaths = g.append("g").selectAll(".edgepath");
var edgelabels = g.append("g").selectAll(".edgelabel");
node = g.append("g").selectAll(".node");


svg.append('defs').append('marker')
.attr('id','arrowhead')
.attr('viewBox','-0 -5 10 10')
.attr('refX',31)
.attr('refY',0)
.attr('orient','auto')
.attr('markerWidth',6)
.attr('markerHeight',6)
.attr('xoverflow','visible')
.append('svg:path')
.attr('d', 'M 0,-5 L 10 ,0 L 0,5')
.attr('fill', '#cacaca')
.style('stroke','none')


function update() {
	
	link = link.data(links, function (d) { return d.source.id + "-" + d.edge + "-" + d.target.id });
	  link.exit().remove();
	  link = link.enter()
	      .append("line")
	      .attr("id", function (d) { return d.source + "-" + d.edge + "-" + d.target })
	      .attr("class", "link")
	      .attr("stroke", "#cacaca")
	      .attr("stroke-width", 1.5)
	      .attr('marker-end','url(#arrowhead)')
	      .merge(link);

	  edgepaths = edgepaths.data(links, function (d) { return 'edgepath-' + d.source.id + "-" + d.edge + "-" + d.target.id });
	  edgepaths.exit().remove();
	  edgepaths = edgepaths
	      .enter()
	      .append('path')
// .merge(edgepaths)
	      .attr('class', 'edgepath')
	      .attr('fill-opacity', 0)
	      .attr('stroke-opacity', 0)
	      .attr('id', function (d) { return 'edgepath-' + d.source + "-" + d.edge + "-" + d.target })
	      .style("pointer-events", "none")
	      .merge(edgepaths)
	      ;
//	  console.log(links);

	  edgelabels = edgelabels.data(links, function (d) { return 'edgelabel-' + d.source.id + "-" + d.edge + "-" + d.target.id });
	  edgelabels.exit().remove();
	  var edgelabelsEnter = edgelabels.enter()
	      .append('text')
	      .style("pointer-events", "none")
	      .attr('class', 'edgelabel')
	      .attr('id', function (d) { return 'edgelabel-' + d.source + "-" + d.edge + "-" + d.target })
	      .attr('font-size', 10)
	      .attr('y', 9)
	      .attr('fill', '#898989')
	      .attr('z-index', 1)
	      ;

	  edgelabels = edgelabelsEnter.merge(edgelabels);


	  edgelabelsEnter
	      .append('textPath')
	      .attr('xlink:href', function (d) { return '#edgepath-' + d.source + "-" + d.edge + "-" + d.target })
	      .style("text-anchor", "middle")
	      .style("pointer-events", "none")
	      .attr("startOffset", "40%")
	      .text(function (d) { return d.edge })
	      ;

    node = node.data(nodes, function (d) { return d.id; });

    node.exit().remove();
    
    var nodeEnter = node.enter()
        .append("g")
        .attr("id", function (d) { return d.id })
        .attr("class", "node")
        .on("mouseover", function () {
            $(this).css('cursor', 'pointer');
            d3.select(this).selectAll(".icon")
                .transition().delay(50).style("opacity", 1)
                ;
        })
        .on("mouseout", function () {
            d3.select(this).selectAll(".icon")
                .transition().delay(150).style("opacity", 0) // 150 ms
                ;
        })
        .on("dblclick", function (d) {
// getNodeRelations(d.id);
        	window.open(d.id, '_blank');
       
        })
        .call(d3.drag()
            .on("start", dragstarted)
            .on("drag", dragged)
        );

    node = nodeEnter.merge(node);
    nodeEnter
        .append('svg:circle')
        .attr("class", "circle")
        .attr("r", 20)
// .attr("fill", "#ffc600") //vorher: #fc8100
        .attr("fill", function(d) {return d.num === 0 ? colors[0] : colors[1]});
        ;

    nodeEnter
        .append('svg:text')
        .text(function (d) { 
// console.log("hallo node " + d.id)
        	return d.label !== null ? d.label : d.id ; })
        .attr("font-family", "sans-serif")
        .attr("font-size", "15px")
        .attr('font-weight', 'bolder')
        .attr("text-anchor", "middle")
        .attr("class", "nodeText")
        .attr("x", 0)
        .attr("y", 3)
        ;

    nodeEnter.append('svg:image')
        .attr("xlink:href", "js/graphics/delete.svg")
        .attr("x", 16)
        .attr("y", -24)
        .attr("width", 12)
        .attr("height", 12)
        .attr("class", "icon")
        .style("opacity", 0)
//        .on("click", function (d) {
//
//            if (nodes.length < 2) {
//                return;
//            }
//            nodes = nodes.filter(function (n) {
//                return n.id != d.id;
//            });
//            links = links.filter(function (l) {
//                return d.id != l.source.id && d.id != l.target.id;
//            });
//
//            update();
//        });
        .on("click", function(d){
        	deleteNode(d);
        	update();
        });
    
    nodeEnter.append('svg:image')
    .attr("xlink:href", "js/graphics/expand.svg")
    .attr("x", -27)
    .attr("y", -24)
    .attr("width", 12)
    .attr("height", 12)
    .attr("class", "icon")
    .style("opacity", 0)
    .on("click", function (d) {
    	d.num = 0;
    	var thisVar = d3.select(this.parentNode);
    	var circ = thisVar.select(".circle");
    	var text = thisVar.select(".nodeText");
    	var icons = thisVar.select(".icon");
    	circ
    		.attr("fill", "#978d75")
// .style("opacity", 0.3);
    	text
    		.style("opacity", 0.3);
    	icons
    		.style("opacity", 0)
    	console.log(thisVar);
//    	console.log(d);
    	var loader = thisVar.append("svg:image")
    		.attr("xlink:href", "js/graphics/spinner.gif")
    		.attr("x", -20)
    		.attr("y", -20)
    		.attr("width", 40)
    		.attr("height", 40)
    		.style("opacity", 1)

    	getNodeRelations(d, circ, text, loader);
    });
    
    nodeEnter.append('svg:text')
//    .text(function(d) {return d.num})
    .attr("font-family", "sans-serif")
    .attr("font-size", "10px")
    .attr('font-weight', 'bolder')
    .attr("text-anchor", "middle")
    .attr("x", -22)
    .attr("y", 15)
    .attr("class", "icon node-page")
    .style("opacity", 0)
    .style("fill", "grey")
    .on("click", function (d) {
            var pageNum = prompt("Enter the Offset", "0");
            console.log(pageNum);
            if(pageNum == null || pageNum == '') {
            	return;
            } else if(!isNaN(pageNum)){
            	d.edgeOffset = parseInt(pageNum, 10);
            	var thisVar = d3.select(this.parentNode);
            	var circ = thisVar.select(".node-page")
            					  .text(d.edgeOffset);
            }
        })
    
    nodeEnter.append('svg:image')
        .attr("xlink:href", "js/graphics/info.svg")
        .attr("x", -12)
        .attr("y", -33)
        .attr("width", 12)
        .attr("height", 12)
        .attr("class", "icon")
        .style("opacity", 0)
        .on("click", function (d) {
            getInfo(d.id);
        });

    nodeEnter.append('svg:image')
        .attr("xlink:href", "js/graphics/pin.svg")
        .attr("x", 3)
        .attr("y", -31)
        .attr("width", 12)
        .attr("height", 12)
        .attr("class", "icon")
        .style("opacity", 0)
        .on("click", function (d) {
            d.fx = null;
            d.fy = null;
        });
    
    
    nodeEnter.append('svg:image')
    .attr("xlink:href", "js/graphics/pencil.svg")
    .attr("x", 16)
    .attr("y", 6)
    .attr("width", 12)
    .attr("height", 12)
    .attr("class", "icon")
    .style("opacity", 0)
    .on("click", function (d) {
    	d.num = d.num === 0 ? pos : 0;
    	var thisVar = d3.select(this.parentNode);
    	var circ = thisVar.select(".circle");
    	circ
    		.attr("fill", function(d) {return d.num === 0 ? colors[0] : colors[1]});
// .attr("fill", "#ff6600");
    	console.log(d.num);
    });
    
    nodePage = node.selectAll(".node-page");
    nodePage.text(function(d){return d.edgeOffset});

    simulation.nodes(nodes).on("tick", ticked);
    simulation.force("link").links(links);
    simulation
        .alphaTarget(0.3)
        .restart();
}

function deleteNode(d) {

    
    
    var nodesToCheck = [];
    var nodesWithEdges = [];
    
    links = links.filter(function (l) {
        if(d.id === l.source.id) {
        	if(l.target.num !== 0){
        		nodesToCheck.push(l.target);
        	}     	
        } else if(d.id === l.target.id) {
        	if(l.source.num !== 0){
        		nodesToCheck.push(l.source);
        	} 
        } else {
        	nodesWithEdges.push(l.source);
        	nodesWithEdges.push(l.target);
        	return true;
        }
    	
//    	return d.id != l.source.id && d.id != l.target.id;
    });
    
    var nodesToRemove = nodesToCheck.filter( ( el ) => !nodesWithEdges.includes( el ) );
    
    nodes = nodes.filter( ( el ) => !nodesToRemove.includes( el ) );
    
    if (nodes.length < 2) {
        return;
    }
    
    nodes = nodes.filter(function (n) {         	
    	return n.id != d.id;
    });
 
}


function getInfo(subject) {
	$('#infoDiv').remove();
	d3.json("getSubject/" + subject.replace(/#/g,"%23")).then(function (data) {
		var infoDiv = d3.select("body")
			.append("div")
			.attr("id", "infoDiv");
		var infoUl = infoDiv.append("ul");
		infoUl.selectAll("li").data(data).enter()
			.append("li")
			.html(function(d){
				return '<p style="color: black;font-weight: bold;">' 
					+ d.predicate + ':</p>' + '<p style="color: cornsilk;">' 
					+ d.object + '</p>'
				});
		infoDiv
			.append("button")
			.attr("id", "infoExitBtn")
			.attr("type", "button")
			.attr("class", "close btn-sm")
			.attr("aria-label", "Close")
			.on("click", function(){
				$('#infoDiv').remove();
				})
			.append("span")
			.attr("class","glyphicon glyphicon-remove")
	    console.log(data);
	  });
}

function ticked() {
 
    link.attr("x1", function (d) { return d.source.x; })
        .attr("y1", function (d) { return d.source.y; })
        .attr("x2", function (d) { return d.target.x; })
        .attr("y2", function (d) { return d.target.y; });

    edgepaths.attr('d', function (d) {
        return 'M ' + d.source.x + ' ' + d.source.y + ' L ' + d.target.x + ' ' + d.target.y;
    });

    edgelabels.attr('transform', function (d) {
        if (d.target.x < d.source.x) {
            var bbox = this.getBBox();

            rx = bbox.x + bbox.width / 2;
            ry = bbox.y + bbox.height / 2;
            return 'rotate(180 ' + rx + ' ' + ry + ')';
        }
        else {
            return 'rotate(0)';
        }
    });
    
    node
    .attr("transform", function (d) { return "translate(" + d.x + ", " + d.y + ")"; });
}

function dragstarted(d) {
    if (!d3.event.active) simulation
        .alphaTarget(0.3)
        .restart();
    d.fx = d.x;
    d.fy = d.y;
}

function dragged(d) {
    d.fx = d3.event.x;
    d.fy = d3.event.y;
}

function updateData(data, currNode) {
    var n = data.nodes;
    var l = data.edges;
    
    var newNodes = false;
   
    console.log(n.length);
    console.log(n);
    
    if(n.length >= edgeLimit) {
    	currNode.edgeOffset = currNode.edgeOffset + 1; 
    } else {
    	if(nodes.length > 0) {
    		currNode.edgeOffset = 0;
    	}
    }
    
    if(n.length > 0) {
    	
    	nodes = nodes.filter(function (n) {
    		return n.num === 0 || (pos - n.num !== edgeLevel && n.sourceNode !== currNode.id);
    		
//            return n.num === 0 || (pos - n.num !== edgeLevel);
            
            
        });
    	links = links.filter(function (l){
    		
//    		var stay = false;
//    		if((l.source.num === 0 || pos - l.source.num !== edgeLevel) && (l.target.num === 0 || pos - l.target.num !== edgeLevel)) {
//    			stay = true;
//    		}
//    		if((l.source.num !== 0 && l.source.sourceNode === currNode.id) || (l.target.num !== 0 && l.target.sourceNode === currNode.id)) {
//    			stay = false;
//    		}
    		
    		return ((l.source.num === 0 || pos - l.source.num !== edgeLevel) 
    					&& (l.target.num === 0 || pos - l.target.num !== edgeLevel))
    			&& !((l.source.num !== 0 && l.source.sourceNode === currNode.id) 
    					|| (l.target.num !== 0 && l.target.sourceNode === currNode.id));
    	});
    	
      node = node.data(nodes, function (d) { return d.id; });
      node.exit().remove();
    }
    

    
    n.forEach(element => {
    	element.num = pos;
        if (!nodeExists(element.id)) {
        	newNodes = true;
            nodes.push(element);
        } else {
        }
    });
    l.forEach(e => {
        if (!linkExists(e.source, e.target, e.edge)) {
            links.push(e);
        } else {
        }
    });
    if (data.nodes.length > 0 && newNodes) {
    	pos++;
    }
}

function linkExists(source, target, link) {
    for (var i = 0; i < links.length; i++) {
        if (links[i].source.id === source
            && links[i].target.id === target
            && links[i].edge === link) {
            return true;
        }
    }
    return false;
}

function nodeExists(id) {
    for (var i = 0; i < nodes.length; i++) {
        if (nodes[i].id === id) {
            return true;
        }
    }
    return false;
}