import java.lang.Math;

public class Vecteur {

    double[] v;

    public Vecteur(double[] tab) {
        v = tab;
    }

    @Override
    public String toString() {
        String s = "[";
        for (int i = 0; i < v.length; i++)
            s += v[i] + ",";

        return s + "]";
    }

    public double distance(Vecteur v) {
        double d = 0;
        for (int i = 0; i < v.v.length; i++) {
            d += Math.abs((this.v[i] - v.v[i]));
        }

        return d;
    }
}