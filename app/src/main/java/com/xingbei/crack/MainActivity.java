package com.xingbei.crack;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        bindService(new Intent(this, BluetoothDiscoverService.class), this.f6754k, 1);
//        bindService(new Intent(this, BluetoothConnectService.class), this.f6757n, 1);
    }

    public void onClick(View view) {
        //String mac, String name, int areaid, String channel, int flag
        //只用到了flag
        AdvEntity advEntity = new AdvEntity("00:00:00:00:00:00", "LF", 123456, "左入口", 0);
        String keyData = ((EditText)this.findViewById(R.id.keyData)).getText().toString();
        String systemTime = ((EditText)this.findViewById(R.id.systemTime)).getText().toString();
        String userPhoneNo = ((EditText)this.findViewById(R.id.userPhoneNo)).getText().toString();

//        String date = new SimpleDateFormat("yyyyMMdd").format(new Date(System.currentTimeMillis()));
        String date = systemTime.split(" ")[0].replace("-", "");
        byte[] bArr = new byte[4];
        for (int i = 0; i < 8; i += 2) {
            bArr[i / 2] = (byte) Integer.parseInt(date.substring(i, i + 2));
        }
        byte[] bArr2 = new byte[16];
        try {
            bArr2 = KeyDataUtil.md5(bArr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        AESUtil aesUtil = new AESUtil(bArr2); //以当前年月日为参数,转换为秘钥,生成AESUtil

        ArrayList<byte[]> arrayList = KeyDataUtil.decryptKeyData(keyData, aesUtil, advEntity, userPhoneNo, systemTime);

        ArrayList<String> strList = new ArrayList<String>();
        for (byte[] item: arrayList) {
            strList.add(byte2hex(item));
        }
        System.out.println("Result: " + strList);

        ((EditText)this.findViewById(R.id.result)).setText(strList.toString());
    }

    private static String byte2hex(byte [] buffer){
        String h = "";

        for(int i = 0; i < buffer.length; i++){
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if(temp.length() == 1){
                temp = "0" + temp;
            }
            h = h + temp;
        }

        return h;

    }
}
