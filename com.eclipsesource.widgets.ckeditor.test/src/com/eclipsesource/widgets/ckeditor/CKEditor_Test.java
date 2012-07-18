package com.eclipsesource.widgets.ckeditor;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.widgets.BrowserCallback;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.internal.widgets.IBrowserAdapter;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;



@SuppressWarnings("restriction")
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
    assertEquals( "rwt-resources/ckeditor/ckeditor.html", editor.browser.getUrl() );
  }

  public void testIsInitiallyNotReady() {
    assertFalse( editor.clientReady );
  }

  public void testIsReadyOnReady() {
    mockBrowser( editor );
    editor.onReady();
    assertTrue( editor.clientReady );
  }

  public void testSetText() {
    mockBrowser( editor );
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

  public void testSetTextBeforeReady() {
    mockBrowser( editor );
    String text = "foo<span>bar</span>";

    editor.setText( text );

    verify( editor.browser, times( 0 ) ).evaluate( anyString() );
  }

  public void testSetNoTextBeforeReady() {
    mockBrowser( editor );

    editor.onReady();

    verify( editor.browser, times( 0 ) ).evaluate( contains( "setText" ) );
  }

  public void testRenderTextAfterReady() {
    mockBrowser( editor );
    String text = "foo<span>bar</span>";

    editor.setText( text );
    editor.onReady();

    String expected = "rap.editor.setData( \"" + text + "\" );";
    verify( editor.browser ).evaluate( contains( expected ) );
  }

  public void testSetTextEscape() {
    mockBrowser( editor );
    String text = "foo<span>\"bar\\</span>\r\n";

    editor.setText( text );
    editor.onReady();

    String expectedText = "foo<span>\\\"bar\\\\</span>\\r\\n";
    String expected = "rap.editor.setData( \"" + expectedText + "\" );";
    verify( editor.browser ).evaluate( contains( expected ) );
  }

  @SuppressWarnings("deprecation")
  public void testGetTextWhenNotReady() {
    mockBrowser( editor );
    String text = "foo<span>bar</span>";

    editor.setText( text );
    String result = editor.getText();

    verify( editor.browser, times( 0 ) ).evaluate( contains( "getText") );
    assertEquals( text, result );
  }

  public void testGetTextAfterReady() {
    mockBrowser( editor );
    editor.onReady();
    String text = "foo<span>bar</span>";
    String script = "return rap.editor.getData();";
    when( editor.browser.evaluate( script ) ).thenReturn( text );

    String result = editor.getText();

    verify( editor.browser, times( 1 ) ).evaluate( script );
    assertEquals( text, result );
  }

  public void testSetFontAfterReady() {
    mockBrowser( editor );

    editor.onReady();

    String expected = "setStyle( \"font\"";
    verify( editor.browser, times( 1 ) ).evaluate( contains( expected ) );
  }

  public void testSetFontFamilyAndSize() {
    mockBrowser( editor );
    editor.onReady();

    editor.setFont( new Font( display, "fantasy", 13, 0 ) );

    String expected = "setStyle( \"font\", \"13px fantasy";
    verify( editor.browser, times( 1 ) ).evaluate( contains( expected ) );
  }

  public void testSetFontEscape() {
    mockBrowser( editor );
    editor.onReady();

    editor.setFont( new Font( display, "\"courier new\"", 13, 0 ) );

    String expected = "setStyle( \"font\", \"13px \\\"courier new\\\"";
    verify( editor.browser, times( 1 ) ).evaluate( contains( expected ) );
  }

  public void testUseBrowserUtilOnSimpleLifycycle() {
    mockBrowser( editor, true );
    final ArrayList<InvocationOnMock> log = getNonBlockingEvalLog( editor );
    editor.onReady();
    log.clear(); // onReady renders font
    String text = "foo<span>bar</span>";

    editor.setText( text );

    String expected = "rap.editor.setData( \"" + text + "\" );";
    assertEquals( 1, log.size() );
    InvocationOnMock invocation = log.get( 0 );
    String script = ( String )invocation.getArguments()[ 0 ];
    assertEquals( expected, script );
  }

//  TODO [tb] : Ideally the getText method never works with SimpleLifecycle, but
//              i did not find a good way to implement that, so this test fails for now.
//  public void testGetTextWithSimpleLifeCycleCrashes() {
//    mockBrowser( editor, true );
//
//    try {
//      editor.getText();
//      fail();
//    } catch( UnsupportedOperationException ex ) {
//      // expected
//    }
//  }

  public void testGetTextWithSimpleLifecycle() {
    mockBrowser( editor, true );
    final String script = "return rap.editor.getData();";
    final String text = "foo<span>bar</span>";
    IBrowserAdapter adapter = mock( IBrowserAdapter.class );
    stub( editor.browser.getAdapter( IBrowserAdapter.class ) ).toReturn( adapter );
    doAnswer( new Answer<Object>() {
      public Object answer( InvocationOnMock invocation ) throws Throwable {
        BrowserCallback callback = ( BrowserCallback )invocation.getArguments()[ 1 ];
        callback.evaluationSucceeded( text );
        return null;
      }
    } ).when( adapter ).evaluateNonBlocking(
      Matchers.eq( script ),
      Matchers.any( BrowserCallback.class )
    );
    final ArrayList<String> callbackLog = new ArrayList<String>();
    editor.onReady();

    editor.getText( new CKEditorCallback() {
      public void handleGetText( String result ) {
        callbackLog.add( result );
      }
    } );

    assertEquals( 1, callbackLog.size() );
    assertEquals( text, callbackLog.get( 0 ) );
  }

  @SuppressWarnings("restriction")
  private ArrayList<InvocationOnMock> getNonBlockingEvalLog( CKEditor editor ) {
    final ArrayList<InvocationOnMock> log = new ArrayList<InvocationOnMock>();
    IBrowserAdapter adapter = mock( IBrowserAdapter.class );
    stub( editor.browser.getAdapter( IBrowserAdapter.class ) ).toReturn( adapter );
    doAnswer( new Answer<Object>() {
      public Object answer( InvocationOnMock invocation ) throws Throwable {
        log.add( invocation ); // because verify can not be used with void methods
        return null;
      }
    } ).when( adapter ).evaluateNonBlocking( Matchers.anyString(), Matchers.any( BrowserCallback.class ) );
    return log;
  }

  /////////
  // Helper

  private void mockBrowser( CKEditor editor ) {
    mockBrowser( editor, false );
  }

  private void mockBrowser( CKEditor editor, boolean JEEMode ) {
    Browser orgBrowser = editor.browser;
    editor.browser = mock( Browser.class );
    editor.browser.setUrl( orgBrowser.getUrl() );
    if( JEEMode ) {
      UnsupportedOperationException throwable = new UnsupportedOperationException();
      stub( editor.browser.evaluate( Matchers.anyString() ) ).toThrow( throwable );
    }
  }

}
