package org.tfelab.mobile;

/**
 * Created by karajan on 2017/3/25.
 */

public interface SmsListener {
    public void messageReceived(String sender, String body);
}