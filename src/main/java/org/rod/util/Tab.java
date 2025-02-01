package org.rod.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.monitoring.TickMonitor;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.MathUtils;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicReference;

public class Tab {

    private final AtomicReference<TickMonitor> lastTick = new AtomicReference<>();
    private final Deque<Double> tickTimesLast10Sec = new ArrayDeque<>();
    private final Deque<Double> tickTimesLast1Min = new ArrayDeque<>();
    private final Deque<Double> tickTimesLast1Hour = new ArrayDeque<>();

    private static final int SECONDS_10 = 10;
    private static final int SECONDS_60 = 60;
    private static final int SECONDS_3600 = 3600;

    public void initialize() {
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        EventNode<Event> allNode = EventNode.all("all");
        globalEventHandler.addChild(allNode);

        // Monitoring
        allNode.addListener(EventListener.builder(ServerTickMonitorEvent.class)
                .handler(event -> lastTick.set(event.getTickMonitor()))
                .build());

        // Header/footer
        MinecraftServer.getSchedulerManager().buildTask(() -> {
            Collection<Player> players = MinecraftServer.getConnectionManager().getOnlinePlayers();
            if (players.isEmpty()) return;

            final Runtime runtime = Runtime.getRuntime();
            final TickMonitor tickMonitor = lastTick.get();
            final long ramUsage = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
            final double tps = getTps();
            final double tps10Sec = getAverageTps(tickTimesLast10Sec, SECONDS_10);
            final double tps1Min = getAverageTps(tickTimesLast1Min, SECONDS_60);
            final double tps1Hour = getAverageTps(tickTimesLast1Hour, SECONDS_3600);

            final Component header = Component.newline()
                    .append(Component.text("RAM USAGE: " + ramUsage + " MB", NamedTextColor.GRAY).append(Component.newline())
                            .append(Component.text("TICK TIME: " + MathUtils.round(tickMonitor.getTickTime(), 2) + "ms", NamedTextColor.GRAY)).append(Component.newline())
                            .append(Component.text("TPS: " + MathUtils.round(tps, 2), NamedTextColor.GRAY)).append(Component.newline())
                            .append(Component.text("TPS (10s): " + MathUtils.round(tps10Sec, 2), NamedTextColor.GRAY)).append(Component.newline())
                            .append(Component.text("TPS (1m): " + MathUtils.round(tps1Min, 2), NamedTextColor.GRAY)).append(Component.newline())
                            .append(Component.text("TPS (1h): " + MathUtils.round(tps1Hour, 2), NamedTextColor.GRAY))).append(Component.newline());

            final Component footer = Component.newline()
                    .append(Component.text("          VoidStorm Test          ")
                            .color(TextColor.color(57, 200, 73))
                            .append(Component.newline()));

            Audiences.players().sendPlayerListHeaderAndFooter(header, footer);
        }).repeat(TaskSchedule.tick(10)).schedule();
    }

    private double getTps() {
        TickMonitor tickMonitor = lastTick.get();
        if (tickMonitor != null) {
            double tickTime = tickMonitor.getTickTime();


            // Ensure tick time is within a reasonable range (e.g., 0.05 ms to 50 ms)
            if (tickTime > 0.05 && tickTime < 50) {
                // Calculating TPS from the raw tick time (ms)
                double tps = 1000.0 / tickTime;

                // Ensuring TPS stays within the range 0 to 20
                return Math.min(20.0, Math.max(0.0, tps));
            }
        }
        return 0.0;
    }

    private double getAverageTps(Deque<Double> tickTimes, int maxSize) {
        TickMonitor tickMonitor = lastTick.get();
        if (tickMonitor != null) {
            double tickTime = tickMonitor.getTickTime();
            double tps = tickTime > 0 ? 1000.0 / tickTime : 0.0;
            tickTimes.add(tps);
            if (tickTimes.size() > maxSize) {
                tickTimes.poll();
            }

            // Calculate the average of the stored TPS values
            double averageTps = tickTimes.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            // Ensuring average TPS stays within the range 0 to 20
            return Math.min(20.0, Math.max(0.0, averageTps));
        }
        return 0.0;
    }
}