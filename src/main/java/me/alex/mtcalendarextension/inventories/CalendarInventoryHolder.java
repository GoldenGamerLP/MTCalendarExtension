package me.alex.mtcalendarextension.inventories;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;

import java.time.LocalDate;
import java.util.UUID;

public class CalendarInventoryHolder {

    public final Inventory inventory;
    private final UUID player;
    private LocalDate localDate;
    private Boolean aligned;

    public CalendarInventoryHolder(UUID player, LocalDate localDate, Boolean aligned) {
        this.player = player;
        this.localDate = localDate;
        this.aligned = aligned;
        this.inventory = new Inventory(InventoryType.CHEST_6_ROW, "<title> (<month>)");
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public Player getPlayer() {
        return MinecraftServer.getConnectionManager().getPlayer(player);
    }

    public UUID getUUID() {
        return player;
    }

    public Boolean getAligned() {
        return aligned;
    }

    public void setAligned(Boolean aligned) {
        this.aligned = aligned;
    }

    public void open() {
        getPlayer().openInventory(this.inventory);
    }
}
