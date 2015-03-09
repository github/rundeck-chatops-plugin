package be.fluid_it.tools.rundeck.plugins.util;

import be.fluid_it.tools.rundeck.plugins.util.ExpandUtil;
import be.fluid_it.tools.rundeck.plugins.util.FakeExecutionContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ExpandUtilTest {
    private FakeExecutionContext fakeExecutionContext;

    @Before
    public void setUp(  ) {
        fakeExecutionContext = new FakeExecutionContext();
        fakeExecutionContext.addOption("param1","param1Value");
        fakeExecutionContext.addOption("param2", "param2Value");
    }

    @After
    public void tearDown(  ) {
        fakeExecutionContext = null;
    }

    @Test
    public void expandWithoutKeys() {
        Assert.assertEquals("http://host:8888/test/url", ExpandUtil.expand("http://host:8888/test/url", fakeExecutionContext));
    }

    @Test
    public void expandWithKeys() {
        Assert.assertEquals("http://host:8888/test/url?param1=param1Value&param2=param2Value",
                ExpandUtil.expand("http://host:8888/test/url?param1=${option.param1.value}&param2=${option.param2.value}", fakeExecutionContext));
    }

    @Test
    public void expandWithUnknownKey() {
        Assert.assertEquals("http://host:8888/test/url?test", ExpandUtil.expand("http://host:8888/test/url?test", fakeExecutionContext));
    }

    @Test
    public void expandWithEmptyKey() {
        Assert.assertEquals("param1=param1Value&param2=param2Value&param3=",
                ExpandUtil.expand("param1=${option.param1.value}&param2=${option.param2.value}&param3=${option.param3.value}", fakeExecutionContext));
    }

    @Test
    public void expandWithUnusedOption() {
        fakeExecutionContext.addOption("param3", "param3Value");
        Assert.assertEquals("param1=param1Value&param2=param2Value",
                ExpandUtil.expand("param1=${option.param1.value}&param2=${option.param2.value}", fakeExecutionContext));
    }
}
