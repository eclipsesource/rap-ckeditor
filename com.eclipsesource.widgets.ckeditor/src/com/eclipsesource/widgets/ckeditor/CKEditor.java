/*******************************************************************************
 * Copyright (c) 2011 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.eclipsesource.widgets.ckeditor;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream.GetField;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.application.ApplicationRunner;
import org.eclipse.rwt.internal.lifecycle.SimpleLifeCycle;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.widgets.BrowserCallback;
import org.eclipse.rwt.widgets.BrowserUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;



public class CKEditor extends Composite {


  private static final String SCRIPT_GET_TEXT = "return rap.editor.getData();";

  private static final String RESOURCES_PATH = "resources/";
  private static final String REGISTER_PATH = "ckeditor/";
  private static final String READY_FUNCTION = "rap_ready";

  private static final String[] RESOURCE_FILES = {
    "ckeditor.html",
    "ckeditor.js",
    "config.js",
    "contents.css",
    "lang/en.js",
    "skins/kama/editor.css",
    "skins/kama/icons.png",
    "skins/kama/images/sprites.png",
    "skins/kama/images/sprites_ie6.png"
  };

  private String text = "";
  Browser browser;
  boolean clientReady = false;
  private StringBuilder scriptBuffer = null;

  public CKEditor( Composite parent, int style ) {
    super( parent, style );
    super.setLayout( new FillLayout() );
    this.setBackgroundMode( SWT.INHERIT_FORCE );
    registerResources();
    browser = new Browser( this, SWT.BORDER );
    browser.setUrl( getEditorHtmlLocation() );
    addBrowserHandler();
  }

  private void registerResources() {
    IResourceManager resourceManager = RWT.getResourceManager();
    boolean isRegistered = resourceManager.isRegistered( REGISTER_PATH + RESOURCE_FILES[ 0 ] );
    if( !isRegistered ) {
      try {
        for( String fileName : RESOURCE_FILES ) {
          register( resourceManager, fileName );
        }
      } catch( IOException ioe ) {
        throw new IllegalArgumentException( "Failed to load resources", ioe );
      }
    }
  }

  private String getEditorHtmlLocation() {
    IResourceManager resourceManager = RWT.getResourceManager();
    return resourceManager.getLocation( REGISTER_PATH + RESOURCE_FILES[ 0 ] );
  }

  private void register( IResourceManager resourceManager, String fileName ) throws IOException {
    ClassLoader classLoader = CKEditor.class.getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream( RESOURCES_PATH + fileName );
    try {
      resourceManager.register( REGISTER_PATH + fileName, inputStream );
    } finally {
      inputStream.close();
    }
  }

  ////////////////////
  // overwrite methods

  @Override
  public void setLayout( Layout layout ) {
    throw new UnsupportedOperationException( "Cannot change internal layout of CkEditor" );
  }

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
    writeText();
    clientReady = false; // order is important
  }

  /**
   * @deprecated Not compatible with JEE compatibility mode. Use
   * {@link CKEditor#getText(CKEditorCallback)} instead.
   * @return
   */
  public String getText() {
    checkWidget();
    try {
      readText();
    } catch( UnsupportedOperationException ex ) {
      String msg = ex + " Use getText(CKEditorCallback).";
      throw new UnsupportedOperationException( msg );
    }
    return text;
  }

  public void getText( final CKEditorCallback ckEditorCallback ) {
    BrowserUtil.evaluate( browser, SCRIPT_GET_TEXT, new BrowserCallback() {
      public void evaluationSucceeded( Object result ) {
        ckEditorCallback.handleGetText( ( String )result );
      }
      public void evaluationFailed( Exception exception ) {
        throw new RuntimeException( exception );
      }
    } );
  }

  //////////////
  // browser I/O

  void onReady() {
    writeFont(); // CKEditor re-creates the document with every setData, losing inline styles
    evalScriptBuffer();
    clientReady = true;
  }

  private void writeText() {
    evalOnReady( "rap.editor.setData( \"" + escapeText( text ) + "\" );" );
  }

  private void writeFont() {
    evalOnReady( "rap.editor.document.getBody().setStyle( \"font\", \"" + getCssFont() + "\" );" );
  }

  private void readText() {
    if( clientReady ) {
      text = ( String )browser.evaluate( SCRIPT_GET_TEXT );
    }
  }

  /////////
  // helper

  private void addBrowserHandler() {
    new BrowserFunction( browser, READY_FUNCTION ) {
      public Object function( Object[] arguments ) {
        onReady();
        return null;
      }
    };
  }

  private void evalOnReady( String script ) {
    if( clientReady ) {
      executeScript( script );
    } else {
      if( scriptBuffer == null ) {
        scriptBuffer = new StringBuilder();
      }
      scriptBuffer.append( script );
    }
  }

  private void evalScriptBuffer() {
    if( scriptBuffer != null ) {
      executeScript( scriptBuffer.toString() );
      scriptBuffer = null;
    }
  }

  private void executeScript( String script ) {
    try {
      browser.evaluate( script );
    } catch( UnsupportedOperationException ex ) {
      BrowserUtil.evaluate( browser, script, new BrowserCallback() {
        public void evaluationSucceeded( Object result ) {
          // nothing to do
        }
        public void evaluationFailed( Exception exception ) {
          throw new RuntimeException( exception );
        }
      } );
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
