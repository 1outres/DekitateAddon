package jp.cron.dsa.feat.impl.util;


import jp.cron.dsa.event.C2SPacketEvent;
import jp.cron.dsa.feat.Feature;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;

public class OldBalCommand extends Feature {


    public OldBalCommand() {
        super("/bal", "OldBalCommand", "旧鯖の/balコマンドを復活させます。", ()->true, true);
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
            if (packet.command().startsWith("bal")) {
                e.setCancelled(true);
                CommandExecutionC2SPacket p = new CommandExecutionC2SPacket("atm balance"+packet.command().substring(3), packet.timestamp(), packet.salt(), packet.argumentSignatures(), packet.signedPreview(), packet.acknowledgment());
                mc.player.networkHandler.sendPacket(p);
            }
        }
    });
}
