package com.xiaohansong.codemaker;

import static com.xiaohansong.codemaker.util.CodeMakerUtil.pushPostponedChanges;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.xiaohansong.codemaker.util.CodeMakerUtil;

/**
 *
 * @author hansong.xhs
 * @version $Id: DocWriteAction.java, v 0.1 2017-03-27 11:04 hansong.xhs Exp $
 */
public class FieldWriteAction extends WriteCommandAction {

    private PsiElement psiElement;

    private PsiField   psiField;

    public FieldWriteAction(@Nullable Project project, PsiElement psiElement, PsiField psiField,
                            PsiFile... files) {
        super(project, files);
        this.psiElement = psiElement;
        this.psiField = psiField;
    }

    /**
     * @see com.intellij.openapi.application.BaseActionRunnable#run(Result)
     */
    @Override
    protected void run(@NotNull Result result) throws Throwable {
        pushPostponedChanges(psiElement);
        psiElement.getNode().addChild(psiField.getNode(), psiElement.getLastChild().getNode());
        CodeMakerUtil.reformatJavaFile(psiElement);
    }

}