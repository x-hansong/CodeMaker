package com.xiaohansong.codemaker;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.xiaohansong.codemaker.util.CodeMakerUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.UnsupportedCharsetException;

/**
 * @author hansong.xhs
 * @version $Id: CreateFileAction.java, v 0.1 2017-01-21 11:44 hansong.xhs Exp $$
 */
public class CreateFileAction implements Runnable {

    /**
     * logger
     */
    private static final Logger LOGGER = CodeMakerUtil.getLogger(CreateFileAction.class);

    private String outputFile;

    private String content;

    private String fileEncoding;

    private DataContext dataContext;

    public CreateFileAction(String outputFile, String content, String fileEncoding, DataContext dataContext) {
        this.outputFile = outputFile;
        this.content = content;
        this.fileEncoding = fileEncoding;
        this.dataContext = dataContext;
    }

    @Override
    public void run() {
        try {
            VirtualFileManager manager = VirtualFileManager.getInstance();
            VirtualFile virtualFile = manager
                    .refreshAndFindFileByUrl(VfsUtil.pathToUrl(outputFile));

            if (virtualFile != null && virtualFile.exists()) {
                virtualFile.setBinaryContent(content.getBytes(fileEncoding));
            } else {
                File file = new File(outputFile);
                FileUtils.writeStringToFile(file, content, fileEncoding);
                virtualFile = manager.refreshAndFindFileByUrl(VfsUtil.pathToUrl(outputFile));
            }
            VirtualFile finalVirtualFile = virtualFile;
            Project project = DataKeys.PROJECT.getData(dataContext);
            if (finalVirtualFile == null || project == null) {
                LOGGER.error(this);
                return;
            }
            ApplicationManager.getApplication()
                    .invokeLater(
                            () -> FileEditorManager.getInstance(project).openFile(finalVirtualFile, true,
                                    true));

        } catch (UnsupportedCharsetException ex) {
            ApplicationManager.getApplication().invokeLater(() -> Messages.showMessageDialog("Unknown Charset: " + fileEncoding + ", please use the correct charset", "Generate Failed", null));
        } catch (Exception e) {
            LOGGER.error("Create file failed", e);
        }

    }
}
