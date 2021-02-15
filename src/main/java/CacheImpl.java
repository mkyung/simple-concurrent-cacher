import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class CacheImpl <K, V> implements Cache<K,V> {

    private final Function<K, V> valueProvider;

    private final List<HashMap<K, V>> cacheStores;

    private final Integer NUM_BUCKETS;


    public CacheImpl(Function<K, V> valueProvider) {
        this(valueProvider, Runtime.getRuntime().availableProcessors());

    }

    public CacheImpl(Function<K, V> valueProvider, Integer numBuckets) {
        this.valueProvider = valueProvider;
        this.NUM_BUCKETS = numBuckets;
        List<HashMap<K, V>> stores = new ArrayList<>();
        for (int i = 0; i < this.NUM_BUCKETS; i++) {
            stores.add(new HashMap<>());
        }
        this.cacheStores = stores;
    }


    @Override
    public V get(K key) throws NullPointerException {
        if (key == null) {
            throw new NullPointerException();
        }

        Integer bucket = key.hashCode() % NUM_BUCKETS;
        HashMap<K, V> cacheStore = cacheStores.get(bucket);

        if (cacheStore.containsKey(key)) {
            return cacheStore.get(key);
        }

        synchronized (cacheStore) {
            if (!cacheStore.containsKey(key)) {
                V value = valueProvider.apply(key);
                cacheStore.put(key, value);
                return value;
            }
            return cacheStore.get(key);
        }
    }
}
