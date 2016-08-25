package com.confluent.api;

public class KeyValueTTL<K, V> {
	public K key;
	public ValueTTL<V> valueTTL;
	
	public KeyValueTTL(K key, ValueTTL<V> valueTimestamp) {
		this.key = key;
		this.valueTTL = valueTimestamp;
	}
	
	@Override
	public boolean equals(Object o) {
		KeyValueTTL<K, V> current = (KeyValueTTL<K, V>) o;
		if (current == null)
			return false;
		if (this.key == current.key
				&& this.valueTTL.value == current.valueTTL.value
				&& this.valueTTL.TTL == current.valueTTL.TTL) {
			return true;
		}
		return false;
	}
}