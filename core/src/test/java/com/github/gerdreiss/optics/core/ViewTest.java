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

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
public class ViewTest extends TestModel {

    @Test
    public void testApply() {
        var innerObj = new InnerObj(null, Optional.empty());

        assertNull(innerObjPropertyView.apply(innerObj));
        assertFalse(innerObjPropertyOptionalView.apply(innerObj).isPresent());

        innerObj = new InnerObj("property", Optional.of("maybeProperty"));

        assertEquals(innerObj.getProperty(), innerObjPropertyView.apply(innerObj));
        assertEquals(innerObj.getPropertyOptional(), innerObjPropertyOptionalView.apply(innerObj));
    }

    @Test
    public void testGet() {
        var innerObj = new InnerObj(null, Optional.empty());

        assertNull(innerObjPropertyView.get(innerObj));
        assertFalse(innerObjPropertyOptionalView.getOptional(innerObj).isPresent());

        innerObj = new InnerObj("property", Optional.of("maybeProperty"));

        assertEquals(innerObj.getProperty(), innerObjPropertyView.get(innerObj));
        assertEquals(
                innerObj.getPropertyOptional(), innerObjPropertyOptionalView.getOptional(innerObj));
    }

    @Test
    public void testAndThen() {

        var composedPropertyView =
                rootObjNestedObjView.andThen(nestedObjInnerObjView).andThen(innerObjPropertyView);

        var composedMaybePropertyView =
                rootObjNestedObjView
                        .andThen(nestedObjInnerObjView)
                        .andThen(innerObjPropertyOptionalView);

        var composedMaybeMaybePropertyView =
                rootObjNestedObjView
                        .andThen(nestedObjInnerObjOptionalView)
                        .andThen(innerObjPropertyOptionalView);

        var rootObj = new RootObj(null);

        assertNull(composedPropertyView.apply(rootObj));
        assertFalse(composedMaybePropertyView.getOptional(rootObj).isPresent());
        assertFalse(composedMaybeMaybePropertyView.getOptional(rootObj).isPresent());

        rootObj = new RootObj(new NestedObj(null));

        assertNull(composedPropertyView.apply(rootObj));
        assertFalse(composedMaybePropertyView.getOptional(rootObj).isPresent());
        assertFalse(composedMaybeMaybePropertyView.getOptional(rootObj).isPresent());

        rootObj = new RootObj(new NestedObj(new InnerObj(null)));

        assertNull(composedPropertyView.apply(rootObj));
        assertFalse(composedMaybePropertyView.getOptional(rootObj).isPresent());
        assertFalse(composedMaybeMaybePropertyView.getOptional(rootObj).isPresent());

        rootObj =
                new RootObj(
                        new NestedObj(
                                new InnerObj("property", Optional.of("maybeProperty")),
                                Optional.of(
                                        new InnerObj("property", Optional.of("maybeProperty")))));

        assertEquals(
                rootObj.getNestedObj().getInnerObj().getProperty(),
                composedPropertyView.get(rootObj));
        assertEquals(
                rootObj.getNestedObj().getInnerObj().getPropertyOptional(),
                composedMaybePropertyView.getOptional(rootObj));
        assertEquals(
                rootObj.getNestedObj().getInnerObj().getPropertyOptional(),
                composedMaybeMaybePropertyView.getOptional(rootObj));
    }

    @Test
    public void testCompose() {

        var composedPropertyView =
                innerObjPropertyView.compose(nestedObjInnerObjView).compose(rootObjNestedObjView);
        var composedMaybePropertyOptional =
                innerObjPropertyOptionalView
                        .compose(nestedObjInnerObjView)
                        .compose(rootObjNestedObjView);
        var composedMaybeMaybePropertyOptional =
                innerObjPropertyOptionalView
                        .compose(nestedObjInnerObjOptionalView)
                        .compose(rootObjNestedObjView);
        var composedPropertyOptional =
                innerObjPropertyView
                        .compose(nestedObjInnerObjOptionalView)
                        .compose(rootObjNestedObjView);

        var rootObj = new RootObj(null);

        assertNull(composedPropertyView.get(rootObj));
        assertFalse(composedPropertyOptional.getOptional(rootObj).isPresent());
        assertFalse(composedMaybePropertyOptional.getOptional(rootObj).isPresent());
        assertFalse(composedMaybeMaybePropertyOptional.getOptional(rootObj).isPresent());

        rootObj = new RootObj(new NestedObj(null));

        assertNull(composedPropertyView.get(rootObj));
        assertFalse(composedPropertyOptional.getOptional(rootObj).isPresent());
        assertFalse(composedMaybePropertyOptional.getOptional(rootObj).isPresent());
        assertFalse(composedMaybeMaybePropertyOptional.getOptional(rootObj).isPresent());

        rootObj = new RootObj(new NestedObj(new InnerObj(null, Optional.empty())));

        assertNull(composedPropertyView.get(rootObj));
        assertFalse(composedPropertyOptional.getOptional(rootObj).isPresent());
        assertFalse(composedMaybePropertyOptional.getOptional(rootObj).isPresent());
        assertFalse(composedMaybeMaybePropertyOptional.getOptional(rootObj).isPresent());

        rootObj =
                new RootObj(
                        new NestedObj(
                                new InnerObj("property", Optional.of("maybeProperty")),
                                Optional.of(
                                        new InnerObj("property", Optional.of("maybeProperty")))));

        assertEquals(
                rootObj.getNestedObj().getInnerObj().getProperty(),
                composedPropertyView.get(rootObj));
        assertEquals(
                rootObj.getNestedObj().getInnerObj().getProperty(),
                composedPropertyOptional.getOptional(rootObj).orElse(null));
        assertEquals(
                rootObj.getNestedObj().getInnerObj().getPropertyOptional(),
                composedMaybePropertyOptional.getOptional(rootObj));
        assertEquals(
                rootObj.getNestedObj().getInnerObj().getPropertyOptional(),
                composedMaybeMaybePropertyOptional.getOptional(rootObj));
    }
}
