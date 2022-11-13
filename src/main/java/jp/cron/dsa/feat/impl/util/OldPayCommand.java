package jp.cron.dsa.feat.impl.util;


import jp.cron.dsa.event.C2SPacketEvent;
import jp.cron.dsa.event.ChatDisplayEvent;
import jp.cron.dsa.event.S2CPacketEvent;
import jp.cron.dsa.feat.Feature;
import jp.cron.dsa.feat.setting.BooleanSetting;
import jp.cron.dsa.util.ClientUtils;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ChatPreviewer;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.time.Instant;

public class OldPayCommand extends Feature {

    private ChatPreviewer chatPreviewer;

    private boolean sent = false;

    private final BooleanSetting autoConfirm = registerBooleanSetting("autoConfirm", "autoConfirm", "自動で振り込みを確定します。", ()->true, true);

    public OldPayCommand() {
        super("/pay", "OldPayCommand", "旧鯖の/payコマンドを復活させます。", ()->true, true);
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

    @Subscribe
    private final Listener<C2SPacketEvent> s2CPacketEventListener = new Listener<>(e-> {
        if ( e.getPacket() instanceof CommandExecutionC2SPacket packet && isEnabled().isOn() && mc.player!=null) {
            if (packet.command().startsWith("pay")) {
                e.setCancelled(true);
                ClientUtils.sendCommand("atm transfer"+packet.command().substring(3));
                sent = true;
            }
        }
    });

    private static final String PAY_CONFIRM_KEYWORD = "▇ できたて銀行 ✿ Bank of Dekitate ▇▇▇▇▇▇▇▇▇▇\n▎ 下記の内容で振り込みますか";

    @Subscribe
    private final Listener<S2CPacketEvent> chatDisplayEventListener = new Listener<>(e -> {
        if (sent && autoConfirm.isOn() && e.getPacket() instanceof GameMessageS2CPacket packet) {
            String msg = packet.content().getString();
            if (msg.contains(PAY_CONFIRM_KEYWORD)) {
                e.setCancelled(true);
                ClientUtils.sendCommand("atm transfer-confirm");
                sent = false;
            }
        }
    });
}
