package me.alex.mtcalendarextension.builder;

import me.alex.mtcalendarextension.inventories.CalendarInventoryHolder;

import java.time.LocalDate;
import java.util.UUID;

public class CalendarInventoryHolderBuilder {

    private final UUID uuid;
    private LocalDate date;
    private Boolean aligned;

    public CalendarInventoryHolderBuilder(UUID uuid) {
        this.uuid = uuid;
    }

    public CalendarInventoryHolderBuilder setLocaleDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public CalendarInventoryHolderBuilder setAligned(Boolean aligned) {
        this.aligned = aligned;
        return this;
    }

    public CalendarInventoryHolder build() {
        if (aligned == null) aligned = true;
        if (date == null) date = LocalDate.now();
        return new CalendarInventoryHolder(uuid, date, aligned);
    }


}
