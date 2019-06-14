package top.zigaoliang;

/**
 * @ClassName TestInterface
 * @Author hanlin
 * @Date 2019/6/13 14:32
 **/
public class TestInterface {

    public interface I1{
        void a();
        void b();
        void c();
    }

    public abstract class AbsA implements I1{
        public void a() {
            System.out.println("a");
        }

        public void b() {
            System.out.println("b");
        }
    }

    public class C1 extends AbsA{

        @Override
        public void a() {
            super.a();
        }

        public void c() {

        }
    }

}
