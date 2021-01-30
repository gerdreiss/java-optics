package com.github.gerdreiss.optics;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LensTest extends TestModel {

    private final Lens<RootObj, NestedObj> nestedObjLens = Lens.of(
            RootObj::getNestedObj,
            (rootObj, innerObj) -> new RootObj(innerObj, rootObj.getMaybeNestedObj(), rootObj.getNestedObjStream()));

    private final Lens<NestedObj, InnerObj> innerObjLens = Lens.of(
            NestedObj::getInnerObj,
            (nestedObj, innerInnerObj) -> new NestedObj(innerInnerObj, nestedObj.getMaybeInnerObj(), nestedObj.getInnerObjStream()));

    private final Lens<InnerObj, String> propertyLens = Lens.of(
            InnerObj::getProperty,
            (innerObj, property) -> new InnerObj(property, innerObj.getMaybeProperty(), innerObj.getPropertyStream()));

    @Test
    public void set() {
        InnerObj created = new InnerObj(null);
        InnerObj updated = propertyLens.set(created, PROP);
        assertEquals(PROP, updated.getProperty());
    }

    @Test
    public void setPartial() {
        InnerObj created = new InnerObj(null);
        InnerObj updated = propertyLens.set(created).apply(PROP);
        assertEquals(PROP, updated.getProperty());
    }

    @Test
    public void modify() {
        InnerObj created = new InnerObj(PROP);
        InnerObj modified = propertyLens.modify(created, String::toUpperCase);
        assertEquals(PROP.toUpperCase(), modified.getProperty());
    }

    @Test
    public void andThen() {
        Lens<RootObj, String> composed = nestedObjLens.andThen(innerObjLens).andThen(propertyLens);

        RootObj o = new RootObj(null);

        RootObj updated = composed.set(o, PROP);
        assertNull(composed.get(updated));

        o = new RootObj(new NestedObj(null));

        updated = composed.set(o, PROP);
        assertNull(composed.get(updated));

        o = new RootObj(new NestedObj(new InnerObj(null)));

        updated = composed.set(o, PROP);
        assertEquals(PROP, composed.get(updated));

        o = new RootObj(new NestedObj(new InnerObj(PROP)));

        updated = composed.set(o, "newProperty");
        assertEquals("newProperty", composed.get(updated));
    }

    @Test
    public void compose() {
        Lens<RootObj, String> composed = propertyLens.compose(innerObjLens).compose(nestedObjLens);

        RootObj o = new RootObj(null);

        RootObj updated = composed.set(o, PROP);
        assertNull(composed.get(updated));

        o = new RootObj(new NestedObj(null));

        updated = composed.set(o, PROP);
        assertNull(composed.get(updated));

        o = new RootObj(new NestedObj(new InnerObj(null)));

        updated = composed.set(o, PROP);
        assertEquals(PROP, composed.get(updated));

        o = new RootObj(new NestedObj(new InnerObj(PROP)));

        updated = composed.set(o, "newProperty");
        assertEquals("newProperty", composed.get(updated));
    }
}