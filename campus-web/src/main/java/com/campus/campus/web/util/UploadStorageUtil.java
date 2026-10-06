package com.campus.campus.web.util;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

public final class UploadStorageUtil {
    private static final Path DEFAULT_UPLOAD_DIRECTORY = Path.of("C:", "Users", "willson", "Desktop", "event-uploads");
    private static final Pattern SAFE_FILE_CHARS = Pattern.compile("[^a-zA-Z0-9._-]");

    private UploadStorageUtil() {
    }

    public static Path resolvePersistentUploadDirectory(HttpServletRequest request) {
        String configuredFromEnv = System.getenv("CAMPUS_UPLOAD_DIR");
        if (configuredFromEnv != null && !configuredFromEnv.isBlank()) {
            return Path.of(configuredFromEnv).toAbsolutePath().normalize();
        }

        String configuredFromProperty = System.getProperty("campus.upload.dir");
        if (configuredFromProperty != null && !configuredFromProperty.isBlank()) {
            return Path.of(configuredFromProperty).toAbsolutePath().normalize();
        }

        return DEFAULT_UPLOAD_DIRECTORY.toAbsolutePath().normalize();
    }

    public static Set<Path> resolvePersistentUploadCandidates(HttpServletRequest request) {
        Set<Path> candidates = new LinkedHashSet<>();
        String configured = System.getenv("CAMPUS_UPLOAD_DIR");
        if (configured != null && !configured.isBlank()) {
            candidates.add(Path.of(configured).toAbsolutePath().normalize());
        }
        String configuredFromProperty = System.getProperty("campus.upload.dir");
        if (configuredFromProperty != null && !configuredFromProperty.isBlank()) {
            candidates.add(Path.of(configuredFromProperty).toAbsolutePath().normalize());
        }
        candidates.add(DEFAULT_UPLOAD_DIRECTORY.toAbsolutePath().normalize());
        candidates.add(Path.of(System.getProperty("user.home"), ".campus-web", "uploads").toAbsolutePath().normalize());
        // Keep old location as fallback for previously uploaded images.
        candidates.add(Path.of(System.getProperty("user.home"), "campus-web-uploads").toAbsolutePath().normalize());
        return candidates;
    }

    public static Path resolveLegacyUploadDirectory(ServletContext context) {
        String webUploadsPath = context.getRealPath("/uploads");
        if (webUploadsPath != null && !webUploadsPath.isBlank()) {
            return Path.of(webUploadsPath);
        }
        Object tmpDirAttribute = context.getAttribute("jakarta.servlet.context.tempdir");
        if (tmpDirAttribute instanceof File tmpDir) {
            return tmpDir.toPath().resolve("campus-web-uploads");
        }
        return Path.of(System.getProperty("java.io.tmpdir"), "campus-web-uploads");
    }

    public static String buildStoredFileName(String originalFileName) {
        String cleaned = "image";
        if (originalFileName != null && !originalFileName.isBlank()) {
            String baseName = Path.of(originalFileName).getFileName().toString();
            cleaned = SAFE_FILE_CHARS.matcher(baseName).replaceAll("_");
            if (cleaned.isBlank()) {
                cleaned = "image";
            }
        }
        return System.currentTimeMillis() + "_" + cleaned;
    }
}
