package top.zigaoliang.contant;

public class ComanyName {
    //注意：不能排序 （遍历到"有限公司"  就不能找"公司"）
    public static String[] comFindType = {"有限公司", "公司","集团"};
    public static String[] unitFindType = {
            "税务", "集团", "证券", "银行", "机构", "商店", "学校", "学院","分校","医院",
            "研究所","情报所","电台","电视台","观察站","种子站","图书馆","博物馆",
            "信息中心","研究院","大学",
            "研究中心","培训中心","检测中心","出版社","报社","总社","稽查大队","执法大队",
            "中学","干部学校","股份", "中心", "中介", "企业", "商行", "个体户", "机关",
            "部", "委", "局", "所", "委", "办", "科", "室", "署", "部","厂", "司", "团",
            "厅", "院", "学", "户", "店","工会", "馆", "社", "站", "队", "组"};

    public static String[] comMaskTypeArray = {"专卖", "乘用车", "五金", "介质", "代理", "企业", "传媒", "体育", "供水", "供电",
                "供销", "保险", "信息", "光伏", "光学", "光电", "全民", "典当", "冶金", "冷冻", "刀片", "分装", "创意", "制冷",
                "制品", "制药", "制衣", "制衣", "制造", "加工", "助剂", "包装", "化妆品", "化工", "化纤", "医疗", "华东", "华北",
                "华南", "卫浴", "印刷", "印务", "印染", "发展", "合资", "咨询", "商店", "商行", "啤酒", "喷织", "器件", "国税",
                "培训", "塑业", "塑件", "塑料", "外商", "媒介", "安装", "宏观", "实业", "客户", "客运", "家具", "家居", "家电",
                "容器", "小组", "工业", "工具", "工程", "市场", "市政", "布业", "广告", "建设", "开发", "彩印", "影业", "总汇",
                "成衣", "成衣", "或", "房地产", "所", "手机", "执行", "技术", "投资", "护栏", "担保", "拍卖", "控股", "摩托",
                "文化", "无限", "日用", "显示", "服务", "服装", "服饰", "服饰", "木业", "机床", "机构", "机械", "机械", "机电",
                "材料", "果蔬", "毛衫", "水务", "水泥", "汽修", "汽车", "汽配", "汽门", "涂料", "涂料", "清油", "漂染", "照明",
                "物业", "物流", "物资", "玩具", "环保", "环境", "玻璃", "玻纤", "生物", "电力", "电动", "电器", "电器", "电子",
                "电机", "电梯", "电气", "电气", "电源", "电网", "电脑", "畜禽", "百货", "皮业", "皮草", "皮革", "矿业", "磁电",
                "科技", "科技", "移动", "税务", "管件", "管理", "粮食", "精密", "精细", "纤维", "纸业", "纸品", "纸塑", "纸箱",
                "纺织", "织造", "经济", "网络", "羊绒", "耐火", "职能", "聚酯", "肉品", "胶木", "艺术", "芯电", "花边", "药业",
                "营销", "衬衫", "衬衫", "袜业", "西", "设备", "设计", "证券", "责任", "贸易", "资产", "车辆", "轮胎", "软件", "软件",
                "过滤", "连锁", "通信", "配件", "酒店", "针织", "针织", "钢材", "钢结构", "钢铁", "铁路", "铜材", "铝业", "铝箔",
                "银行", "铸造", "销售", "锁业", "防火", "集体", "食品", "饮品", "饮料", "香料"};

    public static String[] comMaskTypeArrayThree = {
                "互联网", "内饰件", "化妆品", "工艺品", "日用品", "纺织品", "纺织品", "羊毛衫",
                "聚合物", "自来水", "营业部", "营养品", "输变电", "铝制品", "铝加工", "铝合金"
    };
    public static String[] comMaskTypeArrayThird = {"信息服务", "信息科技", "农副产品", "厨房设备", "厨房设备", "市场经营",
                "建筑材料", "建筑材料", "房屋修缮", "服装服饰", "水泥制品", "污水治理", "精细化工", "羊绒制品发展", "设计服务",
                "通信技术", "酒店用品", "酒店用品"
    };

    public static String[] strCityArray = {"七台河", "三亚", "三明", "三门峡", "上海", "上饶", "东莞", "东营", "中卫", "中国",
                "中山", "临夏", "临汾", "临沂", "临沧", "丹东", "丽水", "丽江", "乌兰察布", "乌海", "乌鲁木齐", "乐山", "乐清",
                "九江", "云南", "云浮", "亳州", "伊春", "伊犁", "佛山", "佳木斯", "保定", "保山", "信阳", "克州", "克拉玛依",
                "六安", "六盘水", "兰州", "兴安", "内江", "内蒙古", "凉山", "包头", "北京", "北海", "十堰", "南京", "南充",
                "南宁", "南平", "南昌", "南通", "南阳", "博尔塔拉蒙古", "厦门", "双鸭山", "台州", "台湾", "合肥", "吉安",
                "吉林", "吉林", "吐鲁番", "吕梁", "吴忠", "周口", "呼伦贝尔", "呼和浩特", "和田", "咸宁", "咸阳", "哈密",
                "哈尔滨", "唐山", "商丘", "商洛", "喀什", "嘉兴", "嘉峪关", "四川", "四平", "固原", "塔城", "大兴安岭", "大同",
                "大庆", "大理", "大连", "天水", "天津", "太原", "威海", "娄底", "孝感", "宁夏", "宁德", "宁波", "安庆", "安康",
                "安徽", "安阳", "安顺", "定西", "宜宾", "宜昌", "宜春", "宝鸡", "宣城", "宿州", "宿迁", "山东", "山南", "山西",
                "岳阳", "崇左", "巢湖", "巴中", "巴彦淖尔", "巴音郭楞蒙古", "常州", "常德", "平凉", "平顶山", "广东", "广元",
                "广安", "广州", "广西", "庆阳", "廊坊", "延安", "延边", "开封", "张家口", "张家界", "张掖", "徐州", "德宏",
                "德州", "德清", "德阳", "忻州", "怀化", "怒江", "思茅", "恩施", "惠州", "成都", "扬州", "承德", "抚州", "抚顺",
                "拉萨", "揭阳", "攀枝花", "文山", "文昌", "新乡", "新余", "新疆", "无锡", "日喀则", "日照", "昆明", "昌吉",
                "昌都", "昭通", "晋中", "晋城", "景德镇", "曲靖", "朔州", "朝阳", "本溪", "来宾", "杭州", "松原", "林芝", "果洛",
                "枣庄", "柳州", "株洲", "桂林", "梅州", "梧州", "楚雄", "榆林", "武威", "武汉", "毕节", "永州", "汉中", "汕头",
                "汕尾", "江苏", "江西", "江门", "池州", "沈阳", "沧州", "河北", "河南", "河池", "河源", "泉州", "泰安", "泰州",
                "泸州", "洛阳", "济南", "济宁", "浙江", "海东", "海北", "海南", "海南", "海口", "海宁", "海西", "淄博", "淮北",
                "淮南", "淮安", "深圳", "清远", "温州", "渭南", "湖北", "湖南", "湖州", "湘潭", "湘西", "湛江", "滁州", "滨州",
                "漯河", "漳州", "潍坊", "潮州", "澳门", "澳门", "濮阳", "烟台", "焦作", "牡丹江", "玉林", "玉树", "玉溪", "珠海",
                "甘南", "甘孜", "甘肃", "白城", "白山", "白银", "百色", "益阳", "盐城", "盘锦", "眉山", "石嘴山", "石家庄",
                "福州", "福建", "秦皇岛", "红河", "绍兴", "绥化", "绵阳", "聊城", "肇庆", "自贡", "舟山", "芜湖", "苏州", "茂名",
                "荆州", "荆门", "莆田", "莱芜", "菏泽", "萍乡", "营口", "葫芦岛", "蚌埠", "衡水", "衡阳", "衢州", "襄樊",
                "西双版纳", "西宁", "西安", "西藏", "许昌", "贵州", "贵港", "贵阳", "贺州", "资阳", "赣州", "赤峰", "辽宁",
                "辽源", "辽阳", "达州", "运城", "连云港", "迪庆", "通化", "通辽", "遂宁", "遵义", "邢台", "那曲", "邯郸", "邵阳",
                "郑州", "郴州", "鄂尔多斯", "鄂州", "酒泉", "重庆", "金华", "金昌", "钦州", "铁岭", "铜仁", "铜川", "铜陵",
                "银川", "锡林郭勒", "锦州", "镇江", "长春", "长沙", "长治", "阜新", "阜阳", "防城港", "阳江", "阳泉", "阿克苏",
                "阿勒泰", "阿坝", "阿拉善", "阿里", "陇南", "陕西", "随州", "雅安", "青岛", "青海", "鞍山", "韶关", "香港",
                "香港", "马鞍山", "驻马店", "鸡西", "鹤壁", "鹤岗", "鹰潭", "黄冈", "黄南", "黄山", "黄石", "黑河", "黑龙江",
                "黔东南", "黔南", "黔西南", "齐齐哈尔", "龙岩"};

    public static String strComNameArray = "凡利卡多安尚川州帝悦情晓衡儒静翰蔚忆双涛丽韵沃晟捷博扬索蓝昂兴聚鸿园波丰壹泽旭旺融誉际巨骄厅湾凡可巧弘禾竹多帆略众汇圣卓宇国普绿斯登阳驰通骏力顺领迅途益和伦道发唯一才月丹文立玉臻燕霖霏莲灿颜麒韬露鹤高德雅格纳欣亿维锐菲佳明滋祥逸风彩朗郎爱景帆耀艺巍兰雪尧谊影慧洁润平同志宜林奇政朋致春帅岚超清云淼业义意资湘会哲营舒曙廷渲梦瑜菏凤叶拓建江雷天策优易威玛日名欧特辰康讯鹏腾宏伟钧锦乐飞福皇嘉达佰美元亮鑫创宝星联晨百尔海瑞科振聆翌迎常浩茗杰智婷越菁萌语荣赫宁铭齐毅进霆聪垒蕾瀚骁永吉先君依昌骄为诚妙英虹芬馨尼迈群诺恒辉缘诗香鼎碧麦邦克思正成翔隆东森迪赛睿艾盈泓品庭展朔轩育航津启金新中盛亚信华豪奥凯泰彩朗赫宁铭齐毅进郎爱景帆阳驰通骏力顺领迅途益和园波丰泽旭贝仑青笑宗雨虹纪亭俊禹垚秋倩宸甜加茜涵琳微菡萱博扬索蓝昂兴聚鸿略众汇圣卓宇国普绿斯登诺恒辉缘易威玛日伦道发唯一才月丹文立玉平同志宜林奇政朋诗香鼎碧麦邦克凡利卡多安尚川州帝悦情明滋祥逸风致春帅盈泓品庭展朔轩育航津启振聆翌迎常浩茗杰智乐飞福皇嘉达亮名佰美元欧特辰康讯鹏腾宏伟钧思正旺融誉际巨骄为诚妙英虹芬馨尼迈群拓建江雷天策优金新中盛亚信华豪奥凯泰鑫创宝星联晨百尔海瑞科锦成翔隆东森迪赛睿艾高德雅格纳欣亿维锐菲佳沃晟捷霆晓衡儒静翰蔚忆双涛丽韵耀艺巍兰雪尧谊影慧洁润臻燕霖霏莲灿颜麒韬露鹤骄厅湾凡聪垒蕾瀚骁永吉先君依昌哲营舒曙廷渲梦瑜菏凤叶卫婷越岚超清云淼业义意资湘会菁萌语荣";
}