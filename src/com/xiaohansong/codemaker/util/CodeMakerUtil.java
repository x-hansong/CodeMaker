package com.xiaohansong.codemaker.util;

import static scala.collection.JavaConversions.seqAsJavaList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scala.lang.psi.api.ScalaFile;
import org.jetbrains.plugins.scala.lang.psi.api.statements.params.ScClassParameter;
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.imports.ScImportStmt;
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.typedef.ScClass;

import com.google.common.collect.Lists;
import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import com.xiaohansong.codemaker.ClassEntry;

/**
 * @author hansong.xhs
 * @version $Id: CodeMakerUtil.java, v 0.1 2017-01-20 10:15 hansong.xhs Exp $$
 */
public class CodeMakerUtil {
    private static final Logger LOGGER = Logger.getInstance(CodeMakerUtil.class);

    public static Logger getLogger(Class clazz) {
        return Logger.getInstance(clazz);
    }

    public static PsiClass chooseClass(Project project, PsiClass defaultClass) {
        TreeClassChooser chooser = TreeClassChooserFactory.getInstance(project)
            .createProjectScopeChooser("Select a class", defaultClass);

        chooser.showDialog();

        return chooser.getSelected();
    }

    public static String getSourcePath(PsiClass clazz) {
        PsiFile containingFile = clazz.getContainingFile();
        return getSourcePath(containingFile);
    }

    @NotNull
    public static String getSourcePath(PsiFile psiFile) {
        String classPath = psiFile.getVirtualFile().getPath();
        return classPath.substring(0, classPath.lastIndexOf('/'));
    }

    public static String generateClassPath(String sourcePath, String className) {
        return generateClassPath(sourcePath, className, "java");
    }

    public static String generateClassPath(String sourcePath, String className, String extension) {
        return sourcePath + "/" + className + "." + extension;
    }

    public static List<String> getImportList(PsiJavaFile javaFile) {
        PsiImportList importList = javaFile.getImportList();
        if (importList == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(importList.getImportStatements())
            .map(PsiImportStatement::getQualifiedName).collect(Collectors.toList());
    }

    public static List<String> getScalaImportList(ScalaFile scalaFile) {
        List<ScImportStmt> scImportStmts = seqAsJavaList(scalaFile.importStatementsInHeader());
        return scImportStmts.stream()
            .flatMap(stmt -> seqAsJavaList(stmt.importExprs()).stream().map(expr -> expr.getText()))
            .collect(Collectors.toList());
    }

    public static List<ClassEntry.Field> getFields(PsiClass psiClass) {
        return Arrays.stream(psiClass.getFields())
            .map(psiField -> new ClassEntry.Field(psiField.getType().getPresentableText(),
                psiField.getName(),
                psiField.getModifierList() == null ? "" : psiField.getModifierList().getText(),
                getDocCommentText(psiField)))
            .collect(Collectors.toList());
    }

    public static String getDocCommentText(PsiField psiField) {
        if (psiField.getDocComment() == null) {
            return "";
        }
        StringBuilder content = new StringBuilder();
        for (PsiElement element : psiField.getDocComment().getDescriptionElements()) {
            content.append(element.getText());
        }
        return content.toString();
    }

    public static List<ClassEntry.Field> getAllFields(PsiClass psiClass) {
        return Arrays.stream(psiClass.getAllFields())
            .map(psiField -> new ClassEntry.Field(psiField.getType().getPresentableText(),
                psiField.getName(),
                psiField.getModifierList() == null ? "" : psiField.getModifierList().getText(),
                getDocCommentText(psiField)))
            .collect(Collectors.toList());
    }

    public static List<ClassEntry.Method> getMethods(PsiClass psiClass) {
        return Arrays.stream(psiClass.getMethods()).map(psiMethod -> {
            String returnType = psiMethod.getReturnType() == null ? ""
                : psiMethod.getReturnType().getPresentableText();
            return new ClassEntry.Method(psiMethod.getName(), psiMethod.getModifierList().getText(),
                returnType, psiMethod.getParameterList().getText());
        }).collect(Collectors.toList());
    }

    public static List<ClassEntry.Method> getAllMethods(PsiClass psiClass) {
        return Arrays.stream(psiClass.getAllMethods()).map(psiMethod -> {
            String returnType = psiMethod.getReturnType() == null ? ""
                : psiMethod.getReturnType().getPresentableText();
            return new ClassEntry.Method(psiMethod.getName(), psiMethod.getModifierList().getText(),
                returnType, psiMethod.getParameterList().getText());
        }).collect(Collectors.toList());
    }

    /**
     * find the method belong to  name
     * @return null if not found
     */
    public static String findClassNameOfSuperMethod(PsiMethod psiMethod) {
        PsiMethod[] superMethods = psiMethod.findDeepestSuperMethods();
        if (superMethods.length == 0 || superMethods[0].getContainingClass() == null) {
            return null;
        }
        return superMethods[0].getContainingClass().getQualifiedName();
    }

    /**
     * Gets all classes in the element.
     *
     * @param element the Element
     * @return the Classes
     */
    public static List<PsiClass> getClasses(PsiElement element) {
        List<PsiClass> elements = Lists.newArrayList();
        List<PsiClass> classElements = PsiTreeUtil.getChildrenOfTypeAsList(element, PsiClass.class);
        elements.addAll(classElements);
        for (PsiClass classElement : classElements) {
            elements.addAll(getClasses(classElement));
        }
        return elements;
    }

    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length();

        for (int i = 0; i < length; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static List<ClassEntry.Field> getScalaClassFields(ScClass scalaClass) {
        return seqAsJavaList(scalaClass.allVals()).stream()
            .filter(tuple -> tuple._1() instanceof ScClassParameter).map(tuple -> {
                ScClassParameter val = (ScClassParameter) tuple._1();
                return new ClassEntry.Field(val.paramType().get().getText(), val.name(),
                    val.getModifierList().getText(), "");
            }).collect(Collectors.toList());
    }

    public static List<String> getClassTypeParameters(PsiClass psiClass) {
        return Arrays.stream(psiClass.getTypeParameters()).map(PsiNamedElement::getName)
            .collect(Collectors.toList());
    }

    public static List<Character> markdownChars = Lists.newArrayList('<', '>', '`', '*', '_', '{',
        '}', '[', ']', '(', ')', '#', '+', '-', '.', '!');

    public static String escapeMarkdown(String str) {
        StringBuilder result = new StringBuilder();
        for (char ch : str.toCharArray()) {
            if (markdownChars.contains(ch)) {
                result.append('\\');
            }
            result.append(ch);
        }
        return result.toString();
    }

    public static int findJavaDocTextOffset(PsiElement theElement) {
        PsiElement javadocElement = theElement.getFirstChild();
        if (!(javadocElement instanceof PsiDocComment)) {
            throw new IllegalStateException("Cannot find element of type PsiDocComment");
        }
        return javadocElement.getTextOffset();
    }

    public static int findJavaCodeTextOffset(PsiElement theElement) {
        if (theElement.getChildren().length < 2) {
            throw new IllegalStateException("Can not find offset of java code");
        }
        return theElement.getChildren()[1].getTextOffset();
    }

    /**
     * save the current change
     * @param element
     */
    public static void pushPostponedChanges(PsiElement element) {
        Editor editor = PsiUtilBase.findEditor(element.getContainingFile());
        if (editor != null) {
            PsiDocumentManager.getInstance(element.getProject())
                .doPostponedOperationsAndUnblockDocument(editor.getDocument());
        }
    }

    public static void reformatJavaDoc(PsiElement theElement) {
        CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(theElement.getProject());
        try {
            int javadocTextOffset = findJavaDocTextOffset(theElement);
            int javaCodeTextOffset = findJavaCodeTextOffset(theElement);
            codeStyleManager.reformatText(theElement.getContainingFile(), javadocTextOffset,
                javaCodeTextOffset + 1);
        } catch (Exception e) {
            LOGGER.error("reformat code failed", e);
        }
    }

    public static void reformatJavaFile(PsiElement theElement) {
        CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(theElement.getProject());
        try {
            codeStyleManager.reformat(theElement);
        } catch (Exception e) {
            LOGGER.error("reformat code failed", e);
        }
    }
}
