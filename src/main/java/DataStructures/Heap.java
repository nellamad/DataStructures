package DataStructures;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * An implementation of a min-heap using an arraylist as the underlying data structure.  O(log n) time complexity
 * for both push() and pop() operations.
 *
 */
public class Heap {
    private static final Logger logger = Logger.getLogger(Logger.class.getName());
    private static final int rootIndex = 0;
    private int tailIndex = 0;
    private ArrayList<Integer> elements = new ArrayList<>();

    /**
     * Pushes the given value onto the heap and then performs necessary maintenance operations to maintain the heap
     * property.
     * @param value A value to insert into the heap
     */
    public void push(int value) {
        elements.add(tailIndex, value);
        upHeap(tailIndex);
        tailIndex++;
    }

    /**
     * Pops the minimal element of the heap and then performs necessary maintenance operations to maintain the heap
     * property.
     * @return The minimal element of the heap, or null if the heap is empty.
     */
    public Integer pop() {
        if (elements.isEmpty()) {
            return null;
        }
        int root = elements.get(rootIndex);
        elements.set(rootIndex, elements.get(tailIndex - 1));
        elements.remove(tailIndex - 1);
        tailIndex--;
        downHeap(0);

        return root;
    }

    /******* General Helper Methods *******/

    public int size() {
        return elements.size();
    }

    private int getParentIndex(int n) {
        return n % 2 == 1 ? n / 2 :  n / 2 - 1;
    }

    /**
     * Bubbles up the element at the given index until the heap property is satisfied, ie. until the element is the
     * root or it has a parent with a smaller value (or equal) than itself
     * @param currentIndex Index of the element to bubble upwards.
     */
    private void upHeap(int currentIndex) {
        if (currentIndex > rootIndex) {
            int parentIndex = getParentIndex(currentIndex);
            int parent = elements.get(parentIndex);
            int current = elements.get(currentIndex);
            if (current < parent) {
                elements.set(currentIndex, parent);
                elements.set(parentIndex, current);
                upHeap(parentIndex);
            }
        }
    }

    /**
     * Buries the element at the given index deeper into the heap until the heap property is satisfied, ie. until the
     * element reaches the bottom of the heap or has a value smaller than both of its children.
     * @param currentIndex Index of the element to bury
     */
    private void downHeap(int currentIndex) {
        int leftIndex = getLeftIndex(currentIndex);
        int rightIndex = getRightIndex(currentIndex);
        Integer left = leftIndex < elements.size() ? elements.get(leftIndex) : null;
        Integer right = rightIndex < elements.size() ? elements.get(rightIndex) : null;
        if (left == null && right == null) {
            return;
        }
        int smallerChildIndex = right == null || (left != null && left <= right) ? leftIndex : rightIndex;
        int smallerChild = elements.get(smallerChildIndex);
        int current = elements.get(currentIndex);
        if (current > smallerChild) {
            elements.set(currentIndex, smallerChild);
            elements.set(smallerChildIndex, current);
        }
        downHeap(smallerChildIndex);
    }

    int getLeftIndex(int n) {
        return n * 2 + 1;
    }

    int getRightIndex(int n) {
        return n* 2 + 2;
    }

    /********* TEST METHODS **************/

    /**
     * Validates the heap property at every element of the heap.
     */
    void validate() {
        for (int i = elements.size() - 1; i >= 0; i--) {
            Integer element = elements.get(i);
            int parentIndex = getParentIndex(i);
            Integer parent = parentIndex >= rootIndex ? elements.get(parentIndex) : null;
            assert element != null &&
                    (parentIndex < 0 || parent <= element)
                    : String.format("elements: %s\nelement: %s\nparent: %s", elements.toString(), element, parent);
        }
    }

}
