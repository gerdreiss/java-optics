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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
public class OptionalViewTest extends TestModel {

    @Test
    public void testApply() {
        var o = new InnerObj(PROP);

        assertFalse(innerObjPropertyOptionalView.apply(o).isPresent());

        o = new InnerObj(PROP, MAYBE_PROP);

        assertTrue(innerObjPropertyOptionalView.apply(o).isPresent());
        assertEquals(MAYBE_PROP, innerObjPropertyOptionalView.apply(o));
    }

    @Test
    public void testGetOptional() {
        var o = new InnerObj(PROP);

        assertFalse(innerObjPropertyOptionalView.getOptional(o).isPresent());

        o = new InnerObj(PROP, MAYBE_PROP);

        assertTrue(innerObjPropertyOptionalView.getOptional(o).isPresent());
        assertEquals(MAYBE_PROP, innerObjPropertyOptionalView.getOptional(o));
    }

    @Test
    public void testAndThen() {
        var composedPropertyOptional =
                rootObjNestedObjOptionalView
                        .andThen(nestedObjInnerObjOptionalView)
                        .andThen(innerObjPropertyOptionalView);

        var composedPropertyView =
                rootObjNestedObjOptionalView
                        .andThen(nestedObjInnerObjOptionalView)
                        .andThen(innerObjPropertyView);

        var o = new RootObj(null);

        assertFalse(composedPropertyOptional.getOptional(o).isPresent());
        assertFalse(composedPropertyView.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(null));

        assertFalse(composedPropertyOptional.getOptional(o).isPresent());
        assertFalse(composedPropertyView.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(new InnerObj(PROP)));

        assertFalse(composedPropertyOptional.getOptional(o).isPresent());
        assertFalse(composedPropertyView.getOptional(o).isPresent());

        o =
                new RootObj(
                        null,
                        Optional.of(
                                new NestedObj(null, Optional.of(new InnerObj(PROP, MAYBE_PROP)))));

        assertEquals(MAYBE_PROP, composedPropertyOptional.getOptional(o));
        assertEquals(PROP, composedPropertyView.getOptional(o).orElse(null));
    }

    @Test
    public void testCompose() {
        var composedPropertyView =
                innerObjPropertyView
                        .compose(nestedObjInnerObjOptionalView)
                        .compose(rootObjNestedObjOptionalView);
        var composedPropertyOptional =
                innerObjPropertyOptionalView
                        .compose(nestedObjInnerObjOptionalView)
                        .compose(rootObjNestedObjOptionalView);

        var o = new RootObj(null);

        assertFalse(composedPropertyView.getOptional(o).isPresent());
        assertFalse(composedPropertyOptional.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(null));

        assertFalse(composedPropertyView.getOptional(o).isPresent());
        assertFalse(composedPropertyOptional.getOptional(o).isPresent());

        o = new RootObj(new NestedObj(new InnerObj(PROP)));

        assertFalse(composedPropertyView.getOptional(o).isPresent());
        assertFalse(composedPropertyOptional.getOptional(o).isPresent());

        o =
                new RootObj(
                        null,
                        Optional.of(
                                new NestedObj(null, Optional.of(new InnerObj(PROP, MAYBE_PROP)))));

        assertEquals(PROP, composedPropertyView.getOptional(o).get());
        assertEquals(MAYBE_PROP, composedPropertyOptional.getOptional(o));
    }
}
