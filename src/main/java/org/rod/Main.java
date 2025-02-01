package org.rod;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.*;
import net.minestom.server.event.*;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.time.TimeUnit;
import org.rod.commands.gamemode.gmc;
import org.rod.commands.gamemode.gms;
import org.rod.commands.gamemode.gmsp;
import org.rod.commands.heal;
import org.rod.commands.test;
import org.rod.util.Tab;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    public static void main(String[] args) {


        // Initialize the server
        MinecraftServer server = MinecraftServer.init();

        // Create an instance manager
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();

        // Create an instance container (world)
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer(new AnvilLoader("world"));

        // Set the world generator
        instanceContainer.setGenerator(event -> {
            event.modifier().fillHeight(0, 70, Block.GRASS_BLOCK);
        });

        // Set the chunk supplier for lighting
        instanceContainer.setChunkSupplier(LightingChunk::new);
        instanceContainer.enableAutoChunkLoad(true);

        // Create a global event handler
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();

        // Create an event node for all events
        EventNode<Event> allNode = EventNode.all("all");
        EventNode<?> playerNode = EventNode.type("players", EventFilter.PLAYER);

        // Add a listener for player configuration events
        allNode.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            // Set the spawning instance and respawn point
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0, 72, 0));
            player.setGameMode(GameMode.CREATIVE);
            player.getInventory().addItemStack(ItemStack.of(Material.BAMBOO_PLANKS, 64));
        });


        // Add a listener for block break events
        allNode.addListener(PlayerBlockBreakEvent.class, event -> {
            var material = event.getBlock().registry().material();
            // Check if the material is not null
            if (material != null) {
                // Create an ItemStack from the material
                var itemstack = ItemStack.of(material);
                // Create an ItemEntity from the ItemStack
                ItemEntity itemEntity = new ItemEntity(itemstack);

                // Randomize the block spawn position
                double x = ThreadLocalRandom.current().nextDouble(0.1, 0.9);
                double y = ThreadLocalRandom.current().nextDouble(0.1, 0.9);
                double z = ThreadLocalRandom.current().nextDouble(0.1, 0.9);

                // Set the instance and position of the ItemEntity
                itemEntity.setInstance(event.getInstance(), event.getBlockPosition().add(x, y, z));
                // Set a pickup delay of 500 milliseconds for the ItemEntity
                itemEntity.setPickupDelay(Duration.ofMillis(500));


                //Debug learn
                var message = Component.text("Broke a block bruh")
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.UNDERLINED, TextDecoration.BOLD)
                        .appendNewline()
                        .append(Component.text("You got: ")
                                .append(Component.text(material.name()).color(NamedTextColor.YELLOW)))
                        .hoverEvent(Component.text("You are sus"));
                event.getPlayer().sendMessage(message);
            }
        });

        // Add a listener for item pickup events with a eventListener Builder very cool
        allNode.addListener(EventListener.builder(PickupItemEvent.class)
                .handler(event -> {
                    var itemStack = event.getItemStack();
                    // Check if the entity picking up the item is a player
                    if (event.getLivingEntity() instanceof Player player) {
                        // Add the item to the player's inventory
                        player.getInventory().addItemStack(itemStack);
                        //Debug
                        System.out.println("Picked up");

                    }

                }).build());


        //Item Drop Event
        allNode.addListener(ItemDropEvent.class, event -> {
            //get the itemstack from the event
            ItemEntity itemEntity = new ItemEntity(event.getItemStack());
            //we get the instance of where the item is dropped and we set it as our itementity
            itemEntity.setInstance(event.getInstance(), event.getPlayer().getPosition());
            //we set the velocity of the item based on where the player is
            itemEntity.setVelocity(event.getPlayer().getPosition().direction().add(0, 1.8, 0).mul(4));
            //0.5 seconds of pickup delay
            itemEntity.setPickupDelay(Duration.ofMillis(350));
            itemEntity.setMergeRange(1.5f);
        });

        // Add the event node to the global event handler
        globalEventHandler.addChild(allNode);
        globalEventHandler.addChild(playerNode);

        // Save the world
        var Scheduler = MinecraftServer.getSchedulerManager();

        Scheduler.buildShutdownTask(() -> {
            CompletableFuture<Void> saveTasks = CompletableFuture.allOf(
                    instanceContainer.saveChunksToStorage(),
                    instanceContainer.saveInstance()
            );

            instanceManager.getInstances().forEach(instance -> {
                saveTasks.thenCompose(v -> instance.saveInstance());
            });

            saveTasks.join();
            System.out.println("World saved....................");
        });

        Scheduler.buildTask(() -> {

                    instanceManager.getInstances().forEach(instance ->
                            instance.saveChunksToStorage()
                    );
                })
                .repeat(30, TimeUnit.MINUTE)
                .schedule();


        //Testing
        Entity zombie = new LivingEntity(EntityType.ZOMBIE);
        zombie.setInstance(instanceContainer, new Pos(0.5, 72, 0.5));


        // Commands
        {
            CommandManager manager = MinecraftServer.getCommandManager();
            manager.setUnknownCommandCallback((sender, contex) -> sender.sendMessage("Command not found."));
        }


        //Initialize commands
        MinecraftServer.getCommandManager().register(new test());
        //Heal
        MinecraftServer.getCommandManager().register(new heal());
        //Gamemodes
        MinecraftServer.getCommandManager().register(new gmc());
        MinecraftServer.getCommandManager().register(new gms());
        MinecraftServer.getCommandManager().register(new gmsp());
        Tab tabInstance = new Tab();
        tabInstance.initialize();


        // Initialize Mojang authentication and start the server
        MojangAuth.init();
        server.start("0.0.0.0", 25565);


    }
}