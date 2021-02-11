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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
class MapViewTest extends TestModel {

    @Test
    void apply() {
        var o = new InnerObj(null);

        assertTrue(innerObjPropertyMapView.apply(o).isEmpty());

        o =
                new InnerObj(
                        null,
                        Optional.empty(),
                        Stream.empty(),
                        Collections.singletonMap("key", "property"));

        var map = innerObjPropertyMapView.apply(o);
        assertFalse(map.isEmpty());
        assertTrue(map.containsKey("key"));
        assertEquals("property", map.get("key"));
    }

    @Test
    void getMap() {
        var o = new InnerObj(null);

        assertTrue(innerObjPropertyMapView.getMap(o).isEmpty());

        o =
                new InnerObj(
                        null,
                        Optional.empty(),
                        Stream.empty(),
                        Collections.singletonMap("key", "property"));

        var map = innerObjPropertyMapView.getMap(o);
        assertFalse(map.isEmpty());
        assertTrue(map.containsKey("key"));
        assertEquals("property", map.get("key"));
    }

    @Test
    void andThen() {
        var rootObjStringInnerObjMapView = rootObjNestedObjMapView.andThen(nestedObjInnerObjView);
        var rootObjStringCollectionMapView =
                rootObjNestedObjMapView.andThen(nestedObjInnerObjMapView);

        var o = new RootObj(null);

        assertTrue(rootObjStringInnerObjMapView.getMap(o).isEmpty());
        assertTrue(rootObjStringCollectionMapView.getMap(o).isEmpty());

        o =
                new RootObj(
                        null,
                        Optional.empty(),
                        Stream.empty(),
                        Collections.singletonMap(
                                "NestedObj",
                                new NestedObj(
                                        new InnerObj(null),
                                        Optional.empty(),
                                        Stream.empty(),
                                        Collections.singletonMap("InnerObj", new InnerObj(PROP)))));

        assertFalse(rootObjStringInnerObjMapView.getMap(o).isEmpty());
        assertTrue(rootObjStringInnerObjMapView.getMap(o).containsKey("NestedObj"));
        assertNotNull(rootObjStringInnerObjMapView.getMap(o).get("NestedObj"));

        assertFalse(rootObjStringCollectionMapView.getMap(o).isEmpty());
        assertTrue(rootObjStringCollectionMapView.getMap(o).containsKey("NestedObj"));
        assertNotNull(rootObjStringCollectionMapView.getMap(o).get("NestedObj"));
        assertEquals(1, rootObjStringCollectionMapView.getMap(o).get("NestedObj").size());
    }

    @Test
    void compose() {
        var rootObjStringInnerObjMapView = nestedObjInnerObjView.compose(rootObjNestedObjMapView);
        var rootObjStringCollectionMapView =
                nestedObjInnerObjMapView.compose(rootObjNestedObjMapView);

        var o = new RootObj(null);

        assertTrue(rootObjStringInnerObjMapView.getMap(o).isEmpty());
        assertTrue(rootObjStringCollectionMapView.getMap(o).isEmpty());

        o =
                new RootObj(
                        null,
                        Optional.empty(),
                        Stream.empty(),
                        Collections.singletonMap(
                                "NestedObj",
                                new NestedObj(
                                        new InnerObj(null),
                                        Optional.empty(),
                                        Stream.empty(),
                                        Collections.singletonMap("InnerObj", new InnerObj(PROP)))));

        assertFalse(rootObjStringInnerObjMapView.getMap(o).isEmpty());
        assertTrue(rootObjStringInnerObjMapView.getMap(o).containsKey("NestedObj"));
        assertNotNull(rootObjStringInnerObjMapView.getMap(o).get("NestedObj"));

        assertFalse(rootObjStringCollectionMapView.getMap(o).isEmpty());
        assertTrue(rootObjStringCollectionMapView.getMap(o).containsKey("NestedObj"));
        assertNotNull(rootObjStringCollectionMapView.getMap(o).get("NestedObj"));
        assertEquals(1, rootObjStringCollectionMapView.getMap(o).get("NestedObj").size());
    }
}
