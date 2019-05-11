public class ThreeColors {
    private static long then;
    private URLProducer urlProducer;
    private ResultPrinter resultPrinter;

    public ThreeColors(String fileName, int threadCount) throws Exception {
        urlProducer = new URLProducer(fileName);
        resultPrinter = new ResultPrinter();

        then = System.currentTimeMillis();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                urlProducer.close();
                resultPrinter.close();

                System.out.println("" + (System.currentTimeMillis() - then) + "ms.");
            }
        });

        new ConsumerPool(threadCount, urlProducer, resultPrinter);
    }

    private static void usage() {
        System.err.println("usage: ThreeColors -input <filename> [-threadcount <n>]");
        System.exit(-1);
    }

    public static void main(String[] arg) throws Exception {
        int threadCount = 1;
        String fileName = null;

        for (int i=0; i<arg.length; i++) {
            if (arg[i].equals("-input") && arg.length >= i + 1) {
                fileName = arg[++i];
            }

            if (arg[i].equals("-threadcount") && arg.length >= i + 1) {
                try {
                    threadCount = Integer.parseInt(arg[++i]);
                } catch (NumberFormatException e) {
                    usage();
                }
            }
        }

        System.out.println("- fileName: " + fileName);
        System.out.println("- threadCount: " + threadCount);

        if (fileName == null) {
            usage();
        }

        new ThreeColors(fileName, threadCount);
    }
}