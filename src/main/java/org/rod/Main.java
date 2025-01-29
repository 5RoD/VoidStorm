package org.rod;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {

        //Init server
        MinecraftServer server = MinecraftServer.init();

        //instance = world
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

        //World Generator
        instanceContainer.setGenerator(generationUnit -> {
            generationUnit.modifier().fillHeight(0, 70, Block.GRAY_WOOL);
        });

        //Light Generator
        instanceContainer.setChunkSupplier(LightingChunk::new);


        //Player Spawning
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();

        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, asyncPlayerConfigurationEvent -> {
            final Player player = asyncPlayerConfigurationEvent.getPlayer();
            asyncPlayerConfigurationEvent.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0, 72, 0));

        });

        //Block Break event
        globalEventHandler.addListener(PlayerBlockBreakEvent.class,playerBlockBreakEvent -> {

            //TESTING
            System.out.println(playerBlockBreakEvent.getPlayer().getUsername() + " Broke a block");


            var material = playerBlockBreakEvent.getBlock().registry().material();
            // Check if the material is not null
            if (material != null) {

                // Create an ItemStack from the material
                var itemstack = ItemStack.of(material);
                // Create an ItemEntity from the ItemStack
                ItemEntity itemEntity = new ItemEntity(itemstack);
                // Set the instance and position of the ItemEntity to the block's position


                //Random Block spawn place
                double x = ThreadLocalRandom.current().nextDouble(0.1, 0.9);
                double y = ThreadLocalRandom.current().nextDouble(0.1, 0.9);
                double z = ThreadLocalRandom.current().nextDouble(0.1, 0.9);

                itemEntity.setInstance(playerBlockBreakEvent.getInstance(), playerBlockBreakEvent.getBlockPosition().add(x, y, z));
                // Set a pickup delay of 500 milliseconds for the ItemEntity
                itemEntity.setPickupDelay(Duration.ofMillis(500));




            }
        } );

        globalEventHandler.addListener(PickupItemEvent.class, pickUpEvent -> {






        });


        //Server start and enable online mode
        MojangAuth.init();
        server.start("0.0.0.0", 25565);

    }
}