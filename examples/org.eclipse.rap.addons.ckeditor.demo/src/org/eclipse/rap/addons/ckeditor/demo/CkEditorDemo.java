/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.addons.ckeditor.demo;

import org.eclipse.rap.addons.ckeditor.CKEditor;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;



public class CkEditorDemo extends AbstractEntryPoint {

  @Override
  protected void createContents( final Composite parent ) {
    getShell().setText( "CkEditor Demo" );
    parent.setLayout( new GridLayout( 1, false ) );
    // CkEditor
    final CKEditor ckEditor = new CKEditor( parent, SWT.BORDER );
    ckEditor.setFont( new Font( parent.getDisplay(), "fantasy", 19, 0 ) );
    ckEditor.setText( "Hello Fantasy Font" );
    ckEditor.setLayoutData( new GridData() );
    ckEditor.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    ToolBar toolbar = new ToolBar( parent, SWT.FLAT );
    ToolItem printBtn = new ToolItem( toolbar, SWT.PUSH );
    printBtn.setText( "Print" );
    printBtn.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        System.out.println( ckEditor.getText() );
      }
    } );
    ToolItem destrBtn = new ToolItem( toolbar, SWT.PUSH );
    destrBtn.setText( "Dispose" );
    destrBtn.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        ckEditor.dispose();
      }
    } );
    ToolItem fontBtn = new ToolItem( toolbar, SWT.PUSH );
    fontBtn.setText( "Font" );
    fontBtn.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        ckEditor.setFont( new Font( parent.getDisplay(), "serif", 9, 0 ) );
      }
    } );
    ToolItem clearBtn = new ToolItem( toolbar, SWT.NONE );
    clearBtn.setText( "Clear" );
    clearBtn.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        ckEditor.setText( "" );
      }
    } );
  }

}
