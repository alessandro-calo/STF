import com.mongodb.*;
import java.net.UnknownHostException;
import java.io.*;
import java.util.Set;
import java.util.Date;
import java.util.ArrayList;

public class Main {

    private static Integer currentPercentage = -1;
    private static Integer count = 0;
    private static Long total;

    public static void main (String[] args) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient();
        DB db = mongoClient.getDB( "STF" );
        DBCollection collection = db.getCollection("acordaos");
        collection.drop();

        System.out.println("\n");

        Integer index = 1;

        Integer numFiles = 5182;
        total = (numFiles).longValue()*10;
        for (Integer p = 1; p <= numFiles; p++) {
            for (Integer j = 1; j <= 10; j++) {
                String fileName = String.format("../Acordaos/STF/P%04dJ%02d", p, j);
                process(fileName, collection, index);
                index++;
            }
        }

        System.out.println("\n");

    }

    private static void process (String fileName, DBCollection collection, Integer index) {
        printProgress();

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            Parser parser = new Parser();
            parser.parse(bufferedReader, fileName);
            String id = parser.getID();
            String uf = parser.getUF();
            String relator = parser.getRelator();
            Date date = parser.getDate();
            ArrayList<String> tags = parser.getTags();
            Boolean quotesSomething = parser.getQuotesSomething();

            BasicDBObject doc = new BasicDBObject();
            doc.append("id", id);
            doc.append("uf", uf);
            doc.append("relator", relator);
            doc.append("date", date);
            doc.append("file", fileName.split("/")[3]);
            doc.append("index", index);
            doc.append("tags", tags);
            doc.append("quotesSomething", quotesSomething);
            collection.insert(doc);

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

    private static void printProgress() {
        count++;

        Integer newPercentage = (int)Math.floor(100 * count / total);
        if (newPercentage != currentPercentage) {
                System.out.print("\r    |");
            for (int i = 1; i <= newPercentage; i++)
                System.out.print("=");
            for (int i = newPercentage + 1; i <= 100; i++)
                System.out.print(" ");
            System.out.print("| " + newPercentage + "%  ");

            currentPercentage = newPercentage;
        }
    }

}
