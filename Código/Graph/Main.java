import com.mongodb.*;
import java.net.UnknownHostException;
import java.io.*;
import java.util.ArrayList;

public class Main {

    private static Integer currentPercentage = -1;
    private static Integer count = 0;
    private static Long total;

    public static void main(String[] args) throws UnknownHostException {

        Tree tree = new Tree();

        MongoClient mongoClient = new MongoClient();
        DB db = mongoClient.getDB( "STF" );
        DBCollection acordaos = db.getCollection("acordaos");

        total = acordaos.count();

        DBCursor cursor = acordaos.find();

        while(cursor.hasNext()) {
           String id = cursor.next().get("id").toString();
           tree.add(id);
        }

        // tree.print();

        DBCollection links = db.getCollection("links");
        links.drop();

        cursor = acordaos.find(new BasicDBObject("index", new BasicDBObject("$lte", 25000)));
        System.out.println("\n");
        while(cursor.hasNext()) {

            printProgress();

            DBObject acordao = cursor.next();

            processFile(tree, acordaos, links, acordao);

        }

        cursor = acordaos.find(new BasicDBObject("index", new BasicDBObject("$gt", 25000)));
        while(cursor.hasNext()) {

            printProgress();

            DBObject acordao = cursor.next();

            processFile(tree, acordaos, links, acordao);

        }

        System.out.println("\n");


    }

    private static void printProgress() {
        count++;
        Integer newPercentage = (int)Math.floor(100 * count / total);
        if (newPercentage != currentPercentage) {
                System.out.print("\r |");
            for (int i = 1; i <= newPercentage; i++)
                System.out.print("=");
            for (int i = newPercentage + 1; i <= 100; i++)
                System.out.print(" ");
            System.out.print("| " + newPercentage + "%  ");

            currentPercentage = newPercentage;
        }
    }


    private static void processFile(Tree tree, DBCollection acordaos, DBCollection links, DBObject acordao) {
        String fileName = acordao.get("file").toString();

        try {
            FileReader fileReader = new FileReader("../Acordaos/STF/" + fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            Finder finder = new Finder(bufferedReader, tree);
            finder.find();

            ArrayList<String> ids = finder.getLinks();

            ArrayList<BasicDBObject> quotes = new ArrayList<BasicDBObject>();

            for (String id : ids) {
                BasicDBObject query = new BasicDBObject("id", id);
                BasicDBObject quote = (BasicDBObject)acordaos.findOne(query);
                quotes.add(quote);
            }
            BasicDBObject link = new BasicDBObject("_id", acordao.get("_id"));
            link.append("id", acordao.get("id"));
            link.append("uf", acordao.get("uf"));
            link.append("relator", acordao.get("relator"));
            link.append("date", acordao.get("date"));
            link.append("file", acordao.get("file"));
            link.append("quotesSomething", acordao.get("quotesSomething"));

            link.append("quotes", quotes);
            links.insert(link);

            bufferedReader.close();
        }
        catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        }
        catch (IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
            ex.printStackTrace();
        }

    }

}
