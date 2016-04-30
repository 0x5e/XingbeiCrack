package com.xingbei.crack;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BluetoothConnectService extends Service {
    public static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID CUSTOM_CHARACTERISTIC = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");//Custom Characteristic


    public static final String SIMPLE_NAME = BluetoothConnectService.class.getSimpleName();
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private String macAddress;
    public BluetoothGatt bluetoothGatt;
    public BluetoothGattCharacteristic bluetoothGattCharacteristic;
    private ArrayList<byte[]> dataToSent;
    public boolean hasSent;


    public Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == 99) {
                bluetoothGattCharacteristic.setValue(dataToSent.get(((Integer) message.obj).intValue()));
                m8753a(bluetoothGattCharacteristic);
            } else if (message.what == 100) {
                sendBroadcast(new Intent("com.reformer.tyt.bluetooth.ACTION_BLE_SEND_FAIL"));
            } else if (message.what == 101) {//MapCore.AM_DATA_SCENIC_WIDGET
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < dataToSent.size(); i++) {

                            int j = 0;
                            for (j = 0; j < 3; j++) {
                                hasSent = false;
                                System.out.println(Arrays.toString(dataToSent.get(i)));
                                Message message = new Message();
                                message.what = 99;
                                message.obj = i;
                                handler.sendMessage(message);

                                long currentTimeMillis = System.currentTimeMillis();
                                while (!hasSent && System.currentTimeMillis() - currentTimeMillis < 1000) {
                                    try {
                                        Thread.sleep(1);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                System.out.println(new StringBuilder("等待时间：").append(System.currentTimeMillis() - currentTimeMillis));
                                if (hasSent) {
                                    hasSent = false;
                                    break;
                                }
                            }

                            //重试三次失败
                            if (j == 3) {
                                handler.sendEmptyMessage(100);
                                return;
                            }
                        }
                    }
                }).start();
            }

            return true;
        }
    });

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (bluetoothGattCharacteristic.getValue()[0] == (byte) 1) {
                BluetoothConnectService.m8744a(BluetoothConnectService.this, "com.reformer.tyt.bluetooth.ACTION_DATA_AVAILABLE", bluetoothGattCharacteristic);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            hasSent = true;
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == 2) {
                System.out.println(2);
                System.out.println(SIMPLE_NAME);
                new StringBuilder("Attempting to start service discovery:").append(bluetoothGatt.discoverServices());
            } else if (newState == 0) {
                System.out.println(0);
                System.out.println(SIMPLE_NAME);
                sendBroadcast(new Intent("com.reformer.tyt.bluetooth.ACTION_BLE_DISCONNECT"));
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            handler.sendEmptyMessageDelayed(101, 300);//MapCore.AM_DATA_SCENIC_WIDGET
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
        }
    };

    static /* synthetic */ void m8744a(BluetoothConnectService bluetoothConnectService, String str, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        Intent intent = new Intent(str);
        if (bluetoothGattCharacteristic.getUuid().equals(CUSTOM_CHARACTERISTIC)) {
            intent.putExtra("com.reformer.tyt.bluetooth.EXTRA_DATA", bluetoothGattCharacteristic.getValue());
        }
        bluetoothConnectService.sendBroadcast(intent);
    }

    public final void m8753a(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        if (this.bluetoothAdapter != null && this.bluetoothGatt != null) {
            this.bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
        }
    }

    public final void m8754a(ArrayList<byte[]> arrayList) {
        this.dataToSent = arrayList;
    }

    public final boolean m8755a() {
        if (this.bluetoothManager == null) {
            this.bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (this.bluetoothManager == null) {
                return false;
            }
        }
        this.bluetoothAdapter = this.bluetoothManager.getAdapter();
        return this.bluetoothAdapter != null;
    }

    public final boolean m8756a(BluetoothGattCharacteristic bluetoothGattCharacteristic, boolean z) {
        if (this.bluetoothAdapter == null || this.bluetoothGatt == null) {
            return false;
        }
        this.bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
        BluetoothGattDescriptor descriptor = bluetoothGattCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
        if (descriptor == null) {
            return false;
        }
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        return this.bluetoothGatt.writeDescriptor(descriptor);
    }

    //connectTo
    public final boolean m8757a(String mac_addr) {
        if (this.bluetoothGatt != null) {
            this.bluetoothGatt.close();
        }
        if (this.bluetoothAdapter == null || mac_addr == null) {
            return false;
        }
        if (this.macAddress != null && mac_addr.equals(this.macAddress) && this.bluetoothGatt != null) {
            return this.bluetoothGatt.connect();
        } else {
            BluetoothDevice remoteDevice = this.bluetoothAdapter.getRemoteDevice(mac_addr);
            if (remoteDevice == null) {
                return false;
            }
            this.bluetoothGatt = remoteDevice.connectGatt(this, false, this.bluetoothGattCallback);
            this.macAddress = mac_addr;
            return true;
        }
    }

    public final void m8758b() {
        if (this.bluetoothAdapter != null && this.bluetoothGatt != null) {
            this.bluetoothGatt.disconnect();
        }
    }

    public final void m8759c() {
        if (this.bluetoothGatt != null) {
            this.bluetoothGatt.close();
            this.bluetoothGatt = null;
        }
    }

    public final List<BluetoothGattService> m8760d() {
        return this.bluetoothGatt == null ? null : this.bluetoothGatt.getServices();
    }

    private final IBinder iBinder = new Binder();
    public IBinder onBind(Intent intent) {
        return this.iBinder;
    }

    public boolean onUnbind(Intent intent) {
        m8759c();
        return super.onUnbind(intent);
    }
}