import java.util.ArrayList;

public class Acordao {

    public ArrayList<Acordao> quotes = new ArrayList<Acordao>();
    public ArrayList<Acordao> isQuotedBy = new ArrayList<Acordao>();

    private String id;
    private String relator;
    private String date;
    private String file;
    private ArrayList<String> quotesIDs;

    public Double pageRank;
    public Double tempPageRank;

    public Acordao(String id, String relator, String date, String file, ArrayList<String> quotesIDs, Long numAcordaos ) {
        this.id        = id;
        this.relator   = relator;
        this.date      = date;
        this.file      = file;
        this.quotesIDs = quotesIDs;
        this.pageRank  = 1/numAcordaos.doubleValue();
    }

    public ArrayList<String> getQuotes() {
        return this.quotesIDs;
    }

    public String getID() {
        return this.id;
    }

    public String getFile() {
        return this.file;
    }

    public String toString() {
        String ret = "[ " + id + "\t(" + file + ")\n\tquotes: [";
        for (Acordao quotedAcordao : quotes) {
            ret = ret + quotedAcordao.getID() + ", ";
        }
        ret = ret + "],\n\tis quoted by: [";
        for (Acordao quotedAcordao : isQuotedBy) {
            ret = ret + quotedAcordao.getID() + ", ";
        }
        ret = ret + "]\n]";
        return ret;
    }


}
