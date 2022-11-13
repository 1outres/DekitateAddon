package jp.cron.dsa.util;

import com.google.gson.JsonObject;
import jp.cron.dsa.DekitateAddon;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ChatPreviewer;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientUtils {

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final ChatPreviewer chatPreviewer = new ChatPreviewer(mc);
    private static final Logger logger = LogManager.getLogger(DekitateAddon.MOD_NAME);

    public static Logger getLogger() {
        return logger;
    }


    public static void displayChatMessage(final String message) {
        if (mc.player == null) {
            getLogger().info("(MCChat)" + message);
            return;
        }

        mc.player.sendMessage(Text.of(message));
    }

    public static void sendCommand(String command) {
        if (mc.player==null)
            throw new RuntimeException("Player is null");
        Text text = Util.map(chatPreviewer.tryConsumeResponse(command), ChatPreviewer.Response::previewText);
        mc.player.sendCommand(command, text);
    }
}
