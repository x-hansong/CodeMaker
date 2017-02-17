package com.xiaohansong.codemaker;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hansong.xhs
 * @version $Id: CodeMakerSettings.java, v 0.1 2017-01-28 9:30 hansong.xhs Exp $$
 */
@State(name = "CodeMakerSettings", storages = { @Storage(id = "app-default", file = "$APP_CONFIG$/CodeMaker-settings.xml") })
public class CodeMakerSettings implements PersistentStateComponent<CodeMakerSettings> {

    public final static String MODEL     = "########################################################################################\n"
                                           + "##\n"
                                           + "## Common variables:\n"
                                           + "##  $YEAR - yyyy\n"
                                           + "##  $TIME - yyyy-MM-dd HH:mm:ss\n"
                                           + "##  $USER - user.name\n"
                                           + "##\n"
                                           + "## Available variables:\n"
                                           + "##  $class0 - the context class\n"
                                           + "##  $class1 - the selected class, like $class2, $class2\n"
                                           + "##  $ClassName - generate by the config of \"Class Name\", the generated class name\n"
                                           + "##\n"
                                           + "## Class Entry Structure:\n"
                                           + "##  $class0.className - the class Name\n"
                                           + "##  $class0.packageName - the packageName\n"
                                           + "##  $class0.importList - the list of imported classes name\n"
                                           + "##  $class0.fields - the list of the class fields\n"
                                           + "##          - type: the field type\n"
                                           + "##          - name: the field name\n"
                                           + "##          - modifier: the field modifier, like \"private\"\n"
                                           + "##  $class0.methods - the list of class methods\n"
                                           + "##          - name: the method name\n"
                                           + "##          - modifier: the method modifier, like \"private static\"\n"
                                           + "##          - returnType: the method returnType\n"
                                           + "##          - params: the method params, like \"(String name)\"\n"
                                           + "##\n"
                                           + "########################################################################################\n"
                                           + "package $class0.PackageName;\n"
                                           + "\n"
                                           + "#foreach($importer in $class0.ImportList)\n"
                                           + "import $importer;\n"
                                           + "#end\n"
                                           + "import lombok.Getter;\n"
                                           + "import lombok.Setter;\n"
                                           + "\n"
                                           + "/**\n"
                                           + " *\n"
                                           + " * @author $USER\n"
                                           + " * @version $Id: ${ClassName}.java, v 0.1 $TIME $USER Exp $$\n"
                                           + " */\n"
                                           + "class $ClassName {\n"
                                           + "\n"
                                           + "#foreach($field in $class0.Fields)\n"
                                           + "    /**\n"
                                           + "     *\n"
                                           + "     */\n"
                                           + "    @Getter\n"
                                           + "    @Setter\n"
                                           + "    private $field.Type $field.Name;\n"
                                           + "\n"
                                           + "#end\n" + "\n" + "}\n";

    public final static String CONVERTER = "########################################################################################\n"
                                           + "##\n"
                                           + "## Common variables:\n"
                                           + "##  $YEAR - yyyy\n"
                                           + "##  $TIME - yyyy-MM-dd HH:mm:ss\n"
                                           + "##  $USER - user.name\n"
                                           + "##\n"
                                           + "## Available variables:\n"
                                           + "##  $class0 - the context class\n"
                                           + "##  $class1 - the selected class, like $class2, $class2\n"
                                           + "##  $ClassName - generate by the config of \"Class Name\", the generated class name\n"
                                           + "##\n"
                                           + "## Class Entry Structure:\n"
                                           + "##  $class0.className - the class Name\n"
                                           + "##  $class0.packageName - the packageName\n"
                                           + "##  $class0.importList - the list of imported classes name\n"
                                           + "##  $class0.fields - the list of the class fields\n"
                                           + "##          - type: the field type\n"
                                           + "##          - name: the field name\n"
                                           + "##          - modifier: the field modifier, like \"private\"\n"
                                           + "##  $class0.methods - the list of class methods\n"
                                           + "##          - name: the method name\n"
                                           + "##          - modifier: the method modifier, like \"private static\"\n"
                                           + "##          - returnType: the method returnType\n"
                                           + "##          - params: the method params, like \"(String name)\"\n"
                                           + "##\n"
                                           + "########################################################################################\n"
                                           + "#macro (cap $strIn)$strIn.valueOf($strIn.charAt(0)).toUpperCase()$strIn.substring(1)#end\n"
                                           + "#macro (low $strIn)$strIn.valueOf($strIn.charAt(0)).toLowerCase()$strIn.substring(1)#end\n"
                                           + "#set($class0Var = \"#low(${class0.ClassName})\")\n"
                                           + "#set($class1Var = \"#low(${class1.ClassName})\")\n"
                                           + "package $class0.PackageName;\n"
                                           + "\n"
                                           + "#foreach($importer in $class0.ImportList)\n"
                                           + "import $importer;\n"
                                           + "#end\n"
                                           + "\n"
                                           + "/**\n"
                                           + " *\n"
                                           + " * @author $USER\n"
                                           + " * @version $Id: ${ClassName}.java, v 0.1 $TIME $USER Exp $$\n"
                                           + " */\n"
                                           + "class $ClassName {\n"
                                           + "\n"
                                           + "    /**\n"
                                           + "     * Convert ${class1.ClassName} to ${class0.ClassName}\n"
                                           + "     * @param ${class1Var}\n"
                                           + "     * @return\n"
                                           + "     */\n"
                                           + "    public static $class0.ClassName convertTo${class0.ClassName}($class1.ClassName #low($class1.ClassName)) {\n"
                                           + "        if (${class1Var} == null) {\n"
                                           + "            return null;\n"
                                           + "        }\n"
                                           + "        $class0.ClassName ${class0Var} = new ${class0.ClassName}();\n"
                                           + "\n"
                                           + "#foreach($field in $class0.Fields)\n"
                                           + "#if( $field.modifier.equals(\"private\"))\n"
                                           + "        ${class0Var}.set#cap($field.Name)(${class1Var}.get#cap($field.Name)());\n"
                                           + "#end\n"
                                           + "#end\n"
                                           + "\n"
                                           + "        return ${class0Var};\n"
                                           + "    }\n"
                                           + "\n"
                                           + "    /**\n"
                                           + "     * Convert ${class0.ClassName} to ${class1.ClassName}\n"
                                           + "     * @param ${class0Var}\n"
                                           + "     * @return\n"
                                           + "     */\n"
                                           + "    public static $class1.ClassName convertTo${class1.ClassName}($class0.ClassName #low($class0.ClassName)) {\n"
                                           + "        if (${class0Var} == null) {\n"
                                           + "            return null;\n"
                                           + "        }\n"
                                           + "        $class1.ClassName ${class1Var} = new ${class1.ClassName}();\n"
                                           + "\n"
                                           + "#foreach($field in $class1.Fields)\n"
                                           + "#if( $field.modifier.equals(\"private\"))\n"
                                           + "        ${class1Var}.set#cap($field.Name)(${class0Var}.get#cap($field.Name)());\n"
                                           + "#end\n"
                                           + "#end\n"
                                           + "\n"
                                           + "        return ${class1Var};\n" + "    }\n" + "}\n";

    public CodeMakerSettings() {
        loadDefaultSettings();
    }

    public void loadDefaultSettings() {
        Map<String, CodeTemplate> codeTemplates = new HashMap<>();
        codeTemplates.put("Converter", new CodeTemplate("Converter",
            "${class0.className}Converter", CONVERTER, 2));
        codeTemplates.put("Model", new CodeTemplate("Model",
            "#set($end = ${class0.className.length()} - 2)${class0.className.substring(0,${end})}",
            MODEL, 1));
        this.codeTemplates = codeTemplates;
    }

    private Map<String, CodeTemplate> codeTemplates;

    @Nullable
    @Override
    public CodeMakerSettings getState() {
        return this;
    }

    @Override
    public void loadState(CodeMakerSettings codeMakerSettings) {
        XmlSerializerUtil.copyBean(codeMakerSettings, this);
    }

    public CodeTemplate getCodeTemplate(String template) {
        return codeTemplates.get(template);
    }

    public void removeCodeTemplate(String template) {
        codeTemplates.remove(template);
    }

    /**
     * Getter method for property <tt>codeTemplates</tt>.
     *
     * @return property value of codeTemplates
     */
    public Map<String, CodeTemplate> getCodeTemplates() {
        return codeTemplates;
    }

    /**
     * Setter method for property <tt>codeTemplates</tt>.
     *
     * @param codeTemplates value to be assigned to property codeTemplates
     */
    public void setCodeTemplates(Map<String, CodeTemplate> codeTemplates) {
        this.codeTemplates = codeTemplates;
    }
}
