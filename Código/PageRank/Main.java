import com.mongodb.*;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {

    private static HashMap<String, Acordao> acordaos = new HashMap<String, Acordao>();

    public static void main(String[] args) throws UnknownHostException {

        buildMap();

        calculatePageRanks();
    }

    private static void buildMap() throws UnknownHostException {
        MongoClient mongoClient = new MongoClient();
        DB db = mongoClient.getDB( "STF" );
        DBCollection links = db.getCollection("links");

        Long numAcordaos = links.count();

        DBCursor cursor = links.find();

        while(cursor.hasNext()) {
            DBObject acordaoObject = cursor.next();

            try {
                String id          = acordaoObject.get("id").toString();
                String relator     = acordaoObject.get("relator").toString();
                String date        = acordaoObject.get("date").toString();
                String file        = acordaoObject.get("file").toString();
                BasicDBList quotes = (BasicDBList) acordaoObject.get("quotes");
                ArrayList<String> quotesIDs = dbListToArrayListOfIDs(quotes);


                Acordao acordao = new Acordao(id, relator, date, file, quotesIDs, numAcordaos);
                acordaos.put(id, acordao);


            } catch(NullPointerException ex) {
                System.out.println("NULL POINTER EXCEPTION, BITCH");
                ex.printStackTrace();
            }
        }
        for (Acordao acordao : acordaos.values()) {
            ArrayList<String> quotes = acordao.getQuotes();
            for (String quotedID : quotes) {
                Acordao quotedAcordao = acordaos.get(quotedID);
                acordao.quotes.add(quotedAcordao);
                quotedAcordao.isQuotedBy.add(acordao);
            }
        }

    }

    private static ArrayList<String> dbListToArrayListOfIDs(BasicDBList list) {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (Object object : list) {
            DBObject dbObject = (DBObject) object;
            String id = dbObject.get("id").toString();
            arrayList.add(id);
        }
        return arrayList;
    }

    private static Double euclidianDistance(HashMap<String, Double> pMap, HashMap<String, Double> qMap) {
        Double sum = 0.0;
        for (Map.Entry<String, Double> entry : pMap.entrySet()) {
            String id = entry.getKey();
            Double p = entry.getValue();
            Double q = qMap.get(id);
            Double term = Math.pow(p - q, 2);
            sum += term;
        }
        System.out.println(Math.sqrt(sum));
        return Math.sqrt(sum);
    }

    private static void calculatePageRanks() throws UnknownHostException {

        System.out.println("calculating pageranks now");

        MongoClient mongoClient = new MongoClient();
        DB db = mongoClient.getDB( "STF" );
        DBCollection links = db.getCollection("links");
        Long count = links.count();
        Double n = count.doubleValue();

        Double d = 0.85;

        HashMap<String, Double> pageRanks = new HashMap<String, Double>();
        for (Acordao acordao : acordaos.values()) {
            pageRanks.put(acordao.getID(), acordao.pageRank);
        }

        Double epsilon = 1.0E-18;

        while(true) {

            for (Acordao acordao : acordaos.values()) {
                Double sum = 0.0;
                for(Acordao quotingAcordao : acordao.isQuotedBy) {
                    Double pr = quotingAcordao.pageRank;
                    Integer l = quotingAcordao.quotes.size();
                    Double term = pr / l;
                    sum += term;
                }
                acordao.tempPageRank = ((1 - d) / n) + (d * sum);
            }

            HashMap<String, Double> newPageRanks = new HashMap<String, Double>();

            for (Acordao acordao : acordaos.values()) {
                acordao.pageRank = acordao.tempPageRank;
                newPageRanks.put(acordao.getID(), acordao.pageRank);
            }

            if (euclidianDistance(pageRanks, newPageRanks) < epsilon) break;

            pageRanks.clear();
            pageRanks = (HashMap<String, Double>) newPageRanks.clone();

        }

        DBCollection pageRanksCollection = db.getCollection("pageRanks");
        pageRanksCollection.drop();

        for (Acordao acordao : acordaos.values()) {
            BasicDBObject doc = new BasicDBObject();
            doc.append("id", acordao.getID());
            doc.append("file", acordao.getFile());
            doc.append("pageRank", acordao.pageRank);
            ArrayList<String> quotingAcordaos = new ArrayList<String>();
            for (Acordao quoting : acordao.isQuotedBy) {
                quotingAcordaos.add(quoting.getID());
            }
            doc.append("isQuotedBy", quotingAcordaos);
            pageRanksCollection.insert(doc);
        }

    }

}
