package com.zyx.elasticsearchplus.sample;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.function.Consumer;

import static org.junit.Assert.fail;

/**
 * @author sxl
 * @since 2019-12-12 15:20
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

    @Test
    public void contextLoads() {
    }

    public static <T> void assertException(Consumer<Void> consumer, Class<T> clazz) {
        try {
            consumer.accept(null);
            fail("Expected exception to be thrown here");
        } catch (Exception e) {
            Assert.assertEquals(e.getClass(), clazz);
        }
    }
}

