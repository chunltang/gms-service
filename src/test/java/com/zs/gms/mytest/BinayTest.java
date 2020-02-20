package com.zs.gms.mytest;

import org.apache.commons.codec.binary.BinaryCodec;
import org.apache.commons.lang3.ArrayUtils;
import sun.awt.SunHints;

import java.math.BigDecimal;
import java.util.function.BinaryOperator;

public class BinayTest {

    public static void main(String[] args) {
        /*byte a = -127;
        int z = 0x00000040;
        System.out.println(0x00000002|0x00000001);//0010 | 0001
        System.out.println(Integer.toBinaryString(64));
        System.out.println();
        byte[] bytes={1,0,0,0};
        System.out.println(Byte2Int(bytes));
        byte[] toByte = IntToByte(1);
        for (byte b : toByte) {
            System.out.println(b);
        }*/

        /*byte[] bytes={65,71,63,63,67,63,63,63};
        byte[] bytes2={65,37,63,29,89,48,63,14};
        byte[] bytes3={14,63,48,89,29,63,37,65};
        double v = bytes2Double(bytes3);
        BigDecimal decimal = new BigDecimal(String.valueOf(v));
        System.out.println(decimal);
        BigDecimal setScale = decimal.setScale(4,BigDecimal.ROUND_HALF_DOWN);
        System.out.println(setScale);
        System.out.println(v);
        byte[] bytes1 = double2Bytes(v);
        for (byte b : bytes1) {
            System.out.println(b);
        }*/

        /**
         * [0, 0, 0, 1, 0, 0, 0, 101, 0, 0, 0, 0, 0, 0, 39, 17, 0, 0, 0, 1,
         * 65, 37, 63, 29, 89, 48, 63, 14, 65, 71, 63, 63, 67, 63, 63, 63,
         * 64, 73, 63, 106, 126, 63, 63, 34, 0, 0, 0, 0, 67, 63, 63, 0,
         * 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 63, 63, 54, 63, 63, 63, 49, 102, 0, 0, 0, 0, 0, 0, 0, 0, 0]
         * */

        /**
         * "1,0,0,0,101,0,0,0,0,0,0,0,17,39,0,0,1,0,0,0,14,190,48,89,29,160,37,65,217,206,247,67,187,143,71,65,34,219,249,126,106,252,73,64,0,0,0,0,0,160,174,67,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,102,49,177,249,184,54,148,63,0,0,0,0,0,0,0,0,0,"
           [1, 0, 0, 0, 101, 0, 0, 0, 0, 0, 0, 0, 17, 39, 0, 0, 1, 0, 0, 0, 14, 63, 48, 89, 29, 63, 37, 65, 63, 63, 63, 67, 63, 63, 71, 65, 34, 63, 63, 126, 106, 63, 73, 64, 0, 0, 0, 0, 0, 63, 63, 67, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 102, 49, 63, 63, 63, 54, 63, 63, 0, 0, 0, 0, 0, 0, 0, 0, 0]         * */
        //[1, 0, 0, 0, 101, 0, 0, 0, 0, 0, 0, 0, 17, 39, 0, 0, 1, 0, 0, 0, 14, -17, -65, -67, 48, 89, 29, -17, -65, -67, 37, 65, -17, -65, -67, -17, -65, -67, -17, -65, -67, 67, -17, -65, -67, -17, -65, -67, 71, 65, 34, -17, -65, -67, -17, -65, -67, 126, 106, -17, -65, -67, 73, 64, 0, 0, 0, 0, 0, -17, -65, -67, -17, -65, -67, 67, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 102, 49, -17, -65, -67, -17, -65, -67, -17, -65, -67, 54, -17, -65, -67, 63, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        byte[] b={65, 37, 63, 29, 89, 48, 63, 14};
        System.out.println(bytes2Double(b));
        char z=63;
        byte a=(byte)190;
        System.out.println(a);

        //一个字节是8位，一个int是32位，对于正数原码来说，反码和补码为原码本身
        //对于负数来说，原码取反为反码，反码+1为补码
        //一个字节是8位，转为int是32位，高位补充符号位，即正数高位补0，负数高位补1
        //&0xFF作用是保持二进制补码的一致性，即高位都为0，地位保持不变
        //16进制是2个字节，一个字节8位，0xFF和0x00FF是一样的，相当于高位补0
    }
    public static double bytes2Double(byte[] arr) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) (arr[i] & 0xff)) << (8 * i);
        }
        return Double.longBitsToDouble(value);
    }

    public static byte[] double2Bytes(double d) {
        long value = Double.doubleToRawLongBits(d);
        byte[] byteRet = new byte[8];
        for (int i = 0; i < 8; i++) {
            byteRet[i] = (byte) ((value >> 8 * i) & 0xff);
        }
        return byteRet;
    }







    public static int Byte2Int(byte[] bytes) {//0000 0001 0000 0000 0000 0000 0000 0000
        return (bytes[0] & 0xff) << 24
                | (bytes[1] & 0xff) << 16
                | (bytes[2] & 0xff) << 8
                | (bytes[3] & 0xff);

    }

    public static byte[]IntToByte(int num){//0000 0000 0000 0000 0000 0000 0000 0001
        byte[]bytes=new byte[4];           //0111 1111 1111 1111 1111 1111 1111 1111
        bytes[0]=(byte) ((num>>24)&0xff);
        bytes[1]=(byte) ((num>>16)&0xff);
        bytes[2]=(byte) ((num>>8)&0xff);
        bytes[3]=(byte) (num&0xff);
        return bytes;
    }
}
