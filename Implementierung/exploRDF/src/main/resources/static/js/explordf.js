var predicates;
var columns = ["predicate", "label", "edge"];

$('#btnFunClick').one('click', function(){

d3.json("getPredicates").then(function(data){
  predicates = data;
  console.log(predicates);
  tabulate(predicates)
});

function tabulate(data){
  //create table in body
  var table = d3.selectAll('#table1')
    .append('table')
    .attr('id', 'table')
    .attr('class', 'table table-bordered table-striped"');
//create table head row and append headers
var thead = table.append('thead').append('tr');
thead.selectAll('th')
    .data(d3.keys(data[0]))
    .enter()
    .append('th')
    .text(function(d){return d});

//create table body
var tbody = table.append('tbody');

//create table body rows
var rows = tbody.selectAll('tr')
  .data(data).enter()
  .append('tr')
  .attr('class', 'trbody');

//create cells in each table body row
$('.trbody').each(function(index, element){
  $(this).append('<td>' + element.__data__.predicate + '</td>');

  var s = '<td><input class="checkboxLabel" type="checkbox" ';
  var t = element.__data__.label ? 'checked':'';
  var q = '></td>';
  console.log(element.__data__.label);

  $(this).append(s+t+q);
  
  var s2 = '<td><input type="checkbox" ';
  var t2 = element.__data__.edge ? 'checked':'';
  var q2 = '></td>';
  
  $(this).append(s2+t2+q2);

  
});

}



});


// Keeps only one checkbox checked at the same time. 
$('#table1').on('click','input.checkboxLabel', function(){
  $('.checkboxLabel').not(this).prop('checked', false);
}); 


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

//Function to toggle between opening and closing the navbar.
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
  $("#mainSidebar").css("width", "160px");
  $("#sidebar").css("width", "200px");
  $("#main").css("margin-left", "200px");
}

// Close the navbar
function closeNav() {
  $("#mainSidebar").css("width", "0px");
  $("#sidebar").css("width", "40px");
  $("#main").css("margin-left", "40px");
} 
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