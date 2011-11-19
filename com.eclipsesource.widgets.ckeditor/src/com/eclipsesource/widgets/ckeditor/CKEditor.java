/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package com.eclipsesource.widgets.ckeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.json.JSONArray;
import org.json.JSONException;



public class CKEditor extends Composite {

  private static final String URL = "/resources/ckeditor.html";
  private static final String READY_FUNCTION = "rap_ready";
  private String text = "";
  private StringBuilder onReadyScript = null;
  private StringBuilder onLoadScript = null;
  private Style[] knownStyles = new Style[ 0 ];
  private Style[] activeStyles;
  Browser browser;
  boolean clientLoaded = false;
  boolean clientReady = false;

  
  public CKEditor( Composite parent, int style ) {
    super( parent, style );
    super.setLayout( new FillLayout() );
    browser = new Browser( this, SWT.NONE );
    this.setBackgroundMode( SWT.INHERIT_FORCE );
    browser.setUrl( URL );
    addBrowserHandler();
    evalOnLoad( "rap.createEditor();" );
  }
  
  ////////////////////
  // overwrite methods

  @Override
  public void setLayout( Layout layout ) {
    throw new UnsupportedOperationException( "Cannot change internal layout of CkEditor" );
  }

//  TODO [ tb ] : can not be overwritten until RAP bug 363844 is fixed
//  @Override
//  public Control[] getChildren() {
//    return new Control[ 0 ];
//  }

  @Override
  public void setFont( Font font ) {
    super.setFont( font );
    writeFont();
  }

  //////
  // API

  public void setText( String text ) {
    checkWidget();
    if( text == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    this.text = text;
    this.clientReady = false;
    onReadyScript = null;
    writeText();
  }

  public String getText() {
    checkWidget();
    readText();
    return text;
  }

  public void setKnownStyles( Style[] styles ) {
    checkWidget();
    if( styles == null ) {
      knownStyles = new Style[ 0 ];
    } else {
      knownStyles = styles;
    }
    writeKnownStyles();      
  }

  public void applyStyle( Style style ) {
    checkWidget();
    // TODO [tb] : support applyStyle, removeFormat, removeStyle before ready
    if( style == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    StringBuilder code = new StringBuilder();
    code.append( "var style = " );
    code.append( getScriptNewStyle( style ) );
    code.append( ";style.apply( rap.editor.document );" );
    evalOnReady( code.toString() );
  }

  public void removeFormat() {
    checkWidget();
    evalOnReady( "rap.editor.execCommand( \"removeFormat\" );" );
  }

  public Style[] getActiveStyles() {
    checkWidget();
    readActiveStyles();
    return activeStyles;
  }

  /////////////
  // browser IO
  
  void onLoad() {
    if( clientLoaded ) {
      throw new IllegalStateException( "Document loaded twice" ); 
    }
    evalOnLoadScript();
    clientLoaded = true;
  }
  
  void onReady() {
    writeFont(); // CKEditor re-creates the document with every setData, loosing inline styles
    evalOnReadyScript();
    clientReady = true;
  }

  private void writeText() {
    evalOnLoad( "rap.editor.setData( \"" + escapeText( text ) + "\" );" );
  }

  private void readText() {
    if( clientReady ) {
      text = ( String )browser.evaluate( "return rap.editor.getData();" );
    }
  }

  private void readActiveStyles() {
    if( clientReady ) {
      try {
        String result = ( String )browser.evaluate( "return rap.getActiveStyles();" );
        JSONArray arr = new JSONArray( result );
        activeStyles = new Style[ arr.length() ];
        for( int i = 0; i < activeStyles.length; i++ ) {
          activeStyles[ i ] = knownStyles[ arr.getInt( i ) ];
        }
      } catch( JSONException e ) {
        throw new RuntimeException( "invalid json" );
      }
    }    
  }

  private void writeFont() {
    evalOnReady( "rap.editor.document.getBody().setStyle( \"font\", \"" + getCssFont() + "\" );" );
  }

  private void writeKnownStyles() {
    StringBuilder script = new StringBuilder( "rap.styles = [ " );
    if( knownStyles != null ) {
      for( int i = 0; i < knownStyles.length; i++ ) {
        if( knownStyles[ i ] != null ) {
          script.append( getScriptNewStyle( knownStyles[ i ] ) );
          if( i != knownStyles.length - 1 ) {
            script.append( ',' );
          }          
        }
      }
      script.append( " ];" );      
    }
    evalOnLoad( script.toString() );
  }

  /////////
  // helper

  private void addBrowserHandler() {
    browser.addProgressListener( new ProgressListener() {
      public void completed( ProgressEvent event ) {
        onLoad();
      }
      public void changed( ProgressEvent event ) {
      }
    } );
    new BrowserFunction( browser, READY_FUNCTION ) {
      public Object function( Object[] arguments ) {
        onReady();
        return null;
      }
    };
  }

  private void evalOnReady( String script ) {
    if( clientReady ) {
      browser.evaluate( "rap.editor.focus();" + script );
    } else {
      if( onReadyScript == null ) {
        onReadyScript = new StringBuilder( "rap.editor.focus();" );
      } 
      onReadyScript.append(  script );
    }
  }  
  
  private void evalOnLoad( String script ) {
    if( clientLoaded ) {
      browser.evaluate( script );
    } else {
      if( onLoadScript == null ) {
        onLoadScript = new StringBuilder();
      } 
      onLoadScript.append( script );
    }
  }  
  
  private void evalOnReadyScript() {
    if( onReadyScript != null ) {
      browser.evaluate( onReadyScript.toString() );
      onReadyScript = null;
    }
  }

  private void evalOnLoadScript() {
    if( onLoadScript != null ) {
      browser.evaluate( onLoadScript.toString() );
      onLoadScript = null;
    }
  }
  
  private String getCssFont() {
    StringBuilder result = new StringBuilder();
    if( getFont() != null ) {
      FontData data = getFont().getFontData()[ 0 ];
      result.append( data.getHeight() );
      result.append( "px " );
      result.append( escapeText( data.getName() ) );
    }
    return result.toString();
  }

  private String getScriptNewStyle( Style style ) {
    return "new CKEDITOR.style( " + style.toJSON() + " )";    
  }

  private static String escapeText( String text ) {
    // escaping backslashes, double-quotes, newlines, and carriage-return 
    StringBuilder result = new StringBuilder();
    for( int i = 0; i < text.length(); i++ ) {
      char ch = text.charAt( i );
      if( ch == '\n' ) {
        result.append( "\\n" );
      } else if( ch == '\r' ) {
        result.append( "\\r" );
      } else if( ch == '\\' ) {
        result.append( "\\\\" );
      } else if( ch == '"' ) {
        result.append( "\\\"" );
      } else {
        result.append( ch );
      }
    }
    return result.toString();
  }

}
