document.title = "Pharmacy Management System || Sales List";


$(document).ready(function() {
	$('#overlay').fadeOut();
	
	
	$('#salesTable').DataTable({
		dom : "'<'col-sm-4'l><'col-sm-4 text-center'><'col-sm-4'>Bfrtip",
		buttons : [ 'copyHtml5', 'excelHtml5', 'csvHtml5', 'pdfHtml5' ],
		aaSorting: [ [0, 'desc'] ]
	});
	
	var url = new URL(window.location.href);
	var range = url.searchParams.get("range");
	var startDate, endDate;
	if(range==null){
		startDate =moment().subtract(7, 'days');
		endDate =moment();
	}else{
		startDate =moment(range.split("_")[0]);
		endDate =moment(range.split("_")[1]);
	}


	$('#pmsDateRangePicker').daterangepicker(
		{
			opens : 'left',
			startDate : startDate,
			endDate : endDate,
			locale : { format : 'YYYY/MM/DD' }
		},
		function(start, end, label) {
			var startDate = start.format('YYYY-MM-DD');
			var endDate = end.format('YYYY-MM-DD');
			console.log("A new date selection was made: " + startDate + ' to ' + endDate);
			var range = "?range=" + startDate + '_' + endDate;
			window.location.replace(window.location.href .split("?")[0]+ range);
		});

});


