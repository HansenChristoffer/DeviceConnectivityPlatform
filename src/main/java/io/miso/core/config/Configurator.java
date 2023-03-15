package io.miso.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

public class Configurator {
    private static final String CONFIG_FILE_PATH = "src/main/resources/dop.properties";

    private Configurator() {
    }

    public static synchronized <T> T getConfig(final Class<T> configClass) throws IOException, IllegalAccessException,
            InstantiationException, InvocationTargetException, NoSuchMethodException {
        final T config = configClass.getDeclaredConstructor().newInstance();
        final Properties properties = new Properties();
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final InputStream inputStream = classLoader.getResourceAsStream(CONFIG_FILE_PATH);

        if (inputStream == null) {
            throw new NullPointerException(String.format("Could not establish stream for [%s] config...", CONFIG_FILE_PATH));
        }

        try (final InputStreamReader reader = new InputStreamReader(inputStream)) {
            properties.load(reader);
            final String sectionName = configClass.getName();

            for (final Method method : configClass.getDeclaredMethods()) {
                if (method.getParameterCount() == 0) {
                    final String propertyName = sectionName + "." + method.getName();
                    final String propertyValue;

                    synchronized (properties) {
                        propertyValue = properties.getProperty(propertyName);
                    }

                    if (propertyValue != null) {
                        final Class<?> returnType = method.getReturnType();
                        final Object convertedValue = convertValue(propertyValue, returnType);
                        method.invoke(config, convertedValue);
                    } else {
                        final Method getterMethod = findGetterMethod(configClass, method.getName());

                        if (getterMethod != null) {
                            final DefaultValue defaultValueAnnotation = getterMethod.getAnnotation(DefaultValue.class);

                            if (defaultValueAnnotation != null) {
                                final String defaultValue = defaultValueAnnotation.value();
                                final Class<?> returnType = method.getReturnType();
                                final Object convertedValue = convertValue(defaultValue, returnType);
                                method.invoke(config, convertedValue);
                            }
                        }
                    }
                }
            }

            return config;
        }
    }

    private static <T> Method findGetterMethod(final Class<T> configClass, final String methodName) {
        for (final Method method : configClass.getDeclaredMethods()) {
            if (method.getName().equals(methodName) && method.getParameterCount() == 0) {
                final Annotation[] annotations = method.getDeclaredAnnotations();
                for (final Annotation annotation : annotations) {
                    if (annotation instanceof DefaultValue) {
                        return method;
                    }
                }
            }
        }
        return null;
    }

    private static Object convertValue(final String value, final Class<?> returnType) {
        if (returnType == String.class) {
            return value;
        } else if (returnType == int.class || returnType == Integer.class) {
            return Integer.parseInt(value);
        } else if (returnType == double.class || returnType == Double.class) {
            return Double.parseDouble(value);
        } else if (returnType == boolean.class || returnType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else {
            throw new IllegalArgumentException("Unsupported return type: " + returnType);
        }
    }
}
