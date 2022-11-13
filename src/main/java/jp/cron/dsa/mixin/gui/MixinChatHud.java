package jp.cron.dsa.mixin.gui;

import jp.cron.dsa.ConnectionStatus;
import jp.cron.dsa.DekitateAddonClient;
import jp.cron.dsa.event.ChatDisplayEvent;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ChatHud.class)
public class MixinChatHud {

    @Inject(method = "logChatMessage", at = @At("HEAD"), cancellable = true)
    private void logChatMessage(Text message, MessageIndicator indicator, CallbackInfo ci) {
        String msg = message.getString();

        Pattern p = Pattern.compile("^できたてサーバー（猫） にようこそ - ([a-zA-Z0-9_]{2,16})@(lobby|survival|resource|shop|event)");
        Matcher m = p.matcher(msg);
        if (m.matches()) {
            String ign = m.group(1);
            String server = m.group(2);

            ConnectionStatus.setIGN(ign);
            ConnectionStatus.setServer(ConnectionStatus.Server.valueOf(server.toUpperCase()));
        }

        boolean isCancelled = DekitateAddonClient.EVENT_BUS.post(new ChatDisplayEvent(message));

        if (isCancelled)
            ci.cancel();
    }
}
