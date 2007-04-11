var webServices = new Object();
var groups = new Object();

ServiceParam = function(name, key)
{
  this.name = name;
  this.key = key; 
  this.value = "#{" + key + "}";
}

ServiceMetadata = function(name, group)
{
  this.name = name;
  this.group = group;
  this.parameters = new Array();

  webServices[name] = this;

  ServiceMetadata.prototype.setDescription = function(description) { this.description = description; };  
  ServiceMetadata.prototype.getDescription = function() { return this.description; };
  ServiceMetadata.prototype.addParameter = function(param) { this.parameters.push(param); };
  ServiceMetadata.prototype.setRequest = function(request) { this.request = request; };
  ServiceMetadata.prototype.getRequest = function() { return this.request; };
}

// start of web service definitions

var svc = new ServiceMetadata("listCategories", "General");
svc.setDescription("List Categories");
svc.setRequest("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
               "\n    xmlns:seam=\"http://seambay.example.seam.jboss.org/\">\n  <soapenv:Header/>" +
               "\n  <soapenv:Body>\n    <seam:listCategories/>\n  </soapenv:Body>\n</soapenv:Envelope>");

svc = new ServiceMetadata("listAuctions", "General");
svc.setDescription("List Auctions");

svc = new ServiceMetadata("login", "Security");
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

svc = new ServiceMetadata("logout", "Security");
svc.setDescription("Logout");
svc.setRequest("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
               "\n    xmlns:seam=\"http://seambay.example.seam.jboss.org/\">\n  <soapenv:Header/>" +
               "\n  <soapenv:Body>" +
               "\n    <seam:logout/>" +
               "\n  </soapenv:Body>" +
               "</soapenv:Envelope>");  

svc = new ServiceMetadata("createAuction", "Create/Update Auction");
svc.setDescription("Create new auction");
svc.setRequest("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
               "\n    xmlns:seam=\"http://seambay.example.seam.jboss.org/\">\n  <soapenv:Header/>" +
               "\n  <soapenv:Body>" +
               "\n    <seam:createAuction>" +
               "\n      <arg0>#{title}</arg0>" +
               "\n      <arg1>#{description}</arg1>" +
               "\n      <arg2>#{categoryId}</arg2>" +
               "\n    </seam:createAuction>" +
               "\n  </soapenv:Body>" +
               "\n</soapenv:Envelope>");
svc.addParameter(new ServiceParam("Auction title", "title"));
svc.addParameter(new ServiceParam("Description", "description"));
svc.addParameter(new ServiceParam("Category ID", "categoryId"));         

svc = new ServiceMetadata("updateAuction", "Create/Update Auction");
svc.setDescription("Update an existing auction");
svc.setRequest("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
               "\n    xmlns:seam=\"http://seambay.example.seam.jboss.org/\">\n  <soapenv:Header/>" +
               "\n  <soapenv:Body>" +
               "\n    <seam:updateAuction>" +
               "\n      <arg0>#{auctionId}</arg0>" +
               "\n      <arg1>#{title}</arg1>" +
               "\n      <arg2>#{description}</arg2>" +
               "\n      <arg3>#{categoryId}</arg3>" +
               "\n    </seam:updateAuction>" +
               "\n  </soapenv:Body>" +
               "\n</soapenv:Envelope>");
svc.addParameter(new ServiceParam("Auction ID", "auctionId"));
svc.addParameter(new ServiceParam("Auction title", "title"));
svc.addParameter(new ServiceParam("Description", "description"));
svc.addParameter(new ServiceParam("Category ID", "categoryId"));      

// end of web service definitions


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

    if (!groups[ws.group])
    {
      groups[ws.group] = document.createElement("div");
      var groupTitle = document.createElement("span");
      groupTitle.appendChild(document.createTextNode(ws.group));
      groups[ws.group].appendChild(groupTitle);
      document.getElementById("services").appendChild(groups[ws.group]); 
    }
    
    groups[ws.group].appendChild(anchor);    
  }
}

initServices();