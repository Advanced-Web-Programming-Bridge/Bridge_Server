package gachon.bridge.mealservice.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RegexService {

    public static boolean isDate(String date){
        Pattern pattern = Pattern.compile("^\\d{2}-\\d{2}-\\{2}$");

        Matcher matcher = pattern.matcher(date);

        return matcher.find();
    }
}
