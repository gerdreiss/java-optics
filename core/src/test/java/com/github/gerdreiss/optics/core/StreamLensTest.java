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

import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
class StreamLensTest extends TestModel {

    @Test
    void setStream() {
        var innerObj = new InnerObj(null);
        assertEquals(0, innerObj.getPropertyStream().count());
        var updated = innerObjPropertyStreamLens.set(innerObj, Stream.of(PROP));
        assertEquals(1, updated.getPropertyStream().count());
    }

    @Test
    void setValue() {
        var innerObj = new InnerObj(null);
        assertEquals(0, innerObj.getPropertyStream().count());
        var updated = innerObjPropertyStreamLens.set(innerObj, PROP);
        assertEquals(1, updated.getPropertyStream().count());
    }

    @Test
    void append() {
        var innerObj = new InnerObj(null, Optional.empty(), Stream.of("val1"));
        var updated = innerObjPropertyStreamLens.append(innerObj, "val2");
        assertEquals(2, updated.getPropertyStream().count());
    }

    @Test
    void prepend() {
        var innerObj = new InnerObj(null, Optional.empty(), Stream.of("val2"));
        var updated = innerObjPropertyStreamLens.prepend(innerObj, "val1");
        assertEquals(Optional.of("val1"), updated.getPropertyStream().findFirst());
    }

    @Test
    void modify() {
        var innerObj = new InnerObj(null, Optional.empty(), Stream.of("val"));
        var updated = innerObjPropertyStreamLens.modify(innerObj, String::toUpperCase);
        assertEquals(Optional.of("VAL"), updated.getPropertyStream().findFirst());
    }

    @Test
    void andThenLens() {
        var composed = nestedObjInnerObjStreamLens.andThen(innerObjPropertyLens);

        var nestedObj = new NestedObj(null, Optional.empty(), Stream.empty());
        var updated = composed.set(nestedObj, Stream.of("streamed1", "streamed2"));
        assertEquals(0, composed.getStream(updated).count());

        nestedObj = new NestedObj(null, Optional.empty(), Stream.of(new InnerObj(null)));
        updated = composed.set(nestedObj, Stream.of("streamed1", "streamed2"));
        assertEquals(
                Optional.of("streamed1"),
                updated.getInnerObjStream().map(InnerObj::getProperty).findFirst());
    }

    @Test
    void andThenOptional() {
        var composed = nestedObjInnerObjStreamLens.andThen(innerObjPropertyOptionalLens);

        var nestedObj = new NestedObj(null, Optional.empty(), Stream.empty());
        var updated =
                composed.set(
                        nestedObj, Stream.of(Optional.of("streamed1"), Optional.of("streamed2")));
        assertEquals(0, composed.getStream(updated).count());

        nestedObj = new NestedObj(null, Optional.empty(), Stream.of(new InnerObj(null)));
        updated =
                composed.set(
                        nestedObj, Stream.of(Optional.of("streamed1"), Optional.of("streamed2")));
        assertEquals(
                Optional.of(Optional.of("streamed1")),
                updated.getInnerObjStream().map(InnerObj::getPropertyOptional).findFirst());
    }

    //    @Test
    //    void andThenStream() {
    //        var composed = nestedObjInnerObjStreamLens.andThen(innerObjPropertyStreamLens);
    //
    //        var nestedObj = new NestedObj(null, Optional.empty(), Stream.empty());
    //        var updated = composed.set(nestedObj, Stream.of("streamed1", "streamed2"));
    //        // assertEquals(0, composed.getStream(updated).count());
    //
    //        var innerObj1 = new InnerObj(null, Optional.empty(), Stream.empty());
    //        var innerObj2 = new InnerObj(null, Optional.empty(), Stream.empty());
    //        nestedObj = new NestedObj(null, Optional.empty(), Stream.of(innerObj1, innerObj2));
    //        updated = composed.set(nestedObj, Stream.of("streamed1", "streamed2"));
    //        assertEquals(4,
    // updated.getInnerObjStream().flatMap(InnerObj::getPropertyStream).count());
    //    }

    @Test
    void composeLens() {
        // TODO
    }

    @Test
    void composeOptional() {
        // TODO
    }

    @Test
    void composeStream() {
        // TODO
    }
}
