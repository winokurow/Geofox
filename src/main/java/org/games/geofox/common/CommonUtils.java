package org.games.geofox.common;

/**
 * Created by Ilja.Winokurow on 21.06.2015.
 */
public class CommonUtils {
public static boolean isAlpha (String name) {
    String pattern= "^\\d*[a-zA-Z][a-zA-Z\\d]*$";
    boolean isMatch = name.matches(pattern);
    return isMatch;
}
}
