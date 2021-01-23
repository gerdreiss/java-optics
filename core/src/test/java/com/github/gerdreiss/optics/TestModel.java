package com.github.gerdreiss.optics;

import java.util.Collections;
import java.util.List;
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
        private final List<InnerObj> innerObjList; // TODO create lenses for this property

        public NestedObj(InnerObj innerObj) {
            this(innerObj, Optional.empty(), Collections.emptyList());
        }

        public NestedObj(InnerObj innerObj, Optional<InnerObj> maybeInnerObj) {
            this(innerObj, maybeInnerObj, Collections.emptyList());
        }

        public <T> NestedObj(InnerObj innerObj, Optional<InnerObj> maybeInnerObj, List<InnerObj> innerObjList) {
            this.innerObj = innerObj;
            this.maybeInnerObj = maybeInnerObj;
            this.innerObjList = innerObjList;
        }

        public InnerObj getInnerObj() {
            return innerObj;
        }

        public Optional<InnerObj> getMaybeInnerObj() {
            return maybeInnerObj;
        }

        public List<InnerObj> getInnerObjList() {
            return innerObjList;
        }
    }

    static class InnerObj {
        private final String property;
        private final Optional<String> maybeProperty; // TODO create lenses for this property
        private final List<String> propertyList; // TODO create lenses for this property

        public InnerObj(String property) {
            this(property, Optional.empty(), Collections.emptyList());
        }

        public InnerObj(String property, Optional<String> maybeProperty) {
            this(property, maybeProperty, Collections.emptyList());
        }

        public <T> InnerObj(String property, Optional<String> maybeProperty, List<String> propertyList) {
            this.property = property;
            this.maybeProperty = maybeProperty;
            this.propertyList = propertyList;
        }

        public String getProperty() {
            return property;
        }

        public Optional<String> getMaybeProperty() {
            return maybeProperty;
        }

        public List<String> getPropertyList() {
            return propertyList;
        }
    }

}
