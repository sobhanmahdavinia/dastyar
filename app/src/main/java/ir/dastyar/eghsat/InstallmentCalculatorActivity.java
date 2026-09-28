package ir.dastyar.eghsat;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.text.NumberFormat;
import java.util.Locale;

public class InstallmentCalculatorActivity extends Activity {
    Theme th;
    EditText loanAmount, installmentCount, installmentAmount;
    LinearLayout resultBox;

    int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }

    GradientDrawable rounded(int color, float radiusDp) {
        GradientDrawable g = new GradientDrawable(); g.setColor(color); g.setCornerRadius(dp(radiusDp)); return g;
    }
    GradientDrawable roundedStroke(int color, int strokeColor, float radiusDp, int strokeDp) {
        GradientDrawable g = new GradientDrawable(); g.setColor(color); g.setCornerRadius(dp(radiusDp)); g.setStroke(dp(strokeDp), strokeColor); return g;
    }
    GradientDrawable roundedStroke(int color, int strokeColor, float radiusDp) { return roundedStroke(color, strokeColor, radiusDp, 2); }

    TextView tv(String s, float size, int color) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color); t.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL)); return t;
    }
    TextView label(String s) { TextView t=tv(s,14,th.muted); t.setPadding(dp(2),dp(14),dp(2),dp(6)); return t; }

    EditText field(String hint, boolean integer) {
        EditText e=new EditText(this); e.setHint(hint); e.setTextSize(17); e.setSingleLine(true);
        e.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if(integer) e.setInputType(InputType.TYPE_CLASS_NUMBER);
        e.setFilters(new InputFilter[]{new InputFilter.LengthFilter(18)});
        e.setTextColor(th.text); e.setHintTextColor(th.muted); e.setTypeface(Typeface.create("sans-serif",Typeface.NORMAL));
        e.setBackground(roundedStroke(th.card, th.border, 18, 2)); e.setPadding(dp(16),dp(12),dp(16),dp(12));
        return e;
    }

    String digits(String raw) {
        if(raw==null) return "";
        return raw.replace('۰','0').replace('۱','1').replace('۲','2').replace('۳','3').replace('۴','4').replace('۵','5').replace('۶','6').replace('۷','7').replace('۸','8').replace('۹','9').replaceAll("[^0-9]", "");
    }
    long parseLong(EditText e) {
        String d=digits(e.getText().toString());
        if(d.isEmpty() || d.length()>15) throw new IllegalArgumentException();
        return Long.parseLong(d);
    }
    void formatOnBlur(EditText e) {
        e.setOnFocusChangeListener((v,hasFocus)->{
            if(!hasFocus) {
                String d=digits(e.getText().toString());
                if(!d.isEmpty()) { String f=NumberFormat.getInstance(Locale.US).format(Long.parseLong(d)); e.setText(f); }
            }
        });
    }
    String money(long n) { return NumberFormat.getInstance(Locale.US).format(n) + " تومان"; }

    @Override public void onCreate(Bundle b) {
        super.onCreate(b); th=Theme.get(this); getWindow().setStatusBarColor(th.bg); getWindow().setNavigationBarColor(th.bg);
        ScrollView scroll=new ScrollView(this); scroll.setBackgroundColor(th.bg);
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(20),dp(18),dp(20),dp(24));
        LinearLayout top=new LinearLayout(this); top.setGravity(Gravity.CENTER_VERTICAL);
        TextView back=tv("←",22,th.text); back.setGravity(Gravity.CENTER); back.setOnClickListener(v->finish()); top.addView(back,new LinearLayout.LayoutParams(dp(44),dp(44)));
        TextView title=tv("ماشین حساب اقساط",22,th.text); title.setTypeface(null,1); LinearLayout.LayoutParams tl=new LinearLayout.LayoutParams(0,-2,1); tl.setMarginStart(dp(8)); top.addView(title,tl); root.addView(top);
        TextView intro=tv("مبلغ وام، تعداد اقساط و مبلغ هر قسط را وارد کنید.",14,th.muted); intro.setPadding(dp(2),dp(8),dp(2),dp(6)); root.addView(intro);

        root.addView(label("مبلغ وام (تومان)")); loanAmount=field("مثلاً 100,000,000",false); formatOnBlur(loanAmount); root.addView(loanAmount);
        root.addView(label("تعداد اقساط")); installmentCount=field("مثلاً 24",true); root.addView(installmentCount);
        root.addView(label("مبلغ هر قسط (تومان)")); installmentAmount=field("مثلاً 10,000,000",false); formatOnBlur(installmentAmount); root.addView(installmentAmount);

        TextView calc=tv("محاسبه",16,th.primaryText); calc.setGravity(Gravity.CENTER); calc.setTypeface(null,1); calc.setBackground(rounded(th.primary,18)); calc.setOnClickListener(v->calculate());
        LinearLayout.LayoutParams clp=new LinearLayout.LayoutParams(-1,dp(52)); clp.setMargins(0,dp(20),0,dp(16)); root.addView(calc,clp);
        resultBox=new LinearLayout(this); resultBox.setOrientation(LinearLayout.VERTICAL); resultBox.setPadding(dp(16),dp(16),dp(16),dp(16)); resultBox.setBackground(rounded(th.card,20)); resultBox.setVisibility(View.GONE); root.addView(resultBox);
        scroll.addView(root); setContentView(scroll);
    }

    void resultRow(String label,String value,boolean strong){ LinearLayout row=new LinearLayout(this); row.setOrientation(LinearLayout.HORIZONTAL); TextView l=tv(label,14,th.muted); TextView v=tv(value,strong?17:15,th.text); if(strong)v.setTypeface(null,1); row.addView(l,new LinearLayout.LayoutParams(0,-2,1)); row.addView(v,new LinearLayout.LayoutParams(-2,-2)); LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2); lp.setMargins(0,0,0,dp(10)); resultBox.addView(row,lp); }

    void calculate() {
        try {
            long loan=parseLong(loanAmount), count=parseLong(installmentCount), each=parseLong(installmentAmount);
            if(loan<=0 || count<=0 || each<=0 || count>10000) throw new IllegalArgumentException();
            // Per the requested rule, a single installment may not be smaller than the loan amount,
            // and the total repayment must never be smaller than the original loan.
            if(each<loan) throw new IllegalArgumentException();
            long total=Math.multiplyExact(count,each);
            if(total<loan) throw new IllegalArgumentException();
            long profit=total-loan; double profitPercent=(profit*100.0)/loan;
            resultBox.removeAllViews(); TextView head=tv("نتیجه محاسبه",17,th.text); head.setTypeface(null,1); head.setPadding(0,0,0,dp(12)); resultBox.addView(head);
            resultRow("مبلغ وام",money(loan),false); resultRow("کل مبلغ بازپرداخت",money(total),true); resultRow("مبلغ سود / اضافه پرداخت",money(profit),true);
            String pct=String.format(Locale.US,"%.2f٪",profitPercent); TextView percent=tv("درصد سود نسبت به مبلغ وام: "+pct,18,th.primary); percent.setTypeface(null,1); percent.setGravity(Gravity.CENTER); percent.setPadding(dp(10),dp(14),dp(10),dp(14)); percent.setBackground(rounded(Color.argb(31,Color.red(th.primary),Color.green(th.primary),Color.blue(th.primary)),18)); resultBox.addView(percent);
            resultBox.setVisibility(View.VISIBLE);
        } catch(Exception ex) { resultBox.setVisibility(View.GONE); Toast.makeText(this,"لطفا مقادیر صحیح را وارد نمائید",Toast.LENGTH_SHORT).show(); }
    }
}
