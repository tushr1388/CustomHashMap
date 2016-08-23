package com.confluent.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.confluent.api.ExpireMap;
import com.confluent.api.ExpiredObject;

public class CustomHashMap<K, V> implements ExpireMap<K, V> {
	private Map<K, ExpiredObject<V>> hashMap = new ConcurrentHashMap<K, ExpiredObject<V>>(1, 1);

	/*
	 * Adds a new entry to the hashmap. If entry already exists with same key,
	 * this updates the current entry.
	 */
	public void put(K key, V value, long timeoutMs) {
		if (key == null) {
			return;
		}
		hashMap.put(key, new ExpiredObject<V>(value, System.currentTimeMillis()));
	}
	
	/*
	 * if the current timestamp is greater than the ttl of the existing entry
	 * return null and remove that entry else return the value;
	 */
	public V get(K key) {
		ExpiredObject<V> expObj = hashMap.get(key);
		if (System.currentTimeMillis() > expObj.getTtl()) {
			hashMap.remove(key);
			return null;
		}
		return expObj.getValue();
	}
	
	/*
	 * Remove the entry for the given key
	 */
	public void remove(K key) {
		hashMap.remove(key);
	}
}
