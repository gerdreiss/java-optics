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
        InnerObj o = new InnerObj(null);
        assertEquals(0, innerObjPropertyStreamView.apply(o).count());
        o = new InnerObj(null, Optional.empty(), Stream.of("val1", "val2"));
        assertEquals(2, innerObjPropertyStreamView.apply(o).count());
    }

    @Test
    void getStream() {
        InnerObj o = new InnerObj(null);
        assertEquals(0, innerObjPropertyStreamView.getStream(o).count());
        o = new InnerObj(null, Optional.empty(), Stream.of("val1", "val2"));
        assertEquals(2, innerObjPropertyStreamView.getStream(o).count());
    }

    @Test
    void getFirst() {
        InnerObj o = new InnerObj(null);
        assertEquals(0, innerObjPropertyStreamView.getStream(o).count());
        o = new InnerObj(null, Optional.empty(), Stream.of("val1", "val2"));
        assertEquals(Optional.of("val1"), innerObjPropertyStreamView.getFirst(o));
    }

    @Test
    void getLast() {
        InnerObj o = new InnerObj(null);
        assertEquals(0, innerObjPropertyStreamView.getStream(o).count());
        o = new InnerObj(null, Optional.empty(), Stream.of("val1", "val2"));
        assertEquals(Optional.of("val2"), innerObjPropertyStreamView.getLast(o));
    }

    @Test
    void getAt() {
        InnerObj o = new InnerObj(null);
        assertEquals(0, innerObjPropertyStreamView.getStream(o).count());
        o = new InnerObj(null, Optional.empty(), Stream.of("val1", "val2"));
        assertEquals(Optional.of("val2"), innerObjPropertyStreamView.getAt(o, 1));
    }

    @Test
    void getAtOutOfIndexArray() {
        InnerObj o = new InnerObj(null);
        assertEquals(0, innerObjPropertyStreamView.getStream(o).count());
        o = new InnerObj(null, Optional.empty(), Stream.of("val1", "val2"));
        assertEquals(Optional.empty(), innerObjPropertyStreamView.getAt(o, 2));
    }

    @Test
    void andThenView() {
        NestedObj o = new NestedObj(null, Optional.empty(), Stream.of(new InnerObj(PROP)), Collections.emptyMap());
        StreamView<NestedObj, String> composed = nestedObjInnerObjStreamView.andThen(innerObjPropertyView);
        Stream<String> properties = composed.getStream(o);
        assertEquals(Optional.of(PROP), properties.findFirst());

    }

    @Test
    void andThenOptionalView() {
        NestedObj o = new NestedObj(null, Optional.empty(), Stream.of(new InnerObj(PROP, MAYBE_PROP)), Collections.emptyMap());
        StreamView<NestedObj, Optional<String>> composed = nestedObjInnerObjStreamView.andThen(innerObjPropertyOptionalView);
        Stream<Optional<String>> properties = composed.getStream(o);
        Optional<Optional<String>> maybeMaybeProperty = properties.findFirst();
        assertTrue(maybeMaybeProperty.isPresent());
        assertEquals(MAYBE_PROP, maybeMaybeProperty.get());
    }

    @Test
    void andThenStreamView() {
        NestedObj o = new NestedObj(
                null,
                Optional.empty(),
                Stream.of(
                        new InnerObj(PROP, MAYBE_PROP, Stream.of("val0", "val1")),
                        new InnerObj(PROP, MAYBE_PROP, Stream.of("val2", "val3"))));
        StreamView<NestedObj, String> composed = nestedObjInnerObjStreamView.andThen(innerObjPropertyStreamView);
        Stream<String> properties = composed.getStream(o);
        assertEquals(4, properties.count());
    }

    @Test
    void composeView() {
        NestedObj o = new NestedObj(null, Optional.empty(), Stream.of(new InnerObj(PROP)), Collections.emptyMap());
        StreamView<NestedObj, String> composed = innerObjPropertyView.compose(nestedObjInnerObjStreamView);
        Stream<String> properties = composed.getStream(o);
        assertEquals(Optional.of(PROP), properties.findFirst());

    }

    @Test
    void composeOptionalView() {
        NestedObj o = new NestedObj(null, Optional.empty(), Stream.of(new InnerObj(PROP, MAYBE_PROP)), Collections.emptyMap());
        StreamView<NestedObj, Optional<String>> composed = innerObjPropertyOptionalView.compose(nestedObjInnerObjStreamView);
        Stream<Optional<String>> properties = composed.getStream(o);
        Optional<Optional<String>> maybeMaybeProperty = properties.findFirst();
        assertTrue(maybeMaybeProperty.isPresent());
        assertEquals(MAYBE_PROP, maybeMaybeProperty.get());
    }

    @Test
    void composeStreamView() {
        NestedObj o = new NestedObj(
                null,
                Optional.empty(),
                Stream.of(
                        new InnerObj(PROP, MAYBE_PROP, Stream.of("val0", "val1")),
                        new InnerObj(PROP, MAYBE_PROP, Stream.of("val2", "val3"))));
        StreamView<NestedObj, String> composed = innerObjPropertyStreamView.compose(nestedObjInnerObjStreamView);
        Stream<String> properties = composed.getStream(o);
        assertEquals(4, properties.count());
    }
}