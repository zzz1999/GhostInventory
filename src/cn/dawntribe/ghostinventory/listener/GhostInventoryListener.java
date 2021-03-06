package cn.dawntribe.ghostinventory.listener;

import cn.dawntribe.ghostinventory.GhostInventory;
import cn.dawntribe.ghostinventory.data.GhostStorageData;
import cn.dawntribe.ghostinventory.data.GhostStorageHandler;
import cn.dawntribe.ghostinventory.data.InventorySyncAction;
import cn.dawntribe.ghostinventory.event.GhostInventoryTerminationEvent;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerToggleSneakEvent;

public class GhostInventoryListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void inventoryChange(EntityInventoryChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            ghostInventorySync((Player) event.getEntity());
        }
    }

    public void ghostInventorySync(Player holder) {
        if (GhostInventoryManager.cooldownInventoryChange.contains(holder.getId())) {
            return;
        }
        Player target = GhostInventoryManager.ghostHolderMap.get(holder);
        if (target == null) {
            target = GhostInventoryManager.ghostTargetMap.get(holder);
        }
        if (target != null) {
            GhostInventory.getAsyncWorker().submit(new InventorySyncAction(holder, target));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void cancelGhost(PlayerToggleSneakEvent event) {
        Player holder = event.getPlayer(), target;
        if ((target = GhostInventoryManager.ghostHolderMap.remove(holder)) != null) {
            //ghostHolderMap.remove(holder);
            GhostInventoryManager.ghostTargetMap.remove(target);
            GhostInventoryTerminationEvent terminationEvent = new GhostInventoryTerminationEvent(holder, target);
            Server.getInstance().getPluginManager().callEvent(terminationEvent);
            if (target.isOnline()) {
                holder.sendMessage("???????????????" + target.getName() + "?????????????????????");
            } else {
                holder.sendMessage("?????????????????????" + target.getName() + "??????,????????????????????????????????????");
                if (GhostStorageHandler.saveOfflinePlayerInventory(holder, target.getName())) {
                    holder.sendMessage("????????????" + target.getName() + "????????????");
                } else {
                    holder.sendMessage("????????????" + target.getName() + "????????????");
                }
            }
            GhostStorageData storageData = GhostInventoryManager.ghostStorageMap.get(holder);
            if (storageData != null) {
                GhostStorageHandler.cover(holder, storageData);
            }
        } else {
            // ?????????????????????????????????
            String pt;
            if ((pt = GhostInventoryManager.ghostOfflineHolderMap.remove(holder)) != null) {
                GhostInventoryTerminationEvent terminationEvent = new GhostInventoryTerminationEvent(holder, target);
                Server.getInstance().getPluginManager().callEvent(terminationEvent);
                if (GhostStorageHandler.saveOfflinePlayerInventory(holder, pt)) {
                    holder.sendMessage("???????????????" + pt + "?????????????????????,??????????????????");
                } else {
                    holder.sendMessage("???????????????" + pt + "?????????????????????,????????????");
                }
            }
            GhostStorageData storageData = GhostInventoryManager.ghostStorageMap.get(holder);
            if (storageData != null) {
                GhostStorageHandler.cover(holder, storageData);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void cancelGhostOnTargetQuit(PlayerQuitEvent event) {
        Player target = event.getPlayer(), holder;
        if ((holder = GhostInventoryManager.ghostTargetMap.get(target)) != null) {
            GhostInventoryManager.ghostHolderMap.remove(holder);
            GhostInventoryManager.ghostTargetMap.remove(target);

            holder.sendMessage("??????" + target.getName() + "?????????,??????????????????");
            GhostStorageData data = GhostInventoryManager.ghostStorageMap.remove(holder);
            if (data != null) {
                GhostStorageHandler.cover(holder, data);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onGhostOfflineTargetOnline(PlayerJoinEvent event) {
        Player target = event.getPlayer();
        Player holder;
        if ((holder = GhostInventoryManager.ghostOfflineTargetMap.remove(target.getName())) != null) {
            GhostInventoryManager.ghostOfflineTargetMap.remove(holder.getName());
            target.getInventory().setContents(holder.getInventory().getContents());
            target.getOffhandInventory().setContents(holder.getOffhandInventory().getContents());
            target.getUIInventory().setContents(holder.getUIInventory().getContents());
            target.getEnderChestInventory().setContents(holder.getEnderChestInventory().getContents());
            holder.sendMessage("??????" + target.getName() + "?????????,????????????????????????");
            GhostInventoryManager.ghostHolderMap.put(holder, target);
            GhostInventoryManager.ghostTargetMap.put(target, holder);
        }
    }

}
