package com.github.gerdreiss.optics.core;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitPlatform.class)
public class OptionalLensTest extends TestModel {

    @Test
    public void set() {
        var created = new InnerObj(PROP);
        var updated = innerObjPropertyOptionalLens.set(created, MAYBE_PROP);

        assertTrue(updated.getPropertyOptional().isPresent());
        assertEquals(MAYBE_PROP, updated.getPropertyOptional());
    }

    @Test
    public void setPartial() {
        var created = new InnerObj(null);
        var updated = innerObjPropertyOptionalLens.set(created).apply(MAYBE_PROP);

        assertTrue(updated.getPropertyOptional().isPresent());
        assertEquals(MAYBE_PROP, updated.getPropertyOptional());
    }

    @Test
    public void modify() {
        var created = new InnerObj(PROP, MAYBE_PROP);
        var updated = innerObjPropertyOptionalLens.modify(created, String::toUpperCase);

        assertTrue(updated.getPropertyOptional().isPresent());
        assertEquals(MAYBE_PROP.map(String::toUpperCase), updated.getPropertyOptional());
    }

    @Test
    public void andThen() {
        var composed = rootObjNestedObjOptionalLens.andThen(nestedObjInnerObjOptionalLens).andThen(innerObjPropertyOptionalLens);

        var o = new RootObj(null);

        var updated = composed.set(o, MAYBE_PROP);
        assertFalse(composed.getOptional(updated).isPresent());

        o = new RootObj(new NestedObj(null));

        updated = composed.set(o, MAYBE_PROP);
        assertFalse(composed.getOptional(updated).isPresent());

        o = new RootObj(new NestedObj(new InnerObj(PROP)));

        updated = composed.set(o, MAYBE_PROP);
        assertFalse(composed.getOptional(updated).isPresent());

        o = new RootObj(null,
                Optional.of(new NestedObj(null,
                        Optional.of(new InnerObj(PROP)))));

        updated = composed.set(o, Optional.of("newProperty"));
        assertEquals("newProperty", composed.getOptional(updated).get());

        updated = composed.modify(updated, String::toUpperCase);
        assertEquals("NEWPROPERTY", composed.getOptional(updated).get());
    }

    @Test
    public void compose() {
        var composed = innerObjPropertyOptionalLens.compose(nestedObjInnerObjOptionalLens).compose(rootObjNestedObjOptionalLens);

        var o = new RootObj(null);

        var updated = composed.set(o, MAYBE_PROP);
        assertNull(composed.getOptional(updated).orElse(null));

        o = new RootObj(new NestedObj(null));

        updated = composed.set(o, MAYBE_PROP);
        assertNull(composed.getOptional(updated).orElse(null));

        o = new RootObj(null,
                Optional.of(new NestedObj(null,
                        Optional.of(new InnerObj(PROP)))));

        updated = composed.set(o, MAYBE_PROP);
        assertEquals(MAYBE_PROP, composed.getOptional(updated));

        updated = composed.set(o, "newProperty");
        assertEquals("newProperty", composed.getOptional(updated).orElse(null));
    }
}