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
        this.setTitle("New BunnyLoader ModItem");
        this.init();
    }

    protected JComponent createCenterPanel() {
        return FormBuilder.createFormBuilder().addLabeledComponent("Class name:", (JComponent)this.name).addLabeledComponent("Namespace:", (JComponent)this.namespace).addLabeledComponent("Selected directory:", (JComponent)new JLabel(this.directory.toString())).addComponent((JComponent)new JLabel("Requires installed dotnet template: blitem.")).getPanel();
    }

    public JComponent getPreferredFocusedComponent() {
        return this.name;
    }

    @Nullable
    protected ValidationInfo doValidate() {
        if (!Inputs.isIdentifier(this.itemName())) {
            return new ValidationInfo("Use a C# class identifier (for example MyItem).", (JComponent)this.name);
        }
        if (!Inputs.isNamespace(this.namespace())) {
            return new ValidationInfo("Enter a C# namespace (for example MyMod.Items).", (JComponent)this.namespace);
        }
        if (Files.exists(this.directory.resolve(this.itemName() + ".cs"), new LinkOption[0])) {
            return new ValidationInfo("A file with this class name already exists.", (JComponent)this.name);
        }
        if (!Files.isDirectory(this.directory, new LinkOption[0]) || !Files.isWritable(this.directory)) {
            return new ValidationInfo("The selected directory is not writable.");
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

