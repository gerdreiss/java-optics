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
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
public class LensTest extends TestModel {

    @Test
    public void set() {
        var created = new InnerObj(null);
        var updated = innerObjPropertyLens.set(created, PROP);
        assertEquals(PROP, updated.getProperty());
    }

    @Test
    public void setPartial() {
        var created = new InnerObj(null);
        var updated = innerObjPropertyLens.set(created).apply(PROP);
        assertEquals(PROP, updated.getProperty());
    }

    @Test
    public void modify() {
        var created = new InnerObj(PROP);
        var modified = innerObjPropertyLens.modify(created, String::toUpperCase);
        assertEquals(PROP.toUpperCase(), modified.getProperty());
    }

    @Test
    public void andThen() {
        var composedPropertyLens =
                rootObjNestedObjLens.andThen(nestedObjInnerObjLens).andThen(innerObjPropertyLens);

        var o = new RootObj(null);

        var updated = composedPropertyLens.set(o, PROP);
        assertNull(composedPropertyLens.get(updated));

        o = new RootObj(new NestedObj(null));

        updated = composedPropertyLens.set(o, PROP);
        assertNull(composedPropertyLens.get(updated));

        o = new RootObj(new NestedObj(new InnerObj(null)));

        updated = composedPropertyLens.set(o, PROP);
        assertEquals(PROP, composedPropertyLens.get(updated));

        o = new RootObj(new NestedObj(new InnerObj(PROP, Optional.empty(), Stream.of(PROP))));

        updated = composedPropertyLens.set(o, "newProperty");
        assertEquals("newProperty", composedPropertyLens.get(updated));

        updated = composedPropertyLens.modify(updated, String::toUpperCase);
        assertEquals("NEWPROPERTY", composedPropertyLens.get(updated));
    }

    @Test
    public void compose() {
        var composedPropertyLens =
                innerObjPropertyLens.compose(nestedObjInnerObjLens).compose(rootObjNestedObjLens);

        var o = new RootObj(null);

        var updated = composedPropertyLens.set(o, PROP);
        assertNull(composedPropertyLens.get(updated));

        o = new RootObj(new NestedObj(null));

        updated = composedPropertyLens.set(o, PROP);
        assertNull(composedPropertyLens.get(updated));

        o = new RootObj(new NestedObj(new InnerObj(null)));

        updated = composedPropertyLens.set(o, PROP);
        assertEquals(PROP, composedPropertyLens.get(updated));

        o = new RootObj(new NestedObj(new InnerObj(PROP, Optional.empty(), Stream.of(PROP))));

        updated = composedPropertyLens.set(o, "newProperty");
        assertEquals("newProperty", composedPropertyLens.get(updated));

        updated = composedPropertyLens.modify(updated, String::toUpperCase);
        assertEquals("NEWPROPERTY", composedPropertyLens.get(updated));
    }
}
