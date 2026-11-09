/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.intellij.openapi.actionSystem.ActionUpdateThread
 *  com.intellij.openapi.actionSystem.AnAction
 *  com.intellij.openapi.actionSystem.AnActionEvent
 *  com.intellij.openapi.actionSystem.CommonDataKeys
 *  com.intellij.openapi.application.ApplicationManager
 *  com.intellij.openapi.fileEditor.FileEditorManager
 *  com.intellij.openapi.project.DumbAware
 *  com.intellij.openapi.project.Project
 *  com.intellij.openapi.vfs.LocalFileSystem
 *  com.intellij.openapi.vfs.VirtualFile
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.bunnyloader.rider;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import dev.bunnyloader.rider.ItemDialog;
import dev.bunnyloader.rider.TemplateRunner;
import java.nio.file.Path;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NewModItemAction
extends AnAction
implements DumbAware {
    @NotNull
    public ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Nullable
    private static VirtualFile selectedDirectory(AnActionEvent anActionEvent) {
        VirtualFile virtualFile = (VirtualFile)anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);
        if (virtualFile == null || !virtualFile.isInLocalFileSystem()) {
            return null;
        }
        return virtualFile.isDirectory() ? virtualFile : virtualFile.getParent();
    }

    public void update(@NotNull AnActionEvent anActionEvent) {
        anActionEvent.getPresentation().setEnabledAndVisible(anActionEvent.getProject() != null && NewModItemAction.selectedDirectory(anActionEvent) != null);
    }

    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        VirtualFile virtualFile = NewModItemAction.selectedDirectory(anActionEvent);
        if (project == null || virtualFile == null) {
            return;
        }
        Path path = virtualFile.toNioPath();
        ItemDialog itemDialog = new ItemDialog(project, path);
        if (!itemDialog.showAndGet()) {
            return;
        }
        String string = itemDialog.itemName();
        TemplateRunner.run(project, "Create BunnyLoader ModItem", path, List.of("new", "blitem", "-n", string, "-o", path.toString(), "--namespace", itemDialog.namespace()), () -> virtualFile.refresh(true, true, () -> {
            VirtualFile generatedFile = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(path.resolve(string + ".cs"));
            if (generatedFile != null && !project.isDisposed()) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    if (!project.isDisposed()) {
                        FileEditorManager.getInstance((Project)project).openFile(generatedFile, true);
                    }
                });
            }
        }));
    }
}

