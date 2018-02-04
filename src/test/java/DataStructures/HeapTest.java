package DataStructures;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

class HeapTest {
    private static final Logger logger = Logger.getLogger(HeapTest.class.getName());
    private static final Level consoleLogLevel = Level.INFO;
    private static String[] input_file_names = Stream.of("src/test/resources/zero_int.txt",
            "src/test/resources/ten_int.txt",
            "src/test/resources/hundred_int.txt").toArray(String[]::new);

    @BeforeAll
    static void setUpAll() {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(consoleLogLevel);
        logger.addHandler(handler);
        logger.setLevel(Level.ALL);

        logger.info("Starting Heap tests...");
    }

    @Test
    void insert() {
        ArrayList<Integer> nodeData = new ArrayList<>();
        for (String file_name : input_file_names) {
            logger.fine("Starting test with input from: " + file_name);
            Heap heap = new Heap();
            try {
                // need to specify utf-16 encoding since our test input is generated from python
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file_name), "UTF-16"));
                while(in.ready()) {
                    String line = in.readLine().trim();
                    nodeData.add(Integer.parseInt(line));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            logger.fine("Starting push test");
            for (int data : nodeData) {
                logger.finer("Adding " + data);
                heap.push(data);
                //heap.print();
                heap.validate();
            }
            assert heap.size() == nodeData.size()
                    : String.format("Inserted %d items but heap size is %d", nodeData.size(), heap.size());

            /*
            logger.finer("Starting search test");
            for (int data: nodeData) {
                assert tree.search(data) : String.format("Value %s should be in tree, but was not found", data);
            }
            */

            nodeData.sort(null);
            logger.fine(String.format("Starting pop test expecting %s", nodeData));
            int previous = Integer.MIN_VALUE;
            for (int data : nodeData) {
                logger.finer("Popping " + data);
                int current = heap.pop();

                assert previous <= current
                        : String.format("Previously popped item %d is greater than %d", previous, current);
                heap.validate();

                previous = current;
            }

        }
        logger.info("Passed");
    }
}