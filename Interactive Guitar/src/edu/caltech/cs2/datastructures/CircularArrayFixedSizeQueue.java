package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IFixedSizeQueue;

import java.util.Iterator;

public class CircularArrayFixedSizeQueue<E> implements IFixedSizeQueue<E> {

    private E[] queue;
    private int front;
    private int size;

    public CircularArrayFixedSizeQueue(int capacity) {
        this.queue = (E[]) new Object[capacity];
        this.front = 0;
        this.size = 0;
    }

    public CircularArrayFixedSizeQueue() {
        this(10);
    }

    @Override
    public boolean isFull() {
        return this.size >= this.queue.length;
    }

    @Override
    public int capacity() {
        return this.queue.length;
    }

    @Override
    public boolean enqueue(E e) {
        if (!isFull()) {
            int idx = (this.front + this.size) % this.queue.length;
            this.queue[idx] = e;
            this.size++;
            return true;
        }
        return false;
    }

    @Override
    public E dequeue() {
        if (this.size != 0) {
            E e = this.queue[this.front];
            this.queue[this.front] = null;
            this.front = (this.front + 1) % this.queue.length;
            this.size--;
            return e;
        }
        return null;
    }

    @Override
    public E peek() {
        return this.queue[this.front];
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Iterator<E> iterator() {
        return new CircularArrayFixedSizeQueueIterator();
    }

    public String toString() {
        if (size() == 0) {
            return "[]";
        }
        String result = "[";
        Iterator<E> iterator = new CircularArrayFixedSizeQueueIterator();
        while (iterator.hasNext()) {
            result += iterator.next() + ", ";
        }
        result = result.substring(0, result.length() - 2);
        return result + "]";
    }

    private class CircularArrayFixedSizeQueueIterator implements Iterator<E> {
        private int currIdx;
        private int counter;

        public CircularArrayFixedSizeQueueIterator() {
            this.currIdx = (CircularArrayFixedSizeQueue.this).front;
            this.counter = 0;
        }

        public boolean hasNext() {
            return this.counter < CircularArrayFixedSizeQueue.this.size();
        }

        public E next() {
            E e = CircularArrayFixedSizeQueue.this.queue[this.currIdx % CircularArrayFixedSizeQueue.this.queue.length];
            this.currIdx++;
            this.counter++;
            return e;
        }
    }
}
