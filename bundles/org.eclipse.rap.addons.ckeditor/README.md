# CKEditor for RAP 2.1

This is a custom widget for the Remote Application Platform (RAP) that wraps the CKEditor, a web-based WYSIWYG/Rich-Text editor.

## API
Currently, the API consists of the two methods <code>getText</code> and <code>setText</code>.
The text can be any valid HTML, but should be limited to the subset the editor can handle.

## Customization

The editor can be customized by editing the files in the <code>src/resources</code> folder of the <code>org.eclipse.rap.addons.ckeditor</code> bundle.
You might need to clear the browsers cache and restart the server for all changes to take effect.

### Editor Configuration

Editing the file <code>config.js</code> lets you change the toolbar, language, and formatting options (fonts, colors).
Be careful, all changes here bear the risk of breaking the editor.

### Editor Theming

To change the icons, edit or replace <code>icons.png</code>.
To change the editors colors, borders, spacings, etc, edit <code>editor.css</code>. You can use a tool like Firebug to examine which CSS classes are used where in the editor.

### Advanced Customization

For various reasons some CKEditor plugins have been removed from <code>ckeditor.js</code> and disabled in <code>config.js</code>, therefore not all options of the full CKEdtior are working.
If you wish, you can compile your own <code>ckeditor.js</code>.
To do so, download the CKPackager (http://docs.cksource.com/CKEditor_3.x/Developers_Guide/CKPackager) and place it in the <code>org.eclipse.rap.addons.ckeditor.build</code> bundle.
You can then edit the <code>ckeditor.pack</code> file to add more CKEditor plugins and build a new <code>ckeditor.js</code>.
Replace the existing <code>ckeditor.js</code> (<code>src/resources</code> folder) and edit the <code>removePlugins</code> array in <code>config.js</code> to activate the added plugins.

## Bundle overview

### org.eclipse.rap.addons.ckeditor

The Widget itself (<code>com.eclipsesource.widgets.ckeditor.CKEditor</code>) and the required resources.

### org.eclipse.rap.addons.ckeditor.demo

A demo application for the widget.
Contains a launch configuration.

### org.eclipse.rap.demo.ckeditor.

An addition to the RAP Examples Demo.

### org.eclipse.rap.addons.ckeditor.test

JUnit an Jasmine Tests.

## Legal

=== License ===

All classes are published under the terms of the Eclipse Public License v1.0
