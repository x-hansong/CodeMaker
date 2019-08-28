package com.xiaohansong.codemaker.action;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.RunResult;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.ReadonlyStatusHandler;
import com.intellij.openapi.vfs.ReadonlyStatusHandler.OperationStatus;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.util.PsiTreeUtil;
import com.xiaohansong.codemaker.DocWriteAction;
import com.xiaohansong.codemaker.util.CodeMakerUtil;
import com.xiaohansong.codemaker.util.VelocityUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Create a @see doc for the override method
 * @author hansong.xhs
 * @version $Id: DocGenerateAction.java, v 0.1 2017-03-24 9:42 hansong.xhs Exp $
 */
public class DocGenerateAction extends AnAction implements DumbAware {

    /**
     * The constant LOGGER.
     */
    private static final Logger LOGGER = Logger.getInstance(DocGenerateAction.class);

    /**
     * The seeDocTemplate.
     */
    private String seeDocTemplate;

    /**
     * Instantiates a new Doc generate action.
     */
    public DocGenerateAction() {
        super();
        try {
            this.seeDocTemplate = FileUtil.loadTextAndClose(DocGenerateAction.class.getResourceAsStream("/template/See.vm"));
        } catch (IOException e) {
            LOGGER.error("load See.vm failed", e);
        }
    }

    /**
     * @see com.intellij.openapi.actionSystem.AnAction#actionPerformed(com.intellij.openapi.actionSystem.AnActionEvent)
     */
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        DumbService dumbService = DumbService.getInstance(project);
        if (dumbService.isDumb()) {
            dumbService.showDumbModeNotification("CodeMaker plugin is not available during indexing");
            return;
        }
        PsiFile javaFile = e.getData(CommonDataKeys.PSI_FILE);
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (javaFile == null || editor == null) {
            return;
        }
        List<PsiClass> classes = CodeMakerUtil.getClasses(javaFile);
        PsiElementFactory psiElementFactory = PsiElementFactory.SERVICE.getInstance(project);
        for (PsiClass psiClass : classes) {
            for (PsiMethod psiMethod : PsiTreeUtil.getChildrenOfTypeAsList(psiClass, PsiMethod.class)) {
                createDocForMethod(psiMethod, psiElementFactory);
            }
        }

    }

    /**
     * Create doc for method.
     *
     * @param psiMethod the psi method
     * @param psiElementFactory the psi element factory
     */
    private void createDocForMethod(PsiMethod psiMethod, PsiElementFactory psiElementFactory) {
        try {
            checkFilesAccess(psiMethod);
            PsiDocComment psiDocComment = psiMethod.getDocComment();
            //return if the method has comment
            if (psiDocComment != null) {
                return;
            }
            String interfaceName = CodeMakerUtil.findClassNameOfSuperMethod(psiMethod);
            if (interfaceName == null) {
                return;
            }
            String methodName = psiMethod.getName();
            Map<String, Object> map = Maps.newHashMap();
            map.put("interface", interfaceName);
            map.put("method", methodName);
            map.put("paramsType", generateMethodParamsType(psiMethod));
            String seeDoc = VelocityUtil.evaluate(seeDocTemplate, map);
            PsiDocComment seeDocComment = psiElementFactory.createDocCommentFromText(seeDoc);
            WriteCommandAction writeCommandAction = new DocWriteAction(psiMethod.getProject(), seeDocComment, psiMethod,
                    psiMethod.getContainingFile());
            RunResult result = writeCommandAction.execute();
            if (result.hasException()) {
                LOGGER.error(result.getThrowable());
                Messages.showErrorDialog("CodeMaker plugin is not available, cause: " + result.getThrowable().getMessage(),
                        "CodeMaker plugin");
            }
        } catch (Exception e) {
            LOGGER.error("Create @see Doc failed", e);
        }
    }

    /**
     * Generate method params type string.
     *
     * @param psiMethod the psi method
     * @return the string
     */
    private String generateMethodParamsType(PsiMethod psiMethod) {
        StringBuilder stringBuilder = new StringBuilder();
        for (PsiParameter psiParameter : psiMethod.getParameterList().getParameters()) {
            stringBuilder.append(psiParameter.getType().getCanonicalText()).append(", ");
        }
        if (stringBuilder.toString().endsWith(", ")) {
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    /**
     * Check files access.
     *
     * @param beforeElement the before element
     */
    private void checkFilesAccess(@NotNull PsiElement beforeElement) {
        PsiFile containingFile = beforeElement.getContainingFile();
        if (containingFile == null || !containingFile.isValid()) {
            throw new IllegalStateException("File cannot be used to generate javadocs");
        }
        OperationStatus status = ReadonlyStatusHandler.getInstance(beforeElement.getProject()).
                ensureFilesWritable(Collections.singletonList(containingFile.getVirtualFile()));
        if (status.hasReadonlyFiles()) {
            throw new IllegalStateException(status.getReadonlyFilesMessage());
        }
    }
}