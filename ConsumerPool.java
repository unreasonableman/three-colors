public class ConsumerPool {
    public ConsumerPool(int instanceCount, URLProducer urlProducer, ResultPrinter resultPrinter) {
        for (int i=0; i<instanceCount; i++) {
            new ImageAnalyzer(urlProducer, resultPrinter);
        }
    }
}