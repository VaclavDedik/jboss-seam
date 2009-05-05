$(document).ready(function() {
	printContexts();
	$('#editTaskSubmit').click(function() {
		var name = $('#editTaskName').val();
		var contextName = $('#editTaskContext').val()
		postTask(contextName, name, function(location) {
			$('#editTaskName').val('');
			$.get(location, function(data) {
				addTask($(data).find('task'), contextName);
			});
		});
	});
});

function showTaskEditForm(contextName, taskId) {
	var contexts = $('#editTaskContext').clone();
	$(contexts).find('[value=' + contextName + ']').attr('selected', 'selected');
	var taskName = $('#' + taskId + " .name").text();
	var name = $('<input/>').attr('type', 'text').addClass('nameField').val(taskName);
	
	var update = $('<input/>').attr('type', 'button').attr('id', 'update').val('Update').click(function() {
		var newContextName = $(contexts).val();
		var callback = function(data) {
			removeTaskEditForm(taskId, taskName);
				if (contextName == newContextName) {
					updateTaskNameOnUI(taskId, data); // just update the name
				} else {
					$('#' + taskId).remove(); // add the task into new context
					addTask(data, newContextName);
				}
		}
		putTask(newContextName, taskId, $(name).val(), false, callback);
	});
	var form = $('<form/>').attr('id', 'updateTask').append(contexts).append(name).append(update);
	$('#' + taskId + ' .name').replaceWith(form);
}

function removeTaskEditForm(taskId, taskName) {
	$('#' + taskId + ' form').replaceWith($('<span/>').addClass('name').text(taskName));
}

function printContexts() {
	getContexts(function(data) {
 	   $(data).find('context').each(function() {
 		   addContext($(this));
 	   });
	});
}

function addContext(context) {
	var contextName = $(context).find('name').text();
	var escapedContextName = escape(contextName);
	var contextCell = $('<td/>').attr('colspan', '2').addClass('name').text(contextName);
	var contextRow = $('<tr/>').attr('id', contextName).append(contextCell);
	$('#contexts tbody').append(contextRow).appendTo('#contexts');
	$('<option/>').attr('value', contextName).text(contextName).appendTo('#editTaskContext');
	getTasksForContext(contextName, false, function(data) {
		$(data).find('task').each(function() {
			addTask($(this), contextName);
	 	});
	});
}

function addTask(task, contextName) {
	var taskId = $(task).find('id').text();
	var taskName = $(task).find('name').text();
	
	var parent = $('[id=' + contextName + ']');
	var nameCell = $('<td/>').append($('<span/>').addClass('name').text(taskName));
	var doneButton = $('<img/>').attr('src', 'img/task-done.png').attr('title', 'Resolve this task').click(function(event) {
		event.preventDefault();
		putTask(contextName, taskId, taskName, true, function() {
			$('#' + taskId).remove();
		});
	});
	var editButton = $('<img/>').attr('src', 'img/task-edit.png').attr('title', 'Edit this task').click(function(event) {
		event.preventDefault();
		if ($('#' + taskId + ' #updateTask').size() == 0) {
			showTaskEditForm(contextName, taskId);
		} else {
			removeTaskEditForm(taskId, taskName);
		}
	});
	var deleteButton = $('<img/>').attr('src', 'img/task-delete.png').attr('title', 'Delete this task').click(function(event) {
		event.preventDefault();
		deleteTask(contextName, taskId, false, function() {
			$('#' + taskId).remove();
		});
	});
	var buttonCell = $('<td/>').append(doneButton).append(editButton).append(deleteButton);
	$('<tr/>').attr('id', taskId).append(buttonCell).append(nameCell).insertAfter('[id=' + contextName + ']');
}

function updateTaskNameOnUI(taskId, task) {
	var taskName = $(task).find('name').text();
	$('#' + taskId + " .name").text(taskName);
}
