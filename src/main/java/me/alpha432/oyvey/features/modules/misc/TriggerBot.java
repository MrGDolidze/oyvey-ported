import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class TriggerBot {
    // Attack delay in ticks (1 tick = 0.05s). 
    // 10 ticks = 0.5s delay between hits to look more natural.
    private static int attackDelay = 0;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Only run if we are in-game and not in a menu
            if (client.player == null || client.world == null || client.currentScreen != null) return;

            // Check what is under the crosshair
            HitResult hit = client.crosshairTarget;

            if (hit != null && hit.getType() == HitResult.Type.ENTITY) {
                Entity target = ((EntityHitResult) hit).getEntity();

                // Only attack living things (Players/Mobs) that aren't dead
                if (target instanceof LivingEntity && ((LivingEntity) target).isAlive()) {
                    
                    // Attack cooldown check: ensure the attack is fully charged for max damage
                    if (client.player.getAttackCooldownProgress(0.5f) >= 1.0f && attackDelay <= 0) {
                        
                        // 1. Tell the server we are attacking
                        client.interactionManager.attackEntity(client.player, target);
                        
                        // 2. Play the arm swing animation
                        client.player.swingHand(Hand.MAIN_HAND);
                        
                        // Reset our safety delay
                        attackDelay = 5; 
                    }
                }
            }

            // Countdown the delay
            if (attackDelay > 0) attackDelay--;
        });
    }
}
