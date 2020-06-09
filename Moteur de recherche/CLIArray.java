import java.io.*;

public class CLIArray {

    double[] C;
    int[] L;
    int[] I;

    public CLIArray() {}

    public int nbLigne() {
        return L.length;
    }

    public double[][] getLigne(int ligne) {
        int nbElement;
        int max;
        if (L.length == 1) {
            nbElement = C.length;
            max = C.length;
        }
        else if (ligne == L.length-1) {
            nbElement = C.length - L[ligne];
            max = C.length;
        } else {
            nbElement = L[ligne+1] - L[ligne];
            max = L[ligne+1];
        }
        double[][] r = new double[2][nbElement];
        int j = 0;
        for (int i = L[ligne]; i < max; i++) {
            r[0][j] = C[i];
            r[1][j] = I[i];
            j++;
        }
        return r;
    }

    @Override
    public String toString() {
        System.out.print("C :\n[");
        for (int i = 0; i < C.length; i++) {
            System.out.print(C[i] + ",");
        }
        System.out.print("]\nL :\n[");
        for (int i = 0; i < L.length; i++) {
            if (L[i] == -1)
                break;
            System.out.print(L[i] + ",");
        }
        System.out.print("]\nI :\n[");
        for (int i = 0; i < I.length; i++) {
            System.out.print(I[i] + ",");
        }
        System.out.println("]");
        return "";
    }

    public void write(String filename) {
        try {
            FileWriter file = new FileWriter("cli_" + filename + ".txt");
            BufferedWriter bw = new BufferedWriter(file);
            bw.write(C.length + "\n");
            for (int i = 0; i < C.length; i++) {
                bw.write(C[i] + "\n");
            }
            for (int i = 0; i < I.length; i++) {
                bw.write(I[i] + "\n");
            }
            bw.write(L.length + "\n");
            for (int i = 0; i < L.length; i++) {
                bw.write(L[i] + "\n");
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void read(String filename) {
        try {
            File f = new File("cli_" + filename + ".txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));

            int taille = Integer.parseInt(br.readLine());
            C = new double[taille];
            for (int i = 0; i < taille; i++) {
                C[i] = Double.parseDouble(br.readLine());
            }
            I = new int[taille];
            for (int i = 0; i < taille; i++) {
                I[i] = Integer.parseInt(br.readLine());
            }
            taille = Integer.parseInt(br.readLine());
            L = new int[taille];
            for (int i = 0; i < taille; i++) {
                L[i] = Integer.parseInt(br.readLine());
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Vecteur multiplication(Vecteur vec) {
        double[] v = new double[vec.v.length];
        for (int i = 0; i < L.length - 1; i++) {
            for (int j = L[i]; j < L[i + 1]; j++) {
                v[I[j]] += C[j] * vec.v[i];
            }
        }

        return new Vecteur(v);
    }

   public Vecteur pageRank(Vecteur v, int pas) {
        Vecteur p = v;

        //System.out.println(v);
        Vecteur last;
        while (pas > 0) {
            last = p;
            p = this.multiplication(p);
            //System.out.println(p + " " + p.distance(last));
            pas--;
        }

        return p;
    }

    public Vecteur variantePageRank(Vecteur v, double precision) {
        Vecteur p = v;
        //System.out.println(v);
        double d = 0.15;
        int n = p.v.length;
        Vecteur last;
        do {
            last = p;
            p = this.multiplication(p);
            for (int i = 0; i < n; i++) {
                p.v[i] = (d / n) + (1 - d) * p.v[i];
            }
            //System.out.println(p + " " + p.distance(last));
        } while (p.distance(last) > precision);

        return p;
    }

    public void convertir() {
        for (int i = 0; i < L.length - 1; i++) {
            for (int j = L[i]; j < L[i + 1]; j++) {
                C[j] /= L[i + 1] - L[i];
            }       
        }
    }
}