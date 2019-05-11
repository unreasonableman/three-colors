import java.awt.image.BufferedImage;
import java.net.URLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;

public class ImageAnalyzer implements Runnable {
    private static final int DUMMY_RGB = 0xff000000;
    private static final Object CACHE_LOCK = new Object();
    private static final int TOP_LIST_COUNT = 3;

    private static final Set<URL> urlCache = new HashSet<URL>();

    private List<CountData> topList = new ArrayList<CountData>();
    private Map<Integer, Integer> colorMap = new HashMap<Integer, Integer>();
    private URLProducer urlProducer;
    private ResultPrinter resultPrinter;

    private class CountData implements Comparable<CountData> {
        Integer count;
        Integer rgb;

        CountData(int count, int rgb) {
            this.count = count;
            this.rgb = rgb;
        }

        public int compareTo(CountData cd) {
            return cd.count - count;
        }

        public String toString() {
            return "[" + (rgb == DUMMY_RGB ? "<missing>" : String.format("%06x", rgb)) + ": "+ count + "]";
        }
    }

    public ImageAnalyzer(URLProducer urlProducer, ResultPrinter resultPrinter) {
        this.urlProducer = urlProducer;
        this.resultPrinter = resultPrinter;

        Thread thread = new Thread(this);
        thread.start();
    }

    private void reset() {
        topList.clear();
        colorMap.clear();

        for (int i=0; i<TOP_LIST_COUNT; i++) {
            topList.add(new CountData(0, DUMMY_RGB));
        }
    }

    private BufferedImage getImage(URL url) {
        try {
            URLConnection conn = url.openConnection();
            conn.connect();
            return ImageIO.read(conn.getInputStream());
        } catch (Exception e) {
            System.err.println("could not get image for " + url + ": " + e);
            return null;
        }
    }

    // Randomly sample 10% of the img pixels. This should give decent results
    // for most real-world images
    private void collectColorData(BufferedImage img) {
        int limit = img.getWidth() * img.getHeight() / 10;
        int i;

        for (i=0; i<limit; i++) {
            int y = (int)(Math.random() * img.getHeight());
            int x = (int)(Math.random() * img.getWidth());
            int rgb = img.getRGB(x, y) & 0xffffff;

            Integer count = colorMap.get(rgb);

            if (count == null) {
                colorMap.put(rgb, 0);
                count = colorMap.get(rgb);
            }

            int newCount = count.intValue() + 1;
            colorMap.put(rgb, newCount);

            CountData cd = topList.get(2);

            if (newCount > cd.count) {
                int j = 0;

                for (; j<topList.size(); j++) {
                    CountData tcd = topList.get(j);

                    if (tcd.rgb == rgb) {
                        tcd.count++;
                        break;
                    }
                }

                if (j == topList.size()) {
                    topList.add(new CountData(newCount, rgb));
                }

                Collections.sort(topList);

                while (topList.size() > TOP_LIST_COUNT) {
                    topList.remove(topList.size() - 1);
                }
            }
        }
    }

    private void printResults(URL url) {
        StringBuilder sb = new StringBuilder();
        sb.append(url);

        for (int i=0; i<3; i++) {
            sb.append(',');

            CountData cd = topList.get(i);

            if (cd.rgb == DUMMY_RGB) {
                sb.append("<missing>");
            } else {
                sb.append(String.format("#%06X", cd.rgb));
            }
        }

        resultPrinter.addResult(sb.toString());
    }

    public void run() {
        for (;;) {
            URL url = urlProducer.getNextURL();
            if (url == null) break;

            // don't re-process URLs we've already seen
            // The provided input file has ~25 copies of
            // each URL.
            // This can save a lot of redundant processing,
            // but can also lead to problems in the case
            // of an input file with 1B (unique) entries,
            // as is mentioned in the problem statement.
            synchronized (CACHE_LOCK) {
                if (urlCache.contains(url)) continue;
                urlCache.add(url);
            }

            reset();

            BufferedImage img = getImage(url);
            if (img == null) {
                continue;
            }

            collectColorData(img);
            printResults(url);
        }
    }
}
