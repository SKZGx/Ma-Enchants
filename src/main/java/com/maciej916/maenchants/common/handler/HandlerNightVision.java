package com.maciej916.maenchants.common.handler;

import com.maciej916.maenchants.common.capabilities.mod.IModCapability;
import com.maciej916.maenchants.common.util.PlayerUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import static com.maciej916.maenchants.common.registries.ModEnchants.NIGHT_VISION;

public class HandlerNightVision {

    public static void handlerPlayerTick(Player player) {
        IModCapability enchantsCap = PlayerUtil.getAliveEnchantsCapability(player);
        if (enchantsCap == null) return;

        ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
        int lvl = EnchantmentHelper.getItemEnchantmentLevel(NIGHT_VISION, stack);

        if (lvl == 0) {
            if (enchantsCap.getNightVision()) {
                player.removeEffect(MobEffects.NIGHT_VISION);
                enchantsCap.setNightVision(false);
            }
        } else {
            if (!enchantsCap.getNightVision()) {
                MobEffectInstance effect = new MobEffectInstance(MobEffects.NIGHT_VISION, 1000000, 100, false, false);
                effect.setNoCounter(true);
                player.addEffect(effect);
                enchantsCap.setNightVision(true);
            }
        }
    }

}

