package com.github.gerdreiss.optics;

public abstract class TestModel {

    static class RootObj {
        private final NestedObj nestedObj;

        public RootObj(NestedObj nestedObj) {
            this.nestedObj = nestedObj;
        }

        public NestedObj getNestedObj() {
            return nestedObj;
        }
    }

    static class NestedObj {

        private final InnerObj innerObj;

        public NestedObj(InnerObj innerObj) {
            this.innerObj = innerObj;
        }

        public InnerObj getInnerObj() {
            return innerObj;
        }
    }

    static class InnerObj {
        private final String property;

        public InnerObj(String property) {
            this.property = property;
        }

        public String getProperty() {
            return property;
        }
    }

}
