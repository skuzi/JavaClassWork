package ru.hse.kuzyaka.injector;

import java.util.HashMap;
import java.util.List;
import ru.hse.kuzyaka.injector.exceptions.*;
import java.lang.reflect.*;

public class Injector {

    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     * @param rootClassName name of class to initialize
     * @param implementationClassNames list of possible implementations of all possible dependencies
     * @return object of class `rootClassName` initialized using given implementation of its dependencies
     * @throws InjectionCycleException if dependencies somehow form a cycle
     * @throws AmbiguousImplementationException if there are several implementation of dependency interface or dependency inheritors
     * @throws ImplementationNotFoundException if there is no implementation or inheritor of any dependency
     */
    public static Object initialize(String rootClassName, List<Class<?>> implementationClassNames) throws Exception {
        Class clazz = Class.forName(rootClassName);
        if(visited.get(clazz) == null || !visited.get(clazz)) {
            visited.put(clazz, true);
        } else {
            throw new InjectionCycleException();
        }
        Constructor<?> constructor = clazz.getConstructors()[0];
        int argCount = constructor.getParameterCount();
        int lastArg = 0;
        Object[] args = new Object[argCount];

        for (var constructorClass : constructor.getParameterTypes()) {
            if(constructorClass.isInterface()) {
                args[lastArg++] = getImplementation(constructorClass, implementationClassNames);
            } else {
                args[lastArg++] = getInheritor(constructorClass, implementationClassNames);
            }
        }
        return constructor.newInstance(args);
    }

    private static Object getInheritor(Class<?> clazzToInstance, List<Class<?>> classes) throws Exception {
        int flag = 0;
        String className = "";
        for(var clazz : classes) {
            if (clazz.getSuperclass().equals(clazzToInstance) || clazz.equals(clazzToInstance)) {
                flag++;
                className = clazz.getName();
            }
        }
        throwByFlag(flag);
        return initialize(className, classes);
    }

    private static Object getImplementation(Class<?> clazzToInstance, List<Class<?>> classes) throws Exception {
        int flag = 0;
        String className = "";
        for (var implementation : classes) {
            for(var interfaceImpl : implementation.getInterfaces()) {
                if(clazzToInstance.getName().equals(interfaceImpl.getName())) {
                    flag++;
                    className = implementation.getName();
                }
            }
        }
        throwByFlag(flag);
        return initialize(className, classes);
    }

    private static void throwByFlag(int flag) throws AmbiguousImplementationException, ImplementationNotFoundException {
        if(flag == 0) {
            throw new ImplementationNotFoundException();
        } else if (flag > 1) {
            throw new AmbiguousImplementationException();
        }

    }

    private static HashMap<Class<?>, Boolean> visited = new HashMap<>();
}