package com.github.gerdreiss.optics;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ViewTest extends TestModel {

    private final View<RootObj, NestedObj> innerObjView = View.of(RootObj::getNestedObj);
    private final View<NestedObj, InnerObj> innerInnerObjView = View.of(NestedObj::getInnerObj);
    private final View<InnerObj, String> propertyView = View.of(InnerObj::getProperty);

    @Test
    public void testApply() {
        InnerObj o = new InnerObj(null);

        assertNull(propertyView.apply(o));

        o = new InnerObj("property1");

        assertEquals("property1", propertyView.apply(o));
    }

    @Test
    public void testGet() {
        InnerObj o = new InnerObj(null);

        assertNull(propertyView.get(o));

        o = new InnerObj("property1");

        assertEquals("property1", propertyView.get(o));
    }

    @Test
    public void testAndThen() throws Exception {

        View<RootObj, String> composed = innerObjView.andThen(innerInnerObjView).andThen(propertyView);

        RootObj o = new RootObj(null);

        assertNull(composed.apply(o));

        o = new RootObj(new NestedObj(null));

        assertNull(composed.apply(o));

        o = new RootObj(new NestedObj(new InnerObj(null)));

        assertNull(composed.apply(o));

        o = new RootObj(new NestedObj(new InnerObj("property1")));

        assertEquals("property1", composed.get(o));
    }

    @Test
    public void testCompose() throws Exception {

        View<RootObj, String> composed = propertyView.compose(innerInnerObjView).compose(innerObjView);

        RootObj o = new RootObj(null);

        assertNull(composed.get(o));

        o = new RootObj(new NestedObj(null));

        assertNull(composed.get(o));

        o = new RootObj(new NestedObj(new InnerObj(null)));

        assertNull(composed.get(o));

        o = new RootObj(new NestedObj(new InnerObj("property1")));

        assertEquals("property1", composed.get(o));
    }
}
