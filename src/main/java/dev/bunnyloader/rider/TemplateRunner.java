/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.intellij.execution.ExecutionException
 *  com.intellij.execution.configurations.GeneralCommandLine
 *  com.intellij.execution.process.CapturingProcessHandler
 *  com.intellij.execution.process.ProcessOutput
 *  com.intellij.openapi.application.ApplicationManager
 *  com.intellij.openapi.diagnostic.Logger
 *  com.intellij.openapi.progress.ProgressIndicator
 *  com.intellij.openapi.progress.Task$Backgroundable
 *  com.intellij.openapi.project.Project
 *  com.intellij.openapi.ui.Messages
 *  com.intellij.openapi.vfs.LocalFileSystem
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.bunnyloader.rider;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class TemplateRunner {
    private static final Logger LOG = Logger.getInstance(TemplateRunner.class);

    private TemplateRunner() {
    }

    static void run(final @Nullable Project project, final String string, final Path path, final List<String> list, final Runnable runnable) {
        new Task.Backgroundable(project, string, true){
            private String error;

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run(@NotNull ProgressIndicator progressIndicator) {
                progressIndicator.setIndeterminate(true);
                progressIndicator.setText(BunnyBundle.message("progress.dotnet.new"));
                try {
                    GeneralCommandLine generalCommandLine = new GeneralCommandLine(new String[]{"dotnet"}).withParameters(list).withCharset(StandardCharsets.UTF_8);
                    CapturingProcessHandler capturingProcessHandler = new CapturingProcessHandler(generalCommandLine);
                    ProcessOutput processOutput = capturingProcessHandler.runProcessWithProgressIndicator(progressIndicator);
                    if (processOutput.isCancelled()) {
                        return;
                    }
                    if (processOutput.getExitCode() != 0) {
                        this.error = BunnyBundle.message("error.dotnet.new", Integer.toString(processOutput.getExitCode()), processOutput.getStderr(), processOutput.getStdout());
                    }
                }
                catch (ExecutionException executionException) {
                    LOG.warn("Unable to execute BunnyLoader template", (Throwable)executionException);
                    this.error = BunnyBundle.message("error.dotnet.start", executionException.getMessage());
                }
                finally {
                    ApplicationManager.getApplication().invokeLater(() -> LocalFileSystem.getInstance().refreshAndFindFileByNioFile(path));
                }
            }

            public void onSuccess() {
                if (project != null && project.isDisposed()) {
                    return;
                }
                if (this.error != null) {
                    Messages.showErrorDialog((Project)project, (String)this.error, (String)string);
                } else {
                    runnable.run();
                }
            }

            public void onCancel() {
                if (project == null || !project.isDisposed()) {
                    Messages.showInfoMessage((Project)project, (String)BunnyBundle.message("creation.template.cancelled"), (String)string);
                }
            }
        }.queue();
    }
}

