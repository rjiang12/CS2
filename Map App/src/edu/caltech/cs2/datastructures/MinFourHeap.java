package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IPriorityQueue;

import java.util.Iterator;

public class MinFourHeap<E> implements IPriorityQueue<E> {

    private static final int DEFAULT_CAPACITY = 10;

    private int size;
    private PQElement<E>[] data;
    private IDictionary<E, Integer> keyToIndexMap;

    /**
     * Creates a new empty heap with DEFAULT_CAPACITY.
     */
    public MinFourHeap() {
        this.size = 0;
        this.data = new PQElement[DEFAULT_CAPACITY];
        this.keyToIndexMap = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
    }

    @Override
    public void increaseKey(PQElement<E> key) {
        if(!this.keyToIndexMap.containsKey(key.data)) {
            throw new IllegalArgumentException("Nonexistent data");
        }
        int idx = keyToIndexMap.get(key.data);
        this.data[idx] = key;
        percolateDown(idx, key);
    }

    private void percolateDown(int idx, PQElement<E> key) {
        int scIdx = smallestChild(idx);
        int currIdx = idx;
        while(scIdx != -1 && (key.priority > this.data[scIdx].priority)) {
            PQElement<E> child = this.data[scIdx];
            this.data[scIdx] = this.data[currIdx];
            this.data[currIdx] = child;
            this.keyToIndexMap.put(child.data, currIdx);
            currIdx = scIdx;
            scIdx = smallestChild(currIdx);
        }
        this.keyToIndexMap.put(key.data, currIdx);
    }

    private int smallestChild(int parentIdx) {
        if(parentIdx * 4 + 1 >= this.size) {
            return -1;
        }
        int firstChild = parentIdx * 4 + 1;
        int lastChild = parentIdx * 4 + 4;
        int smallest = firstChild;
        for(int i = firstChild; i <= lastChild; i++) {
            if(i >= this.size || this.data[i] == null) {
                break;
            }
            if(this.data[i].priority < this.data[smallest].priority) {
                smallest = i;
            }
        }
        return smallest;
    }

    @Override
    public void decreaseKey(PQElement<E> key) {
        if(!this.keyToIndexMap.containsKey(key.data)) {
            throw new IllegalArgumentException("Nonexistent data");
        }
        int idx = keyToIndexMap.get(key.data);
        this.data[idx] = key;
        percolateUp(idx, key);
    }

    private void percolateUp(int idx, PQElement<E> key) {
        int currIdx = idx;
        int parentIdx = (currIdx - 1)/4;
        while(key.priority < this.data[parentIdx].priority) {
            PQElement<E> parent = this.data[parentIdx];
            this.data[parentIdx] = this.data[currIdx];
            this.data[currIdx] = parent;
            this.keyToIndexMap.put(parent.data, currIdx);
            currIdx = parentIdx;
            parentIdx = (currIdx-1)/4;
        }
        this.keyToIndexMap.put(key.data, currIdx);
    }

    @Override
    public boolean enqueue(PQElement<E> epqElement) {
        if(this.keyToIndexMap.containsKey(epqElement.data)) {
            throw new IllegalArgumentException("Element already exists");
        }
        resize();
        this.data[this.size] = epqElement;
        int idx = this.size;
        percolateUp(idx, epqElement);
        this.size++;
        return true;
    }

    private void resize() {
        if(this.size >= this.data.length) {
            PQElement<E>[] data = new PQElement[this.data.length * 4];
            for(int i = 0; i < this.data.length; i++) {
                data[i] = this.data[i];
            }
            this.data = data;
        }
    }

    @Override
    public PQElement<E> dequeue() {
        PQElement<E> removed = this.peek();
        if(removed != null) {
//            PQElement<E> bottomRight = this.data[this.size - 1];
//            this.data[0] = bottomRight;
            this.data[0] = this.data[this.size - 1];
            percolateDown(0, this.data[0]);
            this.keyToIndexMap.remove(removed.data);
            this.size--;
        }
        return removed;
    }

    @Override
    public PQElement<E> peek() {
        return this.data[0];
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
     public Iterator<PQElement<E>> iterator() {
        return null;
    }
}