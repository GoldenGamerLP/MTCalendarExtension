package me.alex.mtcalendarextension.container;

import me.alex.mtcalendarextension.inventories.CalendarInventoryHolder;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class InventoryContainer extends ArrayList<CalendarInventoryHolder> {

    public InventoryContainer() {
        super();
    }

    public Optional<CalendarInventoryHolder> get(UUID uuid) {
        return this.stream().filter(holder -> holder.getUUID().equals(uuid)).findFirst();
    }
}
