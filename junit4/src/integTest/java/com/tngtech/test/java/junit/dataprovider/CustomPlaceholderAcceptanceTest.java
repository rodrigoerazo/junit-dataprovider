package com.tngtech.test.java.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.Placeholders;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.java.junit.dataprovider.internal.placeholder.ParameterPlaceholder;

@RunWith(DataProviderRunner.class)
public class CustomPlaceholderAcceptanceTest {

    public static class StripParameterLengthPlaceholder extends ParameterPlaceholder {
        private final int maxLength;

        public StripParameterLengthPlaceholder(int maxLength) {
            this.maxLength = maxLength;
        }

        @Override
        protected String formatAll(Object[] parameters) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < parameters.length; i++) {
                String formattedParameter = format(parameters[i]);
                if (formattedParameter.length() > maxLength) {
                    stringBuilder.append(formattedParameter, 0, maxLength - 5);
                    stringBuilder.append("...");
                    stringBuilder.append(formattedParameter.substring(formattedParameter.length() - 2));
                } else {
                    stringBuilder.append(formattedParameter);
                }
                if (i < parameters.length - 1) {
                    stringBuilder.append(", ");
                }
            }
            return stringBuilder.toString();
        }
    }

    // Note: is only called before test initialization in Runner's constructor if at least one "external"
    // dataprovider (= static method) exists
    static {
        Placeholders.all().add(0, new StripParameterLengthPlaceholder(10));
    }

    @DataProvider
    public static String[] dataProviderEqualsIgnoreCase() {
        // @formatter:off
        return new String[] {
                "veryVeryLongMethodNameWhichMustBeStripped,                                      null, false",
                "veryVeryLongMethodNameWhichMustBeStripped,                                          , false",
                "veryVeryLongMethodNameWhichMustBeStripped, veryVeryLongMethodNameWhichMustBeStripped,  true",
                "veryverylongmethodnamewhichmustbestripped, veryVeryLongMethodNameWhichMustBeStripped,  true",
                "veryVeryLongMethodNameWhichMustBeStripped, veryverylongmethodnamewhichmustbestripped,  true",
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider
    public void testEqualsIgnoreCase(String methodName1, String methodName2, boolean expected) {
        // Expected:
        assertThat(methodName1.equalsIgnoreCase(methodName2)).isEqualTo(expected);
    }
}
