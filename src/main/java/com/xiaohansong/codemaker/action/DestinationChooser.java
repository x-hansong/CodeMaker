package com.xiaohansong.codemaker.action;

import com.intellij.ide.util.DirectoryChooser;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.refactoring.PackageWrapper;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static com.intellij.refactoring.move.moveClassesOrPackages.MoveClassesOrPackagesUtil.buildDirectoryList;

class DestinationChooser {

    static Destination chooseDestination(final PackageWrapper targetPackage,
                                         final List<? extends VirtualFile> contentSourceRoots,
                                         final PsiDirectory initialDirectory) {
        Project project = targetPackage.getManager().getProject();
        //ensure that there would be no duplicates: e.g. when one content root is subfolder of another root (configured via excluded roots)
        LinkedHashSet<PsiDirectory> targetDirectories = new LinkedHashSet<>();
        Map<PsiDirectory, String> relativePathsToCreate = new HashMap<>();
        buildDirectoryList(targetPackage, contentSourceRoots, targetDirectories, relativePathsToCreate);

        return chooseDirectory(
                targetDirectories.toArray(PsiDirectory.EMPTY_ARRAY),
                initialDirectory,
                project,
                relativePathsToCreate
        );
    }

    private static
    Destination chooseDirectory(PsiDirectory[] targetDirectories,
                                 @Nullable PsiDirectory initialDirectory,
                                 @NotNull Project project,
                                 Map<PsiDirectory, String> relativePathsToCreate) {
        final int SHOW_SOURCE_CODE = 555;
        final DirectoryChooser chooser = new DirectoryChooser(project) {
            @NotNull
            @Override
            protected Action[] createLeftSideActions() {
                final DialogWrapperAction action = new DialogWrapperAction("Show Source") {
                    @Override
                    protected void doAction(ActionEvent e) {
                        close(SHOW_SOURCE_CODE);
                    }

                };
                action.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
                action.putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, 0);
                action.putValue(Action.LONG_DESCRIPTION, "Show Generate Source");


                return new Action[]{action};
            }
        };

        chooser.setTitle("Choose Source Root");
        chooser.fillList(
                targetDirectories,
                initialDirectory,
                project,
                relativePathsToCreate
        );

        chooser.show();
        if(chooser.isOK() && chooser.getSelectedDirectory() != null) {
            return new FileDestination(chooser.getSelectedDirectory().getVirtualFile());
        } else if (chooser.getExitCode() == SHOW_SOURCE_CODE) {
            return ShowSourceDestination;
        } else {
            return null;
        }
    }

    interface Destination {}

    @Data
    static class FileDestination implements Destination {
        private final VirtualFile file;
    }

    static final Destination ShowSourceDestination = new Destination() {};
}
