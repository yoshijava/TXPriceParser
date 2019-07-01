package tbroker;

import java.security.cert.X509Certificate;
import java.util.*;

import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

class Poll {

    public Record pollTe(Record r, String target) {
        return poll(r, target, "電子期");
    }

    public Record pollTx(Record r, String target) {
        return poll(r, target, "臺指期");
    }

    private SSLSocketFactory socketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Throwable e) {
            String message = "Failed to create a SSL socket factory";
            throw new RuntimeException(message, e);
        }
    }

    Record poll(Record r, String target, String key) {
        r.target = target;
        target = target.substring(2);
        String url = "http://info512.taifex.com.tw/Future/FusaQuote_Norl.aspx";
        Date now = new Date();
        if (now.getTime() > Util.parseL(Util.format(now) + " 15:00:00").getTime()
                || now.getTime() < Util.parseL(Util.format(now) + " 5:00:00").getTime()) {
            url = "http://info512ah.taifex.com.tw/Future/FusaQuote_Norl.aspx";
        }
        while (true) {
            try {
                Document doc = Jsoup.connect(url).sslSocketFactory(socketFactory()).get();
                Element table = null;
                Elements rows = null;
                int tableIdx = 0;
                String title = "";
                do {
                    table = doc.select("table").get(tableIdx++); // select the first table.
                    rows = table.select("tr");
                    title = rows.get(0).text().trim();
                } while( title.startsWith("商品") == false);
                String[] headline = title.trim().split("\\s");
                int dealPriIdx = -1;
                int openPriIdx = -1;
                int timeIdx = -1;
                for(int i=0; i<headline.length; i++) {
                    String s = headline[i];
                    if(s.equals("成交價")) {
                        dealPriIdx = i;
                    }
                    else if(s.equals("開盤")) {
                        openPriIdx = i;
                    }
                    else if(s.equals("時間")) {
                        timeIdx = i;
                    }
                }
                for (int i = 0; i < rows.size(); i++) {
                    List<Element> tds = (List<Element>) rows.get(i).select("td");
                    Element e = tds.get(0);
                    String n = e.text();
                    if (n == null) continue;
                    if (n.contains("小" + key)) continue;
                    if (!n.contains(key + target.substring(4, 6) + target.substring(3, 4))) {
                        continue;
                    }
                    r.target = "tx" + target;
                    r.dp =
                            Double.parseDouble(
                                    tds.get(dealPriIdx).text().replaceAll(",", ""));
                    r.op =
                            Double.parseDouble(
                                    tds.get(openPriIdx).text().replaceAll(",", ""));
                    r.tss = tds.get(timeIdx).text();
                    System.out.println(r);
                    return r;
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
