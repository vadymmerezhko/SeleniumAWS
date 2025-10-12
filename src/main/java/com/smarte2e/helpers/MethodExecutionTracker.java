package com.smarte2e.helpers;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Method execution tracker class.
 * It trucks @Test and SmartElement methods that are running concurrently.
 */@Slf4j
public class MethodExecutionTracker {
    private static final ConcurrentMap<Long, Integer> threadCountMap = new ConcurrentHashMap<>();

    private MethodExecutionTracker() {}

    /**
     * Increments running methods count for every method in the same thread.
     */
    public static synchronized void increment(long threadId) {

        // Increment count for every nested @RunAlone method in the same thread
        if (!threadCountMap.containsKey(threadId)) {
            // Add entry when count is 0
            threadCountMap.put(threadId, 1);
        }
        else {
            int count = threadCountMap.get(threadId);
            threadCountMap.put(threadId, count + 1);
        }
        int threadMethodsCount = threadCountMap.getOrDefault(threadId, 0);
        log.debug("Methods counter for thread {} is incremented: {}",
                threadId, threadMethodsCount);
        log.debug("Method tracker threads counter is incremented: {}",
                threadCountMap.size());
    }

    /**
     * Decrements running methods count for every method in the same thread.
     */
    public static synchronized void decrement(long threadId) {

        // Decrement count for every nested @RunAlone method in the same thread
        if (threadCountMap.containsKey(threadId)) {
            int count = threadCountMap.get(threadId);

            if (count > 2) {
                threadCountMap.put(threadId, count - 1);
            }
            else {
                // Remove entry when count is 0
                threadCountMap.remove(threadId);
            }
        }
        int threadMethodsCount = threadCountMap.getOrDefault(threadId, 0);
        log.debug("Methods counter for thread {} is decremented: {}",
                threadId, threadMethodsCount);
        log.debug("Method tracker threads counter is decremented: {}",
                threadCountMap.size());
    }

    /**
     * Gets running method threads count.
     * @return The running method threads count.
     */
    public static synchronized int getRunningMethodsCount() {
        log.debug("Method tracker threads count is returned: {}", threadCountMap.size());
        // Map size represent number of the @RunAlone running and waiting methods at this time
        return threadCountMap.size();
    }
}
