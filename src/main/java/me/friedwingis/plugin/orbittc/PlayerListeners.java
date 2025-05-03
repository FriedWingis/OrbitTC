package me.friedwingis.plugin.orbittc;

import lombok.AllArgsConstructor;
import me.friedwingis.plugin.orbitcore.utils.Chat;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Duration;

/**
 * Copyright FriedWingis - 2023
 * All code is private and not to be used by any
 * other entity unless explicitly stated otherwise.
 **/
@AllArgsConstructor
public class PlayerListeners implements Listener {
    private final OrbitTC plugin;

    @EventHandler
    private void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        plugin.getConnect().getPackagesFrom("UNCLAIMED_PACKAGES", player.getUniqueId()).thenAccept(tebexPackages -> {
            if (!tebexPackages.isEmpty()) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (!player.isOnline())
                        return;

                    final Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3000), Duration.ofMillis(1000));
                    final Title title = Title.title(Chat.format("<gold><b>/confirm"), Chat.format("<gold>You have a new package awaiting your claim!"), times);

                    player.showTitle(title);

                    player.sendMessage(Chat.format("<gold><b><!> You have a new BuyCraft package to /confirm!"));
                    player.sendMessage(Chat.format("<gray>Confirm receipt and ownership of this package to claim the contents via the /confirm menu."));
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.3f);
                }, 60L);
            }
        });
    }
}
