package io.miso.core.config;

import io.miso.exception.ConfigPropertyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Proxy;
import java.util.*;

public class Configurator {
    private static final Logger logger = LogManager.getFormatterLogger();

    private Configurator() {
    }

    public static synchronized <T> T getConfig(final Class<T> configClass) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final String configFileName = configClass.getSimpleName() + ".properties";
        final InputStream inputStream = classLoader.getResourceAsStream(configFileName);

        final Properties properties;

        if (inputStream == null) {
            throw new NullPointerException("InputStream is not allowed to be null. Perhaps ClassLoader was unable to " +
                    "find properties file for said config interface? [" + configFileName + "]");
        }

        try (final InputStreamReader reader = new InputStreamReader(inputStream)) {
            properties = loadPropertiesCaseInsensitive(reader);
        } catch (final IOException e) {
            throw new ConfigPropertyException("Error loading properties file: " + configFileName, e);
        }

        return configClass.cast(Proxy.newProxyInstance(classLoader, new Class<?>[]{configClass}, (proxy, method, args) -> {
            final String methodName = method.getName().toLowerCase();

            // Extract the base name without "get" or "is" prefix
            final String baseName = methodName.replaceFirst("^(get|is)", "");
            final String baseNameWithFirstCharLower = Character.toLowerCase(baseName.charAt(0)) + baseName.substring(1);

            // Generate possible property names
            final List<String> possiblePropertyNames = Arrays.asList(
                    methodName,
                    "get" + baseName,
                    baseNameWithFirstCharLower,
                    "is" + baseName
            );

            // Find the property value and index from the properties
            final Optional<AbstractMap.SimpleEntry<String, String>> propertyEntryOpt = possiblePropertyNames.stream()
                    .map(name -> new AbstractMap.SimpleEntry<>(name, properties.getProperty(name)))
                    .filter(entry -> Objects.nonNull(entry.getValue()) && !entry.getValue().isBlank())
                    .findFirst();

            if (propertyEntryOpt.isPresent()) {
                final Map.Entry<String, String> propertyEntry = propertyEntryOpt.get();
                final String propertyName = propertyEntry.getKey();
                final String propertyValue = propertyEntry.getValue();

                // TODO: This LogEvent is spamming everytime something accesses data from the interface. Either remove it or do something else!
                logger.debug("Using property, %s, from config properties with value: %s", propertyName, propertyValue);
                return convertValue(propertyValue, method.getReturnType());
            }

            final DefaultValue defaultValueAnnotation = method.getAnnotation(DefaultValue.class);
            if (defaultValueAnnotation != null) {
                logger.debug("Using default value for key: %s", methodName);
                return convertValue(defaultValueAnnotation.value(), method.getReturnType());
            }

            throw new ConfigPropertyException("No value found for key: " + methodName);
        }));
    }

    private static Object convertValue(final String value, final Class<?> returnType) {
        if (returnType == String.class) {
            return value;
        } else if (returnType == long.class || returnType == Long.class) {
            return Long.parseLong(value);
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

    private static Properties loadPropertiesCaseInsensitive(final InputStreamReader inputStreamReader) throws IOException {
        final Properties properties = new Properties();
        final Properties caseInsensitiveProperties = new Properties();
        properties.load(inputStreamReader);

        for (final String key : properties.stringPropertyNames()) {
            caseInsensitiveProperties.setProperty(key.toLowerCase(), properties.getProperty(key));
        }

        return caseInsensitiveProperties;
    }

}

