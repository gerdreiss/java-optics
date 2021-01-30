package com.github.gerdreiss.optics;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitPlatform.class)
public class ViewTest extends TestModel {

    private final View<RootObj, NestedObj> nestedObjView = View.of(RootObj::getNestedObj);
    private final View<NestedObj, InnerObj> innerObjView = View.of(NestedObj::getInnerObj);
    private final View<InnerObj, String> propertyView = View.of(InnerObj::getProperty);

    private final OptionalView<NestedObj, InnerObj> maybeInnerObjOptional = OptionalView.of(NestedObj::getMaybeInnerObj);
    private final OptionalView<InnerObj, String> maybePropertyOptional = OptionalView.of(InnerObj::getMaybeProperty);

    @Test
    public void testApply() {
        InnerObj o = new InnerObj(null, Optional.empty());

        assertNull(propertyView.apply(o));
        assertFalse(maybePropertyOptional.apply(o).isPresent());

        o = new InnerObj("property", Optional.of("maybeProperty"));

        assertEquals(o.getProperty(), propertyView.apply(o));
        assertEquals(o.getMaybeProperty(), maybePropertyOptional.apply(o));
    }

    @Test
    public void testGet() {
        InnerObj o = new InnerObj(null, Optional.empty());

        assertNull(propertyView.get(o));
        assertFalse(maybePropertyOptional.getOptional(o).isPresent());

        o = new InnerObj("property", Optional.of("maybeProperty"));

        assertEquals(o.getProperty(), propertyView.get(o));
        assertEquals(o.getMaybeProperty(), maybePropertyOptional.getOptional(o));
    }

    @Test
    public void testAndThen() {

        View<RootObj, String> composedPropertyView =
                nestedObjView.andThen(innerObjView).andThen(propertyView);

        OptionalView<RootObj, String> composedMaybePropertyView =
                nestedObjView.andThen(innerObjView).andThen(maybePropertyOptional);

        OptionalView<RootObj, String> composedMaybeMaybePropertyView =
                nestedObjView.andThen(maybeInnerObjOptional).andThen(maybePropertyOptional);

        RootObj o = new RootObj(null);

        assertNull(composedPropertyView.apply(o));
        assertFalse(composedMaybePropertyView.getOptional(o).isPresent());
        assertFalse(composedMaybeMaybePropertyView.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(null));

        assertNull(composedPropertyView.apply(o));
        assertFalse(composedMaybePropertyView.getOptional(o).isPresent());
        assertFalse(composedMaybeMaybePropertyView.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(new InnerObj(null)));

        assertNull(composedPropertyView.apply(o));
        assertFalse(composedMaybePropertyView.getOptional(o).isPresent());
        assertFalse(composedMaybeMaybePropertyView.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(
                new InnerObj("property", Optional.of("maybeProperty")),
                Optional.of(new InnerObj("property", Optional.of("maybeProperty")))
        ));

        assertEquals(o.getNestedObj().getInnerObj().getProperty(), composedPropertyView.get(o));
        assertEquals(o.getNestedObj().getInnerObj().getMaybeProperty(), composedMaybePropertyView.getOptional(o));
        assertEquals(o.getNestedObj().getInnerObj().getMaybeProperty(), composedMaybeMaybePropertyView.getOptional(o));
    }

    @Test
    public void testCompose() {

        View<RootObj, String> composedPropertyView =
                propertyView.compose(innerObjView).compose(nestedObjView);
        OptionalView<RootObj, String> composedMaybePropertyOptional =
                maybePropertyOptional.compose(innerObjView).compose(nestedObjView);
        OptionalView<RootObj, String> composedMaybeMaybePropertyOptional =
                maybePropertyOptional.compose(maybeInnerObjOptional).compose(nestedObjView);
        OptionalView<RootObj, String> composedPropertyOptional =
                propertyView.compose(maybeInnerObjOptional).compose(nestedObjView);

        RootObj o = new RootObj(null);

        assertNull(composedPropertyView.get(o));
        assertFalse(composedPropertyOptional.getOptional(o).isPresent());
        assertFalse(composedMaybePropertyOptional.getOptional(o).isPresent());
        assertFalse(composedMaybeMaybePropertyOptional.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(null));

        assertNull(composedPropertyView.get(o));
        assertFalse(composedPropertyOptional.getOptional(o).isPresent());
        assertFalse(composedMaybePropertyOptional.getOptional(o).isPresent());
        assertFalse(composedMaybeMaybePropertyOptional.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(new InnerObj(null, Optional.empty())));

        assertNull(composedPropertyView.get(o));
        assertFalse(composedPropertyOptional.getOptional(o).isPresent());
        assertFalse(composedMaybePropertyOptional.getOptional(o).isPresent());
        assertFalse(composedMaybeMaybePropertyOptional.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(
                new InnerObj("property", Optional.of("maybeProperty")),
                Optional.of(new InnerObj("property", Optional.of("maybeProperty")))
        ));

        assertEquals(o.getNestedObj().getInnerObj().getProperty(), composedPropertyView.get(o));
        assertEquals(o.getNestedObj().getInnerObj().getProperty(), composedPropertyOptional.getOptional(o).orElse(null));
        assertEquals(o.getNestedObj().getInnerObj().getMaybeProperty(), composedMaybePropertyOptional.getOptional(o));
        assertEquals(o.getNestedObj().getInnerObj().getMaybeProperty(), composedMaybeMaybePropertyOptional.getOptional(o));
    }
}
