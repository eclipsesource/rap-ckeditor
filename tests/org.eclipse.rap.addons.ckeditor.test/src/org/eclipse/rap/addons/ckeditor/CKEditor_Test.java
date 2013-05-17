package org.eclipse.rap.addons.ckeditor;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import junit.framework.TestCase;

import org.eclipse.rap.addons.ckeditor.CKEditor;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.client.service.JavaScriptLoader;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.mockito.ArgumentCaptor;


public class CKEditor_Test extends TestCase {

  private Display display;
  private Shell shell;
  private CKEditor editor;
  private Connection connection;
  private RemoteObject remoteObject;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    Fixture.fakeNewRequest();
    remoteObject = mock( RemoteObject.class );
    connection = mock( Connection.class );
    when( connection.createRemoteObject( anyString() ) ).thenReturn( remoteObject );
    Fixture.fakeConnection( connection );
    editor = new CKEditor( shell, SWT.BORDER );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testSetLayout() {
    try {
      editor.setLayout( new FillLayout() );
      fail();
    } catch( UnsupportedOperationException ex ) {
      // expected
    }
  }

  public void testContructor_CreatesRemoteObjectWithCorrectType() {
    verify( connection ).createRemoteObject( eq( "eclipsesource.CKEditor" ) );
  }

  public void testContructor_SetsParent() {
    verify( remoteObject ).set( "parent", WidgetUtil.getId( editor ) );
  }

  public void testContructor_LoadsJavaScriptFiles() {
    JavaScriptLoader loader = mockJavaScriptLoader();
    ResourceManager resourceManager = RWT.getResourceManager();

    new CKEditor( shell, SWT.BORDER );

    verify( loader ).require( resourceManager.getLocation( "ckeditor/ckeditor.js" ) );
    verify( loader ).require( resourceManager.getLocation( "ckeditor/config.js" ) );
    verify( loader ).require( resourceManager.getLocation( "ckeditor/handler.js" ) );
  }

  public void testSetText_GetText() {
    String text = "foo<span>bar</span>";

    editor.setText( text );

    assertEquals( text, editor.getText() );
  }

  public void testSetText_RendersToClient() {
    String text = "foo<span>bar</span>";

    editor.setText( text );

    verify( remoteObject ).set( "text", text );
  }

  public void testSetTextNull() {
    try {
      editor.setText( null );
      fail();
    } catch( IllegalArgumentException ex ) {
      // expected
    }
  }

  public void testSetTextFromClient() {
    String text = "foo<span>bar</span>";

    remoteSet( remoteObject, "text", JsonValue.valueOf( text ) );

    assertEquals( text, editor.getText() );
  }


  public void testSetFont_RendersToClient() {
    editor.setFont( new Font( display, "fantasy", 13, 0 ) );

    verify( remoteObject ).set( "font", "13px fantasy" );
  }

  public void testDispose_RendersDestroyToClient() {
    editor.dispose();

    verify( remoteObject ).destroy();
  }

  /////////
  // Helper

  private JavaScriptLoader mockJavaScriptLoader() {
    WebClient client = mock( WebClient.class );
    Fixture.fakeClient( client );
    JavaScriptLoader loader = mock( JavaScriptLoader.class );
    when( client.getService( JavaScriptLoader.class ) ).thenReturn( loader );
    return loader;
  }

  private static void remoteSet( RemoteObject remoteObjectMock, String proprety, JsonValue value ) {
    getHandler( remoteObjectMock ).handleSet( new JsonObject().add( proprety, value ) );
  }

  private static OperationHandler getHandler( RemoteObject remoteObjectMock ) {
    ArgumentCaptor<OperationHandler> captor = ArgumentCaptor.forClass( OperationHandler.class );
    verify( remoteObjectMock ).setHandler( captor.capture() );
    return captor.getValue();
  }

}
