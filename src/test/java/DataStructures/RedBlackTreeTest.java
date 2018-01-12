package DataStructures;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

class RedBlackTreeTest {
    private static String[] input_file_names = Stream.of("src/test/resources/zero_int.txt",
            "src/test/resources/five_int.txt",
            "src/test/resources/hundred_int.txt").toArray(String[]::new);

    @BeforeAll
    static void setUpAll() {

    }

    @BeforeEach
    void setUp() {

    }

    @Test
    void insertAndDelete() {
        ArrayList<Integer> nodeData = new ArrayList<>();
        for (String file_name : input_file_names) {
            System.out.println("Starting test with input from: " + file_name);
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

            System.out.println("Starting insert test");
            for (int data : nodeData) {
                System.out.println("Adding " + data);
                tree.insert(new RedBlackNode(data));
                tree.printTree();
                tree.validate();
            }

            System.out.println("Starting delete test");
            for (int data : nodeData) {
                System.out.println("Deleting " + data);
                tree.delete(data);
                tree.printTree();
                tree.validate();
            }
        }
    }
}