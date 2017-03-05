package com.gmail.trentech.pjp.data;

import com.gmail.trentech.pjp.portal.Portal;
import com.google.common.reflect.TypeToken;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Map;

public class Keys {

    private static final TypeToken<Map<String, Portal>> PORTALS_MAP_TOKEN = new TypeToken<Map<String, Portal>>() {
        private static final long serialVersionUID = -1;
    };
    private static final TypeToken<MapValue<String, Portal>> PORTALS_MAP_VALUE_TOKEN = new TypeToken<MapValue<String, Portal>>() {
        private static final long serialVersionUID = -1;
    };
    private static final TypeToken<Value<Portal>> PORTAL_VALUE_TOKEN = new TypeToken<Value<Portal>>() {
        private static final long serialVersionUID = -1;
    };
    private static final TypeToken<Portal> PORTAL_TOKEN = new TypeToken<Portal>() {
        private static final long serialVersionUID = -1;
    };

    public static final Key<Value<Portal>> PORTAL =
            KeyFactory.makeSingleKey(PORTAL_TOKEN, PORTAL_VALUE_TOKEN, DataQuery.of("portal"), "pjp:portal", "portal");
    public static final Key<MapValue<String, Portal>> PORTALS =
            KeyFactory.makeMapKey(PORTALS_MAP_TOKEN, PORTALS_MAP_VALUE_TOKEN, DataQuery.of("portals"), "pjp:portals", "portals");
}
