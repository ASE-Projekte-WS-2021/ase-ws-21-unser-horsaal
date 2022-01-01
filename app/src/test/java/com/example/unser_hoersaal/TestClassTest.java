package com.example.unser_hoersaal;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class TestClassTest {

    @Test
    public void testIsEmailValid() {
        String testEmail = "vairasza@mailbox.org";
        MatcherAssert.assertThat(String.format("Email Validity Test failed for %s ", testEmail), TestClass.checkEmailForValidity(testEmail), is(true));
    }

    //"The multiplication of 9 and 8 should be 72"
    @Test
    public void testMultiplicateNumbers() {
        int x = 9;
        int y = 8;
        Assert.assertEquals(72, TestClass.multiplicationNumbers(x, y));
    }

}