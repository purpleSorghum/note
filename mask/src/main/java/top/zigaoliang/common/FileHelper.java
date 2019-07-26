package top.zigaoliang.common;


import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.sun.javaws.Globals;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import top.zigaoliang.model.file.FS_FilenameFilter;
import top.zigaoliang.util.DECUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by zeh on 2018/8/18.
 * 处理文件工具类
 */
public class FileHelper {
    private static Logger log = Logger.getLogger(FileHelper.class);

    //删除目录和其子目录[JDK自身非空目录不能删除]
    public static void deleteDir(String dir) {
        if (dir == "/") {
            log.error("can not rm root /");
            return;
        }

        File d = new File(dir);
        if (!d.exists()) {
            return;
        }
        for (File f : d.listFiles()) {
            if (f.isDirectory()) {
                deleteDir(f.getPath());
            }

            if (f.exists()) {
                f.delete();
            }
        }
        if (d.exists()) {
            d.delete();
        }
    }

    public static boolean dirMake(String path) {
        if (null == path) {
            return false;
        }
        File dirFile = new File(path);
        if (!dirFile.exists() && !dirFile.isDirectory()) {
            return dirFile.mkdirs();
        }
        return true;
    }

    public static boolean createNewFile(String path, String fileName) throws IOException {
        if (null == path || null == fileName) {
            return false;
        }
        File dirFile = new File(path);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            if (!dirFile.mkdirs()) {
                return false;
            }
        }
        String filePath = path + fileName;
        if (!path.endsWith("/") && !fileName.startsWith("/")) {
            filePath = path + "/" + fileName;
        }
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return file.createNewFile();
        }
        return false;
    }

    public static boolean isDir(String path) {
        if (null == path) {
            return false;
        }
        File dirFile = new File(path);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        return true;
    }

    public static boolean isFile(String path) {
        if (null == path) {
            return false;
        }
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            return false;
        }
        return true;
    }

    public static boolean isExist(String path) {
        if (null == path) {
            return false;
        }
        File file = new File(path);
        if (file != null && file.exists()) {
            return true;
        }
        return false;
    }

    public static boolean dirHaveFiles(String path) {
        if (null == path) {
            return false;
        }
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            return false;
        }
        for (File file : dirFile.listFiles()) {
            if (file.isDirectory()) {
                if (!dirHaveFiles(file.toString())) {
                    continue;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }

        return false;
    }

    public static void copyFile(String source, String target) throws IOException {
        Files.copy(Paths.get(source), Paths.get(target), REPLACE_EXISTING);
    }

    public static void copyDir(String source, String target) throws IOException {
        copyDir(source, target, true);
    }

    public static void copyDir(String source, String target, boolean toChildren) throws IOException {
        File d = new File(source);
        for (File f : d.listFiles()) {
            if (f.isDirectory()) {
                dirMake(target + "/" + f.getName());
                if (toChildren) {
                    copyDir(f.getAbsolutePath(), target + "/" + f.getName(), toChildren);
                }
            } else {
                Path t = Paths.get(target);
                if (!t.toFile().exists()) {
                    dirMake(target);
                }
                Files.copy(Paths.get(f.getAbsolutePath()), Paths.get(target, f.getName()), REPLACE_EXISTING);
            }
        }
    }

    public static Long getFileSize(Path path) {
        FileChannel fc = null;
        Long size = 0L;
        try {
            File f = path.toFile();
            if (f.exists() && f.isFile()) {
                FileInputStream fis = new FileInputStream(f);
                fc = fis.getChannel();
                size = fc.size();
            }
        } catch (IOException e) {
            log.error("计算文件大小出错", e);
        } finally {
            if (null != fc) {
                try {
                    fc.close();
                } catch (IOException e) {
                    log.error("计算文件大小出错", e);
                }
            }
        }

        return size;
    }

    public static void deleteFile(Path path) {
        File f = path.toFile();
        if (f.exists()) {
            f.delete();
        }
        // 判断当前文件夹是否包含文件，如果没有则删除该文件夹
        if (0 == f.getParentFile().list().length) {
            f.getParentFile().delete();
        }
    }

    public static void deleteOnlyFile(Path path) {
        File f = path.toFile();
        if (f.exists()) {
            f.delete();
        }
    }

    public static void clearPath(File file, boolean self) {
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            if (self) {
                file.delete();
            }
        } else {
            File[] files = file.listFiles();
            if (files != null) {
                Arrays.stream(files).forEach(f -> {
                    clearPath(f, true);
                });
            }
            if (self) {
                file.delete();
            }
        }
    }

    public static boolean renameFile(String path, String newName) {
        if (Strings.isNullOrEmpty(path)) {
            return false;
        }

        File file = new File(path);
        if (file == null || !file.exists()) {
            return false;
        }

        return file.renameTo(new File(newName));
    }

    /**
     * 文件夹与文件名串联
     */
    public static String dirBunchFile(String dir) {
        String retStr;
        if (dir.indexOf("/") >= 0) {
            retStr = dir + (dir.endsWith("/") ? "" : "/");
        } else if (dir.indexOf("\\") >= 0) {
            retStr = dir + (dir.endsWith("\\") ? "" : "\\");
        } else {
            throw new RuntimeException("传入的文件路径错误!");
        }
        return retStr;
    }

    /**
     * 给定的文件夹中是否包含匹配的文件名的文件，包括子文件夹
     *
     * @param fileDir   文件夹路径
     * @param fileStyle 过滤条件
     * @return 存在，返回true，不存在，返回false
     */
    public static boolean isHaveFilesIncludeChildDir(String fileDir, String fileStyle) {
        boolean bHave = false;
        File[] fileList = getFilesNotIncludeChildDir(fileDir, fileStyle);
        for (File f : fileList) {
            if (f.isDirectory()) {
                bHave = isHaveFilesIncludeChildDir(f.getAbsolutePath(), fileStyle);
                if (bHave) {
                    return true;
                } else {
                    continue;
                }
            } else if (f.isFile()) {
                return true;
            }
        }
        return bHave;
    }

    /**
     * 给定的文件夹中是否包含匹配的文件名的文件及文件夹
     *
     * @param fileDir   文件夹路径
     * @param fileStyle 过滤条件
     * @return 匹配的文件及文件夹
     */
    public static File[] getFilesNotIncludeChildDir(String fileDir, String fileStyle) {
        File file = new File(fileDir);
        if (StringUtils.isBlank(fileStyle)) {
            return file.listFiles();
        } else {
            return file.listFiles(new FS_FilenameFilter(fileStyle));
        }
    }

    /**
     * 读取静态资源文件
     *
     * @param <T>
     * @return
     */
    public static <T> List<T> readSource(String file, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        synchronized (FileHelper.class) {
            try {
                String text = IOUtils.toString(Globals.class.getResourceAsStream(file));
                String newText = DECUtil.decrypt(text);
                list = (List<T>) JSON.parseArray(newText, clazz);
            } catch (Exception e) {
                log.error("读取资源文件出现未知异常 要读取的文件名称：" + file);
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 获得读取静态资源的流
     *
     * @return
     */
    public static InputStream getFileStream(String filePath) {
        InputStream inputStream = null;
        synchronized (FileHelper.class) {
            try {
                inputStream = Globals.class.getResourceAsStream(filePath);
            } catch (Exception e) {
                log.error("获得读取静态资源的流 出现未知异常：" + filePath);
                e.printStackTrace();
            }
        }
        return inputStream;
    }

    //读取静态资源文件转字符串
    public static String readResourceToStr(String filePath) {
        String text = "";
        synchronized (FileHelper.class) {
            try {
                text = inputStrem2String(getFileStream(filePath));
                text = DECUtil.decrypt(text);
            } catch (Exception e) {
                log.error("获得读取静态资源 出现未知异常：" + filePath);
                e.printStackTrace();
            }
        }
        return text;
    }


    //文件流转字符串
    public static String inputStrem2String(InputStream in) {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(in, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        String line = "";
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


}
