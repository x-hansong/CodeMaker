package com.xiaohansong.codemaker.templates.input;

import com.xiaohansong.codemaker.ClassEntry;
import com.xiaohansong.codemaker.ClassEntry.Field;
import com.xiaohansong.codemaker.ClassEntry.Method;

import java.util.ArrayList;

import static com.google.common.collect.Lists.newArrayList;

public class ScalaCaseClass implements TestInput {
    @Override
    public String getId() {
        return "scala-case-class";
    }

    @Override
    public String getName() {
        return "Scala case class";
    }

    @Override
    public String getSource(String className) {
        return "package test\n" +
                "\n" +
                "import java.time.Instant\n" +
                "\n" +
                "case class "+className+"[T] (firstName: String, middleName: Option[String], lastName: String, address: Address[T], dob: Instant)\n";
    }

    @Override
    public String getLanguage() {
        return "scala";
    }

    @Override
    public ClassEntry createInput(String className) {
        final ArrayList<Field> fields = newArrayList(
                new Field("String", "lastName", "", ""),
                new Field("Address[T]", "address", "", ""),
                new Field("String", "firstName", "", ""),
                new Field("Instant", "dob", "", ""),
                new Field("Option[String]", "middleName", "", "")

        );
        final ArrayList<Field> allFields = newArrayList();
        final ArrayList<Method> methods = newArrayList(
                new Method("Person", "public", "", "(String firstName, Option<String> middleName, String lastName, Address<T> address, Instant dob)"),
                new Method("lastName", "public", "String", "()"),
                new Method("address", "public", "Address<T>", "()"),
                new Method("productIterator", "public", "Iterator<Object>", "()"),
                new Method("productPrefix", "public", "String", "()"),
                new Method("firstName", "public", "String", "()"),
                new Method("dob", "public", "Instant", "()"),
                new Method("middleName", "public", "Option<String>", "()"),
                new Method("copy", "public", "Person<T>", "(String firstName, Option<String> middleName, String lastName, Address<T> address, Instant dob)"),
                new Method("copy$default$1", "public", "String", "()"),
                new Method("copy$default$2", "public", "Option<String>", "()"),
                new Method("copy$default$3", "public", "String", "()"),
                new Method("copy$default$4", "public", "Address<T>", "()"),
                new Method("copy$default$5", "public", "Instant", "()"),
                new Method("apply", "static public", "Person<T>", "(String firstName, Option<String> middleName, String lastName, Address<T> address, Instant dob)"),
                new Method("unapply", "static public", "Option<Tuple5<String, Option<String>, String, Address<T>, Instant>>", "(Person<T> x$0)")

        );
        final ArrayList<Method> allMethods = newArrayList(
                new Method("Person", "public", "", "(String firstName, Option<String> middleName, String lastName, Address<T> address, Instant dob)"),
                new Method("lastName", "public", "String", "()"),
                new Method("getClass", "public final native", "Class<?>", "()"),
                new Method("wait", "public final", "void", "()"),
                new Method("wait", "public final native", "void", "(long l)"),
                new Method("wait", "public final", "void", "(long l, int i)"),
                new Method("canEqual", "public", "boolean", "(Object that)"),
                new Method("address", "public", "Address<T>", "()"),
                new Method("productElement", "public", "Object", "(int n)"),
                new Method("productIterator", "public", "Iterator<Object>", "()"),
                new Method("notifyAll", "public final native", "void", "()"),
                new Method("notify", "public final native", "void", "()"),
                new Method("productPrefix", "public", "String", "()"),
                new Method("firstName", "public", "String", "()"),
                new Method("dob", "public", "Instant", "()"),
                new Method("hashCode", "public native", "int", "()"),
                new Method("equals", "public", "boolean", "(java.lang.Object o)"),
                new Method("clone", "protected native", "Object", "()"),
                new Method("middleName", "public", "Option<String>", "()"),
                new Method("toString", "public", "String", "()"),
                new Method("finalize", "protected", "void", "()"),
                new Method("copy", "public", "Person<T>", "(String firstName, Option<String> middleName, String lastName, Address<T> address, Instant dob)"),
                new Method("copy$default$1", "public", "String", "()"),
                new Method("copy$default$2", "public", "Option<String>", "()"),
                new Method("copy$default$3", "public", "String", "()"),
                new Method("copy$default$4", "public", "Address<T>", "()"),
                new Method("copy$default$5", "public", "Instant", "()"),
                new Method("productArity", "public", "int", "()"),
                new Method("apply", "static public", "Person<T>", "(String firstName, Option<String> middleName, String lastName, Address<T> address, Instant dob)"),
                new Method("unapply", "static public", "Option<Tuple5<String, Option<String>, String, Address<T>, Instant>>", "(Person<T> x$0)")
        );

        final ArrayList<String> imports = newArrayList("java.time.Instant");
        final ArrayList<String> typeParams = newArrayList("T");
        return new ClassEntry(className,
                "test",
                imports,
                fields,
                allFields,
                methods,
                allMethods,
                newArrayList(typeParams));
    }

}
