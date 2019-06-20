package tbroker;
import java.net.URL;
import java.util.Iterator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class CapitalParser {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // URL
        URL url = new URL("https://info512ah.taifex.com.tw/Future/FusaQuote_Norl.aspx");
        // Create the Document Object
        Document doc = Jsoup.parse(url, 3000);
        // Get first table
        Iterator<Element> tableIterator = doc.select("table").iterator();
        while(tableIterator.hasNext()) {
            Element table = tableIterator.next().select("table").first();
            // Get td Iterator
            Iterator<Element> ite = table.select("td").iterator();
            // Print content
            int cnt = 0;
            String dealString = "成交價";
            String price = "";
            boolean readyToFetch = false;
            while (ite.hasNext()) {
                cnt++;
                String text = ite.next().text();
                // System.out.println("Value " + cnt + ": " + text);
                if(!readyToFetch && text.contains("收盤") == false) {
                    readyToFetch = false;
                    continue;
                }
                else {
                    readyToFetch = true;
                }
                if(text.contains(dealString)) {
                    price = ite.next().text();
                    break;
                }
            }
            System.out.println(price);
        }
    }
}