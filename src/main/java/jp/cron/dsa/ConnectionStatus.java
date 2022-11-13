package jp.cron.dsa;

public class ConnectionStatus {
    private static boolean isConnected = false;
    private static Server server = null;

    private static String ign;

    public static void setConnected(boolean connected) {
        ConnectionStatus.isConnected = connected;
    }

    public static void setServer(Server server) {
        ConnectionStatus.server = server;
    }

    public static void setIGN(String ign) {
        ConnectionStatus.ign = ign;
    }

    public static Server getServer() {
        return server;
    }

    public enum Server {
        LOBBY,
        SURVIVAL,
        RESOURCE,
        SHOP,
        EVENT;
    }

}
