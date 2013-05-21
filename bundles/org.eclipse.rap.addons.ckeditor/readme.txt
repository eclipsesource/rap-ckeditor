CKEditor for RAP
================

This is a custom widget for the Remote Application Platform (RAP) that wraps the CKEditor[1], 
a web-based WYSIWYG/Rich-Text editor. This version is based on CKEditor 3.6.6.1[2] and requires
RAP 2.1 or later to run. Versions compatible with older RAP releases can be found on GitHub[3].

API
---
Currently, the API consists of the two methods "getText" and "setText".
The text can be any valid HTML, but should be limited to the subset the editor can handle.
The "setFont" method inherited from Widget is also supported to set the default font of the
document.


Bundle overview
---------------

* org.eclipse.rap.addons.ckeditor

The Widget itself (com.eclipsesource.widgets.ckeditor.CKEditor) and the required resources.

* org.eclipse.rap.addons.ckeditor.demo

A demo application for the widget. Contains a launch configuration.

* org.eclipse.rap.demo.ckeditor.

An addition to the RAP Examples Demo.

* org.eclipse.rap.addons.ckeditor.test

JUnit and Jasmine Tests.

* org.eclipse.rap.addons.ckeditor.build

The unmodified CKEditor source files and the ckeditor.pack used to create the included ckeditor.js.

Customization
-------------

The editor can be customized by editing the files in the "src/resources" folder of the 
org.eclipse.rap.addons.ckeditor bundle. You might need to clear the browsers cache and restart the 
server for all changes to take effect.

* Editor Configuration

Editing the file config.js lets you change the toolbar, language, and formatting options (fonts, 
colors). Be careful, all changes here bear the risk of breaking the editor.

* Editor Theming

To change the icons, edit or replace icons.png. To change the editors colors, borders, spacings, 
etc, edit editor.css. You can use a tool like Firebug to examine which CSS classes are used where 
in the editor.

* Advanced Customization

Some CKEditor plugins have been removed from the included ckeditor.js and disabled in config.js, 
therefore not all options of the full CKEdtior are enabled. If you wish, you can compile your own 
ckeditor.js. To do so, download the CKPackager[3] and place it in the 
org.eclipse.rap.addons.ckeditor.build bundle. You can then edit the ckeditor.pack file to add more 
CKEditor plugins and use it to with the packager to generate a new ckeditor.js. Replace the 
existing ckeditor.js (in "src/resources") and edit the "removePlugins" array in config.js to 
activate the added plugins.

Legal
-----

=== License ===

All classes are published under the terms of the Eclipse Public License v1.0

References
----------

[1] http://ckeditor.com/
[2] http://ckeditor.com/release/CKEditor-3.6.6.1
[3] https://github.com/eclipsesource/rap-ckeditor
[4] http://docs.cksource.com/CKEditor_3.x/Developers_Guide/CKPackager
