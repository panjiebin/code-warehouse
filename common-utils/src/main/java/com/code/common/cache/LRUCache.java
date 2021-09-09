package com.code.common.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * LRU(Least Recently Used) Cache
 * <p>
 * The LRU cache is a hash table of keys and double linked nodes.
 * The hash table makes the time of get() to be O(1).
 * The list of double linked nodes make the nodes adding/removal operations O(1)
 * <p>
 * no thread safe
 *
 * @author Pan Jiebin
 * @date 2021-01-27 14:52
 */
public class LRUCache<K, V> {

    private final int capacity;
    private final Node<K, V> head;
    private final Node<K, V> tail;
    private final Map<K, Node<K, V>> cache;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        head = new Node<>();
        tail = new Node<>();
        head.next = tail;
        tail.prev = head;
    }

    public void put(K key, V value) {
        Node<K, V> node = this.cache.get(key);
        if (node == null) {
            node = new Node<>(key, value);
            this.cache.put(key, node);
            addNode(node);
            if (capacity < cache.size()) {
                Node<K, V> tail = this.popTail();
                this.cache.remove(tail.key);
            }
        } else {
            node.value = value;
            moveToHead(node);
        }
    }

    public int size() {
        return cache.size();
    }

    public V get(K key) {
        Node<K, V> node = this.cache.get(key);
        if (node == null) {
            return null;
        }
        moveToHead(node);
        return node.value;
    }

    public void clear() {
        head.next = tail;
        tail.prev = head;
        this.cache.clear();
    }

    private void moveToHead(Node<K, V> node) {
        removeNode(node);
        addNode(node);
    }

    private void removeNode(Node<K, V> node) {
        Node<K, V> pre = node.prev;
        Node<K, V> next = node.next;
        pre.next = next;
        next.prev = pre;
    }

    private void addNode(Node<K, V> node) {
        node.next = head.next;
        node.next.prev = node;
        head.next = node;
        node.prev = head;
    }


    private Node<K, V> popTail() {
        Node<K, V> realTail = tail.prev;
        removeNode(realTail);
        return realTail;
    }

    static class Node<K, V> {
        Node<K, V> prev;
        Node<K, V> next;
        K key;
        V value;

        public Node() {
        }

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Node {key = " + value + ", value = " + value + "}";
        }
    }
}
