package net.zousys.bucketcomp;

import lombok.extern.log4j.Log4j2;
import net.zousys.bucketcomp.comparability.*;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * after bucket : 943
 * before bucket : 944
 */
@Log4j2
public class Comparator {
    private ComparatorContext comparatorContext;

    /**
     *
     * @param configFile
     * @param beforeSource
     * @param afterSource
     * @param listener
     * @throws IOException
     */
    public Comparator(String configFile, Source beforeSource, Source afterSource, ComparatorListener listener) throws IOException {
        this(new FileInputStream(configFile), beforeSource, afterSource, listener);
    }

    /**
     * @param comparatorContext
     * @throws Exception
     */
    public Comparator(ComparatorContext comparatorContext) throws Exception {
        this.comparatorContext = comparatorContext;
    }

    /**
     *
     * @param config
     * @param beforeSource
     * @param afterSource
     * @param listener
     * @throws IOException
     */
    public Comparator(CompConfig config, Source beforeSource, Source afterSource, ComparatorListener listener) throws IOException {
        FileUtils.deleteQuietly(new File(config.getBucket()));

        comparatorContext = ComparatorContext.builder()
                .beforeSource(beforeSource)
                .afterSource(afterSource)
                .config(config)
                .listener(listener).build();
        comparatorContext.init();
    }
    /**
     *
     * @param configIS
     * @param beforeSource
     * @param afterSource
     * @param listener
     * @throws IOException
     */
    public Comparator(InputStream configIS, Source beforeSource, Source afterSource, ComparatorListener listener) throws IOException {
        Yaml yaml = new Yaml(new Constructor(CompConfig.class, new LoaderOptions()));
        CompConfig config = yaml.load(configIS);

        FileUtils.deleteQuietly(new File(config.getBucket()));

        comparatorContext = ComparatorContext.builder()
                .beforeSource(beforeSource)
                .afterSource(afterSource)
                .config(config)
                .listener(listener).build();
        comparatorContext.init();
    }

    /**
     * @throws Exception
     */
    public void compare() throws Exception {
        for (int i = 0; i < comparatorContext.getConfig().getBucketNumber(); i++) {
            BucketComparator.compare(i, comparatorContext);
        }
        log.info("Comparison finished.");
    }
}
