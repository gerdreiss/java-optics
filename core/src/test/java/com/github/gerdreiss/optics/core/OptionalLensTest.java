package com.github.gerdreiss.optics.core;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitPlatform.class)
public class OptionalLensTest extends TestModel {


    private final OptionalLens<RootObj, NestedObj> nestedObjOptionalLens =
            OptionalLens.of(
                    RootObj::getMaybeNestedObj,
                    (rootObj, maybeNestedObj) -> new RootObj(rootObj.getNestedObj(), maybeNestedObj, rootObj.getNestedObjStream()));

    private final OptionalLens<NestedObj, InnerObj> innerObjOptionalLens =
            OptionalLens.of(
                    NestedObj::getMaybeInnerObj,
                    (nestedObj, maybeInnerObj) -> new NestedObj(nestedObj.getInnerObj(), maybeInnerObj, nestedObj.getInnerObjStream()));

    private final OptionalLens<InnerObj, String> propertyOptionalLens =
            OptionalLens.of(
                    InnerObj::getMaybeProperty,
                    (innerObj, maybeProperty) -> new InnerObj(innerObj.getProperty(), maybeProperty, innerObj.getPropertyStream()));

    @Test
    public void set() {
        InnerObj created = new InnerObj(MAYBE_PROP);
        InnerObj updated = propertyOptionalLens.set(created, Optional.of(MAYBE_PROP));

        assertTrue(updated.getMaybeProperty().isPresent());
        assertEquals(MAYBE_PROP, updated.getMaybeProperty().get());
    }

    @Test
    public void setPartial() {
        InnerObj created = new InnerObj(null);
        InnerObj updated = propertyOptionalLens.set(created).apply(Optional.of(MAYBE_PROP));

        assertTrue(updated.getMaybeProperty().isPresent());
        assertEquals(MAYBE_PROP, updated.getMaybeProperty().get());
    }

    @Test
    public void modify() {
        InnerObj created = new InnerObj(MAYBE_PROP, Optional.of(MAYBE_PROP));
        InnerObj updated = propertyOptionalLens.modify(created, String::toUpperCase);

        assertTrue(updated.getMaybeProperty().isPresent());
        assertEquals(MAYBE_PROP.toUpperCase(), updated.getMaybeProperty().get());
    }

    @Test
    public void andThen() {
        OptionalLens<RootObj, String> composed =
                nestedObjOptionalLens.andThen(innerObjOptionalLens).andThen(propertyOptionalLens);

        RootObj o = new RootObj(null);

        RootObj updated = composed.set(o, Optional.of(MAYBE_PROP));
        assertFalse(composed.getOptional(updated).isPresent());

        o = new RootObj(new NestedObj(null));

        updated = composed.set(o, Optional.of(MAYBE_PROP));
        assertFalse(composed.getOptional(updated).isPresent());

        o = new RootObj(new NestedObj(new InnerObj(PROP)));

        updated = composed.set(o, Optional.of(MAYBE_PROP));
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
        OptionalLens<RootObj, String> composed = propertyOptionalLens.compose(innerObjOptionalLens).compose(nestedObjOptionalLens);

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
        assertEquals(MAYBE_PROP, composed.getOptional(updated).orElse(null));

        updated = composed.set(o, "newProperty");
        assertEquals("newProperty", composed.getOptional(updated).orElse(null));
    }
}