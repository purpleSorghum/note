package top.zigaoliang.algo;

import org.apache.log4j.Logger;
import top.zigaoliang.AlgoFactory;
import top.zigaoliang.common.JSONSerializer;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.conf.MixedColumn;
import top.zigaoliang.conf.MixedColumnItem;
import top.zigaoliang.core.AlgoId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 混合列算法
 * 通过分隔符把一列切分成多个列
 * Created by byc on 10/24/18.
 */
public class AlgoMixedColumn extends AlgoBase {
    public AlgoMixedColumn() {
        super(AlgoId.MIXEDCOLUMN);
    }
    private static Logger log = Logger.getLogger(AlgoMixedColumn.class.getSimpleName());
    private long customId = -1L;//自定义ID
    private MixedColumn cfg = null;
    public long getCustomId() {
        return customId;
    }

    @Override
    public int init(Conf.ConfFind confFind) {
        cfg = JSONSerializer.deserialize(MixedColumn.class, confFind.extend);
        if (cfg.getCustomeId() != null) {
            this.customId = cfg.getCustomeId();
        }
        return super.init(confFind);
    }

    @Override
    public int init(Conf.ConfMask confMask) {
        Conf.ConfMaskMixedColumn conf = (Conf.ConfMaskMixedColumn)confMask;
        cfg = conf.cfg;
        if (cfg.getCustomeId() != null) {
            this.customId = cfg.getCustomeId();
        }
        return super.init(confFind);
    }
    @Override
    public boolean find(String in) {
        List<String> ins = new ArrayList<>();
        ins.addAll(Arrays.asList(splitStr(in)));
        if (ins.size() <= 0) {
            return false;
        }
        //算法集合
        AlgoBase[] algoList = new AlgoBase[cfg.getSegment().size()];
        for (int i = 0; i < algoList.length; i++) {
            MixedColumnItem mixedColumnItem = cfg.getSegment().get(i);
            algoList[i] = AlgoFactory.getAlgo(AlgoId.getAlgoId(mixedColumnItem.getRule()));
        }
        //混合列满足两个及两个以上就算
        int sum = 0;
        for (int i = 0; i < ins.size(); i++) {
            for (int j = 0; j < algoList.length; j++) {
                if(algoList[j].find(ins.get(i))){
                    sum ++;
                    break;
                }
            }
        }
        if(sum < 2){
            return false;
        }
        return true;
    }

    @Override
    public int random(String in, StringBuilder out) {
        return common(in,out,"random");
    }

    @Override
    public int mask(String in, StringBuilder out) {
        return common(in,out,"mask");
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        return common(in,out,"unmask");
    }

    @Override
    public int cover(String in, StringBuilder out) {
        return 0;
    }

    public int common(String in, StringBuilder out, String state) {
        ErrorCode errorCode = null;
        if(!find(in)){
            errorCode = ErrorCode.MIXEDCOLUMN_ERROR;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        // 1. 切分字符串
        try {
            List<String> ins = new ArrayList<>();
            ins.addAll(Arrays.asList(splitStr(in)));

            if (ins.size() <= 0) {
                out.append(in);
                return 0;
            }
            //算法集合
            AlgoBase[] algoList = new AlgoBase[cfg.getSegment().size()];
            for (int i = 0; i < algoList.length; i++) {
                MixedColumnItem mixedColumnItem = cfg.getSegment().get(i);
                algoList[i] = AlgoFactory.getAlgo(AlgoId.getAlgoId(mixedColumnItem.getRule()));
            }
            // 字符串脱敏
            boolean flag = false;
            List<String> outs = new ArrayList<>();
            for (int i = 0; i < ins.size(); i++) {
                StringBuilder result = new StringBuilder();
                for (int j = 0; j < algoList.length; j++) {
                    if (algoList[j].find(ins.get(i))) {
                        flag = true;
                        Conf.ConfMask conf = AlgoFactory.getDefaultConfMask(algoList[j].id);
                        algoList[j].init(conf);
                        if (state.equals("mask")) {
                            algoList[j].mask(ins.get(i), result);
                        }
                        if (state.equals("random")) {
                            algoList[j].random(ins.get(i), result);
                        }
                        if (state.equals("unmask")) {
                            algoList[j].unmask(ins.get(i), result);
                        }
                        break;
                    }
                }
                if(flag){
                    outs.add(result.toString());
                }else{
                    outs.add(ins.get(i));
                }
                flag = false;
            }
            // 还原字符串
            out.append(rebackStr(outs));
        } catch (Exception e) {
            errorCode = ErrorCode.MIXEDCOLUMN_ERROR;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    //将字符串按照多种字符进行分割
    public String[] splitStr(String in){
        Set<String> setChar = new HashSet<>();
        for (int i = 0; i < cfg.getSegment().size(); i++) {
            setChar.add(cfg.getSegment().get(i).getCh());
        }
        StringBuilder result = new StringBuilder();
        for(String str : setChar){
            result.append(str);
        }
        return in.split(result.toString());
    }

    //还原字符串
    public String rebackStr(List<String> outs){
        List<String> splitChar = new ArrayList<>();
        for (int i = 0; i < cfg.getSegment().size(); i++) {
            splitChar.add(cfg.getSegment().get(i).getCh());
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < outs.size(); i++) {
            result.append(outs.get(i));
            if(i < cfg.getSegment().size()){
                result.append(cfg.getSegment().get(i).getCh());
            }
        }
        return result.substring(0,result.toString().length()-1);
    }
}
