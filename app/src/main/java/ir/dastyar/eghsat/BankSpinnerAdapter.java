package ir.dastyar.eghsat;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BankSpinnerAdapter extends ArrayAdapter<Banks.Bank> {
    private final Activity ctx;
    private final Theme th;

    public BankSpinnerAdapter(Activity ctx, Theme th) { super(ctx, 0, Banks.ALL); this.ctx=ctx; this.th=th; }
    int dp(float v){return (int)(v*ctx.getResources().getDisplayMetrics().density+0.5f);}

    private View row(int position, ViewGroup parent, boolean dropdown) {
        Banks.Bank bank=getItem(position);
        LinearLayout row=new LinearLayout(ctx); row.setOrientation(LinearLayout.HORIZONTAL); row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(12),dp(dropdown?10:7),dp(12),dp(dropdown?10:7));
        if(dropdown) row.setBackground(rounded(th.card,16));

        LinearLayout logo=new LinearLayout(ctx); logo.setGravity(Gravity.CENTER); logo.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable bg=new GradientDrawable(); bg.setShape(GradientDrawable.OVAL); bg.setColor(bank!=null?bank.color:Banks.NONE.color); logo.setBackground(bg);
        TextView mark=new TextView(ctx); mark.setText(bank==null||bank.id.isEmpty()?"–":Banks.initials(bank.name)); mark.setTextColor(Color.WHITE); mark.setTextSize(dropdown?12:11); mark.setTypeface(Typeface.DEFAULT,Typeface.BOLD); mark.setGravity(Gravity.CENTER); logo.addView(mark,new LinearLayout.LayoutParams(-1,-1));
        LinearLayout.LayoutParams logoLp=new LinearLayout.LayoutParams(dp(dropdown?38:32),dp(dropdown?38:32)); logoLp.setMarginEnd(dp(10)); row.addView(logo,logoLp);

        TextView name=new TextView(ctx); name.setText(bank!=null?bank.name:""); name.setTextSize(dropdown?15:14); name.setTextColor(th.text); name.setTypeface(Typeface.DEFAULT,Typeface.BOLD); name.setGravity(Gravity.CENTER_VERTICAL); row.addView(name,new LinearLayout.LayoutParams(0,-2,1));
        if(dropdown){ TextView tag=new TextView(ctx); tag.setText("نشان بانک"); tag.setTextSize(10); tag.setTextColor(th.muted); tag.setGravity(Gravity.CENTER); row.addView(tag,new LinearLayout.LayoutParams(-2,-2)); }
        return row;
    }
    GradientDrawable rounded(int color,float radius){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp(radius));return g;}
    @Override public View getView(int position,View convertView,ViewGroup parent){return row(position,parent,false);}
    @Override public View getDropDownView(int position,View convertView,ViewGroup parent){return row(position,parent,true);}
}
