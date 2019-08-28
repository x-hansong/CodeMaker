package com.xiaohansong.codemaker.templates.input;

import com.xiaohansong.codemaker.ClassEntry;
import com.xiaohansong.codemaker.ClassEntry.Field;
import com.xiaohansong.codemaker.ClassEntry.Method;

import java.util.ArrayList;

import static com.google.common.collect.Lists.newArrayList;

public class JavaClass implements TestInput {
    @Override
    public String getId() {
        return "java-class";
    }

    @Override
    public String getName() {
        return "Java class";
    }

    @Override
    public String getSource(String className) {
        return "package test;\n" +
                "\n" +
                "import java.time.Instant;\n" +
                "\n" +
                "public class "+ className + "<T> {\n" +
                "    private String stringField;\n" +
                "    private int intField;\n" +
                "    private boolean booleanField;\n" +
                "    private Instant instant;\n" +
                "\n" +
                "    public JavaClass"+ className +"(String stringField, int intField, boolean booleanField, Instant instant) {\n" +
                "        this.stringField = stringField;\n" +
                "        this.intField = intField;\n" +
                "        this.booleanField = booleanField;\n" +
                "        this.instant = instant;\n" +
                "    }\n" +
                "\n" +
                "    public String getStringField() {\n" +
                "        return stringField;\n" +
                "    }\n" +
                "\n" +
                "    public void setStringField(String stringField) {\n" +
                "        this.stringField = stringField;\n" +
                "    }\n" +
                "\n" +
                "    public int getIntField() {\n" +
                "        return intField;\n" +
                "    }\n" +
                "\n" +
                "    public void setIntField(int intField) {\n" +
                "        this.intField = intField;\n" +
                "    }\n" +
                "\n" +
                "    public boolean isBooleanField() {\n" +
                "        return booleanField;\n" +
                "    }\n" +
                "\n" +
                "    public void setBooleanField(boolean booleanField) {\n" +
                "        this.booleanField = booleanField;\n" +
                "    }\n" +
                "\n" +
                "    public Instant getInstant() {\n" +
                "        return instant;\n" +
                "    }\n" +
                "\n" +
                "    public void setInstant(Instant instant) {\n" +
                "        this.instant = instant;\n" +
                "    }\n" +
                "}\n";
    }

    @Override
    public String getLanguage() {
        return "java";
    }

    @Override
    public ClassEntry createInput(String className) {
        final ArrayList<Field> fields = newArrayList(
                new Field("String", "stringField", "private", ""),
                new Field("int", "intField", "private", ""),
                new Field("boolean", "booleanField", "private", ""),
                new Field("Instant", "instant", "private", "")
        );
        final ArrayList<Field> allFields = newArrayList(
                new Field("String", "stringField", "private", ""),
                new Field("int", "intField", "private", ""),
                new Field("boolean", "booleanField", "private", ""),
                new Field("Instant", "instant", "private", "")
        );
        final ArrayList<Method> methods = newArrayList(
                new Method("JavaClass", "public", "", "(String stringField, int intField, boolean booleanField, Instant instant)"),
                new Method("getStringField", "public", "String", "()"),
                new Method("setStringField", "public", "void", "(String stringField)"),
                new Method("getIntField", "public", "int", "()"),
                new Method("setIntField", "public", "void", "(int intField)"),
                new Method("isBooleanField", "public", "boolean", "()"),
                new Method("setBooleanField", "public", "void", "(boolean booleanField)"),
                new Method("getInstant", "public", "Instant", "()"),
                new Method("setInstant", "public", "void", "(Instant instant)")
        );
        final ArrayList<Method> allMethods = newArrayList(
                new Method("JavaClass", "public", "", "(String stringField, int intField, boolean booleanField, Instant instant)"),
                new Method("getStringField", "public", "String", "()"),
                new Method("setStringField", "public", "void", "(String stringField)"),
                new Method("getIntField", "public", "int", "()"),
                new Method("setIntField", "public", "void", "(int intField)"),
                new Method("isBooleanField", "public", "boolean", "()"),
                new Method("setBooleanField", "public", "void", "(boolean booleanField)"),
                new Method("getInstant", "public", "Instant", "()"),
                new Method("setInstant", "public", "void", "(Instant instant)"),
                new Method("Object", "public", "", "()"),
                new Method("registerNatives", "private static native", "void", "()"),
                new Method("getClass", "public final native", "Class<?>", "()"),
                new Method("hashCode", "public native", "int", "()"),
                new Method("equals", "public", "boolean", "(java.lang.Object o)"),
                new Method("clone", "protected native", "Object", "()"),
                new Method("toString", "public", "String", "()"),
                new Method("notify", "public final native", "void", "()"),
                new Method("notifyAll", "public final native", "void", "()"),
                new Method("wait", "public final native", "void", "(long l)"),
                new Method("wait", "public final", "void", "(long l, int i)"),
                new Method("wait", "public final", "void", "()"),
                new Method("finalize", "protected", "void", "()")
        );
        final ArrayList<String> imports = newArrayList(
                "java.time.Instant"
        );
        final ArrayList<String> typeParams = newArrayList(
                "T"
        );
        return new ClassEntry(className,
                "test",
                imports,
                fields,
                allFields,
                methods,
                allMethods,
                newArrayList(typeParams)
        );
    }
}
