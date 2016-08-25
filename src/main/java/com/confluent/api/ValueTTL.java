package com.confluent.api;

import static java.lang.System.currentTimeMillis;

public class ValueTTL<V> {
	public V value;
	public long TTL;
	
	public ValueTTL(V value, long timeoutMs) {
		this.value = value;
		this.TTL = currentTimeMillis() + timeoutMs;
	}
}