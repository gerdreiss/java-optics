package com.github.gerdreiss.optics;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class OptionalViewTest extends TestModel {

    private final OptionalView<RootObj, NestedObj> innerObjOptional =
            OptionalView.of((RootObj o) -> Optional.ofNullable(o.getNestedObj()));
    private final OptionalView<NestedObj, InnerObj> innerInnerObjOptional =
            OptionalView.of((NestedObj o) -> Optional.ofNullable(o.getInnerObj()));
    private final OptionalView<InnerObj, String> propertyOptional =
            OptionalView.of((InnerObj o) -> Optional.ofNullable(o.getProperty()));

    @Test
    public void testApply() {
        InnerObj o = new InnerObj(null);

        assertNull(propertyOptional.apply(o).orElse(null));

        o = new InnerObj("property1");

        assertEquals("property1", propertyOptional.apply(o).orElse(null));
    }

    @Test
    public void testGetOptional() {
        InnerObj o = new InnerObj(null);

        assertNull(propertyOptional.getOptional(o).orElse(null));

        o = new InnerObj("property1");

        assertEquals("property1", propertyOptional.getOptional(o).orElse(null));
    }

    @Test
    public void testAndThen() throws Exception {
        OptionalView<RootObj, String> composed =
                innerObjOptional.andThen(innerInnerObjOptional).andThen(propertyOptional);

        RootObj o = new RootObj(null);

        assertFalse(composed.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(null));

        assertFalse(composed.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(new InnerObj(null)));

        assertFalse(composed.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(new InnerObj("property1")));

        assertEquals("property1", composed.getOptional(o).orElse(null));
    }

    @Test
    public void testCompose() throws Exception {
        OptionalView<RootObj, String> composed =
                propertyOptional.compose(innerInnerObjOptional).compose(innerObjOptional);

        RootObj o = new RootObj(null);

        assertFalse(composed.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(null));

        assertFalse(composed.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(new InnerObj(null)));

        assertFalse(composed.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(new InnerObj("property1")));

        assertEquals("property1", composed.getOptional(o).orElse(null));
    }
}
