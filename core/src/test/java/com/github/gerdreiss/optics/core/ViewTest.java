package com.github.gerdreiss.optics.core;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitPlatform.class)
public class ViewTest extends TestModel {

    @Test
    public void testApply() {
        var o = new InnerObj(null, Optional.empty());

        assertNull(innerObjPropertyView.apply(o));
        assertFalse(innerObjPropertyOptionalView.apply(o).isPresent());

        o = new InnerObj("property", Optional.of("maybeProperty"));

        assertEquals(o.getProperty(), innerObjPropertyView.apply(o));
        assertEquals(o.getPropertyOptional(), innerObjPropertyOptionalView.apply(o));
    }

    @Test
    public void testGet() {
        var o = new InnerObj(null, Optional.empty());

        assertNull(innerObjPropertyView.get(o));
        assertFalse(innerObjPropertyOptionalView.getOptional(o).isPresent());

        o = new InnerObj("property", Optional.of("maybeProperty"));

        assertEquals(o.getProperty(), innerObjPropertyView.get(o));
        assertEquals(o.getPropertyOptional(), innerObjPropertyOptionalView.getOptional(o));
    }

    @Test
    public void testAndThen() {

        var composedPropertyView =
                rootObjNestedObjView.andThen(nestedObjInnerObjView).andThen(innerObjPropertyView);

        var composedMaybePropertyView =
                rootObjNestedObjView.andThen(nestedObjInnerObjView).andThen(innerObjPropertyOptionalView);

        var composedMaybeMaybePropertyView =
                rootObjNestedObjView.andThen(nestedObjInnerObjOptionalView).andThen(innerObjPropertyOptionalView);

        var o = new RootObj(null);

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
        assertEquals(o.getNestedObj().getInnerObj().getPropertyOptional(), composedMaybePropertyView.getOptional(o));
        assertEquals(o.getNestedObj().getInnerObj().getPropertyOptional(), composedMaybeMaybePropertyView.getOptional(o));
    }

    @Test
    public void testCompose() {

        var composedPropertyView =
                innerObjPropertyView.compose(nestedObjInnerObjView).compose(rootObjNestedObjView);
        var composedMaybePropertyOptional =
                innerObjPropertyOptionalView.compose(nestedObjInnerObjView).compose(rootObjNestedObjView);
        var composedMaybeMaybePropertyOptional =
                innerObjPropertyOptionalView.compose(nestedObjInnerObjOptionalView).compose(rootObjNestedObjView);
        var composedPropertyOptional =
                innerObjPropertyView.compose(nestedObjInnerObjOptionalView).compose(rootObjNestedObjView);

        var o = new RootObj(null);

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
        assertEquals(o.getNestedObj().getInnerObj().getPropertyOptional(), composedMaybePropertyOptional.getOptional(o));
        assertEquals(o.getNestedObj().getInnerObj().getPropertyOptional(), composedMaybeMaybePropertyOptional.getOptional(o));
    }
}
