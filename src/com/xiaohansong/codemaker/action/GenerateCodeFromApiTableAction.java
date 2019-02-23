package com.xiaohansong.codemaker.action;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.List;
import java.util.Map;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.RunResult;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiType;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.search.GlobalSearchScope;
import com.xiaohansong.codemaker.CodeMakerSettings;
import com.xiaohansong.codemaker.FieldWriteAction;
import com.xiaohansong.codemaker.util.CodeMakerUtil;
import com.xiaohansong.codemaker.util.VelocityUtil;

/**
 * @author hansong.xhs
 * @version $Id: GenerateApiTableAction.java, v 0.1 2018年03月08日 下午7:09 hansong.xhs Exp $
 */
public class GenerateCodeFromApiTableAction extends AnAction implements DumbAware {

    private CodeMakerSettings settings;

    GenerateCodeFromApiTableAction() {
        super();
        this.settings = ServiceManager.getService(CodeMakerSettings.class);
    }

    /**
     * The constant LOGGER.
     */
    private static final Logger LOGGER = Logger.getInstance(GenerateCodeFromApiTableAction.class);

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        DumbService dumbService = DumbService.getInstance(project);
        if (dumbService.isDumb()) {
            dumbService
                .showDumbModeNotification("CodeMaker plugin is not available during indexing");
            return;
        }
        PsiFile javaFile = e.getData(CommonDataKeys.PSI_FILE);
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (javaFile == null || editor == null) {
            return;
        }
        Transferable transferable = CopyPasteManager.getInstance().getContents();
        if (transferable == null) {
            return;
        }
        try {
            String table = (String) transferable.getTransferData(DataFlavor.stringFlavor);
            List<String> tableList = Splitter.onPattern("\n|\t|\r\n").splitToList(table);
            PsiElementFactory psiElementFactory = PsiElementFactory.SERVICE.getInstance(project);
            int mod = tableList.size() % 3;
            int end = tableList.size() - mod;

            for (int i = 0; i < end; i++) {
                String fieldName = tableList.get(i);
                String fieldType = tableList.get(++i);
                String fieldComment = tableList.get(++i);
                PsiField psiField = psiElementFactory.createField(fieldName,
                    PsiType.getTypeByName(fieldType, project, GlobalSearchScope.allScope(project)));
                Map<String, Object> map = Maps.newHashMap();
                map.put("comment", fieldComment);
                String vm = "/**\n" + " * ${comment}\n" + " */";
                if (settings.getCodeTemplate("FieldComment.vm") != null) {
                    vm = settings.getCodeTemplate("FieldComment.vm").getCodeTemplate();
                }
                String fieldDocComment = VelocityUtil.evaluate(vm, map);
                PsiDocComment docComment = psiElementFactory
                    .createDocCommentFromText(fieldDocComment);
                psiField.addBefore(docComment, psiField.getFirstChild());
                WriteCommandAction writeCommandAction = new FieldWriteAction(project,
                    CodeMakerUtil.getClasses(javaFile).get(0), psiField, javaFile);
                RunResult result = writeCommandAction.execute();
                if (result.hasException()) {
                    LOGGER.error(result.getThrowable());
                    Messages.showErrorDialog("CodeMaker plugin is not available, cause: "
                                             + result.getThrowable().getMessage(),
                        "CodeMaker plugin");
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }

}