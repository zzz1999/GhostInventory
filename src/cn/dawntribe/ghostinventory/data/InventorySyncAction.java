package cn.dawntribe.ghostinventory.data;

import cn.nukkit.Player;

public final class InventorySyncAction {
    private final Player holder;
    private final Player target;

    public InventorySyncAction(Player holder, Player target) {
        this.holder = holder;
        this.target = target;
    }

    public Player getHolder() {
        return holder;
    }

    public Player getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return holder.getName() + "," + target.getName() + "," + holder.getInventory().getContents() + "\n" + target.getInventory().getContents();
    }
}
