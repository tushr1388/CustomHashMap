package com.confluent.tests;

import org.testng.annotations.Test;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

import com.confluent.api.ExpireMap;
import com.confluent.impl.CustomHashMap;

public class CustomHashMapImplTest<K, V> {
	private ExpireMap<Integer, String> expireMap = new CustomHashMap<Integer, String>();

	@Test
	public void checkGetBeforeTTL() {
		expireMap.put(1, "HelloWorld", 6000);
		assertEquals("Getting value successful, since ttl not expired", "HelloWorld", expireMap.get(1));
	}

	@Test
	public void checkGetAfterTTL() throws InterruptedException {
		ExpireMap<Integer, String> expireMap = new CustomHashMap<Integer, String>();
		expireMap.put(1, "HelloWorld", 5000);
		Thread.sleep(6000);
		assertNull("Getting value unsuccessful, since ttl expired", expireMap.get(1));
	}

	@Test
	public void checkGetAfterRemove() {
		ExpireMap<Integer, String> expireMap = new CustomHashMap<Integer, String>();
		expireMap.put(1, "HelloWorld", 60000);
		expireMap.remove(1);
		assertNull("Value is null", expireMap.get(1));
	}

	@Test
	public void checkPutReplace() {
		ExpireMap<Integer, String> expireMap = new CustomHashMap<Integer, String>();
		expireMap.put(1, "HelloWorld", 6000);
		expireMap.put(1, "HelloWorld2", 3000);
		assertEquals("Value for key is replaced", "HelloWorld2", expireMap.get(1));
	}
}