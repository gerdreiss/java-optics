package com.github.gerdreiss.optics.core;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@RunWith(JUnitPlatform.class)
public class LensTest extends TestModel {

    @Test
    public void set() {
        InnerObj created = new InnerObj(null);
        InnerObj updated = innerObjPropertyLens.set(created, PROP);
        assertEquals(PROP, updated.getProperty());
    }

    @Test
    public void setPartial() {
        InnerObj created = new InnerObj(null);
        InnerObj updated = innerObjPropertyLens.set(created).apply(PROP);
        assertEquals(PROP, updated.getProperty());
    }

    @Test
    public void modify() {
        InnerObj created = new InnerObj(PROP);
        InnerObj modified = innerObjPropertyLens.modify(created, String::toUpperCase);
        assertEquals(PROP.toUpperCase(), modified.getProperty());
    }

    @Test
    public void andThen() {
        Lens<RootObj, String> composedPropertyLens = rootObjNestedObjLens.andThen(nestedObjInnerObjLens).andThen(innerObjPropertyLens);
        StreamLens<RootObj, String> composedPropertyStreamLens = rootObjNestedObjLens.andThen(nestedObjInnerObjLens).andThen(innerObjPropertyStreamLens);

        RootObj o = new RootObj(null);

        assertEquals(0, composedPropertyStreamLens.getStream(o).count());

        RootObj updated = composedPropertyLens.set(o, PROP);
        assertNull(composedPropertyLens.get(updated));

        o = new RootObj(new NestedObj(null));

        assertEquals(0, composedPropertyStreamLens.getStream(o).count());

        updated = composedPropertyLens.set(o, PROP);
        assertNull(composedPropertyLens.get(updated));

        o = new RootObj(new NestedObj(new InnerObj(null)));

        assertEquals(0, composedPropertyStreamLens.getStream(o).count());

        updated = composedPropertyLens.set(o, PROP);
        assertEquals(PROP, composedPropertyLens.get(updated));

        o = new RootObj(new NestedObj(new InnerObj(PROP, Optional.empty(), Stream.of(PROP))));

        assertEquals(1, composedPropertyStreamLens.getStream(o).count());

        updated = composedPropertyLens.set(o, "newProperty");
        assertEquals("newProperty", composedPropertyLens.get(updated));

        updated = composedPropertyLens.modify(updated, String::toUpperCase);
        assertEquals("NEWPROPERTY", composedPropertyLens.get(updated));

    }

    @Test
    public void compose() {
        Lens<RootObj, String> composedPropertyLens =
                innerObjPropertyLens.compose(nestedObjInnerObjLens).compose(rootObjNestedObjLens);
        StreamLens<RootObj, String> composedPropertyStreamLens =
                innerObjPropertyStreamLens.compose(nestedObjInnerObjLens).compose(rootObjNestedObjLens);

        RootObj o = new RootObj(null);

        RootObj updated = composedPropertyLens.set(o, PROP);
        assertNull(composedPropertyLens.get(updated));

        o = new RootObj(new NestedObj(null));

        updated = composedPropertyLens.set(o, PROP);
        assertNull(composedPropertyLens.get(updated));

        o = new RootObj(new NestedObj(new InnerObj(null)));

        updated = composedPropertyLens.set(o, PROP);
        assertEquals(PROP, composedPropertyLens.get(updated));

        o = new RootObj(new NestedObj(new InnerObj(PROP, Optional.empty(), Stream.of(PROP))));

        assertEquals(1, composedPropertyStreamLens.getStream(o).count());

        updated = composedPropertyLens.set(o, "newProperty");
        assertEquals("newProperty", composedPropertyLens.get(updated));

        updated = composedPropertyLens.modify(updated, String::toUpperCase);
        assertEquals("NEWPROPERTY", composedPropertyLens.get(updated));
    }
}