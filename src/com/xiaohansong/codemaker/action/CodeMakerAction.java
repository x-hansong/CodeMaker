package com.xiaohansong.codemaker.action;

import com.intellij.openapi.editor.actionSystem.EditorAction;

/**
 * @author hansong.xhs
 * @version $Id: CodeMakerAction.java, v 0.1 2017-01-28 обнГ9:23 hansong.xhs Exp $$
 */
public class CodeMakerAction extends EditorAction {

    public CodeMakerAction(String name) {
        super(new CodeMakerActionHandler(name));
        getTemplatePresentation().setDescription("description");
        getTemplatePresentation().setText(name, false);
    }
}
