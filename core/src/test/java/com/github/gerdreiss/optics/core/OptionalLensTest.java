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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
public class OptionalLensTest extends TestModel {

    @Test
    public void set() {
        var created = new InnerObj(PROP);
        var updated = innerObjPropertyOptionalLens.set(created, MAYBE_PROP);

        assertTrue(updated.getPropertyOptional().isPresent());
        assertEquals(MAYBE_PROP, updated.getPropertyOptional());
    }

    @Test
    public void setPartial() {
        var created = new InnerObj(null);
        var updated = innerObjPropertyOptionalLens.set(created).apply(MAYBE_PROP);

        assertTrue(updated.getPropertyOptional().isPresent());
        assertEquals(MAYBE_PROP, updated.getPropertyOptional());
    }

    @Test
    public void modify() {
        var created = new InnerObj(PROP, MAYBE_PROP);
        var updated = innerObjPropertyOptionalLens.modify(created, String::toUpperCase);

        assertTrue(updated.getPropertyOptional().isPresent());
        assertEquals(MAYBE_PROP.map(String::toUpperCase), updated.getPropertyOptional());
    }

    @Test
    public void andThen() {
        var composed =
                rootObjNestedObjOptionalLens
                        .andThen(nestedObjInnerObjOptionalLens)
                        .andThen(innerObjPropertyOptionalLens);

        var o = new RootObj(null);

        var updated = composed.set(o, MAYBE_PROP);
        assertFalse(composed.getOptional(updated).isPresent());

        o = new RootObj(new NestedObj(null));

        updated = composed.set(o, MAYBE_PROP);
        assertFalse(composed.getOptional(updated).isPresent());

        o = new RootObj(new NestedObj(new InnerObj(PROP)));

        updated = composed.set(o, MAYBE_PROP);
        assertFalse(composed.getOptional(updated).isPresent());

        o = new RootObj(null, Optional.of(new NestedObj(null, Optional.of(new InnerObj(PROP)))));

        updated = composed.set(o, Optional.of("newProperty"));
        assertEquals("newProperty", composed.getOptional(updated).get());

        updated = composed.modify(updated, String::toUpperCase);
        assertEquals("NEWPROPERTY", composed.getOptional(updated).get());
    }

    @Test
    public void compose() {
        var composed =
                innerObjPropertyOptionalLens
                        .compose(nestedObjInnerObjOptionalLens)
                        .compose(rootObjNestedObjOptionalLens);

        var o = new RootObj(null);

        var updated = composed.set(o, MAYBE_PROP);
        assertNull(composed.getOptional(updated).orElse(null));

        o = new RootObj(new NestedObj(null));

        updated = composed.set(o, MAYBE_PROP);
        assertNull(composed.getOptional(updated).orElse(null));

        o = new RootObj(null, Optional.of(new NestedObj(null, Optional.of(new InnerObj(PROP)))));

        updated = composed.set(o, MAYBE_PROP);
        assertEquals(MAYBE_PROP, composed.getOptional(updated));

        updated = composed.set(o, "newProperty");
        assertEquals("newProperty", composed.getOptional(updated).orElse(null));
    }
}
