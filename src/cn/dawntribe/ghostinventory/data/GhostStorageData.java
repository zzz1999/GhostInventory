package cn.dawntribe.ghostinventory.data;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

import java.util.Map;

public class GhostStorageData {
    protected Map<Integer, Item> inventoryContent;
    protected Map<Integer, Item> offhandInventoryContent;
    protected Map<Integer, Item> uiInventoryContent;
    protected Map<Integer, Item> enderChestInventoryContent;

    public GhostStorageData(Map<Integer, Item> inventoryContent, Map<Integer, Item> offhandInventoryContent, Map<Integer, Item> uiInventoryContent, Map<Integer, Item> enderChestInventoryContent) {
        this.inventoryContent = inventoryContent;
        this.offhandInventoryContent = offhandInventoryContent;
        this.uiInventoryContent = uiInventoryContent;
        this.enderChestInventoryContent = enderChestInventoryContent;
    }

    public GhostStorageData(Player player) {
        this.inventoryContent = player.getInventory().getContents();
        this.offhandInventoryContent = player.getOffhandInventory().getContents();
        this.uiInventoryContent = player.getUIInventory().getContents();
        this.enderChestInventoryContent = player.getEnderChestInventory().getContents();
    }
}
