package com.xiaohansong.codemaker.action;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.xiaohansong.codemaker.CodeMakerSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hansong.xhs
 * @version $Id: CodeMakerGroup.java, v 0.1 2017-01-28 9:25 hansong.xhs Exp $$
 */
public class CodeMakerGroup extends ActionGroup implements DumbAware {

    private CodeMakerSettings settings;

    public CodeMakerGroup() {
        settings = ServiceManager.getService(CodeMakerSettings.class);
    }

    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent anActionEvent) {
        if (anActionEvent == null) {
            return AnAction.EMPTY_ARRAY;
        }
        Project project = PlatformDataKeys.PROJECT.getData(anActionEvent.getDataContext());
        if (project == null) {
            return AnAction.EMPTY_ARRAY;
        }
        final List<AnAction> children = new ArrayList<>();
        settings.getCodeTemplates().forEach((key, value) -> children.add(getOrCreateAction(key)));

        return children.toArray(new AnAction[children.size()]);
    }

    private AnAction getOrCreateAction(String templateName) {
        final String actionId = "CodeMaker.Menu.Action." + templateName;
        AnAction action = ActionManager.getInstance().getAction(actionId);
        if (action == null) {
            action = new CodeMakerAction(templateName);
            ActionManager.getInstance().registerAction(actionId, action);
        }
        return action;
    }
}
