package DataStructures;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RedBlackTreeTest {
    RedBlackTree tree;
    static ArrayList<Integer> nodeData = new ArrayList<>();

    @BeforeAll
    static void setUpAll() {

        try {
            // need to specify utf-16 encoding since our test input is generated from python
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("src/test/resources/hundred_int.txt"), "UTF-16"));
            while(in.ready()) {
                String line = in.readLine().trim();
                nodeData.add(Integer.parseInt(line));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setUp() {
        tree = new RedBlackTree();
    }


    @Test
    void insert() {
        System.out.println("Starting insert test");
        for (int data : nodeData) {
            System.out.println("Adding " + data);
            tree.insert(new Node(data));
            tree.printTree();
        }

    }
}