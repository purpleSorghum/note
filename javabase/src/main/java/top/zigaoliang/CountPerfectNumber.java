package top.zigaoliang;

import org.junit.Test;

/**
 * @ClassName CountPerfectNumber
 * @Author hanlin
 * @Date 2019/6/25 17:30
 **/
public class CountPerfectNumber {
    public static void main(String[] args) {
        long start=System.currentTimeMillis();
        for(int i =2 ;i<1000;i=i+2){
            int sum = 1 ;
            for(int j = 2;j <= i>>1;j++){
                if(i % j == 0){
                    sum+=j;
                }
            }
            if(sum == i){
                System.out.println(i);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("共花时间(ms)："+ (end - start));
    }

    @Test
    public void PerfectNumber(){
        int p = 17;
        long x=2 << p-1-1;
        long y=(2 << p-1)-1;
        System.out.println(x * y);
    }

    @Test
    public void ss(){
        for(int i =2 ;i<1000;i++){
            boolean f=true;
            for(int j = 2;j <= i>>1;j++){
                if(i % j == 0){
                    f=false;
                    break;
                }
            }
            if(f) System.out.println(i);
        }
    }

}
