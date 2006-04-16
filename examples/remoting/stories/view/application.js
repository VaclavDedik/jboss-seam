function remoteId(element) {
  match = false;
  if(element.id) {
    match = element.id.match(/^[^_]*_(.*)$/)[1];
  }
  return  match ? match : remoteId(element.parentNode);
}

// Callback functions for displaying during AJAX=>Server activity
Ajax.Responders.register({
  onCreate: function() {
    if (Ajax.activeRequestCount > 0)
      new Effect.Appear('spinner', {duration:0.2,queue:'parallel'});
      new Effect.Appear('message', {duration:0.2,queue:'parallel'});
  },
  onComplete: function() {
    if (Ajax.activeRequestCount == 0)
      new Effect.Fade('message', {duration:2, afterFinish: function(){$('message').innerHTML = ''}} );
      new Effect.Fade('spinner', {duration:3});
  }
});