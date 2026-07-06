package net.zousys.dir;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class Collector {

    List<String> newlyAdded = new ArrayList<>();
    List<String> newlyremoved = new ArrayList<>();
    /**
     * Returns the relative path from root to the target file
     */
    public static String getRelativePath(Path root, Path fullPath) {
        if (!fullPath.startsWith(root)) {
            throw new IllegalArgumentException("Path is not inside root: " + fullPath);
        }
        return root.relativize(fullPath).toString();
    }

    /**
     * Returns just the filename (e.g. "Test.java")
     */
    public static String getFileName(Path path) {
        return path.getFileName().toString();
    }


    /**
     *
     * @param root
     * @return
     */
    public Set<String> extractRelativePath(Path root) {
        try (Stream<Path> walk = Files.walk(root)) {
            List<Path> fileList = walk
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith("png") ||
                            path.getFileName().toString().endsWith("xlsx"))
                    .collect(Collectors.toList());

            Set<String> result = new HashSet<>();
            for (Path entry : fileList) {
                result.add(getRelativePath(root, entry));
            }
            return result;
        } catch (IOException e) {
            log.error("Error walking directory: " + e.getMessage());
        }
        return null;
    }

    /**
     *
     * @param pathOld
     * @param pathNew
     */
    public void matchEntries(Path pathOld, Path pathNew) {
        Set<String> entriesOld = extractRelativePath(pathOld);
        Set<String> entriesNew = extractRelativePath(pathNew);

        entriesOld.forEach(entry -> {
            if (!entriesNew.contains(entry)) {
                newlyremoved.add(entry);
            };
        });
        entriesNew.forEach(entry -> {
            if (!entriesOld.contains(entry)) {
                newlyAdded.add(entry);
            };
        });

    }

    public static void main(String[] args) {
        Path rootPath = Paths.get("/Users/songzou/Downloads/aysheila");        // Your root directory
        Path fullPath = Paths.get("/Users/songzou/Downloads/yt-dlp");

        Collector cr = new Collector();
        cr.matchEntries(rootPath, fullPath);

    }
}
