package org.rod;

import net.kyori.adventure.sound.Sound;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.*;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import org.rod.commands.test;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    public static void main(String[] args) {







        // Initialize the server
        MinecraftServer server = MinecraftServer.init();

        // Create an instance manager
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();

        // Create an instance container (world)
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

        // Set the world generator
        instanceContainer.setGenerator(event -> {
            event.modifier().fillHeight(0, 70, Block.GRASS_BLOCK);
        });

        // Set the chunk supplier for lighting
        instanceContainer.setChunkSupplier(LightingChunk::new);

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
            itemEntity.setVelocity(event.getPlayer().getPosition().direction().add(0,1.8,0).mul(4));
            //0.5 seconds of pickup delay
            itemEntity.setPickupDelay(Duration.ofMillis(500));
            itemEntity.setMergeRange(1.5f);
            event.getPlayer().playSound(Sound.sound(SoundEvent.DROP));
        });

        // Add the event node to the global event handler
        globalEventHandler.addChild(allNode);
        globalEventHandler.addChild(playerNode);



       //Initialize commands
        MinecraftServer.getCommandManager().register(new test());


        // Initialize Mojang authentication and start the server
        MojangAuth.init();
        server.start("0.0.0.0", 25565);
    }
}