package jp.cron.dsa.mixin.network;

import jp.cron.dsa.ConnectionStatus;
import jp.cron.dsa.DekitateAddon;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.SocketAddress;

@Mixin(ClientLoginNetworkHandler.class)
public class MixinClientLoginNetworkHandler {

    @Final
    @Shadow
    private ClientConnection connection;

    @Inject(method = "onSuccess", at = @At("HEAD"))
    private void onSuccess(CallbackInfo ci) {
        if (isDekitateServerConnection()) {
            ConnectionStatus.setConnected(true);
        }
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void onDisconnect(CallbackInfo ci) {
        if (isDekitateServerConnection()) {
            ConnectionStatus.setConnected(false);
        }
    }

    @Inject(method = "onDisconnected", at = @At("HEAD"))
    private void onDisconnected(CallbackInfo ci) {
        if (isDekitateServerConnection()) {
            ConnectionStatus.setConnected(false);
        }
    }

    private boolean isDekitateServerConnection() {
        return connection.getAddress().toString().equalsIgnoreCase(DekitateAddon.DEKITATE_ADDRESS);
    }
}
