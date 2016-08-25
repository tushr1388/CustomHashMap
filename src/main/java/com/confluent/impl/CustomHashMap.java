package com.confluent.impl;

import static java.lang.System.currentTimeMillis;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.log4j.Logger;

import com.confluent.api.ExpireMap;
import com.confluent.api.KeyValueTTL;
import com.confluent.api.ValueTTL;

/* This custom hashmap has events which expires once they reach their TTL.
 * 
 * A thread runs and deletes the entry when it expires and if the entry has 
 * not expired it waits till the next expiring TTL.
 * 
 * We have used ordered set to store expiring TTL entries so that when we iterate we
 * can wait for the nearest expiring TTL instead of scanning the Hashmap for the next expiring TTL.
 */
public class CustomHashMap<K, V> implements ExpireMap<K, V> {
	// the underlying hashmap
	private ConcurrentHashMap<K, ValueTTL<V>> hashMap;
	private DeletionThread deletionThread;
	private Logger logger = Logger.getLogger(CustomHashMap.class);
	
	public CustomHashMap() {
		hashMap = new ConcurrentHashMap<K, ValueTTL<V>>();
		deletionThread = new DeletionThread();
		Thread t = new Thread(deletionThread);
		t.start();
	}

	public void put(K key, V value, long timeoutMs) {
		if (timeoutMs > 0) {
			ValueTTL<V> vt = new ValueTTL<V>(value, timeoutMs);
			hashMap.put(key, vt);
			deletionThread.add(key, vt);
			logger.debug("Added value with key : " +  key);
		}
	}

	public V get(K key) {
		ValueTTL<V> value = hashMap.get(key);
		if (value != null) {
			if (value.TTL > currentTimeMillis()) {
				return (V) value.value;
			} else {
				return null;
			}
		}
		return null;
	}

	public void remove(K key) {
		hashMap.remove(key);
		logger.debug("Removed value with key : " +  key);
	}

	// Thread that will remove from the hashmap once the ttl expires
	private class DeletionThread implements Runnable {

		// Here we will store the key,value and the expirationTime
		ConcurrentSkipListSet<KeyValueTTL<K, V>> expirationList;

		public DeletionThread() {
			expirationList = new ConcurrentSkipListSet<KeyValueTTL<K, V>>(new Comparator<KeyValueTTL<K, V>>() {
				// order by TTL
				public int compare(KeyValueTTL<K, V> a, KeyValueTTL<K, V> b) {
					if (a.valueTTL.TTL > b.valueTTL.TTL) {
						return 1;
					} else if (a.valueTTL.TTL < b.valueTTL.TTL) {
						return -1;
					}
					return 0;
				}
			});
		}

		public void run() {
			while (true) {
				long nextTimestamp = -1;
				long currTimestamp = currentTimeMillis();
				
				// remove elements that timed out and figure out the length
				// of time to sleep in milliseconds till next expiration
				for (KeyValueTTL<K, V> element : expirationList) {
					if (element.valueTTL.TTL <= currTimestamp) {
						hashMap.remove(element.key, element.valueTTL);
						expirationList.remove(element);
					} else {
						nextTimestamp = element.valueTTL.TTL;
						break;
					}
				}

				try {
					synchronized (this) {
						// wait for next TTL to complete
						if (nextTimestamp > 0) {
							wait(nextTimestamp - currTimestamp);
						} else {
							wait(1000);
						}
					}
				} catch (InterruptedException e) {
					logger.error("Error'd out while waiting for completion of next TTL : " + e.getMessage());
				}
			}
		}
		
		public void add(K key, ValueTTL<V> vt) {
			expirationList.add(new KeyValueTTL<K, V>(key, vt));
			synchronized (this) {
				notify();
			}
		}
	}
}