package tbroker;

import java.util.*;
import java.text.*;

public class TxParser {
    public static void main(String[] args) throws Exception {
        String sym = "TX07";
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
        // Tick t = new Tick(d, 0, r.dp);
        // return t;
        System.out.println(d + ", " + r.dp);
    }
}