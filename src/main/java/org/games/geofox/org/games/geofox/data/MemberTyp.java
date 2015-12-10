package org.games.geofox.org.games.geofox.data;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Created by Ilja.Winokurow on 16.10.2015.
 */

public enum MemberTyp {
    FOX (1),
    HUNTER (2);

    private final int value;

    MemberTyp(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    @JsonCreator
    static MemberTyp forValue(int val) {
        for(MemberTyp v : values()){
            if( v.value == val){
                return v;
            }
        }
        return null;
    }

}
