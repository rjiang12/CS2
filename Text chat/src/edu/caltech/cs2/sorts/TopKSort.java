package edu.caltech.cs2.sorts;

import edu.caltech.cs2.datastructures.MinFourHeap;
import edu.caltech.cs2.interfaces.IPriorityQueue;

public class TopKSort {
    /**
     * Sorts the largest K elements in the array in descending order. Modifies the array in place.
     * @param array - the array to be sorted; will be manipulated.
     * @param K - the number of values to sort
     * @param <E> - the type of values in the array
     * @throws IllegalArgumentException if K < 0
     */
    public static <E> void sort(IPriorityQueue.PQElement<E>[] array, int K) {
        if (K < 0) {
            throw new IllegalArgumentException("K cannot be negative!");
        }
        if(K == 0) {
            for(int i = 0; i < array.length; i++) {
                array[i] = null;
            }
        }
        else if(array.length == 0) {
            return;
        }
        else {
            MinFourHeap<E> heap = new MinFourHeap();
            for(IPriorityQueue.PQElement<E> element : array) {
                if(heap.size() < K) {
                    heap.enqueue(element);
                }
                else if(heap.size() >= K && element.priority > heap.peek().priority && heap.peek() != null) {
                    heap.dequeue();
                    heap.enqueue(element);
                }
            }
            if(K <= array.length) {
                for (int i = K - 1; i >= 0; i--) {
                    array[i] = heap.dequeue();
                }
            }
            for(int j = K; j < array.length; j++) {
                array[j] = null;
            }
        }
    }
}
