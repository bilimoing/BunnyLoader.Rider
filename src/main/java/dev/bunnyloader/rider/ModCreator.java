/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.intellij.execution.configurations.GeneralCommandLine
 *  com.intellij.execution.process.CapturingProcessHandler
 *  com.intellij.execution.process.ProcessOutput
 *  com.intellij.ide.impl.ProjectUtil
 *  com.intellij.openapi.progress.ProcessCanceledException
 *  com.intellij.openapi.progress.ProgressIndicator
 *  com.intellij.openapi.progress.Task$Backgroundable
 *  com.intellij.openapi.project.Project
 *  com.intellij.openapi.ui.Messages
 *  com.intellij.openapi.vfs.LocalFileSystem
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.bunnyloader.rider;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import dev.bunnyloader.rider.ModForm;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ModCreator {
    private ModCreator() {
    }

    public static void create(@Nullable Project project, ModForm modForm) {
        Request request = new Request(modForm.projectName(), modForm.displayName(), modForm.id(), modForm.author(), modForm.version(), modForm.uid(), modForm.destination(), modForm.iconPath());
        ModCreator.create(project, request);
    }

    public static void create(final @Nullable Project project, final Request request) {
        new Task.Backgroundable(project, "\u521b\u5efa BunnyLoader Mod", true){
            private String failure;

            public void run(@NotNull ProgressIndicator progressIndicator) {
                progressIndicator.setIndeterminate(true);
                try {
                    Path path = request.destination();
                    if (Files.exists(path, new LinkOption[0])) {
                        try (Stream<Path> stream = Files.list(path);){
                            if (stream.findAny().isPresent()) {
                                throw new IOException("\u76ee\u6807\u76ee\u5f55\u5df2\u7ecf\u5305\u542b\u6587\u4ef6\uff0c\u8bf7\u9009\u62e9\u7a7a\u76ee\u5f55\u3002");
                            }
                        }
                    }
                    progressIndicator.setText("\u751f\u6210 Mod \u5de5\u7a0b\u3001\u4ee3\u7801\u548c\u8d44\u6e90\u2026");
                    ModCreator.command(progressIndicator, List.of("new", "blmod", "-n", request.projectName(), "-o", path.toString(), "--modId", request.id(), "--modAuthor", request.author()));
                    progressIndicator.checkCanceled();
                    ModCreator.configureManifest(path, request);
                    progressIndicator.setText("\u521b\u5efa Rider / .NET \u89e3\u51b3\u65b9\u6848\u2026");
                    ModCreator.command(progressIndicator, List.of("new", "sln", "-n", request.projectName(), "--format", "slnx", "-o", path.toString()));
                    ModCreator.command(progressIndicator, List.of("sln", path.resolve(request.projectName() + ".slnx").toString(), "add", path.resolve(request.projectName() + ".csproj").toString()));
                    progressIndicator.checkCanceled();
                }
                catch (ProcessCanceledException processCanceledException) {
                    throw processCanceledException;
                }
                catch (Exception exception) {
                    this.failure = exception.getMessage() + "\n\n\u8bf7\u786e\u8ba4 .NET 10\u3001blmod \u6a21\u677f\u548c BunnyLoader SDK \u5df2\u5b89\u88c5\u3002\u5df2\u751f\u6210\u7684\u6587\u4ef6\u4f1a\u4fdd\u7559\uff0c\u8bf7\u68c0\u67e5\u76ee\u5f55\u540e\u91cd\u8bd5\u3002";
                }
            }

            public void onSuccess() {
                if (project != null && project.isDisposed()) {
                    return;
                }
                if (this.failure != null) {
                    Messages.showErrorDialog((Project)project, (String)this.failure, (String)"\u521b\u5efa BunnyLoader Mod \u5931\u8d25");
                    return;
                }
                LocalFileSystem.getInstance().refreshAndFindFileByNioFile(request.destination());
                ProjectUtil.openOrImport((Path)request.destination().resolve(request.projectName() + ".slnx"), (Project)project, (boolean)false);
            }

            public void onCancel() {
                if (project == null || !project.isDisposed()) {
                    Messages.showInfoMessage((Project)project, (String)"\u521b\u5efa\u5df2\u53d6\u6d88\uff0c\u5df2\u751f\u6210\u7684\u6587\u4ef6\u4fdd\u7559\u5728\u76ee\u6807\u76ee\u5f55\u4e2d\u3002", (String)"BunnyLoader Mod");
                }
            }
        }.queue();
    }

    static void configureManifest(Path path, Request request) throws IOException {
        Path path2 = path.resolve("manifest.json");
        JsonObject jsonObject = JsonParser.parseString((String)Files.readString(path2, StandardCharsets.UTF_8)).getAsJsonObject();
        jsonObject.addProperty("name", request.displayName());
        jsonObject.addProperty("version", request.version());
        jsonObject.addProperty("uid", request.uid());
        jsonObject.addProperty("author", request.author());
        Files.writeString(path2, (CharSequence)(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson((JsonElement)jsonObject) + "\n"), StandardCharsets.UTF_8, new OpenOption[0]);
        Files.writeString(path.resolve("description.md"), (CharSequence)("# " + request.displayName() + "\n"), StandardCharsets.UTF_8, new OpenOption[0]);
        if (request.icon() != null) {
            BufferedImage bufferedImage = ImageIO.read(request.icon().toFile());
            if (bufferedImage == null) {
                throw new IOException("\u65e0\u6cd5\u8bfb\u53d6\u6240\u9009\u56fe\u6807\u3002");
            }
            if (!ImageIO.write((RenderedImage)bufferedImage, "png", path.resolve("icon.png").toFile())) {
                throw new IOException("\u65e0\u6cd5\u4fdd\u5b58 icon.png\u3002");
            }
        }
    }

    private static void command(ProgressIndicator progressIndicator, List<String> list) throws Exception {
        progressIndicator.checkCanceled();
        GeneralCommandLine generalCommandLine = new GeneralCommandLine(new String[]{"dotnet"}).withParameters(list).withCharset(StandardCharsets.UTF_8);
        ProcessOutput processOutput = new CapturingProcessHandler(generalCommandLine).runProcessWithProgressIndicator(progressIndicator);
        if (processOutput.isCancelled()) {
            throw new ProcessCanceledException();
        }
        if (processOutput.getExitCode() != 0) {
            throw new IOException("dotnet " + list.get(0) + " \u5931\u8d25\uff08" + processOutput.getExitCode() + "\uff09\n" + processOutput.getStderr() + "\n" + processOutput.getStdout());
        }
    }

    public record Request(String projectName, String displayName, String id, String author, String version, String uid, Path destination, @Nullable Path icon) {
    }
}

