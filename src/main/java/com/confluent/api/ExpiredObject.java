package com.confluent.api;

public class ExpiredObject<V>{
	private long ttl;
	private V value;
	
	public ExpiredObject(V value, long ttl) {
		this.ttl = ttl;
		this.value = value;
	}
	public V getValue() {
		return value;
	}
	public void setValue(V value) {
		this.value = value;
	}
	public long getTtl() {
		return ttl;
	}
	public void setTtl(long ttl) {
		this.ttl = ttl;
	}
}
