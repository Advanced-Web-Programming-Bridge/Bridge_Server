package gachon.bridge.userservice.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UUIDValidator {
    private static final String UUID_REGEX =
            "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";
    private static final Pattern UUID_PATTERN = Pattern.compile(UUID_REGEX);

    public static boolean validUUIDFormat(String uuid) {
        return UUID_PATTERN.matcher(uuid).matches();
    }
}
