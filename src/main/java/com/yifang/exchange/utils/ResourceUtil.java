package com.yifang.exchange.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * Utility class for handling resources (e.g: files, static contents...)
 */
@Slf4j
public final class ResourceUtil {
    /**
     * Get resource as stream
     *
     * @param filePath location of the file and filename
     * @return Optional.empty() if file not found, or resource stream
     */
    public static Optional<InputStream> getResourceAsStream(String filePath) {
        try {
            return Optional.of(new ClassPathResource(filePath).getInputStream());
        } catch (IOException e) {
            log.warn("Could not get the resource at {}", filePath, e);
            return Optional.empty();
        }
    }
}
