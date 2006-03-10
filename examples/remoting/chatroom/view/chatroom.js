// Returns a reference to an object by its id
function getObject(objectId) {
  if (document.getElementById && document.getElementById(objectId))
    return document.getElementById(objectId);
  else if (document.all && document.all(objectId))
    return document.all(objectId);
  else if (document.layers && document.layers[objectId])
    return document.layers[objectId];
  else
    return false;
}

// Uncomment the following line to enable the debug window
//SeamRemote.setDebug(true);

// Try adjusting the following values
SeamRemote.setPollTimeout(5);   // 3 seconds
SeamRemote.setPollInterval(3); // 10 seconds

var username = null;
var chatJMSTopic = null;
var chatroom = SeamRemote.create("chatroomAction");

function connect() {
  var nameCtl = getObject("username");
  username = nameCtl.value;

  var connectCallback = function(connected, context) {
    setInterfaceState(connected);
    getObject("username").value = username;
    SeamRemote.getContext().setConversationId(context.getConversationId());
  };

  var chatTopicCallback = function(topicName) {
    chatJMSTopic = topicName;
    SeamRemote.subscribe(topicName, channelMessageCallback);
  };

  var listUsersCallback = function(users) {
    for (var i = 0; i < users.length; i++)
      addUser(users[i]);
  };

  SeamRemote.startBatch();
  chatroom.connect(username, connectCallback);
  chatroom.getChatTopicName(chatTopicCallback);
  chatroom.listUsers(listUsersCallback);
  SeamRemote.executeBatch();
}

function disconnect() {
  SeamRemote.unsubscribe(chatJMSTopic);
  setInterfaceState(false);
  chatroom.disconnect();
  getObject("userList").options.length = 0;
}

function channelMessageCallback(message) {
  var ctl = getObject("channelDisplay");

  var actionDTO = message.getValue();

  if (actionDTO.action == "message")
    ctl.innerHTML += "<span style='font-weight:bold" + (actionDTO.getUser() == username ? ";color:green" : "") + "'>" + actionDTO.getUser() + "></span> " + actionDTO.getData() + "<br/>";
  else if (actionDTO.action == "connect")
  {
    addUser(actionDTO.getUser());
    ctl.innerHTML += "<span style='font-weight:bold;color:red'>" + actionDTO.getUser() + " connected.</span><br/>";
  }
  else if (actionDTO.action == "disconnect")
  {
    removeUser(actionDTO.getUser());
    ctl.innerHTML += "<span style='font-weight:bold;color:red'>" + actionDTO.getUser() + " disconnected.</span><br/>";
  }

  ctl.scrollTop = ctl.scrollHeight;
}

function addUser(user) {
  var ctl = getObject("userList");
  var found = false;

  for (var i = 0; i < ctl.options.length; i++) {
    if (ctl.options[i].value == user)
    {
      found = true;
      break;
    }
  }

  if (!found)
    ctl.options[ctl.options.length] = new Option(user, user);
}

function removeUser(user) {
  var ctl = getObject("userList");

  for (var i = 0; i < ctl.options.length; i++) {
    if (ctl.options[i].value == user)
      ctl.options[i] = null;
  }
}

function setInterfaceState(connected) {
  getObject("username").readOnly = connected;
  getObject("btnConnect").disabled = connected;
  getObject("btnDisconnect").disabled = !connected;
}

function sendMessage() {
  var ctl = getObject("messageText");
  chatroom.sendMessage(ctl.value);
  ctl.value = "";
  // Force a poll so that we see our new message straight away
  SeamRemote.poll();
}

function checkEnterPressed(e) {
  if ((window.event && window.event.keyCode == 13) ||
      (e && e.which == 13))
  {
    sendMessage();

    if (navigator.userAgent.indexOf("MSIE") != -1)
    {
      window.event.cancelBubble = true;
      window.event.returnValue = false;
    }
    else
      e.preventDefault();
  }
}
