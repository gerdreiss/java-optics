package com.github.gerdreiss.optics;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class OptionalLensTest extends TestModel {

    private final OptionalLens<RootObj, NestedObj> nestedObjLens =
            OptionalLens.of(
                    rootObj -> Optional.ofNullable(rootObj.getNestedObj()),
                    (ignore, innerObj) -> new RootObj(innerObj));
    private final OptionalLens<NestedObj, InnerObj> innerObjLens =
            OptionalLens.of(
                    nestedObj -> Optional.ofNullable(nestedObj.getInnerObj()),
                    (ignore, innerInnerObj) -> new NestedObj(innerInnerObj));
    private final OptionalLens<InnerObj, String> propertyLens =
            OptionalLens.of(
                    innerObj -> Optional.ofNullable(innerObj.getProperty()),
                    (ignore, property) -> new InnerObj(property));

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
    public void andThen() {
        OptionalLens<RootObj, String> composed = nestedObjLens.andThen(innerObjLens).andThen(propertyLens);

        RootObj o = new RootObj(null);

        RootObj updated = composed.set(o, "property");
        assertNull(composed.getOptional(updated).orElse(null));

        o = new RootObj(new NestedObj(null));

        updated = composed.set(o, "property");
        assertNull(composed.getOptional(updated).orElse(null));

        o = new RootObj(new NestedObj(new InnerObj(null)));

        updated = composed.set(o, "property");
        assertEquals("property", composed.getOptional(updated).orElse(null));

        o = new RootObj(new NestedObj(new InnerObj("property")));

        updated = composed.set(o, "newProperty");
        assertEquals("newProperty", composed.getOptional(updated).orElse(null));
    }

    @Test
    public void compose() {
        OptionalLens<RootObj, String> composed = propertyLens.compose(innerObjLens).compose(nestedObjLens);

        RootObj o = new RootObj(null);

        RootObj updated = composed.set(o, "property");
        assertNull(composed.getOptional(updated).orElse(null));

        o = new RootObj(new NestedObj(null));

        updated = composed.set(o, "property");
        assertNull(composed.getOptional(updated).orElse(null));

        o = new RootObj(new NestedObj(new InnerObj(null)));

        updated = composed.set(o, "property");
        assertEquals("property", composed.getOptional(updated).orElse(null));

        o = new RootObj(new NestedObj(new InnerObj("property")));

        updated = composed.set(o, "newProperty");
        assertEquals("newProperty", composed.getOptional(updated).orElse(null));
    }
}