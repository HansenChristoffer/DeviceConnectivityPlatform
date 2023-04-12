package io.miso.core.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.miso.exception.ConfigPropertyException;

class ConfiguratorTest {
    private static final String PROPERTIES_CONTENT = """
            stringvalue=sample string
            intvalue=42
            doublevalue=3.14
            booleanvalue=true
            """;

    @BeforeAll
    static void setup() {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final InputStream inputStream = new ByteArrayInputStream(PROPERTIES_CONTENT.getBytes(StandardCharsets.UTF_8));

        // Override getResourceAsStream to return our custom InputStream
        Thread.currentThread().setContextClassLoader(new ClassLoader(classLoader) {
            @Override
            public InputStream getResourceAsStream(final String name) {
                if (name.equals(TestConfig.class.getSimpleName() + ".properties")) {
                    return inputStream;
                }
                return super.getResourceAsStream(name);
            }
        });
    }

    @Test
    void testConfigurator() {
        final TestConfig config = Configurator.getConfig(TestConfig.class);

        assertEquals("sample string", config.getStringValue());
        assertEquals(42, config.getIntValue());
        assertEquals(3.14, config.getDoubleValue(), 0.001);
        assertTrue(config.isBooleanValue());
        assertEquals("default string", config.getDefaultStringValue());

        assertThrows(ConfigPropertyException.class, config::getMissingValue);
    }

    public interface TestConfig {
        String getStringValue();
    
        int getIntValue();
    
        double getDoubleValue();
    
        boolean isBooleanValue();
    
        @DefaultValue("default string")
        String getDefaultStringValue();
    
        String getMissingValue();
    }
}
