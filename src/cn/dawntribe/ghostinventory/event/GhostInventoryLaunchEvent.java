package cn.dawntribe.ghostinventory.event;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;

public class GhostInventoryLaunchEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Player holder;
    private final Player target;

    public GhostInventoryLaunchEvent(Player holder, Player target) {
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
