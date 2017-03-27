# CodeMaker
A idea-plugin for java to generate code, support custom code template.

![demonstration](http://7xjtfr.com1.z0.glb.clouddn.com/codemaker.gif)

This plugin generates code from right click 'Generate...' menu while focused on a java class. The output class can be customized using a provided velocity template to format the code based on the origin class and optional selected classes.</p>

The output class will be created at the source path of origin class. The following features are available: 

- Generate the model class for the persistent class.
- Generate the converter class for the model class and the persistent class.
- Support add custom template to generate more code.

# Install
Download [CodeMaker.zip](https://github.com/x-hansong/CodeMaker/releases/download/1.0/CodeMaker.zip)
Or search 'CodeMaker' in Idea plugins

To install a plugin from the disk in idea

1. Open the Settings/Preferences dialog box and select Plugins on the left pane.
2. On the right pane of the dialog, click the Install plugin from disk button.
3. In the dialog that opens, select the desired plugin. You can quickly locate and select the necessary file if you drag-and-drop the corresponding item from your file browser (Explorer, Finder, etc.) into the area where the tree is shown. Click OK to proceed.
4. Click Apply button of the Settings/Preferences dialog.
5. Following the system prompt that appears, restart IntelliJ IDEA to activate the installed plugin, or postpone it, at your choice.

# Usage
This plugin generates code from right click 'Generate...' menu while focused on a java class. 
![Generage](http://7xjtfr.com1.z0.glb.clouddn.com/codemaker0.png)

The plugin support multiple class to generate one code, you can set the class number of template to select multiple classes to use.

![select class](http://7xjtfr.com1.z0.glb.clouddn.com/codemaker1.png)

# Configuration
![configure](http://7xjtfr.com1.z0.glb.clouddn.com/codemaker3.png)
- **Add template**: click "Add Tempalte" button to add a template, after saving changes.
- **Delete template**: click "Delete Template" button to delete a template.

![property](http://7xjtfr.com1.z0.glb.clouddn.com/codemaker2.png)
- **classNumber**: The template context will contain the class which user select when trigger the generate action. for example, the number is 1, the template context only has the focused class: $class0; if the number is 2, when you trigger the generate action, you need select a class, then the template context will have two class entry: $class0, $class1
- **className**: The class name support velocity and the template context.

## Template Context
```
########################################################################################
##
## Common variables:
##  $YEAR - yyyy
##  $TIME - yyyy-MM-dd HH:mm:ss
##  $USER - user.name
##
## Available variables:
##  $class0 - the context class
##  $class1 - the selected class, like $class2, $class2
##  $ClassName - generate by the config of "Class Name", the generated class name
##
## Class Entry Structure:
##  $class0.className - the class Name
##  $class0.packageName - the packageName
##  $class0.importList - the list of imported classes name
##  $class0.fields - the list of the class fields
##          - type: the field type
##          - name: the field name
##          - modifier: the field modifier, like "private",or "@Setter private" if include annotations
##  $class0.allFields - the list of the class fields include all fields of superclass
##          - type: the field type
##          - name: the field name
##          - modifier: the field modifier, like "private",or "@Setter private" if include annotations
##  $class0.methods - the list of class methods
##          - name: the method name
##          - modifier: the method modifier, like "private static"
##          - returnType: the method returnType
##          - params: the method params, like "(String name)"
##  $class0.allMethods - the list of class methods include all methods of superclass
##          - name: the method name
##          - modifier: the method modifier, like "private static"
##          - returnType: the method returnType
##          - params: the method params, like "(String name)"#
########################################################################################
```

## More features
- Generate @see doc for override method
