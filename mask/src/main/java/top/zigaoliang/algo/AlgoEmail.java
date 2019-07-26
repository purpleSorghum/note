package top.zigaoliang.algo;


import org.apache.log4j.Logger;
import top.zigaoliang.common.FileHelper;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.contant.EmailSuffix;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.AlgoMaskUtil;
import top.zigaoliang.util.CommonUtil;
import top.zigaoliang.util.Util;

import java.util.List;

/**
 * 电子邮件算法
 * Created by byc on 10/20/18.
 */
public class AlgoEmail extends AlgoBase {
    static List<String> emailSuffixList = null;
    static List<String> emailSuffixChineseList = null;
    private static Logger log = Logger.getLogger(AlgoEmail.class.getSimpleName());
    public AlgoEmail() {
        super(AlgoId.EMAIL);
        attr = 15;
    }
    static {
        emailSuffixList = FileHelper.readSource("/emailSuffix.txt",String.class);
        emailSuffixChineseList = FileHelper.readSource("/emailSuffixChinese.txt",String.class);
    }
    @Override
    public boolean find(String in) {
        if(in.length() > 50 || in.length() < 6){
            return false;
        }
        if(!in.contains(".") || CommonUtil.countStr(in, "@") != 1){
            return false;
        }
        String[] emailArray = in.split("@");
        if(emailArray.length != 2){
            return false;
        }
        if(emailArray[0].length()>30 || emailArray[0].length()< 2){
            return false;
        }
        if(emailArray[1].length() > 20 || emailArray[1].length() < 3){
            return false;
        }
        if(!CommonUtil.strEndWithSpecial(emailArray[1],EmailSuffix.emailSuffixArr)){
            //中英文域名都找不到，不是邮箱
            if(!CommonUtil.strEndWithSpecial(emailArray[1],EmailSuffix.emailSuffixArrChinese)){
                return false;
            }
        }
        return true;
    }

    @Override
    public int random(String in, StringBuilder out) {
        Conf.ConfMaskEmail confMaskEmail = (Conf.ConfMaskEmail)confMask;
        ErrorCode errorCode = null;
        try {
            String[] emailArray = in.split("@");
            boolean flag = false;
            //true:域名为英文  false:域名为中文
            if(CommonUtil.strEndWithSpecial(emailArray[1],EmailSuffix.emailSuffixArr)){
                flag = true;
            }
            if (confMaskEmail.reservePart) {
                //保留前缀
                out.append(emailArray[0]).append(getRandomEmailSuffix(flag));
            }else{
                //保留域名
                for (int i = 0; i < emailArray[0].length(); i++) {
                    int number = Util.getNumByRange(0, Util.emailBase.length - 1);
                    out.append(Util.emailBase[number]);
                }
                out.append("@").append(emailArray[1]);
            }
        } catch (Exception e) {
            //电子邮箱格式不正确
            errorCode = ErrorCode.EMAIL_RANDOM_UNKNOWN;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    @Override
    public int mask(String in, StringBuilder out) {
        return maskBase(in,out,true);
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        return maskBase(in,out,false);
    }

    @Override
    public int cover(String in, StringBuilder out) {
        Conf.ConfMaskEmail confMaskEmail = (Conf.ConfMaskEmail)confMask;
        String[] emailArray = in.split("@");
        if(confMaskEmail.coverType){
            out.append(CommonUtil.coverBySymbol(confMaskEmail.symbol,emailArray[0].length())).append("@").append(emailArray[1]);
        }else{
            out.append(emailArray[0]).append("@").append(CommonUtil.coverBySymbol(confMaskEmail.symbol,emailArray[0].length()));
        }
        return 0;
    }


    public int maskBase(String in, StringBuilder out, boolean flag) {
        Conf.ConfMaskEmail confMaskEmail = (Conf.ConfMaskEmail) confMask;
        ErrorCode errorCode = null;
        try {
            out.append(AlgoMaskUtil.maskNumberAndChar(in.split("@")[0],
                    confMaskEmail.seed, flag));
            out.append("@").append(in.split("@")[1]);
        } catch (Exception e) {
            errorCode = ErrorCode.EMAIL_MASK_UNKNOWN;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    /**
     * 随机获得一个邮箱域名
     * @param flag true 随机生成一个英文的域名
     *             false 随机生成一个中文的域名
     */
    public String getRandomEmailSuffix(boolean flag) {
        if(flag){
            //英文的域名
            return emailSuffixList.get(Util.getNumByRange(0, emailSuffixList.size() - 1));
        }else{
            //中文的域名
            return emailSuffixChineseList.get(Util.getNumByRange(0,emailSuffixChineseList.size()-1));
        }
    }

}
