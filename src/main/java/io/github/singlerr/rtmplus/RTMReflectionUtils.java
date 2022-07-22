package io.github.singlerr.rtmplus;

import java.lang.reflect.Field;

public final class RTMReflectionUtils {
    public static void setData(Class<?> cls, Object obj, String varName, Object varData) throws NoSuchFieldException, IllegalAccessException {
        Field field = cls.getDeclaredField(varName);
        field.setAccessible(true);
        field.set(obj, varData);
    }
}
