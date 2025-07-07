package hr.tvz.java.freelance.freelancemanagementtool.util;

/**
 * A generic class to hold a pair of values of different types.
 * @param K the type of the key
 * @param V the type of the value
 */
public class Pair<K, V> {
    private final K key;
    private final V value;

    /**
     * Pair constructor
     *
     * @param key Key
     * @param value Value
     */
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Gets the key
     *
     * @return Key
     */
    public K getKey() {
        return key;
    }

    /**
     * Gets the value
     *
     * @return Value
     */
    public V getValue() {
        return value;
    }
}