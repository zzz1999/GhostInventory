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
                holder.sendMessage("已经断开对" + target.getName() + "的背包连接同步");
            } else {
                holder.sendMessage("检测到连接玩家" + target.getName() + "离线,将改为修改玩家的离线数据");
                if (GhostStorageHandler.saveOfflinePlayerInventory(holder, target.getName())) {
                    holder.sendMessage("修改玩家" + target.getName() + "背包成功");
                } else {
                    holder.sendMessage("修改玩家" + target.getName() + "背包失败");
                }
            }
            GhostStorageData storageData = GhostInventoryManager.ghostStorageMap.get(holder);
            if (storageData != null) {
                GhostStorageHandler.cover(holder, storageData);
            }
        } else {
            // 如果下蹲的时候玩家离线
            String pt;
            if ((pt = GhostInventoryManager.ghostOfflineHolderMap.remove(holder)) != null) {
                GhostInventoryTerminationEvent terminationEvent = new GhostInventoryTerminationEvent(holder, target);
                Server.getInstance().getPluginManager().callEvent(terminationEvent);
                if (GhostStorageHandler.saveOfflinePlayerInventory(holder, pt)) {
                    holder.sendMessage("已经断开对" + pt + "的背包连接同步,修改已被保存");
                } else {
                    holder.sendMessage("已经断开对" + pt + "的背包连接同步,修改失败");
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

            holder.sendMessage("玩家" + target.getName() + "以下线,连接自动断开");
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
            holder.sendMessage("玩家" + target.getName() + "以上线,连接改为在线操作");
            GhostInventoryManager.ghostHolderMap.put(holder, target);
            GhostInventoryManager.ghostTargetMap.put(target, holder);
        }
    }

}
