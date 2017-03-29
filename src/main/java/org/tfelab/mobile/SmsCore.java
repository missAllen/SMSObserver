package org.tfelab.mobile;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by karajan on 2017/3/26.
 */

public class SmsCore {

    public static String phone_number = null;
    public static String phone_id = null;

    public static String getPhoneNumber(String content){

        String reg = "\\d{11}";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(content);
        List<String> numbers = new ArrayList<>();
        while(m.find()) {
            numbers.add(m.group());
        }
        if(numbers.size() > 0) {

            return numbers.get(0);
        }

        return null;
    }

    public static void sendSms(String number, String text, Context context){

        String SENT = "sms_sent";
        String DELIVERED = "sms_delivered";

        PendingIntent sentPI = PendingIntent.getActivity(context, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getActivity(context, 0, new Intent(DELIVERED), 0);

        SmsManager.getDefault().sendTextMessage(number, null, text, sentPI, deliveredPI);
    }

}