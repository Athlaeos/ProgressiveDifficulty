package me.athlaeos.progressivelydifficultmobs.pojo;

import java.util.Map;

public class Container<K, V> implements Map.Entry<K, V>{
    private K key;
    private V value;

    public Container(K key, V value){
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        this.value = value;
        return value;
    }
}