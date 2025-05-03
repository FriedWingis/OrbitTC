package me.friedwingis.plugin.orbittc.commands;

import lombok.AllArgsConstructor;
import me.friedwingis.plugin.orbitcore.objects.DiscordWebhook;
import me.friedwingis.plugin.orbitcore.utils.Chat;
import me.friedwingis.plugin.orbittc.OrbitTC;
import me.friedwingis.plugin.orbittc.struct.TebexPackage;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.BukkitCommandActor;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Copyright FriedWingis - 2023
 * All code is private and not to be used by any
 * other entity unless explicitly stated otherwise.
 **/
@AllArgsConstructor
public class QueuePurchaseCommand {
    private final OrbitTC plugin;

    @Command("queuepurchase")
    public void onCommand(final BukkitCommandActor actor, final String transactionID, final String packageName, final String ip, final String purchaserName, final String purchaserId) {
        if (actor.isConsole()) {
            try {
                final TebexPackage tebexPackage = new TebexPackage(transactionID, packageName, ip, purchaserName, purchaserId);
                plugin.getPackageManager().processIncoming(tebexPackage);
            } catch (IOException | GeneralSecurityException e) {
                final DiscordWebhook webhook = new DiscordWebhook(DiscordWebhook.PredefinedChannels.TEBEX_CONNECT.url);
                webhook.setContent("**SEVERE PACKAGE PURCHASE ERROR:** Transaction (id:" + transactionID + ") for player " + purchaserName + " has **FAILED** to be processed. This requires immediate attention!!!");
                try {
                    webhook.execute();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                throw new RuntimeException(e);
            }
        } else
            actor.getSender().sendMessage(Chat.format("<dark_red>You seem lost among the stars... this path is for the unseen hand guiding from the void."));
    }
}
