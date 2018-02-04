package DataStructures;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

class RedBlackTreeTest {
    private static final Logger logger = Logger.getLogger(RedBlackTreeTest.class.getName());
    private static String[] input_file_names = Stream.of("src/test/resources/zero_int.txt",
            "src/test/resources/ten_int.txt",
            "src/test/resources/hundred_int.txt").toArray(String[]::new);

    @BeforeAll
    static void setUpAll() {
        logger.info("Starting RedBlackTree tests...");
    }

    @BeforeEach
    void setUp() {

    }

    /**
     * Tests the RedBlackTree insert, search and delete methods
     */
    @Test
    void comprehensiveTest() {
        ArrayList<Integer> nodeData = new ArrayList<>();
        for (String file_name : input_file_names) {
            logger.fine("Starting test with input from: " + file_name);
            RedBlackTree tree = new RedBlackTree();
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

            logger.finer("Starting insert test");
            for (int data : nodeData) {
                logger.finer("Adding " + data);
                tree.insert(data);
                tree.printTree();
                tree.validate();
            }

            logger.finer("Starting search test");
            for (int data: nodeData) {
                assert tree.search(data) : String.format("Value %s should be in tree, but was not found", data);
            }

            logger.finer("Starting delete test");
            for (int data : nodeData) {
                logger.finer("Deleting " + data);
                tree.delete(data);
                tree.printTree();
                tree.validate();
            }
        }
        logger.info("Passed");
    }
}