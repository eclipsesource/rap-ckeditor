(function(){
  'use strict';

  var createStubs = function( object, names ) {
    var fun = function(){ return false; };
    for( var i = 0; i < names.length; i++ ) {
      object[ names[ i ] ] = fun;
    }
  };

  window.CKEDITOR = {
    editor : function(){},
    appendTo : function( element ) {
      return new CKEDITOR.editor( element );
    }
  };

  createStubs(
    CKEDITOR.editor.prototype,
    [ "on", "resize", "setData", "getData", "checkDirty", "resetDirty", "destroy" ]
  );

  CKEDITOR.editor.prototype.document = {
    "getBody" : function() {
      return this.body;
    },
    "body" : {
      setStyle : function(){}
    }
  };

}());