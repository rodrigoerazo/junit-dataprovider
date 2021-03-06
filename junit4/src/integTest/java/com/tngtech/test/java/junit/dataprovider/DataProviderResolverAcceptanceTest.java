package com.tngtech.test.java.junit.dataprovider;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderMethodResolver;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.java.junit.dataprovider.internal.DefaultDataProviderMethodResolver;

// @FixMethodOrder(MethodSorters.NAME_ASCENDING) // since 4.11
@RunWith(DataProviderRunner.class)
public class DataProviderResolverAcceptanceTest {

    /**
     * {@link DataProviderMethodResolver} which returns all dataproviders which start with the name of the {@code testMethod}.
     */
    private static class DataProviderStartWithTestMethodNameResolver extends DefaultDataProviderMethodResolver {
        @Override
        protected List<FrameworkMethod> findDataProviderMethods(List<TestClass> locations, String testMethodName,
                String useDataProviderValue) {
            List<FrameworkMethod> result = new ArrayList<FrameworkMethod>();

            for (TestClass location : locations) {
                List<FrameworkMethod> dataProviderMethods = location.getAnnotatedMethods(DataProvider.class);
                for (FrameworkMethod dataProviderMethod : dataProviderMethods) {
                    if (dataProviderMethod.getName().startsWith(testMethodName)) {
                        result.add(dataProviderMethod);
                    }
                }
            }
            Collections.sort(result, new Comparator<FrameworkMethod>() {
                @Override
                public int compare(FrameworkMethod a, FrameworkMethod b) {
                    return a.getName().compareTo(b.getName());
                }
            });
            return result;
        }
    }

    private static AtomicInteger counter;

    @BeforeClass
    public static void setupClass() {
        counter = new AtomicInteger(0);
    }

    @AfterClass
    public static void tearDownClass() {
        assertThat(counter.get()).isEqualTo(6);
    }

    @DataProvider
    public static Object[][] testNumberA() {
        // @formatter:off
        return new Object[][] {
            { (byte) 1 },
        };
        // @formatter:on
    }

    @SuppressWarnings("unchecked")
    @DataProvider
    public static Iterable<Set<Integer>> testNumberB() {
        // @formatter:off
        return Arrays.asList(
                singleton(2),
                singleton(Integer.valueOf(3))
            );
        // @formatter:on
    }

    @DataProvider
    public static Set<Number> testNumberC() {
        Set<Number> result = new LinkedHashSet<Number>();
        result.add(4);
        result.add(5L);
        result.add(6.0);
        return result;
    }

    @Test
    @UseDataProvider(resolver = DataProviderStartWithTestMethodNameResolver.class)
    public void testNumber(Number number) {
        // When:
        int count = counter.incrementAndGet();

        // Then:
        assertThat(count).isEqualTo(number.intValue());
    }
}
