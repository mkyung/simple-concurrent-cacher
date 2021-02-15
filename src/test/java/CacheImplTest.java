import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;


public class CacheImplTest {
    @Test(expected = NullPointerException.class)
    public void testSimpleKeyNull() throws  NullPointerException{
        Cache<String, String> cache = new CacheImpl(x -> "value");
        String s = cache.get(null);
    }

    @Test()
    public void testSimpleValueNull() throws  NullPointerException{
        Cache<String, String> cache = new CacheImpl(x -> null);
        String s = cache.get("abc");
        Assert.assertEquals(null, s);
    }

    @Test
    public void testSimpleGetString() throws  NullPointerException{
        Cache<String, String> cache = new CacheImpl(x -> "value");
        String a = cache.get("abc");
        assertEquals("value", a);
    }

    @Test
    public void testSimpleGetList() throws  NullPointerException{
        Cache<String, ArrayList<String>> cache = new CacheImpl<>(x -> new ArrayList<>());
        ArrayList l = cache.get("abc");
        assertEquals(0, l.size());
    }

    @Test
    public void testGetSameKeyConcurrently() throws  InterruptedException, NullPointerException {
        final Integer TEST_THREAD_NUM = 10;
        final CountDownLatch latch = new CountDownLatch(TEST_THREAD_NUM);
        final AtomicInteger runCount = new AtomicInteger(0);
        final Cache<String, Integer> cache = new CacheImpl<>(x -> {
            return runCount.incrementAndGet();
        });

        for (int i = 0; i < TEST_THREAD_NUM; i++) {
            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(100);
                    cache.get("test");
                } catch (InterruptedException e) {

                } finally {
                    latch.countDown();
                }
            });
            t.start();
        }

        latch.await();
        Assert.assertEquals(1, runCount.get());
    }

}
