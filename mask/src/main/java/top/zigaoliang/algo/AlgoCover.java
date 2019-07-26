package top.zigaoliang.algo;


import com.google.common.base.Strings;
import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.DateUtil;

/**
 * 遮蔽算法
 * Created by byc on 10/24/18.
 */
public class AlgoCover extends AlgoBase {
    private static Logger log = Logger.getLogger(AlgoCover.class);

    public AlgoCover() {
        super(AlgoId.COVER);
    }

    private Conf.ConfMaskCover confMaskCover = new Conf.ConfMaskCover();

    @Override
    public int init(Conf.ConfMask confMask) {
        if (confMask == null) {
            return ErrorCode.CONF_INIT_MASK.getCode();
        }
        this.confMask = confMask;
        if (confMask instanceof Conf.ConfMaskCover) {
            confMaskCover = (Conf.ConfMaskCover) confMask;
        }
        return 0;
    }

    @Override
    public boolean find(String in) {
        return true;
    }

    @Override
    public int random(String in, StringBuilder out) {
        return cover(in, out);
    }

    @Override
    public int mask(String in, StringBuilder out) {
        return cover(in, out);
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        out.append(in);
        return 0;
    }

    public  int cover(String in, StringBuilder out) {
        ErrorCode errorCode = null;
        if (Strings.isNullOrEmpty(in)) {
            errorCode = ErrorCode.COVER_INPUT;
            log.info(errorCode.getMsg() + "; 输入数据：" + in);
            out.append(in);
            return 0;
        }
        int length = 0;
        String split = "";
        String[] emailArray = null;
        if (confMaskCover.relateId == 20) {
            split = "@";
            emailArray = in.split(split);
            length = confMaskCover.place == true ? emailArray[0].length() : emailArray[1].length();
        } else if (confMaskCover.relateId == 12) {
            split = ".";
            emailArray = in.split(split);
            length = confMaskCover.place == true ? emailArray[0].length() : emailArray[1].length();
        } else {
            length = in.length();
        }
        int start = confMaskCover.begin <= 0 ? 0 : confMaskCover.begin - 1;
        int end = confMaskCover.end >= 0 ? (confMaskCover.end > length ? length : confMaskCover.end) : length + confMaskCover.end + 1;
        if (!paramCheck(in, length, start, end)) {
            out.append(in);
            return 0;
        }
        String symbol = this.getCoverStr(start, end);
        if (confMaskCover.relateId == 20 || confMaskCover.relateId == 12) {
            //对邮箱进行遮蔽
            //对金额数字进行遮蔽
            try {
                if (confMaskCover.place) {
                    if (confMaskCover.direction) {
                        out.append(emailArray[0]);
                        //从左向右
                        out.replace(confMaskCover.begin - 1, confMaskCover.end, symbol);
                    } else {
                        //从右向左
                        StringBuilder tem = new StringBuilder(emailArray[0]).reverse()
                                .replace(confMaskCover.begin - 1, confMaskCover.end, symbol);
                        tem.reverse();
                        out.append(tem.toString());
                    }
                    out.append(split).append(emailArray[1]);
                } else {
                    out.append(emailArray[0]).append(split);
                    if (confMaskCover.direction) {
                        //从左向右
                        StringBuilder tem = new StringBuilder();

                        tem.replace(confMaskCover.begin - 1, confMaskCover.end, symbol.toString());
                        out.append(tem);
                    } else {
                        //从右向左
                        StringBuilder tem = new StringBuilder(emailArray[0]).reverse()
                                .replace(confMaskCover.begin - 1, confMaskCover.end, symbol.toString());

                        tem.reverse();
                        out.append(tem.toString());
                    }
                }
            } catch (Exception e) {
                errorCode = ErrorCode.COVER_EMAILANDMONEY;
                log.debug(errorCode.getMsg() + "; 输入数据：" + in);
                return errorCode.getCode();
            }
            return 0;
        } else if (confMaskCover.relateId == 8) {
            out.append(this.coverForDate(in));
            return 0;
        } else {
            out.append(in);
            //剩余的算法进行统一的处理
            if (confMaskCover.direction) {
                out.replace(start, end, symbol);
            } else {
                out.reverse().replace(start, end, symbol).reverse();
            }

        }

        return 0;
    }

    /**
     * 对传入的遮蔽的参数进行校验
     *
     * @param in
     * @return length  要被遮蔽的字符串的长度
     */
    public boolean paramCheck(String in, int length, int start, int end) {
        ErrorCode errorCode = null;
        if (start > end) {
            errorCode = ErrorCode.COVER_INDE_ERROR;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return false;
        }
        if (confMaskCover.begin > length) {
            errorCode = ErrorCode.COVER_INDEX_LONG;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return false;
        }
        if (confMaskCover.end + length < 0) {
            errorCode = ErrorCode.COVER_INDEX_UNDER;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return false;
        }
        return true;
    }

    /**
     * 获得用来遮蔽的 字符串
     *
     * @return
     */
    public String getCoverStr(int start, int end) {
        StringBuilder symbol = new StringBuilder();
        for (int i = 0; i < end - start; i++) {
            symbol.append(confMaskCover.symbol);
        }
        return symbol.toString();
    }

    /**
     * 对日期进行遮蔽
     *
     * @return
     */
    public String coverForDate(String in) {
        if (confMaskCover.dateCoverType > 5 || confMaskCover.dateCoverType < 1) {
            return null;
        }
        //对日期进行这个处理
        String[] date = DateUtil.getDateInfor(in);
        String split = DateUtil.getDateSplit(in);
        StringBuilder out = new StringBuilder();
        if (split.equals("年")) {
            out.append(date[0]).append(split);
            if (confMaskCover.dateCoverType == 1) {
                out.append(DateUtil.getCoverStr(2, confMaskCover.symbol))
                        .append("月").append(DateUtil.getCoverStr(2, confMaskCover.symbol))
                        .append("日");
                out.append(" ").append(DateUtil.getCoverStr(2, confMaskCover.symbol))
                        .append(":").append(DateUtil.getCoverStr(2, confMaskCover.symbol))
                        .append(":").append(DateUtil.getCoverStr(2, confMaskCover.symbol));
            }
            if (confMaskCover.dateCoverType == 2) {
                out.append(date[1])
                        .append("月").append(DateUtil.getCoverStr(2, confMaskCover.symbol))
                        .append("日");
                out.append(" ").append(DateUtil.getCoverStr(2, confMaskCover.symbol)).append(":")
                        .append(DateUtil.getCoverStr(2, confMaskCover.symbol)).append(":")
                        .append(DateUtil.getCoverStr(2, confMaskCover.symbol));
            }
            if (confMaskCover.dateCoverType == 3) {
                out.append(date[1])
                        .append("月").append(date[2])
                        .append("日");
                out.append(" ").append(DateUtil.getCoverStr(2, confMaskCover.symbol))
                        .append(":").append(DateUtil.getCoverStr(2, confMaskCover.symbol))
                        .append(":").append(DateUtil.getCoverStr(2, confMaskCover.symbol));
            }
            if (confMaskCover.dateCoverType == 4) {
                out.append(date[1])
                        .append("月").append(date[2])
                        .append("日");
                out.append(" ").append(date[3]).append(":")
                        .append(DateUtil.getCoverStr(2, confMaskCover.symbol))
                        .append(":").append(DateUtil.getCoverStr(2, confMaskCover.symbol));
            }
            if (confMaskCover.dateCoverType == 5) {
                out.append(date[1])
                        .append("月").append(date[2])
                        .append("日");
                out.append(" ").append(date[3]).append(":")
                        .append(date[4])
                        .append(":").append(DateUtil.getCoverStr(2, confMaskCover.symbol));
            }
        } else {
            if (confMaskCover.dateCoverType == 1) {
                out.append(date[0]).append(split)
                        .append(DateUtil.getCoverStr(2, confMaskCover.symbol))
                        .append(split).append(DateUtil.getCoverStr(2, confMaskCover.symbol));
                out.append(" ").append(DateUtil.getCoverStr(2, confMaskCover.symbol))
                        .append(":").append(DateUtil.getCoverStr(2, confMaskCover.symbol))
                        .append(":").append(DateUtil.getCoverStr(2, confMaskCover.symbol));
            }
            if (confMaskCover.dateCoverType == 2) {
                out.append(date[0]).append(split)
                        .append(date[1])
                        .append(split).append(DateUtil.getCoverStr(2, confMaskCover.symbol));
                out.append(" ").append(DateUtil.getCoverStr(2, confMaskCover.symbol))
                        .append(":").append(DateUtil.getCoverStr(2, confMaskCover.symbol))
                        .append(":").append(DateUtil.getCoverStr(2, confMaskCover.symbol));
            }
            if (confMaskCover.dateCoverType == 3) {
                out.append(date[0]).append(split)
                        .append(date[1])
                        .append(split).append(date[2]);
                out.append(" ").append(DateUtil.getCoverStr(2, confMaskCover.symbol))
                        .append(":").append(DateUtil.getCoverStr(2, confMaskCover.symbol))
                        .append(":").append(DateUtil.getCoverStr(2, confMaskCover.symbol));

            }
            if (confMaskCover.dateCoverType == 4) {
                out.append(date[0]).append(split)
                        .append(date[1])
                        .append(split).append(date[2]);
                out.append(" ").append(date[3])
                        .append(":").append(DateUtil.getCoverStr(2, confMaskCover.symbol))
                        .append(":").append(DateUtil.getCoverStr(2, confMaskCover.symbol));
            }
            if (confMaskCover.dateCoverType == 5) {
                out.append(date[0]).append(split)
                        .append(date[1])
                        .append(split).append(date[2]);
                out.append(" ").append(date[3])
                        .append(":").append(date[4])
                        .append(":").append(DateUtil.getCoverStr(2, confMaskCover.symbol));
            }

        }
        return out.toString();
    }

}
