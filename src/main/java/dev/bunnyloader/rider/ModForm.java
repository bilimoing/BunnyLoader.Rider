/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.intellij.openapi.fileChooser.FileChooser
 *  com.intellij.openapi.fileChooser.FileChooserDescriptor
 *  com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
 *  com.intellij.openapi.project.Project
 *  com.intellij.openapi.ui.Messages
 *  com.intellij.openapi.ui.ValidationInfo
 *  com.intellij.openapi.vfs.VirtualFile
 *  com.intellij.ui.JBColor
 *  com.intellij.util.ui.UIUtil
 *  org.jetbrains.annotations.Nullable
 */
package dev.bunnyloader.rider;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.UIUtil;
import dev.bunnyloader.rider.Inputs;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jetbrains.annotations.Nullable;

public final class ModForm {
    private static final Color TEXT = JBColor.foreground();
    private static final Color MUTED = UIUtil.getContextHelpForeground();
    private static final String ID_PATTERN = "[a-z][a-z0-9_]{0,63}";
    private static final String VERSION_PATTERN = "[A-Za-z0-9][A-Za-z0-9._+-]{0,63}";
    private static final String UUID_PATTERN = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
    private final JPanel root = new JPanel(new BorderLayout(0, 12));
    private final JTextField projectName = new JTextField("MyMod", 28);
    private final JTextField displayName = new JTextField("My Mod", 28);
    private final JTextField modId = new JTextField("mymod", 28);
    private final JTextField author = new JTextField(System.getProperty("user.name", "Author"), 28);
    private final JTextField version = new JTextField("1.0.0", 12);
    private final JTextField uid = new JTextField(UUID.randomUUID().toString(), 28);
    private final JTextField destination = new JTextField(28);
    @Nullable
    private final Project project;
    private final JLabel iconName = ModForm.label(BunnyBundle.message("mod.icon.none"), MUTED);
    private final JButton removeIcon = new JButton(BunnyBundle.message("button.remove"));
    @Nullable
    private Path selectedIcon;
    private boolean idEdited;
    private boolean updatingId;
    private final JLabel previewIcon = new JLabel("BL", JLabel.CENTER);
    private final JLabel previewName = label("My Mod", TEXT);
    private final JLabel previewDetails = label("", MUTED);
    private BufferedImage previewImage;

    public ModForm(@Nullable Project project) {
        this.project = project;
        Path path = Path.of(System.getProperty("user.home"), "BunnyLoaderMods");
        if (project != null && project.getBasePath() != null) {
            Path parent = Path.of(project.getBasePath()).getParent();
            if (parent != null) path = parent;
        }
        this.setDestination(path.resolve("MyMod"));
        this.root.setOpaque(false);
        this.root.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);
        JPanel jPanel = new JPanel(new GridBagLayout());
        jPanel.setOpaque(false);
        ModForm.addRow(jPanel, 0, BunnyBundle.message("mod.project.name"), this.projectName);
        ModForm.addRow(jPanel, 1, BunnyBundle.message("mod.display.name"), this.displayName);
        ModForm.addRow(jPanel, 2, BunnyBundle.message("mod.id"), this.modId);
        ModForm.addRow(jPanel, 3, BunnyBundle.message("mod.author"), this.author);
        ModForm.addRow(jPanel, 4, BunnyBundle.message("mod.version"), this.version);
        this.uid.setEditable(false);
        this.uid.setToolTipText(BunnyBundle.message("mod.uid.tooltip"));
        JButton jButton = new JButton(BunnyBundle.message("button.regenerate"));
        jButton.addActionListener(actionEvent -> this.uid.setText(UUID.randomUUID().toString()));
        ModForm.addRow(jPanel, 5, BunnyBundle.message("mod.uid"), ModForm.withButton(this.uid, jButton));
        ModForm.addRow(jPanel, 6, BunnyBundle.message("mod.icon"), this.iconControls());
        JButton jButton2 = new JButton(BunnyBundle.message("button.browse"));
        jButton2.addActionListener(actionEvent -> this.chooseDestination());
        ModForm.addRow(jPanel, 7, BunnyBundle.message("mod.destination"), ModForm.withButton(this.destination, jButton2));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = 2;
        gridBagConstraints.insets = new Insets(2, 8, 4, 0);
        jPanel.add((Component)ModForm.label(BunnyBundle.message("mod.destination.hint"), MUTED), gridBagConstraints);
        content.add(jPanel, BorderLayout.NORTH);
        content.add(createPreview(), BorderLayout.SOUTH);
        this.root.add(content, BorderLayout.NORTH);
        this.projectName.setToolTipText(BunnyBundle.message("mod.project.name.tooltip"));
        this.displayName.setToolTipText(BunnyBundle.message("mod.display.name.tooltip"));
        this.modId.setToolTipText(BunnyBundle.message("mod.id.tooltip"));
        this.version.setToolTipText(BunnyBundle.message("mod.version.tooltip"));
        this.destination.setToolTipText(BunnyBundle.message("mod.destination.tooltip"));
        ModForm.watch(this.modId, () -> {
            if (!this.updatingId) {
                this.idEdited = true;
            }
        });
        ModForm.watch(this.displayName, () -> {
            if (!this.idEdited) {
                this.updatingId = true;
                try {
                    this.modId.setText(ModForm.idFromName(this.displayName()));
                }
                finally {
                    this.updatingId = false;
                }
            }
        });
        ModForm.watch(this.displayName, this::updatePreview);
        ModForm.watch(this.author, this::updatePreview);
        ModForm.watch(this.version, this::updatePreview);
        updatePreview();
    }

    private JPanel createPreview() {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                com.intellij.util.ui.JBUI.Borders.customLine(com.intellij.ui.JBColor.border()),
                com.intellij.util.ui.JBUI.Borders.empty(12)));
        previewIcon.setPreferredSize(com.intellij.util.ui.JBUI.size(64, 64));
        previewIcon.setFont(previewIcon.getFont().deriveFont(java.awt.Font.BOLD, 20f));
        card.add(previewIcon, BorderLayout.WEST);
        JPanel details = new JPanel(new java.awt.GridLayout(2, 1, 0, 4));
        details.setOpaque(false);
        previewName.setFont(previewName.getFont().deriveFont(java.awt.Font.BOLD));
        details.add(previewName);
        details.add(previewDetails);
        card.add(details, BorderLayout.CENTER);
        card.add(label("C#", MUTED), BorderLayout.EAST);
        JPanel section = new JPanel(new BorderLayout(0, 6));
        section.setOpaque(false);
        section.add(label(BunnyBundle.message("mod.preview"), MUTED), BorderLayout.NORTH);
        section.add(card, BorderLayout.CENTER);
        return section;
    }

    private void updatePreview() {
        previewName.setText(displayName().isBlank() ? BunnyBundle.message("mod.display.name") : displayName());
        previewName.setToolTipText(displayName());
        previewDetails.setText(version().isBlank() ? (author().isBlank() ? BunnyBundle.message("mod.author") : author()) :
                BunnyBundle.message("mod.preview.details", author().isBlank() ? BunnyBundle.message("mod.author") : author(), version()));
        if (previewImage == null) {
            previewIcon.setIcon(null);
            previewIcon.setText("BL");
        } else {
            int size = com.intellij.util.ui.JBUI.scale(64);
            double scale = Math.min((double) size / previewImage.getWidth(), (double) size / previewImage.getHeight());
            previewIcon.setText("");
            previewIcon.setIcon(new javax.swing.ImageIcon(previewImage.getScaledInstance(
                    Math.max(1, (int) (previewImage.getWidth() * scale)),
                    Math.max(1, (int) (previewImage.getHeight() * scale)), java.awt.Image.SCALE_SMOOTH)));
        }
    }

    public JComponent component() {
        return this.root;
    }

    public String projectName() {
        return this.projectName.getText().trim();
    }

    public String displayName() {
        return this.displayName.getText().trim();
    }

    public String id() {
        return this.modId.getText().trim();
    }

    public String author() {
        return this.author.getText().trim();
    }

    public String version() {
        return this.version.getText().trim();
    }

    public String uid() {
        return this.uid.getText().trim();
    }

    @Nullable
    public Path iconPath() {
        return this.selectedIcon;
    }

    public Path destination() {
        return Path.of(this.destination.getText().trim(), new String[0]).normalize();
    }

    public void setDestination(Path path) {
        this.destination.setText(path.toString());
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    public ValidationInfo validateForm() {
        if (!Inputs.isIdentifier(this.projectName())) {
            return new ValidationInfo(BunnyBundle.message("validation.project.name"), (JComponent)this.projectName);
        }
        if (this.displayName().isBlank()) {
            return new ValidationInfo(BunnyBundle.message("validation.display.name"), (JComponent)this.displayName);
        }
        if (!this.id().matches(ID_PATTERN)) {
            return new ValidationInfo(BunnyBundle.message("validation.mod.id"), (JComponent)this.modId);
        }
        if (this.author().isBlank()) return new ValidationInfo(BunnyBundle.message("validation.author"), (JComponent)this.author);
        if (this.author().startsWith("-")) {
            return new ValidationInfo(BunnyBundle.message("validation.author"), (JComponent)this.author);
        }
        if (!this.version().matches(VERSION_PATTERN)) return new ValidationInfo(BunnyBundle.message("validation.version"), (JComponent)this.version);
        if (this.version().endsWith(".")) {
            return new ValidationInfo(BunnyBundle.message("validation.version"), (JComponent)this.version);
        }
        if (!this.uid().matches(UUID_PATTERN)) {
            return new ValidationInfo(BunnyBundle.message("validation.uuid"), (JComponent)this.uid);
        }
        try {
            UUID.fromString(this.uid());
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return new ValidationInfo(BunnyBundle.message("validation.uuid"), (JComponent)this.uid);
        }
        if (this.selectedIcon != null) {
            try {
                if (!Files.isRegularFile(this.selectedIcon, new LinkOption[0])) return new ValidationInfo(BunnyBundle.message("validation.icon"), (JComponent)this.iconName);
                if (!Files.isReadable(this.selectedIcon)) return new ValidationInfo(BunnyBundle.message("validation.icon"), (JComponent)this.iconName);
                if (ImageIO.read(this.selectedIcon.toFile()) == null) {
                    return new ValidationInfo(BunnyBundle.message("validation.icon"), (JComponent)this.iconName);
                }
            }
            catch (IOException | RuntimeException exception) {
                return new ValidationInfo(BunnyBundle.message("error.icon.read", ModForm.errorMessage(exception)), (JComponent)this.iconName);
            }
        }
        try {
            Path path;
            if (this.destination.getText().isBlank()) {
                return new ValidationInfo(BunnyBundle.message("validation.destination.required"), (JComponent)this.destination);
            }
            Path path2 = this.destination();
            if (!path2.isAbsolute()) {
                return new ValidationInfo(BunnyBundle.message("validation.destination.absolute"), (JComponent)this.destination);
            }
            if (Files.exists(path2, LinkOption.NOFOLLOW_LINKS)) {
                if (!Files.isDirectory(path2, new LinkOption[0])) {
                    return new ValidationInfo(BunnyBundle.message("validation.destination.directory"), (JComponent)this.destination);
                }
                try (Stream<Path> stream = Files.list(path2);){
                    if (!stream.findAny().isPresent()) return null;
                    ValidationInfo validationInfo = new ValidationInfo(BunnyBundle.message("validation.destination.empty"), (JComponent)this.destination);
                    return validationInfo;
                }
            }
            for (path = path2.getParent(); path != null && !Files.exists(path, LinkOption.NOFOLLOW_LINKS); path = path.getParent()) {
            }
            if (path == null) return new ValidationInfo(BunnyBundle.message("validation.destination.parent"), (JComponent)this.destination);
            if (!Files.isDirectory(path, new LinkOption[0])) return new ValidationInfo(BunnyBundle.message("validation.destination.parent"), (JComponent)this.destination);
            if (Files.isWritable(path)) return null;
            return new ValidationInfo(BunnyBundle.message("validation.destination.parent"), (JComponent)this.destination);
        }
        catch (IOException | SecurityException | InvalidPathException exception) {
            return new ValidationInfo(BunnyBundle.message("validation.destination.invalid", ModForm.errorMessage(exception)), (JComponent)this.destination);
        }
    }

    private JPanel iconControls() {
        JButton jButton = new JButton(BunnyBundle.message("button.choose"));
        jButton.addActionListener(actionEvent -> this.chooseIcon());
        this.removeIcon.setEnabled(false);
        this.removeIcon.addActionListener(actionEvent -> {
            this.selectedIcon = null;
            this.previewImage = null;
            updatePreview();
            this.iconName.setText(BunnyBundle.message("mod.icon.none"));
            this.iconName.setToolTipText(null);
            this.removeIcon.setEnabled(false);
        });
        JPanel jPanel = new JPanel(new BorderLayout(6, 0));
        jPanel.setOpaque(false);
        jPanel.add((Component)this.iconName, "Center");
        JPanel jPanel2 = new JPanel(new FlowLayout(2, 4, 0));
        jPanel2.setOpaque(false);
        jPanel2.add(jButton);
        jPanel2.add(this.removeIcon);
        jPanel.add((Component)jPanel2, "East");
        return jPanel;
    }

    private void chooseIcon() {
        FileChooserDescriptor fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor().withTitle(BunnyBundle.message("dialog.icon.title"));
        VirtualFile virtualFile = FileChooser.chooseFile((FileChooserDescriptor)fileChooserDescriptor, (Project)this.project, null);
        if (virtualFile == null) {
            return;
        }
        Path path = virtualFile.toNioPath().toAbsolutePath().normalize();
        try {
            String string = path.getFileName().toString().toLowerCase(Locale.ROOT);
            if (!(string.endsWith(".png") || string.endsWith(".jpg") || string.endsWith(".jpeg"))) {
                throw new IOException(BunnyBundle.message("error.icon.format"));
            }
            BufferedImage bufferedImage = ImageIO.read(path.toFile());
            if (bufferedImage == null) {
                throw new IOException(BunnyBundle.message("error.icon.decode"));
            }
            this.selectedIcon = path;
            this.previewImage = bufferedImage;
            updatePreview();
            this.iconName.setText(path.getFileName().toString());
            this.iconName.setToolTipText(path.toString());
            this.removeIcon.setEnabled(true);
        }
        catch (IOException | RuntimeException exception) {
            Messages.showErrorDialog((Project)this.project, (String)(BunnyBundle.message("error.icon.read", ModForm.errorMessage(exception))), (String)BunnyBundle.message("error.icon.title"));
        }
    }

    private void chooseDestination() {
        FileChooserDescriptor fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor().withTitle(BunnyBundle.message("dialog.destination.title"));
        VirtualFile virtualFile = FileChooser.chooseFile((FileChooserDescriptor)fileChooserDescriptor, (Project)this.project, null);
        if (virtualFile != null) {
            this.setDestination(virtualFile.toNioPath().resolve(this.projectName()).toAbsolutePath().normalize());
        }
    }

    private static String idFromName(String string) {
        String string2 = Normalizer.normalize(string, Normalizer.Form.NFD).replaceAll("\\p{M}+", "").replaceAll("[^A-Za-z0-9]", "").toLowerCase(Locale.ROOT).replaceFirst("^[^a-z]+", "");
        return string2.substring(0, Math.min(string2.length(), 64));
    }

    private static JLabel label(String string, Color color) {
        JLabel jLabel = new JLabel(string);
        jLabel.putClientProperty("html.disable", Boolean.TRUE);
        jLabel.setForeground(color);
        return jLabel;
    }

    private static JPanel withButton(JComponent jComponent, JButton jButton) {
        JPanel jPanel = new JPanel(new BorderLayout(6, 0));
        jPanel.setOpaque(false);
        jPanel.add((Component)jComponent, "Center");
        jPanel.add((Component)jButton, "East");
        return jPanel;
    }

    private static void addRow(JPanel jPanel, int n, String string, JComponent jComponent) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = n;
        gridBagConstraints.anchor = 21;
        gridBagConstraints.insets = new Insets(5, 0, 5, 8);
        JLabel jLabel = ModForm.label(string, TEXT);
        jLabel.setLabelFor(jComponent);
        jPanel.add((Component)jLabel, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = 2;
        gridBagConstraints.insets = new Insets(5, 8, 5, 0);
        jPanel.add((Component)jComponent, gridBagConstraints);
    }

    private static void watch(JTextField jTextField, final Runnable runnable) {
        jTextField.getDocument().addDocumentListener(new DocumentListener(){

            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                runnable.run();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                runnable.run();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                runnable.run();
            }
        });
    }

    private static String errorMessage(Exception exception) {
        return exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage();
    }
}

