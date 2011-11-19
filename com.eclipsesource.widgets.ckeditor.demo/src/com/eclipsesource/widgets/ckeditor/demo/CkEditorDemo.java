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
package com.eclipsesource.widgets.ckeditor.demo;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.eclipsesource.widgets.ckeditor.CKEditor;
import com.eclipsesource.widgets.ckeditor.Style;


public class CkEditorDemo implements IEntryPoint {

  public int createUI() {
    final Display display = new Display();
    final Shell shell = new Shell( display );
    shell.setBounds( 10, 10, 800, 550 );
    shell.setText( "CkEditor Demo" );
    shell.setLayout( new GridLayout( 1, false ) );
    // CkEditor
    final CKEditor ckEditor = new CKEditor( shell, SWT.BORDER );
    ckEditor.setText( "bala<i>\"la\\la\"</i>la\r\nfoooo" );
    ckEditor.setKnownStyles( new Style[] {
      Style.BOLD,
      Style.ITALIC,
      Style.STRIKE,
      Style.SUBSCRIPE,
      Style.SUPERSCRIPE,
      Style.UNDERLINE
    } );
    ckEditor.applyStyle( Style.STRIKE );
    ckEditor.setLayoutData( new GridData() );
    System.out.println( ckEditor.getText() );
    ckEditor.setBackground( display.getSystemColor( SWT.COLOR_YELLOW ) );
    GridDataFactory.fillDefaults().grab( true, true ).applyTo( ckEditor );
    final ToolBar toolbar = new ToolBar( shell, SWT.FLAT );
    // Save button
    ToolItem printBtn = new ToolItem( toolbar, SWT.PUSH );
    printBtn.setText( "Print" );
    printBtn.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        System.out.println( ckEditor.getText() );
      }
    } );
    ToolItem styleBtn = new ToolItem( toolbar, SWT.NONE );
    styleBtn.setText( "Style" );
    final Menu dropDownMenu = new Menu( shell, SWT.POP_UP );
    MenuItem item = new MenuItem( dropDownMenu, SWT.PUSH );
    item.setText( "Bold" );
    item.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        ckEditor.applyStyle( Style.BOLD );
      }
    } );
    item = new MenuItem( dropDownMenu, SWT.PUSH );
    item.setText( "Italic" );
    item.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        ckEditor.applyStyle( Style.ITALIC );
      }
    } );
    item = new MenuItem( dropDownMenu, SWT.PUSH );
    item.setText( "Strike" );
    item.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        ckEditor.applyStyle( Style.STRIKE );
      }
    } );
    item = new MenuItem( dropDownMenu, SWT.PUSH );
    item.setText( "Subscripe" );
    item.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        ckEditor.applyStyle( Style.SUBSCRIPE );
      }
    } );
    item = new MenuItem( dropDownMenu, SWT.PUSH );
    item.setText( "Superscripe" );
    item.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        ckEditor.applyStyle( Style.SUPERSCRIPE );
      }
    } );
    item = new MenuItem( dropDownMenu, SWT.PUSH );
    item.setText( "Underline" );
    item.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        ckEditor.applyStyle( Style.UNDERLINE );
      }
    } );
    styleBtn.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        Point point = toolbar.toDisplay( event.x, event.y );
        dropDownMenu.setLocation( point );
        dropDownMenu.setVisible( true );
      }
    } );
    ToolItem fontBtn = new ToolItem( toolbar, SWT.PUSH );
    fontBtn.setText( "default-Font" );
    fontBtn.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        ckEditor.setFont( new Font( display, new FontDialog( shell ).open() ) );
      }
    } );
    ToolItem removeBtn = new ToolItem( toolbar, SWT.NONE );
    removeBtn.setText( "Unformat" );
    removeBtn.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        ckEditor.removeFormat();
      }
    } );
    ToolItem clearBtn = new ToolItem( toolbar, SWT.NONE );
    clearBtn.setText( "Clear" );
    clearBtn.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        ckEditor.setText( "" );
      }
    } );
    ToolItem activeBtn = new ToolItem( toolbar, SWT.NONE );
    activeBtn.setText( "actives" );
    activeBtn.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        Style[] actives = ckEditor.getActiveStyles();
        System.out.println( "---" );
        for( int i = 0; i < actives.length; i++ ) {
          if( actives[ i ] == Style.BOLD ) {
            System.out.println( "Bold" );
          } else if( actives[ i ] == Style.ITALIC ) {
            System.out.println( "Italic" );
          } else if( actives[ i ] == Style.STRIKE ) {
            System.out.println( "Strike" );
          } else if( actives[ i ] == Style.SUBSCRIPE ) {
            System.out.println( "Suberscripe" );
          } else if( actives[ i ] == Style.UNDERLINE ) {
            System.out.println( "Underline" );
          }
        }
      }
    } );
    shell.open();
    runReadAndDispatchLoop( shell );
    return 0;
  }

  private void runReadAndDispatchLoop( Shell shell ) {
    while( !shell.isDisposed() ) {
      if( !shell.getDisplay().readAndDispatch() ) {
        shell.getDisplay().sleep();
      }
    }
  }
}
