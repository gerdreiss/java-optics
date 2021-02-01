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

    private final Lens<RootObj, NestedObj> nestedObjLens = Lens.of(
            RootObj::getNestedObj,
            (rootObj, innerObj) -> new RootObj(innerObj, rootObj.getMaybeNestedObj(), rootObj.getNestedObjStream()));

    private final Lens<NestedObj, InnerObj> innerObjLens = Lens.of(
            NestedObj::getInnerObj,
            (nestedObj, innerInnerObj) -> new NestedObj(innerInnerObj, nestedObj.getMaybeInnerObj(), nestedObj.getInnerObjStream()));

    private final Lens<InnerObj, String> propertyLens = Lens.of(
            InnerObj::getProperty,
            (innerObj, property) -> new InnerObj(property, innerObj.getMaybeProperty(), innerObj.getPropertyStream()));

    private final StreamLens<InnerObj, String> propertyStreamLens = StreamLens.of(
            InnerObj::getPropertyStream,
            (innerObj, propertyStream) -> new InnerObj(innerObj.getProperty(), innerObj.getMaybeProperty(), propertyStream));


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
        Lens<RootObj, String> composedPropertyLens = nestedObjLens.andThen(innerObjLens).andThen(propertyLens);
        StreamLens<RootObj, String> composedPropertyStreamLens = nestedObjLens.andThen(innerObjLens).andThen(propertyStreamLens);

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

        o = new RootObj(new NestedObj(new InnerObj(PROP, Optional.of(MAYBE_PROP), Stream.of(PROP))));

        assertEquals(1, composedPropertyStreamLens.getStream(o).count());

        updated = composedPropertyLens.set(o, "newProperty");
        assertEquals("newProperty", composedPropertyLens.get(updated));

        updated = composedPropertyLens.modify(updated, String::toUpperCase);
        assertEquals("NEWPROPERTY", composedPropertyLens.get(updated));

    }

    @Test
    public void compose() {
        Lens<RootObj, String> composedPropertyLens =
                propertyLens.compose(innerObjLens).compose(nestedObjLens);
        StreamLens<RootObj, String> composedPropertyStreamLens =
                propertyStreamLens.compose(innerObjLens).compose(nestedObjLens);

        RootObj o = new RootObj(null);

        RootObj updated = composedPropertyLens.set(o, PROP);
        assertNull(composedPropertyLens.get(updated));

        o = new RootObj(new NestedObj(null));

        updated = composedPropertyLens.set(o, PROP);
        assertNull(composedPropertyLens.get(updated));

        o = new RootObj(new NestedObj(new InnerObj(null)));

        updated = composedPropertyLens.set(o, PROP);
        assertEquals(PROP, composedPropertyLens.get(updated));

        o = new RootObj(new NestedObj(new InnerObj(PROP, Optional.of(MAYBE_PROP), Stream.of(PROP))));

        assertEquals(1, composedPropertyStreamLens.getStream(o).count());

        updated = composedPropertyLens.set(o, "newProperty");
        assertEquals("newProperty", composedPropertyLens.get(updated));

        updated = composedPropertyLens.modify(updated, String::toUpperCase);
        assertEquals("NEWPROPERTY", composedPropertyLens.get(updated));
    }
}