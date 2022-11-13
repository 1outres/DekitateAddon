package jp.cron.dsa.mixin.network;

import io.netty.buffer.ByteBufUtil;
import jp.cron.dsa.ConnectionStatus;
import jp.cron.dsa.DekitateAddonClient;
import jp.cron.dsa.event.C2SPacketEvent;
import jp.cron.dsa.event.S2CPacketEvent;
import jp.cron.dsa.event.ServerChangedEvent;
import jp.cron.dsa.util.BytesUtil;
import jp.cron.dsa.util.ClientUtils;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiling.jfr.event.PacketEvent;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ClientConnection.class)
public class MixinClientConnection {

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static void handlePacket(Packet<?> p, PacketListener listener, CallbackInfo ci) {
        if (p instanceof CustomPayloadS2CPacket packet) {
            if (packet.getChannel().equals(Identifier.of("dekitate_core", "channel"))) {
                byte[] data = ByteBufUtil.getBytes(packet.getData());
                String hex = BytesUtil.bytesToHex(data);

                ClientUtils.getLogger().debug("Received CustomPayloadPacket: " + hex);

                Pattern teleportPattern = Pattern.compile(
                        "^0008" + // NULL BS
                                "54656C65706F7274" + // "Teleport"
                                "[0-9ABCDEF]{36}"+ // Unknown
                                "([0-9ABCDEF]+)" + // destServer
                                "[0-9ABCDEF]{32}" + // Player UUID
                                "0005" + // Unknown
                                "([0-9ABCDEF]+)" + // destWorld
                                "([0-9ABCDEF]{16})" + // destX
                                "([0-9ABCDEF]{16})" + // destY
                                "([0-9ABCDEF]{16})" + // destZ
                                "[0-9ABCDEF]{16}$" // Unknown
                );
                Matcher m = teleportPattern.matcher(hex);
                if (m.matches()) {
                    try {
                        double destX = Double.longBitsToDouble(BytesUtil.parseUnsignedHex(m.group(3)));
                        double destY = Double.longBitsToDouble(BytesUtil.parseUnsignedHex(m.group(4)));
                        double destZ = Double.longBitsToDouble(BytesUtil.parseUnsignedHex(m.group(5)));

                        byte[] destServerBytes = Hex.decodeHex(m.group(1));
                        byte[] destWorldBytes = Hex.decodeHex(m.group(2));

                        String destServer = new String(destServerBytes);
                        String destWorld = new String(destWorldBytes);

                        ClientUtils.getLogger().info("Detected teleporting to " + destServer + " " + destWorld + " " + destX + " " + destY + " " + destZ);

                        ConnectionStatus.Server joinedServer = ConnectionStatus.Server.valueOf(destServer.toUpperCase());

                        ServerChangedEvent event = new ServerChangedEvent(ConnectionStatus.getServer(), joinedServer);

                        ConnectionStatus.setServer(ConnectionStatus.Server.valueOf(destServer.toUpperCase()));

                        DekitateAddonClient.EVENT_BUS.post(event);
                    } catch (DecoderException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        S2CPacketEvent event = new S2CPacketEvent(p);
        boolean isCancelled = DekitateAddonClient.EVENT_BUS.post(event);
        if (isCancelled)
            ci.cancel();
    }

    @Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
    private void sendImmediately(Packet<?> packet, @Nullable PacketCallbacks callbacks, CallbackInfo ci) {
        C2SPacketEvent event = new C2SPacketEvent(packet);
        boolean isCancelled = DekitateAddonClient.EVENT_BUS.post(event);
        if (isCancelled)
            ci.cancel();
    }

}
