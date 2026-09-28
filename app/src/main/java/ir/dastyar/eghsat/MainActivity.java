package ir.dastyar.eghsat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.widget.*;
import java.text.NumberFormat;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout list, root, summaryBox;
    Theme th;
    View fab;
    FrameLayout outer, drawer;
    View scrim;
    boolean drawerOpen = false;
    final int REQ = 90;

    int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }
    TextView tv(String s, float size, int color) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color);
        t.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL)); return t;
    }
    GradientDrawable rounded(int color, float radiusDp) { GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp(radiusDp));return g; }
    GradientDrawable stroke(int color,int border,float radiusDp){GradientDrawable g=rounded(color,radiusDp);g.setStroke(dp(1),border);return g;}
    String money(long n) { return NumberFormat.getInstance(Locale.US).format(n) + " تومان"; }

    @Override public void onCreate(Bundle b) {
        super.onCreate(b); th=Theme.get(this); applyBars(); build();
        if (android.os.Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ);
    }

    void applyBars(){ getWindow().setStatusBarColor(th.bg);getWindow().setNavigationBarColor(th.bg); }

    void build() {
        outer=new FrameLayout(this);outer.setBackgroundColor(th.bg);
        root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(16),dp(16),dp(16),dp(12));

        // Navigation lives in one compact menu button.
        LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
        TextView menu=tv("☰",23,th.text);menu.setGravity(Gravity.CENTER);menu.setBackground(rounded(th.card,18));
        menu.setContentDescription("منوی برنامه");menu.setOnClickListener(v->openDrawer());
        top.addView(menu,new LinearLayout.LayoutParams(dp(48),dp(44)));
        LinearLayout.LayoutParams topLp=new LinearLayout.LayoutParams(-1,-2);topLp.setMargins(0,0,0,dp(12));root.addView(top,topLp);

        summaryBox=new LinearLayout(this);summaryBox.setOrientation(LinearLayout.VERTICAL);summaryBox.setPadding(dp(16),dp(16),dp(16),dp(16));summaryBox.setBackground(rounded(th.primary,24));
        LinearLayout.LayoutParams sLp=new LinearLayout.LayoutParams(-1,-2);sLp.setMargins(0,0,0,dp(18));root.addView(summaryBox,sLp);
        TextView h=tv("اقساط من",18,th.text);h.setTypeface(null,1);LinearLayout.LayoutParams hLp=new LinearLayout.LayoutParams(-1,-2);hLp.setMargins(dp(2),0,dp(2),dp(8));root.addView(h,hLp);

        ScrollView sv=new ScrollView(this);sv.setClipToPadding(false);list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);list.setPadding(0,0,0,dp(76));sv.addView(list);root.addView(sv,new LinearLayout.LayoutParams(-1,0,1));
        outer.addView(root,new FrameLayout.LayoutParams(-1,-1));

        TextView fabBtn=tv("＋  تعریف اقساط",15,th.primaryText);fabBtn.setGravity(Gravity.CENTER);fabBtn.setTypeface(null,1);fabBtn.setBackground(rounded(th.primary,30));fabBtn.setElevation(dp(8));fabBtn.setOnClickListener(v->startActivity(new Intent(this,AddInstallmentActivity.class)));
        FrameLayout.LayoutParams fp=new FrameLayout.LayoutParams(dp(142),dp(58));fp.gravity=Gravity.BOTTOM|Gravity.RIGHT;fp.setMargins(0,0,dp(20),dp(20));outer.addView(fabBtn,fp);fab=fabBtn;

        outer.setOnApplyWindowInsetsListener((v,insets)->{Insets sb=insets.getInsets(WindowInsets.Type.systemBars());root.setPadding(dp(16),sb.top+dp(12),dp(16),dp(12));FrameLayout.LayoutParams flp=(FrameLayout.LayoutParams)fab.getLayoutParams();flp.bottomMargin=sb.bottom+dp(20);fab.setLayoutParams(flp);return insets;});
        setContentView(outer);refresh();
    }

    @Override protected void onResume(){super.onResume();if(list!=null)refresh();}
    @Override public void onBackPressed(){if(drawerOpen){closeDrawer();return;}super.onBackPressed();}

    void openDrawer(){
        if(drawerOpen)return;drawerOpen=true;
        scrim=new View(this);scrim.setBackgroundColor(0x99000000);scrim.setOnClickListener(v->closeDrawer());
        outer.addView(scrim,new FrameLayout.LayoutParams(-1,-1));scrim.bringToFront();
        drawer=new FrameLayout(this);drawer.setBackground(rounded(th.bg,0));drawer.setElevation(dp(18));
        int width=(int)(getResources().getDisplayMetrics().widthPixels*0.58f);
        FrameLayout.LayoutParams dpLp=new FrameLayout.LayoutParams(width,-1);dpLp.gravity=Gravity.LEFT;outer.addView(drawer,dpLp);drawer.bringToFront();
        buildDrawer();drawer.setTranslationX(-width);drawer.animate().translationX(0).setDuration(220).start();
    }

    void closeDrawer(){if(!drawerOpen)return;drawerOpen=false;if(drawer!=null){int w=drawer.getWidth();drawer.animate().translationX(-w).setDuration(180).withEndAction(()->{outer.removeView(drawer);outer.removeView(scrim);drawer=null;scrim=null;}).start();}}

    TextView menuItem(String icon,String label){
        TextView t=tv(icon+"   "+label,15,th.text);t.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);t.setPadding(dp(18),0,dp(18),0);t.setBackground(stroke(th.card,th.border,18));return t;
    }
    void addMenuItem(LinearLayout box,String icon,String label,View.OnClickListener click){TextView t=menuItem(icon,label);t.setOnClickListener(v->{closeDrawer();click.onClick(v);});LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(52));lp.setMargins(0,0,0,dp(10));box.addView(t,lp);}

    void buildDrawer(){
        LinearLayout panel=new LinearLayout(this);panel.setOrientation(LinearLayout.VERTICAL);panel.setPadding(dp(14),dp(28),dp(14),dp(18));
        int[] drawerGradient = th.mode == Theme.ORANGE
                ? new int[]{0xFF5A2410,0xFF8E3F16,0xFFC65D1C,0xFFFFB74D}
                : new int[]{Color.rgb(7,31,63),Color.rgb(13,63,117),Color.rgb(21,101,192),Color.rgb(100,181,246)};
        GradientDrawable headBg=new GradientDrawable(GradientDrawable.Orientation.TL_BR,drawerGradient);headBg.setCornerRadius(dp(26));
        LinearLayout head=new LinearLayout(this);head.setOrientation(LinearLayout.VERTICAL);head.setPadding(dp(18),dp(18),dp(18),dp(18));head.setBackground(headBg);
        TextView ht=tv("منوی اقساط",20,Color.WHITE);ht.setTypeface(null,1);head.addView(ht);
        TextView hs=tv("مدیریت، تقویم، محاسبه و ظاهر برنامه",12,0xE6FFFFFF);hs.setPadding(0,dp(5),0,0);head.addView(hs);
        LinearLayout shades=new LinearLayout(this);shades.setPadding(0,dp(14),0,0);
        int[] shadesPalette = th.mode == Theme.ORANGE
                ? new int[]{0xFF5A2410,0xFF7A3213,0xFF9B4317,0xFFC65D1C,0xFFE47D2C,0xFFFFA44A,0xFFFFD3A3}
                : new int[]{0xFF071F3F,0xFF0D3F75,0xFF155FAD,0xFF1976D2,0xFF42A5F5,0xFF90CAF9,0xFFE3F2FD};
        for(int c:shadesPalette){View v=new View(this);v.setBackground(rounded(c,4));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,dp(6),1);lp.setMargins(dp(2),0,dp(2),0);shades.addView(v,lp);}head.addView(shades);
        panel.addView(head,new LinearLayout.LayoutParams(-1,dp(142)));

        TextView sec=tv("ابزار",13,th.muted);sec.setTypeface(null,1);sec.setPadding(dp(4),dp(18),dp(4),dp(8));panel.addView(sec);
        addMenuItem(panel,"📋","داشبورد مدیریت اقساط",v->startActivity(new Intent(this,AllInstallmentsActivity.class)));
        addMenuItem(panel,"📅","تقویم اقساط",v->startActivity(new Intent(this,CalendarActivity.class)));
        addMenuItem(panel,"🧮","ماشین حساب اقساط",v->startActivity(new Intent(this,InstallmentCalculatorActivity.class)));
        addMenuItem(panel,"＋","تعریف قسط جدید",v->startActivity(new Intent(this,AddInstallmentActivity.class)));

        TextView themeTitle=tv("ظاهر برنامه",13,th.muted);themeTitle.setTypeface(null,1);themeTitle.setPadding(dp(4),dp(8),dp(4),dp(8));panel.addView(themeTitle);
        LinearLayout themes=new LinearLayout(this);themes.setOrientation(LinearLayout.HORIZONTAL);
        addThemeButton(themes,"☀","روشن",Theme.LIGHT);addThemeButton(themes,"☾","تاریک",Theme.DARK);addThemeButton(themes,"🔵","آبی",Theme.BLUE);addThemeButton(themes,"🍂","نارنجی",Theme.ORANGE);
        panel.addView(themes,new LinearLayout.LayoutParams(-1,dp(74)));
        drawer.addView(panel,new FrameLayout.LayoutParams(-1,-1));
    }

    void addThemeButton(LinearLayout box,String icon,String label,int mode){
        LinearLayout b=new LinearLayout(this);b.setOrientation(LinearLayout.VERTICAL);b.setGravity(Gravity.CENTER);b.setPadding(dp(4),dp(6),dp(4),dp(6));
        int base;
        int txt;
        int iconColor;
        if (mode == Theme.BLUE) {
            base = 0xFF0E3456; txt = Color.WHITE; iconColor = 0xFF64B5F6;
        } else if (mode == Theme.DARK) {
            base = 0xFF1E2938; txt = Color.WHITE; iconColor = Color.WHITE;
        } else if (mode == Theme.ORANGE) {
            base = 0xFFFFF0E1; txt = 0xFF7A3E16; iconColor = 0xFFC65D1C;
        } else {
            base = Color.WHITE; txt = th.text; iconColor = txt;
        }
        b.setBackground(stroke(base,mode==th.mode?th.primary:th.border,16));TextView i=tv(icon,20,iconColor);i.setGravity(Gravity.CENTER);b.addView(i);TextView l=tv(label,11,txt);l.setGravity(Gravity.CENTER);b.addView(l);
        b.setOnClickListener(v->{Theme.setMode(MainActivity.this,mode);th=Theme.get(MainActivity.this);applyBars();closeDrawer();recreate();});
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-1,1);lp.setMargins(dp(3),0,dp(3),0);box.addView(b,lp);
    }

    void summaryRow(String label,String value,boolean strong){LinearLayout r=new LinearLayout(this);r.setOrientation(LinearLayout.HORIZONTAL);TextView l=tv(label,14,th.primaryText);l.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));l.setAlpha(.92f);r.addView(l,new LinearLayout.LayoutParams(0,-2,1));TextView v=tv(value,strong?17:15,th.primaryText);v.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));r.addView(v,new LinearLayout.LayoutParams(-2,-2));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.topMargin=dp(6);summaryBox.addView(r,lp);}

    void refresh(){
        list.removeAllViews();List<Installment> all=Store.all(this);
        Calendar now=Calendar.getInstance();int[] todayJ=PersianDate.toJalali(now.get(Calendar.YEAR),now.get(Calendar.MONTH)+1,now.get(Calendar.DAY_OF_MONTH));
        Map<Long,Long> dueThisMonth=new LinkedHashMap<>();
        for(Installment x:all)if(!x.isFinished()){Calendar c=Calendar.getInstance();c.setTimeInMillis(x.nextDueMillis());int[] j=PersianDate.toJalali(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH));if(j[0]==todayJ[0]&&j[1]==todayJ[1])dueThisMonth.put(x.id,x.amount);}
        long paidThisMonth=0;for(PaymentLog.Event e:PaymentLog.all(this)){Calendar c=Calendar.getInstance();c.setTimeInMillis(e.timestamp);int[] j=PersianDate.toJalali(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH));if(j[0]==todayJ[0]&&j[1]==todayJ[1]){paidThisMonth+=e.amount;dueThisMonth.put(e.installmentId,e.amount);}}
        long totalThisMonth=0;for(long v:dueThisMonth.values())totalThisMonth+=v;long remainingThisMonth=Math.max(0,totalThisMonth-paidThisMonth);
        long totalDebt=0;int openCount=0;Installment next=null;for(Installment x:all)if(!x.isFinished()){totalDebt+=x.amount*(x.totalCount-x.paidCount);openCount++;if(next==null||x.nextDueMillis()<next.nextDueMillis())next=x;}
        summaryBox.removeAllViews();TextView head=tv("📆  خلاصه‌ی "+PersianDate.MONTH_NAMES[todayJ[1]-1],17,th.primaryText);head.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));summaryBox.addView(head);
        summaryRow("کل این ماه",money(totalThisMonth),false);summaryRow("پرداخت‌شده",money(paidThisMonth),false);summaryRow("باقی‌مانده این ماه",money(remainingThisMonth),true);summaryRow("مانده کل بدهی",money(totalDebt)+"  ("+openCount+" قسط فعال)",false);
        List<Installment> visible=new ArrayList<>();for(Installment x:all)if(x.isVisibleOnHome())visible.add(x);
        if(visible.isEmpty()){TextView e=tv(all.isEmpty()?"هنوز قسطی ثبت نکرده‌ای.\nاز منوی بالا یا دکمه + شروع کن.":"الان قسطی نزدیک سررسید نیست 👌",16,th.muted);e.setGravity(Gravity.CENTER);list.addView(e,new LinearLayout.LayoutParams(-1,dp(180)));return;}
        Collections.sort(visible,(a,b)->Long.compare(a.nextDueMillis(),b.nextDueMillis()));
        for(Installment x:visible)list.addView(CardBuilder.build(this,th,x,new CardBuilder.Actions(){public void onPay(Installment y){confirmPay(y);}public void onEdit(Installment y){startActivity(new Intent(MainActivity.this,AddInstallmentActivity.class).putExtra("id",y.id));}public void onDelete(Installment y){confirmDelete(y);}}));
    }

    void confirmPay(Installment x){int n=Math.min(x.paidCount+1,x.totalCount);new AlertDialog.Builder(this).setTitle("تأیید پرداخت قسط").setMessage("از پرداخت قسط "+n+" از "+x.totalCount+" برای «"+x.title+"» به مبلغ "+money(x.amount)+" مطمئنی؟").setPositiveButton("بله، پرداخت شد",(d,w)->{x.paidCount++;if(x.paidCount>=x.totalCount)x.active=false;Store.upsert(this,x);PaymentLog.record(this,x.id,x.amount);AlarmHelper.schedule(this,x);refresh();}).setNegativeButton("انصراف",null).show();}
    void confirmDelete(Installment x){new AlertDialog.Builder(this).setTitle("حذف قسط").setMessage("«"+x.title+"» حذف بشه؟ این کار قابل بازگشت نیست.").setPositiveButton("حذف",(d,w)->{AlarmHelper.cancel(this,x.id);Store.delete(this,x.id);PaymentLog.deleteForInstallment(this,x.id);refresh();}).setNegativeButton("انصراف",null).show();}
}
