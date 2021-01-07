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
        private final Optional<InnerObj> maybeInnerObj; // TODO create lenses for this property

        public NestedObj(InnerObj innerObj) {
            this(innerObj, Optional.empty());
        }

        public NestedObj(InnerObj innerObj, Optional<InnerObj> maybeInnerObj) {
            this.innerObj = innerObj;
            this.maybeInnerObj = maybeInnerObj;
        }

        public InnerObj getInnerObj() {
            return innerObj;
        }

        public Optional<InnerObj> getMaybeInnerObj() {
            return maybeInnerObj;
        }
    }

    static class InnerObj {
        private final String property;
        private final Optional<String> maybeProperty; // TODO create lenses for this property

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
