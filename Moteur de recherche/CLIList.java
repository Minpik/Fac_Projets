import java.io.*;

public class CLIList {

    MyList<Double> C;
    MyList<Integer> L;
    MyList<Integer> I;
    int last;

    public CLIList() {
        C = new MyList<>();
        L = new MyList<>();
        I = new MyList<>();
        last = -1;
    }

    public void add(double n, int ligne, int colonne) {
        C.add(n);
        I.add(colonne);
        if (last == -1) {
            if (ligne > 0) {
                for (int i = 0; i < ligne; i++) {
                    L.add(0);
                }
            }
            L.add(C.size() - 1);
            last = ligne;
        } else {
            if (last < ligne - 1) {
                for (int j = last; j < ligne; j++) {
                    L.add(C.size() - 1);
                }
            } else if (last != ligne) {
                L.add(C.size() - 1);
            }

            last = ligne;
        }
    }

    public void end(int ligne) {
        if (last < ligne - 1) {
            for (int j = last; j < ligne; j++) {
                L.add(C.size());
            }
        } else if (last != ligne) {
            L.add(C.size());
        }

        last = ligne;
    }

    public void write(String filename) {
        try {
            FileWriter file = new FileWriter("cli_" + filename + ".txt");
            BufferedWriter bw = new BufferedWriter(file);
            bw.write(C.size() + "\n");
            while(C.size() > 0) {
                bw.write(C.poll() + "\n");
            }
            while (I.size() > 0) {
                bw.write(I.poll() + "\n");
            }
            bw.write(L.size() + "\n");
            while(L.size() > 0) {
                bw.write(L.poll() + "\n");
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CLIArray toArray() {
        CLIArray r = new CLIArray();
        r.C = new double[C.size()];
        int nb = C.size();
        for (int i = 0; i < nb; i++) {
            r.C[i] = C.poll();
        }
        r.I = new int[nb];
        for (int i = 0; i < nb; i++) {
            r.I[i] = I.poll();
        }
        nb = L.size();
        r.L = new int[nb];
        for (int i = 0; i < nb; i++) {
            r.L[i] = L.poll();
        }
        return r;
    }
}