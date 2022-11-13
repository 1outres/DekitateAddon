package jp.cron.dsa.feat.impl.util;

import jp.cron.dsa.event.ChatDisplayEvent;
import jp.cron.dsa.event.ServerChangedEvent;
import jp.cron.dsa.event.TickEvent;
import jp.cron.dsa.feat.Feature;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AfkNotifMod extends Feature {

    private boolean isGettingWarn = false;

    private static final String WARN_MESSAGE = "離席中です. まもなくロビーにテレポートします...";
    private static final String UNWARN_MESSAGE = "離席中から復帰しました.";

    public AfkNotifMod() {
        super("AFK Notification", "AfkNotificationMod", "離席判定されロビーにワープされる際に警告音を鳴らします。", ()->true, true);
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
        this.isGettingWarn = false;
    }

    private int playSoundTimer = 0;

    @Subscribe
    private final Listener<TickEvent> tickEventListener = new Listener<>(e-> {
        if (isGettingWarn && isEnabled().isOn()) {
            if (mc.player==null) {
                isGettingWarn = false;
                return;
            }

            playSoundTimer++;
            if (playSoundTimer >= 2) {


                mc.player.playSound(
                        new SoundEvent(Identifier.of("minecraft", "block.note_block.pling")),
                        SoundCategory.AMBIENT,
                        1.0f, 1.0f
                );

                playSoundTimer = 0;
            }
        }
    });

    @Subscribe
    private final Listener<ChatDisplayEvent> clientChatReceivedEventListener = new Listener<>(e-> {
        Text msg = e.getMessage();
        if (msg.getString().equals(WARN_MESSAGE)) {
            isGettingWarn = true;
        } else if (msg.getString().equals(UNWARN_MESSAGE)) {
            isGettingWarn = false;
        }
    });

    @Subscribe
    private final Listener<ServerChangedEvent> serverChangedEventListener = new Listener<>(e-> {
        isGettingWarn = false;
    });

}
