package me.friedwingis.plugin.orbittc.struct;

import me.friedwingis.plugin.orbitcore.objects.ItemBuilder;
import me.friedwingis.plugin.orbittc.utils.Encryptor;
import me.friedwingis.plugin.orbittc.utils.Serialization;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.UUID;

/**
 * Copyright FriedWingis - 2023
 * All code is private and not to be used by any
 * other entity unless explicitly stated otherwise.
 **/
public class TebexPackage implements Serializable {
    public static final NamespacedKey ITEM_KEY = new NamespacedKey("orbittc", "ipackage_f");

    public final UUID r;
    public final String transactionID,
            packageName,
            ipEncrypted,
            purchaserName,
            purchaserId;

    public TebexPackage(final String transactionID, final String packageName, final String ipEncrypted, final String purchaserName, final String purchaserId) throws GeneralSecurityException {
        this.r = UUID.randomUUID();
        this.transactionID = transactionID;
        this.packageName = packageName;
        this.ipEncrypted = Encryptor.encrypt(ipEncrypted);
        this.purchaserName = purchaserName;
        this.purchaserId = purchaserId;
    }

    public ItemStack toDisplayStack() {
        return new ItemBuilder(Material.CHEST)
                .setDisplayName("<white><b>" + packageName)
                .setLore("<gray><i>Click to claim this shop package.",
                        "",
                        "<red><b>WARNING:</b> <gray>By accepting this package",
                        "<gray>you accept full responsibility for the",
                        "<gray>payment and will be banned if it is",
                        "<gray>flagged as fraudulent by PayPal.",
                        "",
                        "<red>If you do <b>not</b> wish to accept",
                        "<red>this package, right-click it.",
                        "",
                        "<gray>" + transactionID)
                .addPersistentData(ITEM_KEY, PersistentDataType.STRING, Serialization.serialize(this))
                .build();
    }

    @Override
    public String toString() {
        return "(id:" + transactionID + ",package:" + packageName + ",ipEncrypted:" + ipEncrypted + ",purchaserName:" + purchaserName + ",purchaserId:" + purchaserId + ")";
    }
}
