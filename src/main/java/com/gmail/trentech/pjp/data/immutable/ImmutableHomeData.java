package com.gmail.trentech.pjp.data.immutable;

import static com.gmail.trentech.pjp.data.Keys.PORTALS;

import com.gmail.trentech.pjp.data.mutable.HomeData;
import com.gmail.trentech.pjp.portal.Portal;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableMappedData;
import org.spongepowered.api.data.value.immutable.ImmutableMapValue;

import java.util.Map;

public class ImmutableHomeData extends AbstractImmutableMappedData<String, Portal, ImmutableHomeData, HomeData> {

    public ImmutableHomeData(Map<String, Portal> value) {
        super(value, PORTALS);
    }

    public ImmutableMapValue<String, Portal> homes() {
        return Sponge.getRegistry().getValueFactory().createMapValue(PORTALS, getValue()).asImmutable();
    }

    @Override
    public int getContentVersion() {
        return 0;
    }

    @Override
    public HomeData asMutable() {
        return new HomeData(this.getValue());
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(PORTALS, getValue());
    }
}
