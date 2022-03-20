package cn.dawntribe.ghostinventory.data;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

@SuppressWarnings("deprecation")
public class GhostStorageHandler {
    public static void cover(Player player, GhostStorageData data) {
        player.getInventory().setContents(data.inventoryContent);
        player.getOffhandInventory().setContents(data.offhandInventoryContent);
        player.getUIInventory().setContents(data.uiInventoryContent);
        player.getEnderChestInventory().setContents(data.enderChestInventoryContent);
    }

    public static boolean loadOfflinePlayerInventory(Player holder, String offlinePlayerName) {
        CompoundTag namedTag = Server.getInstance().getOfflinePlayerData(offlinePlayerName, false);
        if (namedTag == null) {
            holder.sendMessage("无法找到玩家" + offlinePlayerName + "的离线数据");
            return false;
        }
        if (namedTag.contains("Inventory") && namedTag.get("Inventory") instanceof ListTag) {
            ListTag<CompoundTag> inventoryList = namedTag.getList("Inventory", CompoundTag.class);
            for (CompoundTag item : inventoryList.getAll()) {
                int slot = item.getByte("Slot");
                if (slot >= 0 && slot < 9) { //hotbar
                    //Old hotbar saving stuff, remove it (useless now)
                    inventoryList.remove(item);
                } else if (slot >= 100 && slot < 104) {
                    holder.getInventory().setItem(holder.getInventory().getSize() + slot - 100, NBTIO.getItemHelper(item));
                } else if (slot == -106) {
                    holder.getOffhandInventory().setItem(0, NBTIO.getItemHelper(item));
                } else {
                    holder.getInventory().setItem(slot - 9, NBTIO.getItemHelper(item));
                }
            }
        }

        if (namedTag.contains("EnderItems") && namedTag.get("EnderItems") instanceof ListTag) {
            ListTag<CompoundTag> inventoryList = namedTag.getList("EnderItems", CompoundTag.class);
            for (CompoundTag item : inventoryList.getAll()) {
                holder.getEnderChestInventory().setItem(item.getByte("Slot"), NBTIO.getItemHelper(item));
            }
        }
        return true;
    }

    public static boolean saveOfflinePlayerInventory(Player holder, String offlinePlayerName) {
        CompoundTag namedTag = Server.getInstance().getOfflinePlayerData(offlinePlayerName, false);
        if (namedTag == null) {
            holder.sendMessage("无法找到玩家" + offlinePlayerName + "的离线数据");
            return false;
        }
        ListTag<CompoundTag> inventoryTag = null;
        if (holder.getInventory() != null) {
            inventoryTag = new ListTag<>("Inventory");
            namedTag.putList(inventoryTag);

            for (int slot = 0; slot < 9; ++slot) {
                inventoryTag.add(new CompoundTag()
                        .putByte("Count", 0)
                        .putShort("Damage", 0)
                        .putByte("Slot", slot)
                        .putByte("TrueSlot", -1)
                        .putShort("id", 0)
                );
            }

            int slotCount = Player.SURVIVAL_SLOTS + 9;
            for (int slot = 9; slot < slotCount; ++slot) {
                Item item = holder.getInventory().getItem(slot - 9);
                inventoryTag.add(NBTIO.putItemHelper(item, slot));
            }

            for (int slot = 100; slot < 104; ++slot) {
                Item item = holder.getInventory().getItem(holder.getInventory().getSize() + slot - 100);
                if (item != null && item.getId() != Item.AIR) {
                    inventoryTag.add(NBTIO.putItemHelper(item, slot));
                }
            }
        }

        if (holder.getOffhandInventory() != null) {
            Item item = holder.getOffhandInventory().getItem(0);
            if (item.getId() != Item.AIR) {
                if (inventoryTag == null) {
                    inventoryTag = new ListTag<>("Inventory");
                    namedTag.putList(inventoryTag);
                }
                inventoryTag.add(NBTIO.putItemHelper(item, -106));
            }
        }

        namedTag.putList(new ListTag<CompoundTag>("EnderItems"));
        if (holder.getEnderChestInventory() != null) {
            for (int slot = 0; slot < 27; ++slot) {
                Item item = holder.getEnderChestInventory().getItem(slot);
                if (item != null && item.getId() != Item.AIR) {
                    namedTag.getList("EnderItems", CompoundTag.class).add(NBTIO.putItemHelper(item, slot));
                }
            }
        }
        Server.getInstance().saveOfflinePlayerData(offlinePlayerName, namedTag);
        holder.sendMessage("保存玩家" + offlinePlayerName + "的库存成功");
        return true;
    }
}
