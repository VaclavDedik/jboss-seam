function getContexts(callback) {
	$.get("seam/resource/v1/auth/context", callback);
}

function putContext(contextName, callback) {
	$.ajax({
		type: "PUT",
		url: "seam/resource/v1/auth/context/" + contextName,
		contentType: "application/xml",
		success: callback
	});
}

function deleteContext(contextName, callback) {
	$.ajax({
		type: "DELETE",
		url: "seam/resource/v1/auth/context/" + contextName,
		success: callback
	});
}

function getTask(contextName, taskId, taskDone, callback) {
	var URI = escape("seam/resource/v1/auth/context/" + contextName + (taskDone ? "/resolved" : "/unresolved"));
	$.get(URI, callback);
}

function getTasksForContext(contextName, taskDone, callback) {
	var URI = escape("seam/resource/v1/auth/context/" + contextName + (taskDone ? "/resolved" : "/unresolved"));
	$.get(URI, function(data) {
		callback(data);
	});
}

function getResolvedTasks(username, start, show, callback) {
	var URI = escape("seam/resource/v1/user/" + username + "/tasks/resolved.xml");
	URI += "?start=" + start + "&show=" + show;
	$.get(URI, function(data) {
		callback(data);
	});
}

function postTask(contextName, taskName, callback) {
	var URI = escape("seam/resource/v1/auth/context/" + contextName + "/unresolved");
	var data = '<task><name>' + taskName + '</name></task>';
	var request = $.ajax({
		type: "POST",
		url: URI,
		contentType: "application/xml",
		dataType: "application/xml",
		data: data,
		success: function() {
			callback(request.getResponseHeader('Location'));
		}
	});
}

function putTask(contextName, taskId, taskName, taskDone, callback) {
	var URI = escape("seam/resource/v1/auth/context/" + contextName + (taskDone ? "/resolved/" : "/unresolved/") + taskId);
	var data = '<task><id>' + taskId + '</id><name>' + taskName + '</name></task>'
	$.ajax({
		type: "PUT",
		url: URI,
		contentType: "application/xml",
		data: data,
		success: function() {
			callback(data);
		}
	});
}

function deleteTask(contextName, taskId, taskDone, callback) {
	var URI = escape("seam/resource/v1/auth/context/" + contextName + (taskDone ? "/resolved/" : "/unresolved/") + taskId);
	$.ajax({
		type: "DELETE",
		url: URI,
		dataType: "application/xml",
		success: callback
	});
}