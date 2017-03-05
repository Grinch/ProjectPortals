package com.gmail.trentech.pjp.utils;

import co.aikar.timings.Timing;
import com.gmail.trentech.pjp.Main;

public class Timings {

    private final Timing changeSignEvent, interactBlockEventSecondary, changeBlockEventModify, changeBlockEventPlace,
            changeBlockEventBreak, constructPortalEvent, moveEntityEvent, teleportEvent, teleportEventServer,
            teleportEventLocal, destructEntityEventDeath, collideBlockEvent;

    public Timings() {
        this.changeSignEvent = timing("onChangeSignEvent");
        this.interactBlockEventSecondary = timing("onInteractBlockEventSecondary");
        this.changeBlockEventModify = timing("onChangeBlockEventModify");
        this.changeBlockEventPlace = timing("onChangeBlockEventPlace");
        this.changeBlockEventBreak = timing("onChangeBlockEventBreak");
        this.constructPortalEvent = timing("onConstructPortalEvent");
        this.moveEntityEvent = timing("onMoveEntityEvent");
        this.teleportEvent = timing("onTeleportEvent");
        this.teleportEventServer = timing("onTeleportEventServer");
        this.teleportEventLocal = timing("onTeleportEventLocal");
        this.destructEntityEventDeath = timing("onDestructEntityEventDeath");
        this.collideBlockEvent = timing("onCollideBlockEvent");
    }

    private Timing timing(String key) {
        return co.aikar.timings.Timings.of(Main.getPlugin(), key);
    }

    public Timing onChangeSignEvent() {
        return changeSignEvent;
    }

    public Timing onInteractBlockEventSecondary() {
        return interactBlockEventSecondary;
    }

    public Timing onConstructPortalEvent() {
        return constructPortalEvent;
    }

    public Timing onMoveEntityEvent() {
        return moveEntityEvent;
    }

    public Timing onTeleportEvent() {
        return teleportEvent;
    }

    public Timing onTeleportEventLocal() {
        return teleportEventLocal;
    }

    public Timing onTeleportEventServer() {
        return teleportEventServer;
    }

    public Timing onChangeBlockEventModify() {
        return changeBlockEventModify;
    }

    public Timing onChangeBlockEventPlace() {
        return changeBlockEventPlace;
    }

    public Timing onChangeBlockEventBreak() {
        return changeBlockEventBreak;
    }

    public Timing onDestructEntityEventDeath() {
        return destructEntityEventDeath;
    }

    public Timing onCollideBlockEvent() {
        return collideBlockEvent;
    }
}
