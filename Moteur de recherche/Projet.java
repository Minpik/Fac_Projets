import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.IOException;
import java.text.Normalizer;

@SuppressWarnings("unchecked")

public class Projet {

    public static void main(String[] args) {
        String p = "cinema";
        if (args.length == 1) {
            p = args[0];
        }
        selectTitles(p);
        mostCountedWords(p);
        words_pagesAndLinks(p);
        CLIArray cli = tri(p, 5);
        cli.write("sorted_" + p);
        System.out.println("Calcul terminé");
    }

    public static void selectTitles(String filename) {
        System.out.println("SELECT TITLES");
        HashSet<String> fw = new HashSet<>();
        try {
            File f = new File("fw");
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"));
            String line;

            while ((line = br.readLine()) != null) {
                fw.add(line);
            }

            br.close();
            // System.out.println(fw.size());

            f = new File(filename + ".xml");
            br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"));
            FileWriter writer = new FileWriter("words_title_" + filename + ".txt");
            BufferedWriter bw = new BufferedWriter(writer);
            FileWriter writer1 = new FileWriter("title_" + filename + ".txt");
            BufferedWriter bw1 = new BufferedWriter(writer1);
            int nb = 0;
            HashSet<String> deja = new HashSet<>();
            int x = 0;
            while ((line = br.readLine()) != null) {
                if (line.contains("<title>") && line.contains("</title>") && !line.contains(":")) {
                    x++;
                    String s = enleverBalises(line);
                    s = s.substring(1);
                    if (s.length() == 1 && s.charAt(0) == ' ') {
                        continue;
                    }

                    if (nb % 5000 == 0) {
                        System.out.println(nb);
                    }
                    bw1.write(s + "\n");
                    nb++;
                    Matcher m = Pattern.compile("[\\w\\d]+").matcher(s);
                    while (m.find()) {
                        String st = normaliser(m.group());
                        if (!fw.contains(st) && !deja.contains(st)) {
                            deja.add(st);
                            bw.write(st + '\n');
                        }
                    }
                }
            }
            System.out.println("nbr --->" + x);
            br.close();
            bw.close();
            bw1.close();
            writer1.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void mostCountedWords(String filename) {
        System.out.println("MOST COUNTED WORDS");
        HashSet<String> mot_titre = new HashSet<>();
        try {
            File f = new File("words_title_" + filename + ".txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"));
            String line;

            while ((line = br.readLine()) != null) {
                mot_titre.add(normaliser(line));
            }

            br.close();

            f = new File(filename + ".xml");
            br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"));

            int nb = 0;
            HashMap<String, Integer> dic = new HashMap<>();
            boolean b = false;
            StringBuffer text = new StringBuffer();

            int x = 0;
            boolean compter = true;
            while ((line = br.readLine()) != null) {
                if (line.contains("<title>") && line.contains("</title>") && !line.contains(":")) {
                    compter = true;
                    x++;
                } else if (line.contains("<title>") && line.contains("</title>")) {
                    compter = false;
                }
                if (b) {
                    text.append(line);
                    text.append("\n");
                }
                if (line.contains("<text") && compter) {
                    text.append(line);
                    text.append("\n");
                    b = true;
                }
                if (line.contains("</text>") && compter) {
                    if (nb % 5000 == 0) {
                        System.out.println(nb);
                    }
                    nb++;
                    String t = normaliser(enleverBalises(text.toString()));
                    Matcher m = Pattern.compile("[\\w\\d]+").matcher(t);
                    while (m.find()) {
                        String st = m.group();
                        if (mot_titre.contains(st)) {
                            if (dic.containsKey(st)) {
                                dic.replace(st, dic.get(st) + 1);
                            } else {
                                dic.put(st, 1);
                            }
                        }
                    }
                    text = new StringBuffer();
                    b = false;
                }
            }
            br.close();

            System.out.println("nbr --->" + x);
            Object[] a = dic.entrySet().toArray();
            Arrays.sort(a, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((Map.Entry<String, Integer>) o2).getValue()
                            .compareTo(((Map.Entry<String, Integer>) o1).getValue());
                }
            });
            nb = 0;
            HashSet<String> s = new HashSet<>(10000);
            for (Object e : a) {
                if (nb == 10000) {
                    break;
                }
                nb++;
                s.add(((Map.Entry<String, Integer>) e).getKey());
            }

            FileWriter writer = new FileWriter("most_count_sorted_" + filename + ".txt");
            BufferedWriter bw = new BufferedWriter(writer);

            List<String> list = new ArrayList<String>(s);
            Collections.sort(list);
            for (String st : list) {
                bw.write(st + '\n');
            }
            bw.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void words_pagesAndLinks(String filename) {
        try {
            System.out.println("WORDS_PAGES AND LINKS");
            File f = new File("most_count_sorted_" + filename + ".txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"));
            String line;
            HashMap<String, Integer> listWord = new HashMap<>();
            int nb = 0;

            while ((line = br.readLine()) != null) {
                listWord.put(line, nb);
                nb++;
            }
            br.close();

            HashMap<Integer, Integer>[] map = new HashMap[nb];
            for (int i = 0; i < nb; i++) {
                map[i] = new HashMap<>();
            }

            HashMap<String, Integer> title = new HashMap<>();
            nb = 0;
            // System.out.println("TITRE");
            f = new File("title_" + filename + ".txt");
            br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"));
            boolean b = false;
            StringBuffer bf = new StringBuffer();

            while ((line = br.readLine()) != null) {
                title.put(normaliser(line), nb);
                nb++;
            }
            br.close();

            // System.out.println("MOT PAGE");
            f = new File(filename + ".xml");
            br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"));

            bf = new StringBuffer();
            nb = 0;
            b = false;
            CLIList cliLien = new CLIList();
            boolean compter = true;
            int x = 0;
            while ((line = br.readLine()) != null) {
                if (line.contains("<title>") && line.contains("</title>") && !line.contains(":")) {
                    compter = true;
                } else if (line.contains("<title>") && line.contains("</title>")) {
                    compter = false;
                }

                if (b) {
                    bf.append(line);
                }
                if (line.contains("<text") && compter) {
                    bf.append(line);
                    b = true;
                    x++;
                }
                if (line.contains("</text>") && compter) {
                    String s = normaliser(enleverBalises(bf.toString()));
                    StringBuilder stbf = new StringBuilder();
                    StringBuilder mot_lien = new StringBuilder();
                    boolean lien = false;
                    HashSet<Integer> set = new HashSet<>();
                    for (int pos = 0; pos < s.length(); pos++) {
                        char c = s.charAt(pos);
                        if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9') {
                            stbf.append(c);
                            if (lien)
                                mot_lien.append(c);
                        } else {
                            if (stbf.length() > 0) {
                                String w = stbf.toString();
                                if (listWord.containsKey(w)) {
                                    int id = listWord.get(w);
                                    if (id == 6217 && nb == 16478)
                                        System.out.println("ICI " + w);
                                    if (map[id].containsKey(nb)) {
                                        map[id].put(nb, map[id].get(nb) + 1);
                                    } else {
                                        map[id].put(nb, 1);
                                    }
                                }
                                stbf = new StringBuilder();
                            }

                            if (lien) {
                                if (c == ']' && s.length() - 1 > pos && s.charAt(pos + 1) == ']') {
                                    lien = false;
                                    String lie;
                                    String[] tab = mot_lien.toString().split("\\|");
                                    if (tab.length == 1) {
                                        lie = tab[0];
                                    } else if (tab.length == 2) {
                                        lie = tab[1];
                                    } else {
                                        mot_lien = new StringBuilder();
                                        continue;
                                    }
                                    if (title.containsKey(lie) && !set.contains(title.get(lie))) {
                                        cliLien.add(1, nb, title.get(lie));
                                        set.add(title.get(lie));
                                    }
                                    mot_lien = new StringBuilder();
                                } else if (c == ']') {
                                    mot_lien = new StringBuilder();
                                    lien = false;
                                } else if (c != '[') {
                                    mot_lien.append(c);
                                }
                            } else if (c == '[' && s.length() - 1 > pos && s.charAt(pos + 1) == '[') {
                                lien = true;
                            }
                        }
                    }

                    nb++;
                    if (nb % 5000 == 0)
                        System.out.println(nb);
                    bf = new StringBuffer();
                    b = false;
                }
            }
            if (b)
                nb++;
            br.close();

            cliLien.end(nb);
            System.out.println("nbr --->" + x);
            CLIList clilist = new CLIList();

            for (int i = 0; i < map.length; i++) {
                for (Integer j : map[i].keySet()) {
                    clilist.add(map[i].get(j), i, j);
                }
                map[i] = null;
            }
            clilist.end(map.length);
            // System.out.println("aaa " + map.length);

            clilist.write(filename);
            cliLien.write("lien_" + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CLIArray tri(String filename, int freqMin) {
        System.out.println("PAGE RANK");

        CLIArray motPage = new CLIArray();
        motPage.read(filename);
        CLIArray pageRank = new CLIArray();
        pageRank.read("lien_" + filename);

        double v[] = new double[pageRank.L.length - 1];
        for (int i = 0; i < v.length; i++) {
            v[i] = 1.0 / v.length;
        }
        Vecteur Z = new Vecteur(v);

        pageRank.convertir();
        Vecteur rank = pageRank.variantePageRank(Z, 0.05);

        CLIList mot_page_sorted = new CLIList();

        LinkedList<Page> pages = new LinkedList<Page>();
        for (int i = 0; i < motPage.L.length - 1; i++) {
            for (int j = motPage.L[i]; j < motPage.L[i + 1]; j++) {
                if (motPage.C[j] >= freqMin) {
                    pages.add(new Page(motPage.C[j], rank.v[motPage.I[j]], motPage.I[j]));
                }
            }

            Collections.sort(pages);
            for (Page p : pages) {
                mot_page_sorted.add(p.frequence, i, p.page);
            }
            pages = new LinkedList<>();
        }

        mot_page_sorted.end(motPage.L.length - 1);

        return mot_page_sorted.toArray();
    }

    static String normaliser(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase();
    }

    static String enleverBalises(String text) {
        return text.replaceAll("\\<[^>]*>", "").replaceAll("\\s+", " ");
    }
}