import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileInputStream;
import java.net.URL;

public class URLProducer {
    BufferedReader in;

    public URLProducer(String filename) throws IOException {
        in = new BufferedReader(
            new InputStreamReader(
                new FileInputStream(filename)
            )
        );
    }

    public synchronized URL getNextURL() {
        String s  = null;

        try {
            s = in.readLine();
            if (s == null) return null;

            return new URL(s);
        } catch (Exception e) {
            System.err.println("could not get next url: " + e);
            return null;
        }
    }

    public void close() {
        try {
            if (in != null) {
                in.close();
                in = null;
            }
        } catch (Exception e) {
            System.err.println("could not close input reader: " + e);
        }
    }
}