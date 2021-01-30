package com.github.gerdreiss.optics;

import java.util.Optional;
import java.util.stream.Stream;

public abstract class TestModel {

    protected static final String PROP = "prop";
    protected static final String MAYBE_PROP = "maybeProp";


    /**
     * The root object that contains a nested obj, an optional nested object, and a stream of nested objects
     */
    static class RootObj {
        private final NestedObj nestedObj;
        private final Optional<NestedObj> maybeNestedObj;
        private final Stream<NestedObj> nestedObjStream;

        public RootObj(NestedObj nestedObj) {
            this(nestedObj, Optional.empty(), Stream.empty());
        }

        public RootObj(NestedObj nestedObj, Optional<NestedObj> maybeNestedObj) {
            this(nestedObj, maybeNestedObj, Stream.empty());
        }

        public RootObj(NestedObj nestedObj, Optional<NestedObj> maybeNestedObj, Stream<NestedObj> nestedObjStream) {
            this.nestedObj = nestedObj;
            this.maybeNestedObj = maybeNestedObj;
            this.nestedObjStream = nestedObjStream;
        }

        public NestedObj getNestedObj() {
            return nestedObj;
        }

        public Optional<NestedObj> getMaybeNestedObj() {
            return maybeNestedObj;
        }

        public Stream<NestedObj> getNestedObjStream() {
            return nestedObjStream;
        }
    }

    static class NestedObj {

        private final InnerObj innerObj;
        private final Optional<InnerObj> maybeInnerObj;
        private final Stream<InnerObj> innerObjStream;

        public NestedObj(InnerObj innerObj) {
            this(innerObj, Optional.empty(), Stream.empty());
        }

        public NestedObj(InnerObj innerObj, Optional<InnerObj> maybeInnerObj) {
            this(innerObj, maybeInnerObj, Stream.empty());
        }

        public <T> NestedObj(InnerObj innerObj, Optional<InnerObj> maybeInnerObj, Stream<InnerObj> innerObjStream) {
            this.innerObj = innerObj;
            this.maybeInnerObj = maybeInnerObj;
            this.innerObjStream = innerObjStream;
        }

        public InnerObj getInnerObj() {
            return innerObj;
        }

        public Optional<InnerObj> getMaybeInnerObj() {
            return maybeInnerObj;
        }

        public Stream<InnerObj> getInnerObjStream() {
            return innerObjStream;
        }
    }

    static class InnerObj {
        private final String property;
        private final Optional<String> maybeProperty;
        private final Stream<String> propertyStream;

        public InnerObj(String property) {
            this(property, Optional.empty(), Stream.empty());
        }

        public InnerObj(String property, Optional<String> maybeProperty) {
            this(property, maybeProperty, Stream.empty());
        }

        public <T> InnerObj(String property, Optional<String> maybeProperty, Stream<String> propertyStream) {
            this.property = property;
            this.maybeProperty = maybeProperty;
            this.propertyStream = propertyStream;
        }

        public String getProperty() {
            return property;
        }

        public Optional<String> getMaybeProperty() {
            return maybeProperty;
        }

        public Stream<String> getPropertyStream() {
            return propertyStream;
        }
    }

}
