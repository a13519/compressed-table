package net.zousys.dir;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class Renamer {

    public static void main(String[] args) {
        Path rootPath = Paths.get("/Users/songzou/Downloads/aysheila");   // Change this
        String tag = "1.1.08";                               // Tag to search for

        renameDirectoriesContaining(rootPath, tag,"VERSION");
    }

    /**
     * Renames all files whose name contains the given tag
     */
    public static void renameFilesContaining(Path root, String tag, String newtag) {
        try (Stream<Path> walk = Files.walk(root)) {

            List<Path> filesToRename = walk
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().contains(tag))
                    .collect(Collectors.toList());

            for (Path oldPath : filesToRename) {
                String oldName = oldPath.getFileName().toString();
                String newName = oldName.replace(tag, newtag);
                Path newPath = oldPath.resolveSibling(newName);

                if (Files.exists(newPath)) {
                    log.warn("⚠️  Skipped (already exists): " + oldName);
                    continue;
                }

                try {
                    Files.move(oldPath, newPath);
                    log.info("✅ Renamed: " + oldName + " → " + newName);
                } catch (IOException e) {
                    log.error("❌ Failed to rename " + oldName + ": " + e.getMessage());
                }
            }

        } catch (IOException e) {
            log.error("Error walking directory: " + e.getMessage());
        }
    }

    public static void renameDirectoriesContaining(Path root, String tag, String replacement) {
        try (var walk = Files.walk(root)) {   // depth = 1 to only direct subdirs

            walk.filter(Files::isDirectory)
                    .filter(path -> !path.equals(root))   // skip root itself
                    .filter(path -> path.getFileName().toString().contains(tag))
                    .forEach(oldDir -> {
                        String oldName = oldDir.getFileName().toString();
                        String newName = oldName.replace(tag, replacement);
                        Path newDir = oldDir.resolveSibling(newName);

                        if (Files.exists(newDir)) {
                            System.out.println("⚠️ Skipped (already exists): " + oldName);
                            return;
                        }

                        try {
                            Files.move(oldDir, newDir);
                            System.out.println("✅ Renamed dir: " + oldName + " → " + newName);
                        } catch (IOException e) {
                            System.out.println("❌ Failed: " + oldName + " - " + e.getMessage());
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
