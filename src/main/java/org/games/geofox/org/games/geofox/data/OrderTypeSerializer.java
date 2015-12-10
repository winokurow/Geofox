package org.games.geofox.org.games.geofox.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by Ilja.Winokurow on 16.10.2015.
 */
public class OrderTypeSerializer extends JsonSerializer<MemberTyp> {

    @Override
    public void serialize(MemberTyp value, JsonGenerator generator,
                          SerializerProvider provider) throws IOException,
            JsonProcessingException {

        generator.writeStartObject();
        generator.writeFieldName("typ");
        generator.writeString(value.toString());
    }
}
