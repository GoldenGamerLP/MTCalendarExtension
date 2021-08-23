package me.alex.mtcalendarextension.utils;

import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.PlayerHeadMeta;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ItemUtils {

    private final ConcurrentHashMap<String, ItemStack> headCache = new ConcurrentHashMap<>();

    public ItemUtils() {
    }

    public ItemStack skullOf(String name) {
        return headCache.computeIfAbsent(name, s -> ItemStack.of(Material.PLAYER_HEAD).with(itemStackBuilder -> itemStackBuilder.meta(new PlayerHeadMeta.Builder().playerSkin(PlayerSkin.fromUsername(name)).skullOwner(UUID.randomUUID()).build())));
    }

}
