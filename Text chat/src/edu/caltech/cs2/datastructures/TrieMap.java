package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.ITrieMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public class TrieMap<A, K extends Iterable<A>, V> implements ITrieMap<A, K, V> {
    private TrieNode<A, V> root;
    private Function<IDeque<A>, K> collector;
    private int size;

    public TrieMap(Function<IDeque<A>, K> collector) {
        this.root = null;
        this.collector = collector;
        this.size = 0;
    }
    

    @Override
    public boolean isPrefix(K key) {
        if (this.root == null) {
            return false;
        }
        TrieNode<A,V> current = this.root;
        for(A a : key) {
            if(current.pointers.containsKey(a)) {
                current = current.pointers.get(a);
            }
            else {
                return false;
            }
        }
        return true;
    }

    @Override
    public IDeque<V> getCompletions(K prefix) {
        IDeque<V> vals = new LinkedDeque<>();
        if(isPrefix(prefix)) {
            TrieNode<A,V> current = this.root;
            for(A a : prefix) {
                current = current.pointers.get(a);
            }
            getCompletionsHelper(vals, current);
        }
        return vals;
    }

    private void getCompletionsHelper(IDeque<V> vals, TrieNode<A,V> current) {
        if(current.value != null) {
            vals.add(current.value);
        }
        for(TrieNode<A,V> child : current.pointers.values()) {
            getCompletionsHelper(vals, child);
        }
    }

    @Override
    public void clear() {
        this.root = null;
        this.size = 0;
    }

    @Override
    public V get(K key) {
        if(this.root == null) {
            return null;
        }
        TrieNode<A,V> current = this.root;
        for(A a : key) {
            if(!current.pointers.containsKey(a)) {
                return null;
            }
            current = current.pointers.get(a);
        }
        return current.value;
    }

    @Override
    public V remove(K key) {
        if (!this.containsKey(key)) {
            return null;
        }
        V value = get(key);
        Iterator<A> keyIter= key.iterator();
        removeHelper(this.root, keyIter);
        if (this.root.pointers.isEmpty() ) {
            this.root = null;
        }
        return value;
    }


    private TrieNode<A, V> removeHelper(TrieNode<A, V> triNode, Iterator<A> keyIter) {
        if (!keyIter.hasNext()) {
            this.size--;
            triNode.value = null;
            if (triNode.pointers.isEmpty()) {
                return null;
            }
            return triNode;
        }
        else {
            A key = keyIter.next();
            if (removeHelper(triNode.pointers.get(key), keyIter) == null) {
                triNode.pointers.remove(key);
            }
            if (triNode.value == null && triNode.pointers.isEmpty() == true ) {
                return null;
            }
            return triNode;
        }
    }

    @Override
    public V put(K key, V value) {
        if(this.root == null) {
            this.root = new TrieNode<>();
        }
        TrieNode<A,V> current = this.root;
        for(A a : key) {
            if(current.pointers.containsKey(a)) {
                current = current.pointers.get(a);
            }
            else {
                TrieNode<A,V> child = new TrieNode<>();
                current.pointers.put(a, child);
                current = child;
            }
        }
        V previous = current.value;
        if(previous == null) {
            this.size++;
        }
        current.value = value;
        return previous;
    }

    @Override
    public boolean containsKey(K key) {
        return this.get(key) != null;
    }

    @Override
    public boolean containsValue(V value) {
        return values().contains(value);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keys() {
        IDeque<K> keys = new LinkedDeque<>();
        IDeque<A> letters = new LinkedDeque<>();
        if(this.root != null) {
            keysHelper(keys, letters, this.root);
        }
        return keys;
    }

    private void keysHelper(IDeque<K> keys, IDeque<A> letters, TrieNode<A,V> current) {
        if(current.value != null) {
            keys.add(this.collector.apply(letters));
        }
        for(A a : current.pointers.keySet()) {
            letters.add(a);
            keysHelper(keys, letters, current.pointers.get(a));
            letters.removeBack();
        }
    }

    @Override
    public ICollection<V> values() {
        IDeque<V> vals = new LinkedDeque<>();
        if(this.root != null) {
            valuesHelper(vals, this.root);
        }
        return vals;
    }

    private void valuesHelper(IDeque<V> vals, TrieNode<A,V> current) {
        if(current.value != null) {
            vals.add(current.value);
        }
        for(TrieNode<A,V> children : current.pointers.values()) {
            valuesHelper(vals, children);
        }
    }

    @Override
    public Iterator<K> iterator() {
        return this.keys().iterator();
    }
    
    private static class TrieNode<A, V> {
        public final Map<A, TrieNode<A, V>> pointers;
        public V value;

        public TrieNode() {
            this(null);
        }

        public TrieNode(V value) {
            this.pointers = new HashMap<>();
            this.value = value;
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            if (this.value != null) {
                b.append("[" + this.value + "]-> {\n");
                this.toString(b, 1);
                b.append("}");
            }
            else {
                this.toString(b, 0);
            }
            return b.toString();
        }

        private String spaces(int i) {
            StringBuilder sp = new StringBuilder();
            for (int x = 0; x < i; x++) {
                sp.append(" ");
            }
            return sp.toString();
        }

        protected boolean toString(StringBuilder s, int indent) {
            boolean isSmall = this.pointers.entrySet().size() == 0;

            for (Map.Entry<A, TrieNode<A, V>> entry : this.pointers.entrySet()) {
                A idx = entry.getKey();
                TrieNode<A, V> node = entry.getValue();

                if (node == null) {
                    continue;
                }

                V value = node.value;
                s.append(spaces(indent) + idx + (value != null ? "[" + value + "]" : ""));
                s.append("-> {\n");
                boolean bc = node.toString(s, indent + 2);
                if (!bc) {
                    s.append(spaces(indent) + "},\n");
                }
                else if (s.charAt(s.length() - 5) == '-') {
                    s.delete(s.length() - 5, s.length());
                    s.append(",\n");
                }
            }
            if (!isSmall) {
                s.deleteCharAt(s.length() - 2);
            }
            return isSmall;
        }
    }
}
