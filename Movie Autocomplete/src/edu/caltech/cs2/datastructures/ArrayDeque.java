package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IStack;

import java.util.Iterator;

public class ArrayDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {

    private static final int CAPACITY = 10;
    private static final int GROW = 2;
    private E[] arr;
    private int start;
    private int size;

    public ArrayDeque() {
        this(CAPACITY);
    }

    public ArrayDeque(int capacity) {
        this.arr = (E[]) new Object[capacity];
        this.start = 0;
        this.size = 0;
    }

    private void expand() {
        if (this.size == this.arr.length) {
            E[] newArr = (E[])new Object[this.arr.length * GROW];
            for (int i = this.start; i < this.start + this.size; i++) {
                newArr[i] = this.arr[i];
            }
            this.arr = newArr;
        }
    }

    @Override
    public void addFront(E e) {
        expand();
        for(int i = this.size + this.start; i > 0; i--) {
            this.arr[i] = this.arr[i - 1];
        }
        this.arr[0] = e;
        this.size++;
    }

    @Override
    public void addBack(E e) {
        expand();
        this.arr[this.size + this.start] = e;
        this.size++;
    }

    @Override
    public E removeFront() {
        if(this.size > 0) {
            E removed = this.arr[0];
            for(int i = 0; i < this.size + this.start - 1; i++) {
                this.arr[i] = this.arr[i+1];
            }
            this.size--;
            return removed;
        }
        return null;
    }

    @Override
    public E removeBack() {
        if(this.size > 0) {
            E removed = this.arr[this.size + this.start - 1];
            this.arr[this.size + this.start - 1] = null;
            this.size--;
            return removed;
        }
        return null;
    }

    @Override
    public boolean enqueue(E e) {
        addFront(e);
        return true;
    }

    @Override
    public E dequeue() {
        return removeBack();
    }

    @Override
    public boolean push(E e) {
        expand();
        addBack(e);
        return true;
    }

    @Override
    public E pop() {
        return removeBack();
    }

    @Override
    public E peekFront() {
        return this.size == 0 ? null : this.arr[0];
    }

    @Override
    public E peekBack() {
        return this.size == 0 ? null : this.arr[this.size - 1];
    }

    @Override
    public E peek() {
        return peekBack();
    }

    @Override
    public Iterator<E> iterator() {
        return new ArrayDequeIterator();
    }

    @Override
    public int size() {
        return this.size;
    }

    public String toString() {
        if (this.size == 0) {
            return "[]";
        }
        String result = "[";
        for (int i = this.start; i < this.start + this.size; i++) {
            result += this.arr[i] + ", ";
        }
        result = result.substring(0, result.length() - 2);
        return result + "]";
    }
    private class ArrayDequeIterator implements Iterator<E> {
        private int currIdx;

        public ArrayDequeIterator() {
            this.currIdx = 0;
        }

        public boolean hasNext() {
            return this.currIdx < (ArrayDeque.this).size();
        }

        public E next() {
            E e = ArrayDeque.this.arr[currIdx];
            this.currIdx++;
            return e;
        }
    }

}

