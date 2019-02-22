/**
 * ****************************************************************
 * --------------Functions concerning triple store connections-----
 * ---------------------------START--------------------------------
 */

// Gets current connection properties. 
$(document).ready(function() {
  getConnProps();
  getSupportedServers();
});

// Enables to send connection credentials via pushing the ENTER key.
$('#connectForm').keypress(function(event){
  if(event.key != "Enter") return;
  setConnection();
});

// Sending connection credentials via pushing the connect button.
$("#connectBtn").on("click", function(){
	setConnection();
});

// Set new connection properties defined in the connection form.
function setConnection() {
	if($("#tripleStoreUrl").val() === ""){
		$("#invalidURLMsg").html("Please enter a valid Triple Store URL.");
		return;
	}
	var connForm = {};
	
	$("#connectForm :input").each(function(x,y){
		connForm[y.id] = $(y).val();
	});
	
	$("body").css("cursor", "progress");
	
	$.ajax({
		url:'connect',
		type:'POST',
		xhrFields: {
            withCredentials:true
        },
        contentType: 'application/json; charset=utf-8',
		data:JSON.stringify(connForm),
        success:function(data){
//        	console.log(data);
        	$('#connectDiv').css('display', 'none');
        	if(data != null && data.tripleStoreUrl != null) {
        		getConnProps();
        		$('#connectSuccess').css('display', 'block');
        	}
        	else {
        		$('#connectFail').css('display', 'block'); 		
        		$('#connectFailSpan').html(connForm.tripleStoreUrl + ' ' 
        				+ connForm.tripleStoreRepo);
        	}
        	$("body").css("cursor", "default");
        },
        error:function() {
            console.log("fail");
            $("body").css("cursor", "default");
        }
	});
};

// Button to continue on if connection to the defined database failed.
$("#connectionFailBtn").on("click", function(){
	$('#connectFail').css('display', 'none');
	$('#connectDiv').css('display', 'block');
});


// Gets all customized predicate lists.
function getGraphVisualizationTypes() {
	$('#visualizationTypeGroup').empty();
	var visTypes;
	d3.json("getAllPredicatesLists").then(function(data){
		visTypes = data;

		var select = d3.select('#visualizationTypeGroup');
		var options = select.selectAll('option')
			.data(visTypes).enter()
			.append('option')
			.text(function(d) {return d});		
	});
	
}


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

// Get all supported Servers as select options 
function getSupportedServers() {
	var servers;
	d3.json("getSupportedServers").then(function(data){
		servers = data;

		var select = d3.select('#tripleStoreServer');
		var options = select.selectAll('option')
			.data(servers).enter()
			.append('option')
			.text(function(d) {return d});		
	});
	
}

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

  d3.json("getSubject/" + subj.replace(/#/g,"%23")).then(function (data) {
    choiceResult = data;
    drawChoiceTable(choiceResult, subj);
    $("body").css("cursor", "default");
  });
  getGraphVisualizationTypes();  
});

// Drawing a table with all information about the chosen element.
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

// Searches for a certain term by pressing the ENTER key.
$('#searchInput').keypress(function(event){
  if(event.key != "Enter") return;
  searchTerm();
});

//Searches for a certain term by pressing the search button.
$('#searchBtn').on('click', function(){
  searchTerm();
});

// Function for searching for a certain term.
function searchTerm() {
  var term = $('#searchTerm').val();
  
  if(term === '.') {
	  term = "";
  }
  
  $("body").css("cursor", "progress");
  $('#headingResult').text('Results for "' + term + '"');
  
  var broaderSearch = $("#broaderSearchRadio").is(":checked") ? "1" : "0";
  
  searchingTerm = term.replace(/#/g,"%23").replace(/%/g, "%25").replace(/\?/g, "%3F")
  					  .replace(/\|/g,"%7C").replace(/\[/g,"%5B").replace(/\]/g,"%5D")
  					  .replace(/\+/g, "%2B");
  
  d3.json("simpleSearch/" + searchingTerm + "/" + broaderSearch)
  		.then(function (data) {
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

//Drawing a table with all result triples with the entered search term.
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

// Button to proceed to predicates list customization.
$("#newVisualizationBtn").on('click', function () {
	$("body").css("cursor", "progress");
	$('#choiceDiv').css("display", "none");
	  $('#predicatesDiv').css('display', 'block');
	  $('#headingChoice').css('display', 'none');
	  $('#headingPredicates').css('display', 'block');
	  
	  var selectedOpt = $("#visualizationTypeGroup option:selected" ).text();
	  
	  if(selectedOpt !== null && selectedOpt !== '') {
		  $('#predicatesInput').val(selectedOpt);
		  getPredicatesList(selectedOpt);
	  } else {
		  getPredicates();
	  }	 
});

// Calls for all different predicates from the current database.
function getPredicates() {
	d3.json("getPredicates").then(function (data) {
	    predicates = data;
	    drawPredicatesTable(predicates);
	    $("body").css("cursor", "default");
	  });
}

// Gets the customized predicate list.
function getPredicatesList(listName) {
	d3.json("getPredicates/" + listName).then(function (data) {
	    predicates = data;
	    drawPredicatesTable(predicates);
	    $("body").css("cursor", "default");
	  });
}

// Drawing a table with all predicates
function drawPredicatesTable(data) {
  var tbody = d3.select('#predicatesTbody');

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

// Button for requesting a new predicate list.
$('#loadNewPredicatesBtn').on('click', function(){
	$('#predicatesTbody').empty();
	$('#predicatesInput').val("");
	$("body").css("cursor", "progress");
	getPredicates();
});

// Keeps only one checkbox checked at the same time. 
$('#predicatesTable').on('click', 'input.checkboxLabel', function () {
  $('.checkboxLabel').not(this).prop('checked', false);
});

//Save predicates list with the selected properties
$('#savePredicatesBtn').on('click', function(){
	
	var listName = $('#predicatesInput').val();
	if(!validateName(listName)){
		$('#invalidPredicatesListName').html('Please enter a valid predicate type name. Without \ / : * ? " < > |');
		return;
	}
	
	var exists = false;
	$("#visualizationTypeGroup option").each(function(){
		if($(this).text() === listName) {
			exists = true;
		}
	});
	console.log(exists);
	var answer;
	if(exists) {
		answer = confirm("A predicate list with that name already exists. " 
				+ "Do you want to overwrite it?");
		if(!answer) {
			return;
		}
	}
	
	console.log("listName: " + listName);
	var predicates = [];
	
	$('#predicatesTbody tr').each(function(index, element){
		
		var p = $(this).find('td:eq(0)').text();
		var l = $(this).find('td:eq(1)').find('input').is(':checked');
		var e = $(this).find('td:eq(2)').find('input').is(':checked');

		var predicate = {};
		predicate['predicate'] = p;
		predicate['label'] = l;
		predicate['edge'] = e;

		predicates.push(predicate);

	});
	
	$.ajax({
		url:'savePredicatesList/' + listName,
		type:'POST',
		xhrFields: {
            withCredentials:true
        },
        contentType: 'application/json; charset=utf-8',
		data: JSON.stringify(predicates),
        success:function(listName){
        	console.log(listName);
        	turnBack();
        	
        },
        error:function() {
            console.log("fail");
            alert("Failed to save the predicates.");
        }
	});
});

// Function to validate the entered predicate list.
function validateName(name) {
	if(name == null || $.trim(name) === '') {
		return false;
	} else {
		return /^[A-Za-z0-9]+$/.test(name);
	}
}

//Back button for the search divs.
$('#searchBackBtn').on('click', function() {
	turnBack();
});

//Showing the search divs depending on the current visible table div.
function turnBack() {
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
	      $('#invalidPredicatesListName').empty();
	      getGraphVisualizationTypes();
	      break;
	    default:
	      console.log("Something went wrong!");
	  }
	  $("body").css("cursor", "default");
}
/**
   * --------------------------------------------------------------
   * ----------------Functions concerning the tables---------------
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