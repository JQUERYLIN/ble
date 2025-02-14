package com.ywx.ble;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class ByteUtils {
    /**
     * 将一个单字节的byte转换成32位的int
     *
     * @param b byte
     * @return convert result
     */
    public static int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }

    /**
     * 将一个单字节的Byte转换成十六进制的数
     *
     * @param b byte
     * @return convert result
     */
    public static String byteToHex(byte b) {
        int i = b & 0xFF;
        return Integer.toHexString(i);
    }

    /**
     * 将一个4byte的数组转换成32位的int
     *
     * @param buf bytes buffer
     * @return convert result
     */
    public static long unsigned4BytesToInt(byte[] buf, int pos) {
        int firstByte = 0;
        int secondByte = 0;
        int thirdByte = 0;
        int fourthByte = 0;
        int index = pos;
        firstByte = (0x000000FF & ((int) buf[index]));
        secondByte = (0x000000FF & ((int) buf[index + 1]));
        thirdByte = (0x000000FF & ((int) buf[index + 2]));
        fourthByte = (0x000000FF & ((int) buf[index + 3]));
        index = index + 4;
        return ((long) (firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte)) & 0xFFFFFFFFL;
    }

    /**
     * 将一个4byte的数组转换成32位的int
     *
     * @param buf bytes buffer
     * @return convert result
     */
    public static long unsigned2BytesToInt(byte[] buf, int pos) {
        int firstByte = 0;
        int secondByte = 0;

        int index = pos;
        firstByte = (0x000000FF & ((int) buf[index]));
        secondByte = (0x000000FF & ((int) buf[index + 1]));
        index = index + 4;
        return ((long) (firstByte << 8 | secondByte )) & 0xFFFFL;
    }

    /**
     * 将16位的short转换成byte数组
     *
     * @param s short
     * @return byte[] 长度为2
     */
    public static byte[] shortToByteArray(short s) {
        byte[] targets = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }

//    public static byte[] intToByteArray2(int num){
//        byte[] targets = new byte[2];
//        byte LowH =(byte)((num >>> 8)&0xff);
//        byte LowL = (byte)(num&0xff);
//        targets[0] = LowH;
//        targets[1] = LowL;
//        return targets;
//    }

    /**
     * 将32位整数转换成长度为4的byte数组
     *
     * @param s int
     * @return byte[]
     */
    public static byte[] intToByteArray(int s) {
        byte[] targets = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }

    public static byte[] intToReByteArray(int s) {
        byte[] targets = new byte[2];
        for (int i = targets.length - 1; i >=0; i--) {
            int offset = i * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }

    /**
     * long to byte[]
     *
     * @param s long
     * @return byte[]
     */
    public static byte[] longToByteArray(long s) {
        byte[] targets = new byte[2];
        for (int i = 0; i < 8; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }

    /**
     * 32位int转byte[]
     */
    public static byte[] int2byte(int res) {
        byte[] targets = new byte[4];
        targets[0] = (byte) (res & 0xff);// 最低位
        targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
        targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
        targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
        return targets;
    }

    /**
     * 将长度为2的byte数组转换为16位int
     *
     * @param res byte[]
     * @return int
     */
    public static int byteH2int(byte[] res) {
        int targets = (res[0] * 0xff) + (res[1]); //
        return targets;
    }


    /**
     * 将长度为2的byte数组转换为16位int
     *
     * @param res byte[]
     * @return int
     */
    public static int byteH3int(byte[] res) {
        int targets = (res[0] * 0xffff) + (res[1] * 0xff)+ (res[2]); //
        return targets;
    }

    /**
     * 将长度为2的byte数组转换为16位int
     *
     * @param res byte[]
     * @return int
     */
    public static int byteL2int(byte[] res) {
        // res = InversionByte(res);
        // 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000
        int targets = (res[0] * 0xff) + ((res[1] << 8) & 0xff00); // | 表示安位或
        return targets;
    }


    /**
     * 将长度为2的byte数组转换为16位int
     *
     * @param res byte[]
     * @return int
     */
    public static int byte2int(byte[] res) {
        // res = InversionByte(res);
        // 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000
        int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00); // | 表示安位或
        return targets;
    }

    /**
     * byte数组与short数组转换
     *
     * @param data
     * @return
     */
    public static short[] byteArray2ShortArray(byte[] data) {
        if (data == null) {
            // Log.e(TAG, "byteArray2ShortArray, data = null");
            return null;
        }

        short[] retVal = new short[data.length / 2];
        for (int i = 0; i < retVal.length; i++) {
            retVal[i] = (short) ((data[i * 2] & 0xff) | (data[i * 2 + 1] & 0xff) << 8);
        }

        return retVal;
    }

    /**
     * byte数组与short数组转换
     *
     * @param data
     * @return
     */
    public static byte[] shortArray2ByteArray(short[] data) {
        byte[] retVal = new byte[data.length * 2];
        for (int i = 0; i < retVal.length; i++) {
            int mod = i % 2;
            if (mod == 0) {
                retVal[i] = (byte) (data[i / 2]);
            } else {
                retVal[i] = (byte) (data[i / 2] >> 8);
            }
        }

        return retVal;
    }

    /**
     * 对象转数组
     *
     * @param obj
     * @return
     */
    public static byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }

    /**
     * 数组转对象
     *
     * @param bytes
     * @return
     */
    public static Object toObject(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return obj;
    }

    /**
     * 数组拼接
     *
     * @param first
     * @param second
     * @return
     */
    public static byte[] concat(byte[] first, byte[] second) {
        byte[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static byte[] bytesMerger(int size, byte[] by) {
        byte[] result = Arrays.copyOf(by, size);
        byte[] null_push={32};
        int i=0;
        for(i=by.length;i<size;i++)
        {
            System.arraycopy(null_push,0,result,i,1);
        }
        return result;
    }


    /**
     * 数组拼接
     *
     * @param first
     * @param second
     * @return
     */
    public static byte[] concat(byte[] first, byte[] second,byte[] three) {
        return  concat(concat(first,second),three);
    }

    /**
     * 数组拼接
     *
     * @param first
     * @param second
     * @return
     */
    public static byte[] concat(byte[] first, byte[] second,byte[] three, byte[] four) {
        return  concat(concat(first,second),concat(three,four));
    }

    /**
     * byte数组转 float
     *
     * @param b
     * @return
     */
    public static float getFloat(byte[] b) {
        int accum = 0;
        accum = accum | (b[0] & 0xff) << 0;
        accum = accum | (b[1] & 0xff) << 8;
        accum = accum | (b[2] & 0xff) << 16;
        accum = accum | (b[3] & 0xff) << 24;
        System.out.println(accum);
        return Float.intBitsToFloat(accum);
    }

    /**
     * byte数组转 float
     *
     * @param b
     * @return
     */
    public static float getGRFloat(byte[] b) {
        int accum = 0;
        for(int i=0;i<b.length;i++)
        {
            accum = accum | (b[i] & 0xff) << 0*8;
        }
        return Float.intBitsToFloat(accum);
    }

    public static int getInt(byte[] bytes) {
        return (int) ((char) bytes[0] | (char) bytes[1] << 8);
    }

    public static int getHInt(byte[] bytes) {
        int accum = 0;
        accum = accum | (bytes[0] & 0xff) << 8;
        accum = accum | (bytes[1] & 0xff) << 0;
        return accum;
    }

    /**
     * 数组截取
     *
     * @param b      原数组
     * @param off    开始位置
     * @param length 长度
     * @return 截取的数组
     */
    public static byte[] subByte(byte[] b, int off, int length) {
        byte[] b1 = new byte[length];
        System.arraycopy(b, off, b1, 0, length);
        return b1;
    }

    /**
     * byte数组转 String
     *
     * @param data
     * @return
     */
    public static String byte2String(byte[] data) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[data.length * 2];
        for (int j = 0; j < data.length; j++) {
            int v = data[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        String result = new String(hexChars);
        result = result.replace(" ", "");
        return result;
    }

    /**
     * 为Byte数组添加两位CRC校验
     *
     * @param bytes
     * @return
     */
//    public static byte[] getCRC(byte[] bytes) {
//        int CRC = 0x0000ffff;
//        int POLYNOMIAL = 0x0000a001;
//        int i, j;
//        for (i = 0; i < bytes.length; i++) {
//            CRC ^= ((int) bytes[i] & 0x000000ff);
//            for (j = 0; j < 8; j++) {
//                if ((CRC & 0x00000001) != 0) {
//                    CRC >>= 1;
//                    CRC ^= POLYNOMIAL;
//                } else {
//                    CRC >>= 1;
//                }
//            }
//        }
//        String remain = Integer.toHexString(CRC);
//        if (remain.length() < 4) {
//            remain = "0" + remain;
//        }
//        return addCRCByte(bytes, remain);
//    }



    /**
     * @param bytes
     * @param CRC
     * @return
     */
//    public static byte[] addCRCByte(byte[] bytes, String CRC) {
//        byte[] data = new byte[bytes.length + 2];
//        byte[] crcs = HexUtil.hexStringToBytes(CRC);
//        for (int i = 0; i < bytes.length; i++) {
//            data[i] = bytes[i];
//        }
//
//        for (int i = 0; i < crcs.length; i++) {
//            data[bytes.length + i] = crcs[crcs.length - i - 1];
//        }
//        return data;
//    }


    /**
     * 判断是否一个字节数组按顺序包含另一个字节数组
     *
     * @param pSrcByteArray
     * @param pSubByteArray
     * @return
     */
    public static boolean isIncludeByteArray(byte[] pSrcByteArray, byte[] pSubByteArray) {
        boolean retValue = false;
        int lvSrcByteArrayLen = pSrcByteArray.length;
        int lvSubByteArrayLen = pSubByteArray.length;

        while (true) {
            if (lvSrcByteArrayLen < lvSubByteArrayLen) {
                break;
            }
            int lvHitByteNumberValue = 0;
            for (int i = 0; i < lvSrcByteArrayLen; i++) {
                int tvByteValue = pSrcByteArray[i];
                if (lvHitByteNumberValue == pSubByteArray.length) {
                    retValue = true;
                    break;
                }
                if (tvByteValue == pSubByteArray[lvHitByteNumberValue]) {
                    lvHitByteNumberValue++;
                    continue;
                }
                lvHitByteNumberValue = 0;
                //剩余字节数
                int tvRemaindingByteLen = lvSrcByteArrayLen - i - 1;
                if (tvRemaindingByteLen < pSubByteArray.length) {
                    break;
                }
            }
            break;
        }
        return retValue;
    }


    /**
     * float 转 byte
     *
     */
    public static byte[] float2byte(float f) {
        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);

        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }

        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }
        return dest;
    }

    /**
     * 16进制表示的字符串转换为字节数组
     *
     * @param s 16进制表示的字符串
     * @return byte[] 字节数组
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] b = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个字节
            b[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return b;
    }

//    public static String binaryToHex(String binaryString){
//        // 将二进制字符串转换为十进制整数
//        int decimalValue = binaryString.toInt(2);
//        // 将十进制整数转换为十六进制字符串
//        return decimalValue.toString(16).toUpperCase();
//    }

}