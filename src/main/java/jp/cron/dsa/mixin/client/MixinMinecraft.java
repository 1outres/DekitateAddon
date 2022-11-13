package jp.cron.dsa.mixin.client;

import jp.cron.dsa.DekitateAddonClient;
import jp.cron.dsa.event.TickEvent;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraft {

    @Inject(method = "run", at = @At("HEAD"))
    private void init(CallbackInfo callbackInfo) {
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void tick(CallbackInfo callbackInfo) {
        DekitateAddonClient.EVENT_BUS.post(new TickEvent());
    }

    @Inject(method = "printCrashReport", at = @At("HEAD"))
    private static void printCrashReport(CallbackInfo callbackInfo) {
        DekitateAddonClient.INSTANCE.saveConfig.save();
    }

    @Inject(method = "stop", at = @At("HEAD"))
    private void stop(CallbackInfo callbackInfo) {
        DekitateAddonClient.INSTANCE.saveConfig.save();
    }

}
