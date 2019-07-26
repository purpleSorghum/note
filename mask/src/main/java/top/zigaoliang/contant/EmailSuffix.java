package top.zigaoliang.contant;

public class EmailSuffix {
    //邮箱后缀 英文域名
    public static String[] emailSuffixArr = {".com",".cn",".com.cn",".net"};

    //中文域名
    /**
     *   中英文域名分开主要原因是：
     *   中文域名只是少数，保持中文域名脱敏成中文域名，
     *   英文域名脱敏成英文域名的原则
     */
    public static String[] emailSuffixArrChinese = {".公司",".网络",".中国"};
}
