var planning = {
  totalPoints: 0,

  recalcTotalPoints: function() {
    planning.totalPoints = 0
    $$('.Story .Points INPUT').each(function(input) {
      planning.totalPoints += new Number(input.value)
    })
    $('total_points').innerHTML = planning.totalPoints
  },

  rules: {
    '.Backlog' : function(element) {
      Sortable.create(element, {
        constraint: '',
        dropOnEmpty: true,
        overlap: 'vertical',
        tag: 'DIV',
        onUpdate: function() {
          storyIds = $A($$('.Story')).collect(function(story) { return remoteId(story);});
          SeamRemote.create("stories").reorder(storyIds);
        }
      });
    },

    '.Story .Points INPUT' : function(element) {
      Event.observe(element, 'focus', function() {
        this.select()
      })
      Event.observe(element, 'change', function() {
        storyId = element.id;
        SeamRemote.create("stories").setPoints( storyId, this.value, function(response) { planning.recalcTotalPoints() } )
      });
    }
  },

  deleteStory: function(storyId) {
    story = $('story_' + storyId);
    new Effect.Highlight(story);
    SeamRemote.create("stories").deleteStory( storyId, function(result) { Effect.Puff(story); } );
  },
  
  createStory: function(project_id) {
    new Ajax.Request('story.seam?id=' + project_id, {
      asynchronous: true,
      method: 'post',
      onSuccess: function(response) {
        list = $('backlog')
        new Insertion.After($('backlog_header'), response.responseText)
        // list.firstChild might be a text node so do the more complex lookup!
        div = $A(list.getElementsByTagName('div')).first()
        new Effect.Appear(div, {duration:1.5, fps:50, queue:'end'})
        new Effect.Highlight(div)
        Behaviour.apply()
      }
    })
    $A($$('.List .Advice')).each(function(div) {
      new Effect.Puff(div) // get rid of the help text
    })
  },

  createChild: function(parent, storyId) {
    parent = $('story_' + storyId)
    new Effect.Highlight(parent);
    new Effect.Shake(parent)
    new Ajax.Request('/story/birth/' + storyId, {
      asynchronous:true,
      method: 'post',
      onSuccess: function(response) {
        Element.addClassName(parent, 'Parent');
        new Insertion.After(parent, response.responseText);
        new Effect.SlideDown(parent.nextSibling);
        Behaviour.apply();
      }
    });
  }

};

Behaviour.register(planning.rules);