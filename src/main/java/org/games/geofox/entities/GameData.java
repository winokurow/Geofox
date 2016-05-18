package org.games.geofox.entities;

/**
 * Created by Ilja.Winokurow on 29.03.2016.
 */
public class GameData {
    /**
     * sessionID.
     */
    private String sessionID;

    /**
     * Version.
     */
    private String version;

    /**
     * Url.
     */
    private String url;

    /**
     * Serviceinterval.
     */
    private int serviceinterval;

    /**
     * Servicefirstrun.
     */
    private int servicefirstrun;

    /**
     * Gamelength.
     */
    private int gamelength;

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getServiceinterval() {
        return serviceinterval;
    }

    public void setServiceinterval(int serviceinterval) {
        this.serviceinterval = serviceinterval;
    }

    public int getServicefirstrun() {
        return servicefirstrun;
    }

    public void setServicefirstrun(int servicefirstrun) {
        this.servicefirstrun = servicefirstrun;
    }

    public int getGamelength() {
        return gamelength;
    }

    public void setGamelength(int gamelength) {
        this.gamelength = gamelength;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
