package me.friedwingis.plugin.orbittc.utils;

import me.friedwingis.plugin.orbitcore.objects.DiscordWebhook;
import me.friedwingis.plugin.orbitcore.utils.TimeUtils;
import me.friedwingis.plugin.orbittc.struct.TebexPackage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.Calendar;
import java.util.Date;

/**
 * Copyright FriedWingis - 2023
 * All code is private and not to be used by any
 * other entity unless explicitly stated otherwise.
 **/
public class EmbedHelper {

    public static DiscordWebhook.EmbedObject getNewPackagePurchase(final TebexPackage tebexPackage) {
        final OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(tebexPackage.purchaserName);

        final DiscordWebhook.EmbedObject embed = getBase(":money_with_wings: | Tebex Package Purchase", Color.ORANGE);
        embed.setDescription("A new package has been purchased from the store!");
        embed.addField("Transaction Id", "> " + tebexPackage.transactionID, true);
        embed.addField("Package", "> " + tebexPackage.packageName, true);
        embed.addField("Online", "> **" + (player != null && player.isOnline() ? "yes!" : "no") + "**", true);
        embed.addField("Account Name", "> " + tebexPackage.purchaserName, true);
        embed.addField("Has played before", "> **" + (player != null ? "yes!" : "no") + "**", true);
        embed.addField(player != null ? "Known UUID: " : "Determined UUID: ", "> " + tebexPackage.purchaserId, true);

        return embed;
    }

    public static DiscordWebhook.EmbedObject getAccepted(final TebexPackage tebexPackage, final Player player) {
        final DiscordWebhook.EmbedObject embed = getBase(":white_check_mark: | Package Confirmed", Color.GREEN);
        embed.setDescription(player.getName() + " has confirmed a package!");
        embed.addField("Transaction Id", "> " + tebexPackage.transactionID, true);
        embed.addField("Package", "> " + tebexPackage.packageName, true);

        return embed;
    }

    public static DiscordWebhook.EmbedObject getDenied(final TebexPackage tebexPackage, final Player player) {
        final DiscordWebhook.EmbedObject embed = getBase(":x: | package denied", Color.RED);
        embed.setDescription(player.getName() + " has denied a package!");
        embed.addField("Transaction Id", "> " + tebexPackage.transactionID, true);
        embed.addField("Package", "> " + tebexPackage.packageName, true);

        return embed;
    }

    private static DiscordWebhook.EmbedObject getBase(final String title, final Color color) {
        return new DiscordWebhook.EmbedObject()
                .setTitle(title)
                .setColor(color)
                .setFooter("© " + Calendar.getInstance().get(Calendar.YEAR) + " | OrbitCraft", "");
    }
}
