var webServices = new Object();

ServiceParam = function(name, key)
{
  this.name = name;
  this.key = key; 
  this.value = "#{" + key + "}";
}

ServiceMetadata = function(name)
{
  this.name = name;
  this.parameters = new Array();

  webServices[name] = this;

  ServiceMetadata.prototype.setDescription = function(description) { this.description = description; };  
  ServiceMetadata.prototype.getDescription = function() { return this.description; };
  ServiceMetadata.prototype.addParameter = function(param) { this.parameters.push(param); };
  ServiceMetadata.prototype.setRequest = function(request) { this.request = request; };
  ServiceMetadata.prototype.getRequest = function() { return this.request; };
}

var svc = new ServiceMetadata("listCategories");
svc.setDescription("List Categories");
svc.setRequest("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
               "\n    xmlns:seam=\"http://seambay.example.seam.jboss.org/\">\n  <soapenv:Header/>" +
               "\n  <soapenv:Body>\n    <seam:listCategories/>\n  </soapenv:Body>\n</soapenv:Envelope>");

svc = new ServiceMetadata("listAuctions");
svc.setDescription("List Auctions");

svc = new ServiceMetadata("login");
svc.setDescription("Login");
svc.setRequest("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
               "\n    xmlns:seam=\"http://seambay.example.seam.jboss.org/\">\n  <soapenv:Header/>" +
               "\n  <soapenv:Body>" +
               "\n    <seam:login>" +
               "\n      <arg0>#{username}</arg0>" +
               "\n      <arg1>#{password}</arg1>" +
               "\n    </seam:login>" +
               "\n  </soapenv:Body>" +
               "</soapenv:Envelope>");
svc.addParameter(new ServiceParam("Username", "username"));
svc.addParameter(new ServiceParam("Password", "password"));      

var selectedService = null;         

function setAllParams()
{
  if (!selectedService)
    return;
  
  var request = selectedService.request;  
  
  for (var i = 0; i < selectedService.parameters.length; i++)
  {
    var param = selectedService.parameters[i];
    var search = "#{" + param.key + "}";
    
    request = request.replace(search, param.value);
  } 
  
  document.getElementById("serviceRequest").value = request;  
}

function setParamValue(event)
{
  var ctl = null
  if (event.target)
    ctl = event.target;
  else if (window.event.srcElement)
    ctl = window.event.srcElement;
    
  var key = ctl.id;
  
  for (var i = 0; i < selectedService.parameters.length; i++)
  {
    var param = selectedService.parameters[i];
    if (param.key == key)
    {
      param.value = ctl.value;
      break;
    }
  } 
  
  setAllParams();
}

function selectService(serviceName)
{
  var svc = webServices[serviceName];
  
  if (!svc)
  {
    alert("Unknown service");
    return;
  }
  
  selectedService = svc;
  
  document.getElementById("selectedService").innerHTML = svc.getDescription();
 // document.getElementById("serviceRequest").value = svc.getRequest();
  document.getElementById("serviceResponse").value = null;
  
  var ctl = document.getElementById("parameters");
  for (var i = ctl.childNodes.length - 1; i >= 0; i--)
  {
     ctl.removeChild(ctl.childNodes[i]);
  }

  var tbl = document.createElement("table");
  tbl.cellspacing = 0;
  tbl.cellpadding = 0;
  
  ctl.appendChild(tbl);
    
  for (var i = 0; i < svc.parameters.length; i++)
  {
     var row = tbl.insertRow(-1);
     
     var td = document.createElement("td");
     row.appendChild(td);
     td.innerHTML = svc.parameters[i].name;
          
     var inp = document.createElement("input");

     inp.id = svc.parameters[i].key;
     inp.value = svc.parameters[i].value;
     inp.onchange = setParamValue;
     inp.onkeyup = setParamValue;
     
     td = document.createElement("td");
     row.appendChild(td);
     td.appendChild(inp);
  }
  
  setAllParams();
}

function sendRequest()
{
  document.getElementById("serviceResponse").value = null;
  
  var req;
  if (window.XMLHttpRequest)
  {
    req = new XMLHttpRequest();
    if (req.overrideMimeType)
      req.overrideMimeType("text/xml");
  }
  else
    req = new ActiveXObject("Microsoft.XMLHTTP");
    
  req.onreadystatechange = function() { receiveResponse(req); };
  req.open("POST", "/seam-bay-seam-bay/AuctionService", true);
  req.setRequestHeader("Content-type", "text/xml");
  req.send(document.getElementById("serviceRequest").value);
}

function receiveResponse(req)
{
  if (req.readyState == 4)
  {
    if (req.responseText)
      document.getElementById("serviceResponse").value = req.responseText;
      
    if (req.status != 200)
    {
      alert("There was an error processing your request.  Error code: " + req.status);      
    }
  }  
}

function initServices()
{
  for (var i in webServices)
  {    
    var ws = webServices[i];
    
    var anchor = document.createElement("a");
    anchor.href = "javascript:selectService('" + ws.name + "')";  
    anchor.appendChild(document.createTextNode(ws.getDescription()));
    document.getElementById("services").appendChild(anchor); 
  }
}

initServices();