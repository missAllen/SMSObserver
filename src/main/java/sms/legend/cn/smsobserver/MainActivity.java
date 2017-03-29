package sms.legend.cn.smsobserver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.tfelab.io.requester.RestfulRequester;
import org.tfelab.mobile.SmsCore;
import org.tfelab.mobile.SmsListener;
import org.tfelab.mobile.SmsReceiver;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {


    private static String srv_url = "http://tetra-data.com:59021/sms";
    private static String uid = "c582abeb-f9fa-4909-ae0f-04e95f430950";
    private static String private_key = "3658244053c0451ca1dbdb47faa2a589";

    private Map<String,String> map = new HashMap<String,String>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private Handler mhanler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

//        SmsCore.sendSms("10010", "CXHM", this);

        RestfulRequester.getInstance().updateUidAndPrivateKey(uid, private_key);

        SmsCore.phone_id = ((TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

        setContentView(R.layout.activity_main);

        final SharedPreferences preferences = getSharedPreferences("info", MODE_PRIVATE);
        SmsCore.phone_number = preferences.getString("phoneNumber", null);
        srv_url = preferences.getString("srvUrl", srv_url);
        ((EditText) findViewById(R.id.editText)).getText().append(srv_url);


        if(SmsCore.phone_number != null && SmsCore.phone_number.length() > 0) {
            ((EditText) findViewById(R.id.edit)).getText().append(SmsCore.phone_number);
        }

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String str = ((EditText) findViewById(R.id.edit)).getText().toString();
                if(str != null && str.length() > 0) {

                    Toast.makeText(MainActivity.this, "设定本机号码: "+str,Toast.LENGTH_LONG).show();
                    SmsCore.phone_number = str;
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("phoneNumber", str);
                    editor.commit();
                }

                String newSrvUrl = ((EditText) findViewById(R.id.editText)).getText().toString();
                if(newSrvUrl != null && newSrvUrl.length() > 0) {

                    Toast.makeText(MainActivity.this, "设定服务器地址: "+newSrvUrl,Toast.LENGTH_LONG).show();
                    srv_url = newSrvUrl;
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("srvUrl", newSrvUrl);
                    editor.commit();
                }
            }
        });

        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String sender, String content) {

                if(sender.equals("10010") && SmsCore.phone_number == null) {
                    SmsCore.phone_number = SmsCore.getPhoneNumber(content);
                    Toast.makeText(MainActivity.this, "本机号码: "+SmsCore.phone_number,Toast.LENGTH_LONG).show();
                }

                String code = "";
                /**
                 * 处理短信中的验证码
                 */
                String[] regs = {
                        ".*?(\\d{4,6}) ?为您的注册验证码.*?",
                        ".*?验证码是? ?(\\d{4,6}).*?",
                        ".*?效验码是? ?(\\d{4,6}).*?"
                };

                for(String reg : regs) {
                    Pattern p = Pattern.compile(reg);
                    Matcher m = p.matcher(content);
                    if(m.find()) {
                        code = m.group(1);
                        break;
                    }
                    if(code.length() > 0) break;
                }

                final Map<String, String> map = new HashMap<>();
                map.put("imei", SmsCore.phone_id);
                map.put("from", sender);
                map.put("to", SmsCore.phone_number);
                map.put("content", content);
                map.put("code", code);

                new Thread() {
                    @Override
                    public void run() {
                        try {
                            String msg = map.get("imei") + map.get("from") + map.get("to") + map.get("content");
                            Log.e("msg",msg);
                            String resp = RestfulRequester.getInstance().request(srv_url, RestfulRequester.RequestType.GET, map);
                            Log.e("SendToBackEnd", resp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

                //Toast.makeText(MainActivity.this, "Message: "+messageText,Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        startActivity(launcherIntent);
    }

}
