package es.cristichi.mod.magiaborras.util;

import java.util.HashMap;
import java.util.Map;

public class EasyMap<K, V> extends HashMap<K, V> {
    @SafeVarargs
    public EasyMap(Map.Entry<K, V>... pairs) {
        super(pairs.length);
        for(Entry<K, V> pair : pairs){
            put(pair.getKey(), pair.getValue());
        }
    }

    public static class EasyEntry<K, V> implements Entry<K, V> {
        private final K key;
        private V value;

        public EasyEntry(K key, V value) {
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
}
