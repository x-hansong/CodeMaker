package com.xiaohansong.codemaker;

import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.util.PsiUtilBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author hansong.xhs
 * @version $Id: DocWriteAction.java, v 0.1 2017-03-27 11:04 hansong.xhs Exp $
 */
public class DocWriteAction extends WriteCommandAction {
    private static final Logger LOGGER = Logger.getInstance(DocWriteAction.class);

    private PsiDocComment psiDocComment;

    private PsiElement psiElement;

    public DocWriteAction(@Nullable Project project,
                          PsiDocComment psiDocComment, PsiElement psiElement, PsiFile... files) {
        super(project, files);
        this.psiDocComment = psiDocComment;
        this.psiElement = psiElement;
    }

    /**
     * @see com.intellij.openapi.application.BaseActionRunnable#run(com.intellij.openapi.application.Result)
     */
    @Override
    protected void run(@NotNull Result result) throws Throwable {
        pushPostponedChanges(psiElement);
        psiElement.getNode().addChild(psiDocComment.getNode(), psiElement.getFirstChild().getNode());
        reformatJavaDoc(psiElement);
    }

    private static void reformatJavaDoc(PsiElement theElement) {
        CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(theElement.getProject());
        try {
            int javadocTextOffset = findJavaDocTextOffset(theElement);
            int javaCodeTextOffset = findJavaCodeTextOffset(theElement);
            codeStyleManager.reformatText(theElement.getContainingFile(), javadocTextOffset, javaCodeTextOffset + 1);
        } catch (Exception e) {
            LOGGER.error("reformat code failed", e);
        }
    }

    private static int findJavaDocTextOffset(PsiElement theElement) {
        PsiElement javadocElement = theElement.getFirstChild();
        if (!(javadocElement instanceof PsiDocComment)) {
            throw new IllegalStateException("Cannot find element of type PsiDocComment");
        }
        return javadocElement.getTextOffset();
    }

    private static int findJavaCodeTextOffset(PsiElement theElement) {
        if (theElement.getChildren().length < 2) {
            throw new IllegalStateException("Can not find offset of java code");
        }
        return theElement.getChildren()[1].getTextOffset();
    }

    /**
     * save the current change
     * @param element
     */
    private static void pushPostponedChanges(PsiElement element) {
        Editor editor = PsiUtilBase.findEditor(element.getContainingFile());
        if (editor != null) {
            PsiDocumentManager.getInstance(element.getProject())
                    .doPostponedOperationsAndUnblockDocument(editor.getDocument());
        }
    }

}