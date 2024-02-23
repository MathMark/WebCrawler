package com.experimental.webcrawler.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebsiteUtil {
    
    private static final Pattern pattern = Pattern.compile("^(https?://[^/]+)");
    
    private WebsiteUtil() {
    }
    
    public static String cutDomain(String url) {
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }
}
