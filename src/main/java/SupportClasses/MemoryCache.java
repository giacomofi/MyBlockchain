package SupportClasses;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.LRUMap;

import java.util.ArrayList;

public class MemoryCache<K,V>{

    //Setting cache parameters
    private long timeToLive;
    private LRUMap cacheMap;
    private static final int MULTIPLIER = 1000;

    //Setting cache properties
    protected class CacheObject{
        public long lastAccessed = System.currentTimeMillis();
        public V value;

        protected CacheObject(V value){
            this.value = value;
        }
    }

    //Csche's definition

    public MemoryCache(long timeToLive, final long timerInterval, int maxItems){
        this.timeToLive = timeToLive * MULTIPLIER;

        cacheMap = new LRUMap(maxItems);


        if(timeToLive > 0 && timerInterval > 0){

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(timerInterval * MULTIPLIER);
                    }catch(InterruptedException ex){
                        ex.printStackTrace();
                    }
                }
            });

            t.setDaemon(true);
            t.start();
        }
    }

    //Put in cache
    public void put(K key, V value){
        synchronized (cacheMap){
            cacheMap.put(key, new CacheObject(value));
        }
    }

    //Get from cache
    public V get(K key){

        synchronized (cacheMap){
            CacheObject c = (CacheObject) cacheMap.get(key);

            if (c == null)
                return null;
            else {
                c.lastAccessed = System.currentTimeMillis();
                return c.value;
            }
        }
    }

    //Remove from cache
    public void remove(K key) {
        synchronized (cacheMap) {
            cacheMap.remove(key);
        }
    }

    //Check size of the cache
    public int size() {
        synchronized (cacheMap) {
            return cacheMap.size();
        }
    }

    //Clean the cache
    public void cleanup() {

        long now = System.currentTimeMillis();
        ArrayList<K> deleteKey = null;

        synchronized (cacheMap) {
            MapIterator itr = cacheMap.mapIterator();

            deleteKey = new ArrayList<>((cacheMap.size() / 2) + 1);
            K key = null;
            CacheObject c = null;

            while (itr.hasNext()) {
                key = (K) itr.next();
                c = (CacheObject) itr.getValue();

                if (c != null && (now > (timeToLive + c.lastAccessed))) {
                    deleteKey.add(key);
                }
            }
        }

        for (K key : deleteKey) {
            synchronized (cacheMap) {
                cacheMap.remove(key);
            }

            Thread.yield();
        }
    }

}
