RapMock = function() {

  this.fakeRemoteObject = {
    set : function(){},
    notify : function(){},
    call : function(){}
  };

  this.fakeComposite = {
    append : function( node ){
      document.createElement( "div" ).appendChild( node );
    },
    addListener : function(){},
    removeListener : function(){},
    getClientArea : function(){ return [ 0, 0, 0, 0 ]; }
  };

};

RapMock.prototype = {

  on: function() {},

  off: function() {},

  registerTypeHandler : function() {},

  getObject : function() {
    return this.fakeComposite;
  },

  getRemoteObject : function() {
    return this.fakeRemoteObject;
  }

};

rap = new RapMock();