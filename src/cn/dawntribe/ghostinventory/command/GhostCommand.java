package cn.dawntribe.ghostinventory.command;

import cn.dawntribe.ghostinventory.GhostInventory;
import cn.dawntribe.ghostinventory.data.GhostStorageData;
import cn.dawntribe.ghostinventory.data.GhostStorageHandler;
import cn.dawntribe.ghostinventory.data.InventorySyncAction;
import cn.dawntribe.ghostinventory.event.GhostInventoryLaunchEvent;
import cn.dawntribe.ghostinventory.form.FormHandler;
import cn.dawntribe.ghostinventory.listener.GhostInventoryManager;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.TextFormat;

import java.util.Iterator;

public class GhostCommand extends Command {
    private static final int MAXIMUM_ONLINE_PLAYERS_IN_FORM = 25;

    public GhostCommand() {
        super("ghostinventory", "幽灵背包命令", "/gi <玩家名字(空格使用&代替)>", new String[]{"gi"});
        this.setPermission("ghostinventory.command.invCommand");
        this.setPermission("你没有权限使用幽灵背包");
        this.commandParameters.clear();
        this.commandParameters.put("ghostinventory_target_choose", new CommandParameter[]{
                CommandParameter.newType("targetPlayer", CommandParamType.TARGET)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }
        if (sender instanceof Player) {
            Player holder = (Player) sender;
            if (args.length == 0) {
                // 在线玩家form
                FormWindowSimple form = new FormWindowSimple("幽灵背包", "请选择需要连接的玩家。\n列出当前在线玩家，最多" + MAXIMUM_ONLINE_PLAYERS_IN_FORM + "个。");
                Iterator<Player> it = Server.getInstance().getOnlinePlayers().values().iterator();
                int count = 0;
                while (it.hasNext()) {
                    if (MAXIMUM_ONLINE_PLAYERS_IN_FORM > count) {
                        Player player = it.next();
                        form.addButton(new ElementButton((player.isOnline() ? TextFormat.GREEN : TextFormat.GRAY) + player.getName(),
                                new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_PATH, player.isOp() ? "textures/ui/permissions_op_crown.png" : "textures/ui/permissions_member_star.png"))
                        );
                        count++;
                    } else {
                        break;
                    }
                }
                FormHandler.getOnlineSelectFormResponseMap().put(holder.showFormWindow(form), holder);
            } else {
                String targetName = args[0];
                Player target = Server.getInstance().getPlayerExact(targetName);

                if (GhostInventoryManager.getGhostHolderMap().containsKey(holder)) {
                    holder.sendMessage(TextFormat.YELLOW + "你正在与玩家" + target + "连接中,请先断开连接");
                    return true;
                }
                if (holder == target) {
                    holder.sendMessage("你不能与自己进行背包连接");
                    return true;
                }
                GhostInventoryLaunchEvent event = new GhostInventoryLaunchEvent(holder, target);
                Server.getInstance().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    if (target != null) {
                        GhostInventoryManager.getGhostStorageMap().put(holder, new GhostStorageData(holder));
                        GhostInventory.getAsyncWorker().submit(new InventorySyncAction(target, holder));
                        GhostInventoryManager.getGhostHolderMap().put(holder, target);
                        GhostInventoryManager.getGhostTargetMap().put(target, holder);
                        holder.sendMessage(TextFormat.GREEN + "连接背包成功,以将你与" + target.getName() + "的背包连接.按下下蹲键取消连接");
                    } else {
                        sender.sendMessage(TextFormat.YELLOW + "玩家" + targetName + "为离线玩家,正在尝试寻找该玩家数据");
                        if (GhostStorageHandler.loadOfflinePlayerInventory(holder, targetName)) {
                            GhostInventoryManager.getGhostStorageMap().put(holder, new GhostStorageData(holder));
                            GhostInventoryManager.getGhostOfflineTargetMap().put(targetName, holder);
                            GhostInventoryManager.getGhostOfflineHolderMap().put(holder, targetName);
                            holder.sendMessage(TextFormat.GREEN + "连接背包成功,以将你与" + targetName + "背包连接.按下下蹲键取消连接");
                        } else {
                            sender.sendMessage(TextFormat.RED + "无法找到玩家" + targetName + "的数据,该玩家可能没有进过服");
                        }
                    }
                } else {
                    holder.sendMessage(TextFormat.RED + "连接背包被事件取消");
                }
            }
        } else {
            sender.sendMessage("只有玩家才能使用这个命令");
            return false;
        }
        return true;
    }
}
