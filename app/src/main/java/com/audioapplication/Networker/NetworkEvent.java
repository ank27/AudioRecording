package com.audioapplication.Networker;

/**
 * Created by ankurkhandelwal on 17/05/17.
 */

public class NetworkEvent {
    public final String event;
    public final boolean status;

    public NetworkEvent(String event, boolean status) {
        this.event = event;
        this.status = status;
    }
}
