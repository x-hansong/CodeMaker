package com.xiaohansong.codemaker;

import java.util.List;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.xiaohansong.codemaker.util.CodeMakerUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author hansong.xhs
 * @version $Id: ClassEntry.java, v 0.1 2017-01-22 9:53 hansong.xhs Exp $$
 */
@Data
@AllArgsConstructor
public class ClassEntry {

    private String className;

    private String packageName;

    private List<String> importList;

    private List<Field> fields;

    private List<Field> allFields;

    private List<Method> methods;

    private List<Method> allMethods;

    @Data
    @AllArgsConstructor
    public static class Method {
        /**
         * method name
         */
        private String name;

        /**
         * the method modifier, like "private",or "@Setter private" if include annotations
         */
        private String modifier;

        /**
         * the method returnType
         */
        private String returnType;

        /**
         * the method params, like "(String name)"
         */
        private String params;

    }

    @Data
    @AllArgsConstructor
    public static class Field {
        /**
         * field type
         */
        private String type;

        /**
         * field name
         */
        private String name;

        /**
         * the field modifier, like "private",or "@Setter private" if include annotations
         */
        private String modifier;

    }

    private ClassEntry() {

    }

    public static ClassEntry create(PsiClass psiClass) {
        PsiJavaFile javaFile = (PsiJavaFile) psiClass.getContainingFile();
        ClassEntry classEntry = new ClassEntry();
        classEntry.setClassName(psiClass.getName());
        classEntry.setPackageName(javaFile.getPackageName());
        classEntry.setImportList(CodeMakerUtil.getImportList(javaFile));
        classEntry.setFields(CodeMakerUtil.getFields(psiClass));
        classEntry.setAllFields(CodeMakerUtil.getAllFields(psiClass));
        classEntry.setMethods(CodeMakerUtil.getMethods(psiClass));
        classEntry.setAllMethods(CodeMakerUtil.getAllMethods(psiClass));
        return classEntry;
    }

}
