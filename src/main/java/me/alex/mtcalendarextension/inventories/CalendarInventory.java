package me.alex.mtcalendarextension.inventories;

import me.alex.mtcalendarextension.CalendarExtension;
import me.alex.mtcalendarextension.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class CalendarInventory {

    protected final Tag<String> tag = Tag.String("inv");
    private final LocalDate currentDate = LocalDate.now(ZoneId.of("Europe/Berlin"));
    private final ItemUtils utils = new ItemUtils();
    private final MiniMessage miniMessage = MiniMessage.get();
    private final String titleLayout = "<title> (<month>)";
    private final String currentMonthTitleLayout = "<title> <gradient:blue:dark_green:yellow>(This Month)</gradient>";
    private final String itemLayout = "<gradient:white:dark_gray><italic><dayanddaynumber></italic></gradient>";
    private final String changeLayout = "<gradient:gold:yellow>Change the layout to: <layout>";
    private final String currentDayItemLayout = "<gradient:white:dark_gray><italic><dayanddaynumber></italic></gradient> <gradient:blue:dark_green:yellow>(This Day)</gradient>";
    private final String arrowTitle = "<gradient:gray:white:black>Month <key></gradient>";
    private final String noChangeMonthLoreLayout = "<gray>-</gray> <gradient:dark_red:red>You cant go <key> a Month fourther!</gradient>";
    private final ItemStack arrowUP = utils.skullOf("MHF_ARROWUP").withDisplayName(miniMessage.parse(arrowTitle, Template.of("key", "+1")));
    private final ItemStack arrowDOWN = utils.skullOf("MHF_ARROWDOWN").withDisplayName(miniMessage.parse(arrowTitle, Template.of("key", "-1")));
    private final ItemStack exclamation = utils.skullOf("MHF_EXCLAMATION");
    private final DateTimeFormatter fullDateTimeFormatter = DateTimeFormatter.ofPattern("EE d. - MMMM - yyyy");
    private final ExecutorService service;

    public CalendarInventory(CalendarExtension extension) {
        this.service = extension.getExecutorService();
    }

    public void initInventory(CalendarInventoryHolder uuid) {
        service.execute(() -> {
            uuid.inventory.addInventoryCondition((player, slot, clickType, result) -> {
                if (!result.getClickedItem().hasTag(tag)) return;
                result.setCancel(true);
                service.execute(() -> {
                    switch (result.getClickedItem().hasTag(tag) ? result.getClickedItem().getTag(tag) : "") {
                        case "ly":
                            player.sendMessage("ly");
                            this.updateLayout(uuid);
                            this.recalculateDate(uuid);
                            break;
                        case "+1":
                            player.sendMessage("+1 month");
                            uuid.setLocalDate(uuid.getLocalDate().plusMonths(1));
                            this.recalculateDate(uuid);
                            this.updateArrows(uuid);
                            this.updateLayout(uuid);
                            uuid.getPlayer().sendMessage(uuid.getLocalDate().getMonth().getValue() + "+");
                        case "-1":
                            player.sendMessage("-1 month");
                            uuid.setLocalDate(uuid.getLocalDate().minusMonths(1));
                            this.recalculateDate(uuid);
                            this.updateArrows(uuid);
                            this.updateLayout(uuid);
                            uuid.getPlayer().sendMessage(uuid.getLocalDate().getMonth().getValue() + "-");
                    }
                });
            });


            for (int i = 0; i < 6; i++) {
                uuid.inventory.setItemStack(i * 9 + 7, ItemStack.of(Material.BLACK_STAINED_GLASS_PANE).withDisplayName(Component.empty()));
            }

            this.recalculateDate(uuid);
            this.updateArrows(uuid);
            this.updateLayout(uuid);
        });
    }

    public void updateArrows(CalendarInventoryHolder uuid) {
        boolean lastYear = uuid.getLocalDate().plusMonths(1).getYear() == currentDate.getYear();
        boolean nextYear = uuid.getLocalDate().minusMonths(1).getYear() == currentDate.getYear();
        boolean lastYearArrowUpdate = !uuid.inventory.getItemStack(8).hasTag(tag) || nextYear;
        boolean nextYearArrowUpdate = !uuid.inventory.getItemStack(uuid.inventory.getSize() - 1).hasTag(tag) || lastYear;
        if (lastYearArrowUpdate)
            uuid.inventory.setItemStack(8, lastYear ? arrowUP.withTag(tag, "+1") : arrowUP.withLore(Collections.singletonList(miniMessage.parse(noChangeMonthLoreLayout, Template.of("key", "+1")))));
        if (nextYearArrowUpdate)
            uuid.inventory.setItemStack(uuid.inventory.getSize() - 1, nextYear ? arrowDOWN.withTag(tag, "-1") : arrowDOWN.withLore(Collections.singletonList(miniMessage.parse(noChangeMonthLoreLayout, Template.of("key", "-1")))));
    }

    public void updateLayout(CalendarInventoryHolder uuid) {
        uuid.setAligned(!uuid.getAligned());
        uuid.inventory.setItemStack(uuid.inventory.getInnerSize() / 2 + 8, exclamation.withDisplayName(miniMessage.parse(changeLayout, Template.of("layout", uuid.getAligned() ? "Aligned" : "Not Aligned"))).withTag(tag, "ly"));
    }

    public void recalculateDate(CalendarInventoryHolder uuid) {
        final LinkedHashSet<LocalDate> dates = new LinkedHashSet<>();
        List<LocalDate> currentDays = uuid.getLocalDate().with(TemporalAdjusters.firstDayOfMonth()).datesUntil(uuid.getLocalDate().with(TemporalAdjusters.lastDayOfMonth())).toList();

        if(uuid.getAligned()) {
            List<LocalDate> firstDays = uuid.getLocalDate().with(TemporalAdjusters.firstDayOfMonth()).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).datesUntil(uuid.getLocalDate().with(TemporalAdjusters.firstDayOfMonth())).toList();
            List<LocalDate> lastDays = uuid.getLocalDate().with(TemporalAdjusters.lastDayOfMonth()).datesUntil(uuid.getLocalDate().with(TemporalAdjusters.lastDayOfMonth()).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))).toList();
            dates.addAll(firstDays);
            dates.addAll(currentDays);
            dates.addAll(lastDays);
        } else dates.addAll(currentDays);

        AtomicInteger slot = new AtomicInteger(0);

        /*for (int i = 0; i < 42; i++) {
            if ((slot.get() + 2) % 9 == 0) {
                slot.addAndGet(2);
            }
            uuid.inventory.setItemStack(slot.getAndIncrement(), ItemStack.AIR);
        }

        slot.set(0);*/
        final ItemStack[] stacks = uuid.inventory.getItemStacks();
        final Random random = new Random();
        dates.stream().limit(42).forEach(localDate -> {
            if ((slot.get() + 2) % 9 == 0) {
                slot.addAndGet(2);
            }

            ItemStack item;

            if (uuid.getAligned() && localDate.getMonth().equals(uuid.getLocalDate().minusMonths(1).getMonth()))
                item = ItemStack.of(Material.ORANGE_STAINED_GLASS_PANE);
            else if (uuid.getAligned() && localDate.getMonth().equals(uuid.getLocalDate().plusMonths(1).getMonth()))
                item = ItemStack.of(Material.GREEN_STAINED_GLASS_PANE);
            else if (localDate.equals(currentDate)) {
                item = ItemStack.of(Material.BLUE_STAINED_GLASS_PANE);
            } else item = ItemStack.of(Material.WHITE_STAINED_GLASS_PANE);

            stacks[slot.getAndIncrement()] = item.withAmount(localDate.getDayOfMonth()).withDisplayName(Component.text(localDate.format(fullDateTimeFormatter))).withLore(Collections.singletonList(Component.text(random.nextInt())));
        });

        uuid.inventory.copyContents(stacks);
            /*for (LocalDate date : dates) {


                ItemStack stack;

                if(currentDate.getMonth().equals(date.minusMonths(1).getMonth())) stack = ItemStack.of(Material.ORANGE_STAINED_GLASS_PANE);
                else if(currentDate.getMonth().equals(date.plusMonths(1).getMonth())) stack = ItemStack.of(Material.GREEN_STAINED_GLASS_PANE);
                else stack = ItemStack.of(Material.WHITE_STAINED_GLASS_PANE);

                uuid.inventory.setItemStack(slot.getAndIncrement(), stack.withDisplayName(Component.text(date.format(fullDateTimeFormatter))));
                uuid.getPlayer().sendMessage(" " + slot.get());
            }*/
        uuid.inventory.setTitle(miniMessage.parse(uuid.getLocalDate().isEqual(currentDate) ? currentMonthTitleLayout : titleLayout, Template.of("title", uuid.getLocalDate().format(fullDateTimeFormatter.withLocale(uuid.getPlayer().getLocale()))), Template.of("month", Component.text(uuid.getLocalDate().getMonth().getValue()))));
        uuid.inventory.update();
    }

}
