<%/*
  Generates java code that creates a ClassEntry that can be used as test input
  */%>
import ClassEntry;
import ClassEntry.Field;
import ClassEntry.Method;

import java.util.ArrayList;

import static com.google.common.collect.Lists.newArrayList;

public class ExampleInput {
    final ArrayList<Field> fields = newArrayList(<% clazz.fields.eachWithIndex { field, index -> %>
         new Field("${field.type}", "${field.name}", "${field.modifier}", "${field.comment}")<% if(index < clazz.fields.size - 1) { %>,<% }%><% } %>
    );
    final ArrayList<Field> allFields = newArrayList(<% clazz.allFields.eachWithIndex { field, index -> %>
           new Field("${field.type}", "${field.name}", "${field.modifier}", "${field.comment}")<% if(index < clazz.allFields.size - 1) { %>,<% } %><% } %>
    );
    final ArrayList<Method> methods = newArrayList(<% clazz.methods.eachWithIndex { method, index -> %>
           new Method("${method.name}", "${method.modifier}", "${method.returnType}", "${method.params}")<% if(index < clazz.methods.size - 1) { %>,<% } %> <% } %>
    );
    final ArrayList<Method> allMethods = newArrayList(<% clazz.allMethods.eachWithIndex { method, index -> %>
           new Method("${method.name}", "${method.modifier}", "${method.returnType}", "${method.params}")<% if(index < clazz.allMethods.size - 1) { %>,<% } %><% } %>
    );
    final ArrayList<String> imports = newArrayList(<% clazz.importList.eachWithIndex { imp, index -> %>
           "${imp}"<% if(index < clazz.importList.size - 1) { %>,<% } %><% } %>
    );
    final ArrayList<String> typeParams = newArrayList(<% clazz.typeParams.eachWithIndex { tp, index -> %>
           "${tp}"<% if(index < clazz.typeParams.size - 1) { %>,<% } %><% } %>
    );
    return new ClassEntry("${ClassName}",
        "${clazz.packageName}",
        imports,
        fields,
        allFields,
        methods,
        allMethods,
        newArrayList(typeParams)
    );
}
