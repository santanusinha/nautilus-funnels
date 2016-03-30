package io.appform.nautilus.funnel.utils;


import com.google.common.reflect.TypeToken;
import io.appform.nautilus.funnel.model.core.TemporalTypedEntity;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by guruprasad.sridharan on 08/02/16.
 */
public class TypeUtilsTest {
    @Test
    public void testTypeName_Class() {
        Assert.assertEquals("typeutils", TypeUtils.typeName(TypeUtils.class));
    }

    @Test
    public void testTypeName_TypeToken() {
        Assert.assertEquals("typeutils", TypeUtils.typeName(new TypeToken<TypeUtils>() {}));
    }

    @Test
    public void testTypeName_TemporalTypedEntity() {
        Assert.assertEquals("typeutils", TypeUtils.typeName(new TemporalTypedEntity<TypeUtils>() {
            @Override
            protected TypeToken<TypeUtils> token() {
                return new TypeToken<TypeUtils>() {};
            }
        }));
    }
}
