Process a list of image URLs. For each image, find the three most common rgb values. Output a CSV file with an entry for each unique URL listing the URL and rgb values.

Build and run with 'run.sh'.

I noticed that the input files have a lot of redundant URLs and added a URL cache to prevent needless reprocessing.

I first tested the code without the URL cache, and saw a speedup of x4 when running with 8 threads as compared to single threaded.

With the cache enabled, the overall processing time drops pretty dramatically, but the speedup for multi-threading goes down to about x3, which makes sense considering that execution time in this case is only a few seconds.
