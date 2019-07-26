package top.zigaoliang;

/**
 * @ClassName Test
 * @Author hanlin
 * @Date 2019/7/2 16:11
 **/
public class Test {
    public static void main(String[] args) {
        System.out.println("hello java");
        System.out.println(Thread.currentThread().isInterrupted());
        Thread.interrupted();
    }
}
