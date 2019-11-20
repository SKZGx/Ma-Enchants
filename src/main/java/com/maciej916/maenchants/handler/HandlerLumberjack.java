package com.maciej916.maenchants.handler;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.maciej916.maenchants.client.Keys;
import com.maciej916.maenchants.network.Networking;
import com.maciej916.maenchants.network.PacketLumberjack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.world.BlockEvent;

import static com.maciej916.maenchants.init.ModEnchants.LUMBERJACK;

public class HandlerLumberjack {

    private static HashMultimap<PlayerEntity, BlockPos> treeMap = HashMultimap.create();

    @OnlyIn(Dist.CLIENT)
    public static void handlerBreak(BlockEvent.BreakEvent event) {
        PlayerEntity player = event.getPlayer();
        if (EnchantmentHelper.getMaxEnchantmentLevel(LUMBERJACK, player) == 0) return;

        BlockState state = event.getState();
        Block block = state.getBlock();
        if (!block.isIn(BlockTags.LOGS)) return;

        World world = (World) event.getWorld();
        BlockPos pos = event.getPos();

        if (Keys.excavateKey.isKeyDown()) {
            findTree(player, world, pos, state);
            if (treeMap.get(player).size() < 512) {
                Networking.INSTANCE.sendToServer(new PacketLumberjack(pos));
                event.setCanceled(true);
            } else {
                treeMap.clear();
            }
        }
    }

    public static void doBreak(PlayerEntity player, World world, BlockPos pos) {
        int logsBreak = 0;
        Block block = world.getBlockState(pos).getBlock();
        ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);
        for (BlockPos point : ImmutableSet.copyOf(treeMap.get(player))) {
            if (stack.getDamage() > 0) {
                logsBreak++;
                world.setBlockState(point, Blocks.AIR.getDefaultState());
                stack.damageItem(1, player, playerEntity -> playerEntity.sendBreakAnimation(player.getActiveHand()));
            } else {
                break;
            }
        }

        ItemEntity item = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(block, logsBreak));
        world.addEntity(item);

        treeMap.clear();
    }

    private static void findTree(PlayerEntity player, World world, BlockPos mine, BlockState state) {
        for (int offsetX = -1; offsetX <= 1; offsetX++) {
            for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
                for (int offsetY = -1; offsetY <= 1; offsetY++) {
                    BlockPos newPoint = mine.add(offsetX, offsetY, offsetZ);
                    if (treeMap.containsEntry(player, newPoint)) continue;

                    BlockState newBlockState = world.getBlockState(newPoint);
                    boolean isLeaves = newBlockState.getMaterial() == Material.LEAVES;

                    if (newBlockState.getBlock() == state.getBlock() && !isLeaves) {
                        treeMap.put(player, newPoint);
                        findTree(player, world, newPoint, state);
                    }
                }
            }
        }
    }

}