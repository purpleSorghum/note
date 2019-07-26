package top.zigaoliang.algo;

import org.apache.log4j.Logger;
import sun.misc.BASE64Encoder;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.core.AlgoId;

import java.security.MessageDigest;

/**
 * HASH算法
 * Created by byc on 10/24/18.
 */
public class AlgoHashValue extends AlgoBase {
    private static Logger log = Logger.getLogger(AlgoHashValue.class);
    public AlgoHashValue() {
        super(AlgoId.HASHVALUE);
    }
    @Override
    public boolean find(String in) {
        return false;
    }

    @Override
    public int random(String in, StringBuilder out) {
        return  maskBase(in, out);
    }

    @Override
    public int mask(String in, StringBuilder out) {
        return  maskBase(in, out);
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        return  maskBase(in, out);
    }

    @Override
    public int cover(String in, StringBuilder out) {
        return 0;
    }

    public int maskBase(String in, StringBuilder out){
        ErrorCode errorCode = null;
        try{
            out.append(encrypt(in));
        }catch (Exception e){
            errorCode = ErrorCode.HASH_ENCRYPT;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    public String encrypt(String in) {
        //改写成只支持MD5加密
        Conf.ConfMaskHashValue confMaskHashValue = (Conf.ConfMaskHashValue)confMask;
        int length =  confMaskHashValue.crc32 == true? 32:16;
        String hashType = null;
        if (confMaskHashValue.type.getType() == 0) {
            //hash(源数据))
            hashType = in;
        } else if (confMaskHashValue.type.getType() == 1) {
            //(hash(源数据+盐值))
            hashType = in + confMaskHashValue.saltValue;
        } else {
            hashType = encryptMD5(in,false,length)
                        + encryptMD5(confMaskHashValue.saltValue,false,length);
        }
        return encryptMD5(hashType,false,length);
    }


    /**
     * SHA加密工具类
     *
     * @param
     * @return
     */
    public String encryptSHA(String encryptType, String src) {
        ErrorCode errorCode = null;
        encryptType = encryptType.replaceAll(" ", "").toUpperCase();
        try {
            MessageDigest sha1 = MessageDigest.getInstance(encryptType);
            sha1.update(src.getBytes("utf-8"));
            byte[] sha1Bytes = sha1.digest();
            String sign = bin2hex(sha1Bytes);
            return sign;
        } catch (Exception e) {
            errorCode = ErrorCode.HASH_SHA;
            log.debug(errorCode.getMsg() + "; 输入数据：" + src);
            return null;
        }
    }


    /**
     * 字符串进制转换
     *
     * @param bin
     * @return
     */
    public static String bin2hex(byte[] bin) {
        char hex[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bin.length; i++) {
            char upper = hex[(bin[i] & 0xf0) >>> 4];
            char lower = hex[(bin[i] & 0x0f)];
            sb = sb.append(upper).append(lower);
        }
        return sb.toString();
    }



    /**
     *  MD5加密工具类
     * @param pwd
     *            需要加密的字符串
     * @param isUpper
     *            字母大小写（false为小写，true为大小写）
     * @param bit
     *            加密的类型（16，32，64）
     * @return
     */
    public static String encryptMD5(String pwd, boolean isUpper, Integer bit) {
        ErrorCode errorCode = null;
        String md5 = new String();
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            if (bit == 64) {
                BASE64Encoder bw = new BASE64Encoder();
                String bsB64 = bw.encode(md.digest(pwd.getBytes("utf-8")));
                md5 = bsB64;
            } else {
                md.update(pwd.getBytes());
                byte b[] = md.digest();
                int i;
                StringBuilder sb = new StringBuilder("");
                for (int offset = 0; offset < b.length; offset++) {
                    i = b[offset];
                    if (i < 0)
                        i += 256;
                    if (i < 16)
                        sb.append("0");
                    sb.append(Integer.toHexString(i));
                }
                md5 = sb.toString();
                if(bit == 16) {
                    String md16 = md5.substring(8, 24).toString();
                    md5 = md16;
                    if (isUpper)
                        md5 = md5.toUpperCase();
                    return md5;
                }
            }
            if (isUpper)
                md5 = md5.toUpperCase();
        } catch (Exception e) {
            errorCode = ErrorCode.HASH_MD5;
            log.debug(errorCode.getMsg() + "; 输入数据：" + pwd);
        }
        return md5;
    }
}
