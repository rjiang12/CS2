package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IStack;

import java.util.Iterator;

public class LinkedDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {

    private int size;
    private Node<E> front;
    private Node<E> back;

    public LinkedDeque() {
        this.size = 0;
        this.front = null;
        this.back = null;
    }

    @Override
    public void addFront(E e) {
        Node<E> n = new Node<E>(e);
        if (this.size == 0) {
            this.front = n;
            this.back = n;
        }
        else{
            n.next = this.back;
            this.back.prev = n;
            this.back = n;
        }
        this.size++;
    }

    @Override
    public void addBack(E e) {
        Node<E> n = new Node<E>(e);
        if(this.size == 0) {
            this.front = n;
            this.back = n;
        }
        else {
            n.prev = this.front;
            this.front.next = n;
            this.front = n;
        }
        this.size++;
    }

    @Override
    public E removeFront() {
        if(this.size == 0) {
            return null;
        }
        else{
            E front = this.back.data;
            this.back = this.back.next;
            if(this.back == null) {
                this.front = null;
            }
            else {
                this.back.prev = null;
            }
            this.size--;
            return front;
        }
    }

    @Override
    public E removeBack() {
        if(this.size == 0) {
            return null;
        }
        else{
            E back = this.front.data;
            this.front = this.front.prev;
            if(this.front == null) {
               this.back = null;
            }
            else {
                this.front.next = null;
            }
            this.size--;
            return back;
        }
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
        addBack(e);
        return true;
    }

    @Override
    public E pop() {
        return removeBack();
    }

    @Override
    public E peekFront() {
        return this.back != null ? this.back.data : null;
    }

    @Override
    public E peekBack() {
        return this.front != null ? this.front.data : null;
    }

    @Override
    public E peek() {
        return peekBack();
    }

    @Override
    public Iterator<E> iterator() {
        return new LinkedDequeIterator();
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
        Iterator<E> iterator = new LinkedDequeIterator();
        while (iterator.hasNext()) {
            result += iterator.next() + ", ";
        }
        result = result.substring(0, result.length() - 2);
        return result + "]";
    }

    private static class Node<E> {
        private final E data;
        private Node<E> next;
        private Node<E> prev;

        private Node(E data) {
            this(data, null, null);
        }

        private Node(E data, Node<E> next, Node<E> prev) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    private class LinkedDequeIterator implements Iterator<E> {
                private Node<E> current;

        public LinkedDequeIterator() {
            this.current = LinkedDeque.this.back;
        }

        public boolean hasNext() {
            return this.current != null;
        }

        public E next() {
            E e = this.current.data;
            this.current = this.current.next;
            return e;
        }
    }
}
