package org.games.geofox.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.games.geofox.org.games.geofox.data.MemberTyp;

import java.io.Serializable;

/**
 * Position.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberData implements Serializable {

    /**
     * Name.
     */
    private String name;

    /**
     * Type.
     */
    private MemberTyp typ;

    /**
     * Longitude.
     */
    private double longitude;

    /**
     * Latitude.
     */
    private double latitude;

    /**
     * Accuracy.
     */
    private double accuracy;

    /**
     * Altitude.
     */
    private double altitude;

    /**
     * Speed.
     */
    private double speed;

    public MemberData(String name, double latitude, double longitude) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    //Introducing the dummy constructor
    public MemberData() {
    }


    public MemberData(String name, MemberTyp typ, double longitude, double latitude, double accuracy, double altitude, double speed) {
        this.name = name;
        this.typ = typ;
        this.longitude = longitude;
        this.latitude = latitude;
        this.accuracy = accuracy;
        this.altitude = altitude;
        this.speed = speed;
    }

    /**
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude
     *          the longitude to set
     */
    public void setLongitude(final double longitude) {
        this.longitude = longitude;
    }

    /**
     * @return the Latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude
     *          the Latitude to set
     */
    public void setLatitude(final double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the accuracy
     */
    public double getAccuracy() {
        return accuracy;
    }

    /**
     * @param accuracy
     *          the accuracy to set
     */
    public void setAccuracy(final double accuracy) {
        this.accuracy = accuracy;
    }

    /**
     * @return the speed
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * @param speed
     *          the speed to set
     */
    public void setSpeed(final double speed) {
        this.speed = speed;
    }

    /**
     * @return the altitude
     */
    public double getAltitude() {
        return altitude;
    }

    /**
     * @param altitude
     *          the altitude to set
     */
    public void setAltitude(final double altitude) {
        this.altitude = altitude;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MemberTyp getTyp() {
        return typ;
    }

    public void setTyp(MemberTyp typ) {
        this.typ = typ;
    }
}

