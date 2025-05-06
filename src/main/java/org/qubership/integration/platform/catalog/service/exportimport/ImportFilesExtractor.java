package org.qubership.integration.platform.catalog.service.exportimport;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


@RequiredArgsConstructor
public class ImportFilesExtractor {
    private final String entityNamePrefix;
    private final String archParentDir;

    public List<File> extract(File inputArchFile, String importFolderName) throws IOException {
        return extractFromZip(FileUtils.openInputStream(inputArchFile), importFolderName);
    }

    public List<File> extractFromZip(InputStream is, String importFolderName) throws IOException {
        try (ZipInputStream inputStream = new ZipInputStream(is)) {
            extractZip(importFolderName, inputStream, archParentDir);

            return extractFromImportDirectory(importFolderName);
        }
    }

    public String extractEntityIdFromFileName(File file) {
        return file.getName().substring(this.entityNamePrefix.length(), file.getName().lastIndexOf("."));
    }

    public List<File> extractFromImportDirectory(String importFolderName) throws IOException {
        Path start = Paths.get(importFolderName + File.separator + archParentDir);
        if (Files.exists(start)) {
            try (Stream<Path> sp = Files.walk(start)) {
                return sp.filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .filter(f -> f.getName().startsWith(entityNamePrefix) && f.getName().endsWith(ExportImportConstants.YAML_EXTENSION))
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }

    private void extractZip(String importFolderName, ZipInputStream inputStream, String archParentDir) throws IOException {
        Path path = Paths.get(importFolderName);

        for (ZipEntry entry; (entry = inputStream.getNextEntry()) != null; ) {
            Path resolvedPath = path.resolve(entry.getName());
            Path normalizedResolvedPath = resolvedPath.normalize();
            Path entryPath = Paths.get(entry.getName());

            if (entryPath.startsWith(archParentDir) && normalizedResolvedPath.startsWith(path)) {
                if (!entry.isDirectory()) {
                    Files.createDirectories(resolvedPath.getParent());
                    Files.copy(inputStream, resolvedPath);
                    Files.setLastModifiedTime(resolvedPath, FileTime.fromMillis(entry.getTime()));
                } else {
                    Files.createDirectories(resolvedPath);
                }
            }
        }
    }
}
