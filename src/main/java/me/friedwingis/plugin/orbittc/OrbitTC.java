package me.friedwingis.plugin.orbittc;

import lombok.Getter;
import me.friedwingis.plugin.orbitcore.OrbitCore;
import me.friedwingis.plugin.orbittc.commands.ConfirmCommand;
import me.friedwingis.plugin.orbittc.commands.QueuePurchaseCommand;
import me.friedwingis.plugin.orbittc.managers.PackageManager;
import me.friedwingis.plugin.orbittc.struct.ConnectDatabase;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class OrbitTC extends JavaPlugin {
    private ConnectDatabase connect;
    private PackageManager packageManager;

    @Override
    public void onEnable() {
        this.packageManager = new PackageManager(this);

        this.connect = new ConnectDatabase(this);
        this.connect.attemptTableCreate("UNCLAIMED_PACKAGES");
        this.connect.attemptTableCreate("CLAIMED_PACKAGES");
        this.connect.attemptTableCreate("DENIED_PACKAGES");

        OrbitCore._I.getCommandHandler().register(new ConfirmCommand(this), new QueuePurchaseCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerListeners(this), this);

        getLogger().info("--------------------------");
        getLogger().info("TEBEXCONNECT BY FRIED HAS ENABLED");
        getLogger().info("Awaiting incoming package purchases!");
        getLogger().info("--------------------------");
    }
}
