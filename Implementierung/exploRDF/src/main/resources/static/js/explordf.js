
$("#postBtn").click(function(){
	var txt = $("#uname").val();
	
	alert(txt);
	
	var s = $.ajax({
		
		url: "writePredicates",
		type: 'POST',
		contentType: "text/plain",
		data: txt,
		success: function(){
			alert("funktioniert");
		}
	});
});

/*
 * Functions concerning the navbar.
 * ---------Begin-----------------
 */

// Function to toggle between opening and closing the navbar.
function tog() {
  if($("#buttonSymbol").hasClass("glyphicon-chevron-left")) {
    closeNav();
  } else {
    openNav();
  }
  $("#buttonSymbol").toggleClass("glyphicon-chevron-left glyphicon-menu-hamburger");
};

// Open the navbar
function openNav() {
  document.getElementById("mainSidebar").style.width = "160px";
  document.getElementById("sidebar").style.width = "200px";
  document.getElementById("main").style.marginLeft = "200px";
}

// Close the navbar
function closeNav() {
  document.getElementById("mainSidebar").style.width = "0";
  document.getElementById("sidebar").style.width = "40px";
  document.getElementById("main").style.marginLeft = "40px";
} 

$("#navQuery").click(function(){
	
});

/*
 * Functions concerning the navbar.
 * ---------End-------------------
 */

/*--------------------------------------------------------------------------------------------------------------------------------------------------------------*/

/*
 * Functions concerning the visualisation.
 * -----------Begin----------------------
 */

var nodes2 = [{s:'Tami', p:'liiert', o:'Andi'}];
var nodes = [{s:'Tami', p:'liiert', o:'Andi'},
             {s:'Tami', p:'kindVon', o:'Carolin'},
             {s:'Luni', p:'istHausTierVon', o:'Tami'}];

 var mainSvg = d3.select('#main-svg');

var container = mainSvg.selectAll('.node')
.data(nodes, function(d) {return d.s;})
.enter()
.append('g')
.attr('id', function(d) {return d.s;})
.attr('class', 'node')
.attr('transform','translate(100,100)');

container.append('svg:circle')
.attr('class', 'circle')
.attr('r', 20)
// .attr('fill', 'red');

container.append('svg:text')
.text(function(d) {return d.s;})
.attr("font-family", "sans-serif")
.attr("font-size", "15px")
.attr('font-weight', 'bolder')
.attr("text-anchor", "middle")

var dragger = d3.drag()
.on('drag', function(d){
  d3.select(this)
  .attr('transform', 'translate(' + (d3.event.x) + ',' + (d3.event.y) + ')');
});

dragger(d3.selectAll('.node'));

$('.node').hover(function(){
  $(this).css('cursor','pointer');
});

$('.node').dblclick(function(){
  alert('hallo');
});

// var drag_handler = d3.drag()
//     .on("drag", function(d) {
//          d3.select(this)
//            .attr("cx", d3.event.x  )
//            .attr("cy", d3.event.y  );
//            });
   
//    drag_handler(d3.selectAll(container));
/*
 * Functions concerning the visualisation.
 * -----------End----------------------
 */