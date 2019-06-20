package tbroker;

import java.util.*;
import java.text.*;
import org.json.*;

public class TxParser {
    public JSONObject getTxPrice(String sym) {
        Record r = new Record();
        Poll poll = new Poll();
        String type = sym.substring(0, 2).toLowerCase();
        Date now = new Date();
        if (Util.format(now, "MM").equals("12"))
            now = Util.addYear(now, 1);
        String tar = type + Util.format(now, "yyyy") + sym.substring(2, 4);
        System.out.println("tar = " + tar);
        if (type.equals("tx")) {
            poll.pollTx(r, tar);
        } else {
            poll.pollTe(r, tar);
        }
        long ts = r.getTS();
        int hh = (int) ((ts >> 24) & 0xff);
        int mm = (int) ((ts >> 16) & 0xff);
        int ss = (int) ((ts >> 8) & 0xff);
        now = new Date();
        String hhmmss = String.format("%2d:%2d:%2d", hh, mm, ss);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String yyyyMMdd = sdf.format(now);
        Date d = Util.parseL(yyyyMMdd + " " + hhmmss);
        if (now.getHours() == 0 && hh == 23) {
            d = Util.addDay(d, -1);
        }
        /*
            {"ret":"OK","a":"-1","b":"-1","v":"10668.00","ts":"1561039297","o":"10602.00"}
        */
        JSONObject jObj = new JSONObject();
        jObj.put("ret", "OK");
        jObj.put("a", "-1");
        jObj.put("b", "-1");
        jObj.put("v", String.valueOf(r.dp));
        jObj.put("ts", String.valueOf(d.getTime()/1000)); // seconds from 1970
        jObj.put("o", String.valueOf(r.op));
        return jObj;
    }

    public static void main(String[] args) throws Exception {
        JSONObject obj = new TxParser().getTxPrice("TX07");
        System.out.println(obj);
    }
}