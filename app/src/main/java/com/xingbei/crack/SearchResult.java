package com.xingbei.crack;


import android.bluetooth.BluetoothDevice;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

final class SearchResult {
    private HashMap<BluetoothDevice, Integer> deviceRssiMap = new HashMap();//设备信号强度Map
    private List<AdvEntity> advEntityList = new ArrayList();//闸门信息列表
    private /* synthetic */ BluetoothDiscoverService bluetoothDiscoverService;

    public SearchResult(BluetoothDiscoverService bluetoothDiscoverService) {
        this.bluetoothDiscoverService = bluetoothDiscoverService;
    }

    public final void clear() {
        this.deviceRssiMap.clear();
        this.advEntityList.clear();
    }

    //添加到扫描结果
    public final void addToResult(BluetoothDevice bluetoothDevice, int i, AdvEntity advEntity) {
        if (!this.deviceRssiMap.containsKey(bluetoothDevice)) {
            this.deviceRssiMap.put(bluetoothDevice, i);
            this.advEntityList.add(advEntity);
        } else if (this.deviceRssiMap.get(bluetoothDevice) != i) {
            this.deviceRssiMap.put(bluetoothDevice, i);
        }
    }

    //排序
    public final List<AdvEntity> sort() {
        Map a = BluetoothDiscoverService.getAdvEntityMap(this.bluetoothDiscoverService, this.advEntityList);
        List arrayList = new ArrayList();
        for (Object value : a.entrySet()) {
            ArrayList<AdvEntity> arrayList2 = (ArrayList) ((Entry)value).getValue();
            Collections.sort(arrayList2, new Comparator<AdvEntity>() {
                @Override
                public int compare(AdvEntity advEntity, AdvEntity advEntity2) {
                    return advEntity.getFlag() > advEntity2.getFlag() ? 1 : advEntity.getFlag() < advEntity2.getFlag() ? -1 : 0;
                }
            });
            for (AdvEntity advEntity : arrayList2) {
                arrayList.add(advEntity);
            }
        }

        System.out.println(new StringBuilder().append(arrayList));

        return arrayList;
    }
}