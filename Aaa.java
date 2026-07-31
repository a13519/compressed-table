import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpClient;
import java.net.HttpRequest;
import java.net.HttpResponse;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;

public class AzureRepoLineCounter {

    // ======= Configure Here =======
    private static final String ORGANIZATION = "yourOrg";
    private static final String PROJECT = "yourProject";
    private static final String REPOSITORY = "yourRepo";
    private static final String PAT = "xxxxxxxxxxxxxxxxxxxxxxxx";

    private static final Set<String> SOURCE_EXTENSIONS = Set.of(
            ".java",
            ".xml",
            ".sql",
            ".properties",
            ".json",
            ".yaml",
            ".yml",
            ".csv",
            ".txt",
            ".js",
            ".ts",
            ".html",
            ".css",
            ".md"
    );

    public static void main(String[] args) throws Exception {

        String auth = Base64.getEncoder()
                .encodeToString((":" + PAT).getBytes(StandardCharsets.UTF_8));

        String url = String.format(
                "https://dev.azure.com/%s/%s/_apis/git/repositories/%s/items"
                        + "?scopePath=/"
                        + "&download=true"
                        + "&$format=zip"
                        + "&api-version=7.1",
                ORGANIZATION,
                PROJECT,
                REPOSITORY);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Basic " + auth)
                .GET()
                .build();

        HttpResponse<java.io.InputStream> response =
                client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() != 200) {
            System.out.println("Download failed.");
            System.out.println(response.statusCode());
            return;
        }

        long totalFiles = 0;
        long totalLines = 0;

        try (ZipArchiveInputStream zip =
                     new ZipArchiveInputStream(response.body())) {

            ZipArchiveEntry entry;

            while ((entry = zip.getNextZipEntry()) != null) {

                if (entry.isDirectory()) {
                    continue;
                }

                String name = entry.getName();

                if (!isSourceFile(name)) {
                    continue;
                }

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(zip, StandardCharsets.UTF_8));

                long lines = 0;

                while (reader.readLine() != null) {
                    lines++;
                }

                totalFiles++;
                totalLines += lines;

                System.out.printf("%7d  %s%n", lines, name);
            }
        }

        System.out.println();
        System.out.println("====================================");
        System.out.println("Files : " + totalFiles);
        System.out.println("Lines : " + totalLines);
    }

    private static boolean isSourceFile(String filename) {

        String lower = filename.toLowerCase();

        return SOURCE_EXTENSIONS.stream()
                .anyMatch(lower::endsWith);
    }

}
