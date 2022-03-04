package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDictionary;

import java.util.Iterator;
import java.util.function.Supplier;

public class ChainingHashDictionary<K, V> implements IDictionary<K, V> {
    private Supplier<IDictionary<K, V>> chain;
    private int size;
    private int capacity;
    private IDictionary<K, V>[] hashTable;
    //private int[] primes = new int[]{31, 67, 137, 277, 557, 1117, 2237, 4481, 8963, 17929, 35863, 71741, 573953};

    public ChainingHashDictionary(Supplier<IDictionary<K, V>> chain) {
        this.chain = chain;
        this.size = 0;
        this.capacity = 31;
        this.hashTable = new IDictionary[this.capacity];
    }

    /**
     * @param key
     * @return value corresponding to key
     */
    @Override
    public V get(K key) {
        int hashKey = Math.abs(key.hashCode()) % this.capacity;
        if(hashKey<0) {
            hashKey += this.capacity;
        }
        if(this.size == 0 || this.hashTable[hashKey] == null || this.hashTable[hashKey].isEmpty()) {
            return null;
        }
        return this.hashTable[hashKey].get(key);
    }

    @Override
    public V remove(K key) {
        /*if(this.size == 0) {
            return null;
        }*/
        V value = this.get(key);
        if(value == null) {
            return null;
        }
        int hashKey = Math.abs(key.hashCode()) % this.capacity;
        if(hashKey<0) {
            hashKey += this.capacity;
        }
        this.hashTable[hashKey].remove(key);
        this.size--;
        return value;
    }

    @Override
    public V put(K key, V value) {
        this.resize();
        V removed = this.remove(key);
        int hashKey = Math.abs(key.hashCode()) % this.capacity;
        if(this.hashTable[hashKey] == null) {
            this.hashTable[hashKey] = this.chain.get();
        }
        this.hashTable[hashKey].put(key, value);
        this.size++;
        return removed;
    }

    private void resize() {
        if((double)this.size/(double)this.capacity >= 1.0) {
            int newCap = this.capacity * 2 + 1;
            while(!isPrime(newCap)) {
                newCap++;
            }
            /*int newCap = 0;
            for(int prime : primes) {
                if(prime >= this.capacity * 2 + 1) {
                    newCap = prime;
                    break;
                }
            }*/
            IDictionary<K, V>[] newTable = new IDictionary[newCap];
            for (int i = 0; i < this.hashTable.length; i++) {
                if (this.hashTable[i] != null) {
                    for (K key : this.hashTable[i]) {
                        int newKey = Math.abs(key.hashCode()) % newCap;
                        if(newKey<0) {
                            newKey += newCap;
                        }
                        if (newTable[newKey] == null) {
                            newTable[newKey] = this.chain.get();
                        }
                        V newVal = this.hashTable[i].get(key);
                        newTable[newKey].put(key, newVal);
                    }
                }
            }
            this.hashTable = newTable;
            this.capacity = newCap;
        }
    }

    private boolean isPrime(int num) {
        for(int i = 2; i <= num/2; i++) {
            if(num % i == 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsKey(K key) {
        return this.get(key) != null;
    }

    /**
     * @param value
     * @return true if the HashDictionary contains a key-value pair with
     * this value, and false otherwise
     */
    @Override
    public boolean containsValue(V value) {
        return this.values().contains(value);
    }

    /**
     * @return number of key-value pairs in the HashDictionary
     */
    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keys() {
        LinkedDeque<K> keys = new LinkedDeque<>();
        for(int i = 0; i < this.capacity; i++) {
            if(this.hashTable[i] != null) {
                keys.addAll(this.hashTable[i].keys());
            }
        }
        return keys;
    }

    @Override
    public ICollection<V> values() {
        LinkedDeque<V> values = new LinkedDeque<>();
        for(int i = 0; i < this.capacity; i++) {
            if(this.hashTable[i] != null) {
                values.addAll(this.hashTable[i].values());
            }
        }
        return values;
    }

    /**
     * @return An iterator for all entries in the HashDictionary
     */
    @Override
    public Iterator<K> iterator() {
        return this.keys().iterator();
    }
}
