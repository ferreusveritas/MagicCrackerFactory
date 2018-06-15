package com.ferreusveritas.mcf.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VoidMap<K,V> implements Map<K,V> {
	public int size() { return 0; }
	public boolean isEmpty() { return true; }
	public boolean containsKey(Object key) { return false; }
	public boolean containsValue(Object value) { return false; }
	public V get(Object key) { return null; }
	public V put(Object key, Object value) { return null; }
	public V remove(Object key) { return null; }
	public void putAll(Map m) { }
	public void clear() { }
	public Set keySet() { return new HashSet<K>(); }
	public Collection values() { return new ArrayList<V>(0); }
	public Set entrySet() { return new HashSet<Map.Entry<K, V>>(); }
}
