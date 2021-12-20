package com.example.unser_hoersaal;
import static org.junit.Assert.assertNotNull;

public class MainActivityTest extends MainActivity {

    private MainActivity mTestActivity;

    public MainActivityTest() {
        mTestActivity = new MainActivity();
        testPreconditions();

    }
    public void testPreconditions() {
        assertNotNull("mTestActivity is null", mTestActivity);
    }
}
