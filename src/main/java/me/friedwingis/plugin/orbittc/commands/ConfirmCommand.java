package me.friedwingis.plugin.orbittc.commands;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import lombok.AllArgsConstructor;
import me.friedwingis.plugin.orbitcore.utils.Chat;
import me.friedwingis.plugin.orbittc.OrbitTC;
import me.friedwingis.plugin.orbittc.struct.TebexPackage;
import me.friedwingis.plugin.orbittc.utils.Serialization;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;

/**
 * Copyright FriedWingis - 2023
 * All code is private and not to be used by any
 * other entity unless explicitly stated otherwise.
 **/
@AllArgsConstructor
public class ConfirmCommand {
    private final OrbitTC plugin;

    @Command({"confirm", "claim"})
    @CommandPermission("default.OrbitTC")
    public void onCommand(final Player player) {
        plugin.getConnect().getPackagesFrom("UNCLAIMED_PACKAGES", player.getUniqueId()).thenAccept(tebexPackages -> {
            if (tebexPackages.isEmpty()) {
                player.sendMessage(Chat.severe("You have no unclaimed packages at this time."));
                return;
            }

            final ChestGui gui = buildGUI(tebexPackages);
            gui.show(player);
        });
    }

    private ChestGui buildGUI(final List<TebexPackage> packages) {
        final ChestGui gui = new ChestGui((packages.size() <= 3) ? 3 : 5, "BuyCraft Package Claim");
        gui.setOnGlobalClick(event -> {
            event.setCancelled(true);

            final ItemStack item = event.getCurrentItem();
            if (item != null && item.getType() == Material.CHEST) {
                final String value = item.getItemMeta().getPersistentDataContainer().get(TebexPackage.ITEM_KEY, PersistentDataType.STRING);
                final TebexPackage tebexPackage = Serialization.deserialize(value);

                plugin.getPackageManager().processConfirmPackage((Player) event.getWhoClicked(), tebexPackage, event.getClick());
            }
        });

        final OutlinePane pane = new OutlinePane(0, 0, gui.getRows() * 9, gui.getRows());
        for (int i = 0; i < Math.min(packages.size(), gui.getRows() * 9); i++) {
            final TebexPackage tebexPackage = packages.get(i);
            pane.addItem(new GuiItem(tebexPackage.toDisplayStack()));
        }

        gui.addPane(pane);
        return gui;
    }
}
