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
     * @param configFile
     * @param beforeFile
     * @param afterFile
     * @throws IOException
     */
    public Comparator(String configFile, String beforeFile, String afterFile, ComparatorListener listener) throws IOException {
        this(new FileInputStream(configFile), new FileInputStream(beforeFile), new FileInputStream(afterFile), listener);
    }

    /**
     * @param comparatorContext
     * @throws Exception
     */
    public Comparator(ComparatorContext comparatorContext) throws Exception {
        this.comparatorContext = comparatorContext;
    }

    /**
     * @param configIS
     * @param beforeIS
     * @param afterIS
     */
    public Comparator(InputStream configIS, InputStream beforeIS, InputStream afterIS, ComparatorListener listener) throws IOException {
        Yaml yaml = new Yaml(new Constructor(CompConfig.class, new LoaderOptions()));

        CompConfig config = yaml.load(configIS);
        FileUtils.deleteQuietly(new File(config.getBucket()));
        Source beforeSource = new Source("before", config, beforeIS);
        Source afterSource = new Source("after", config, afterIS);

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
