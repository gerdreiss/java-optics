package com.github.gerdreiss.optics;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class ViewTest extends TestModel {

    private final View<RootObj, NestedObj> innerObjView = View.of(RootObj::getNestedObj);
    private final View<NestedObj, InnerObj> innerInnerObjView = View.of(NestedObj::getInnerObj);
    private final View<InnerObj, String> propertyView = View.of(InnerObj::getProperty);
    private final View<InnerObj, Optional<String>> maybePropertyView = View.of(InnerObj::getMaybeProperty);

    @Test
    public void testApply() {
        InnerObj o = new InnerObj(null, Optional.empty());

        assertNull(propertyView.apply(o));
        assertFalse(maybePropertyView.apply(o).isPresent());

        o = new InnerObj("property", Optional.of("maybeProperty"));

        assertEquals(o.getProperty(), propertyView.apply(o));
        assertEquals(o.getMaybeProperty(), maybePropertyView.apply(o));
    }

    @Test
    public void testGet() {
        InnerObj o = new InnerObj(null, Optional.empty());

        assertNull(propertyView.get(o));
        assertFalse(maybePropertyView.get(o).isPresent());

        o = new InnerObj("property", Optional.of("maybeProperty"));

        assertEquals(o.getProperty(), propertyView.get(o));
        assertEquals(o.getMaybeProperty(), maybePropertyView.get(o));
    }

    @Test
    public void testAndThen() {

        View<RootObj, String> composedPropertyView =
                innerObjView.andThen(innerInnerObjView).andThen(propertyView);
        View<RootObj, Optional<String>> composedMeybePropertyView =
                innerObjView.andThen(innerInnerObjView).andThen(maybePropertyView);

        RootObj o = new RootObj(null);

        assertNull(composedPropertyView.apply(o));

        o = new RootObj(new NestedObj(null));

        assertNull(composedPropertyView.apply(o));

        o = new RootObj(new NestedObj(new InnerObj(null)));

        assertNull(composedPropertyView.apply(o));

        o = new RootObj(new NestedObj(new InnerObj("property", Optional.of("maybeProperty"))));

        assertEquals(o.getNestedObj().getInnerObj().getProperty(), composedPropertyView.get(o));
        assertEquals(o.getNestedObj().getInnerObj().getMaybeProperty(), composedMeybePropertyView.get(o));
    }

    @Test
    public void testCompose() {

        View<RootObj, String> composedPropertyView =
                propertyView.compose(innerInnerObjView).compose(innerObjView);
        View<RootObj, Optional<String>> composedMaybePropertyView =
                maybePropertyView.compose(innerInnerObjView).compose(innerObjView);

        RootObj o = new RootObj(null);

        assertNull(composedPropertyView.get(o));

        o = new RootObj(new NestedObj(null));

        assertNull(composedPropertyView.get(o));

        o = new RootObj(new NestedObj(new InnerObj(null, Optional.empty())));

        assertNull(composedPropertyView.get(o));

        o = new RootObj(new NestedObj(new InnerObj("property", Optional.of("maybeProperty"))));

        assertEquals(o.getNestedObj().getInnerObj().getProperty(), composedPropertyView.get(o));
        assertEquals(o.getNestedObj().getInnerObj().getMaybeProperty(), composedMaybePropertyView.get(o));
    }
}
