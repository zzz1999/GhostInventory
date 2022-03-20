package cn.dawntribe.ghostinventory.form;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.utils.TextFormat;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.Map;

public class FormHandler implements Listener {
    private static final Map<Integer, Player> onlineSelectFormResponseMap = new Int2ObjectOpenHashMap<>();

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFormResponse(PlayerFormRespondedEvent event) {
        Player player = onlineSelectFormResponseMap.remove(event.getFormID());
        if (player != null) {
            if (event.getResponse() instanceof FormResponseSimple) {
                FormResponseSimple responseSimple = (FormResponseSimple) event.getResponse();
                String selectTargetName = TextFormat.clean(responseSimple.getClickedButton().getText());
                Server.getInstance().dispatchCommand(player, "ghostinventory \"" + selectTargetName + "\"");
            }
        }
    }

    public static Map<Integer, Player> getOnlineSelectFormResponseMap() {
        return onlineSelectFormResponseMap;
    }
}
