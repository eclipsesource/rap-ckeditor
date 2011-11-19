package com.eclipsesource.widgets.ckeditor;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class CKEditor_Test extends TestCase {

  private Display display;
  private Shell shell;
  private CKEditor editor;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    editor = new CKEditor( shell, SWT.NONE );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testGetLayout() {
    assertTrue( editor.getLayout() instanceof FillLayout );
  }
  
  public void testBackgroundMode() {
    assertEquals( SWT.INHERIT_FORCE, editor.getBackgroundMode() );
  }
  
//  public void testGetChildren() {
//    assertEquals( 0, editor.getChildren().length );
//  }

  public void testSetLayout() {
    try {
      editor.setLayout( new FillLayout() );
      fail();
    } catch( UnsupportedOperationException ex ) {
      // expected
    }
  }

  public void testURL() {
    assertEquals( "/resources/ckeditor.html", editor.browser.getUrl() );
  }

  public void testIsInitiallyNotLoaded() {
    assertFalse( editor.clientLoaded );
  }
  
  public void testIsInitiallyNotReady() {
    assertFalse( editor.clientReady );
  }

  public void testIsLoadedOnLoad() {
    mockBrowser( editor );
    editor.onLoad();
    assertTrue( editor.clientLoaded );
  }

  public void testIsReadyOnReady() {
    mockBrowser( editor );
    editor.onLoad();
    editor.onReady();
    assertTrue( editor.clientReady );
  }
  
  public void testSetText() {
    mockBrowser( editor );
    editor.onLoad();
    editor.onReady();
    String text = "foo<span>bar</span>";
    
    editor.setText( text );
    
    String expected = "rap.editor.setData( \"" + text + "\" );";
    verify( editor.browser ).evaluate( expected );
  }
  
  public void testSetTextNull() {
    try {
      editor.setText( null );
      fail();
    } catch( IllegalArgumentException ex ) {
      // expected
    }
  }

  public void testSetTextBeforeLoaded() {
    mockBrowser( editor );
    String text = "foo<span>bar</span>";
    
    editor.setText( text );
    
    verify( editor.browser, times( 0 ) ).evaluate( anyString() );
  }

  public void testSetNoTextBeforeLoad() {
    mockBrowser( editor );
    
    editor.onLoad();
    
    verify( editor.browser, times( 0 ) ).evaluate( contains( "setText" ) );
  }

  public void testRenderTextAfterLoaded() {
    mockBrowser( editor );
    String text = "foo<span>bar</span>";

    editor.setText( text );
    editor.onLoad();
    
    String expected = "rap.editor.setData( \"" + text + "\" );";
    verify( editor.browser ).evaluate( contains( expected ) );
  }
  
  public void testNoSecondLoaded() {
    mockBrowser( editor );
    editor.onLoad();
    try {
      editor.onLoad();
      fail();
    } catch( IllegalStateException ex ) {
      // expected
    }
  }

  public void testSetTextEscape() {
    mockBrowser( editor );
    editor.onLoad();
    String text = "foo<span>\"bar\\</span>\r\n";

    editor.setText( text );
    editor.onReady();
    
    String expectedText = "foo<span>\\\"bar\\\\</span>\\r\\n";
    String expected = "rap.editor.setData( \"" + expectedText + "\" );";
    verify( editor.browser ).evaluate( contains( expected ) );
  }
  
  public void testGetTextWhenNotReady() {
    mockBrowser( editor );
    editor.onLoad();
    String text = "foo<span>bar</span>";
    
    editor.setText( text );
    String result = editor.getText();

    verify( editor.browser, times( 0 ) ).evaluate( contains( "getText") );
    assertEquals( text, result );
  }
  
  public void testGetTextAfterReady() {
    mockBrowser( editor );
    editor.onLoad();
    editor.onReady();
    String text = "foo<span>bar</span>";
    String script = "return rap.editor.getData();";
    when( editor.browser.evaluate( script ) ).thenReturn( text );
    
    String result = editor.getText();
    
    verify( editor.browser, times( 1 ) ).evaluate( script );
    assertEquals( text, result );
  }
  
  public void testApplyStyle() {
    mockBrowser( editor );
    editor.onLoad();
    editor.onReady();
    Style style = new Style( "b" );
    
    editor.applyStyle( style );
    
    verify( editor.browser ).evaluate( contains( "focus();var style = new CKEDITOR.style( {" ) );
    verify( editor.browser ).evaluate( contains( "\"element\":\"b\"" ) );
    verify( editor.browser ).evaluate( contains( "style.apply( rap.editor.document );" ) );
  }

  public void testApplyStyleNull() {
    mockBrowser( editor );
    editor.onLoad();
    editor.onReady();

    try {
      editor.applyStyle( null );
      fail();
    } catch( IllegalArgumentException ex ) {
      // expected
    }
    
  }
  
  public void testApplyStyleBeforeReady() {
    mockBrowser( editor );
    editor.onLoad();
    
    editor.applyStyle( new Style( "b" ) );
    
    verify( editor.browser, times( 0 ) ).evaluate( contains( "style.apply" ) );
  }
  
  public void testRenderStyleOnReady() {
    mockBrowser( editor );
    editor.onLoad();
    
    editor.applyStyle( new Style( "b" ) );
    editor.applyStyle( new Style( "c" ) );
    editor.onReady();
    
    verify( editor.browser, times( 1 ) ).evaluate( contains( "focus();var style" ) );
    verify( editor.browser ).evaluate( contains( "\"element\":\"b\"" ) );
    verify( editor.browser ).evaluate( contains( "\"element\":\"c\"" ) );
  }
  
  public void testApplyStyleBeforeSetText() {
    mockBrowser( editor );
    editor.onLoad();
    
    editor.applyStyle( new Style( "b" ) );
    editor.setText( "foo" );
    editor.applyStyle( new Style( "c" ) );
    editor.onReady();
    
    verify( editor.browser, times( 1 ) ).evaluate( contains( "style.apply" ) );
    verify( editor.browser, times( 1 ) ).evaluate( contains( "\"element\":\"c\"" ) );
    verify( editor.browser, times( 0 ) ).evaluate( contains( "\"element\":\"b\"" ) );
  }

  public void testRemoveFormat() {
    mockBrowser( editor );
    editor.onLoad();
    editor.onReady();
    
    editor.removeFormat();
    
    verify( editor.browser ).evaluate( contains( "rap.editor.execCommand( \"removeFormat\" );" ) );
  }

  public void testRemoveFormatBeforeReady() {
    mockBrowser( editor );
    editor.onLoad();
    
    editor.removeFormat();
    
    verify( editor.browser, times( 0 ) ).evaluate( contains( "\"removeFormat\" );" ) );
  }
  
  public void testRenderRemoveFormatOnReady() {
    mockBrowser( editor );
    editor.onLoad();
    
    editor.removeFormat();
    editor.onReady();
    
    verify( editor.browser, times( 1 ) ).evaluate( contains( "\"removeFormat\" );" ) );
  }
  

  public void testSetFontAfterReady() {
    mockBrowser( editor );
    editor.onLoad();
    
    editor.onReady();

    String expected = "setStyle( \"font\"";
    verify( editor.browser, times( 1 ) ).evaluate( contains( expected ) );
  }

  public void testSetFontFamilyAndSize() {
    mockBrowser( editor );
    editor.onLoad();
    editor.onReady();

    editor.setFont( new Font( display, "fantasy", 13, 0 ) );
    
    String expected = "setStyle( \"font\", \"13px fantasy";
    verify( editor.browser, times( 1 ) ).evaluate( contains( expected ) );
  }
  
  public void testSetFontEscape() {
    mockBrowser( editor );
    editor.onLoad();
    editor.onReady();
    
    editor.setFont( new Font( display, "\"courier new\"", 13, 0 ) );
    
    String expected = "setStyle( \"font\", \"13px \\\"courier new\\\"";
    verify( editor.browser, times( 1 ) ).evaluate( contains( expected ) );
  }

  public void testSetKnownStyles() {
    mockBrowser( editor );
    editor.onLoad();    
    
    editor.setKnownStyles( new Style[]{ new Style( "b" ), new Style( "u" ) } );
    
    String expected = "rap.styles = [ new CKEDITOR.style( {";
    verify( editor.browser, times( 1 ) ).evaluate( contains( expected ) );
    verify( editor.browser, times( 1 ) ).evaluate( contains( "\"element\":\"b\"" ) );
    verify( editor.browser, times( 1 ) ).evaluate( contains( "\"element\":\"u\"" ) );
  }

  public void testSetKnownStylesBeforeLoad() {
    mockBrowser( editor );
    
    editor.setKnownStyles( new Style[]{ new Style( "b" ), new Style( "u" ) } );
    
    String expected = "rap.styles = [ new CKEDITOR.style( {";
    verify( editor.browser, times( 0 ) ).evaluate( contains( expected ) );
  }

  public void testSetKnownStylesRenderAtLoad() {
    mockBrowser( editor );
    
    editor.setKnownStyles( new Style[]{ new Style( "b" ), new Style( "u" ) } );
    editor.onLoad();    
    
    String expected = "rap.styles = [ new CKEDITOR.style( {";
    verify( editor.browser, times( 1 ) ).evaluate( contains( expected ) );
    verify( editor.browser, times( 1 ) ).evaluate( contains( "\"element\":\"b\"" ) );
    verify( editor.browser, times( 1 ) ).evaluate( contains( "\"element\":\"u\"" ) );
  }

  public void testgetActiveStyles() {
    mockBrowser( editor );
    editor.onLoad();
    editor.onReady();
    Style[] styles = new Style[]{ new Style( "b" ), new Style( "u" ), new Style( "x" ) };
    editor.setKnownStyles( styles );
    String response = "[ 0, 2 ]";
    String expected = "rap.getActiveStyles()";
    when( editor.browser.evaluate( contains( expected ) ) ).thenReturn( response );
    
    Style[] result = editor.getActiveStyles();
    
    verify( editor.browser, times( 1 ) ).evaluate( contains( expected ) );
    assertEquals( 2, result.length );
    assertEquals( styles[ 0 ], result[ 0 ] );
    assertEquals( styles[ 2 ], result[ 1 ] );
  }

  /////////
  // Helper

  private void mockBrowser( CKEditor editor ) {
    Browser orgBrowser = editor.browser;
    editor.browser = mock( Browser.class );
    editor.browser.setUrl( orgBrowser.getUrl() );
  }

}
