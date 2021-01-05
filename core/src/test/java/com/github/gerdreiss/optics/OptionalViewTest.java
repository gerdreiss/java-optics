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
    private final OptionalView<InnerObj, String> maybePropertyOptional =
            OptionalView.of(InnerObj::getMaybeProperty);

    @Test
    public void testApply() {
        InnerObj o = new InnerObj(null, Optional.empty());

        assertNull(propertyOptional.apply(o).orElse(null));
        assertNull(maybePropertyOptional.apply(o).orElse(null));

        o = new InnerObj("property", Optional.of("maybeProperty"));

        assertEquals(o.getProperty(), propertyOptional.apply(o).orElse(null));
        assertEquals(o.getMaybeProperty(), maybePropertyOptional.apply(o));
    }

    @Test
    public void testGetOptional() {
        InnerObj o = new InnerObj(null, Optional.empty());

        assertNull(propertyOptional.getOptional(o).orElse(null));
        assertNull(maybePropertyOptional.getOptional(o).orElse(null));

        o = new InnerObj("property", Optional.of("maybeProperty"));

        assertEquals(o.getProperty(), propertyOptional.getOptional(o).orElse(null));
        assertEquals(o.getMaybeProperty(), maybePropertyOptional.getOptional(o));
    }

    @Test
    public void testAndThen() throws Exception {
        OptionalView<RootObj, String> composedPropertyView =
                innerObjOptional.andThen(innerInnerObjOptional).andThen(propertyOptional);
        OptionalView<RootObj, String> composedMaybePropertyView =
                innerObjOptional.andThen(innerInnerObjOptional).andThen(maybePropertyOptional);

        RootObj o = new RootObj(null);

        assertFalse(composedPropertyView.getOptional(o).isPresent());
        assertFalse(composedMaybePropertyView.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(null));

        assertFalse(composedPropertyView.getOptional(o).isPresent());
        assertFalse(composedMaybePropertyView.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(new InnerObj(null, Optional.empty())));

        assertFalse(composedPropertyView.getOptional(o).isPresent());
        assertFalse(composedMaybePropertyView.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(new InnerObj("property", Optional.of("maybeProperty"))));

        assertEquals(o.getNestedObj().getInnerObj().getProperty(), composedPropertyView.getOptional(o).orElse(null));
        assertEquals(o.getNestedObj().getInnerObj().getMaybeProperty(), composedMaybePropertyView.getOptional(o));
    }

    @Test
    public void testCompose() throws Exception {
        OptionalView<RootObj, String> composedPropertyView =
                propertyOptional.compose(innerInnerObjOptional).compose(innerObjOptional);
        OptionalView<RootObj, String> composedMaybePropertyView =
                maybePropertyOptional.compose(innerInnerObjOptional).compose(innerObjOptional);

        RootObj o = new RootObj(null);

        assertFalse(composedPropertyView.getOptional(o).isPresent());
        assertFalse(composedMaybePropertyView.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(null));

        assertFalse(composedPropertyView.getOptional(o).isPresent());
        assertFalse(composedMaybePropertyView.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(new InnerObj(null, Optional.empty())));

        assertFalse(composedPropertyView.getOptional(o).isPresent());
        assertFalse(composedMaybePropertyView.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(new InnerObj("property", Optional.of("maybeProperty"))));

        assertEquals(o.getNestedObj().getInnerObj().getProperty(), composedPropertyView.getOptional(o).orElse(null));
        assertEquals(o.getNestedObj().getInnerObj().getMaybeProperty(), composedMaybePropertyView.getOptional(o));
    }
}
