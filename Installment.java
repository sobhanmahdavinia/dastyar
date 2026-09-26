package ir.dastyar.eghsat;

import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Installment {
    public long id;
    public String title = "";
    public long amount;
    public int totalCount = 1;
    public int paidCount = 0;
    public int dueDay = 1;
    public long firstDueMillis;
    public boolean active = true;

    public JSONObject toJson() throws Exception {
        JSONObject o = new JSONObject();
        o.put("id", id);
        o.put("title", title);
        o.put("amount", amount);
        o.put("totalCount", totalCount);
        o.put("paidCount", paidCount);
        o.put("dueDay", dueDay);
        o.put("firstDueMillis", firstDueMillis);
        o.put("active", active);
        return o;
    }

    public static Installment fromJson(JSONObject o) {
        Installment x = new Installment();
        x.id = o.optLong("id");
        x.title = o.optString("title");
        x.amount = o.optLong("amount");
        x.totalCount = o.optInt("totalCount", 1);
        x.paidCount = o.optInt("paidCount", 0);
        x.dueDay = o.optInt("dueDay", 1);
        x.firstDueMillis = o.optLong("firstDueMillis");
        x.active = o.optBoolean("active", true);
        return x;
    }

    public String dueText() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(firstDueMillis);
        return new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(c.getTime());
    }

    public long nextDueMillis() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(firstDueMillis);
        c.add(Calendar.MONTH, paidCount);
        int max = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        c.set(Calendar.DAY_OF_MONTH, Math.min(dueDay, max));
        return c.getTimeInMillis();
    }

    public boolean isFinished() { return paidCount >= totalCount || !active; }
}
