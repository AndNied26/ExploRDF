/**
 * ****************************************************************
 * --------------Functions concerning triple store connections-----
 * ---------------------------START--------------------------------
 */

$("#connectBtn").on("click", function(){
	if($("#tripleStoreUrl").val() == ""){
		$("#invalidURLMsg").html("Please enter a valid Triple Store URL.");
		return;
	}
	var connForm = {};
	
	$("#connectForm :input").each(function(x,y){
		connForm[y.id] = $(y).val();
	});
	
	
	$.ajax({
		url:'connect',
		type:'POST',
		xhrFields: {
            withCredentials:true
        },
        contentType: 'application/json; charset=utf-8',
		data:JSON.stringify(connForm),
        success:function(data){
        	console.log(data);
        	$('#connectDiv').css('display', 'none');
        	if(data != null && data.tripleStoreUrl != null) {
        		getConnProps();
        		
        		$('#connectSuccess').css('display', 'block');
        	}
        	else {
        		$('#connectFail').css('display', 'block');
        		
        		
        		$('#connectFailSpan').html(connForm.tripleStoreUrl + ' ' + connForm.tripleStoreRepo);
        	}
        },
        error:function() {
            console.log("fail");
        }
	});

});

$("#connectionFailBtn").on("click", function(){
	$('#connectFail').css('display', 'none');
	$('#connectDiv').css('display', 'block');
});

/**
   * --------------------------------------------------------------
   * ------------Functions concerning triple store connections-----
   * --------------------------END---------------------------------
   * **************************************************************
   */


/**
 * ***************************************************************
 * ----------------Functions concerning the tables----------------
 * --------------------------START--------------------------------
 */

//Data about all results of the queried term.
var searchResults;
var searchResultsHeadings = ["Subject", "Predicate", "Object"];

//Data about a certain chosen result.
var choiceResult;

//Data concerning the predicate table.
var predicates;
var predicateHeadings = ["Predicate", "Label", "Edge"];


// Choosing one element from the result table.
$('#resultTbody').on("click", "a", function (e) {
  var subj = $(e.target).text();
  console.log(subj);
  $("body").css("cursor", "progress");

  d3.json("getSubject/" + subj).then(function (data) {
    choiceResult = data;
    drawChoiceTable(choiceResult, subj);
    $("body").css("cursor", "default");
  });
  
//  d3.json("getSubject", {method: 'post', body: subj}).then(function (data) {
//	    choiceResult = data;
//	    drawChoiceTable(choiceResult, subj);
//	    $("body").css("cursor", "default");
//	  });
  
});

// Drawing a table of all information about the choosen element.
function drawChoiceTable(data, subj) {
  var tbody = d3.select('#choiceTbody');
  $('#resultDiv').css("display", "none");
  $('#choiceDiv').css('display', 'block');
  $('#headingResult').css('display', 'none');
  $('#headingChoice').text(subj);
  $('#headingChoice').css('display', 'block');
  tbody.selectAll('tr')
    .data(data).enter()
    .append('tr')
    .attr('class', 'choiceTrbody');
  $('.choiceTrbody').each(function (index, element) {
    $(this).append('<td>' + element.__data__.subject + '</td>');
    $(this).append('<td>' + element.__data__.predicate + '</td>');
    $(this).append('<td>' + element.__data__.object + '</td>');
  });
}

// Simple search: Searching for a certain term.
$('#searchInput').keypress(function(event){
  if(event.key != "Enter") return;
  searchTerm();
});

$('#searchBtn').on('click', function(){
  searchTerm();
});

//Function for searching for a certain term.
function searchTerm() {
  var term = $('#searchTerm').val();
  $("body").css("cursor", "progress");
  $('#headingResult').text('Results for "' + term + '"');
  
  console.log("hallo");
  var broaderSearch = $("#broaderSearchRadio").is(":checked") ? "1" : "0";
  
  d3.json("simpleSearch/" + term + "/" + broaderSearch).then(function (data) {
	  console.log("simpleSearch/" + term + "/" + broaderSearch);
	searchResults = data;
	var count = data.length;
	var res = count==1 ? 'Result':'Results';
	$('#headingResult').text(count + ' ' + res + ' for "' + term + '"');
    drawSearchTable(searchResults);
    $("body").css("cursor", "default");
  });
  $('#searchDiv').css("display", "none");
  $('#resultDiv').css("display", "block");
  $('#contentHeader').css("display", "grid");
};

//Drawing a table with all results of a query.
function drawSearchTable(data) {
  var tbody = d3.select('#resultTbody');
  tbody.selectAll('tr')
    .data(data).enter()
    .append('tr')
    .attr('class', 'resultTrBody');

  $('.resultTrBody').each(function (index, element) {
    $(this).append('<td><a class="subject">' + element.__data__.subject + '</a></td>');
    $(this).append('<td>' + element.__data__.predicate + '</td>');
    $(this).append('<td>' + element.__data__.object + '</td>');
  });
}

// Choosing which predicates to visualize.
$("#newVisualizationBtn").on('click', function () {
	$("body").css("cursor", "progress");
	$('#choiceDiv').css("display", "none");
	  $('#predicatesDiv').css('display', 'block');
	  $('#headingChoice').css('display', 'none');
	  $('#headingPredicates').css('display', 'block');
  d3.json("getPredicates").then(function (data) {
    predicates = data;
    drawPredicatesTable(predicates);
    $("body").css("cursor", "default");
  });
});

// Drawing a table with all predicates
function drawPredicatesTable(data) {
  var tbody = d3.select('#predicatesTbody');
//  $('#choiceDiv').css("display", "none");
//  $('#predicatesDiv').css('display', 'block');
//  $('#headingChoice').css('display', 'none');
//  $('#headingPredicates').css('display', 'block');
  tbody.selectAll('tr')
    .data(data).enter()
    .append('tr')
    .attr('class', 'predicatesTrbody');
  $('.predicatesTrbody').each(function (index, element) {
    $(this).append('<td>' + element.__data__.predicate + '</td>');

    var s = '<td><input class="checkboxTd checkboxLabel" type="checkbox" ';
    var t = element.__data__.label ? 'checked' : '';
    var q = '></td>';
    $(this).append(s + t + q);

    var s2 = '<td><input class="checkboxTd" type="checkbox" ';
    var t2 = element.__data__.edge ? 'checked' : '';
    var q2 = '></td>';
    $(this).append(s2 + t2 + q2);
  });
}

// Keeps only one checkbox checked at the same time. 
$('#predicatesTable').on('click', 'input.checkboxLabel', function () {
  $('.checkboxLabel').not(this).prop('checked', false);
});



// Back button depending on the current visible table div.
$('#searchBackBtn').on('click', function () {
  var visibleDiv = $(".containerDiv:visible").attr('id');
  console.log(visibleDiv);
  switch (visibleDiv) {
    case "resultDiv":
      $('#searchDiv').css("display", "block");
      $('#resultDiv').css("display", "none");
      $('#contentHeader').css("display", "none");
      $('#resultTbody').empty();
      break;
    case "choiceDiv":
      $('#resultDiv').css("display", "block");
      $('#choiceDiv').css("display", "none");
      $('#headingResult').css('display', 'block');
      $('#headingChoice').css('display', 'none');
      $('#choiceTbody').empty();
      break
    case "predicatesDiv":
      $('#choiceDiv').css("display", "block");
      $('#predicatesDiv').css("display", "none");
      $('#headingChoice').css('display', 'block');
      $('#headingPredicates').css('display', 'none');
      $('#predicatesTbody').empty();
      break;
    default:
      console.log("Something went wrong!");
  }
});
/**
   * --------------------------------------------------------------
   * ----------------Functions concerning the tables---------------
   * --------------------------END---------------------------------
   * **************************************************************
   */


/**
 ***************************************************************
 * ---------------Functions concerning the caption.--------------
 * -------------------------------------------------------------
 * --------------------------START------------------------------
 */
$(document).ready(function() {
  getConnProps();
});

//Get Connection properties
function getConnProps() {
	var connection;
	d3.json("getConnectionProps").then(function(data){
	    connection = data;
	    $("#dbSpan").text(connection.tripleStoreUrl);
	    if(connection.tripleStoreRepo != "") {
	    	$("#repoSpan").text(connection.tripleStoreRepo);
	    } else {
	    	$("#repoSpan").text("-");
	    }
	  });
}


/**
   * --------------------------------------------------------------
   * ----------------Functions concerning the caption---------------
   * --------------------------END---------------------------------
   * **************************************************************
   */

/**
 ***************************************************************
 * ---------------Functions concerning the navbar.--------------
 * -------------------------------------------------------------
 * --------------------------START------------------------------
 */
// Function to toggle between opening and closing the navbar.
$("#toggleButton").on("click", function () {
  if ($("#buttonSymbol").hasClass("glyphicon-chevron-left")) {
    closeNav();
  } else {
    openNav();
  }
  $("#buttonSymbol").toggleClass("glyphicon-chevron-left glyphicon-menu-hamburger");
});

// Open the navbar
function openNav() {
  $("#mainSidebar").css("width", "105px");
  $("#sidebar").css("width", "140px");
}

// Close the navbar
function closeNav() {
  $("#mainSidebar").css("width", "0px");
  $("#sidebar").css("width", "35px");
}
  /**
   * --------------------------------------------------------------
   * ----------------Functions concerning the navbar---------------
   * --------------------------END---------------------------------
   * **************************************************************
   */