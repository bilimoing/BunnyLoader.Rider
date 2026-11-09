/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.intellij.icons.AllIcons$Nodes
 *  com.intellij.openapi.application.ApplicationManager
 *  com.intellij.openapi.observable.properties.AtomicBooleanProperty
 *  com.intellij.openapi.ui.ValidationInfo
 *  com.intellij.ui.JBColor
 *  com.intellij.util.ui.JBUI
 *  com.intellij.util.ui.JBUI$Borders
 *  com.jetbrains.rd.util.lifetime.Lifetime
 *  com.jetbrains.rd.util.reactive.IProperty
 *  com.jetbrains.rd.util.reactive.Property
 *  com.jetbrains.rider.projectView.projectTemplates.NewProjectDialogContext
 *  com.jetbrains.rider.projectView.projectTemplates.ProjectTemplatesSharedModel
 *  com.jetbrains.rider.projectView.projectTemplates.SharedData
 *  com.jetbrains.rider.projectView.projectTemplates.generators.ProjectTemplateGenerator
 *  com.jetbrains.rider.projectView.projectTemplates.providers.ProjectTemplateProvider
 *  com.jetbrains.rider.projectView.projectTemplates.templateTypes.ProjectTemplateType
 *  kotlin.Unit
 *  kotlin.coroutines.Continuation
 *  kotlin.jvm.functions.Function1
 */
package dev.bunnyloader.rider;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.observable.properties.AtomicBooleanProperty;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import com.jetbrains.rd.util.lifetime.Lifetime;
import com.jetbrains.rd.util.reactive.IProperty;
import com.jetbrains.rd.util.reactive.Property;
import com.jetbrains.rider.projectView.projectTemplates.NewProjectDialogContext;
import com.jetbrains.rider.projectView.projectTemplates.ProjectTemplatesSharedModel;
import com.jetbrains.rider.projectView.projectTemplates.SharedData;
import com.jetbrains.rider.projectView.projectTemplates.generators.ProjectTemplateGenerator;
import com.jetbrains.rider.projectView.projectTemplates.providers.ProjectTemplateProvider;
import com.jetbrains.rider.projectView.projectTemplates.templateTypes.ProjectTemplateType;
import dev.bunnyloader.rider.ModCreator;
import dev.bunnyloader.rider.ModForm;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function1;

public final class BunnyModTemplateProvider
implements ProjectTemplateProvider {
    private final Property<Boolean> ready = new Property<>(true);

    public IProperty<Boolean> isReady() {
        return this.ready;
    }

    public IProperty<Set<ProjectTemplateType>> load(Lifetime lifetime, NewProjectDialogContext newProjectDialogContext) {
        return new Property<>(newProjectDialogContext.getCreateSolution() ? Set.of(new ModTemplate()) : Set.of());
    }

    private static final class ModTemplate
    implements ProjectTemplateType {
        private ModTemplate() {
        }

        public String getGroup() {
            return "BunnyLoader";
        }

        public Icon getIcon() {
            return AllIcons.Nodes.Module;
        }

        public String getName() {
            return "BunnyLoader Mod";
        }

        public int getOrder() {
            return 0;
        }

        public String getUniqueId() {
            return "BunnyLoader.Mod";
        }

        public Set<String> getKeywords() {
            return Set.of("BunnyLoader", "Mod", "C#", "dotnet", "\u6a21\u7ec4");
        }

        public ProjectTemplateGenerator createGenerator(Lifetime lifetime, NewProjectDialogContext newProjectDialogContext, ProjectTemplatesSharedModel projectTemplatesSharedModel) {
            return new Generator(lifetime, newProjectDialogContext);
        }
    }

    private static final class Generator
    implements ProjectTemplateGenerator {
        private final NewProjectDialogContext context;
        private final ModForm form;
        private final JPanel panel = new JPanel(new BorderLayout(0, JBUI.scale((int)8)));
        private final JLabel validationMessage = new JLabel(" ");
        private final AtomicBooleanProperty canExpand = new AtomicBooleanProperty(false);

        Generator(Lifetime lifetime, NewProjectDialogContext newProjectDialogContext) {
            this.context = newProjectDialogContext;
            this.form = new ModForm(newProjectDialogContext.getProject());
            this.panel.setBorder((Border)JBUI.Borders.empty((int)10, (int)20));
            this.panel.add((Component)this.form.component(), "North");
            this.validationMessage.setForeground((Color)JBColor.RED);
            this.panel.add((Component)this.validationMessage, "South");
            ArrayList<JTextComponent> arrayList = new ArrayList<JTextComponent>();
            Generator.collectTextFields(this.form.component(), arrayList);
            DocumentListener documentListener = new DocumentListener(){

                @Override
                public void insertUpdate(DocumentEvent documentEvent) {
                    Generator.this.validate();
                }

                @Override
                public void removeUpdate(DocumentEvent documentEvent) {
                    Generator.this.validate();
                }

                @Override
                public void changedUpdate(DocumentEvent documentEvent) {
                    Generator.this.validate();
                }
            };
            for (JTextComponent jTextComponent : arrayList) {
                jTextComponent.getDocument().addDocumentListener(documentListener);
            }
            lifetime.onTermination(() -> {
                for (JTextComponent jTextComponent : arrayList) {
                    jTextComponent.getDocument().removeDocumentListener(documentListener);
                }
                return Unit.INSTANCE;
            });
            this.validate();
        }

        public JComponent getComponent() {
            return this.panel;
        }

        public AtomicBooleanProperty getCanExpand() {
            return this.canExpand;
        }

        private void validate() {
            ValidationInfo validationInfo = this.form.validateForm();
            this.canExpand.set(validationInfo == null);
            this.validationMessage.setText(validationInfo == null ? " " : validationInfo.message);
        }

        public SharedData getSharedData() {
            String string = null;
            try {
                Path path = this.form.destination().getParent();
                if (path != null) {
                    string = path.toString();
                }
            }
            catch (InvalidPathException invalidPathException) {
                // empty catch block
            }
            return new SharedData(this.form.projectName(), this.form.projectName(), string);
        }

        public void setSharedData(SharedData sharedData) {
            if (sharedData == null || sharedData.getDirectory() == null || sharedData.getDirectory().isBlank()) {
                return;
            }
            try {
                this.form.setDestination(Path.of(sharedData.getDirectory(), new String[0]).resolve(this.form.projectName()));
            }
            catch (InvalidPathException invalidPathException) {
                // empty catch block
            }
            this.validate();
        }

        public Object expandTemplate(Continuation<? super Function1<? super Continuation<? super Unit>, ? extends Object>> continuation2) {
            AtomicReference<ModCreator.Request> atomicReference = new AtomicReference<>();
            Generator.onUiThread(() -> {
                ValidationInfo validationInfo = this.form.validateForm();
                if (validationInfo != null) {
                    throw new IllegalStateException(validationInfo.message);
                }
                atomicReference.set(new ModCreator.Request(this.form.projectName(), this.form.displayName(), this.form.id(), this.form.author(), this.form.version(), this.form.uid(), this.form.destination(), this.form.iconPath()));
            });
            Function1<Continuation<? super Unit>, Object> function1 = continuation -> {
                Generator.onUiThread(() -> ModCreator.create(this.context.getProject(), atomicReference.get()));
                return Unit.INSTANCE;
            };
            return function1;
        }

        private static void onUiThread(Runnable runnable) {
            if (ApplicationManager.getApplication().isDispatchThread()) {
                runnable.run();
            } else {
                ApplicationManager.getApplication().invokeAndWait(runnable);
            }
        }

        private static void collectTextFields(Container container, List<JTextComponent> list) {
            for (Component component : container.getComponents()) {
                Container container2;
                if (component instanceof JTextComponent) {
                    container2 = (JTextComponent)component;
                    list.add((JTextComponent)container2);
                }
                if (!(component instanceof Container)) continue;
                container2 = (Container)component;
                Generator.collectTextFields(container2, list);
            }
        }
    }
}

