package com.suzuran_ss.bloodcraft_ss.items;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper; // Add this import
import net.minecraft.world.InteractionHand; // Add this import if needed for broadcastBreakEvent
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CustomScytheItem extends SwordItem {
    private static final Logger LOGGER = LogManager.getLogger();

    private final float extraDamagePercentage;

    public CustomScytheItem(Tier tier, int damage, float speed, float extraPercentage, Properties properties) {
        super(tier, damage, speed, properties);
        this.extraDamagePercentage = extraPercentage;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        LOGGER.debug("CustomScytheItem.hurtEnemy called! Target: {}, Attacker: {}", target.getName().getString(), attacker.getName().getString());

        float currentHealthBefore = target.getHealth();
        float targetMaxHealth = target.getMaxHealth();

        double baseAttackDamage = attacker.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue();

        double enchantmentBonus = EnchantmentHelper.getDamageBonus(stack, target.getMobType());

        float extraDamage = targetMaxHealth * this.extraDamagePercentage;

        float totalDamage = (float) (baseAttackDamage + enchantmentBonus) + extraDamage;

        float cooldown = 1.0F;
        if (attacker instanceof Player player) {
            cooldown = player.getAttackStrengthScale(0.5F);
        }
        float finalDamage = totalDamage * cooldown;

        LOGGER.debug("Base Attack Damage: {}", baseAttackDamage);
        LOGGER.debug("Enchantment Bonus: {}", enchantmentBonus);
        LOGGER.debug("Calculating extra damage: {} ({}% of target's max health {})", extraDamage, this.extraDamagePercentage * 100, targetMaxHealth);
        LOGGER.debug("Calculated total damage to apply: {} (Base: {} + Enchant: {} + Extra: {})", totalDamage, (float)baseAttackDamage, (float)enchantmentBonus, extraDamage);

        DamageSource damageSource = attacker.level().damageSources().playerAttack((Player) attacker);

        boolean hurtResult = target.hurt(damageSource, finalDamage);

        float healthAfterOurDamage = target.getHealth();
        LOGGER.debug("Health after applying calculated total damage ({}): {}. Hurt result: {}", finalDamage, healthAfterOurDamage, hurtResult);

        stack.hurtAndBreak(2, attacker, (entity) -> {
            if (entity instanceof Player playerEntity) {
                playerEntity.broadcastBreakEvent(InteractionHand.MAIN_HAND); // 或根据实际使用的手
            }
        });

        LOGGER.debug("Net damage from this attack (before - after): {}", currentHealthBefore - healthAfterOurDamage);

        return hurtResult;
    }
}