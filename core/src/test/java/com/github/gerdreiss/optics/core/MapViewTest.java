package com.github.gerdreiss.optics.core;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitPlatform.class)
class MapViewTest extends TestModel {

    @Test
    void apply() {
        var o = new InnerObj(null);

        assertTrue(innerObjPropertyMapView.apply(o).isEmpty());

        o = new InnerObj(null, Optional.empty(), Stream.empty(), Collections.singletonMap("key", "property"));

        var map = innerObjPropertyMapView.apply(o);
        assertFalse(map.isEmpty());
        assertTrue(map.containsKey("key"));
        assertEquals("property", map.get("key"));
    }

    @Test
    void getMap() {
        var o = new InnerObj(null);

        assertTrue(innerObjPropertyMapView.getMap(o).isEmpty());

        o = new InnerObj(null, Optional.empty(), Stream.empty(), Collections.singletonMap("key", "property"));

        var map = innerObjPropertyMapView.getMap(o);
        assertFalse(map.isEmpty());
        assertTrue(map.containsKey("key"));
        assertEquals("property", map.get("key"));
    }

    @Test
    void andThen() {
        var rootObjStringInnerObjMapView = rootObjNestedObjMapView.andThen(nestedObjInnerObjView);
        var rootObjStringCollectionMapView = rootObjNestedObjMapView.andThen(nestedObjInnerObjMapView);

        var o = new RootObj(null);

        assertTrue(rootObjStringInnerObjMapView.getMap(o).isEmpty());
        assertTrue(rootObjStringCollectionMapView.getMap(o).isEmpty());

        o = new RootObj(
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
        var rootObjStringCollectionMapView = nestedObjInnerObjMapView.compose(rootObjNestedObjMapView);

        var o = new RootObj(null);

        assertTrue(rootObjStringInnerObjMapView.getMap(o).isEmpty());
        assertTrue(rootObjStringCollectionMapView.getMap(o).isEmpty());

        o = new RootObj(
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