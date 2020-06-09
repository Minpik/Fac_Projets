public class Page implements Comparable<Page> {
    
    double frequence;
    double pageRank;
    int page;

    public Page(double f, double p, int pa) {
        frequence = f;
        pageRank = p;
        page = pa;
    }

    @Override
    public int compareTo(Page p) {
        if (pageRank == p.pageRank)
            return 0;
        if (p.pageRank > pageRank)
            return 1;
        return -1;
    }
}