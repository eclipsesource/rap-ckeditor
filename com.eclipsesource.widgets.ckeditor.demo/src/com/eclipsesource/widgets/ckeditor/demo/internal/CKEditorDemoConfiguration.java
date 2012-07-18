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

package com.eclipsesource.widgets.ckeditor.demo.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.application.Application;
import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.client.WebClient;

import com.eclipsesource.widgets.ckeditor.demo.CkEditorDemo;


public class CKEditorDemoConfiguration implements ApplicationConfiguration {

  public void configure( Application application ) {
    Map<String, String> map = new HashMap<String, String>();
    map.put( WebClient.PAGE_TITLE, "CKEditor Demo" );
    application.addEntryPoint( "/ckeditor", CkEditorDemo.class, map );
  }
}
