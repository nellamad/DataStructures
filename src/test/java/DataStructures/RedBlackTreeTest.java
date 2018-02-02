package DataStructures;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

class RedBlackTreeTest {
    private static final Logger logger = Logger.getLogger(RedBlackTreeTest.class.toString());
    private static String[] input_file_names = Stream.of("src/test/resources/zero_int.txt",
            "src/test/resources/ten_int.txt",
            "src/test/resources/hundred_int.txt").toArray(String[]::new);

    @BeforeAll
    static void setUpAll() {

    }

    @BeforeEach
    void setUp() {

    }

    @Test
    void comprehensiveTest() {
        ArrayList<Integer> nodeData = new ArrayList<>();
        for (String file_name : input_file_names) {
            logger.info("Starting test with input from: " + file_name);
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

            logger.fine("Starting insert test");
            for (int data : nodeData) {
                logger.fine("Adding " + data);
                tree.insert(new RedBlackNode(data));
                tree.printTree();
                tree.validate();
            }

            logger.fine("Starting search test");
            for (int data: nodeData) {
                assert tree.search(data) : String.format("Value %s should be in tree, but was not found", data);
            }

            logger.fine("Starting delete test");
            for (int data : nodeData) {
                logger.fine("Deleting " + data);
                tree.delete(data);
                tree.printTree();
                tree.validate();
            }
        }
    }
}