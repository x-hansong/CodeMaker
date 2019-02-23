package com.xiaohansong.codemaker;

import static com.xiaohansong.codemaker.util.CodeMakerUtil.pushPostponedChanges;
import static com.xiaohansong.codemaker.util.CodeMakerUtil.reformatJavaDoc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.javadoc.PsiDocComment;

/**
 *
 * @author hansong.xhs
 * @version $Id: DocWriteAction.java, v 0.1 2017-03-27 11:04 hansong.xhs Exp $
 */
public class DocWriteAction extends WriteCommandAction {
    private static final Logger LOGGER = Logger.getInstance(DocWriteAction.class);

    private PsiDocComment       psiDocComment;

    private PsiElement          psiElement;

    public DocWriteAction(@Nullable Project project, PsiDocComment psiDocComment,
                          PsiElement psiElement, PsiFile... files) {
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
        psiElement.getNode().addChild(psiDocComment.getNode(),
            psiElement.getFirstChild().getNode());
        reformatJavaDoc(psiElement);
    }

}