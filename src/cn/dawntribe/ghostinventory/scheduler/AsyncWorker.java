package cn.dawntribe.ghostinventory.scheduler;

import cn.dawntribe.ghostinventory.data.InventorySyncAction;
import cn.dawntribe.ghostinventory.event.GhostInventorySyncEvent;
import cn.dawntribe.ghostinventory.listener.GhostInventoryManager;
import cn.nukkit.Player;
import cn.nukkit.Server;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@SuppressWarnings("InfiniteLoopStatement")
public class AsyncWorker extends Thread {
    private static final Queue<InventorySyncAction> actionQueue = new LinkedBlockingQueue<>();

    public AsyncWorker() {
        this.setName("GhostInventory Action Thread");
    }

    @Override
    public void run() {
        while (true) {
            InventorySyncAction action = actionQueue.poll();
            if (action != null) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Player holder = action.getHolder(), target = action.getTarget();
                GhostInventorySyncEvent event = new GhostInventorySyncEvent(holder, target);
                Server.getInstance().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    target.getInventory().setContents(holder.getInventory().getContents());
                    target.getOffhandInventory().setContents(holder.getOffhandInventory().getContents());
                    target.getUIInventory().setContents(holder.getUIInventory().getContents());
                    target.getEnderChestInventory().setContents(holder.getEnderChestInventory().getContents());
                    GhostInventoryManager.getCooldownInventoryChange().remove(action.getTarget().getId());
                }
            } else {
                LockSupport.park();
            }
        }
    }

    public void submit(InventorySyncAction action) {
        GhostInventoryManager.getCooldownInventoryChange().add(action.getTarget().getId());
        actionQueue.offer(action);
        LockSupport.unpark(this);
    }
}
