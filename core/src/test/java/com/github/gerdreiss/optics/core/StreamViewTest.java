package com.github.gerdreiss.optics.core;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(JUnitPlatform.class)
class StreamViewTest extends TestModel {

    @Test
    void apply() {
        var o = new InnerObj(null);
        assertEquals(0, innerObjPropertyStreamView.apply(o).count());
        o = new InnerObj(null, Optional.empty(), Stream.of("val1", "val2"));
        assertEquals(2, innerObjPropertyStreamView.apply(o).count());
    }

    @Test
    void getStream() {
        var o = new InnerObj(null);
        assertEquals(0, innerObjPropertyStreamView.getStream(o).count());
        o = new InnerObj(null, Optional.empty(), Stream.of("val1", "val2"));
        assertEquals(2, innerObjPropertyStreamView.getStream(o).count());
    }

    @Test
    void getFirst() {
        var o = new InnerObj(null);
        assertEquals(0, innerObjPropertyStreamView.getStream(o).count());
        o = new InnerObj(null, Optional.empty(), Stream.of("val1", "val2"));
        assertEquals(Optional.of("val1"), innerObjPropertyStreamView.getFirst(o));
    }

    @Test
    void getLast() {
        var o = new InnerObj(null);
        assertEquals(0, innerObjPropertyStreamView.getStream(o).count());
        o = new InnerObj(null, Optional.empty(), Stream.of("val1", "val2"));
        assertEquals(Optional.of("val2"), innerObjPropertyStreamView.getLast(o));
    }

    @Test
    void getAt() {
        var o = new InnerObj(null);
        assertEquals(0, innerObjPropertyStreamView.getStream(o).count());
        o = new InnerObj(null, Optional.empty(), Stream.of("val1", "val2"));
        assertEquals(Optional.of("val2"), innerObjPropertyStreamView.getAt(o, 1));
    }

    @Test
    void getAtOutOfIndexArray() {
        var o = new InnerObj(null);
        assertEquals(0, innerObjPropertyStreamView.getStream(o).count());
        o = new InnerObj(null, Optional.empty(), Stream.of("val1", "val2"));
        assertEquals(Optional.empty(), innerObjPropertyStreamView.getAt(o, 2));
    }

    @Test
    void andThenView() {
        var o = new NestedObj(null, Optional.empty(), Stream.of(new InnerObj(PROP)), Collections.emptyMap());
        var composed = nestedObjInnerObjStreamView.andThen(innerObjPropertyView);
        var properties = composed.getStream(o);
        assertEquals(Optional.of(PROP), properties.findFirst());

    }

    @Test
    void andThenOptionalView() {
        var o = new NestedObj(null, Optional.empty(), Stream.of(new InnerObj(PROP, MAYBE_PROP)), Collections.emptyMap());
        var composed = nestedObjInnerObjStreamView.andThen(innerObjPropertyOptionalView);
        var properties = composed.getStream(o);
        var maybeMaybeProperty = properties.findFirst();
        assertTrue(maybeMaybeProperty.isPresent());
        assertEquals(MAYBE_PROP, maybeMaybeProperty.get());
    }

    @Test
    void andThenStreamView() {
        var o = new NestedObj(
                null,
                Optional.empty(),
                Stream.of(
                        new InnerObj(PROP, MAYBE_PROP, Stream.of("val0", "val1")),
                        new InnerObj(PROP, MAYBE_PROP, Stream.of("val2", "val3"))));
        var composed = nestedObjInnerObjStreamView.andThen(innerObjPropertyStreamView);
        var properties = composed.getStream(o);
        assertEquals(4, properties.count());
    }

    @Test
    void composeView() {
        var o = new NestedObj(null, Optional.empty(), Stream.of(new InnerObj(PROP)), Collections.emptyMap());
        var composed = innerObjPropertyView.compose(nestedObjInnerObjStreamView);
        var properties = composed.getStream(o);
        assertEquals(Optional.of(PROP), properties.findFirst());

    }

    @Test
    void composeOptionalView() {
        var o = new NestedObj(null, Optional.empty(), Stream.of(new InnerObj(PROP, MAYBE_PROP)), Collections.emptyMap());
        var composed = innerObjPropertyOptionalView.compose(nestedObjInnerObjStreamView);
        var properties = composed.getStream(o);
        var maybeMaybeProperty = properties.findFirst();
        assertTrue(maybeMaybeProperty.isPresent());
        assertEquals(MAYBE_PROP, maybeMaybeProperty.get());
    }

    @Test
    void composeStreamView() {
        var o = new NestedObj(
                null,
                Optional.empty(),
                Stream.of(
                        new InnerObj(PROP, MAYBE_PROP, Stream.of("val0", "val1")),
                        new InnerObj(PROP, MAYBE_PROP, Stream.of("val2", "val3"))));
        var composed = innerObjPropertyStreamView.compose(nestedObjInnerObjStreamView);
        var properties = composed.getStream(o);
        assertEquals(4, properties.count());
    }
}