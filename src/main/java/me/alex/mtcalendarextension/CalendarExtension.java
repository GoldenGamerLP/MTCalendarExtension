package me.alex.mtcalendarextension;

import me.alex.mtcalendarextension.commands.CalendarCommand;
import me.alex.mtcalendarextension.container.InventoryContainer;
import me.alex.mtcalendarextension.inventories.CalendarInventory;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extensions.Extension;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CalendarExtension extends Extension {

    private final InventoryContainer container = new InventoryContainer();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final me.alex.mtcalendarextension.inventories.CalendarInventory inventory = new CalendarInventory(this);

    @Override
    public void initialize() {
        this.getLogger().info("Calendar Extension loaded!");
        MinecraftServer.getCommandManager().register(new CalendarCommand("cl", this));
    }

    @Override
    public void terminate() {

        container.clear();
        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    public InventoryContainer getContainer() {
        return container;
    }

    public CalendarInventory getInventory() {
        return inventory;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
}
