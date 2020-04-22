/*
 * Decompiled with CFR 0.139.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 */
package eu.blackfire62.MySkin.Shared.Util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;

public class Reflect {
    public static String serverVersion = null;

    public static Field getField(Class<?> clazz, String fname) throws Exception {
        Field f;
        try {
            f = clazz.getDeclaredField(fname);
        }
        catch (Exception e) {
            f = clazz.getField(fname);
        }
        Reflect.setFieldAccessible(f);
        return f;
    }

    public static void setFieldAccessible(Field f) throws Exception {
        f.setAccessible(true);
    }

    public static Object getObject(Object obj, String fname) throws Exception {
        return Reflect.getField(obj.getClass(), fname).get(obj);
    }

    public static Object getObject(Class<?> clazz, Object obj, String fname) throws Exception {
        return Reflect.getField(clazz, fname).get(obj);
    }

    public static void setObject(Object obj, String fname, Object value) throws Exception {
        Reflect.getField(obj.getClass(), fname).set(obj, value);
    }

    public static void setObject(Class<?> clazz, Object obj, String fname, Object value) throws Exception {
        Reflect.getField(clazz, fname).set(obj, value);
    }

    public static Method getMethod(Class<?> clazz, String mname) throws Exception {
        Method m = null;
        try {
            m = clazz.getDeclaredMethod(mname, new Class[0]);
        }
        catch (Exception e) {
            try {
                m = clazz.getMethod(mname, new Class[0]);
            }
            catch (Exception ex) {
                return m;
            }
        }
        m.setAccessible(true);
        return m;
    }

    public static /* varargs */ Method getMethod(Class<?> clazz, String mname, Class<?> ... args) throws Exception {
        Method m = null;
        try {
            m = clazz.getDeclaredMethod(mname, args);
        }
        catch (Exception e) {
            try {
                m = clazz.getMethod(mname, args);
            }
            catch (Exception ex) {
                return m;
            }
        }
        m.setAccessible(true);
        return m;
    }

    public static /* varargs */ Constructor<?> getConstructor(Class<?> clazz, Class<?> ... args) throws Exception {
        Constructor<?> c = clazz.getConstructor(args);
        c.setAccessible(true);
        return c;
    }

    public static Field getFirstFieldOf(Class<?> clazz, Object instance, Class<?> objclass) throws Exception {
        Field f = null;
        for (Field fi : clazz.getDeclaredFields()) {
            if (!fi.getType().equals(objclass)) continue;
            f = fi;
            break;
        }
        if (f == null) {
            for (Field fi : clazz.getFields()) {
                if (!fi.getType().equals(objclass)) continue;
                f = fi;
                break;
            }
        }
        Reflect.setFieldAccessible(f);
        return f;
    }

    public static void setFirstFieldOf(Class<?> clazz, Object instance, Class<?> objclass, Object toset) throws Exception {
        Field f = Reflect.getFirstFieldOf(clazz, instance, objclass);
        if (f != null) {
            f.set(instance, toset);
            return;
        }
        throw new Exception("setFirstFieldOf failed with field " + objclass);
    }

    public static Enum<?> getEnum(Class<?> clazz, String enumname, String constant) throws Exception {
        Enum[] econstants;
        Class<?> c = Class.forName(clazz.getName() + "$" + enumname);
        for (Enum e : econstants = (Enum[])c.getEnumConstants()) {
            if (!e.name().equalsIgnoreCase(constant)) continue;
            return e;
        }
        throw new Exception("Enum constant not found " + constant);
    }

    public static Enum<?> getEnum(Class<?> clazz, String constant) throws Exception {
        Enum[] econstants;
        Class<?> c = Class.forName(clazz.getName());
        for (Enum e : econstants = (Enum[])c.getEnumConstants()) {
            if (!e.name().equalsIgnoreCase(constant)) continue;
            return e;
        }
        throw new Exception("Enum constant not found " + constant);
    }

    public static Class<?> getNMSClass(String clazz) throws Exception {
        return Class.forName("net.minecraft.server." + serverVersion + "." + clazz);
    }

    public static Class<?> getBukkitClass(String clazz) throws Exception {
        return Class.forName("org.bukkit.craftbukkit." + serverVersion + "." + clazz);
    }

    public static /* varargs */ Object invokeMethod(Class<?> clazz, Object obj, String method, Class<?>[] args, Object ... initargs) throws Exception {
        return Reflect.getMethod(clazz, method, args).invoke(obj, initargs);
    }

    public static Object invokeMethod(Class<?> clazz, Object obj, String method) throws Exception {
        return Reflect.getMethod(clazz, method).invoke(obj, new Object());
    }

    public static /* varargs */ Object invokeMethod(Class<?> clazz, Object obj, String method, Object ... initargs) throws Exception {
        return Reflect.getMethod(clazz, method).invoke(obj, initargs);
    }

    public static Object invokeMethod(Object obj, String method) throws Exception {
        return Reflect.getMethod(obj.getClass(), method).invoke(obj, new Object());
    }

    public static Object invokeMethod(Object obj, String method, Object[] initargs) throws Exception {
        return Reflect.getMethod(obj.getClass(), method).invoke(obj, initargs);
    }

    public static /* varargs */ Object invokeConstructor(Class<?> clazz, Class<?>[] args, Object ... initargs) throws Exception {
        return Reflect.getConstructor(clazz, args).newInstance(initargs);
    }

    static {
        try {
            Class.forName("org.bukkit.Bukkit");
            serverVersion = Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf(46) + 1);
        }
        catch (Exception e) {
            try {
                Class.forName("net.md_5.bungee.BungeeCord");
                serverVersion = "BungeeCord";
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }
}

