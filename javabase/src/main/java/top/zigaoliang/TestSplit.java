package top.zigaoliang;

/**
 * @ClassName TestSplit
 * @Author hanlin
 * @Date 2019/8/15 16:16
 **/
public class TestSplit {

    public static void main(String[] args) {
        String str = "ab~~cdefg";
        String[] ss = str.split("~");
        for(String s : ss){
            System.out.println(s);
        }
    }
}
