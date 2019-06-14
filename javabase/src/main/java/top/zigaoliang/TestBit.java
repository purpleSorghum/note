package top.zigaoliang;

/**
 * @ClassName TestBit
 * @Author hanlin
 * @Date 2019/6/14 15:45
 **/
public class TestBit {
    public static void main(String[] args) {
        System.out.println(isTowPower(16));
        System.out.println(System.currentTimeMillis());
        System.out.println(System.currentTimeMillis());
        System.out.println(System.nanoTime());
        System.out.println(System.nanoTime());
    }

    //如果一个数是2 的n次方，则这个数 与它减1的数进行与运算，结果为0
    public static boolean isTowPower(int n){
        return (n & n-1)==0;
    }
}
