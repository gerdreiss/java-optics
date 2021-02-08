package com.github.gerdreiss.optics.core;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitPlatform.class)
public class OptionalViewTest extends TestModel {


    @Test
    public void testApply() {
        InnerObj o = new InnerObj(PROP);

        assertFalse(innerObjPropertyOptionalView.apply(o).isPresent());

        o = new InnerObj(PROP, MAYBE_PROP);

        assertTrue(innerObjPropertyOptionalView.apply(o).isPresent());
        assertEquals(MAYBE_PROP, innerObjPropertyOptionalView.apply(o));
    }

    @Test
    public void testGetOptional() {
        InnerObj o = new InnerObj(PROP);

        assertFalse(innerObjPropertyOptionalView.getOptional(o).isPresent());

        o = new InnerObj(PROP, MAYBE_PROP);

        assertTrue(innerObjPropertyOptionalView.getOptional(o).isPresent());
        assertEquals(MAYBE_PROP, innerObjPropertyOptionalView.getOptional(o));
    }

    @Test
    public void testAndThen() {
        OptionalView<RootObj, String> composedPropertyOptional =
                rootObjNestedObjOptionalView.andThen(nestedObjInnerObjOptionalView).andThen(innerObjPropertyOptionalView);

        OptionalView<RootObj, String> composedPropertyView =
                rootObjNestedObjOptionalView.andThen(nestedObjInnerObjOptionalView).andThen(innerObjPropertyView);


        RootObj o = new RootObj(null);

        assertFalse(composedPropertyOptional.getOptional(o).isPresent());
        assertFalse(composedPropertyView.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(null));

        assertFalse(composedPropertyOptional.getOptional(o).isPresent());
        assertFalse(composedPropertyView.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(new InnerObj(PROP)));

        assertFalse(composedPropertyOptional.getOptional(o).isPresent());
        assertFalse(composedPropertyView.getOptional(o).isPresent());

        o = new RootObj(null,
                Optional.of(new NestedObj(null,
                        Optional.of(new InnerObj(PROP, MAYBE_PROP)))));

        assertEquals(MAYBE_PROP, composedPropertyOptional.getOptional(o));
        assertEquals(PROP, composedPropertyView.getOptional(o).orElse(null));
    }

    @Test
    public void testCompose() {
        OptionalView<RootObj, String> composedPropertyView =
                innerObjPropertyView.compose(nestedObjInnerObjOptionalView).compose(rootObjNestedObjOptionalView);
        OptionalView<RootObj, String> composedPropertyOptional =
                innerObjPropertyOptionalView.compose(nestedObjInnerObjOptionalView).compose(rootObjNestedObjOptionalView);

        RootObj o = new RootObj(null);

        assertFalse(composedPropertyView.getOptional(o).isPresent());
        assertFalse(composedPropertyOptional.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(null));

        assertFalse(composedPropertyView.getOptional(o).isPresent());
        assertFalse(composedPropertyOptional.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(new InnerObj(PROP)));

        assertFalse(composedPropertyView.getOptional(o).isPresent());
        assertFalse(composedPropertyOptional.getOptional(o).isPresent());

        o = new RootObj(null,
                Optional.of(new NestedObj(null,
                        Optional.of(new InnerObj(PROP, MAYBE_PROP)))));

        assertEquals(PROP, composedPropertyView.getOptional(o).get());
        assertEquals(MAYBE_PROP, composedPropertyOptional.getOptional(o));
    }
}
