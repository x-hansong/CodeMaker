package com.xiaohansong.codemaker;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.xiaohansong.codemaker.util.CodeMakerUtil;

import java.util.List;

/**
 * @author hansong.xhs
 * @version $Id: ClassEntry.java, v 0.1 2017-01-22 9:53 hansong.xhs Exp $$
 */
public class ClassEntry {

    private String       className;

    private String       packageName;

    private List<String> importList;

    private List<Field>  fields;

    private List<Method> methods;

    /**
     * Getter method for property <tt>fields</tt>.
     *
     * @return property value of fields
     */
    public List<Field> getFields() {
        return fields;
    }

    /**
     * Setter method for property <tt>fields</tt>.
     *
     * @param fields value to be assigned to property fields
     */
    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    /**
     * Getter method for property <tt>methods</tt>.
     *
     * @return property value of methods
     */
    public List<Method> getMethods() {
        return methods;
    }

    /**
     * Setter method for property <tt>methods</tt>.
     *
     * @param methods value to be assigned to property methods
     */
    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    public static class Method {
        private String name;
        private String modifier;
        private String returnType;
        private String params;

        public Method(String name, String modifier, String returnType, String params) {
            this.name = name;
            this.modifier = modifier;
            this.returnType = returnType;
            this.params = params;
        }

        /**
         * Getter method for property <tt>name</tt>.
         *
         * @return property value of name
         */
        public String getName() {
            return name;
        }

        /**
         * Setter method for property <tt>name</tt>.
         *
         * @param name value to be assigned to property name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Getter method for property <tt>modifier</tt>.
         *
         * @return property value of modifier
         */
        public String getModifier() {
            return modifier;
        }

        /**
         * Setter method for property <tt>modifier</tt>.
         *
         * @param modifier value to be assigned to property modifier
         */
        public void setModifier(String modifier) {
            this.modifier = modifier;
        }

        /**
         * Getter method for property <tt>returnType</tt>.
         *
         * @return property value of returnType
         */
        public String getReturnType() {
            return returnType;
        }

        /**
         * Setter method for property <tt>returnType</tt>.
         *
         * @param returnType value to be assigned to property returnType
         */
        public void setReturnType(String returnType) {
            this.returnType = returnType;
        }

        /**
         * Getter method for property <tt>params</tt>.
         *
         * @return property value of params
         */
        public String getParams() {
            return params;
        }

        /**
         * Setter method for property <tt>params</tt>.
         *
         * @param params value to be assigned to property params
         */
        public void setParams(String params) {
            this.params = params;
        }
    }

    public static class Field {
        private String type;
        private String name;
        private String modifier;

        public Field(String type, String name, String modifier) {
            this.type = type;
            this.name = name;
            this.modifier = modifier;
        }

        /**
         * Getter method for property <tt>type</tt>.
         *
         * @return property value of type
         */
        public String getType() {
            return type;
        }

        /**
         * Setter method for property <tt>type</tt>.
         *
         * @param type value to be assigned to property type
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * Getter method for property <tt>name</tt>.
         *
         * @return property value of name
         */
        public String getName() {
            return name;
        }

        /**
         * Setter method for property <tt>name</tt>.
         *
         * @param name value to be assigned to property name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Getter method for property <tt>modifier</tt>.
         *
         * @return property value of modifier
         */
        public String getModifier() {
            return modifier;
        }

        /**
         * Setter method for property <tt>modifier</tt>.
         *
         * @param modifier value to be assigned to property modifier
         */
        public void setModifier(String modifier) {
            this.modifier = modifier;
        }
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
        classEntry.setMethods(CodeMakerUtil.getMethods(psiClass));
        return classEntry;
    }

    /**
     * Getter method for property <tt>className</tt>.
     *
     * @return property value of className
     */
    public String getClassName() {
        return className;
    }

    /**
     * Setter method for property <tt>className</tt>.
     *
     * @param className value to be assigned to property className
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Getter method for property <tt>packageName</tt>.
     *
     * @return property value of packageName
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Setter method for property <tt>packageName</tt>.
     *
     * @param packageName value to be assigned to property packageName
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Getter method for property <tt>importList</tt>.
     *
     * @return property value of importList
     */
    public List<String> getImportList() {
        return importList;
    }

    /**
     * Setter method for property <tt>importList</tt>.
     *
     * @param importList value to be assigned to property importList
     */
    public void setImportList(List<String> importList) {
        this.importList = importList;
    }

}
