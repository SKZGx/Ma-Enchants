package com.maciej916.maenchants.handler;

import com.maciej916.maenchants.capabilities.IEnchants;
import com.maciej916.maenchants.utils.PlayerUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

import static com.maciej916.maenchants.init.ModEnchants.NIGHT_VISION;

public class HandlerNightVision {

    public static void handlerPlayerTick(PlayerEntity player) {
        if (!player.isAlive()) return;

        int lvl = EnchantmentHelper.getMaxEnchantmentLevel(NIGHT_VISION, player);
        IEnchants enchantsCap = PlayerUtil.getEnchantsCapability(player);

        if (lvl == 0) {
            if (enchantsCap.getNightVision()) {
                player.removePotionEffect(Effects.NIGHT_VISION);
                enchantsCap.setNightVision(false);
            }
        } else {
            if (!enchantsCap.getNightVision()) {
                EffectInstance effectinstance = new EffectInstance(Effects.NIGHT_VISION, 1000000, 100, false, false);
                effectinstance.setPotionDurationMax(true);
                player.addPotionEffect(effectinstance);
                enchantsCap.setNightVision(true);
            }
        }
    }

}

