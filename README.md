# CustomHashMap

This custom hashmap has events which expires once they reach their TTL.
  
A thread runs and deletes the entry when it expires and if the entry has not expired, it waits till the next expiring TTL.
  
I have used ordered set to store expiring TTL entries so that when we iterate we can wait for the nearest expiring TTL instead of scanning the Hashmap for the next expiring TTL.

Note : If TTL is too small, you may find GET function giving you null, 2-3 milliseconds before TTL.

e.g 

```
CustomHashMap hmap = new CustomHashmap();
hmap.put(1,"HelloWorld",10);
Thread.sleep(7);
hmap.get(1); //may return null sometimes because of milliseconds time taken by put operation.
```

One optimization which I can think is use of Min Heap for storing TTL instead of the ConcurrentSkipListSet.
But this makes the implementation little bit unclean and longer.
