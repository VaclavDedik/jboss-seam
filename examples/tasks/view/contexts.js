$(document).ready(function() {
	// load context list
	getContexts(function(data) {
		$(data).find('context').each(function() {
			var contextName = $(this).find('name').text();
			addContext(contextName);
	 	});
	});
	// create new context on submit
	$('#editContextSubmit').click(function(event) {
		event.preventDefault();
		var contextName = $('#editContextName').attr('value');
		putContext(contextName, function() {
			addContext(contextName);
			$('#editContextName').attr('value', '');
		});
	});
});

function addContext(contextName) {
	var nameCell = $('<td/>').addClass('name').text(contextName);
	var deleteButton = $('<img/>').attr('src', 'img/task-delete.png').attr('title', 'Delete this context').click(function(event) {
		event.preventDefault();
		deleteContext(contextName, function() {
			$('#' + contextName).remove();
		});
	});
	var buttonCell = $('<td/>').append(deleteButton);
	$('<tr/>').attr('id', contextName).append(buttonCell).append(nameCell).appendTo('#contexts tbody');
}