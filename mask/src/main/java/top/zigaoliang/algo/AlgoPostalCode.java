package top.zigaoliang.algo;

import com.google.common.base.Strings;
import lombok.Data;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.CommonUtil;
import top.zigaoliang.util.PostalCodeUtil;
import top.zigaoliang.util.Util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 邮政编码算法
 * Created by byc on 10/24/18.
 * Update zaj
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class AlgoPostalCode extends AlgoBase {
    public AlgoPostalCode() {
        super(AlgoId.POSTALCODE);
    }

    @Override
    public boolean find(String in) {
        if (in.length() != 6) {
            return false;
        }
        return PostalCodeUtil.postCodeList != null && PostalCodeUtil.postCodeList.contains(in);
    }

    @Override
    public int random(String in, StringBuilder out) {
        Conf.ConfMaskPostalCode postalCode = (Conf.ConfMaskPostalCode) confMask;
        //只要投递区保留，其他的都保留
        //if else的这个判断先后顺序不能变
        if (postalCode.region) {
            out.append(remainOne(in));
        } else if (postalCode.city) {
            //只要市保留，省，邮区都要保留
            out.append(remainTwo(in));
        } else if (postalCode.domain) {
            out.append(remainThree(in));
        } else if (postalCode.province) {
            out.append(remainThird(in));
        } else {
            out.append(remainFive(in));
        }
        return 0;
    }

    @Override
    public int mask(String in, StringBuilder out) {
        return maskCommon(in, out, true);
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        return maskCommon(in, out, false);
    }

    @Override
    public int cover(String in, StringBuilder out) {
        Conf.ConfMaskPostalCode postalCode = (Conf.ConfMaskPostalCode) confMask;
        String[] postalArr = getPostalCodeArr(in);
        switch (postalCode.coverType) {
            case 1:
                out.append(CommonUtil.coverBySymbol(postalCode.symbol, 2))
                        .append(postalArr[1]).append(postalArr[2]).append(postalArr[3]);
                break;
            case 2:
                out.append(postalArr[0])
                        .append(CommonUtil.coverBySymbol(postalCode.symbol, 1))
                        .append(postalArr[2]).append(postalArr[3]);
                break;
            case 3:
                out.append(postalArr[0]).append(postalArr[1])
                        .append(CommonUtil.coverBySymbol(postalCode.symbol, 1))
                        .append(postalArr[3]);
                break;
            case 4:
                out.append(postalArr[0]).append(postalArr[1])
                        .append(postalArr[2])
                        .append(CommonUtil.coverBySymbol(postalCode.symbol, 2));
                break;
            default:
                out.append(in);
        }
        return 0;
    }

    /**
     * 切分邮编，分别得到省，邮区，市，投递区
     *
     * @return
     */
    public String[] getPostalCodeArr(String in) {
        String[] arr = new String[4];
        arr[0] = in.substring(0, 2);
        arr[1] = in.substring(2, 3);
        arr[2] = in.substring(3, 4);
        arr[3] = in.substring(4);
        return arr;
    }


    public int maskCommon(String in, StringBuilder out, boolean flag) {
        Conf.ConfMaskPostalCode postalCode = (Conf.ConfMaskPostalCode) confMask;
        int cityNumber = Integer.parseInt(in.substring(3, 4));
        String result = "";
        switch (cityNumber) {
            case 0:
                result = getMaskData(PostalCodeUtil.postCodeList_0, PostalCodeUtil.postCodeMap_0, postalCode, in, flag);
                break;
            case 1:
                result = getMaskData(PostalCodeUtil.postCodeList_1, PostalCodeUtil.postCodeMap_1, postalCode, in, flag);
                break;
            case 2:
                result = getMaskData(PostalCodeUtil.postCodeList_2, PostalCodeUtil.postCodeMap_2, postalCode, in, flag);
                break;
            case 3:
                result = getMaskData(PostalCodeUtil.postCodeList_3, PostalCodeUtil.postCodeMap_3, postalCode, in, flag);
                break;
            case 4:
                result = getMaskData(PostalCodeUtil.postCodeList_4, PostalCodeUtil.postCodeMap_4, postalCode, in, flag);
                break;
            case 5:
                result = getMaskData(PostalCodeUtil.postCodeList_5, PostalCodeUtil.postCodeMap_5, postalCode, in, flag);
                break;
            case 6:
                result = getMaskData(PostalCodeUtil.postCodeList_6, PostalCodeUtil.postCodeMap_6, postalCode, in, flag);
                break;
            case 7:
                result = getMaskData(PostalCodeUtil.postCodeList_7, PostalCodeUtil.postCodeMap_7, postalCode, in, flag);
                break;
            case 8:
                result = getMaskData(PostalCodeUtil.postCodeList_8, PostalCodeUtil.postCodeMap_8, postalCode, in, flag);
                break;
            case 9:
                result = getMaskData(PostalCodeUtil.postCodeList_9, PostalCodeUtil.postCodeMap_9, postalCode, in, flag);
                break;
            default:
                result = in;
        }
        out.append(result);
        return 0;
    }

    /**
     * 只对保留市可以逆向脱敏
     *
     * @param dataList 数据字典
     * @param in       输入值
     * @param flag     正向 or 逆向
     * @return 脱敏后的值
     */
    public String getMaskData(List<String> dataList, Map<String, Integer> dataMap, Conf.ConfMaskPostalCode postalCode, String in, boolean flag) {
        int oldIndex = dataMap.get(in);
        int[] indexRange = {0, dataList.size() - 1};
        int newIndex = Util.maskBaseForInteger(indexRange, oldIndex, postalCode.seed, flag);
        return dataList.get(newIndex);
    }


    //仿真的四种方式(性能待优化)
    /**
     * 1.  省 邮区 市 投递区全部保留
     * 2.  省 邮区 市保留 投递区不保留
     * 3.  省 邮区保留 市 投递区不保留
     * 4.  省保留 邮区 市 投递区不保留
     * 5.  省 邮区 市 投递区 全部不保留
     */
    /**
     * 1.  省 邮区 市 投递区全部保留
     *
     * @return
     */
    public String remainOne(String in) {
        return in;
    }

    /**
     * 2.  省 邮区 市保留 投递区不保留
     *
     * @param in
     * @return
     */
    public String remainTwo(String in) {
        List<String> subCodeList = PostalCodeUtil.postCodeList.stream().filter(p -> p.startsWith(in.substring(0, 4))).collect(Collectors.toList());
        int randomIndex = Util.getNumByRange(0, subCodeList.size() - 1);
        return subCodeList.get(randomIndex);
    }

    /**
     * 3.  省 邮区保留 市 投递区不保留
     *
     * @param in
     * @return
     */
    public String remainThree(String in) {
        List<String> subCodeList = PostalCodeUtil.postCodeList.stream().filter(p -> p.startsWith(in.substring(0, 3))).collect(Collectors.toList());
        int randomIndex = Util.getNumByRange(0, subCodeList.size() - 1);
        return subCodeList.get(randomIndex);
    }

    /**
     * 5.  省 邮区 市 投递区 全部不保留
     *
     * @param in
     * @return
     */
    public String remainThird(String in) {
        List<String> subCodeList = PostalCodeUtil.postCodeList.stream().filter(p -> p.startsWith(in.substring(0, 2))).collect(Collectors.toList());
        int randomIndex = Util.getNumByRange(0, subCodeList.size() - 1);
        return subCodeList.get(randomIndex);
    }

    /**
     * 4.  省保留 邮区 市 投递区不保留
     *
     * @param in
     * @return
     */
    public String remainFive(String in) {
        int randomIndex = Util.getNumByRange(0, PostalCodeUtil.postCodeList.size() - 1);
        return PostalCodeUtil.postCodeList.get(randomIndex);
    }

    @Override
    public int random(StringBuilder out) {
        List<String> array = new ArrayList<>(2);
        array.add("430012");
        array.add("100011");
        Conf.ConfMaskPostalCode conf = new Conf.ConfMaskPostalCode();

        conf.seed = Util.getNumByRange(0, 65565);
        conf.province = Util.getNumByRange(0, 1) == 1;
        conf.domain = Util.getNumByRange(0, 1) == 1;
        conf.city = Util.getNumByRange(0, 1) == 1;
        conf.region = Util.getNumByRange(0, 1) == 1;
        this.confMask = conf;

        this.mask(array.get(Util.getNumByRange(0, 1)), out);
        return 0;
    }

    @Override
    public Object[] validateMaskData(Conf.ConfMask conf, String in, String out) {
        if (Strings.isNullOrEmpty(in) || Strings.isNullOrEmpty(out)) {
            return new Object[]{false, "in or out data is null"};
        }

        Conf.ConfMaskPostalCode cf = (Conf.ConfMaskPostalCode) conf;

        String inProvince = in.substring(0, 2);
        String inDomain = in.substring(2, 3);
        String inCity = in.substring(3, 4);
        String inRegion = in.substring(4);

        String outProvince = out.substring(0, 2);
        String outDomain = out.substring(2, 3);
        String outCity = out.substring(3, 4);
        String outRegion = out.substring(4);


        switch (conf.process) {
            case MASK:
            case UNMASK:
            case RANDOM:
                //根据当前的算法，在保留的情况下，脱敏前后一定相等，在不保留的情况向，前后可能相等也可能不等
                if (cf.province && !inProvince.equals(outProvince)) {
                    return new Object[]{false, "违反保留省策略"};
                }

                if (cf.domain && !inDomain.equals(outDomain)) {
                    return new Object[]{false, "违反保留邮区策略"};
                }

                if (cf.city && !inCity.equals(outCity)) {
                    return new Object[]{false, "违反保留市策略"};
                }

                if (cf.region && !inRegion.equals(outRegion)) {
                    return new Object[]{false, "违反保留投地区策略"};
                }

                break;
            case COVER:

                switch (cf.coverType) {
                    //遮蔽省
                    case 1: {
                        StringBuilder coverPart = new StringBuilder();
                        for (int i = 0; i < inProvince.length(); i++) {
                            coverPart.append(cf.symbol);
                        }
                        if (!(outProvince.equals(coverPart.toString())
                                && inCity.equals(outCity)
                                && inDomain.equals(outDomain)
                                && inRegion.equals(outRegion))) {
                            return new Object[]{false, "违反遮蔽省策略"};
                        }
                    }
                    break;
                    //遮蔽邮区
                    case 2: {
                        StringBuilder coverPart = new StringBuilder();
                        for (int i = 0; i < inDomain.length(); i++) {
                            coverPart.append(cf.symbol);
                        }
                        if (!(outDomain.equals(coverPart.toString())
                                && inCity.equals(outCity)
                                && inProvince.equals(outProvince)
                                && inRegion.equals(outRegion))) {
                            return new Object[]{false, "违反遮蔽邮区策略"};
                        }
                    }
                    break;
                    //遮蔽市
                    case 3: {
                        StringBuilder coverPart = new StringBuilder();
                        for (int i = 0; i < inCity.length(); i++) {
                            coverPart.append(cf.symbol);
                        }

                        if (!(outCity.equals(coverPart.toString())
                                && inDomain.equals(outDomain)
                                && inProvince.equals(outProvince)
                                && inRegion.equals(outRegion))) {
                            return new Object[]{false, "违反遮蔽市策略"};
                        }
                    }
                    break;
                    //遮蔽投递区
                    case 4: {
                        StringBuilder coverPart = new StringBuilder();
                        for (int i = 0; i < inRegion.length(); i++) {
                            coverPart.append(cf.symbol);
                        }

                        if (!(outRegion.equals(coverPart.toString())
                                && inDomain.equals(outDomain)
                                && inProvince.equals(outProvince)
                                && inCity.equals(outCity))) {
                            return new Object[]{false, "违反遮蔽市策略"};
                        }
                    }
                    break;
                }
                break;
        }
        return new Object[]{true, null};
    }
}
