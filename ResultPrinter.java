import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class ResultPrinter {
    PrintWriter out;

    public ResultPrinter() {
        try {
            out = new PrintWriter(new FileOutputStream("output.csv"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void addResult(String result) {
        out.println(result);
    }

    public void close() {
        out.close();
    }
}