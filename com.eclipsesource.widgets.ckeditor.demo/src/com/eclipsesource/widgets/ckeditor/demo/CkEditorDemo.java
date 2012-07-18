/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

package com.eclipsesource.widgets.ckeditor.demo;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.eclipsesource.widgets.ckeditor.CKEditor;
import com.eclipsesource.widgets.ckeditor.CKEditorCallback;


public class CkEditorDemo implements IEntryPoint {

  public int createUI() {
    final Display display = new Display();
    Shell shell = new Shell( display );
    shell.setBounds( 10, 10, 800, 550 );
    shell.setText( "CkEditor Demo" );

    shell.setLayout( new GridLayout( 1, false ) );
    // CkEditor
    final CKEditor ckEditor = new CKEditor( shell, SWT.NONE );
    ckEditor.setFont( new org.eclipse.swt.graphics.Font( display, "fantasy", 13, 0 ) );
    ckEditor.setText( "This is awsome text!" );
    ckEditor.setLayoutData( new GridData() );
    ckEditor.setBackground( display.getSystemColor( SWT.COLOR_YELLOW ) );
    GridDataFactory.fillDefaults().grab( true, true ).applyTo( ckEditor );
    ToolBar toolbar = new ToolBar( shell, SWT.FLAT );
    ToolItem printBtn = new ToolItem( toolbar, SWT.PUSH );
    printBtn.setText( "Print" );
    printBtn.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        ckEditor.getText( new CKEditorCallback() {
          public void handleGetText( String result ) {
            System.out.println( result );
          }
        } );
      }
    } );
    ToolItem fontBtn = new ToolItem( toolbar, SWT.PUSH );
    fontBtn.setText( "Font" );
    fontBtn.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        ckEditor.setFont( new org.eclipse.swt.graphics.Font( display, "serif", 13, 0 ) );
      }
    } );
    ToolItem clearBtn = new ToolItem( toolbar, SWT.NONE );
    clearBtn.setText( "Clear" );
    clearBtn.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        ckEditor.setText( "" );
      }
    } );
    shell.open();
    return 0;
  }

}
