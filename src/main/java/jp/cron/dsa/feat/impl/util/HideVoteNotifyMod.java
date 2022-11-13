package jp.cron.dsa.feat.impl.util;

import jp.cron.dsa.event.S2CPacketEvent;
import jp.cron.dsa.feat.Feature;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HideVoteNotifyMod extends Feature {
    public HideVoteNotifyMod() {
        super("Hide Vote Notify", "HideVoteNotifyMod","投票の通知を非表示にします。", ()->true, true);
    }

    private static final Pattern VOTE_MESSAGE_PATTERN = Pattern.compile("══════════════════════════════════\n [a-zA-Z0-9_]{2,16}が[0-9]+投票ポイントを受け取りました！ \\[投票はこちら]\n══════════════════════════════════", Pattern.MULTILINE);

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    @Subscribe
    private final Listener<S2CPacketEvent> s2CPacketEventListener = new Listener<>(e-> {
        if ( e.getPacket() instanceof GameMessageS2CPacket packet && isEnabled().isOn()) {
            Matcher m = VOTE_MESSAGE_PATTERN.matcher(packet.content().getString());
            if (m.matches())
                e.setCancelled(true);
        }
    });
}
