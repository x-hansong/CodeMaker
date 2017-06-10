package com.xiaohansong.codemaker.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.JavaProjectRootsUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.refactoring.PackageWrapper;
import com.intellij.refactoring.move.moveClassesOrPackages.MoveClassesOrPackagesUtil;
import com.xiaohansong.codemaker.ClassEntry;
import com.xiaohansong.codemaker.CodeMakerSettings;
import com.xiaohansong.codemaker.CodeTemplate;
import com.xiaohansong.codemaker.CreateFileAction;
import com.xiaohansong.codemaker.util.CodeMakerUtil;
import com.xiaohansong.codemaker.util.VelocityUtil;
import org.apache.commons.lang.time.DateFormatUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hansong.xhs
 * @version $Id: CodeMakerAction.java, v 0.1 2017-01-28 9:23 hansong.xhs Exp $$
 */
public class CodeMakerAction extends AnAction implements DumbAware {

    private static final Logger log = Logger.getInstance(CodeMakerAction.class);

    private CodeMakerSettings settings;

    private String            templateKey;

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

        PsiElement psiElement = anActionEvent.getData(PlatformDataKeys.PSI_ELEMENT);
        log.info("current pisElement: "+psiElement.getClass().getName() + "("+psiElement+")");
        while(psiElement!= null && !(psiElement instanceof PsiClass))
        {
            psiElement = psiElement.getParent();
        }

        if(psiElement == null)
        {
            Messages.showMessageDialog(project, "Please select a class", "Generate Failed", null);
            return;
        }

        PsiClass psiClass = (PsiClass) psiElement;
        String language = psiElement.getLanguage().getID().toLowerCase();
        List<ClassEntry> selectClasses = getClasses(project, codeTemplate.getClassNumber(), psiClass);

        if (selectClasses.size() < 1) {
            Messages.showMessageDialog(project, "No Classes found", "Generate Failed", null);
            return;
        }

        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < selectClasses.size(); i++) {
            map.put("class" + i, selectClasses.get(i));
        }
        try {
            Date now = new Date();
            map.put("class", selectClasses.get(0));
            map.put("YEAR", DateFormatUtils.format(now, "yyyy"));
            map.put("TIME", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            map.put("USER", System.getProperty("user.name"));
            String className = VelocityUtil.evaluate(codeTemplate.getClassNameVm(), map);
            map.put("ClassName", className);
            map.put("utils", new Utils());
            map.put("BR", "\n");

            String content = VelocityUtil.evaluate(codeTemplate.getCodeTemplate(), map);

            String packageName = selectClasses.get(0).getPackageName();
            final PackageWrapper targetPackage = new PackageWrapper(PsiManager.getInstance(project), packageName);
            List<VirtualFile> suitableRoots = JavaProjectRootsUtil.getSuitableDestinationSourceRoots(project);
            VirtualFile sourceRoot = null;
            if(suitableRoots.size() > 1) {
                sourceRoot = MoveClassesOrPackagesUtil.chooseSourceRoot(targetPackage, suitableRoots,
                        psiElement.getContainingFile().getContainingDirectory());

            }
            else if(suitableRoots.size() == 1){
                sourceRoot = suitableRoots.get(0);
            }

            if (sourceRoot!= null) {
                String sourcePath = sourceRoot.getPath() + "/" + packageName.replace(".","/");
                String targetPath = CodeMakerUtil.generateClassPath(sourcePath, className, language);

                VirtualFileManager manager = VirtualFileManager.getInstance();
                VirtualFile virtualFile = manager
                        .refreshAndFindFileByUrl(VfsUtil.pathToUrl(targetPath));

                if(virtualFile == null || !virtualFile.exists() || userConfirmedOverride()) {
                    // async write action
                    ApplicationManager.getApplication().runWriteAction(
                            new CreateFileAction(targetPath, content, anActionEvent
                                    .getDataContext()));
                }
            }

        } catch (Exception e) {
            Messages.showMessageDialog(project, e.getMessage(), "Generate Failed", null);
        }
    }

    private boolean userConfirmedOverride() {
        return Messages.showYesNoDialog("Overwrite?", "File Exists", null) == Messages.OK;
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
          if(list.isEmpty())
              return "";
          else
              return list.stream()
                  .map(Object::toString)
                  .collect(Collectors.joining(delimiter, prefix, suffix));
       }

       public String delim(Collection<?> list, int velocityCount, String delim) {
           if(velocityCount < list.size())
               return delim;
           else
               return "";
       }

    }

}
