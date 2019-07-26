package top.zigaoliang.model.file;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by root on 18-6-13.
 */
public class FS_FilenameFilter extends FileFilterParent implements FilenameFilter {

    public FS_FilenameFilter(String regex) {
        super(regex);
    }


    private boolean isDir(String dir, String fileName) {
        String filePath = "";
        if (dir.indexOf("/") >= 0) {
            filePath = dir + "/" + fileName;
        } else if (dir.indexOf("\\") >= 0) {
            filePath = dir + "\\" + fileName;
        }
        File file = new File(filePath);
        if (file.isDirectory()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean accept(File dir, String name) {
        //如果是目录，直接返回true,不用匹配
        if (isDir(dir.toString(), name)) {
            return true;
        }
        return acceptMatch(name);
    }
}
