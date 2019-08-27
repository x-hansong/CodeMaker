package com.xiaohansong.codemaker.ui;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;

public class Editors {
    public static Editor createSourceEditor(Project project, String language, String content, boolean readOnly) {
        final EditorFactory factory = EditorFactory.getInstance();
        final Editor editor = factory.createEditor(factory.createDocument(content), project, FileTypeManager.getInstance()
                .getFileTypeByExtension(language), readOnly);
        editor.getSettings().setRefrainFromScrolling(false);
        return editor;
    }

    public static void release(Editor editor) {
        EditorFactory.getInstance().releaseEditor(editor);
    }
}
