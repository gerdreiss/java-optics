package com.github.gerdreiss.optics;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class OptionalViewTest extends TestModel {

    private final OptionalView<RootObj, NestedObj> nestedObjOptional =
            OptionalView.of((RootObj o) -> Optional.ofNullable(o.getNestedObj()));

    private final OptionalView<NestedObj, InnerObj> innerObjOptional =
            OptionalView.of((NestedObj o) -> Optional.ofNullable(o.getInnerObj()));

    private final OptionalView<InnerObj, String> propertyOptional =
            OptionalView.of((InnerObj o) -> Optional.ofNullable(o.getProperty()));

    private final OptionalView<NestedObj, InnerObj> maybeInnerObjOptional =
            OptionalView.of(NestedObj::getMaybeInnerObj);

    private final OptionalView<InnerObj, String> maybePropertyOptional =
            OptionalView.of(InnerObj::getMaybeProperty);

    private final View<InnerObj, String> propertyView =
            View.of(InnerObj::getProperty);

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
        OptionalView<RootObj, String> composedPropertyOptional =
                nestedObjOptional.andThen(innerObjOptional).andThen(propertyOptional);
        OptionalView<RootObj, String> composedPropertyView =
                nestedObjOptional.andThen(maybeInnerObjOptional).andThen(propertyView);
        OptionalView<RootObj, String> composedMaybePropertyOptional =
                nestedObjOptional.andThen(innerObjOptional).andThen(maybePropertyOptional);
        OptionalView<RootObj, String> composedMaybeMaybePropertyOptional =
                nestedObjOptional.andThen(maybeInnerObjOptional).andThen(maybePropertyOptional);

        RootObj o = new RootObj(null);

        assertFalse(composedPropertyOptional.getOptional(o).isPresent());
        assertFalse(composedPropertyView.getOptional(o).isPresent());
        assertFalse(composedMaybePropertyOptional.getOptional(o).isPresent());
        assertFalse(composedMaybeMaybePropertyOptional.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(null, Optional.empty()));

        assertFalse(composedPropertyOptional.getOptional(o).isPresent());
        assertFalse(composedPropertyView.getOptional(o).isPresent());
        assertFalse(composedMaybePropertyOptional.getOptional(o).isPresent());
        assertFalse(composedMaybeMaybePropertyOptional.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(new InnerObj(null, Optional.empty()), Optional.empty()));

        assertFalse(composedPropertyOptional.getOptional(o).isPresent());
        assertFalse(composedPropertyView.getOptional(o).isPresent());
        assertFalse(composedMaybePropertyOptional.getOptional(o).isPresent());
        assertFalse(composedMaybeMaybePropertyOptional.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(
                new InnerObj("property", Optional.of("maybeProperty")),
                Optional.of(new InnerObj("property", Optional.of("maybeProperty")))
        ));

        assertEquals(o.getNestedObj().getInnerObj().getProperty(), composedPropertyOptional.getOptional(o).orElse(null));
        assertEquals(o.getNestedObj().getInnerObj().getProperty(), composedPropertyView.getOptional(o).orElse(null));
        assertEquals(o.getNestedObj().getInnerObj().getMaybeProperty(), composedMaybePropertyOptional.getOptional(o));
        assertEquals(o.getNestedObj().getInnerObj().getMaybeProperty(), composedMaybeMaybePropertyOptional.getOptional(o));
    }

    @Test
    public void testCompose() throws Exception {
        OptionalView<RootObj, String> composedPropertyView =
                propertyOptional.compose(innerObjOptional).compose(nestedObjOptional);
        OptionalView<RootObj, String> composedMaybePropertyView =
                maybePropertyOptional.compose(innerObjOptional).compose(nestedObjOptional);

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
