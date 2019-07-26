package top.zigaoliang.model.file;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by root on 18-6-14.
 */
public abstract class FileFilterParent {
    private String regex = "";

    private Character ASTERISK = '*';
    private Character QUESIONMASK = '?';

    public FileFilterParent(String regex) {
        this.regex = regex;
    }

    public boolean acceptMatch(String name) {
        if (StringUtils.isBlank(regex)) {
            return true;
        } else {
            if (regex.indexOf(",") > 0) {
                String[] regexs = regex.split(",");
                boolean bMatch = false;
                for (String reg : regexs) {
                    bMatch = bMatch(reg, name);
                    if (bMatch) {
                        break;
                    }
                }
                return bMatch;
            } else {
                return bMatch(regex, name);
            }
        }
    }

    private boolean bMatch(String rex, String fileName) {
        boolean bMatched = true;
        int curFilePos = 0;
        for (int i = 0; i < rex.length(); i++) {
            //如果是*，则可以跳过多位
            if (rex.charAt(i) == ASTERISK) {
                continue;
            } else if (rex.charAt(i) == QUESIONMASK) {
                curFilePos++;
                continue;
            } else {
                if (i == 0) {
                    if (rex.charAt(i) != fileName.charAt(i)) {
                        return false;
                    } else {
                        curFilePos++;
                        continue;
                    }
                } else {
                    if (rex.charAt(i - 1) == ASTERISK) {
                        boolean bHave = false;
                        for (int j = curFilePos; j < fileName.length(); j++) {
                            if (rex.charAt(i) == fileName.charAt(j)) {
                                curFilePos = j + 1;
                                bHave = true;
                                break;
                            }
                        }
                        if (!bHave) {
                            return false;
                        }
                    } else {
                        if (rex.charAt(i) != fileName.charAt(curFilePos)) {
                            return false;
                        } else {
                            curFilePos++;
                            continue;
                        }
                    }
                }
            }
        }

        if (rex.charAt(rex.length() - 1) != ASTERISK && curFilePos < fileName.length() - 1) {
            bMatched = false;
        }
        return bMatched;
    }
}
