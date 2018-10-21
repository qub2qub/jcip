package net.jcip.examples.publication;

/**
 * Unsafe publication
 */
public class StuffIntoPublic {
    // Две проблемы:
    //1) ссылка самого холдера плохая=null
    // 2) ошибочные значения у переменных внутри холдера
    public Holder holder;

    public void initialize() {
        holder = new Holder(42);
    }

    public static void main(String[] args) {
        StuffIntoPublic st = new StuffIntoPublic();
        System.out.println("1 st.holder = " + st.holder);
        st.initialize();
        System.out.println("2 st.holder = " + st.holder);
        st.holder = new Holder(123);
        System.out.println("3 st.holder = " + st.holder);

    }
}
