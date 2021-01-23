package com.github.gerdreiss.optics;

import org.junit.Test;

import java.util.Optional;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LensTest extends TestModel {

    private final Lens<RootObj, NestedObj> nestedObjLens =
            Lens.of(RootObj::getNestedObj, (ignore, innerObj) -> new RootObj(innerObj));
    private final Lens<NestedObj, InnerObj> innerObjLens =
            Lens.of(NestedObj::getInnerObj, (ignore, innerInnerObj) -> new NestedObj(innerInnerObj));
    private final Lens<NestedObj, Optional<InnerObj>> maybeInnerObjLens =
            Lens.of(NestedObj::getMaybeInnerObj, (ignore, maybeInnerObj) -> new NestedObj(ignore.getInnerObj(), maybeInnerObj));
    private final Lens<InnerObj, String> propertyLens =
            Lens.of(InnerObj::getProperty, (ignore, property) -> new InnerObj(property));

    @Test
    public void set() {
        InnerObj created = new InnerObj(null);
        InnerObj updated = propertyLens.set(created, "property");
        assertEquals("property", updated.getProperty());
    }

    @Test
    public void setPartial() {
        InnerObj created = new InnerObj(null);
        InnerObj updated = propertyLens.set(created).apply("property");
        assertEquals("property", updated.getProperty());
    }

    @Test
    public void modify() {
        InnerObj created = new InnerObj("property");
        InnerObj modified = propertyLens.modify(created, String::toUpperCase);
        assertEquals("PROPERTY", modified.getProperty());
    }

    @Test
    public void andThen() {
        Lens<RootObj, String> composed = nestedObjLens.andThen(innerObjLens).andThen(propertyLens);

        RootObj o = new RootObj(null);

        RootObj updated = composed.set(o, "property");
        assertNull(composed.get(updated));

        o = new RootObj(new NestedObj(null));

        updated = composed.set(o, "property");
        assertNull(composed.get(updated));

        o = new RootObj(new NestedObj(new InnerObj(null)));

        updated = composed.set(o, "property");
        assertEquals("property", composed.get(updated));

        o = new RootObj(new NestedObj(new InnerObj("property")));

        updated = composed.set(o, "newProperty");
        assertEquals("newProperty", composed.get(updated));
    }

    @Test
    public void compose() {
        Lens<RootObj, String> composed = propertyLens.compose(innerObjLens).compose(nestedObjLens);

        RootObj o = new RootObj(null);

        RootObj updated = composed.set(o, "property");
        assertNull(composed.get(updated));

        o = new RootObj(new NestedObj(null));

        updated = composed.set(o, "property");
        assertNull(composed.get(updated));

        o = new RootObj(new NestedObj(new InnerObj(null)));

        updated = composed.set(o, "property");
        assertEquals("property", composed.get(updated));

        o = new RootObj(new NestedObj(new InnerObj("property")));

        updated = composed.set(o, "newProperty");
        assertEquals("newProperty", composed.get(updated));
    }
}