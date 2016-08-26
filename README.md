# CustomHashMap

This custom hashmap has key-value events that expire once they reach their TTL.
  
Whenever an object of this custom hashmap is created ,a thread is started which runs through an ordered set and deletes the entry when it expires and if the entry has not expired, it waits till the next expiring TTL.
  
I have used an ordered set to store expiring TTL entries, instead of scanning the actual Hashmap everytime for the next expiring TTL.

Note : If TTL is too small, you may find GET function giving you null, 2-3 milliseconds before TTL.

e.g 

```
CustomHashMap hmap = new CustomHashmap();
hmap.put(1,"HelloWorld",10);
Thread.sleep(7);
hmap.get(1); //may return null sometimes because of some time taken by put operation.
```

One optimization which I can think is use of Min Heap for storing TTL instead of the ConcurrentSkipListSet.
But this makes the implementation little bit unclean and longer.
