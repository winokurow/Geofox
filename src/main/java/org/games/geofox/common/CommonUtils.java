/**
 * Created by Ilja.Winokurow on 21.06.2015.
 */
package org.games.geofox.common;


public class CommonUtils {
public static boolean isAlpha (String name) {
    String pattern= "^\\d*[a-zA-Z][a-zA-Z\\d]*$";
    return name.matches(pattern);
}
}
