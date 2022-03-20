package cn.dawntribe.ghostinventory.listener;

import cn.dawntribe.ghostinventory.data.GhostStorageData;
import cn.nukkit.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GhostInventoryManager {
    static final Map<Player, Player> ghostHolderMap = new HashMap<>();
    static final Map<Player, Player> ghostTargetMap = new HashMap<>();
    static final List<Long> cooldownInventoryChange = new ArrayList<>();
    static final Map<Player, GhostStorageData> ghostStorageMap = new HashMap<>();
    static final Map<Player, String> ghostOfflineHolderMap = new HashMap<>();
    static final Map<String, Player> ghostOfflineTargetMap = new HashMap<>();

    public static Map<Player, Player> getGhostHolderMap() {
        return ghostHolderMap;
    }

    public static Map<Player, Player> getGhostTargetMap() {
        return ghostTargetMap;
    }

    public static List<Long> getCooldownInventoryChange() {
        return cooldownInventoryChange;
    }

    public static Map<Player, GhostStorageData> getGhostStorageMap() {
        return ghostStorageMap;
    }

    public static Map<Player, String> getGhostOfflineHolderMap() {
        return ghostOfflineHolderMap;
    }

    public static Map<String, Player> getGhostOfflineTargetMap() {
        return ghostOfflineTargetMap;
    }
}
