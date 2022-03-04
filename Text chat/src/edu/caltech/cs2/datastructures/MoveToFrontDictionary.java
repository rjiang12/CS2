package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDictionary;

import java.util.Iterator;

public class MoveToFrontDictionary<K, V> implements IDictionary<K,V> {

    private Node<K, V> head;
    private int size;

    public MoveToFrontDictionary() {
        this.head = null;
        this.size = 0;
    }

    @Override
    public V remove(K key) {
        if(!this.containsKey(key)) {
            return null;
        }
        if(this.head.key.equals(key)) {
            V removedVal = this.head.value;
            this.head = this.head.next;
            this.size--;
            return removedVal;
        }
        Node<K, V> current = this.head;
        while(current.next != null) {
            if(current.next.key.equals(key)) {
                V removedVal = current.next.value;
                current.next = current.next.next;
                this.size--;
                return removedVal;
            }
            current = current.next;
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        V previousValue = remove(key);
        Node<K, V> newHead = new Node<K, V> (key, value);
        newHead.next = this.head;
        this.head = newHead;
        this.size++;
        return previousValue;
    }

    @Override
    public boolean containsKey(K key) {
        return this.get(key) != null;
    }

    @Override
    public boolean containsValue(V value) {
        return this.values().contains(value);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keys() {
        LinkedDeque<K> keys = new LinkedDeque<>();
        if(this.head != null) {
            Node<K, V> current = this.head;
            keys.add(this.head.key);
            while(current.next != null) {
                keys.add(current.next.key);
                current = current.next;
            }
        }
        return keys;
    }

    @Override
    public ICollection<V> values() {
        LinkedDeque<V> values = new LinkedDeque<>();
        if(this.head != null) {
            Node<K, V> current = this.head;
            values.add(this.head.value);
            while (current.next != null) {
                values.add(current.next.value);
                current = current.next;
            }
        }
        return values;
    }

    public V get(K key) {
        if(this.head == null) {
            return null;
        }
        if(this.head.key.equals(key)) {
            return this.head.value;
        }
        Node<K, V> current = this.head;
        while(current.next != null) {
            if(current.next.key.equals(key)) {
                V value = current.next.value;
                Node<K, V> newHead = current.next;
                current.next = current.next.next;
                newHead.next = this.head;
                this.head = newHead;
                return value;
            }
            current = current.next;
        }
        return null;
    }

   /*     if(this.head != null) {
            if(this.size() == 1 || this.head.key.equals(key)) {
                return this.head.value;
            }
            Node<K, V> current = this.head;
            while(current.next != null) {
                if(current.next.key.equals(key)) {
                    V returnValue = current.next.value;
                    Node<K, V> newHead= current.next;
                    current.next = current.next.next;
                    newHead.next = this.head;
                    this.head = newHead;
                    return returnValue;
                }
                current = current.next;
            }
            return null;
        }
        return null;
    }
    */

    @Override
    public Iterator<K> iterator() {
        return this.keys().iterator();
    }

    private static class Node<K, V> {
        public K key;
        public V value;
        public Node<K, V> next;

        public Node(K key, V value) {
            this(key, value, null);
        }

        public Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }


    }
}
