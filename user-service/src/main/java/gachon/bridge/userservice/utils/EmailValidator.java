package gachon.bridge.userservice.utils;

import java.util.regex.Pattern;

public class EmailValidator {
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static boolean validEmailFormat(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
