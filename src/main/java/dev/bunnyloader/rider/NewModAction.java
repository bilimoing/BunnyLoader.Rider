/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.intellij.openapi.actionSystem.AnAction
 *  com.intellij.openapi.actionSystem.AnActionEvent
 *  com.intellij.openapi.project.DumbAware
 *  org.jetbrains.annotations.NotNull
 */
package dev.bunnyloader.rider;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import dev.bunnyloader.rider.ModCreator;
import dev.bunnyloader.rider.ModDialog;
import org.jetbrains.annotations.NotNull;

public final class NewModAction
extends AnAction
implements DumbAware {
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        ModDialog modDialog = new ModDialog(anActionEvent.getProject());
        if (modDialog.showAndGet()) {
            ModCreator.create(anActionEvent.getProject(), modDialog.form());
        }
    }
}

