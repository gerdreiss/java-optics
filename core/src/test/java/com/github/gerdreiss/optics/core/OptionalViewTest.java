package com.github.gerdreiss.optics.core;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitPlatform.class)
public class OptionalViewTest extends TestModel {

    private final OptionalView<RootObj, NestedObj> nestedObjOptional =
            OptionalView.of(RootObj::getMaybeNestedObj);

    private final OptionalView<NestedObj, InnerObj> innerObjOptional =
            OptionalView.of(NestedObj::getMaybeInnerObj);

    private final OptionalView<InnerObj, String> propertyOptional =
            OptionalView.of(InnerObj::getMaybeProperty);


    private final View<InnerObj, String> propertyView =
            View.of(InnerObj::getProperty);

    @Test
    public void testApply() {
        InnerObj o = new InnerObj(PROP);

        assertFalse(propertyOptional.apply(o).isPresent());

        o = new InnerObj(PROP, Optional.of(MAYBE_PROP));

        assertTrue(propertyOptional.apply(o).isPresent());
        assertEquals(MAYBE_PROP, propertyOptional.apply(o).get());
    }

    @Test
    public void testGetOptional() {
        InnerObj o = new InnerObj(PROP);

        assertFalse(propertyOptional.getOptional(o).isPresent());

        o = new InnerObj(PROP, Optional.of(MAYBE_PROP));

        assertTrue(propertyOptional.getOptional(o).isPresent());
        assertEquals(MAYBE_PROP, propertyOptional.getOptional(o).get());
    }

    @Test
    public void testAndThen() {
        OptionalView<RootObj, String> composedPropertyOptional =
                nestedObjOptional.andThen(innerObjOptional).andThen(propertyOptional);

        OptionalView<RootObj, String> composedPropertyView =
                nestedObjOptional.andThen(innerObjOptional).andThen(propertyView);


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
                        Optional.of(new InnerObj(PROP, Optional.of(MAYBE_PROP))))));

        assertEquals(MAYBE_PROP, composedPropertyOptional.getOptional(o).orElse(null));
        assertEquals(PROP, composedPropertyView.getOptional(o).orElse(null));
    }

    @Test
    public void testCompose() {
        OptionalView<RootObj, String> composedPropertyView =
                propertyView.compose(innerObjOptional).compose(nestedObjOptional);
        OptionalView<RootObj, String> composedPropertyOptional =
                propertyOptional.compose(innerObjOptional).compose(nestedObjOptional);

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
                        Optional.of(new InnerObj(PROP, Optional.of(MAYBE_PROP))))));

        assertEquals(PROP, composedPropertyView.getOptional(o).get());
        assertEquals(MAYBE_PROP, composedPropertyOptional.getOptional(o).get());
    }
}
