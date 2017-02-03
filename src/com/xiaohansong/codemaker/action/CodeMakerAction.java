package com.xiaohansong.codemaker.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.xiaohansong.codemaker.ClassEntry;
import com.xiaohansong.codemaker.CodeMakerSettings;
import com.xiaohansong.codemaker.CodeTemplate;
import com.xiaohansong.codemaker.CreateFileAction;
import com.xiaohansong.codemaker.util.CodeMakerUtil;
import com.xiaohansong.codemaker.util.VelocityUtil;
import org.apache.commons.lang.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hansong.xhs
 * @version $Id: CodeMakerAction.java, v 0.1 2017-01-28 ÏÂÎç9:23 hansong.xhs Exp $$
 */
public class CodeMakerAction extends AnAction {
    private CodeMakerSettings settings;
    private String            templateKey;

    public CodeMakerAction(String templateKey) {
        this.settings = ServiceManager.getService(CodeMakerSettings.class);
        this.templateKey = templateKey;
        getTemplatePresentation().setDescription("description");
        getTemplatePresentation().setText(templateKey, false);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        PsiJavaFile javaFile = (PsiJavaFile) anActionEvent.getData(PlatformDataKeys.PSI_FILE);
        if (javaFile == null) {
            return;
        }
        PsiClass[] classes = javaFile.getClasses();
        if (classes.length < 1) {
            return;
        }
        PsiClass currentClass = classes[0];
        List<PsiClass> selectClasses = new ArrayList<>();
        selectClasses.add(currentClass);
        CodeTemplate codeTemplate = settings.getCodeTemplate(templateKey);
        //select the other class by classChooser
        for (int i = 1; i < codeTemplate.getClassNumber(); i++) {
            PsiClass psiClass = CodeMakerUtil.chooseClass(project, currentClass);
            if (psiClass == null) {
                return;
            }
            selectClasses.add(psiClass);
        }

        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < selectClasses.size(); i++) {
            map.put("class" + i, ClassEntry.create(selectClasses.get(i)));
        }
        Date now = new Date();
        map.put("YEAR", DateFormatUtils.format(now, "yyyy"));
        map.put("TIME", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        map.put("USER", System.getProperty("user.name"));
        String className = VelocityUtil.evaluate(codeTemplate.getClassNameVm(), map);
        map.put("ClassName", className);

        String content = VelocityUtil.evaluate(codeTemplate.getCodeTemplate(), map);

        // async write action
        ApplicationManager.getApplication().runWriteAction(
            new CreateFileAction(CodeMakerUtil.generateClassPath(
                CodeMakerUtil.getSourcePath(currentClass), className), content, anActionEvent
                .getDataContext()));

    }
}
