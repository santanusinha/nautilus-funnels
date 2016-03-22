package io.appform.nautilus.funnel.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

/**
 * Created by guruprasad.sridharan on 08/02/16.
 */
public class AttributeUtilsTest {
    @Test
    public void testIsValid() {
        Assert.assertTrue(AttributeUtils.isValid(null));
        Assert.assertTrue(AttributeUtils.isValid(Collections.EMPTY_MAP));
        Assert.assertTrue(AttributeUtils.isValid(ImmutableMap.of("location", "gbsvh", "address", ImmutableList.of("21", "Downing Street"))));
        Assert.assertTrue(!AttributeUtils.isValid(ImmutableMap.of("_", ImmutableList.of("21", "Downing Street"), "location", "gbsvh")));
        Assert.assertTrue(!AttributeUtils.isValid(ImmutableMap.of("pincode", "560078", "location", "gbsvh")));
        Assert.assertTrue(!AttributeUtils.isValid(ImmutableMap.of("location", "gbsvh", "pincode", 560078)));
    }
}
