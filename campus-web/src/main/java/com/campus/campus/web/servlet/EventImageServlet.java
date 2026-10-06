package com.campus.campus.web.servlet;

import com.campus.campus.web.util.UploadStorageUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@WebServlet(urlPatterns = {"/media/event-image", "/ImageServlet"})
public class EventImageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileParam = request.getParameter("name");
        if (fileParam == null || fileParam.isBlank()) {
            fileParam = request.getParameter("image");
        }
        if (fileParam == null || fileParam.isBlank()) {
            fileParam = request.getParameter("file");
        }
        if (fileParam == null || fileParam.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing image parameter.");
            return;
        }

        String normalized = fileParam.replace("\\", "/").trim();
        if (normalized.isBlank() || normalized.contains("..")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid file name.");
            return;
        }

        Path imagePath = resolveImagePath(request, normalized);
        if (imagePath == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String contentType = Files.probeContentType(imagePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        response.setContentType(contentType);
        response.setContentLengthLong(Files.size(imagePath));
        Files.copy(imagePath, response.getOutputStream());
    }

    private Path resolveImagePath(HttpServletRequest request, String normalizedInput) {
        // Support legacy absolute DB values if they still exist.
        try {
            Path rawPath = Path.of(normalizedInput).normalize();
            if (rawPath.isAbsolute() && Files.isRegularFile(rawPath)) {
                migrateToPersistentDirectory(request, rawPath, rawPath.getFileName().toString());
                return rawPath;
            }
        } catch (InvalidPathException ignored) {
            // Continue with candidate-based resolution.
        }

        String fileName = normalizedInput.substring(normalizedInput.lastIndexOf('/') + 1);
        Set<String> relativeCandidates = new LinkedHashSet<>();
        relativeCandidates.add(normalizedInput);
        relativeCandidates.add(normalizedInput.startsWith("/") ? normalizedInput.substring(1) : normalizedInput);
        if (normalizedInput.startsWith("uploads/")) {
            relativeCandidates.add(normalizedInput.substring("uploads/".length()));
        }
        relativeCandidates.add(fileName);

        for (Path dir : candidateUploadDirectories(request)) {
            for (String candidate : relativeCandidates) {
                if (candidate == null || candidate.isBlank()) {
                    continue;
                }
                Path filePath = dir.resolve(candidate).normalize();
                if (Files.isRegularFile(filePath)) {
                    migrateToPersistentDirectory(request, filePath, fileName);
                    return filePath;
                }
            }
        }
        return null;
    }

    private void migrateToPersistentDirectory(HttpServletRequest request, Path source, String fileName) {
        if (fileName == null || fileName.isBlank() || source == null || !Files.isRegularFile(source)) {
            return;
        }
        try {
            Path persistentDir = UploadStorageUtil.resolvePersistentUploadDirectory(request);
            Files.createDirectories(persistentDir);
            Path target = persistentDir.resolve(fileName).normalize();
            if (!Files.exists(target)) {
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ignored) {
            // Best-effort migration only.
        }
    }

    private List<Path> candidateUploadDirectories(HttpServletRequest request) {
        Set<Path> unique = new LinkedHashSet<>();
        String webUploadsPath = request.getServletContext().getRealPath("/uploads");
        if (webUploadsPath != null && !webUploadsPath.isBlank()) {
            unique.add(Path.of(webUploadsPath));
        }

        unique.addAll(UploadStorageUtil.resolvePersistentUploadCandidates(request));
        unique.add(UploadStorageUtil.resolveLegacyUploadDirectory(request.getServletContext()));

        String webRootPath = request.getServletContext().getRealPath("/");
        if (webRootPath != null && !webRootPath.isBlank()) {
            unique.add(Path.of(webRootPath).resolve("uploads"));
        }

        Object tmpDirAttribute = request.getServletContext().getAttribute("jakarta.servlet.context.tempdir");
        if (tmpDirAttribute instanceof File tmpDir) {
            unique.add(tmpDir.toPath().resolve("campus-web-uploads"));
        }

        unique.add(Path.of(System.getProperty("java.io.tmpdir"), "campus-web-uploads"));
        unique.add(Path.of(System.getProperty("user.dir"), "uploads"));

        // GlassFish legacy path where older uploads may have been written.
        String instanceRoot = System.getProperty("com.sun.aas.instanceRoot");
        String appName = request.getContextPath() == null ? "" : request.getContextPath().replaceFirst("^/", "");
        if (instanceRoot != null && !instanceRoot.isBlank()) {
            Path generatedJsp = Path.of(instanceRoot, "generated", "jsp");
            if (!appName.isBlank()) {
                unique.add(generatedJsp.resolve(appName).resolve("uploads"));
            }
            unique.add(generatedJsp.resolve("uploads"));

            // Also include versioned deployment folders, e.g. campus-web-1.0-SNAPSHOT/uploads
            addChildUploadsDirectories(generatedJsp, unique);

            // Legacy exploded deployment locations in GlassFish
            Path applicationsDir = Path.of(instanceRoot, "applications");
            if (!appName.isBlank()) {
                unique.add(applicationsDir.resolve(appName).resolve("uploads"));
            }
            addChildUploadsDirectories(applicationsDir, unique);
        }

        List<Path> candidates = new ArrayList<>();
        for (Path path : unique) {
            if (Files.exists(path)) {
                candidates.add(path);
            }
        }
        return candidates;
    }

    private void addChildUploadsDirectories(Path parent, Set<Path> unique) {
        if (parent == null || !Files.isDirectory(parent)) {
            return;
        }
        try (DirectoryStream<Path> children = Files.newDirectoryStream(parent)) {
            for (Path child : children) {
                if (Files.isDirectory(child)) {
                    unique.add(child.resolve("uploads"));
                }
            }
        } catch (IOException ignored) {
            // Best-effort legacy scan; ignore inaccessible folders.
        }
    }
}
