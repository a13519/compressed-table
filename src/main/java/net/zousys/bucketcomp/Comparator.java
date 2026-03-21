package net.zousys.bucketcomp;

import lombok.extern.log4j.Log4j2;
import net.zousys.bucketcomp.comparability.*;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Log4j2
public class Comparator {
    CompConfig config;

    public static void main(String[] args) throws Exception {
        Yaml yaml = new Yaml(new Constructor(CompConfig.class, new LoaderOptions()));

        try (InputStream is = Files.newInputStream(Paths.get("/Users/songzou/Documents/IdeaProjects/compressed-table/src/main/resources/ucompconf.yml"))) {
            CompConfig config = yaml.load(is);
            System.out.println(config);

            new Comparator(config,
                    "/Users/songzou/Documents/IdeaProjects/compressed-table/src/test/resources/customers-1000b.csv",
                    "/Users/songzou/Documents/IdeaProjects/compressed-table/src/test/resources/customers-1000a.csv");
        }
    }

    /**
     * @param config
     * @param beforeFile
     * @param afterFile
     * @throws IOException
     */
    public Comparator(CompConfig config, String beforeFile, String afterFile) throws IOException {

        try {
            CompListener listener = new CompListener();

            Source beforeSource = new Source("before", config, new BufferedReader(new FileReader(beforeFile)));
            Source afterSource = new Source("after", config, new BufferedReader(new FileReader(afterFile)));

            ComparatorContext comparatorContext = ComparatorContext.builder()
                    .beforeSource(beforeSource)
                    .afterSource(afterSource)
                    .config(config)
                    .listener(listener).build();
            comparatorContext.init();

            for (int i = 0; i < config.getBucketNumber(); i++) {
                BucketComparator.compare(i, comparatorContext);
            }

            log.info("\nComparison finished.");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
