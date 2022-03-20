package cn.dawntribe.ghostinventory;

import cn.dawntribe.ghostinventory.command.GhostCommand;
import cn.dawntribe.ghostinventory.form.FormHandler;
import cn.dawntribe.ghostinventory.listener.GhostInventoryListener;
import cn.dawntribe.ghostinventory.scheduler.AsyncWorker;
import cn.nukkit.plugin.PluginBase;

public class GhostInventory extends PluginBase {
    private static GhostInventory instance;
    private static final AsyncWorker asyncWorker = new AsyncWorker();

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new GhostInventoryListener(), this);
        this.getServer().getPluginManager().registerEvents(new FormHandler(), this);
        this.getServer().getCommandMap().register("dts", new GhostCommand());

        asyncWorker.start();
    }

    public static GhostInventory getInstance() {
        return instance;
    }

    public static AsyncWorker getAsyncWorker() {
        return asyncWorker;
    }

}
