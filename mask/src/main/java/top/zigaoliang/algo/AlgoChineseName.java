package top.zigaoliang.algo;


import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.AlgoMaskUtil;
import top.zigaoliang.util.CommonUtil;
import top.zigaoliang.util.HashMapUtil;
import top.zigaoliang.util.IndexMapList;
import top.zigaoliang.util.Util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * 中文姓名算法
 * Created by byc on 10/24/18.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class AlgoChineseName extends AlgoBase {
    private static Logger log = Logger.getLogger(AlgoChineseName.class.getSimpleName());
    private static IndexMapList indexMapListSingle;
    private static IndexMapList indexMapListDouble;
    /**
     * 初始化姓名字典
     */
    static {
        indexMapListSingle = HashMapUtil.convertToIndexMap("/chineseName/singleSurname.txt");
        indexMapListDouble = HashMapUtil.convertToIndexMap("/chineseName/doubleSurname.txt");
    }

    public AlgoChineseName() {
        super(AlgoId.CHINESENAME);
        attr = 4;
    }

    @Override
    public boolean find(String in) {
        if (in.length() < 2 || in.length() > 4) {
            return false;
        }

        String surname = getSurname(in);
        String name = getName(in, surname);

        if (StringUtils.isBlank(surname) || StringUtils.isBlank(name)) {
            return false;
        }
        //如果是单姓，那么名字总长度只能是2或3
        if (surname.length() == 1) {
            if (in.length() != 2 && in.length() != 3) {
                return false;
            }
        }
        //如果是复姓，那么名字总长度只能是3或4
        if (surname.length() == 2) {
            if (in.length() != 3 && in.length() != 4) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int random(String in, StringBuilder out) {
        Conf.ConfMaskChineseName confMaskChineseName = (Conf.ConfMaskChineseName) confMask;
        return randomChineseName(in, out, confMaskChineseName);
    }

    public int random(String in, StringBuilder out, Conf.ConfMaskCustomerName confMaskCustomerName) {
        Conf.ConfMaskChineseName confMaskChineseName = new Conf.ConfMaskChineseName();
        confMaskChineseName.firstName = confMaskCustomerName.firstName;
        confMaskChineseName.seed = confMaskCustomerName.seed;
        return randomChineseName(in, out, confMaskChineseName);
    }

    public int randomChineseName(String in, StringBuilder out, Conf.ConfMaskChineseName confMaskChineseName) {
        if (in.length() < 2 || in.length() > 4) {
            out.append(in);
            return 0;
        }
        ErrorCode errorCode = null;
        //获取该姓名的姓
        String surname = getSurname(in);
        if(StringUtils.isBlank(surname)){
            out.append( AlgoMaskUtil.getRandomChineseFrom3500(in.length()));
            return 0;
        }

        String name = getName(in, surname);
        try {
            if (confMaskChineseName.firstName) {
                out.append(surname);
                //保留姓
                if (confMaskChineseName.length) {
                    //保留字数
                    out.append(AlgoMaskUtil.getRandomChineseFrom3500(name.length()));
                } else {
                    //不保留字数
                    int nameLength = Util.getNumByRange(0, 9) % 2 == 0 ? 1 : 2;
                    out.append(AlgoMaskUtil.getRandomChineseFrom3500(nameLength));
                }
            } else {
                //保留名
                if (confMaskChineseName.length) {
                    if (surname.length() == 1) {
                        int surNameIndex = Util.getNumByRange(0, indexMapListSingle.getList().size() - 1);
                        out.append(indexMapListSingle.getList().get(surNameIndex));
                    } else {
                        int surNameIndex = Util.getNumByRange(0, indexMapListDouble.getList().size() - 1);
                        out.append(indexMapListDouble.getList().get(surNameIndex));
                    }
                } else {
                    int seed = Util.getNumByRange(0,9);
                    if(seed % 2 == 0){
                        int surNameIndex = Util.getNumByRange(0, indexMapListSingle.getList().size() - 1);
                        out.append(indexMapListSingle.getList().get(surNameIndex));
                    }else {
                        int surNameIndex = Util.getNumByRange(0, indexMapListDouble.getList().size() - 1);
                        out.append(indexMapListDouble.getList().get(surNameIndex));
                    }
                }
                out.append(name);
            }
        } catch (Exception e) {
            errorCode = ErrorCode.CHINESENAME_RANDOM_ERROR;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    @Override
    public int mask(String in, StringBuilder out) {
        Conf.ConfMaskChineseName confMaskChineseName = (Conf.ConfMaskChineseName) confMask;
        return maskBase(in, out, true, confMaskChineseName);
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        Conf.ConfMaskChineseName confMaskChineseName = (Conf.ConfMaskChineseName) confMask;
        return maskBase(in, out, false, confMaskChineseName);
    }

    @Override
    public int cover(String in, StringBuilder out) {
        Conf.ConfMaskChineseName confMaskChineseName = (Conf.ConfMaskChineseName) confMask;
        //获取该姓名的姓
        String surname = getSurname(in);
        //获取该姓名的名
        String name = getName(in, surname);
        switch (confMaskChineseName.coverType) {
            case 1:
                out.append(CommonUtil.coverBySymbol(confMaskChineseName.symbol, surname.length()))
                            .append(name);
                break;
            case 2:
                out.append(surname).append(CommonUtil.coverBySymbol(confMaskChineseName.symbol, name.length()));
                break;
            case 3:
                //遮蔽中间字
                if (in.length() == 3 || in.length() == 4) {
                    String[] arrChar = in.split("");
                    out.append(arrChar[0])
                                .append(CommonUtil.coverBySymbol(confMaskChineseName.symbol, arrChar.length - 1))
                                .append(arrChar[arrChar.length - 1]);
                } else {
                    out.append(CommonUtil.coverBySymbol(confMaskChineseName.symbol, 2));
                }
                break;
            case 4:
                //保留中间字
                if (in.length() == 3 || in.length() == 4) {
                    String[] arrChar = in.split("");
                    out.append(confMaskChineseName.symbol);
                    for (int i = 1; i < arrChar.length - 1; i++) {
                        out.append(arrChar[i]);
                    }
                    out.append(confMaskChineseName.symbol);
                } else {
                    out.append(in);
                }
                break;
            default:
                out.append(in);
        }
        return 0;
    }

    public int maskBase(String in, StringBuilder out, boolean flag, Conf.ConfMaskCustomerName confMaskCustomerName) {
        Conf.ConfMaskChineseName confMaskChineseName = new Conf.ConfMaskChineseName();
        confMaskChineseName.firstName = confMaskCustomerName.firstName;
        confMaskChineseName.seed = confMaskCustomerName.seed;
        return maskBase(in, out, flag, confMaskChineseName);
    }

    public int maskBase(String in, StringBuilder out, boolean flag, Conf.ConfMaskChineseName confMaskChineseName) {
        if (in.length() < 2 || in.length() > 4) {
            out.append(in);
            return 0;
        }
        ErrorCode errorCode = null;
        try {
            String surname = getSurname(in);
            if(StringUtils.isBlank(surname)){
                out.append(in);
                return 0;
            }
            /**
             * 可逆的时候，如何姓为空，就不是一个合法的姓名，
             * 那么就不脱敏，因为脏数据脱敏后逆不回来
             */
            String name = getName(in, surname);
            if (confMaskChineseName.firstName) {
                out.append(surname);
                out.append(AlgoMaskUtil.maskChinese(name, confMaskChineseName.seed, flag));
            } else {
                out.append(maskSurmane(surname, confMaskChineseName.seed, flag)).append(name);
            }
        } catch (Exception e) {
            errorCode = ErrorCode.CHINESENAME_MASK_ERROR;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    //获取该姓名的姓
    public String getSurname(String in) {
        String surnameOne = in.substring(0, 1);
        String surnameTwo = in.substring(0, 2);
        //先查找复姓 在查找单姓  顺序不能换
        //原因 复姓 夏侯  可能 找到单姓 夏
        if (HashMapUtil.containsKey(indexMapListDouble.getMap(), surnameTwo)) {
            if (in.length() != 2) {
                return surnameTwo;
            } else if (HashMapUtil.containsKey(indexMapListSingle.getMap(), surnameOne)) {
                return surnameOne;
            }
        } else if (HashMapUtil.containsKey(indexMapListSingle.getMap(), surnameOne)) {
            return surnameOne;
        }
        return "";
    }

    //获得名
    public String getName(String in, String surname) {
        return in.substring(surname.length());
    }

    //对姓进行脱敏
    public String maskSurmane(String surName, int seed, boolean flag) {
        if (surName.length() == 1) {
            int sruNameIndex = HashMapUtil.getMapValue(indexMapListSingle.getMap(), surName);
            int[] sruNameRange = {0, indexMapListSingle.getList().size() - 1};
            return indexMapListSingle.getList().get(Util.maskBaseForInteger(sruNameRange, sruNameIndex, seed, flag));
        }
        if (surName.length() == 2) {
            int sruNameIndex = HashMapUtil.getMapValue(indexMapListDouble.getMap(), surName);
            int[] sruNameRange = {0, indexMapListDouble.getList().size() - 1};
            return indexMapListDouble.getList().get(Util.maskBaseForInteger(sruNameRange, sruNameIndex, seed, flag));
        }
        return "";
    }

    @Override
    public int random(StringBuilder out) {
        /**
         * 1.从百家姓字典中随机定位姓
         * 2.从中文字典中随机定位名称2个以内汉字
         * 3.将姓与名称组合为随机姓名
         * 4.可设置产生数据的数量
         */
        int seed = Util.getNumByRange(0,9);
        if(seed % 2 == 0){
            int surNameIndex = Util.getNumByRange(0, indexMapListSingle.getList().size() - 1);
            out.append(indexMapListSingle.getList().get(surNameIndex));
        }else {
            int surNameIndex = Util.getNumByRange(0, indexMapListDouble.getList().size() - 1);
            out.append(indexMapListDouble.getList().get(surNameIndex));
        }
        //拼接名字时随机1-2个汉字
        int _nameNum = Util.getNumByRange(1, 2);
        out.append(AlgoMaskUtil.getRandomChineseFrom3500(_nameNum));
        return 0;
    }

    @Override
    public Object[] validateMaskData(Conf.ConfMask conf, String in, String out) {
        if (Strings.isNullOrEmpty(in) && Strings.isNullOrEmpty(out)) {
            return new Object[]{true, null};
        }
        if (Strings.isNullOrEmpty(in) || Strings.isNullOrEmpty(out)) {
            return new Object[]{false, "in or out is null"};
        }
        try {
            String infirstName = this.getSurname(in);
            String outfirstName = this.getSurname(out);
            switch (conf.process) {
                case RANDOM:
                case MASK:
                case UNMASK:
                    if (((Conf.ConfMaskChineseName) conf).firstName) {
                        if (!infirstName.equals(outfirstName)) {
                            return new Object[]{false, "违反保留姓策略"};
                        }
                        if (in.substring(infirstName.length()).equals(out.substring(outfirstName.length()))) {
                            return new Object[]{false, "违反保留姓策略"};
                        }
                    } else {
                        if (infirstName.equals(outfirstName)) {
                            return new Object[]{false, "违反保留名策略"};
                        }
                        if (!in.substring(infirstName.length()).equals(out.substring(outfirstName.length()))) {
                            return new Object[]{false, "违反保留名策略"};
                        }
                    }

                    if (((Conf.ConfMaskChineseName) conf).length) {
                        return in.length() == out.length() ? new Object[]{true, null} : new Object[]{false, "违反保留数据长度策略"};
                    }
                    break;
                case COVER:
                    //1 遮蔽姓 2 遮蔽名 3 遮蔽中间字 4 保留中间字
                    switch (((Conf.ConfMaskChineseName) conf).coverType) {
                        case 1:
                            if (!in.substring(infirstName.length()).equals(out.substring(outfirstName.length()))) {
                                return new Object[]{false, "违反遮蔽姓策略"};
                            }
                            StringBuilder sb = new StringBuilder();
                            for (int j = 0; j < infirstName.length(); j++) {
                                sb.append(((Conf.ConfMaskChineseName) conf).symbol);
                            }
                            return outfirstName.substring(0, infirstName.length()).equals(sb.toString()) ? new Object[]{true, null} : new Object[]{false, "违反遮蔽姓策略"};
                        case 2:
                            if (!infirstName.equals(outfirstName)) {
                                return new Object[]{false, "违反遮蔽名策略"};
                            }
                            StringBuilder sb2 = new StringBuilder();
                            for (int k = 0; k < infirstName.substring(infirstName.length()).length(); k++) {
                                sb2.append(((Conf.ConfMaskChineseName) conf).symbol);
                            }
                            return outfirstName.substring(infirstName.length()).equals(sb2.toString()) ? new Object[]{true, null} : new Object[]{false, "违反遮蔽名策略"};
                        case 3:
                        case 4:
                            break;
                    }
                    break;
                default:
                    return new Object[]{true, null};
            }
        } catch (Exception e) {
            return new Object[]{false, "validateMaskData数据解析异常!"};
        }
        return new Object[]{true, null};
    }

    public static IndexMapList getIndexMapListSingle() {
        return indexMapListSingle;
    }

    public static void setIndexMapListSingle(IndexMapList indexMapListSingle) {
        AlgoChineseName.indexMapListSingle = indexMapListSingle;
    }

    public static IndexMapList getIndexMapListDouble() {
        return indexMapListDouble;
    }

    public static void setIndexMapListDouble(IndexMapList indexMapListDouble) {
        AlgoChineseName.indexMapListDouble = indexMapListDouble;
    }
}

