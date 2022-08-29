package ru.maksym.plan.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
public @interface SaveKey {
    String[] key();
}
