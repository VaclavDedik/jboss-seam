var inPlaceOptions = function(element) {
  var opts = {
    highlightcolor: "#f7fef1",
    highlightendcolor: "#ffffff",
    okButton: true,
    cancelLink: true
  }
  if (Element.hasClassName(element, 'Body')) {
    opts.rows = 2;
  }
  return opts;
}

var addEditor = function(element, action) {
      if (element.editor) return; // prevent multiple behavior attachment
      element.editor = new SeamRemote.InPlaceEditor(element, action, inPlaceOptions(element));
  }

