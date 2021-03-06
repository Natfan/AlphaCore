/*
 *  Created by Filip P. on 2/13/15 11:35 PM.
 */

package me.pauzen.alphacore.utils.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/*
 * Written by FilipDev on 12/24/14 12:19 AM.
 */

public class Reflection<T> {

    private T        object;
    private Class<T> clazz;

    /**
     * Initializes Reflection object giving it an object value.
     *
     * @param object Object value to give to the Reflection object.
     */
    public Reflection(T object) {
        this.object = object;
        this.clazz = (Class<T>) object.getClass();
    }

    /**
     * Initialize without specifying the object to modify. Must use setObject to have any function other than interact with static members.
     *
     * @param clazz The class to give to the Reflection object.
     */
    public Reflection(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * Gets the value of the Reflection object.
     *
     * @return The value of the Reflection object.
     */
    public T getObject() {
        return object;
    }

    /**
     * So that a single Reflection object can be used for multiple objects of the same type.
     *
     * @param object New object value to give to the Reflection object.
     */
    public void setObject(T object) {
        this.object = object;
    }

    /**
     * Returns Class value.
     *
     * @return Class value.
     */
    public Class<T> getClassValue() {
        return clazz;
    }

    /**
     * Gets the value of the field specified by the name.
     *
     * @param name Name of the field to get value of.
     * @return The retrieved field value. Null if field not found or if value is null.
     */
    public Object getValue(String name) {
        check();
        try {
            return ReflectionFactory.getField(clazz, name).get(object);
        } catch (IllegalAccessException ignored) {
        }
        return null;
    }

    /**
     * Sets the value of a field specified by the name.
     *
     * @param name   Name of the field to set value of.
     * @param object The new value of the field.
     */
    public void setValue(String name, Object object) {
        check();
        Field field = ReflectionFactory.getField(clazz, name);
        if (Modifier.isFinal(field.getModifiers())) ReflectionFactory.removeFinal(field);
        try {
            field.set(this.object, object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setValue(Field field, Object object) {
        if (Modifier.isFinal(field.getModifiers())) ReflectionFactory.removeFinal(field);
        try {
            field.setAccessible(true);
            field.set(this.object, object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets static field value specified by the name.
     *
     * @param name Name of the field to get value of.
     * @return The value of the field.
     */
    public Object getStaticValue(String name) {
        try {
            return ReflectionFactory.getField(clazz, name).get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets static field value specified by the name.
     *
     * @param name   Name of the field to set value of.
     * @param object The new value of the field.
     */
    public void setStaticValue(String name, Object object) {
        Field field = ReflectionFactory.getField(clazz, name);
        if (Modifier.isFinal(field.getModifiers())) ReflectionFactory.removeFinal(field);
        try {
            field.set(null, object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> getValues() {
        check();
        try {
            Map<String, Object> values = new HashMap<>();
            for (Field field : ReflectionFactory.getFields(clazz))
                values.put(field.getName(), field.get(this.getObject()));
            return values;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Calls a method given its name, the parameters it takes in, and the Objects to use as the argument.
     *
     * @param name       Name of the method to call.
     * @param paramTypes The classes of the parameters it accepts.
     * @param args       The arguments to call the method with.
     * @return The value returned from the method. Null if method return type is void.
     */
    public Object callMethod(String name, Class[] paramTypes, Object[] args) {
        check();
        Method method = ReflectionFactory.getMethod(clazz, name, paramTypes);
        if (method != null) {
            try {
                return method.invoke(getObject(), args);
            } catch (IllegalAccessException | InvocationTargetException ignored) {
            }
        }
        return null;
    }

    /**
     * Calls a method given its name and the Objects to use as argument. Uses Objects' class values to determine which parameters types to use.
     *
     * @param name Name of the method to call.
     * @param args The arguments to call the method with, also used to get the parameter types of.
     * @return The value returned from the method. Null if method return type is void.
     */
    public Object callMethod(String name, Object... args) {
        return callMethod(name, ReflectionFactory.toClassArray(args), args);
    }

    private void check() {
        if (this.object == null) throw new NullPointerException("Object value is null. Use setObject() first.");
    }
}
