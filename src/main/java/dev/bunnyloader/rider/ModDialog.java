/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.intellij.openapi.project.Project
 *  com.intellij.openapi.ui.DialogWrapper
 *  com.intellij.openapi.ui.ValidationInfo
 *  org.jetbrains.annotations.Nullable
 */
package dev.bunnyloader.rider;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import dev.bunnyloader.rider.ModForm;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nullable;

final class ModDialog
extends DialogWrapper {
    private final ModForm form;

    ModDialog(@Nullable Project project) {
        super(project, true);
        this.form = new ModForm(project);
        this.setTitle("\u65b0\u5efa BunnyLoader Mod");
        this.setOKButtonText("\u521b\u5efa Mod");
        this.init();
    }

    protected JComponent createCenterPanel() {
        return this.form.component();
    }

    @Nullable
    protected ValidationInfo doValidate() {
        return this.form.validateForm();
    }

    ModForm form() {
        return this.form;
    }
}

