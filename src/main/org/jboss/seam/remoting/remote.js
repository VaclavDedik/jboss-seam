function SeamRemote() { }

SeamRemote.PATH_EXECUTE = "/execute";
SeamRemote.PATH_SUBSCRIPTION = "/subscription";
SeamRemote.PATH_POLL = "/poll";

SeamRemote.types = new Array();
SeamRemote.debug = false;
SeamRemote.debugWindow = null;

SeamRemote.setDebug = function(val)
{
  SeamRemote.debug = val;
}

SeamRemote.log = function(msg)
{
  if (!SeamRemote.debug)
    return;

  if (!SeamRemote.debugWindow || SeamRemote.debugWindow.document == null)
  {
    var attr = "left=400,top=400,resizable=yes,scrollbars=yes,width=400,height=400";
    SeamRemote.debugWindow = window.open("", "__seamDebugWindow", attr);
    if (SeamRemote.debugWindow)
    {
      SeamRemote.debugWindow.document.write("<html><head><title>Seam Debug Window</title></head><body></body></html>");
      var bodyTag = SeamRemote.debugWindow.document.getElementsByTagName("body").item(0);
      bodyTag.style.fontFamily = "arial";
      bodyTag.style.fontSize = "8pt";
    }
  }

  if (SeamRemote.debugWindow)
  {
    msg = msg.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
    SeamRemote.debugWindow.document.write("<pre>" + (new Date()) + ": " + msg + "</pre><br/>");
  }
}

SeamRemote.__Context = function() {
  this.conversationId = null;

  SeamRemote.__Context.prototype.setConversationId = function(conversationId)
  {
    this.conversationId = conversationId;
  }

  SeamRemote.__Context.prototype.getConversationId = function()
  {
    return this.conversationId;
  }
}

SeamRemote.context = new SeamRemote.__Context();

SeamRemote.getContext = function()
{
  return SeamRemote.context;
}

SeamRemote.__Map = function()
{
  this.elements = new Array();

  SeamRemote.__Map.prototype.size = function()
  {
    return elements.length;
  }

  SeamRemote.__Map.prototype.isEmpty = function()
  {
    return elements.length == 0;
  }

  SeamRemote.__Map.prototype.keySet = function()
  {
    var keySet = new Array();
    for (var i = 0; i < this.elements.length; i++)
      keySet[keySet.length] = this.elements[i].key;
    return keySet;
  }

  SeamRemote.__Map.prototype.values = function()
  {
    var values = new Array();
    for (var i = 0; i < this.elements.length; i++)
      values[values.length] = this.elements[i].value;
    return values;
  }

  SeamRemote.__Map.prototype.get = function(key)
  {
    for (var i = 0; i < this.elements.length; i++)
    {
      if (this.elements[i].key == key)
        return this.elements[i].value;
    }
    return null;
  }

  SeamRemote.__Map.prototype.put = function(key, value)
  {
    for (var i = 0; i < this.elements.length; i++)
    {
      if (this.elements[i].key == key)
      {
        this.elements[i].value = value;
        return;
      }
    }
    this.elements[this.elements.length] = {key:key,value:value};
  }

  SeamRemote.__Map.prototype.remove = function(key)
  {
    var found = false;

    for (var i = 0; i < this.elements.length; i++)
    {
      if (!found && this.elements[i].key == key)
        found = true;

      if (found && i < this.elements.length)
        this.elements[i] = this.elements[i + 1];
    }
    if (found)
      this.elements.length = this.elements.length - 1;
  }

  SeamRemote.__Map.prototype.contains = function(key)
  {
    for (var i = 0; i < this.elements.length; i++)
    {
      if (this.elements[i].key == key)
        return true;
    }
    return false;
  }
}

SeamRemote.register = function(type)
{
  for (var i = 0; i < SeamRemote.types.length; i++)
  {
    if (SeamRemote.types[i].__name == type.__name)
    {
      SeamRemote.types[i] = type;
      return;
    }
  }
  SeamRemote.types[SeamRemote.types.length] = type;
}

SeamRemote.create = function(name)
{
  if (name == "Map")
    return new SeamRemote.__Map();

  for (var i = 0; i < SeamRemote.types.length; i++)
  {
    if (SeamRemote.types[i].__name == name)
      return new SeamRemote.types[i];
  }
}

SeamRemote.getType = function(obj)
{
  for (var i = 0; i < SeamRemote.types.length; i++)
  {
    if (obj instanceof SeamRemote.types[i])
      return SeamRemote.types[i];
  }
}

SeamRemote.getTypeName = function(obj)
{
  var type = SeamRemote.getType(obj);
  return type ? type.__name : null;
}

SeamRemote.getMetadata = function(obj)
{
  for (var i = 0; i < SeamRemote.types.length; i++)
  {
    if (obj instanceof SeamRemote.types[i])
    {
      return SeamRemote.types[i].__metadata;
    }
  }
}

SeamRemote.serializeContext = function(obj)
{
  var ctx = obj.__context;

  var data = "<context>";

  if (ctx.getConversationId())
  {
    data += "<conversationId>";
    data += ctx.getConversationId();
    data += "</conversationId>";
  }

  data += "</context>";

  return data;
}

SeamRemote.serializeValue = function(value, type, refs)
{
  if (value == null)
    return "<null/>";
  else if (type)
  {
    switch (type) {
      // Boolean
      case "bool": return "<bool>" + (value ? "true" : "false") + "</bool>";

      // Numerical types
      case "int": return "<int>" + value + "</int>";
      case "long": return "<long>" + value + "</long>";
      case "single": return "<single>" + value + "</single>";
      case "double": return "<double>" + value + "</double>";

      // Date
      case "date": return SeamRemote.serializeDate(value);
      // Beans
      case "bean": return SeamRemote.getTypeRef(value, refs);

      // Collections
      case "bag": return SeamRemote.serializeBag(value, refs);
      case "map": return SeamRemote.serializeMap(value, refs);

      default: return "<str>" + value + "</str>";
    }
  }
  else // We don't know the type.. try to guess
  {
    if (value instanceof SeamRemote.__Map)
      return SeamRemote.serializeMap(value, refs);

    switch (typeof(value)) {
      case "number": return "<int>" + value + "</int>";
      case "boolean": return "<bool>" + (value ? "true" : "false") + "</bool>";
      case "object":
        if (value.constructor == Array)
          return SeamRemote.serializeBag(value, refs);
        else
          return SeamRemote.getTypeRef(value, refs);
      default: return "<str>" + value + "</str>"; // Default to String
    }
  }
}

SeamRemote.serializeBag = function(value, refs)
{
  var data = "<bag>";

  for (var i = 0; i < value.length; i++)
  {
    data += "<element>";
    data += SeamRemote.serializeValue(value[i], null, refs);
    data += "</element>";
  }

  data += "</bag>";
  return data;
}

SeamRemote.serializeMap = function(value, refs)
{
  var data = "<map>";

  var keyset = value.keySet();
  for (var i = 0; i < keyset.length; i++)
  {
    data += "<element><k>";
    data += SeamRemote.serializeValue(keyset[i], null, refs);
    data += "</k><v>";
    data += SeamRemote.serializeValue(value.get(keyset[i]), null, refs);
    data += "</v></element>";
  }

  data += "</map>";
  return data;
}

SeamRemote.serializeDate = function(value)
{
  var zeroPad = function(val, digits) { while (("" + val).length < digits) val = "0" + val; };

  var data = "<date>";
  data += value.getFullYear();
  data += zeroPad(value.getMonth() + 1, 2);
  data += zeroPad(value.getDate(), 2);
  data += zeroPad(value.getHours(), 2);
  data += zeroPad(value.getMinutes(), 2);
  data += zeroPad(value.getSeconds(), 2);
  data += zeroPad(value.getMilliseconds(), 3);
  data += "</date>";
  return data;
}

SeamRemote.getTypeRef = function(obj, refs)
{
  var refId = -1;

  for (var i = 0; i < refs.length; i++)
  {
    if (refs[i] == obj)
    {
      refId = i;
      break;
    }
  }

  if (refId == -1)
  {
    refId = refs.length;
    refs[refId] = obj;
  }

  return "<ref id=\"" + refId + "\"/>";
}

SeamRemote.serializeType = function(obj, refs)
{
  var data = "<bean type=\"";
  data += SeamRemote.getType(obj).__name;
  data += "\">\n";

  var meta = SeamRemote.getMetadata(obj);
  for (var i = 0; i < meta.length; i++)
  {
    data += "<member name=\"";
    data += meta[i].field;
    data += "\">";
    data += SeamRemote.serializeValue(obj[meta[i].field], meta[i].type, refs);
    data += "</member>\n";
  }

  data += "</bean>";

  return data;
}

SeamRemote.__callId = 0;

SeamRemote.createCall = function(obj, methodName, params, callback)
{
  var callId = "" + SeamRemote.__callId++;
  if (!callback)
    callback = obj.__callback[methodName];

  var data = "<call component=\"";
  data += SeamRemote.getType(obj).__name;
  data += "\" method=\"";
  data += methodName;
  data += "\" id=\"";
  data += callId;
  data += "\">\n";

  // Add parameters
  data += "<params>";

  var refs = new Array();

  for (var i = 0; i < params.length; i++)
  {
    data += "<param>";
    data += SeamRemote.serializeValue(params[i], null, refs);
    data += "</param>";
  }

  data += "</params>";

  // Add refs
  data += "<refs>";
  for (var i = 0; i < refs.length; i++)
  {
    data += "<ref id=\"" + i + "\">";
    data += SeamRemote.serializeType(refs[i], refs);
    data += "</ref>";
  }
  data += "</refs>";

  data += "</call>";

  return {data: data, id: callId, callback: callback};
}

SeamRemote.createHeader = function()
{
  var header = "";

  header += "<context>";
  if (SeamRemote.getContext().getConversationId())
  {
    header += "<conversationId>";
    header += SeamRemote.getContext().getConversationId();
    header += "</conversationId>";
  }
  header += "</context>";

  return header;
}

SeamRemote.createEnvelope = function(header, body)
{
  var data = "<envelope>";

  if (header)
  {
    data += "<header>";
    data += header;
    data += "</header>";
  }

  if (body)
  {
    data += "<body>";
    data += body;
    data += "</body>";
  }

  data += "</envelope>";

  return data;
}

SeamRemote.pendingCalls = new SeamRemote.__Map();
SeamRemote.inBatch = false;
SeamRemote.batchedCalls = new Array();

SeamRemote.startBatch = function()
{
  SeamRemote.inBatch = true;
  SeamRemote.batchedCalls.length = 0;
}

SeamRemote.executeBatch = function()
{
  if (!SeamRemote.inBatch)
    return;

  var data = "";
  for (var i = 0; i < SeamRemote.batchedCalls.length; i++)
  {
    SeamRemote.pendingCalls.put(SeamRemote.batchedCalls[i].id, SeamRemote.batchedCalls[i]);
    data += SeamRemote.batchedCalls[i].data;
  }

  var envelope = SeamRemote.createEnvelope(SeamRemote.createHeader(), data);
  SeamRemote.sendAjaxRequest(envelope, SeamRemote.PATH_EXECUTE, SeamRemote.processResponse, false);
  SeamRemote.inBatch = false;
}

SeamRemote.cancelBatch = function()
{
  SeamRemote.inBatch = false;
  // Todo - unregister the callbacks for the calls in the batch
}

SeamRemote.execute = function(component, methodName, params, callback)
{
  var call = SeamRemote.createCall(component, methodName, params, callback);

  if (SeamRemote.inBatch)
  {
    SeamRemote.batchedCalls[SeamRemote.batchedCalls.length] = call;
  }
  else
  {
    // Marshal the request
    var envelope = SeamRemote.createEnvelope(SeamRemote.createHeader(), call.data);
    SeamRemote.pendingCalls.put(call.id, call);
    SeamRemote.sendAjaxRequest(envelope, SeamRemote.PATH_EXECUTE, SeamRemote.processResponse, false);
  }
}

SeamRemote.sendAjaxRequest = function(envelope, path, callback, silent)
{
  SeamRemote.log("Request packet:\n" + envelope);

  if (!silent)
    SeamRemote.displayLoadingMessage();

  var asyncReq;

  if (window.XMLHttpRequest)
  {
    asyncReq = new XMLHttpRequest();
    if (asyncReq.overrideMimeType)
      asyncReq.overrideMimeType('text/xml');
  }
  else
    asyncReq = new ActiveXObject("Microsoft.XMLHTTP");

  asyncReq.onreadystatechange = function() {SeamRemote.requestCallback(asyncReq, callback); }
  asyncReq.open("POST", SeamRemote.contextPath + "/seam/remoting" + path, true);
  asyncReq.send(envelope);
}

SeamRemote.setCallback = function(component, methodName, callback)
{
  component.__callback[methodName] = callback;
}

SeamRemote.requestCallback = function(req, callback)
{
  if (req.readyState == 4)
  {
    SeamRemote.hideLoadingMessage();

    if (req.status == 200)
    {
      SeamRemote.log("Response packet:\n" + req.responseText);

      if (callback)
        callback(req.responseXML);
    }
    else
      alert("There was an error processing your request.  Error code: " + req.status);
  }
}

SeamRemote.processResponse = function(doc)
{
  var headerNode;
  var bodyNode;
  var context = new SeamRemote.__Context;

  for (var i = 0; i < doc.documentElement.childNodes.length; i++)
  {
    var node = doc.documentElement.childNodes.item(i);
    if (node.tagName == "header")
      headerNode = node;
    else if (node.tagName == "body")
      bodyNode = node;
  }

  if (headerNode)
  {
    var contextNode;
    for (var i = 0; i < headerNode.childNodes.length; i++)
    {
      var node = headerNode.childNodes.item(i);
      if (node.tagName == "context")
      {
        contextNode = node;
        break;
      }
    }
    if (contextNode)
      SeamRemote.unmarshalContext(contextNode, context);
  }

  if (bodyNode)
  {
    for (var i = 0; i < bodyNode.childNodes.length; i++)
    {
      var node = bodyNode.childNodes.item(i);
      if (node.tagName == "result")
      SeamRemote.processResult(node, context);
    }
  }
}

SeamRemote.processResult = function(result, context)
{
  var callId = result.getAttribute("id");
  var call = SeamRemote.pendingCalls.get(callId);
  SeamRemote.pendingCalls.remove(callId);

  if (call && call.callback)
  {
    var valueNode = null;
    var refsNode = null;

    var children = result.childNodes;
    for (var i = 0; i < children.length; i++)
    {
      var tag = children.item(i).tagName;
      if (tag == "value")
        valueNode = children.item(i);
      else if (tag == "refs")
        refsNode = children.item(i);
    }

    var refs = new Array();
    if (refsNode)
      SeamRemote.unmarshalRefs(refsNode, refs);

    var value = SeamRemote.unmarshalValue(valueNode.firstChild, refs);

    call.callback(value, context);
  }
}

SeamRemote.unmarshalContext = function(ctxNode, context)
{
  for (var i = 0; i < ctxNode.childNodes.length; i++)
  {
    var tag = ctxNode.childNodes.item(i).tagName;
    if (tag == "conversationId")
      context.setConversationId(ctxNode.childNodes.item(i).firstChild.nodeValue);
  }
}

SeamRemote.unmarshalRefs = function(refsNode, refs)
{
  var objs = new Array();

  // Pass 1 - create the reference objects
  for (var i = 0; i < refsNode.childNodes.length; i++)
  {
    if (refsNode.childNodes.item(i).tagName == "ref")
    {
      var refNode = refsNode.childNodes.item(i);
      var refId = parseInt(refNode.getAttribute("id"));

      var valueNode = refNode.firstChild;
      if (valueNode.tagName == "bean")
      {
        var obj = SeamRemote.create(valueNode.getAttribute("type"));
        if (obj)
        {
          refs[refId] = obj;
          objs[objs.length] = {obj: obj, node: valueNode};
        }
      }
    }
  }

  // Pass 2 - populate the object members
  for (var i = 0; i < objs.length; i++)
  {
    for (var j = 0; j < objs[i].node.childNodes.length; j++)
    {
      var child = objs[i].node.childNodes.item(j);
      if (child.tagName == "member")
      {
        var name = child.getAttribute("name");
        objs[i].obj[name] = SeamRemote.unmarshalValue(child.firstChild, refs);
      }
    }
  }
}

SeamRemote.unmarshalValue = function(element, refs)
{
  var tag = element.tagName;

  switch (tag)
  {
    case "bool": return element.firstChild.nodeValue == "true";
    case "int": return parseInt(element.firstChild.nodeValue);
    case "str": return element.firstChild ? element.firstChild.nodeValue : "";
    case "ref": return refs[parseInt(element.getAttribute("id"))];
    case "bag":
      var value = new Array();
      for (var i = 0; i < element.childNodes.length; i++)
      {
        if (element.childNodes.item(i).tagName == "element")
          value[value.length] = SeamRemote.unmarshalValue(element.childNodes.item(i).firstChild, refs);
      }
      return value;
    case "map":
      var map = new SeamRemote.__Map();
      for (var i = 0; i < element.childNodes.length; i++)
      {
        var childNode = element.childNodes.item(i);
        if (childNode.tagName == "element")
        {
          var key = null
          var value = null;

          for (var j = 0; j < childNode.childNodes.length; j++)
          {
            if (key == null && childNode.childNodes.item(j).tagName == "k")
              key = SeamRemote.unmarshalValue(childNode.childNodes.item(j).firstChild, refs);
            else if (value == null && childNode.childNodes.item(j).tagName == "v")
              value = SeamRemote.unmarshalValue(childNode.childNodes.item(j).firstChild, refs);
          }

          if (key != null)
            map.put(key, value);
        }
      }
      return map;
    case "date": return SeamRemote.deserializeDate(element.firstChild.nodeValue);
    default: return null;
  }
}

SeamRemote.deserializeDate = function(val)
{
  var dte = new Date();
  dte.setFullYear(parseInt(val.substring(0,4), 10));
  dte.setMonth(parseInt(val.substring(4,6), 10) - 1);
  dte.setDate(parseInt(val.substring(6,8), 10));
  dte.setHours(parseInt(val.substring(8,10), 10));
  dte.setMinutes(parseInt(val.substring(10,12), 10));
  dte.setSeconds(parseInt(val.substring(12,14), 10));
  dte.setMilliseconds(parseInt(val.substring(14,17), 10));
  return dte;
}

SeamRemote.loadingMsgDiv = null;

SeamRemote.displayLoadingMessage = function(message)
{
  var loadingMessage = "Please wait...";

  if (!SeamRemote.loadingMsgDiv)
  {
    SeamRemote.loadingMsgDiv = document.createElement('div');
    var msgDiv = SeamRemote.loadingMsgDiv;
    msgDiv.setAttribute('id', 'loadingMsg');

    msgDiv.style.position = "absolute";
    msgDiv.style.top = "0px";
    msgDiv.style.right = "0px";
    msgDiv.style.background = "red";
    msgDiv.style.color = "white";
    msgDiv.style.fontFamily = "Verdana,Helvetica,Arial";
    msgDiv.style.fontSize = "small";
    msgDiv.style.padding = "2px";
    msgDiv.style.border = "1px solid black";

    document.body.appendChild(msgDiv);

    var text = document.createTextNode(loadingMessage);
    msgDiv.appendChild(text);
  }
  else
  {
    SeamRemote.loadingMsgDiv.innerHTML = loadingMessage;
    SeamRemote.loadingMsgDiv.style.visibility = 'visible';
  }
}

SeamRemote.hideLoadingMessage = function()
{
  SeamRemote.loadingMsgDiv.style.visibility = 'hidden';
}

/* Messaging API */

SeamRemote.pollInterval = 10; // Default poll interval of 10 seconds
SeamRemote.pollTimeout = 0; // Default timeout of 0 seconds
SeamRemote.polling = false;

SeamRemote.setPollInterval = function(interval)
{
  SeamRemote.pollInterval = interval;
}

SeamRemote.setPollTimeout = function(timeout)
{
  SeamRemote.pollTimeout = timeout;
}

SeamRemote.subscriptionRegistry = new Array();

SeamRemote.subscribe = function(topicName, callback)
{
  for (var i = 0; i < SeamRemote.subscriptionRegistry.length; i++)
  {
    if (SeamRemote.subscriptionRegistry[i].topic == topicName)
      return;
  }

  var body = "<subscribe topic=\"" + topicName + "\"/>";
  var env = SeamRemote.createEnvelope(null, body);
  SeamRemote.subscriptionRegistry.push({topic:topicName, callback:callback});
  SeamRemote.sendAjaxRequest(env, SeamRemote.PATH_SUBSCRIPTION, SeamRemote.subscriptionCallback, false);
}

SeamRemote.unsubscribe = function(topicName)
{
  var token = null;

  var tokenFound = false;

  for (var i = 0; i < SeamRemote.subscriptionRegistry.length; i++)
  {
    if (!tokenFound && SeamRemote.subscriptionRegistry[i].topic == topicName)
    {
      token = SeamRemote.subscriptionRegistry[i].token;
      tokenFound = true;
    }

    if (tokenFound && i < SeamRemote.subscriptionRegistry.length - 1)
      SeamRemote.subscriptionRegistry[i] = SeamRemote.subscriptionRegistry[i + 1];
  }

  if (tokenFound)
  {
    SeamRemote.subscriptionRegistry.length = SeamRemote.subscriptionRegistry.length - 1;
    var body = "<unsubscribe token=\"" + token + "\"/>";
    var env = SeamRemote.createEnvelope(null, body);
    SeamRemote.sendAjaxRequest(env, SeamRemote.PATH_SUBSCRIPTION, null, false);
  }
}

SeamRemote.subscriptionCallback = function(doc)
{
  var body = doc.documentElement.firstChild;
  for (var i = 0; i < body.childNodes.length; i++)
  {
    var node = body.childNodes.item(i);
    if (node.tagName == "subscription")
    {
      var topic = node.getAttribute("topic");
      var token = node.getAttribute("token");
      for (var i = 0; i < SeamRemote.subscriptionRegistry.length; i++)
      {
        if (SeamRemote.subscriptionRegistry[i].topic == topic)
        {
          SeamRemote.subscriptionRegistry[i].token = token;
          SeamRemote.poll();
          break;
        }
      }
    }
  }
}

SeamRemote.pollTimeoutFunction = null;

SeamRemote.poll = function()
{
  if (SeamRemote.polling)
    return;

  SeamRemote.polling = true;
  clearTimeout(SeamRemote.pollTimeoutFunction);

  var body = "";

  if (SeamRemote.subscriptionRegistry.length == 0)
  {
    SeamRemote.polling = false;
    return;
  }

  for (var i = 0; i < SeamRemote.subscriptionRegistry.length; i++)
  {
    body += "<poll token=\"" + SeamRemote.subscriptionRegistry[i].token + "\" ";
    body += "timeout=\"" + SeamRemote.pollTimeout + "\"/>";
  }

  var env = SeamRemote.createEnvelope(null, body);
  SeamRemote.sendAjaxRequest(env, SeamRemote.PATH_POLL, SeamRemote.pollCallback, true);
}

SeamRemote.pollCallback = function(doc)
{
  SeamRemote.polling = false;

  var body = doc.documentElement.firstChild;
  for (var i = 0; i < body.childNodes.length; i++)
  {
    var node = body.childNodes.item(i);
    if (node.tagName == "messages")
      SeamRemote.processMessages(node);
  }

  SeamRemote.pollTimeoutFunction = setTimeout("SeamRemote.poll()", Math.max(SeamRemote.pollInterval * 1000, 1000));
}

SeamRemote.processMessages = function(messages)
{
  var token = messages.getAttribute("token");

  var callback = null;
  for (var i = 0; i < SeamRemote.subscriptionRegistry.length; i++)
  {
    if (SeamRemote.subscriptionRegistry[i].token == token)
    {
      callback = SeamRemote.subscriptionRegistry[i].callback;
      break;
    }
  }

  if (callback != null)
  {
    var messageNode = null;

    var children = messages.childNodes;
    for (var i = 0; i < children.length; i++)
    {
      if (children.item(i).tagName == "message")
      {
        messageNode = children.item(i);
        var messageType = messageNode.getAttribute("type");

        var valueNode = null;
        var refsNode = null;
        for (var j = 0; j < messageNode.childNodes.length; j++)
        {
          var node = messageNode.childNodes.item(j);
          if (node.tagName == "value")
            valueNode = node;
          else if (node.tagName == "refs")
            refsNode = node;
        }

        var refs = new Array();
        if (refsNode)
          SeamRemote.unmarshalRefs(refsNode, refs);

        var value = SeamRemote.unmarshalValue(valueNode.firstChild, refs);

        callback(SeamRemote.createMessage(messageType, value));
      }
    }
  }
}

SeamRemote.__ObjectMessage = function()
{
  this.value = null;

  SeamRemote.__ObjectMessage.prototype.getValue = function()
  {
    return this.value;
  }

  SeamRemote.__ObjectMessage.prototype.setValue = function(value)
  {
    this.value = value;
  }
}

SeamRemote.__TextMessage = function()
{
  this.text = null;

  SeamRemote.__TextMessage.prototype.getText = function()
  {
    return this.text;
  }

  SeamRemote.__TextMessage.prototype.setText = function(text)
  {
    this.text = text;
  }
}

SeamRemote.createMessage = function(messageType, value)
{

  switch (messageType)
  {
    case "object":
      var msg = new SeamRemote.__ObjectMessage();
      msg.setValue(value);
      return msg;
    case "text":
      var msg = new SeamRemote.__TextMessage();
      msg.setText(value);
      return msg;
  }
  return null;
}
