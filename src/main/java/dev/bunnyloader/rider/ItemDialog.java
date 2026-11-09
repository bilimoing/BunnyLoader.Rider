/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.intellij.openapi.project.Project
 *  com.intellij.openapi.ui.DialogWrapper
 *  com.intellij.openapi.ui.ValidationInfo
 *  com.intellij.util.ui.FormBuilder
 *  org.jetbrains.annotations.Nullable
 */
package dev.bunnyloader.rider;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.util.ui.FormBuilder;
import dev.bunnyloader.rider.Inputs;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.jetbrains.annotations.Nullable;

final class ItemDialog
extends DialogWrapper {
    private final JTextField name = new JTextField("MyItem", 30);
    private final JTextField namespace = new JTextField("MyMod.Items", 30);
    private final Path directory;

    ItemDialog(Project project, Path path) {
        super(project, true);
        this.directory = path;
        this.setTitle(BunnyBundle.message("dialog.item.title"));
        this.init();
    }

    protected JComponent createCenterPanel() {
        return FormBuilder.createFormBuilder().addLabeledComponent(BunnyBundle.message("item.class.name"), (JComponent)this.name).addLabeledComponent(BunnyBundle.message("item.namespace"), (JComponent)this.namespace).addLabeledComponent(BunnyBundle.message("item.directory"), (JComponent)new JLabel(this.directory.toString())).addComponent((JComponent)new JLabel(BunnyBundle.message("item.template.hint"))).getPanel();
    }

    public JComponent getPreferredFocusedComponent() {
        return this.name;
    }

    @Nullable
    protected ValidationInfo doValidate() {
        if (!Inputs.isIdentifier(this.itemName())) {
            return new ValidationInfo(BunnyBundle.message("validation.item.name"), (JComponent)this.name);
        }
        if (!Inputs.isNamespace(this.namespace())) {
            return new ValidationInfo(BunnyBundle.message("validation.item.namespace"), (JComponent)this.namespace);
        }
        if (Files.exists(this.directory.resolve(this.itemName() + ".cs"), new LinkOption[0])) {
            return new ValidationInfo(BunnyBundle.message("validation.item.exists"), (JComponent)this.name);
        }
        if (!Files.isDirectory(this.directory, new LinkOption[0]) || !Files.isWritable(this.directory)) {
            return new ValidationInfo(BunnyBundle.message("validation.item.directory"));
        }
        return null;
    }

    String itemName() {
        return this.name.getText().trim();
    }

    String namespace() {
        return this.namespace.getText().trim();
    }
}

