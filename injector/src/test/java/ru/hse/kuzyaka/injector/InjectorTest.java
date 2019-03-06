package ru.hse.kuzyaka.injector;

import org.junit.jupiter.api.Test;
import ru.hse.kuzyaka.injector.exceptions.*;
import ru.hse.kuzyaka.injector.testClasses.*;


import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class InjectorTest {

    @Test
    public void injectorShouldInitializeClassWithoutDependencies()
            throws Exception {
        Object object = Injector.initialize("ru.hse.kuzyaka.injector.testClasses.ClassWithoutDependencies", Collections.emptyList());
        assertTrue(object instanceof ClassWithoutDependencies);
    }

    @Test
    public void injectorShouldInitializeClassWithOneClassDependency()
            throws Exception {
        Object object = Injector.initialize(
                "ru.hse.kuzyaka.injector.testClasses.ClassWithOneClassDependency",
                Collections.singletonList(Class.forName("ru.hse.kuzyaka.injector.testClasses.ClassWithoutDependencies")));
        assertTrue(object instanceof ClassWithOneClassDependency);
        ClassWithOneClassDependency instance = (ClassWithOneClassDependency) object;
        assertTrue(instance.dependency != null);
    }

    @Test
    public void injectorShouldInitializeClassWithOneInterfaceDependency()
            throws Exception {
        Object object = Injector.initialize(
                "ru.hse.kuzyaka.injector.testClasses.ClassWithOneInterfaceDependency",
                Collections.singletonList(Class.forName("ru.hse.kuzyaka.injector.testClasses.InterfaceImpl"))
        );
        assertTrue(object instanceof ClassWithOneInterfaceDependency);
        ClassWithOneInterfaceDependency instance = (ClassWithOneInterfaceDependency) object;
        assertTrue(instance.dependency instanceof InterfaceImpl);
    }

    @Test
    public void injectorAmbiguousDependencies() {
        assertThrows(AmbiguousImplementationException.class,
                () -> Injector.initialize("ru.hse.kuzyaka.injector.testClasses.ClassWithOneClassDependency",
                List.of(ClassWithoutDependencies.class, ClassExtendsDependency.class)));
    }

    @Test
    public void injectorNoImplementation() {
        assertThrows(ImplementationNotFoundException.class,
                () -> Injector.initialize("ru.hse.kuzyaka.injector.testClasses.ClassWithOneClassDependency",
                        Collections.emptyList()));
    }

    @Test
    public void injectorCyclicDependency() {
        assertThrows(InjectionCycleException.class,
                () -> Injector.initialize("ru.hse.kuzyaka.injector.testClasses.A",
                List.of(A.class, B.class, C.class)));
    }

    @Test
    public void injectorImplements() throws Exception {
        Object object = Injector.initialize("ru.hse.kuzyaka.injector.testClasses.ClassWithABDependency",
                List.of(ClassImplementsAB.class));
        assertTrue(object instanceof ClassWithABDependency);
    }
}