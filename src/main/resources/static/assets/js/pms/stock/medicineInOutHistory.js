document.title = "Pharmacy Management System || Stock In Out History";


$(document).ready(function() {
	$('#overlay').fadeOut();
	
	
	$('#stockTable').DataTable({
		dom : "'<'col-sm-4'l><'col-sm-4 text-center'><'col-sm-4'>Bfrtip",
		buttons : [ 'copyHtml5', 'excelHtml5', 'csvHtml5', 'pdfHtml5' ],
		aaSorting: [ [0, 'asc'] ]
	});
	
	

});

