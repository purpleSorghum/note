package top.zigaoliang.algo;

import com.google.common.base.Strings;
import org.apache.log4j.Logger;
import top.zigaoliang.common.JSONObject;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.core.AlgoId;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.FileReader;
import java.util.Map;

/**
 * 自定义算法
 * Created by byc on 10/24/18.
 */
public class AlgoCustom extends AlgoBase {
    Logger log = Logger.getLogger(AlgoCustom.class);

    private int customId;//自定义ID

    /**
     * 1.之所以初始化两个JS引擎，是考虑到一个线程可能需要加载发现脚本还需要加载脱敏脚本
     * 2.如果写一个，在初始化init时后者将前者脚本覆盖
     * 3.为提高自定义处理效率，可减少引擎初始化次数，一张表对应使用的字段初始化一次即可
     */
    private ScriptEngine findJSEngine;

    private ScriptEngine maskJSEngine;

    public AlgoCustom() {
        super(AlgoId.CUSTOM);
    }

    public int getCustomId() {
        return customId;
    }

    /**
     * 初始化算法
     * @param confFind 发现配置
     * @return 成功返回0；失败返回错误码
     */
    @Override
    public int init(Conf.ConfFind confFind) {
        if (confFind == null) {
            return ErrorCode.CONF_INIT_FIND.getCode();
        }
        //自定义发现配置，初始化javascript引擎编译（一张表初始化一次效率最好）
        findJSEngine = new ScriptEngineManager().getEngineByName("js");
        confFind.extend = initJSEngine(findJSEngine,confFind.extend);

        this.confFind = confFind;

        return 0;
    }

    /**
     * 初始化算法
     * @param confMask 脱敏配置
     * @return 成功返回0；失败返回错误码
     */
    @Override
    public int init(Conf.ConfMask confMask) {
        if (confMask == null) {
            return ErrorCode.CONF_INIT_MASK.getCode();
        }
        //自定义发现配置，初始化javascript引擎编译（一张表初始化一次效率最好）
        maskJSEngine = new ScriptEngineManager().getEngineByName("js");
        Conf.ConfMaskCustom _cus = (Conf.ConfMaskCustom)confMask;
        _cus.function = initJSEngine(maskJSEngine, _cus.function);

        this.confMask = confMask;

        return 0;
    }

    /**
     * 初始化时加载JS脚本
     */
    private String initJSEngine(ScriptEngine se,String par){
        if(!Strings.isNullOrEmpty(par)){
            try{
                //自定义发现算法中function使用json格式，需解析例如:{fileLoacation:/opt/csbit/?,mainMethod:findHandler}
                Map<String,Object> _confMap = JSONObject.fromObject(par).getMap();
                if(_confMap!=null&&_confMap.containsKey("fileLoacation")&&_confMap.containsKey("mainMethod")){
                    //执行在FileReader的参数为所要执行的js文件的路径
                    se.eval(new FileReader(_confMap.get("fileLoacation").toString()));
                    return  _confMap.get("mainMethod").toString();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return par;
    }

    @Override
    public boolean find(String in) {
        boolean result = false;
        try {
            synchronized(findJSEngine){
                //取得调用接口,执行函数
                Object value = ((Invocable) findJSEngine).invokeFunction(confFind.extend, in);
                result = value != null && ("true".equals(value) || value.equals(true)) ? true : false;
        }
        } catch (Exception e) {
            log.debug("maskcore->find()执行JavaScrip脚本出现异常:",e);
        }
        return result;
    }

    @Override

    public int random(String in, StringBuilder out) {
        this.mask(in, out);

        return 0;
    }

    @Override
    public int mask(String in, StringBuilder out) {
        try {
            synchronized(maskJSEngine) {
                //取得调用接口,执行函数
                Object value = ((Invocable) maskJSEngine).invokeFunction(((Conf.ConfMaskCustom) confMask).function, in);
                if (value == null) {
                    return ErrorCode.MASK_FUNC_ERROR.getCode();
                }
                if (out == null) {
                    out = new StringBuilder(value.toString());
                } else {
                    out.append(value.toString());
                }
            }
        } catch (Exception e) {
            log.debug("maskcore->mask()执行JavaScrip脚本出现异常:",e);
            out.append(e.getMessage());
            return ErrorCode.MASK_FUNC_ERROR.getCode();
        }
        return 0;
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        return 0;
    }


    @Override
    public int cover(String in, StringBuilder out) {
        return 0;
    }
}
