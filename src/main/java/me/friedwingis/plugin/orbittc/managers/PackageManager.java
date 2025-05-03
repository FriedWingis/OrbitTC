package me.friedwingis.plugin.orbittc.managers;

import com.google.common.collect.Lists;
import me.friedwingis.plugin.orbitcore.objects.DiscordWebhook;
import me.friedwingis.plugin.orbitcore.utils.Chat;
import me.friedwingis.plugin.orbittc.OrbitTC;
import me.friedwingis.plugin.orbittc.struct.TebexPackage;
import me.friedwingis.plugin.orbittc.utils.EmbedHelper;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Copyright FriedWingis - 2023
 * All code is private and not to be used by any
 * other entity unless explicitly stated otherwise.
 **/
public class PackageManager {
    private final OrbitTC plugin;
    private final List<String> thankYou = Lists.newArrayList();

    public PackageManager(final OrbitTC plugin) {
        this.plugin = plugin;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!thankYou.isEmpty()) {
                    thankYou.forEach(s -> {
                        Bukkit.broadcast(Chat.format("<dark_gray>[<b><light_purple>Orbit<dark_purple>Alert<gray></b>] <light_purple>Thank you <b>" + s + "</b> for supporting <dark_purple><b>store.orbitcraft.net"));
                    });

                    plugin.getLogger().info("Thanked " + thankYou.size() + " name(s) for their patronage!");
                    thankYou.clear();
                }
            }
        }.runTaskTimer(plugin, 20L * 300L, 20L * 300L);
    }

    public void processIncoming(final TebexPackage tebexPackage) throws IOException {
        final DiscordWebhook webhook = new DiscordWebhook(DiscordWebhook.PredefinedChannels.TEBEX_CONNECT.url);
        webhook.setContent("**NEW PACKAGE PURCHASE RECEIVED**");
        webhook.addEmbed(EmbedHelper.getNewPackagePurchase(tebexPackage));
        CompletableFuture.runAsync(() -> {
            try {
                webhook.execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        final UUID uuid = UUID.fromString(tebexPackage.purchaserId);
        plugin.getConnect().createNewEntry("UNCLAIMED_PACKAGES", uuid, tebexPackage);

        final Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            final Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3000), Duration.ofMillis(1000));
            final Title title = Title.title(Chat.format("<gold><b>/confirm"), Chat.format("<gold>You have a new package awaiting your claim!"), times);

            player.showTitle(title);

            player.sendMessage(Chat.format("<gold><b><!> You have a new BuyCraft package to /confirm!"));
            player.sendMessage(Chat.format("<gray>Confirm receipt and ownership of this package to claim the contents via the /confirm menu."));
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.3f);
        }

        thankYou.add(tebexPackage.purchaserName);
        plugin.getLogger().info("New package purchase! " + tebexPackage);
    }

    public void processConfirmPackage(final Player player, final TebexPackage tebexPackage, final ClickType clickType) {
        if (clickType == ClickType.LEFT) {
            final DiscordWebhook webhook = new DiscordWebhook(DiscordWebhook.PredefinedChannels.TEBEX_CONNECT.url);
            webhook.addEmbed(EmbedHelper.getAccepted(tebexPackage, player));
            webhook.setContent("**NEW PACKAGE ACCEPTED**");
            CompletableFuture.runAsync(() -> {
                try {
                    webhook.execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            player.closeInventory();
            player.sendMessage(Chat.format("<green><b>You have accepted the " + tebexPackage.packageName + " package!"));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.77f);

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "acredits give " + player.getName() + " " + (tebexPackage.packageName.replace("CREDITS_", "")));

            plugin.getConnect().deleteEntry("UNCLAIMED_PACKAGES", player.getUniqueId(), tebexPackage);
            plugin.getConnect().createNewEntry("CLAIMED_PACKAGES", player.getUniqueId(), tebexPackage);

            plugin.getLogger().info(player.getName() + " has ACCEPTED a package! " + tebexPackage);
        } else if (clickType == ClickType.RIGHT) {
            final DiscordWebhook webhook = new DiscordWebhook(DiscordWebhook.PredefinedChannels.TEBEX_CONNECT.url);
            webhook.addEmbed(EmbedHelper.getDenied(tebexPackage, player));
            webhook.setContent("**NEW PACKAGE DENIED**");
            CompletableFuture.runAsync(() -> {
                try {
                    webhook.execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            player.closeInventory();
            player.sendMessage(Chat.format("<red><b>You have declined to accept the " + tebexPackage.packageName + " package."));
            player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 1.0f, 0.77f);

            plugin.getConnect().deleteEntry("UNCLAIMED_PACKAGES", player.getUniqueId(), tebexPackage);
            plugin.getConnect().createNewEntry("DENIED_PACKAGES", player.getUniqueId(), tebexPackage);

            plugin.getLogger().info(player.getName() + " has DENIED a package! " + tebexPackage);
        }
    }
}
