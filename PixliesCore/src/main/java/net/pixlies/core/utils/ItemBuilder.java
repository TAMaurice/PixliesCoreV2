package net.pixlies.core.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {

    private final ItemStack item;
    private final List<String> lore = new ArrayList<>();
    private final ItemMeta meta;

    public ItemBuilder(Material material, int amount) {
        item = new ItemStack(material, amount);
        meta = item.getItemMeta();
    }

    public ItemBuilder(ItemStack item) {
        this.item = item;
        meta = item.getItemMeta();
    }

    public ItemBuilder(Material material) {
        item = new ItemStack(material, 1);
        meta = item.getItemMeta();
    }

    public ItemBuilder setAmount(int value) {
        item.setAmount(value);
        return this;
    }

    public ItemBuilder setDamage(int value) {
        ((Damageable) meta).setDamage(value);
        return this;
    }

    public ItemBuilder setNoName() {
        meta.displayName(Component.text(" "));
        return this;
    }

    public ItemBuilder setDisplayName(String name) {
        meta.displayName(Component.text(name));
        return this;
    }

    public ItemBuilder setGlow() {
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder setGlow(boolean state) {
        if (state) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public ItemBuilder addLoreLine(String string) {
        lore.add(string);
        return this;
    }

    public ItemBuilder removeLoreLine(String string) {
        lore.remove(string);
        return this;
    }

    public ItemBuilder addLoreArray(String[] strings) {
        List<String> list = new ArrayList<>(Arrays.asList(strings));
        lore.addAll(list);
        return this;
    }

    public ItemBuilder addLoreAll(List<String> list) {
        lore.addAll(list);
        return this;
    }

    public ItemBuilder setSkullOwner(String owner) {
        ((SkullMeta) meta).setOwningPlayer(Bukkit.getOfflinePlayerIfCached(owner));
        return this;
    }

    public ItemBuilder setSkullOwner(UUID uuid) {
        ((SkullMeta) meta).setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        return this;
    }

    public ItemBuilder setColor(Color color) {
        ((LeatherArmorMeta) meta).setColor(color);
        return this;
    }

    public ItemBuilder setBook(Enchantment enchantment, int level) {
        ((EnchantmentStorageMeta) meta).addStoredEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean value) {
        meta.setUnbreakable(value);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag flag) {
        meta.addItemFlags(flag);
        return this;
    }

    /**
     * Builds the ItemStack that you made.
     * @return the item that is built.
     */
    public @NotNull ItemStack build() {
        if (!this.lore.isEmpty()) {
            List<Component> lore = new ArrayList<>();
            this.lore.forEach(s -> {
                lore.add(Component.text(s));
            });
            meta.lore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Clones the ItemBuilder
     * @return A new ItemBuilder
     */
    public @NotNull ItemBuilder copy() {
        return new ItemBuilder(this.build());
    }

}
