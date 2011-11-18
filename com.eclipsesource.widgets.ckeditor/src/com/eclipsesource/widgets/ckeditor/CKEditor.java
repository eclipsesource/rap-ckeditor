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



public class CKEditor extends Composite {

  private static final String URL = "/resources/ckeditor.html";
  private static final String READY_FUNCTION = "rap_ready";
  private String text = "";
  private StringBuilder evalScript = null;
  Browser browser;
  boolean loaded = false;
  boolean ready = false;

  
  public CKEditor( Composite parent, int style ) {
    super( parent, style );
    super.setLayout( new FillLayout() );
    browser = new Browser( this, SWT.NONE );
    this.setBackgroundMode( SWT.INHERIT_FORCE );
    browser.setUrl( URL );
    addBrowserHandler();
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
    if( text == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    this.text = text;
    this.ready = false;
    evalScript = null;
    if( loaded ) {
      // special case: text can be set any time after load, event if not ready
      browser.evaluate( getScriptSetText() );          
    }
  }

  public String getText() {
    readText();
    return text;
  }

  public void applyStyle( Style style ) {
    // TODO [tb] : support applyStyle, removeFormat, removeStyle before ready
    if( style == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    evaluate( getScriptApplyStyle( style ) );
  }

  public void removeFormat() {
    evaluate( "rap.editor.execCommand( \"removeFormat\" );" );
  }

  /////////////
  // browser IO
  
  void onLoad() {
    if( loaded ) {
      throw new IllegalStateException( "Document loaded twice" ); 
    }
    loaded = true;
    String script = "rap.createEditor();";
    if( !text.equals( "" ) ) {
      script += getScriptSetText();
    }
    browser.evaluate( script );
  }
  
  void onReady() {
    writeFont(); // CKEditor re-creates the document with every setData, loosing inline styles
    writePendingScript();
    ready = true;
  }
  
  private void readText() {
    if( ready ) {
      text = ( String )browser.evaluate( "return rap.editor.getData();" );
    }
  }
  
  private void writeFont() {
    evaluate( "rap.editor.document.getBody().setStyle( \"font\", \"" + getCssFont() + "\" );" );
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

  private void evaluate( String script ) {
    if( ready ) {
      browser.evaluate( "rap.editor.focus();" + script );
    } else {
      if( evalScript == null ) {
        evalScript = new StringBuilder( "rap.editor.focus();" );
      } 
      evalScript.append(  script );
    }
  }  
  
  private void writePendingScript() {
    if( evalScript != null ) {
      browser.evaluate( evalScript.toString() );
      evalScript = null;
    }
  }

  private String getCssFont() {
    StringBuilder result = new StringBuilder();
    if( getFont() != null ) {
      FontData data = getFont().getFontData()[ 0 ];
      result.append( data.getHeight() );
      result.append( "pt " );
      result.append( escapeText( data.getName() ) );
    }
    return result.toString();
  }

  private String getScriptSetText() {
    return "rap.editor.setData( \"" + escapeText( text ) + "\" );";
  }

  private String getScriptApplyStyle( Style style ) {
    StringBuilder code = new StringBuilder();
    code.append( "var style = new CKEDITOR.style( " );
    code.append( style.toJSON() );
    code.append( " );style.apply( rap.editor.document );" );
    return code.toString();
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
