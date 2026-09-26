package ir.dastyar.eghsat;

import android.app.*;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.*;
import java.text.NumberFormat;
import java.util.*;

public class AddInstallmentActivity extends Activity {
    EditText title, amount, count, day;
    DatePicker date;
    long editId=-1;

    int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+0.5f);}
    TextView label(String s){TextView t=new TextView(this);t.setText(s);t.setTextSize(15);t.setTextColor(Color.DKGRAY);t.setPadding(0,dp(10),0,dp(4));return t;}
    EditText field(String hint){EditText e=new EditText(this);e.setHint(hint);e.setTextSize(16);e.setSingleLine(true);return e;}

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        editId=getIntent().getLongExtra("id",-1);
        LinearLayout r=new LinearLayout(this);r.setOrientation(LinearLayout.VERTICAL);r.setPadding(dp(20),dp(14),dp(20),dp(20));
        TextView h=label(editId>0?"ویرایش قسط":"قسط جدید");h.setTextSize(24);h.setTypeface(null,1);r.addView(h);

        r.addView(label("عنوان قسط")); title=field("مثلاً وام بانک");r.addView(title);
        r.addView(label("مبلغ هر قسط (تومان)")); amount=field("مثلاً 5000000");amount.setInputType(2);r.addView(amount);
        r.addView(label("تعداد کل اقساط")); count=field("مثلاً 24");count.setInputType(2);r.addView(count);
        r.addView(label("روز سررسید ماهانه")); day=field("مثلاً 10");day.setInputType(2);r.addView(day);
        r.addView(label("تاریخ اولین سررسید"));
        date=new DatePicker(this); date.setCalendarViewShown(false);r.addView(date,new LinearLayout.LayoutParams(-1,dp(150)));

        Button save=new Button(this);save.setText("ذخیره قسط");save.setOnClickListener(v->save());r.addView(save);
        setContentView(r);
        if(editId>0) load();
    }

    void load(){
        Installment x=Store.find(this,editId); if(x==null)return;
        title.setText(x.title);amount.setText(String.valueOf(x.amount));count.setText(String.valueOf(x.totalCount));day.setText(String.valueOf(x.dueDay));
        Calendar c=Calendar.getInstance();c.setTimeInMillis(x.firstDueMillis);
        date.updateDate(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
    }

    void save(){
        try{
            String t=title.getText().toString().trim();
            long a=Long.parseLong(amount.getText().toString().trim());
            int c=Integer.parseInt(count.getText().toString().trim());
            int d=Integer.parseInt(day.getText().toString().trim());
            if(t.isEmpty()||a<=0||c<=0||d<1||d>31) throw new Exception();
            Installment x=editId>0?Store.find(this,editId):new Installment();
            if(x==null)x=new Installment();
            x.id=editId>0?editId:System.currentTimeMillis();
            x.title=t;x.amount=a;x.totalCount=c;x.dueDay=d;
            Calendar cal=Calendar.getInstance();cal.set(date.getYear(),date.getMonth(),date.getDayOfMonth(),9,0,0);
            x.firstDueMillis=cal.getTimeInMillis();x.active=true;
            Store.upsert(this,x);AlarmHelper.schedule(this,x);finish();
        }catch(Exception e){Toast.makeText(this,"اطلاعات را درست وارد کن.",Toast.LENGTH_SHORT).show();}
    }
}
