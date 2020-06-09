public class Noeud<T> {

    T i;
    Noeud<T> next;

    public Noeud(T a) {
        i = a;
    }

    public Noeud getNext() {
        return next;
    }

    public void setNext(Noeud<T> n) {
        next = n;
    }
}