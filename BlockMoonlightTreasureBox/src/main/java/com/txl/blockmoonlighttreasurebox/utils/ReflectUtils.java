package com.txl.blockmoonlighttreasurebox.utils;

import java.lang.reflect.Field;

/**
 * 反射工具类
 * 提供反射操作的通用方法
 */
public class ReflectUtils {
    /**
     * 通过反射获取long类型的字段值
     * @param o 对象实例
     * @param c 类的Class对象
     * @param fileName 字段名
     * @param defaultValue 默认值
     * @return 字段的long值，失败时返回默认值
     */
    public static long reflectLongField(Object o,Class c, String fileName,long defaultValue){
        try {
            Field file = c.getDeclaredField(fileName);
            file.setAccessible(true);
            return file.getLong(o);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    /**
     * 通过反射获取对象的字段值
     * @param o 对象实例
     * @param c 类的Class对象
     * @param fieldName 字段名
     * @return 字段的对象，失败时返回null
     */
    public static Object reflectFiled(Object o,Class c,String fieldName){
        try {
            Field file = c.getDeclaredField(fieldName);
            file.setAccessible(true);
            return file.get(o);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
