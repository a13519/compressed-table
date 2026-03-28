package samples;

import net.zousys.bucketcomp.comparability.CSVSource;
import net.zousys.bucketcomp.comparability.CompConfig;
import net.zousys.bucketcomp.comparability.Source;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;

/**
 * 
 */
public class XMLComparatorSample {
    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        CompListener listener = new CompListener();
        Yaml yaml = new Yaml(new Constructor(CompConfig.class, new LoaderOptions()));
        CompConfig config = yaml.load("configIS");
        FileUtils.deleteQuietly(new File(config.getBucket()));

        // setup xml sources, TODO

    }
}
