var webServices = {};

ServiceMetadata = function(name)
{
  this.name = name;
  this.parameters = {};

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



function selectService(serviceName)
{
  var svc = webServices[serviceName];
  
  if (!svc)
  {
    alert("Unknown service");
    return;
  }
  
  document.getElementById('selectedService').innerHTML = svc.getDescription();
  document.getElementById('serviceRequest').value = svc.getRequest();
}

function sendRequest()
{
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