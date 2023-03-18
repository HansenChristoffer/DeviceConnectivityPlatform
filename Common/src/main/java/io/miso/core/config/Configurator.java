package io.miso.core.config;

import io.miso.exception.ConfigPropertyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Proxy;
import java.util.Properties;

public class Configurator {
    private static final Logger logger = LogManager.getFormatterLogger();

    private Configurator() {
    }

    public static synchronized <T> T getConfig(final Class<T> configClass) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final String configFileName = configClass.getSimpleName() + ".properties";
        final InputStream inputStream = classLoader.getResourceAsStream(configFileName);

        final Properties properties = new Properties();

        if (inputStream == null) {
            throw new NullPointerException("InputStream is not allowed to be null. Perhaps ClassLoader was unable to " +
                    "find properties file for said config interface?");
        }

        try (final InputStreamReader reader = new InputStreamReader(inputStream)) {
            properties.load(reader);
        } catch (final IOException e) {
            throw new ConfigPropertyException("Error loading properties file: " + configFileName, e);
        }

        return configClass.cast(Proxy.newProxyInstance(classLoader, new Class<?>[]{configClass}, (proxy, method, args) -> {
            final String methodName = method.getName().toLowerCase();
            final String methodNameWithGet = methodName.startsWith("get") ? Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4) : methodName;
            final String methodNameWithoutGet = methodName.startsWith("get") ? methodName.substring(3) : methodName;

            final String[] possiblePropertyNames = new String[]{
                    methodName,
                    methodNameWithGet,
                    methodNameWithoutGet
            };

            for (final String name : possiblePropertyNames) {
                final String propertyValue = properties.getProperty(name);

                if (propertyValue != null && !propertyValue.isBlank()) {
                    logger.error("Using property, %s, from config properties with value: %s", name, propertyValue);
                    return convertValue(propertyValue, method.getReturnType());
                }
            }

            final DefaultValue defaultValueAnnotation = method.getAnnotation(DefaultValue.class);
            if (defaultValueAnnotation != null) {
                logger.error("Using default value for key: %s", methodName);
                return convertValue(defaultValueAnnotation.value(), method.getReturnType());
            }

            throw new ConfigPropertyException("No value found for key: " + methodName);
        }));
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
