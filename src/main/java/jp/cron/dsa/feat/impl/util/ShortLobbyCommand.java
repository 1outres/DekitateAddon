package jp.cron.dsa.feat.impl.util;


import jp.cron.dsa.event.C2SPacketEvent;
import jp.cron.dsa.feat.Feature;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;

public class ShortLobbyCommand extends Feature {


    public ShortLobbyCommand() {
        super("/l", "ShortLobbyCommand","/lobbyコマンドを１文字に短縮します。", ()->true, true);
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
            if (packet.command().equalsIgnoreCase("l")) {
                e.setCancelled(true);
                CommandExecutionC2SPacket p = new CommandExecutionC2SPacket("lobby", packet.timestamp(), packet.salt(), packet.argumentSignatures(), packet.signedPreview(), packet.acknowledgment());
                mc.player.networkHandler.sendPacket(p);
            }
        }
    });
}
