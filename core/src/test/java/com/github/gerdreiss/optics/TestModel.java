package com.github.gerdreiss.optics;

import java.util.Optional;

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
        private final Optional<String> maybeProperty;

        public InnerObj(String property) {
            this(property, Optional.empty());
        }

        public InnerObj(String property, Optional<String> maybeProperty) {
            this.property = property;
            this.maybeProperty = maybeProperty;
        }

        public String getProperty() {
            return property;
        }

        public Optional<String> getMaybeProperty() {
            return maybeProperty;
        }
    }

}
