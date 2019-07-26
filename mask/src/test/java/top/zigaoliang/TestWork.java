package top.zigaoliang;

import org.junit.Test;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.core.AlgoId;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName TestWork
 * @Author hanlin
 * @Date 2019/7/23 14:32
 **/
public class TestWork {
    @Test
    public void singleFindTest() {
        String strIn = "李白";

        //数据库架构配置
        Conf.ConfTableFind confTableFind = new Conf.ConfTableFind();
        confTableFind.name = "测试表";

        Conf.ConfColumnFind confColumnFind = new Conf.ConfColumnFind();
        confColumnFind.name = "name";
        confColumnFind.isFind = true;
        confTableFind.columns.add(confColumnFind);

        //算法配置
        Conf.ConfFind confFind1 = new Conf.ConfFind();
        confFind1.id = AlgoId.EMAIL;
        confFind1.rate = 0.8;
        confFind1.count = 3000;
        confFind1.extend = "";
        confTableFind.confFinds.add(confFind1);

        Conf.ConfFind confFind2 = new Conf.ConfFind();
        confFind2.id = AlgoId.CHINESENAME;
        confFind2.rate = 0.8;
        confFind2.count = 3000;
        confFind2.extend = "";
        confTableFind.confFinds.add(confFind2);


        Maskcore maskcore = new Maskcore();
        String errorMsg = maskcore.initFind(confTableFind);
        if (!errorMsg.equals("0")) {
            System.out.println("初始化错误：" + errorMsg);
            return;
        }

        Conf.FindResult out = new Conf.FindResult();
        errorMsg = maskcore.singleFind(strIn, out);
        if (!errorMsg.equals("0")) {
            System.out.println("执行发现错误：" + errorMsg);
            return;
        }
//        Maskcore maskcore = new Maskcore();
//        String l = maskcore.initFind(confTableFind);
//        if (!errorMsg.equals("0")) {
//            System.out.println("初始化错误：" + errorMsg);
//            return;
//        }
//
//        errorMsg = maskcore.singleFind(strIn, strOut);
//        if (!errorMsg.equals("0")) {
//            System.out.println("执行发现错误：" + errorMsg);
//            return;
//        }
        System.out.println("发现输出结果=============================");
        System.out.println("列索引：" + out.index);
        System.out.println("列名：" + out.name);
        System.out.println("表格行数：" + out.tableRows);
        System.out.println("扫描行数：" + out.scanRows);
        for (Conf.FindResultItem item : out.algoItems) {
            System.out.println("列算法：" + item.algoId);
            System.out.println("匹配行数：" + item.matchRows);
            System.out.println("样例:" + item.samples);
        }
    }

    @Test
    public void findTest() {
        //初始化数据
        List<List<Object>> listDatas = new ArrayList<>();
        List<Object> row = new ArrayList<>();
        row.add("1");
        row.add("白永超");
        row.add("河北省唐山市");
        listDatas.add(row);

        row = new ArrayList<>();
        row.add("2");
        row.add("赵伟全");
        row.add("河北省石家庄市");
        listDatas.add(row);

        List<Conf.FindResult> listOut = new ArrayList<>();

        //数据库架构配置
        Conf.ConfTableFind confTableFind = new Conf.ConfTableFind();
        confTableFind.name = "测试表";

        Conf.ConfColumnFind confColumnFind = new Conf.ConfColumnFind();
        confColumnFind.name = "id";
        confColumnFind.isFind = false;
        confTableFind.columns.add(confColumnFind);

        confColumnFind = new Conf.ConfColumnFind();
        confColumnFind.name = "name";
        confColumnFind.isFind = true;
        confTableFind.columns.add(confColumnFind);

        confColumnFind = new Conf.ConfColumnFind();
        confColumnFind.name = "address";
        confColumnFind.isFind = true;
        confTableFind.columns.add(confColumnFind);

        //算法配置
        Conf.ConfFind confFind = new Conf.ConfFind();
        confFind.id = AlgoId.EMAIL;
        confFind.ruleId=new Integer(AlgoId.EMAIL.getId()).longValue();
        confFind.rate = 0.8;
        confFind.count = 3000;
        confFind.extend = "";
        confTableFind.confFinds.add(confFind);

        confFind = new Conf.ConfFind();
        confFind.id = AlgoId.CHINESENAME;
        confFind.ruleId=new Integer(AlgoId.CHINESENAME.getId()).longValue();
        confFind.rate = 0.8;
        confFind.count = 3000;
        confFind.extend = "";
        confTableFind.confFinds.add(confFind);

        confFind = new Conf.ConfFind();
        confFind.id = AlgoId.ADDRESS;
        confFind.ruleId=new Integer(AlgoId.ADDRESS.getId()).longValue();
        confFind.rate = 0.8;
        confFind.count = 3000;
        confFind.extend = "";
        confTableFind.confFinds.add(confFind);

        confFind = new Conf.ConfFind();
        confFind.id = AlgoId.BANKCARD;
        confFind.ruleId=new Integer(AlgoId.BANKCARD.getId()).longValue();
        confFind.rate = 0.8;
        confFind.count = 3000;
        confFind.extend = "";
        confTableFind.confFinds.add(confFind);

        confFind = new Conf.ConfFind();
        confFind.id = AlgoId.CELLPHONE;
        confFind.ruleId=new Integer(AlgoId.CELLPHONE.getId()).longValue();
        confFind.rate = 0.8;
        confFind.count = 3000;
        confFind.extend = "";
        confTableFind.confFinds.add(confFind);

        //发现
        Maskcore maskcore = new Maskcore();
        String errorMsg = maskcore.initFind(confTableFind);
        if (!errorMsg.equals("0")) {
            System.out.println("初始化错误：" + errorMsg);
            return;
        }

        errorMsg = maskcore.find(listDatas, listOut);
        if (!errorMsg.equals("0")) {
            System.out.println("执行发现错误：" + errorMsg);
            return;
        }

        //输出发现结果
        for (Conf.FindResult fr : listOut) {
            System.out.println("==================================");
            System.out.println("列索引：" + fr.index);
            System.out.println("列名：" + fr.name);
            System.out.println("表格行数：" + fr.tableRows);
            System.out.println("扫描行数：" + fr.scanRows);
            for (Conf.FindResultItem item : fr.algoItems) {
                System.out.println("列算法：" + item.algoId);
                System.out.println("匹配行数：" + item.matchRows);
                System.out.println("样例:" + item.samples);
            }
        }
    }


    @Test
    public void singleMaskTest() {
        String strIn = "李白";
        StringBuilder strOut = new StringBuilder();

        //数据库架构配置
        Conf.ConfTableMask confTableMask = new Conf.ConfTableMask();
        confTableMask.name = "测试表";

        Conf.ConfColumnMask confColumnMask = new Conf.ConfColumnMask();
        confColumnMask.name = "name";
        confColumnMask.isMask = true;

        Conf.ConfMaskChineseName confMaskChineseName = new Conf.ConfMaskChineseName();
        confMaskChineseName.seed = (int) (Math.random() * 100);
        confMaskChineseName.process = Conf.MaskType.MASK;
        confMaskChineseName.firstName = true;
        confMaskChineseName.length = false;
        confColumnMask.confMask = confMaskChineseName;

        confTableMask.columns.add(confColumnMask);

        Maskcore maskcore = new Maskcore();
        String errorMsg = maskcore.initMask(confTableMask);
        if (!errorMsg.equals("0")) {
            System.out.println("初始化错误：" + errorMsg);
            return;
        }

        errorMsg = maskcore.singleMask(strIn, strOut);
        if (!errorMsg.equals("0")) {
            System.out.println("执行脱敏错误：" + errorMsg);
            return;
        }

        System.out.println("脱敏输出结果：" + strOut.toString());
    }

    //一般脱敏
    @Test
    public void maskTest() {
        //初始化数据
        List<List<Object>> listDatas = new ArrayList<>();
        List<Object> row = new ArrayList<>();
        row.add("1");
        row.add("白永超");
        row.add("河北省唐山市");
        listDatas.add(row);

        row = new ArrayList<>();
        row.add("2");
        row.add("赵伟全");
        row.add("河北省石家庄市");
        listDatas.add(row);

        row = new ArrayList<>();
        row.add("3");
        row.add("王兴攀");
        row.add("河北省邢台市");
        listDatas.add(row);
        System.out.println("脱敏输入结果：" + listDatas);

        List<List<Object>> listOutDatas = new ArrayList<>();

        //数据库架构配置
        Conf.ConfTableMask confTableMask = new Conf.ConfTableMask();
        confTableMask.name = "测试表";

        Conf.ConfColumnMask confColumnMask = new Conf.ConfColumnMask();
        confColumnMask.name = "id";
        confColumnMask.isMask = false;
        confTableMask.columns.add(confColumnMask);

        confColumnMask = new Conf.ConfColumnMask();
        confColumnMask.name = "name";
        confColumnMask.isMask = true;
        Conf.ConfMaskChineseName confMaskChineseName = new Conf.ConfMaskChineseName();
        confMaskChineseName.seed = (int) (Math.random() * 100);
        confMaskChineseName.process = Conf.MaskType.COVER;
        confMaskChineseName.firstName = true;
        confMaskChineseName.length = true;
        confMaskChineseName.coverType=2;
        confMaskChineseName.symbol="#";
        confColumnMask.confMask = confMaskChineseName;
        confTableMask.columns.add(confColumnMask);

        confColumnMask = new Conf.ConfColumnMask();
        confColumnMask.name = "address";
        confColumnMask.isMask = true;
        Conf.ConfMaskAddress confMaskAddress = new Conf.ConfMaskAddress();
        confMaskAddress.seed = (int) (Math.random() * 100);
        confMaskAddress.process = Conf.MaskType.COVER;
        confMaskAddress.province = false;
        confMaskAddress.city = false;
        confMaskAddress.county = false;
        confMaskAddress.town = false;
        confMaskAddress.village = false;
        confMaskAddress.street = false;
        confMaskAddress.suffix = false;
        confColumnMask.confMask = confMaskAddress;

        confTableMask.columns.add(confColumnMask);

        Maskcore maskcore = new Maskcore();
        String errorMsg = maskcore.initMask(confTableMask);
        if (!errorMsg.equals("0")) {
            System.out.println("初始化错误：" + errorMsg);
            return;
        }

        errorMsg = maskcore.mask(listDatas, listOutDatas);
        if (!errorMsg.equals("0")) {
            System.out.println("执行脱敏错误：" + errorMsg);
            return;
        }

        System.out.println("脱敏输出结果：" + listOutDatas);
    }

}
