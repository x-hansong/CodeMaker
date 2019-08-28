package com.xiaohansong.codemaker.action;

import com.intellij.openapi.actionSystem.*;
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

    /**
     * @see com.intellij.openapi.actionSystem.ActionGroup#getChildren(com.intellij.openapi.actionSystem.AnActionEvent)
     */
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
        settings.getCodeTemplates().forEach((key, value) -> children.add(getOrCreateAction(key, value.getName())));

        return children.toArray(new AnAction[children.size()]);
    }

    private AnAction getOrCreateAction(String key, String title) {
        final String actionId = "CodeMaker.Menu.Action." + key;
        AnAction action = ActionManager.getInstance().getAction(actionId);
        if (action == null) {
            action = new CodeMakerAction(key);
            ActionManager.getInstance().registerAction(actionId, action);
        }
        return action;
    }
}
