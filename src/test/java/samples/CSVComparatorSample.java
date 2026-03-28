package samples;

import net.zousys.bucketcomp.Comparator;
import net.zousys.bucketcomp.comparability.CSVSource;
import net.zousys.bucketcomp.comparability.CompConfig;
import net.zousys.bucketcomp.comparability.Source;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;

/**
 *
 */
public class CSVComparatorSample {
    /**
     *
     * @throws Exception
     */
    @Test
    public void testCSV() throws Exception {
        CompListener listener = new CompListener();
        Yaml yaml = new Yaml(new Constructor(CompConfig.class, new LoaderOptions()));
        CompConfig config = yaml.load(new FileInputStream("../resources/ucompconf.yml"));
        FileUtils.deleteQuietly(new File(config.getBucket()));
        Source beforeSource = new CSVSource("before", config, new FileInputStream("../resources/customers-2000000a.csv"));
        Source afterSource = new CSVSource("after", config, new FileInputStream("../resources/customers-2000000a.csv"));
        Comparator csvComparator = new Comparator(config, beforeSource, afterSource, listener);
        csvComparator.compare();
    }
}
