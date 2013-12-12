package introduceParameter;

public class Class_A_J implements Runnable{
        
    private int field = 4;
    
    public Class_A_J() {
        int x = field;
    }
       
    public void m(int param, int zz) {
        String x = "ABC";
        String y = "ABC";
        int z = zz;
    }
    
    public void run() {
        int x = 3;
    }
    
    
    class Super {
        public void m1(final int myParameter) {
            int y = 3;
        }
    }
    
    class Sub extends Super {
        public void m1(final int myParameter) {
        }
    }
    
    public void x() {
        int i = field;
    }
    
    public void y() {
        long i = System.currentTimeMillis();
    }
    
    class Generics<T> {
        public void genMethod() {
            T t = null;
        }
    }
    
    public void usage() {
        new Class_A_J();        
        m(1,2);
        new Super().m1(3);
        new Sub().m1(3);
        y();
    
    }                                             
}


