package com.xiaohansong.codemaker.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.JavaProjectRootsUtil;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.refactoring.PackageWrapper;
import com.intellij.uiDesigner.core.GridConstraints;
import com.xiaohansong.codemaker.ClassEntry;
import com.xiaohansong.codemaker.CodeMakerSettings;
import com.xiaohansong.codemaker.CodeTemplate;
import com.xiaohansong.codemaker.CreateFileAction;
import com.xiaohansong.codemaker.util.CodeMakerUtil;
import com.xiaohansong.codemaker.util.VelocityUtil;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang.time.DateFormatUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hansong.xhs
 * @version $Id: CodeMakerAction.java, v 0.1 2017-01-28 9:23 hansong.xhs Exp $$
 */
public class CodeMakerAction extends AnAction implements DumbAware {

    private static final Logger log = Logger.getInstance(CodeMakerAction.class);

    private CodeMakerSettings settings;

    private String templateKey;

    CodeMakerAction(String templateKey) {
        this.settings = ServiceManager.getService(CodeMakerSettings.class);
        this.templateKey = templateKey;
        getTemplatePresentation().setDescription("description");
        getTemplatePresentation().setText(templateKey, false);
    }

    /**
     * @see com.intellij.openapi.actionSystem.AnAction#actionPerformed(com.intellij.openapi.actionSystem.AnActionEvent)
     */
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project == null) {
            return;
        }
        DumbService dumbService = DumbService.getInstance(project);
        if (dumbService.isDumb()) {
            dumbService.showDumbModeNotification("CodeMaker plugin is not available during indexing");
            return;
        }
        CodeTemplate codeTemplate = settings.getCodeTemplate(templateKey);

        PsiElement psiElement = anActionEvent.getData(LangDataKeys.PSI_ELEMENT);
        if (!(psiElement instanceof PsiClass)) {
            Messages.showMessageDialog(project, "Please focus on a class", "Generate Failed", null);
            return;
        }
        log.info("current pisElement: " + psiElement.getClass().getName() + "(" + psiElement + ")");

        PsiClass psiClass = (PsiClass) psiElement;
        String language = psiElement.getLanguage().getID().toLowerCase();
        List<ClassEntry> selectClasses = getClasses(project, codeTemplate.getClassNumber(), psiClass);

        if (selectClasses.size() < 1) {
            Messages.showMessageDialog(project, "No Classes found", "Generate Failed", null);
            return;
        }

        try {
            ClassEntry currentClass = selectClasses.get(0);
            GeneratedSource generated = generateSource(codeTemplate, selectClasses, currentClass);
            DestinationChooser.Destination destination = chooseDestination(currentClass, project, psiElement);
            if (destination instanceof DestinationChooser.FileDestination) {
                saveToFile(anActionEvent, language, generated.className, generated.content, currentClass, (DestinationChooser.FileDestination) destination, codeTemplate.getFileEncoding());
            }
            else if(destination == DestinationChooser.ShowSourceDestination) {

                showSource(project, language, generated.className, generated.content);
            }

        } catch (Exception e) {
            Messages.showMessageDialog(project, e.getMessage(), "Generate Failed", null);
        }
    }

    @NotNull
    private GeneratedSource generateSource(CodeTemplate codeTemplate, List<ClassEntry> selectClasses, ClassEntry currentClass) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < selectClasses.size(); i++) {
            map.put("class" + i, selectClasses.get(i));
        }
        Date now = new Date();
        map.put("class", currentClass);
        map.put("YEAR", DateFormatUtils.format(now, "yyyy"));
        map.put("TIME", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        map.put("USER", System.getProperty("user.name"));
        map.put("utils", new Utils());
        map.put("BR", "\n");
        String className = VelocityUtil.evaluate(codeTemplate.getClassNameVm(), map);
        map.put("ClassName", className);

        String content = VelocityUtil.evaluate(codeTemplate.getCodeTemplate(), map);

        return new GeneratedSource(className, content);
    }

    private void saveToFile(AnActionEvent anActionEvent, String language, String className, String content, ClassEntry currentClass, DestinationChooser.FileDestination destination, String encoding) {
        final VirtualFile file = destination.getFile();
        final String sourcePath = file.getPath() + "/" + currentClass.getPackageName().replace(".", "/");
        final String targetPath = CodeMakerUtil.generateClassPath(sourcePath, className, language);

        VirtualFileManager manager = VirtualFileManager.getInstance();
        VirtualFile virtualFile = manager
                .refreshAndFindFileByUrl(VfsUtil.pathToUrl(targetPath));

        if (virtualFile == null || !virtualFile.exists() || userConfirmedOverride()) {
            // async write action
            ApplicationManager.getApplication().runWriteAction(
                    new CreateFileAction(targetPath, content, encoding, anActionEvent
                            .getDataContext()));
        }
    }

    private void showSource(Project project, String language, String className, String content) {
        final EditorFactory factory = EditorFactory.getInstance();
        final Editor editor = factory.createEditor(factory.createDocument(content), project, FileTypeManager.getInstance()
                .getFileTypeByExtension(language), true);

        final JPanel panel = new JPanel();
        GridConstraints constraints = new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(300, 300), null, 0, true);

        panel.add(editor.getComponent(), constraints);

        final DialogBuilder builder = new DialogBuilder(project);
        builder.addCloseButton().setText("Close");
        builder.setCenterPanel(panel);
        builder.setTitle(className);

        builder.show();
    }

    /**
     * allow user to select the generated code source root
     */
    private DestinationChooser.Destination chooseDestination(ClassEntry classEntry, Project project, PsiElement psiElement) {
        String packageName = classEntry.getPackageName();
        final PackageWrapper targetPackage = new PackageWrapper(PsiManager.getInstance(project), packageName);
        List<VirtualFile> suitableRoots = JavaProjectRootsUtil.getSuitableDestinationSourceRoots(project);
        return DestinationChooser.chooseDestination(targetPackage, suitableRoots,
                psiElement.getContainingFile().getContainingDirectory());
    }

    private boolean userConfirmedOverride() {
        return Messages.showYesNoDialog("Overwrite?", "File Exists", null) == Messages.YES;
    }


    @NotNull
    private List<ClassEntry> getClasses(Project project, int requiredClassCount, PsiClass currentClass) {
        List<ClassEntry> selectClasses = new ArrayList<>();
        selectClasses.add(ClassEntry.create(currentClass));
        //select the other classes by classChooser
        for (int i = 1; i < requiredClassCount; i++) {
            PsiClass psiClass = CodeMakerUtil.chooseClass(project, currentClass);
            if (psiClass == null) {
                return Collections.emptyList();
            }
            selectClasses.add(ClassEntry.create(psiClass));
        }
        return selectClasses;
    }

    public static class Utils {
        public String mkString(Collection<?> list, String delimiter, String prefix, String suffix) {
            if (list.isEmpty())
                return "";
            else
                return list.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(delimiter, prefix, suffix));
        }

        public String delim(Collection<?> list, int velocityCount, String delim) {
            if (velocityCount < list.size())
                return delim;
            else
                return "";
        }

        public String camelCase(String prefix, String name){
            if(name == null || name.isEmpty())
                return name;
            String identifier = scala.removeBackticks(name);
            return scala.removeBackticks(prefix) + identifier.substring(0, 1).toUpperCase() + identifier.substring(1);
        }

        @Getter
        private final ScalaUtils scala = new ScalaUtils();

        public static class ScalaUtils {
            /**
             * backticks are sometimes used in scala identifiers to escape reserved words like `type`, `object`, etc.
             */
            public String removeBackticks(String str) {
                if(str == null) return str;
                else return str.replace("`", "");
            }
        }
    }


    @Data
    private static class GeneratedSource {
       private final String className;
       private final String content;
    }

}
