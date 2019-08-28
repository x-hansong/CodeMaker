package com.xiaohansong.codemaker.action;

import java.awt.datatransfer.DataFlavor;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import com.intellij.designer.clipboard.SimpleTransferable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.xiaohansong.codemaker.ClassEntry;
import com.xiaohansong.codemaker.util.CodeMakerUtil;

/**
 * @author hansong.xhs
 * @version $Id: GenerateApiTableAction.java, v 0.1 2018年03月08日 下午7:09 hansong.xhs Exp $
 */
public class GenerateApiTableHtmlAction extends AnAction implements DumbAware {

    /**
     * The constant LOGGER.
     */
    private static final Logger LOGGER = Logger.getInstance(GenerateApiTableHtmlAction.class);

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
        List<PsiClass> classes = CodeMakerUtil.getClasses(javaFile);
        StringBuilder table = new StringBuilder(128);
        table.append("<table border=\"1\">");
        for (PsiClass psiClass : classes) {
            for (ClassEntry.Field field : CodeMakerUtil.getAllFields(psiClass)) {
                if (field.getModifier().contains("static")) {
                    continue;
                }
                table.append("<tr>");
                table.append("<th>").append(field.getName()).append("</th>");
                table.append("<th>").append(StringEscapeUtils.escapeHtml(field.getType()))
                    .append("</th>");
                table.append("<th>").append(StringEscapeUtils.escapeHtml(field.getComment()))
                    .append("</th>");
                table.append("</tr>");
            }
        }
        table.append("</table>");
        CopyPasteManager.getInstance()
            .setContents(new SimpleTransferable(table.toString(), DataFlavor.allHtmlFlavor));
    }

}