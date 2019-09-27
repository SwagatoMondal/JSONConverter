package com.swag.jsonconverter.sample;

import androidx.annotation.Nullable;

import com.swag.jsonconverter.Ignore;

public class InnerEntity1 {
    public Entity.InnerStaticEntity innerStaticEntity;
    private String string = "Inner1 string";
    InnerEntity2 innerEntity2 = new InnerEntity2();
    @Ignore
    private String extra = "InnerEntity1 : Extra string to be ignored";

    public InnerEntity1() {}

    InnerEntity1(Entity.InnerStaticEntity innerStaticEntity) {
        this.innerStaticEntity = innerStaticEntity;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof InnerEntity1) {
            InnerEntity1 newObj = (InnerEntity1) obj;
            return innerStaticEntity.equals(newObj.innerStaticEntity) && string.equals(newObj.string)
                    && innerEntity2.equals(newObj.innerEntity2);
        } else {
            return false;
        }
    }

    public class InnerEntity2 {
        private String string = "Inner2 string";
        protected InnerEntity3 innerEntity3 = new InnerEntity3();

        public InnerEntity2() {}

        public class InnerEntity3 {
            private String string = "Inner3 string";

            public InnerEntity3() {}

            @Override
            public boolean equals(@Nullable Object obj) {
                if (obj instanceof InnerEntity3) {
                    InnerEntity3 entity3 = (InnerEntity3) obj;
                    return string.equals(entity3.string);
                } else {
                    return false;
                }
            }
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof InnerEntity2) {
                InnerEntity2 entity2 = (InnerEntity2) obj;
                return string.equals(entity2.string) && innerEntity3.equals(entity2.innerEntity3);
            } else {
                return false;
            }
        }
    }
}
