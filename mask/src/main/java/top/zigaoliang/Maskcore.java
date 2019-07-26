package top.zigaoliang;


import top.zigaoliang.algo.*;

import org.apache.log4j.Logger;

import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.CommonUtil;
import top.zigaoliang.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 算法处理类
 * 在外部调用时可以使用这个类
 * 使用步骤：
 * 1.new maskcore对象
 * 2.调用init()方法
 * 3.发现调用find()或singlefind()方法
 * 4.脱敏调用mask()或singlemask()方法
 * Created by byc on 10/20/18.
 */
public class Maskcore {
    private static Logger log = Logger.getLogger(Maskcore.class.getSimpleName());

    private class AlgoStat {
        int count = 0;//每个算法匹配行数统计
        List<String> samples = new ArrayList<>();//样例
    }

    private class ColumnFindStat {
        int scanRows;//扫描行数
        Map<Long, AlgoStat> algoStat = new HashMap<>();
    }

    private Conf.ConfTableFind confTableFind;//发现配置
    private Map<Long, AlgoBase> listAlgos = new HashMap<>();//发现使用，发现算法实例
    private Map<Integer, ColumnFindStat> mapAlgoStat = new HashMap<>();
    private int nTableRowCount;//数据行数

    private Conf.ConfTableMask confTableMask;//脱敏配置
    private boolean isRelate = false;//是否包含关联算法
    private boolean isVertical = false;//是否包含纵向列算法

    /**
     * 发现初始化配置
     *
     * @param table 表和具体列的配置信息
     * @return 成功返回0；失败返回错误码。可能有多个错误，所以用string类型返回。
     */
    public String initFind(Conf.ConfTableFind table) {
        confTableFind = table;
        listAlgos.clear();
        mapAlgoStat.clear();
        nTableRowCount = 0;

        String errorMsg = "0";
        if (confTableFind.confFinds.size() == 0) {
            //如果没有算法id及相关参数，直接返回
            errorMsg = ErrorCode.CONF_ALGO_SIZE.getMsg();
            log.info(errorMsg);
            return errorMsg;
        }

        for (Conf.ConfFind confFind : confTableFind.confFinds) {
            AlgoBase algoBase = AlgoFactory.getAlgo(confFind.id);
            //遍历算法配置列表，通过算法id获取具体的算法
            if (algoBase == null) {
                errorMsg = ErrorCode.NEW_ALGO_ERROR.getMsg() + ";AlgoID:" + confFind.id.getId();
                log.debug(errorMsg);
                return errorMsg;
            }
            //并使用配置信息初始化算法对象
            algoBase.init(confFind);

            listAlgos.put(confFind.ruleId, algoBase);//将算法添加到算法集合
        }


        for (int i = 0; i < confTableFind.columns.size(); i++) {
            Conf.ConfColumnFind colFind = confTableFind.columns.get(i);
            if (colFind.isFind) {//遍历所有列，如果该列需要进行发现
                ColumnFindStat stat = new ColumnFindStat();
                stat.scanRows = 0;
                for (Conf.ConfFind confFind : this.confTableFind.confFinds) {//
                    stat.algoStat.put(confFind.ruleId, new AlgoStat());
                }
                mapAlgoStat.put(i, stat);//为每个算法创建一个状态对象
            }
        }


        return errorMsg;
    }

    /**
     * 发现处理函数
     *
     * @param in  输入数据
     * @param out 发现的输出结果
     * @return 成功返回0；失败返回错误码。可能有多个错误，所以用string类型返回。
     */
    public String find(List<List<Object>> in, List<Conf.FindResult> out) {
        String errorMsg = "";
        out.clear();

        List<Conf.ConfColumnFind> columnFinds = confTableFind.columns;

        for (List<Object> row : in) { //总数据
            //配置检查
            if (row.size() != columnFinds.size()) {
                errorMsg += ErrorCode.CONF_COLUMN_SIZE.getMsg() + "; row size " + row.size()
                            + "; conf size " + columnFinds.size();
                log.debug(errorMsg);
                continue;
            }

            for (int index = 0; index < row.size(); index++) {//一行数据
                if (!columnFinds.get(index).isFind) {
                    continue;
                }

                //发现
                Object tmpData = row.get(index);
                if (tmpData == null) {
                    continue;
                }
                String strCol = tmpData.toString();
                if (strCol.isEmpty()) {
                    continue;
                }

                ColumnFindStat stat = mapAlgoStat.get(index);
                stat.scanRows++;

                Set<Long> keys = stat.algoStat.keySet();
                for (Long key : keys) {
                    AlgoStat algoStat = stat.algoStat.get(key);
                    AlgoBase algoBase = listAlgos.get(key);
                    try {
                        String[] specialSymbol = {CommonUtil.getPrefix(strCol), CommonUtil.getSuffix(strCol)};
                        strCol = CommonUtil.removeSpecialFromSrc(strCol, specialSymbol[0], specialSymbol[1]);
                        boolean b = algoBase.find(strCol);
                        if (b) {
                            algoStat.count++;
                        }
                    } catch (Exception e) {
                        errorMsg += ErrorCode.FIND_FUNC_ERROR.getMsg() + "; AlgoId:" + algoBase.getId().getId() +
                                    "; Data:" + strCol;
                        log.debug(errorMsg);
                    }
                }
            }
        }

        //整理发现结果
        List<Conf.ConfFind> confFinds = confTableFind.confFinds;//发现算法配置
        nTableRowCount = in.size();//表行数
        for (int index = 0; index < columnFinds.size(); index++) {
            if (!columnFinds.get(index).isFind) {
                continue;
            }
            if (!mapAlgoStat.containsKey(index)) {
                continue;
            }

            Conf.FindResult findResult = new Conf.FindResult();
            findResult.index = index;
            findResult.name = columnFinds.get(index).name;
            findResult.tableRows = nTableRowCount;
            findResult.scanRows = mapAlgoStat.get(index).scanRows;

            int nScanRows = mapAlgoStat.get(index).scanRows;
            Set<Long> keys = listAlgos.keySet();
            for (Long key : keys) {
                AlgoBase algoBase = listAlgos.get(key);
                //取得每个算法的统计值
                int id = algoBase.getId().getId();
                AlgoStat algoStat = mapAlgoStat.get(index).algoStat.get(key);
                if (algoStat.count == 0) {
                    continue;
                }

                //计算算法匹配率
                double rate = algoStat.count / (float) nScanRows;
                //循环配置，比较匹配率
                for (Conf.ConfFind cf : confFinds) {
                    if (cf.id == algoBase.getId()) {
                        if (rate >= cf.rate) {
                            Conf.FindResultItem item = new Conf.FindResultItem();
                            item.algoId = AlgoId.getAlgoId(id);
                            item.customId = cf.ruleId;
                            item.matchRows = algoStat.count;
                            findResult.algoItems.add(item);
                        }
                    }
                }
            }
            sortResultFindList(findResult.algoItems);
            out.add(findResult);
        }

        if (errorMsg.isEmpty()) {
            errorMsg = "0";
        }
        return errorMsg;
    }


    /**
     * 单值发现函数
     *
     * @param in  输入字符串
     * @param out 发现的输出结果
     * @return 成功返回0；失败返回错误码。可能有多个错误，所以用string类型返回。
     */
    public String singleFind(String in, Conf.FindResult out) {
        String errorMsg = "0";
        out.index = 0;
        out.name = "单列";
        out.tableRows = 1;
        out.scanRows = 1;

        if (in.isEmpty()) {
            errorMsg = ErrorCode.FIND_FUNC_EMPTY.getMsg();
            return errorMsg;
        }

        if (!mapAlgoStat.containsKey(0)) {
            errorMsg = ErrorCode.CONF_INIT_FIND.getMsg() + "; 配置为空";
            return errorMsg;
        }
        ColumnFindStat stat = mapAlgoStat.get(0);
        stat.scanRows++;

        Map<Long, AlgoStat> algoStat = stat.algoStat;
        Set<Long> keys = listAlgos.keySet();
        for (Long key : keys) {
            AlgoBase algoBase = listAlgos.get(key);
            try {
                String[] specialSymbol = {CommonUtil.getPrefix(in), CommonUtil.getSuffix(in)};
                in = CommonUtil.removeSpecialFromSrc(in, specialSymbol[0], specialSymbol[1]);
                boolean b = algoBase.find(in);
                if (b) {
                    algoStat.get(key).count++;
                }
            } catch (Exception e) {
                errorMsg += ErrorCode.FIND_FUNC_ERROR.getMsg() + "; AlgoId:" + algoBase.getId().getId() +
                            "; Data:" + in;
                log.debug(errorMsg);
            }
        }

        //整理发现结果
        for (Long key : keys) {
            //取得每个算法的统计值
            AlgoBase algoBase = listAlgos.get(key);
            int id = algoBase.getId().getId();
            AlgoStat as = algoStat.get(key);
            if (as.count > 0) {
                Conf.FindResultItem item = new Conf.FindResultItem();
                item.algoId = AlgoId.getAlgoId(id);
                getCustomId(item, algoBase);
                item.matchRows = 1;
//                item.samples.add(in);
                out.algoItems.add(item);
            }
        }

        return errorMsg;
    }


    /**
     * 脱敏初始化配置
     *
     * @param table 表和具体列的配置信息
     * @return 成功返回0；失败返回错误码。可能有多个错误，所以用string类型返回。
     */
    public String initMask(Conf.ConfTableMask table) {
        confTableMask = table;
        String errorMsg = "0";
        for (Conf.ConfColumnMask column : confTableMask.columns) {
            if (column != null && column.isMask) {
                AlgoId algoId = column.confMask.id;
                column.algoBase = AlgoFactory.getAlgo(algoId);
                if (column.algoBase == null) {
                    errorMsg = ErrorCode.NEW_ALGO_ERROR.getMsg() + "; AlgoID:" + algoId.getId();
                    log.debug(errorMsg);
                    return errorMsg;
                }

                int res = column.algoBase.init(column.confMask);
                if (res != 0) {
                    errorMsg = ErrorCode.getErrorCode(res).getMsg();
                    log.debug(errorMsg);
                    return errorMsg;
                }

                //判断是否存在关联算法和混合列算法 整数区间算法
                switch (algoId) {
                    case RELATEIDTOAGE:
                    case RELATEIDTOBIRTHDAY:
                    case RELATECOMPUTE:
                    case INTEGERRANGE:
                        isRelate = true;
                        break;
                    case RELATEVERTICALMAIN:
                        isVertical = true;
                        break;
                    default:
                }
            }
        }

        /* *
         * 序列化脱敏配置,
         * 例获取配置信息时打开
         */
//        try {
//            XMLUtil.convertToXml(table, cn.csbit.core.common.Globals.CLASSPATH + "confRelateIdMask.xml");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return errorMsg;
    }

    /**
     * 脱敏处理函数
     *
     * @param in  输入数据
     * @param out 脱敏的输出结果
     * @return 成功返回0；失败返回错误码。可能有多个错误，所以用string类型返回。
     */
    public String mask(List<List<Object>> in, List<List<Object>> out) {
        String errorMsg = "0";

        List<Conf.ConfColumnMask> columnMasks = confTableMask.columns;
        //关联算法--纵向列关联算法
        if (isVertical) {
            VerticalShuffle(in);
        }
        //非关联算法
        for (List<Object> row : in) {//总数据
            //配置检查
            if (row.size() != columnMasks.size()) {
                out.add(row);

                errorMsg += ErrorCode.CONF_COLUMN_SIZE.getMsg() + "; row size " + row.size()
                            + "; conf size " + columnMasks.size() + "\n";
                log.debug(errorMsg);
                StringBuilder str = new StringBuilder("输出数据：");
                for (Object col : row) {
                    str.append(col == null ? "" : col.toString()).append("\n");
                }
                log.debug(str.toString());
                continue;
            }

            List<Object> rowOut = new ArrayList<>();
            for (int index = 0; index < row.size(); index++) { //一行数据
                Object data = row.get(index);
                if (data == null) {
                    rowOut.add(null);
                    continue;
                }
                String strCol = data.toString();
                Conf.ConfColumnMask confColumnMask = columnMasks.get(index);
                if (!confColumnMask.isMask) {
                    //针对lob类型字段，必须保证其原始类型，需要将原data装回去，而不是String类型的数据
                    rowOut.add(data);
                    continue;
                }

                if (isRelate) {
                    if (confColumnMask.confMask.id == AlgoId.RELATEIDTOAGE ||
                                confColumnMask.confMask.id == AlgoId.RELATEIDTOBIRTHDAY ||
                                confColumnMask.confMask.id == AlgoId.RELATECOMPUTE) {
                        rowOut.add(strCol);
                        continue;//跳过关联算法
                    }
                }

                try {
                    StringBuilder strOut = new StringBuilder();
                    StringBuilder tempOut = new StringBuilder();
                    int res = 0;
                    String[] specialSymbol = {CommonUtil.getPrefix(strCol), CommonUtil.getSuffix(strCol)};
                    strCol = CommonUtil.removeSpecialFromSrc(strCol, specialSymbol[0], specialSymbol[1]);
//                    if ((confColumnMask.algoBase.getId().getId() > 31 || confColumnMask.algoBase.find(strCol))) {
//                    if ((confColumnMask.algoBase.getId().getId() > 31 && confColumnMask.algoBase.getId().getId()!= AlgoId.WATERMARK.getId())|| confColumnMask.algoBase.find(strCol)) {
                    strOut.append(specialSymbol[0]);
                    if (confColumnMask.confMask.process == Conf.MaskType.MASK) {
                        res = confColumnMask.algoBase.mask(strCol, tempOut);
                    } else if (confColumnMask.confMask.process == Conf.MaskType.UNMASK) {
                        res = confColumnMask.algoBase.unmask(strCol, tempOut);
                    } else if (confColumnMask.confMask.process == Conf.MaskType.RANDOM) {
                        res = confColumnMask.algoBase.random(strCol, tempOut);
                    } else {
                        res = confColumnMask.algoBase.cover(strCol, tempOut);
                    }
                    strOut.append(tempOut).append(specialSymbol[1]);
//                    } else {
//                        strOut.append(strCol);
//                    }

                    if (res != 0) {
                        errorMsg += ErrorCode.getErrorCode(res).getMsg() + "\n";
                        log.debug(errorMsg);
                        rowOut.add(strCol);
                        continue;
                    }
                    rowOut.add(strOut.toString());
                } catch (Exception e) {
//                    errorMsg += ErrorCode.MASK_FUNC_ERROR.getMsg() + "\n脱敏方式：" + confColumnMask.confMask.process +
//                            "脱敏算法：" + confColumnMask.confMask + "脱敏数据：" + strCol;
//                    log.debug(errorMsg);
                    rowOut.add(strCol);
                    continue;
                }
            }//for row

            Integer integerRangeIndex = null;
            Conf.ConfMaskIntegerRange confMaskIntegerRange = null;
            //对关联算法特殊处理
            if (isRelate) {
                for (int index = 0; index < rowOut.size(); index++) {  //一行数据
                    Conf.ConfColumnMask confColumnMask = columnMasks.get(index);
                    if (confColumnMask.confMask == null) {
                        continue;
                    }
                    String strCol;
                    if (confColumnMask.confMask.id == AlgoId.RELATEIDTOAGE) {
                        int colIndex = ((Conf.ConfMaskRelateIdToAge) confColumnMask.confMask).index;
                        strCol = rowOut.get(colIndex).toString();
                    } else if (confColumnMask.confMask.id == AlgoId.RELATEIDTOBIRTHDAY) {
                        int colIndex = ((Conf.ConfMaskRelateIdToBirthday) confColumnMask.confMask).index;
                        strCol = rowOut.get(colIndex).toString();
                    } else if (confColumnMask.confMask.id == AlgoId.RELATECOMPUTE) {
                        Conf.ConfMaskRelateCompute confMaskRelateCompute = (Conf.ConfMaskRelateCompute) confColumnMask.confMask;
                        List<Integer> colIndexs = confMaskRelateCompute.columns;
                        StringBuilder sb = new StringBuilder();
                        colIndexs.forEach(colIndex -> sb.append(rowOut.get(colIndex)).append(";"));
//                        sb.append(index);
                        strCol = sb.toString();
                    } else if (confColumnMask.confMask.id == AlgoId.INTEGERRANGE) {
                        //找到整数区间列索引
                        confMaskIntegerRange = (Conf.ConfMaskIntegerRange) confColumnMask.confMask;
                        integerRangeIndex = ((Conf.ConfMaskIntegerRange) confColumnMask.confMask).index;
                        strCol = rowOut.get(integerRangeIndex).toString();
                    } else {
                        continue;//非关联列不处理
                    }

                    try {
                        StringBuilder strOut = new StringBuilder();
                        int res = confColumnMask.algoBase.random(strCol, strOut);
                        if (res != 0) {
                            errorMsg += ErrorCode.getErrorCode(res).getMsg() + "\n";
                            log.debug(errorMsg);
                            continue;
                        }
                        rowOut.set(index, strOut.toString());
                    } catch (Exception e) {
                        errorMsg += ErrorCode.MASK_FUNC_ERROR.getMsg() + "\n脱敏方式：" + confColumnMask.confMask.process +
                                    "脱敏算法：" + confColumnMask.algoBase.getId() + "脱敏数据：" + strCol;
                        log.debug(errorMsg);
                        continue;
                    }
                }
            }
            out.add(rowOut);
            //找到整数区间那列的数据替换
            integerRangeReplace(out, integerRangeIndex, confMaskIntegerRange);
        }//for data
        return "0";
    }

    public void integerRangeReplace(List<List<Object>> out, Integer integerRangeIndex, Conf.ConfMaskIntegerRange confMaskIntegerRange) {
        if (integerRangeIndex != null && confMaskIntegerRange != null) {
            List<Integer> colDate = getCloData(out.size(), confMaskIntegerRange);
            for (int i = 0; i < out.size(); i++) {
                for (int j = 0; j < out.get(i).size(); j++)
                    if (j == integerRangeIndex) {
                        out.get(i).set(integerRangeIndex, colDate.get(i));
                    }
            }
        }
    }

    //生成一列数据
    public List<Integer> getCloData(int length, Conf.ConfMaskIntegerRange confMaskIntegerRange) {
        //脱敏单独测试的时候调用
        List<Integer> resultNumber = new ArrayList<>();
        resultNumber.add(confMaskIntegerRange.begin);
        int sum = confMaskIntegerRange.begin;
        for (int i = 0; i < length; i++) {
            if ((sum + confMaskIntegerRange.step) < confMaskIntegerRange.max) {
                resultNumber.add(sum + confMaskIntegerRange.step);
                sum = sum + confMaskIntegerRange.step;
            } else {
                sum = confMaskIntegerRange.begin;
                resultNumber.add(sum);
            }
        }
        return resultNumber;
    }


    /**
     * 单值脱敏函数
     *
     * @param in  输入数据
     * @param out 脱敏的输出结果
     * @return 成功返回0；失败返回错误码。可能有多个错误，所以用string类型返回。
     */
    public String singleMask(String in, StringBuilder out) {
        String errorMsg = "0";

        if (in.isEmpty()) {
            out.append(in);
            return errorMsg;
        }

        if (confTableMask.columns.size() == 0 || confTableMask.columns.get(0) == null) {
            errorMsg = ErrorCode.CONF_INIT_MASK.getMsg();
            errorMsg += "单值脱敏配置为空";
            return errorMsg;
        }

        Conf.ConfColumnMask confColumnMask = confTableMask.columns.get(0);
        try {
            int res = 0;
            String[] specialSymbol = {CommonUtil.getPrefix(in), CommonUtil.getSuffix(in)};
            in = CommonUtil.removeSpecialFromSrc(in, specialSymbol[0], specialSymbol[1]);
            StringBuilder tempOut = new StringBuilder();
            if (confColumnMask.algoBase.getId().getId() > AlgoId.INTEGER.getId() || confColumnMask.algoBase.find(in)) {
                out.append(specialSymbol[0]);
                if (confColumnMask.algoBase.getId().getId() == AlgoId.INTEGERRANGE.getId()) {
                    //整数区间算法的脱敏要单独处理，因为整数区间返回的是多个值
                    AlgoIntegerRange integerRange = (AlgoIntegerRange) confColumnMask.algoBase;
                    res = integerRange.randomSingle(in, tempOut);
                } else {
                    if (confColumnMask.confMask.process == Conf.MaskType.MASK) {
                        res = confColumnMask.algoBase.mask(in, tempOut);
                    } else if (confColumnMask.confMask.process == Conf.MaskType.UNMASK) {
                        res = confColumnMask.algoBase.unmask(in, tempOut);
                    } else if (confColumnMask.confMask.process == Conf.MaskType.RANDOM) {
                        res = confColumnMask.algoBase.random(in, tempOut);
                    } else {
                        res = confColumnMask.algoBase.cover(in, tempOut);
                    }
                }
                out.append(tempOut).append(specialSymbol[1]);
            } else {
                out.append("不符合");
            }
            if (res != 0) {
                errorMsg += ErrorCode.getErrorCode(res).getMsg() + "\n";
                log.debug(errorMsg);
                out.append("不符合");
            }
        } catch (Exception e) {
            out.append("不符合");
        }
        return errorMsg;
    }

    /**
     * in
     * 纵向列打乱处理，将打乱的数据写入该列的算法中
     *
     * @param in 输入数据
     */
    private void VerticalShuffle(List<List<Object>> in) {
        int nSize = in.size();

        //获取每一列脱敏配置
        List<Conf.ConfColumnMask> columnMasks = confTableMask.columns;

        // 统计分组关联列的列序号
        List<Integer> groupIndexes = new ArrayList<>();
        int idx = 0;
        for (Conf.ConfColumnMask colConf : columnMasks) {
            if (colConf.confMask != null && colConf.confMask.id == AlgoId.RELATEVERTICALGROUP) {
                groupIndexes.add(idx);
            }
            idx++;
        }

        // 遍历每一列的脱敏配置，找到主关联列
        for (Conf.ConfColumnMask colConf : columnMasks) {
            if (colConf.confMask == null || colConf.confMask.id != AlgoId.RELATEVERTICALMAIN) {
                continue;
            }

            // 获取主关联列脱敏配置
            Conf.ConfMaskRelateVertical relateVertical = (Conf.ConfMaskRelateVertical) colConf.confMask;

            // 获取乱序后的序号
            int[] shuffle;
            if (groupIndexes.size() > 0) {
                // 针对需要分组关联的，按照分组进行乱序
                shuffle = Util.shuffle(in, groupIndexes);
            } else {
                // 没有分组分组关联的，直接进行乱序
                shuffle = Util.shuffle(nSize);
            }

            // 主关联列脱敏配置包含其所有的副关联列的索引信息
            // 遍历所有的副关联列
            for (int colIndex : relateVertical.columns) {//列index
                // 存储当前副关联列乱序后的列数据
                List<Object> listColData = new ArrayList<>();
                // 遍历每一行数据
                for (int i = 0; i < nSize; i++) {//行index
                    // 找到当前行乱序后的行号，i为乱序前的行号，rowIndex为乱序后的行号
                    int rowIndex = shuffle[i];
                    // 将乱序后的行数据放到乱序前的位置，达到乱序效果
                    // 例如：i = 0 , rowIndex = 5 ,表示原来在第 5 行的数据，乱序后放在了第 0 行的位置
                    listColData.add(in.get(rowIndex).get(colIndex));
                }

                // 防止越界
                if (columnMasks.size() <= colIndex) {
                    continue;
                }
                // 找到当前副关联列的脱敏算法
                AlgoRelateVertical algoRelateVertical = (AlgoRelateVertical) (columnMasks.get(colIndex).algoBase);
                // 将乱序后的列数据写入到此列的脱敏算法中
                algoRelateVertical.setListColData(listColData);
            }
        }
    }


    /**
     * 对于自定义类型的算法，如自定义算法、正则算法、混合列算法，获取自定义ID
     *
     * @param item     发现结果item
     * @param algoBase 算法
     */
    private void getCustomId(Conf.FindResultItem item, AlgoBase algoBase) {
        switch (item.algoId) {
            case REGEX:
                AlgoRegex algoRegex = (AlgoRegex) algoBase;
                item.customId = algoRegex.getCustomId();
                break;
            case MIXEDCOLUMN:
                AlgoMixedColumn algoMixedColumn = (AlgoMixedColumn) algoBase;
                item.customId = algoMixedColumn.getCustomId();
                break;
            case CUSTOM:
                AlgoCustom algoCustom = (AlgoCustom) algoBase;
                item.customId = algoCustom.getCustomId();
                break;
            default:
                item.customId = -1;
                break;
        }
    }


    /**
     * 对要扫描的算法进行排序
     * 算法优先级： 邮编，基金  -> 整数 -> 金额
     * 身份证 -> 税号
     */
    public static void sortResultFindList(List<Conf.FindResultItem> findResultItems) {
        try {
            //第一条线 基金代码 -> 邮编，股票代码,日期 银行卡号 (手机号，座机号 -> 电话号码) ->(身份证号 -> 税号) ->  社会统一信用代码  -> 整数
            /**
             * 1.如果日期的格式:2180613  就有可能扫描成整数
             * 2.日期现在也适配MySQL的year类型比如：2019 可能会被扫描成整数
             * 3.手机号，银行卡号都有可能扫描成整数
             */
            Conf.FindResultItem[] scanResultFitst = new Conf.FindResultItem[12];
            //第二条线 身份证 -> 税号
//            ScanResult[] scanResultTwo = new ScanResult[2];
            //第三条线 公司名称，单位名称，中文姓名 -> 客户名称
            Conf.FindResultItem[] scanResultThree = new Conf.FindResultItem[4];
            //第5条线:  军官号 -> 中文地址   空第0000001号有可能扫描成中文地址
            Conf.FindResultItem[] scanResultFive = new Conf.FindResultItem[2];
            List<Conf.FindResultItem> otherScanResult = new ArrayList<>();
            for (Conf.FindResultItem scanResultTemp : findResultItems) {
                if (scanResultTemp.algoId.getId() == AlgoId.FUNDCODE.getId()) {
                    scanResultFitst[0] = scanResultTemp;
                } else if (scanResultTemp.algoId.getId() == AlgoId.POSTALCODE.getId()) {
                    scanResultFitst[1] = scanResultTemp;
                } else if (scanResultTemp.algoId.getId() == AlgoId.STOCKCODE.getId()) {
                    scanResultFitst[2] = scanResultTemp;
                } else if (scanResultTemp.algoId.getId() == AlgoId.DATE.getId()) {
                    scanResultFitst[3] = scanResultTemp;
                } else if (scanResultTemp.algoId.getId() == AlgoId.BANKCARD.getId()) {
                    scanResultFitst[4] = scanResultTemp;
                } else if (scanResultTemp.algoId.getId() == AlgoId.CELLPHONE.getId()) {
                    scanResultFitst[5] = scanResultTemp;
                } else if (scanResultTemp.algoId.getId() == AlgoId.TELEPHONE.getId()) {
                    scanResultFitst[6] = scanResultTemp;
                } else if (scanResultTemp.algoId.getId() == AlgoId.PHONE.getId()) {
                    scanResultFitst[7] = scanResultTemp;
                } else if (scanResultTemp.algoId.getId() == AlgoId.IDCARD.getId()) {
                    scanResultFitst[8] = scanResultTemp;
                } else if (scanResultTemp.algoId.getId() == AlgoId.TAXNUMBER.getId()) {
                    scanResultFitst[9] = scanResultTemp;
                } else if (scanResultTemp.algoId.getId() == AlgoId.CREDITCODE.getId()) {
                    scanResultFitst[10] = scanResultTemp;
                }  else if (scanResultTemp.algoId.getId() == AlgoId.INTEGER.getId()) {
                    scanResultFitst[11] = scanResultTemp;
                }else if (scanResultTemp.algoId.getId() == AlgoId.COMPANY.getId()) {
                    scanResultThree[0] = scanResultTemp;
                } else if (scanResultTemp.algoId.getId() == AlgoId.UNITNAME.getId()) {
                    scanResultThree[1] = scanResultTemp;
                } else if (scanResultTemp.algoId.getId() == AlgoId.CHINESENAME.getId()) {
                    scanResultThree[2] = scanResultTemp;
                } else if (scanResultTemp.algoId.getId() == AlgoId.CUSTOMERNAME.getId()) {
                    scanResultThree[3] = scanResultTemp;
                } else if (scanResultTemp.algoId.getId() == AlgoId.MILITARYCARD.getId()) {
                    scanResultFive[0] = scanResultTemp;
                } else if (scanResultTemp.algoId.getId() == AlgoId.ADDRESS.getId()) {
                    scanResultFive[1] = scanResultTemp;
                } else {
                    otherScanResult.add(scanResultTemp);
                }
            }
            findResultItems.clear();
            for (int i = 0; i < scanResultFitst.length; i++) {
                if (scanResultFitst[i] != null) {
                    findResultItems.add(scanResultFitst[i]);
                }
            }
            for (int i = 0; i < scanResultThree.length; i++) {
                if (scanResultThree[i] != null) {
                    findResultItems.add(scanResultThree[i]);
                }
            }
            for (int i = 0; i < scanResultFive.length; i++) {
                if (scanResultFive[i] != null) {
                    findResultItems.add(scanResultFive[i]);
                }
            }
            findResultItems.addAll(otherScanResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
