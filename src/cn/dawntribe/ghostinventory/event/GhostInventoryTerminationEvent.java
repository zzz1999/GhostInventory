package cn.dawntribe.ghostinventory.event;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;

public class GhostInventoryTerminationEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player holder;
    private final Player target;

    public GhostInventoryTerminationEvent(Player holder, Player target) {
        this.holder = holder;
        this.target = target;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Player getHolder() {
        return holder;
    }

    public Player getTarget() {
        return target;
    }
}
