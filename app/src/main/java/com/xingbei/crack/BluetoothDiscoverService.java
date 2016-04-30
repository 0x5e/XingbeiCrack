package com.xingbei.crack;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import java.io.FileDescriptor;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BluetoothDiscoverService extends Service {
//    private static final String f6899a = BluetoothDiscoverService.class.getSimpleName();
    public BluetoothAdapter btAdapter;
    private Handler mHandler;
    public boolean f6902d;
    private SearchResult searchResult;
    public long timeStamp;

    //扫描蓝牙闸门
    public LeScanCallback leScanCallback = new LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            //"LF"开头的蓝牙才可能是闸门
            if (device.getName() != null && device.getName().startsWith("LF")) {
                System.out.println(new StringBuilder("历时：").append(System.currentTimeMillis() - timeStamp));

                //http://www.race604.com/ble-advertising/
                //不太明白
                byte[] record = new byte[20];
                if (scanRecord[0] == (byte) 2) {
                    System.arraycopy(scanRecord, 9, record, 0, 20);
                } else if (scanRecord[0] == (byte) 30) {
                    System.arraycopy(scanRecord, 40, record, 0, 20);
                }

                try {
                    BluetoothDiscoverService.parseAdvData(BluetoothDiscoverService.this, device, record, rssi);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    static /* synthetic */ Map getAdvEntityMap(BluetoothDiscoverService bluetoothDiscoverService, List list) {
        Map treeMap = new TreeMap();
        for (int i = 0; i < list.size(); i++) {
            AdvEntity advEntity = (AdvEntity) list.get(i);
            if (treeMap.containsKey(Integer.valueOf(advEntity.getAreaid()))) {
                ((ArrayList) treeMap.get(Integer.valueOf(advEntity.getAreaid()))).add(advEntity);
            } else {
                ArrayList arrayList = new ArrayList();
                arrayList.add(advEntity);
                treeMap.put(Integer.valueOf(advEntity.getAreaid()), arrayList);
            }
        }
        return treeMap;
    }

    static /* synthetic */ void parseAdvData(BluetoothDiscoverService bluetoothDiscoverService, BluetoothDevice bluetoothDevice, byte[] record, int rssid) {
        byte sum = (byte) 0;
        for (int i = 0; i < record.length - 1; i++) {
            sum = (byte) (sum + record[i]);
        }
        //record累加和校验
        if (sum == record[19]) {
            // int32 + 14 char + byte + sum

            //areaid
            int areaid = ((((record[0] & 255) << 24) | ((record[1] & 255) << 16)) | ((record[2] & 255) << 8)) | (record[3] & 255);

            //channel,最大长度14,gbk编码
            byte[] byteArray = new byte[14];
            System.arraycopy(record, 4, byteArray, 0, 14);

            String channel = null;
            try {
                channel = new String(byteArray, "gbk");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //flag
            byte flag = record[18];

            //蓝牙mac地址,蓝牙名称(取前两位,应为"LF"),类似uuid,字符串,1 byte
            AdvEntity advEntity = new AdvEntity(bluetoothDevice.getAddress(), bluetoothDevice.getName().substring(2), areaid, channel, flag);
            bluetoothDiscoverService.searchResult.addToResult(bluetoothDevice, rssid, advEntity);

            System.out.println(advEntity.toString());
        }
    }

    public final List<AdvEntity> m8767a() {
        return this.searchResult.sort();
    }

    public final List<AdvEntity> m8768a(int i) {
        List<AdvEntity> b = this.searchResult.sort();
        for (int size = b.size() - 1; size >= 0; size--) {
            if (((AdvEntity) b.get(size)).getAreaid() != i) {
                b.remove(size);
            }
        }
        return b;
    }

    public final void m8769a(boolean z) {
        if (!z) {
            this.f6902d = false;
            this.btAdapter.stopLeScan(this.leScanCallback);
        } else if (!this.f6902d) {
            this.timeStamp = System.currentTimeMillis();
            this.searchResult.clear();
            this.mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    f6902d = false;
                    btAdapter.stopLeScan(leScanCallback);
                    sendBroadcast(new Intent("com.reformer.tyt.bluetooth.BluetoothDiscoverService"));
                }
            }, 1500);

            this.f6902d = true;
            this.btAdapter.startLeScan(this.leScanCallback);
        }
    }

    private final IBinder mBinder = new Binder();
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    public void onCreate() {
        super.onCreate();
        this.mHandler = new Handler();
        this.btAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        this.searchResult = new SearchResult(this);
    }
}