package top.zigaoliang.algo;

import com.google.common.base.Strings;
import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.conf.Region.TelePhone;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.AlgoMaskUtil;
import top.zigaoliang.util.HashMapUtil;
import top.zigaoliang.util.IndexMapList;
import top.zigaoliang.util.PhoneUtil;
import top.zigaoliang.util.Util;

/**
 * 电话号码算法
 * Created by byc on 10/24/18.
 */
public class AlgoPhone extends AlgoBase {
    private static Logger log = Logger.getLogger(AlgoPhone.class);
    public AlgoPhone() {
        super(AlgoId.PHONE);
    }
    private AlgoTelephone algoTelephone = new AlgoTelephone();
    private AlgoCellphone algoCellphone = new AlgoCellphone();

    static IndexMapList indexMapListCell = null;
    static IndexMapList indexMapListTell = null;
    static {
        indexMapListCell = HashMapUtil.convertToIndexMap("/cellPhoneCode.txt");
        indexMapListTell = HashMapUtil.convertToIndexMap("/telePhone.txt",TelePhone.class,"areaCode");
    }

    @Override
    public boolean find(String in) {
        if (Strings.isNullOrEmpty(in)) {
            return false;
        }
        boolean isName = algoTelephone.find(in);
        return isName ? isName : algoCellphone.find(in);
    }

    @Override
    public int random(String in, StringBuilder out) {
        Conf.ConfMaskPhone confMaskPhone = (Conf.ConfMaskPhone) confMask;
        ErrorCode errorCode = null;
        try {
            if (algoTelephone.find(in)) {
                //对座机号仿真
                randomTelePhone(in, out, confMaskPhone);
            } else {
                //手机号仿真
                randomCellPhone(in, out, confMaskPhone);
            }
        } catch (Exception e) {
            errorCode = ErrorCode.PHONE_MASK_ERROR;
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

    public AlgoCover coverPramConf(){
        Conf.ConfMaskPhone confMaskPhone = (Conf.ConfMaskPhone) confMask;
        Conf.ConfMaskCover confMaskCover =new  Conf.ConfMaskCover();
        confMaskCover.symbol = confMaskPhone.symbol;
        confMaskCover.begin = confMaskPhone.begin;
        confMaskCover.end = confMaskPhone.end;
        confMaskCover.direction = confMaskPhone.direction;
        AlgoCover algoCover = new AlgoCover();
        algoCover.init(confMaskCover);
        return algoCover;
    }


    @Override
    public int cover(String in, StringBuilder out) {
        return coverPramConf().cover(in,out);
    }

    public int maskBase(String in, StringBuilder out, boolean flag){
        Conf.ConfMaskPhone confMaskPhone = (Conf.ConfMaskPhone) confMask;
        ErrorCode errorCode = null;
        in = in.trim();
        try {
            if (algoTelephone.find(in)) {
                maskTelePhone(in, out, confMaskPhone, flag);
            } else {
                maskCellPhone(in, out, confMaskPhone, flag);
            }
        } catch (Exception e) {
            errorCode = ErrorCode.PHONE_MASK_ERROR;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }



    /**
     * 对座机号仿真
     * @param in
     * @param out
     * @param confMaskPhone
     */
    public void randomTelePhone(String in, StringBuilder out, Conf.ConfMaskPhone confMaskPhone) {
        String[] telePhoneArray = AlgoTelephone.getSplitTelePhoneMask(in);
        out.append(PhoneUtil.getRandomRegionCode(indexMapListTell.getList())).append(telePhoneArray[1]);
        if (confMaskPhone.keepMiddle && confMaskPhone.keepLast) {
            out.append(Util.getRandowNumber(2)).append(PhoneUtil.getMiddleTelePhone(telePhoneArray[2])).append(PhoneUtil.getTelePhoneLastSome(telePhoneArray[2]));
        }
        if (confMaskPhone.keepMiddle && !confMaskPhone.keepLast) {
            out.append(Util.getRandowNumber(2)).append(PhoneUtil.getMiddleTelePhone(telePhoneArray[2])).append(Util.getRandowNumber(PhoneUtil.getTelePhoneLastSome(telePhoneArray[2]).length()));
        }
        if (!confMaskPhone.keepMiddle && !confMaskPhone.keepLast) {
            out.append(Util.getRandowNumber(telePhoneArray[2].length()));
        }
        if (!confMaskPhone.keepMiddle && confMaskPhone.keepLast) {
            out.append(Util.getRandowNumber( PhoneUtil.getTelePhoneFirst(telePhoneArray[2]).length())).append(PhoneUtil.getTelePhoneLast(telePhoneArray[2]));
        }
    }

    /**
     * 对手机号进行仿真
     * @param in
     * @param out
     * @param confMaskPhone
     */
    public void randomCellPhone(String in, StringBuilder out, Conf.ConfMaskPhone confMaskPhone){
        out.append(PhoneUtil.getRandomCellRegionCode(indexMapListCell.getList())).append(Util.getNumByRange(0,9));
        out.append(confMaskPhone.keepMiddle == true? PhoneUtil.getCellPhoneMiddle(in):Util.getRandowNumber(3))
           .append(confMaskPhone.keepLast == true? PhoneUtil.getTelePhoneLast(in):Util.getRandowNumber(4));
    }

    /**
     * 对座机号进行脱敏
     * @param in
     * @param out
     * @param confMaskPhone
     */
    public void maskTelePhone(String in, StringBuilder out, Conf.ConfMaskPhone confMaskPhone, boolean flag){
        String[] telePhoneArray = PhoneUtil.splitTelePhone(in);
        int oleIndex = indexMapListTell.getMap().get(telePhoneArray[0]);
        int[] indexRange = {0, indexMapListTell.getList().size()-1};
        int newIndex = Util.maskBaseForInteger(indexRange, oleIndex, confMaskPhone.seed, flag);
        String region = indexMapListTell.getList().get(newIndex);
        out.append(region).append(telePhoneArray[2]);
        if (confMaskPhone.keepMiddle) {
            out.append(AlgoMaskUtil.maskNumberStr(telePhoneArray[1].substring(0, 2), confMaskPhone.seed, flag))
                    .append(PhoneUtil.getMiddleTelePhone(telePhoneArray[1]))
                    .append(AlgoMaskUtil.maskNumberStr(PhoneUtil.getTelePhoneLastSome(telePhoneArray[1]), confMaskPhone.seed, flag));
        } else {
            out.append(AlgoMaskUtil.maskNumberStr(telePhoneArray[1], confMaskPhone.seed, flag));
        }
    }

    /**
     * 对手机号进行脱敏
     * @param in
     * @param out
     * @param confMaskPhone
     * @param flag
     */
    public void maskCellPhone(String in, StringBuilder out, Conf.ConfMaskPhone confMaskPhone, boolean flag){
        out.append(PhoneUtil.maskCellPhoneRegion(indexMapListCell,in,confMaskPhone.seed,flag))
                .append(Util.maskBaseForInteger(Util.numberArray,Integer.parseInt(in.substring(3,4)),confMaskPhone.seed,flag))
                .append(confMaskPhone.keepMiddle == true?PhoneUtil.getCellPhoneMiddle(in)
                 :AlgoMaskUtil.maskNumberStr(in.substring(4,7),confMaskPhone.seed,flag))
                .append(AlgoMaskUtil.maskNumberStr(in.substring(7),confMaskPhone.seed,flag));
    }

}
