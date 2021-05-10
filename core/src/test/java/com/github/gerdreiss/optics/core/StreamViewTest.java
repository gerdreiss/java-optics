/*
 * Copyright 2021 DiffPlug
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.gerdreiss.optics.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

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
        var o =
                new NestedObj(
                        null,
                        Optional.empty(),
                        Stream.of(new InnerObj(PROP)),
                        Collections.emptyMap());
        var composed = nestedObjInnerObjStreamView.andThen(innerObjPropertyView);
        var properties = composed.getStream(o);
        assertEquals(Optional.of(PROP), properties.findFirst());
    }

    @Test
    void andThenOptionalView() {
        var o =
                new NestedObj(
                        null,
                        Optional.empty(),
                        Stream.of(new InnerObj(PROP, MAYBE_PROP)),
                        Collections.emptyMap());
        var composed = nestedObjInnerObjStreamView.andThen(innerObjPropertyOptionalView);
        var properties = composed.getStream(o);
        var maybeMaybeProperty = properties.findFirst();
        assertTrue(maybeMaybeProperty.isPresent());
        assertEquals(MAYBE_PROP, maybeMaybeProperty);
    }

    @Test
    void andThenStreamView() {
        var o =
                new NestedObj(
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
        var o =
                new NestedObj(
                        null,
                        Optional.empty(),
                        Stream.of(new InnerObj(PROP)),
                        Collections.emptyMap());
        var composed = innerObjPropertyView.compose(nestedObjInnerObjStreamView);
        var properties = composed.getStream(o);
        assertEquals(Optional.of(PROP), properties.findFirst());
    }

    @Test
    void composeOptionalView() {
        var o =
                new NestedObj(
                        null,
                        Optional.empty(),
                        Stream.of(new InnerObj(PROP, MAYBE_PROP)),
                        Collections.emptyMap());
        var composed = innerObjPropertyOptionalView.compose(nestedObjInnerObjStreamView);
        var properties = composed.getStream(o);
        var maybeMaybeProperty = properties.findFirst();
        assertTrue(maybeMaybeProperty.isPresent());
        assertEquals(MAYBE_PROP, maybeMaybeProperty);
    }

    @Test
    void composeStreamView() {
        var o =
                new NestedObj(
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
