/* Cachable global scripts */

// ###################### Log #################################

function log(message) {
    return; // Remove to enable logging
    if (!log.window_ || log.window_.closed) {
        var win = window.open("", null, "width=400,height=200," +
                                        "scrollbars=yes,resizable=yes,status=no," +
                                        "location=no,menubar=no,toolbar=no");
        if (!win) return;
        var doc = win.document;
        doc.write("<html><head><title>Debug Log</title></head><body style='font-family: monospace'></body></html>");
        doc.close();
        log.window_ = win;
    }
    var logLine = log.window_.document.createElement("div");
    logLine.appendChild(log.window_.document.createTextNode(message));
    log.window_.document.body.appendChild(logLine);
}

// ###################### Seam Remoting #################################

Seam.Remoting.displayLoadingMessage = function() {};
Seam.Remoting.hideLoadingMessage = function() {};

// ###################### jQuery Integration #################################

jQuery.noConflict(); // Avoid conflicts with the RichFaces/Prototype library

function jsf(id) {
    // Find the dynamic JSF client identifier by looking up
    // the static identifier of its j4j proxy child element
    if (document.getElementById(id) == null) { alert("Couldn't find JSF element: " + id); }
    var realId = document.getElementById(id).title;
    var element = document.getElementById(realId);
    return jQuery(element);
}

// ###################### Form helpers #################################

function onAjaxRequestComplete() {
    resetSessionTimeoutCheck();
    wrapBoxes();
}

function selectCheckBoxes(styleClass) {
    jQuery("."+styleClass).attr("checked", "true");
}
function deselectCheckBoxes(styleClass) {
    jQuery("."+styleClass).removeAttr("checked");
}

function clickClear(thisfield, defaulttext) {
    if (thisfield.value == defaulttext) {
        thisfield.value = "";
    }
}
function clickRecall(thisfield, defaulttext) {
    if (thisfield.value == '') {
        thisfield.value = defaulttext;
    }
}

function trimString(s) {
    return s.replace(/(^\s+|\s+$)/g, "");
}

function stringEndsWith(s, suffix) {
    return s.substring(s.length - suffix.length) == suffix;
}

function formatText(textArea, formatString) {
    var inlinePlaceholder = "{i}";
    var blockPlaceholder = "{b}";
    var inline = formatString.indexOf(inlinePlaceholder) != -1;
    var block = formatString.indexOf(blockPlaceholder) != -1;
    if (!(inline || block)) return;
    var prefix = formatString.substring(0, formatString.indexOf(inline ? inlinePlaceholder : blockPlaceholder));
    var suffix = formatString.substring(formatString.indexOf(inline ? inlinePlaceholder : blockPlaceholder)+3, formatString.length);
    if (block) {
        prefix = "\n" + prefix;
        suffix = suffix + "\n";
    }

    if (typeof(textArea.caretPos) != "undefined" && textArea.createTextRange) {
        var caretPos = textArea.caretPos;
        caretPos.text = caretPos.text.charAt(caretPos.text.length - 1) == ' ' ? prefix + caretPos.text + suffix + ' ' : prefix + caretPos.text + suffix;
        caretPos.select();
    } else if (typeof(textArea.selectionStart) != "undefined") {
        var begin = textArea.value.substr(0, textArea.selectionStart);
        var selection = textArea.value.substr(textArea.selectionStart, textArea.selectionEnd - textArea.selectionStart);
        var end = textArea.value.substr(textArea.selectionEnd);
        var newCursorPos = textArea.selectionStart;
        var scrollPos = textArea.scrollTop;
        textArea.value = begin + prefix + selection + suffix + end;
        if (textArea.setSelectionRange) {
            if (selection.length == 0)
                textArea.setSelectionRange(newCursorPos + prefix.length, newCursorPos + prefix.length);
            else
                textArea.setSelectionRange(newCursorPos, newCursorPos + prefix.length + selection.length + suffix.length);
            textArea.focus();
        }
        textArea.scrollTop = scrollPos;
    } else {
        textArea.value += prefix + suffix;
        textArea.focus(textArea.value.length - 1);
    }
}

