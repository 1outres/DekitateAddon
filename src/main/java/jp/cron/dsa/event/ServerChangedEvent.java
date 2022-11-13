package jp.cron.dsa.event;

import jp.cron.dsa.ConnectionStatus;

public class ServerChangedEvent {
    ConnectionStatus.Server server, joinedServer;

    public ServerChangedEvent(ConnectionStatus.Server server, ConnectionStatus.Server joinedServer) {
        this.server = server;
        this.joinedServer = joinedServer;
    }

    public ConnectionStatus.Server getServer() {
        return server;
    }

    public ConnectionStatus.Server getJoinedServer() {
        return joinedServer;
    }
}