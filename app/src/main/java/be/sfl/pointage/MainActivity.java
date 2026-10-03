package be.sfl.pointage;

import android.app.*;
import android.os.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.content.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    LeatherView view;
    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.rgb(89,48,25));
        getWindow().setNavigationBarColor(Color.rgb(55,31,17));
        view=new LeatherView(this);
        setContentView(view);
    }

    public void playSignature(){
        try{
            ToneGenerator tg=new ToneGenerator(AudioManager.STREAM_NOTIFICATION,75);
            tg.startTone(ToneGenerator.TONE_PROP_ACK,180);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                tg.startTone(ToneGenerator.TONE_PROP_BEEP2,130);
                new Handler(Looper.getMainLooper()).postDelayed(tg::release,180);
            },210);
        }catch(Exception ignored){}
    }

    class LeatherView extends View {
        Paint p=new Paint(3);
        Paint stroke=new Paint(3);
        float den;
        RectF r=new RectF();
        boolean checked=false;
        int activeTab=0;
        final int cream=Color.rgb(250,244,235);
        final int paper=Color.rgb(255,250,244);
        final int brown=Color.rgb(103,57,29);
        final int brown2=Color.rgb(143,88,47);
        final int gold=Color.rgb(202,164,94);
        final int dark=Color.rgb(46,33,25);
        final int muted=Color.rgb(121,101,83);
        final int green=Color.rgb(31,122,85);

        LeatherView(Context c){super(c); den=getResources().getDisplayMetrics().density; setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
        float d(float v){return v*den;}
        void fill(int c){p.setStyle(Paint.Style.FILL);p.setColor(c);}
        void text(float sz,int c,boolean bold){p.setStyle(Paint.Style.FILL);p.setColor(c);p.setTextSize(d(sz));p.setTypeface(Typeface.create("sans",bold?Typeface.BOLD:Typeface.NORMAL));}
        void serif(float sz,int c,boolean bold){p.setStyle(Paint.Style.FILL);p.setColor(c);p.setTextSize(d(sz));p.setTypeface(Typeface.create("serif",bold?Typeface.BOLD:Typeface.NORMAL));}
        void round(Canvas c,float l,float t,float rr,float bb,float rad,int color){fill(color);r.set(d(l),d(t),d(rr),d(bb));c.drawRoundRect(r,d(rad),d(rad),p);}
        void stitch(Canvas c,float l,float t,float rr,float bb,float rad,int color){
            stroke.setStyle(Paint.Style.STROKE);stroke.setStrokeWidth(d(1));stroke.setColor(color);
            stroke.setPathEffect(new DashPathEffect(new float[]{d(5),d(4)},0));
            r.set(d(l),d(t),d(rr),d(bb));c.drawRoundRect(r,d(rad),d(rad),stroke);stroke.setPathEffect(null);
        }
        void line(Canvas c,float x1,float y1,float x2,float y2,int color){stroke.setStyle(Paint.Style.STROKE);stroke.setStrokeWidth(d(1));stroke.setColor(color);c.drawLine(d(x1),d(y1),d(x2),d(y2),stroke);}
        void circle(Canvas c,float x,float y,float rad,int color){fill(color);c.drawCircle(d(x),d(y),d(rad),p);}
        void label(Canvas c,String s,float x,float y){text(9,Color.rgb(141,91,52),true);c.drawText(s.toUpperCase(),d(x),d(y),p);}
        void card(Canvas c,float l,float t,float rr,float bb,float rad){
            round(c,l,t,rr,bb,rad,paper);
            stroke.setStyle(Paint.Style.STROKE);stroke.setStrokeWidth(d(.8f));stroke.setColor(Color.rgb(218,198,174));r.set(d(l),d(t),d(rr),d(bb));c.drawRoundRect(r,d(rad),d(rad),stroke);
            stitch(c,l+7,t+7,rr-7,bb-7,Math.max(6,rad-7),Color.argb(150,202,164,94));
        }

        @Override protected void onDraw(Canvas c){
            super.onDraw(c);
            float W=getWidth()/den, H=getHeight()/den;
            LinearGradient bg=new LinearGradient(0,0,0,getHeight(),Color.rgb(243,234,221),Color.rgb(229,214,194),Shader.TileMode.CLAMP);
            p.setShader(bg);c.drawRect(0,0,getWidth(),getHeight(),p);p.setShader(null);

            // Header leather
            LinearGradient lg=new LinearGradient(0,d(0),0,d(150),Color.rgb(91,48,25),Color.rgb(116,64,34),Shader.TileMode.CLAMP);
            p.setShader(lg);c.drawRect(0,0,getWidth(),d(152),p);p.setShader(null);
            stitch(c,14,12,W-14,142,18,Color.argb(150,229,191,125));
            serif(23,Color.rgb(242,218,180),false);c.drawText("ConsultPresence",d(58),d(54),p);
            text(9,Color.rgb(226,196,153),false);p.setLetterSpacing(.18f);c.drawText("SFL CONSULTING",d(112),d(72),p);p.setLetterSpacing(0);
            text(8,Color.rgb(231,204,167),false);p.setLetterSpacing(.14f);c.drawText("VOS PROJETS. VOTRE TEMPS. PLUS LOIN ENSEMBLE.",d(56),d(98),p);p.setLetterSpacing(0);
            // briefcase icon
            stroke.setStyle(Paint.Style.STROKE);stroke.setStrokeWidth(d(2));stroke.setColor(gold);
            r.set(d(20),d(34),d(48),d(56));c.drawRoundRect(r,d(4),d(4),stroke);line(c,28,34,28,29,gold);line(c,40,34,40,29,gold);line(c,28,29,40,29,gold);

            if(activeTab==0) drawToday(c,W,H);
            else if(activeTab==1) drawTimesheets(c,W,H);
            else drawProfile(c,W,H);
            drawNav(c,W,H);
        }

        void drawToday(Canvas c,float W,float H){
            float y=170;
            if(checked){
                round(c,16,y,W-16,y+58,16,Color.rgb(33,91,59));
                circle(c,42,y+29,16,Color.rgb(63,139,92));
                text(20,Color.WHITE,true);c.drawText("✓",d(34),d(y+36),p);
                text(12,Color.rgb(244,247,242),true);c.drawText("Pointage automatique effectué",d(66),d(y+25),p);
                text(9,Color.rgb(220,234,224),false);c.drawText("Votre présence a été enregistrée avec succès.",d(66),d(y+42),p);
                y+=72;
            }
            serif(30,dark,true);c.drawText("Aujourd’hui",d(16),d(y+30),p);
            text(10,muted,false);c.drawText("Mercredi 19 mars 2025",d(17),d(y+49),p);
            y+=65;

            card(c,16,y,W-16,y+215,20);
            label(c,"Client",72,y+28);
            serif(23,dark,true);c.drawText("NovaBank",d(72),d(y+55),p);
            text(8,muted,false);p.setLetterSpacing(.12f);c.drawText("BUILDING A STRONGER TOMORROW",d(72),d(y+72),p);p.setLetterSpacing(0);
            circle(c,45,y+43,21,Color.rgb(249,231,202));text(21,Color.rgb(72,93,55),false);c.drawText("♜",d(35),d(y+51),p);
            line(c,30,y+88,W-30,y+88,Color.rgb(232,219,202));
            label(c,"Projet",72,y+115);
            serif(21,dark,true);c.drawText("Core Banking",d(72),d(y+141),p);
            text(9,muted,false);c.drawText("Transformation digitale du système bancaire",d(72),d(y+159),p);
            circle(c,45,y+130,21,Color.rgb(249,231,202));text(18,Color.rgb(149,87,35),false);c.drawText("▣",d(36),d(y+137),p);
            round(c,30,y+171,W-30,y+203,13,Color.rgb(232,241,231));
            circle(c,53,y+187,12,Color.rgb(208,229,203));text(16,green,true);c.drawText("◎",d(45),d(y+193),p);
            text(9,Color.rgb(72,105,66),true);c.drawText("●  STATUT",d(75),d(y+184),p);
            text(12,Color.rgb(45,100,65),true);c.drawText(checked?"Présence reconnue automatiquement":"En attente de présence",d(75),d(y+198),p);

            y+=230;
            card(c,16,y,(W/2)-4,y+88,18); card(c,(W/2)+4,y,W-16,y+88,18);
            label(c,"Heure d’arrivée",56,y+25); serif(22,dark,true);c.drawText(checked?"08:32":"—",d(56),d(y+56),p);
            label(c,"Temps comptabilisé",(W/2)+44,y+25); serif(22,dark,true);c.drawText(checked?"1h 09":"0h 00",d((W/2)+44),d(y+56),p);

            y+=102;
            card(c,16,y,W-16,y+66,18);
            text(16,Color.rgb(150,91,39),false);c.drawText("♬",d(34),d(y+39),p);
            text(11,dark,true);c.drawText("Son de pointage automatique",d(62),d(y+27),p);
            text(8,muted,false);c.drawText("Un son unique vous confirme votre pointage.",d(62),d(y+43),p);
            text(9,Color.rgb(137,85,40),true);c.drawText("Activé",d(W-88),d(y+35),p);
            round(c,W-58,y+20,W-27,y+42,12,Color.rgb(163,103,57));circle(c,W-38,y+31,9,Color.rgb(255,247,237));

            y+=81;
            card(c,16,y,W-16,Math.min(H-88,y+190),18);
            serif(19,dark,true);c.drawText("Fil d’activité",d(34),d(y+31),p);
            text(9,Color.rgb(128,87,49),true);c.drawText("Voir tout",d(W-85),d(y+30),p);
            float yy=y+58;
            activity(c,yy,checked?"08:32":"08:30",checked?"Pointage automatique effectué":"Mission prévue",checked?"Présence reconnue chez NovaBank":"NovaBank – Genève",checked);
            activity(c,yy+48,"09:05","Réunion d’équipe","Suivi du sprint Core Banking",false);
            activity(c,yy+96,"10:15","Analyse des spécifications","Module Paiements",false);
        }

        void activity(Canvas c,float y,String time,String title,String sub,boolean ok){
            circle(c,42,y,6,ok?green:Color.rgb(166,157,146));
            text(9,muted,false);c.drawText(time,d(64),d(y+3),p);
            text(10,dark,true);c.drawText(title,d(118),d(y+1),p);
            text(8,muted,false);c.drawText(sub,d(118),d(y+16),p);
        }

        void drawTimesheets(Canvas c,float W,float H){
            float y=177;
            serif(29,dark,true);c.drawText("Timesheets",d(16),d(y+25),p);
            text(10,muted,false);c.drawText("Semaine 40 · synthèse avant soumission",d(17),d(y+45),p);
            y+=64;
            card(c,16,y,W-16,y+120,20);
            label(c,"Total semaine",34,y+28);serif(32,dark,true);c.drawText(checked?"24h39":"23h30",d(34),d(y+69),p);
            text(9,muted,false);c.drawText("Objectif 40h",d(34),d(y+92),p);
            round(c,34,y+100,W-34,y+108,4,Color.rgb(232,220,204));round(c,34,y+100,(W-34)*(checked?.62f:.59f),y+108,4,brown2);
            y+=137;
            String[] days={"Lun  8h00","Mar  7h30","Mer  8h00","Jeu  "+(checked?"1h09":"—"),"Ven  —"};
            for(String s:days){card(c,16,y,W-16,y+52,15);text(11,dark,true);c.drawText(s,d(34),d(y+31),p);y+=60;}
        }

        void drawProfile(Canvas c,float W,float H){
            float y=177;
            serif(29,dark,true);c.drawText("Profil",d(16),d(y+25),p);
            text(10,muted,false);c.drawText("Préférences, configuration et confidentialité",d(17),d(y+45),p);
            y+=64;
            card(c,16,y,W-16,y+92,20);
            circle(c,52,y+46,24,brown2);text(13,Color.WHITE,true);c.drawText("JD",d(43),d(y+51),p);
            text(12,dark,true);c.drawText("Julien Dupont",d(88),d(y+39),p);text(9,muted,false);c.drawText("Senior Consultant · SFL Consulting",d(88),d(y+57),p);
            y+=108;
            card(c,16,y,W-16,y+210,20);
            serif(17,dark,true);c.drawText("Administration",d(34),d(y+32),p);
            setupRow(c,y+58,"Société de consultance","SFL Consulting");
            setupRow(c,y+98,"Contact côté consultance","Sophie Martin");
            setupRow(c,y+138,"Consultant","Julien Dupont");
            setupRow(c,y+178,"Client","NovaBank");
            y+=226;
            card(c,16,y,W-16,y+98,20);
            text(11,dark,true);c.drawText("Vie privée par conception",d(34),d(y+31),p);
            text(8,muted,false);c.drawText("Aucun trajet GPS complet n’est conservé.",d(34),d(y+50),p);
            text(8,muted,false);c.drawText("Seuls les événements métier sont synchronisés.",d(34),d(y+67),p);
        }
        void setupRow(Canvas c,float y,String a,String b){text(10,dark,true);c.drawText(a,d(34),d(y),p);text(8,muted,false);c.drawText(b,d(34),d(y+15),p);text(16,Color.rgb(151,96,49),false);c.drawText("›",d(365),d(y+8),p);}

        void drawNav(Canvas c,float W,float H){
            float top=H-70;
            round(c,8,top,W-8,H-6,18,Color.rgb(77,42,23));stitch(c,15,top+7,W-15,H-13,12,Color.argb(150,215,173,101));
            String[] n={"⌂","▦","◎"};String[] lab={"Aujourd’hui","Timesheets","Profil"};
            for(int i=0;i<3;i++){
                float cx=W*(i+.5f)/3f;
                if(i==activeTab) round(c,cx-50,top+7,cx+50,H-13,13,Color.rgb(105,58,31));
                text(18,i==activeTab?Color.rgb(238,204,147):Color.rgb(207,177,139),false);c.drawText(n[i],d(cx-8),d(top+29),p);
                text(9,i==activeTab?Color.rgb(248,222,176):Color.rgb(213,187,154),true);c.drawText(lab[i],d(cx-25),d(top+50),p);
            }
        }

        @Override public boolean onTouchEvent(android.view.MotionEvent e){
            if(e.getAction()!=MotionEvent.ACTION_UP)return true;
            float x=e.getX()/den,y=e.getY()/den,W=getWidth()/den,H=getHeight()/den;
            if(y>H-82){
                activeTab=Math.max(0,Math.min(2,(int)(x/(W/3f))));invalidate();return true;
            }
            if(activeTab==0){
                checked=!checked;
                if(checked) playSignature();
                invalidate();
                Toast.makeText(MainActivity.this,checked?"Pointage automatique effectué":"Mode démo réinitialisé",Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    }
}
