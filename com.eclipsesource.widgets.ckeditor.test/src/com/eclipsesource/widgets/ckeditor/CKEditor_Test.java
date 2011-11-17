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
    assertFalse( editor.loaded );
  }
  
  public void testIsInitiallyNotReady() {
    assertFalse( editor.ready );
  }

  public void testIsLoadedOnLoad() {
    mockBrowser( editor );
    editor.onLoad();
    assertTrue( editor.loaded );
  }

  public void testIsReadyOnReady() {
    mockBrowser( editor );
    editor.onLoad();
    editor.onReady();
    assertTrue( editor.ready );
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
    
    verify( editor.browser, times( 2 ) ).evaluate( anyString() );
    verify( editor.browser ).evaluate( contains( "var style = new CKEDITOR.style( {" ) );
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
  
//  public void testApplyStyleBeforeReady() {
//    mockBrowser( editor );
//    editor.onLoad();
//    Style style = new Style( "b" );
//    
//    editor.applyStyle( style );
//    
//    verify( editor.browser, times( 0 ) ).evaluate( contains( "style.apply" ) );
//  }
  
  public void testRemoveFormat() {
    mockBrowser( editor );
    editor.onLoad();
    editor.onReady();
    
    editor.removeFormat();
    
    verify( editor.browser ).evaluate( contains( "rap.editor.execCommand( \"removeFormat\" );" ) );
  }
  
  /////////
  // Helper

  private void mockBrowser( CKEditor editor ) {
    Browser orgBrowser = editor.browser;
    editor.browser = mock( Browser.class );
    editor.browser.setUrl( orgBrowser.getUrl() );
  }

}
