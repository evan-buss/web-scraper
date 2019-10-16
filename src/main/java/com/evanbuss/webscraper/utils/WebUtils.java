package com.evanbuss.webscraper.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebUtils {

    private static Logger logger = LoggerFactory.getLogger(WebUtils.class);
    private static String OS = System.getProperty("os.name").toLowerCase();

    private static boolean isWindows() {
        return (OS.contains("win"));
    }

    private static boolean isMac() {
        return (OS.contains("mac"));
    }

    private static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0);
    }

    /**
     * Open the given url in the system's default browser. Handles different operating system protocols
     *
     * @param url the url to open. It should start with http or https
     */
    public static void openInBrowser(String url) {
        try {
            if (isUnix()) {
                Runtime.getRuntime().exec(new String[]{"xdg-open", url});
            } else if (isWindows()) {
                Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
            } else if (isMac()) {
                Runtime.getRuntime().exec(new String[]{"open", url});
            }
        } catch (Exception ignored) {
            logger.error("Could not open browser.");
        }

    }

}
