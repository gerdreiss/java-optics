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
        InnerObj created = new InnerObj(PROP);
        InnerObj updated = innerObjPropertyOptionalLens.set(created, MAYBE_PROP);

        assertTrue(updated.getPropertyOptional().isPresent());
        assertEquals(MAYBE_PROP, updated.getPropertyOptional());
    }

    @Test
    public void setPartial() {
        InnerObj created = new InnerObj(null);
        InnerObj updated = innerObjPropertyOptionalLens.set(created).apply(MAYBE_PROP);

        assertTrue(updated.getPropertyOptional().isPresent());
        assertEquals(MAYBE_PROP, updated.getPropertyOptional());
    }

    @Test
    public void modify() {
        InnerObj created = new InnerObj(PROP, MAYBE_PROP);
        InnerObj updated = innerObjPropertyOptionalLens.modify(created, String::toUpperCase);

        assertTrue(updated.getPropertyOptional().isPresent());
        assertEquals(MAYBE_PROP.map(String::toUpperCase), updated.getPropertyOptional());
    }

    @Test
    public void andThen() {
        OptionalLens<RootObj, String> composed =
                rootObjNestedObjOptionalLens.andThen(nestedObjInnerObjOptionalLens).andThen(innerObjPropertyOptionalLens);

        RootObj o = new RootObj(null);

        RootObj updated = composed.set(o, MAYBE_PROP);
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
        OptionalLens<RootObj, String> composed = innerObjPropertyOptionalLens.compose(nestedObjInnerObjOptionalLens).compose(rootObjNestedObjOptionalLens);

        RootObj o = new RootObj(null);

        RootObj updated = composed.set(o, MAYBE_PROP);
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