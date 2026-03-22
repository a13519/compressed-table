package samples;

import net.zousys.bucketcomp.Comparator;

public class BucketComparatorSample {
    public static void main(String[] args) throws Exception {
        CompListener listener = new CompListener();
        System.out.println( Math.abs("823949076hruf398hukey".hashCode()) % 2);
        new Comparator("path to/ucompconf.yml",
                "path to before csv",
                "path to after csv", listener).compare();

    }
}
