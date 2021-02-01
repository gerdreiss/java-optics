package com.github.gerdreiss.optics.core;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
class StreamLensTest extends TestModel {

    private final Lens<RootObj, NestedObj> nestedObjLens = Lens.of(
            RootObj::getNestedObj,
            (rootObj, innerObj) -> new RootObj(innerObj, rootObj.getMaybeNestedObj(), rootObj.getNestedObjStream()));

    private final Lens<NestedObj, InnerObj> innerObjLens = Lens.of(
            NestedObj::getInnerObj,
            (nestedObj, innerInnerObj) -> new NestedObj(innerInnerObj, nestedObj.getMaybeInnerObj(), nestedObj.getInnerObjStream()));

    private final StreamLens<InnerObj, String> propertyStreamLens = StreamLens.of(
            InnerObj::getPropertyStream,
            (innerObj, propertyStream) -> new InnerObj(innerObj.getProperty(), innerObj.getMaybeProperty(), propertyStream));


    @Test
    void set() {
        // TODO
    }

    @Test
    void testSet() {
        // TODO
    }

    @Test
    void append() {
        // TODO
    }

    @Test
    void prepend() {
        // TODO
    }

    @Test
    void modify() {
        // TODO
    }

    @Test
    void andThenLens() {
        // TODO
    }

    @Test
    void andThenOptional() {
        // TODO
    }

    @Test
    void andThenStream() {
        // TODO
    }

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