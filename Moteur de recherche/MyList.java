public class MyList<T> {

    Noeud<T> first;
    Noeud<T> last;
    int size;

    public MyList() {
        size = 0;
    }

    public void add(T i) {
        if (size == 0) {
            first = new Noeud<>(i);
            last = first;
            size++;
        }
        else {
            Noeud<T> n = new Noeud<>(i);
            last.setNext(n);
            last = n;
            size++;
        }
    }

    public int size() {
        return size;
    }

    public T poll() {
        if (size > 0) {
            T i = first.i;
            first = first.next;
            size--;
            return i;
        }
        return null;
    }
}