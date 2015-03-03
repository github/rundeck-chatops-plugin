package be.fluid_it.tools.rundeck.plugins.util;

import be.fluid_it.tools.rundeck.plugins.util.ExpandUtil;
import be.fluid_it.tools.rundeck.plugins.util.FakeExecutionContext;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ExpandUtilTest {
    @Test
    public void checkExpandUrl() {
        FakeExecutionContext fakeExecutionContext = new FakeExecutionContext();
        fakeExecutionContext.addOption("param1","param1Value");
        fakeExecutionContext.addOption("param2","param2Value");
        Assert.assertEquals("param1=param1Value&param2=param2Value", ExpandUtil.expand("param1=${option.param1.value}&param2=${option.param2.value}", fakeExecutionContext));
        Assert.assertEquals("http://host:8888/test/url", ExpandUtil.expand("http://host:8888/test/url", fakeExecutionContext));
        Assert.assertEquals("http://host:8888/test/url?param1=param1Value&param2=param2Value", ExpandUtil.expand("http://host:8888/test/url?param1=${option.param1.value}&param2=${option.param2.value}", fakeExecutionContext));
    }
}
