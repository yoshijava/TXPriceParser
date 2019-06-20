package tbroker;

class Record {
    String tss;
    public String target;
    public double dp, op;

    public long getTS() {
        long h = (tss.charAt(0) - '0') * 10 + (tss.charAt(1) - '0');
        long m = (tss.charAt(3) - '0') * 10 + (tss.charAt(4) - '0');
        long s = (tss.charAt(6) - '0') * 10 + (tss.charAt(7) - '0');
        long v = 0;
        return ((h & 0xff) << 24) | ((m & 0xff) << 16) | ((s & 0xff) << 8) | v;
    }

    public boolean isValid() {
        return tss != null;
    }

    public String toString() {
        return target + " @ " + getTS() + "(" + tss + "), dp = " + dp + ", op = " + op;
    }
}