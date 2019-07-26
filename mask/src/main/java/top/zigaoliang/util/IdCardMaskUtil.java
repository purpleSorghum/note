package top.zigaoliang.util;


import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.conf.Region.AdressCode;
import top.zigaoliang.contant.RegionContants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 身份证算法
 */
public class IdCardMaskUtil {
    private static Logger log = Logger.getLogger(IdCardMaskUtil.class.getSimpleName());

    static IndexMapList indexMapList = null;

    static {
        indexMapList = HashMapUtil.convertToIndexMap(RegionContants.regionCodeArray);
    }

    public static boolean validateIdCard(String in) {
        ErrorCode errorCode = null;
        String idCardTemp = "";
        StringBuilder birthDay = new StringBuilder();
        try {
            if (CommonUtil.hasChinese(in)) {
                return false;
            }
            //验证省份证的长度
            if (in.length() != 15 && in.length() != 18) {
                errorCode = ErrorCode.ID_LACKDINGITS;
                log.debug(errorCode.getMsg() + "; 输入数据：" + in);
                return false;
            }
            // 如果身份证前6位的地区码不在Contants.regionCodeArray，则地区码有误
            if (CommonUtil.outOfIntRange(in.substring(0, 6))) {
                return false;
            }

            //验证身份证中的地区编号是否正确
            if (!HashMapUtil.containsKey(indexMapList.getMap(), in.substring(0, 6))) {
                return false;
            }
            //18身份证前17位是数字，如果是15位的身份证则所有号码都是数字
            if (in.length() == 18) {
                idCardTemp = in.substring(0, 17);
            } else if (in.length() == 15) {
                idCardTemp = in;
            }
            if (!CommonUtil.isDigit(idCardTemp)) {
                errorCode = ErrorCode.ID_LASTOFNUMBER;
                log.debug(errorCode.getMsg() + "; 输入数据：" + in);
                return false;
            }
            //判断出生年月是否有效 获得出生日期
            String year = "";
            if (in.length() == 18) {
                year = in.substring(6, 10);
                birthDay.append(year).append("-")
                            .append(in.substring(10, 12)).append("-")
                            .append(in.substring(12, 14));
            } else if (in.length() == 15) {
                year = in.substring(6, 8);
                birthDay.append(year).append("-")
                            .append(in.substring(8, 10)).append("-")
                            .append(in.substring(10, 12));
            }
            if (Util.isDate(birthDay.toString()) == false) {
                errorCode = ErrorCode.ID_INVALIDBIRTH;
                log.debug(errorCode.getMsg() + "; 输入数据：" + in);
                return false;
            }
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if ((gregorianCalendar.getTime().getTime() - sdf.parse(birthDay.toString()).getTime() < 0)
            ) {
                errorCode = ErrorCode.ID_INVALIDSCOPE;
                log.debug(errorCode.getMsg() + "; 输入数据：" + in);
                return false;
            }
            if (in.length() == 18) {
                if (Util.isVarifyCode(idCardTemp, in) == false) {
                    errorCode = ErrorCode.ID_INVALIDCALIBRATION;
                    log.debug(errorCode.getMsg() + "; 输入数据：" + in);
                    return false;
                }
            }
        } catch (ParseException e) {
            errorCode = ErrorCode.ID_BIRTH_UNKNOWN;
            log.info(errorCode.getMsg() + "; 输入数据：" + in + "年月日：" + birthDay.toString());
            e.printStackTrace();
        }
        return true;
    }


    //随机生成一个省份证号
    public static int getRandomIdCard(Conf.ConfMaskIdCard confMaskIdCard, String in, StringBuilder out) {
        ErrorCode errorCode = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        StringBuilder result = new StringBuilder();
        try {
            String provice = "";
            if (confMaskIdCard.province) {
                //保留省份
                provice = in.substring(0, 2);
            } else {
                //不保留省份
                provice = Integer.toString(Util.getProvice());
            }
            result.append(provice);
            String city = "";
            //根据省随机生成市编号
            if (confMaskIdCard.city) {
                //保留市
                city = in.substring(2, 4);
            } else {
                //不保留市
                //根据省号找到省号下面的某个市编号
                city = AdressCode.getCityCodeByProvice(provice);
            }
            result.append(city);
            String county = "";
            if (confMaskIdCard.county) {
                //保留区县
                county = in.substring(4, 6);
            } else {
                //不保留区县
                //根据县和市随机生成一个县code
//                county = in.substring(4, 6);
                county = AdressCode.getCountyCodeByCity(provice, city);
            }

            result.append(county);
            String birth = Util.getRandomDateRange();

            //如果身份证是15 那么出生年份是2位 且没有校验位
            if (in.length() == 15) {
                if (confMaskIdCard.birthday) {
                    result.append(in.substring(6, 12));
                } else {
                    result.append(birth.substring(2));
                }
            } else if (in.length() == 18) {
                if (confMaskIdCard.birthday) {
                    //保留生日
                    result.append(in.substring(6, 14));
                } else {
                    //不保留生日
                    result.append(birth);
                }
            } else {
                /**
                 *  走到这一步才发现长度位数不够，没关系，
                 *  如果这一列都是身份证
                 *  那么99%都不会走到这一步，比一开始脱敏就验证长度是否合法更节省性能
                 */
                out.append(in);
                return 0;
            }
            result.append(Util.getRandowNumber(3));
            if (in.length() == 18) {
                result.append(Util.comPuteIDcardCheck(result.toString()));
            }
        } catch (Exception e) {
            errorCode = ErrorCode.ID_RANDOM_UNKNOWN;
            log.debug(errorCode.getMsg() + "; 输入数据：" + result.toString());
            return errorCode.getCode();
        }
        out.append(result);
        return 0;

    }


    public static void maskBase(String in, Conf.ConfMaskIdCard confMaskIdCard, boolean flag, StringBuilder out) {
        if (confMaskIdCard.city && confMaskIdCard.province && confMaskIdCard.county) {
            out.append(Integer.parseInt(in.substring(0, 6)));
        } else {
            int index = Arrays.binarySearch(RegionContants.regionCodeArray, Integer.parseInt(in.substring(0, 6)));
            int[] regionCodeRange = {0, RegionContants.regionCodeArray.length - 1};
            int seedIndex = Util.maskBaseForInteger(regionCodeRange, index, confMaskIdCard.seed, flag);
            out.append(RegionContants.regionCodeArray[seedIndex]);
        }
        if (in.length() == 18) {
            //7 8 9 10  表示出生年份
            /*StringBuilder newBirth = new StringBuilder();
            int maskedYear = 0;
            if (Integer.parseInt(in.substring(6, 10)) > Util.yearRangeIdCard[1]
                        || Integer.parseInt(in.substring(6, 10)) < Util.yearRangeIdCard[0]) {
                maskedYear = Integer.parseInt(in.substring(6, 10));
            } else {
                maskedYear = Util.maskBaseForInteger(Util.yearRangeIdCard, Integer.parseInt(in.substring(6, 10)),
                            confMaskIdCard.seed, flag);
            }
            out.append(maskedYear);
            newBirth.append(maskedYear);
            //11 12  表示月份
            int maskedMonth = Util.maskBaseForInteger(Util.monthRange, Integer.parseInt(in.substring(10, 12)),
                        confMaskIdCard.seed, flag);
            out.append(CommonUtil.fillNumberStr(Integer.toString(maskedMonth), 2));
            newBirth.append(CommonUtil.fillNumberStr(Integer.toString(maskedMonth), 2));

            // 13 14 表示日期
            out.append(Util.maskDayByYearAndMonth(in.substring(6, 14), newBirth.toString()+"01", confMaskIdCard.seed, flag));*/
            out.append(in.substring(6,14));
            char[] temp = in.substring(14, 16).toCharArray();
            for (int i = 0; i < temp.length; i++) {
                out.append(Util.maskBaseForInteger(Util.numberArray, temp[i] - 48, confMaskIdCard.seed, flag));
            }
            int sex = Integer.parseInt(in.substring(16, 17));
            if (confMaskIdCard.sex) {
                //保留性别
                if (sex % 2 != 0) {
                    //男
                    int oldIndex = Arrays.binarySearch(Util.numberSingle, sex);
                    if (oldIndex >= 0) {
                        int[] rangeIndex = {0, Util.numberSingle.length - 1};
                        int newIndex = Util.maskBaseForInteger(rangeIndex, oldIndex, confMaskIdCard.seed, flag);
                        out.append(Util.numberSingle[newIndex]);
                    }
                } else {
                    //女
                    int oldIndex = Arrays.binarySearch(Util.numberEven, sex);
                    if (oldIndex >= 0) {
                        int[] rangeIndex = {0, Util.numberEven.length - 1};
                        int newIndex = Util.maskBaseForInteger(rangeIndex, oldIndex, confMaskIdCard.seed, flag);
                        out.append(Util.numberEven[newIndex]);
                    }
                }
            } else {
                //不保留姓别
                out.append(Util.maskBaseForInteger(Util.numberArray, sex, confMaskIdCard.seed, flag));
            }
            out.append(Util.comPuteIDcardCheck(out.toString()));
        }

        if (in.length() == 15) {
            //7 8   表示出生年份
            /*StringBuilder newBirth = new StringBuilder();
            int maskedYear = Util.maskBaseForInteger(Util.yearRange, Integer.parseInt("19" + in.substring(6, 8)),
                        confMaskIdCard.seed, flag);
            out.append(Integer.toString(maskedYear).substring(2));
            //11 12  表示月份
            int maskedMonth = Util.maskBaseForInteger(Util.monthRange, Integer.parseInt(in.substring(8, 10)),
                        confMaskIdCard.seed, flag);
            out.append(CommonUtil.fillNumberStr(Integer.toString(maskedMonth), 2));
            newBirth.append(CommonUtil.fillNumberStr(Integer.toString(maskedMonth), 2));
            // 13 14 表示日期
            out.append(Util.maskDayByYearAndMonth(in.substring(6, 12), newBirth.toString(), confMaskIdCard.seed, flag));*/
            out.append(in.substring(6, 12));
            char[] charArray = in.substring(14).toCharArray();
            for (int i = 0; i < charArray.length; i++) {
                out.append(Util.maskBaseForInteger(Util.numberArray, charArray[i] - 48, confMaskIdCard.seed, flag));
            }
            if (confMaskIdCard.sex) {
                int sex = Integer.parseInt(in.substring(14));
                //保留性别
                if (sex % 2 != 0) {
                    //男
                    int oldIndex = Arrays.binarySearch(Util.numberSingle, sex);
                    if (oldIndex >= 0) {
                        int[] rangeIndex = {0, Util.numberSingle.length - 1};
                        int newIndex = Util.maskBaseForInteger(rangeIndex, oldIndex, confMaskIdCard.seed, flag);
                        out.append(Util.numberSingle[newIndex]);
                    }
                } else {
                    //女
                    int oldIndex = Arrays.binarySearch(Util.numberEven, sex);
                    if (oldIndex >= 0) {
                        int[] rangeIndex = {0, Util.numberEven.length - 1};
                        int newIndex = Util.maskBaseForInteger(rangeIndex, oldIndex, confMaskIdCard.seed, flag);
                        out.append(Util.numberEven[newIndex]);
                    }
                }
            } else {
                //不保留姓别
                int sex = Integer.parseInt(in.substring(14));
                out.append(Util.maskBaseForInteger(Util.numberArray, sex, confMaskIdCard.seed, flag));
            }
        }
    }


    /**
     * 通过身份证号获取生日
     *
     * @param idCard 身份证
     * @return
     */
    public static String getBirthday(String idCard) {
        if (validateIdCard(idCard)) {
            //跟据身份证的位数截取日期 2018/12/18 yehuan
            return idCard.length() == 15 ? "19" + idCard.substring(6, 12) : idCard.substring(6, 14);
        } else {
            return "19000101";
        }
    }


    public static int getAge(String idCard) {
        int age = 0;
        if (validateIdCard(idCard)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            Date parse;
            try {
                parse = format.parse(getBirthday(idCard));
                Calendar now = Calendar.getInstance();
                now.setTime(new Date());
                Calendar birth = Calendar.getInstance();
                birth.setTime(parse);
                if (!birth.after(now)) {
                    age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
                    if (now.get(Calendar.DAY_OF_YEAR) > birth.get(Calendar.DAY_OF_YEAR)) {
                        age += 1;
                    }
                }
            } catch (ParseException e) {
                return age;
            }
            return age;
        } else {
            return age;
        }
    }
}
