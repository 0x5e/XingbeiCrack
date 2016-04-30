package com.xingbei.crack;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by gaosen on 16/4/29.
 */
public class KeyDataUtil {


    // m51a
    public static ArrayList<byte[]> decryptKeyData(String keyData, AESUtil aesUtil, AdvEntity advEntity, String userPhoneNo, String systemTime) {
//        System.out.println(new StringBuilder("systemTime=").append(systemTime));

        //时间格式转换,"2016-04-30 11:11:07 6"转为[20,16,4,30,11,11,7,6]
        String[] split = systemTime.split(" ");
        int parseInt = Integer.parseInt(split[0].split("-")[0]);
        int parseInt2 = Integer.parseInt(split[0].split("-")[1]);
        int parseInt3 = Integer.parseInt(split[0].split("-")[2]);
        int parseInt4 = Integer.parseInt(split[1].split(":")[0]);
        int parseInt5 = Integer.parseInt(split[1].split(":")[1]);
        int parseInt6 = Integer.parseInt(split[1].split(":")[2]);
        int parseInt7 = Integer.parseInt(split[2]);
        byte[] bArr = new byte[]{(byte) (parseInt / 100), (byte) (parseInt % 100), (byte) parseInt2, (byte) parseInt3, (byte) parseInt4, (byte) parseInt5, (byte) parseInt6, (byte) parseInt7};
        if (bArr[7] == (byte) 0) {
            bArr[7] = (byte) 7;
        }

        //将md5(userPhoneNo)作为aes秘钥,解密经Base64编码过的keyData
        byte[] bMd5UserPhoneNo = KeyDataUtil.md5(userPhoneNo.getBytes());
        byte[] bKeyData = Base64.decode(keyData, 0);
        byte[] result = new AESUtil(bMd5UserPhoneNo).decode(bKeyData);

        byte[] obj = new byte[23];
        System.arraycopy(result, 0, obj, 0, 14);
        System.arraycopy(result, 16, obj, 14, 9);
        byte[] obj2 = new byte[21];
        System.arraycopy(obj, 0, obj2, 0, 21);
        byte[] a = KeyDataUtil.m67a(KeyDataUtil.m8777a(obj2));
        if (a[2] == obj[21] && a[3] == obj[22]) {
            result = new byte[16];
            System.arraycopy(obj2, 0, result, 0, 16);
        } else {
            result = null;
        }
        byte[] bArr2 = new byte[(result.length + 10)];
        bArr2[0] = (byte) result.length;
        for (parseInt = 0; parseInt < result.length; parseInt++) {
            bArr2[parseInt + 1] = result[parseInt];
        }
        bArr2[result.length + 1] = (byte) advEntity.getFlag();
        for (parseInt = 0; parseInt < 8; parseInt++) {
            bArr2[(result.length + 2) + parseInt] = bArr[parseInt];
        }
        ArrayList<byte[]> arrayList = new ArrayList();
        parseInt5 = bArr2.length;
        parseInt = parseInt5 / 14;
        parseInt3 = parseInt5 % 14;
        if (parseInt == 0) {
            obj = new byte[20];
            obj[0] = (byte) 1;
            obj[1] = (byte) 3;
            obj[2] = (byte) 20;
            byte[] bArr3 = new byte[parseInt5];
            for (parseInt7 = 0; parseInt7 < parseInt5; parseInt7++) {
                bArr3[parseInt7] = bArr2[parseInt7];
            }
            bArr3 = KeyDataUtil.m69a(bArr3, aesUtil);
            for (parseInt7 = 0; parseInt7 < bArr3.length; parseInt7++) {
                obj[parseInt7 + 3] = bArr3[parseInt7];
            }
            obj[19] = KeyDataUtil.sum(obj);
            arrayList.add(obj);
            return arrayList;
        }

        byte[] obj3 = new byte[20];
        obj3[0] = (byte) 1;
        obj3[1] = (byte) 1;
        obj3[2] = (byte) 20;
        bArr = new byte[14];
        for (parseInt7 = 0; parseInt7 < 14; parseInt7++) {
            bArr[parseInt7] = bArr2[parseInt7];
        }
        bArr = KeyDataUtil.m69a(bArr, aesUtil);
        for (parseInt7 = 0; parseInt7 < bArr.length; parseInt7++) {
            obj3[parseInt7 + 3] = bArr[parseInt7];
        }
        obj3[19] = KeyDataUtil.sum(obj3);
        arrayList.add(obj3);
        parseInt7 = parseInt3 == 0 ? parseInt - 1 : parseInt;
        for (parseInt3 = 1; parseInt3 < parseInt7; parseInt3++) {
            obj3 = new byte[20];
            obj3[0] = (byte) 1;
            obj3[1] = (byte) 0;
            obj3[2] = (byte) 20;
            byte[] bArr4 = new byte[14];
            for (parseInt = 0; parseInt < 14; parseInt++) {
                bArr4[parseInt] = bArr2[(parseInt3 * 14) + parseInt];
            }
            bArr4 = KeyDataUtil.m69a(bArr4, aesUtil);
            for (parseInt = 0; parseInt < bArr.length; parseInt++) {
                obj3[parseInt + 3] = bArr4[parseInt];
            }
            obj3[19] = KeyDataUtil.sum(obj3);
            arrayList.add(obj3);
        }
        parseInt = parseInt5 - (parseInt7 * 14);
        byte[] obj4 = new byte[20];
        obj4[0] = (byte) 1;
        obj4[1] = (byte) 2;
        obj4[2] = (byte) 20;
        byte[] bArr5 = new byte[parseInt];
        for (parseInt7 = 0; parseInt7 < parseInt; parseInt7++) {
            bArr5[parseInt7] = bArr2[(parseInt5 - parseInt) + parseInt7];
        }
        byte[] a2 = KeyDataUtil.m69a(bArr5, aesUtil);
        for (parseInt7 = 0; parseInt7 < a2.length; parseInt7++) {
            obj4[parseInt7 + 3] = a2[parseInt7];
        }
        obj4[19] = KeyDataUtil.sum(obj4);
        arrayList.add(obj4);
        return arrayList;
    }

    public static byte[] m67a(int i) {
        byte[] bArr = new byte[4];
        for (int i2 = 0; i2 < 4; i2++) {
            bArr[i2] = (byte) (i >> ((3 - i2) * 8));
        }
        return bArr;
    }

    public static byte[] m69a(byte[] bArr, AESUtil AESUtil) {
        int i = 0;
        byte[] bArr2 = new byte[(bArr.length + 1)];
        byte b = (byte) 0;
        while (i < bArr.length) {
            bArr2[i] = bArr[i];
            b = (byte) (b + bArr[i]);
            i++;
        }
        bArr2[bArr.length] = b;
        return AESUtil.encode(bArr2);
    }

    // m80b
    public static byte[] md5(byte[] bArr) {
        MessageDigest instance = null;
        byte[] result = null;
        try {
            instance = MessageDigest.getInstance("MD5");
            instance.update(bArr);
            result = instance.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static byte sum(byte[] bArr) {
        byte b = 0;
        for (int i=0;i < 19;i++) {
            b += bArr[i];
        }
        return b;
    }

    private static char[] f6921a = new char[]{'\u0000', '\u1021', '\u2042', '\u3063', '\u4084', '\u50a5', '\u60c6', '\u70e7', '\u8108', '\u9129', '\ua14a', '\ub16b', '\uc18c', '\ud1ad', '\ue1ce', '\uf1ef', '\u1231', '\u0210', '\u3273', '\u2252', '\u52b5', '\u4294', '\u72f7', '\u62d6', '\u9339', '\u8318', '\ub37b', '\ua35a', '\ud3bd', '\uc39c', '\uf3ff', '\ue3de', '\u2462', '\u3443', '\u0420', '\u1401', '\u64e6', '\u74c7', '\u44a4', '\u5485', '\ua56a', '\ub54b', '\u8528', '\u9509', '\ue5ee', '\uf5cf', '\uc5ac', '\ud58d', '\u3653', '\u2672', '\u1611', '\u0630', '\u76d7', '\u66f6', '\u5695', '\u46b4', '\ub75b', '\ua77a', '\u9719', '\u8738', '\uf7df', '\ue7fe', '\ud79d', '\uc7bc', '\u48c4', '\u58e5', '\u6886', '\u78a7', '\u0840', '\u1861', '\u2802', '\u3823', '\uc9cc', '\ud9ed', '\ue98e', '\uf9af', '\u8948', '\u9969', '\ua90a', '\ub92b', '\u5af5', '\u4ad4', '\u7ab7', '\u6a96', '\u1a71', '\u0a50', '\u3a33', '\u2a12', '\udbfd', '\ucbdc', '\ufbbf', '\ueb9e', '\u9b79', '\u8b58', '\ubb3b', '\uab1a', '\u6ca6', '\u7c87', '\u4ce4', '\u5cc5', '\u2c22', '\u3c03', '\u0c60', '\u1c41', '\uedae', '\ufd8f', '\ucdec', '\uddcd', '\uad2a', '\ubd0b', '\u8d68', '\u9d49', '\u7e97', '\u6eb6', '\u5ed5', '\u4ef4', '\u3e13', '\u2e32', '\u1e51', '\u0e70', '\uff9f', '\uefbe', '\udfdd', '\ucffc', '\ubf1b', '\uaf3a', '\u9f59', '\u8f78', '\u9188', '\u81a9', '\ub1ca', '\ua1eb', '\ud10c', '\uc12d', '\uf14e', '\ue16f', '\u1080', '\u00a1', '\u30c2', '\u20e3', '\u5004', '\u4025', '\u7046', '\u6067', '\u83b9', '\u9398', '\ua3fb', '\ub3da', '\uc33d', '\ud31c', '\ue37f', '\uf35e', '\u02b1', '\u1290', '\u22f3', '\u32d2', '\u4235', '\u5214', '\u6277', '\u7256', '\ub5ea', '\ua5cb', '\u95a8', '\u8589', '\uf56e', '\ue54f', '\ud52c', '\uc50d', '\u34e2', '\u24c3', '\u14a0', '\u0481', '\u7466', '\u6447', '\u5424', '\u4405', '\ua7db', '\ub7fa', '\u8799', '\u97b8', '\ue75f', '\uf77e', '\uc71d', '\ud73c', '\u26d3', '\u36f2', '\u0691', '\u16b0', '\u6657', '\u7676', '\u4615', '\u5634', '\ud94c', '\uc96d', '\uf90e', '\ue92f', '\u99c8', '\u89e9', '\ub98a', '\ua9ab', '\u5844', '\u4865', '\u7806', '\u6827', '\u18c0', '\u08e1', '\u3882', '\u28a3', '\ucb7d', '\udb5c', '\ueb3f', '\ufb1e', '\u8bf9', '\u9bd8', '\uabbb', '\ubb9a', '\u4a75', '\u5a54', '\u6a37', '\u7a16', '\u0af1', '\u1ad0', '\u2ab3', '\u3a92', '\ufd2e', '\ued0f', '\udd6c', '\ucd4d', '\ubdaa', '\uad8b', '\u9de8', '\u8dc9', '\u7c26', '\u6c07', '\u5c64', '\u4c45', '\u3ca2', '\u2c83', '\u1ce0', '\u0cc1', '\uef1f', '\uff3e', '\ucf5d', '\udf7c', '\uaf9b', '\ubfba', '\u8fd9', '\u9ff8', '\u6e17', '\u7e36', '\u4e55', '\u5e74', '\u2e93', '\u3eb2', '\u0ed1', '\u1ef0'};
    public static int m8777a(byte[] bArr) {
        int i = 21;
        int i2 = 0;
        int i3 = 0;
        while (true) {
            int i4 = i - 1;
            if (i == 0) {
                return i3;
            }
            i = (((byte) (i3 / 256)) ^ bArr[i2]) & 255;
            i3 = 65535 & (f6921a[i] ^ (i3 << 8));
            i2++;
            i = i4;
        }
    }
}
