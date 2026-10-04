package be.sfl.consultpresence.cleanroom;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.graphics.*;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
    private ConsultPresenceView view;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(92, 50, 30));
        getWindow().setNavigationBarColor(Color.rgb(59, 36, 23));
        view = new ConsultPresenceView();
        setContentView(view);
    }

    private void playSflSignature() {
        try {
            final ToneGenerator tone = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 70);
            tone.startTone(ToneGenerator.TONE_PROP_ACK, 150);
            Handler h = new Handler(Looper.getMainLooper());
            h.postDelayed(() -> tone.startTone(ToneGenerator.TONE_PROP_BEEP2, 120), 180);
            h.postDelayed(() -> tone.startTone(ToneGenerator.TONE_PROP_ACK, 90), 340);
            h.postDelayed(tone::release, 520);
        } catch (Throwable ignored) {}
    }

    private final class ConsultPresenceView extends View {
        private static final float W = 390f;
        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF r = new RectF();
        private int tab = 0;
        private boolean checkedIn = false;
        private boolean soundEnabled = true;
        private float scale = 1f;
        private float vh = 800f;

        private final int CREAM = Color.rgb(239,228,211);
        private final int PAPER = Color.rgb(255,249,240);
        private final int LEATHER = Color.rgb(104,58,34);
        private final int DARK = Color.rgb(69,39,24);
        private final int GOLD = Color.rgb(203,160,96);
        private final int INK = Color.rgb(52,39,30);
        private final int MUTED = Color.rgb(123,102,84);
        private final int GREEN = Color.rgb(43,116,77);

        ConsultPresenceView() { super(MainActivity.this); setLayerType(View.LAYER_TYPE_SOFTWARE, null); }

        private void text(float size,int color,boolean bold,boolean serif){
            p.setShader(null); p.setStyle(Paint.Style.FILL); p.setColor(color); p.setTextSize(size);
            p.setTypeface(Typeface.create(serif?"serif":"sans", bold?Typeface.BOLD:Typeface.NORMAL));
        }
        private void fill(int color){ p.setShader(null); p.setStyle(Paint.Style.FILL); p.setColor(color); }
        private void rounded(Canvas c,float l,float t,float rr,float bb,float rad,int color){
            fill(color); r.set(l,t,rr,bb); c.drawRoundRect(r,rad,rad,p);
        }
        private void card(Canvas c,float l,float t,float rr,float bb){
            p.setShadowLayer(9,0,4,Color.argb(30,55,35,20));
            rounded(c,l,t,rr,bb,18,PAPER); p.clearShadowLayer();
            stroke.setStyle(Paint.Style.STROKE); stroke.setStrokeWidth(1); stroke.setColor(Color.rgb(220,202,180));
            r.set(l,t,rr,bb); c.drawRoundRect(r,18,18,stroke);
            stroke.setColor(Color.argb(150,199,154,91));
            stroke.setPathEffect(new DashPathEffect(new float[]{5,4},0));
            r.set(l+7,t+7,rr-7,bb-7); c.drawRoundRect(r,11,11,stroke); stroke.setPathEffect(null);
        }

        @Override protected void onDraw(Canvas real){
            super.onDraw(real);
            scale=getWidth()/W; vh=getHeight()/scale;
            real.save(); real.scale(scale,scale);
            drawBg(real); drawHeader(real);
            if(tab==0) drawToday(real); else if(tab==1) drawTimesheets(real); else drawProfile(real);
            drawNav(real); real.restore();
        }

        private void drawBg(Canvas c){
            LinearGradient g=new LinearGradient(0,120,0,vh,Color.rgb(246,238,227),CREAM,Shader.TileMode.CLAMP);
            p.setShader(g); c.drawRect(0,0,W,vh,p); p.setShader(null);
        }

        private void drawHeader(Canvas c){
            LinearGradient g=new LinearGradient(0,0,0,126,Color.rgb(81,43,26),LEATHER,Shader.TileMode.CLAMP);
            p.setShader(g); c.drawRect(0,0,W,126,p); p.setShader(null);
            stroke.setStyle(Paint.Style.STROKE); stroke.setStrokeWidth(1); stroke.setColor(Color.argb(170,226,184,116));
            stroke.setPathEffect(new DashPathEffect(new float[]{6,5},0)); r.set(12,10,378,116); c.drawRoundRect(r,17,17,stroke); stroke.setPathEffect(null);
            text(22,Color.rgb(244,221,184),false,true); c.drawText("ConsultPresence",22,48,p);
            text(8.5f,Color.rgb(226,196,151),true,false); c.drawText("SFL CONSULTING",23,66,p);
            text(7.6f,Color.rgb(222,192,151),false,false); c.drawText("VOTRE TEMPS. VOS MISSIONS. AUTOMATIQUEMENT.",22,94,p);
            rounded(c,313,28,365,65,13,Color.rgb(74,40,24));
            text(8,Color.rgb(235,203,155),true,false); c.drawText("CLEAN",322,50,p);
        }

        private void drawToday(Canvas c){
            float y=147;
            text(29,INK,true,true); c.drawText("Aujourd’hui",16,y+26,p);
            text(9,MUTED,false,false); c.drawText("Build propre · aucune permission sensible",17,y+44,p);
            y+=60;

            if(checkedIn){
                rounded(c,16,y,374,y+48,15,Color.rgb(40,95,64));
                text(11,Color.WHITE,true,false); c.drawText("✓  Pointage automatique effectué",31,y+28,p);
                y+=60;
            }

            card(c,16,y,374,y+176);
            text(8.5f,Color.rgb(145,92,52),true,false); c.drawText("CLIENT",31,y+28,p);
            text(22,INK,true,true); c.drawText("NovaBank",31,y+55,p);
            text(9,MUTED,false,false); c.drawText("Projet : Core Banking",31,y+76,p);
            stroke.setStyle(Paint.Style.STROKE); stroke.setStrokeWidth(1); stroke.setColor(Color.rgb(231,217,199));
            c.drawLine(31,y+92,359,y+92,stroke);
            text(8.5f,Color.rgb(145,92,52),true,false); c.drawText("STATUT",31,y+118,p);
            text(11,checkedIn?GREEN:MUTED,true,false);
            c.drawText(checkedIn?"Présence reconnue":"En attente de présence",31,y+143,p);
            rounded(c,31,y+153,359,y+165,6,checkedIn?Color.rgb(220,238,226):Color.rgb(232,224,214));

            y+=191;
            card(c,16,y,186,y+72); card(c,204,y,374,y+72);
            text(8.5f,Color.rgb(145,92,52),true,false); c.drawText("ARRIVÉE",31,y+24,p); c.drawText("TEMPS COMPTÉ",219,y+24,p);
            text(21,INK,true,true); c.drawText(checkedIn?"08:32":"—",31,y+53,p); c.drawText(checkedIn?"1h 09":"0h 00",219,y+53,p);

            y+=87;
            card(c,16,y,374,y+62);
            text(10.5f,INK,true,false); c.drawText("Son de pointage automatique",31,y+26,p);
            text(7.8f,MUTED,false,false); c.drawText("Signature sonore SFL unique au pointage.",31,y+43,p);
            rounded(c,323,y+20,355,y+42,11,soundEnabled?Color.rgb(142,84,47):Color.rgb(180,170,158));
            fill(PAPER); c.drawCircle(soundEnabled?344:334,y+31,8,p);

            y+=76;
            rounded(c,16,y,374,y+50,16,checkedIn?Color.rgb(226,217,205):LEATHER);
            text(10.5f,checkedIn?MUTED:Color.rgb(248,226,190),true,false);
            c.drawText(checkedIn?"Réinitialiser la démonstration":"Simuler le pointage automatique",70,y+30,p);
        }

        private void drawTimesheets(Canvas c){
            float y=148; text(29,INK,true,true); c.drawText("Timesheets",16,y+26,p);
            text(9,MUTED,false,false); c.drawText("Prévisualisation hebdomadaire",17,y+44,p); y+=62;
            card(c,16,y,374,y+110);
            text(8.5f,Color.rgb(145,92,52),true,false); c.drawText("TOTAL SEMAINE",32,y+27,p);
            text(31,INK,true,true); c.drawText(checkedIn?"24h39":"23h30",32,y+66,p);
            text(8.5f,MUTED,false,false); c.drawText("Objectif : 40h00",32,y+88,p);
            y+=126;
            String[] ds={"Lundi","Mardi","Mercredi","Jeudi","Vendredi"};
            String[] hs={"8h00","7h30","8h00",checkedIn?"1h09":"—","—"};
            for(int i=0;i<ds.length && y+55<vh-70;i++){
                card(c,16,y,374,y+50);
                text(10.5f,INK,true,false); c.drawText(ds[i],32,y+30,p);
                text(11,i<3||(i==3&&checkedIn)?LEATHER:MUTED,true,false); c.drawText(hs[i],319,y+30,p);
                y+=58;
            }
        }

        private void drawProfile(Canvas c){
            float y=148; text(29,INK,true,true); c.drawText("Profil",16,y+26,p);
            text(9,MUTED,false,false); c.drawText("Configuration de démonstration",17,y+44,p); y+=62;
            card(c,16,y,374,y+88);
            rounded(c,31,y+21,77,y+67,23,Color.rgb(142,84,47));
            text(13,Color.WHITE,true,false); c.drawText("JD",43,y+50,p);
            text(12,INK,true,false); c.drawText("Julien Dupont",92,y+38,p);
            text(8.4f,MUTED,false,false); c.drawText("Senior Consultant · SFL Consulting",92,y+56,p);
            y+=103;
            card(c,16,y,374,y+176);
            text(17,INK,true,true); c.drawText("Administration",32,y+31,p);
            profileRow(c,y+64,"Société de consultance","SFL Consulting");
            profileRow(c,y+103,"Consultant","Julien Dupont");
            profileRow(c,y+142,"Client","NovaBank");
            y+=191;
            if(y+96<vh-70){
                card(c,16,y,374,y+92);
                text(11,INK,true,false); c.drawText("Vie privée par conception",32,y+30,p);
                text(8,MUTED,false,false); c.drawText("Aucune permission de localisation dans cette build.",32,y+51,p);
                c.drawText("Aucun trajet GPS n’est enregistré.",32,y+69,p);
            }
        }

        private void profileRow(Canvas c,float y,String a,String b){
            text(9.5f,INK,true,false); c.drawText(a,32,y,p);
            text(7.8f,MUTED,false,false); c.drawText(b,32,y+15,p);
        }

        private void drawNav(Canvas c){
            float top=vh-64; rounded(c,8,top,382,vh-5,18,DARK);
            String[] labels={"Aujourd’hui","Timesheets","Profil"};
            for(int i=0;i<3;i++){
                float cx=W*(i+.5f)/3f;
                int col=i==tab?Color.rgb(244,211,158):Color.rgb(202,173,136);
                if(i==tab) rounded(c,cx-49,top+7,cx+49,vh-12,12,Color.rgb(92,50,30));
                text(8.4f,col,true,false); float tw=p.measureText(labels[i]); c.drawText(labels[i],cx-tw/2,top+36,p);
            }
        }

        @Override public boolean onTouchEvent(MotionEvent e){
            if(e.getAction()!=MotionEvent.ACTION_UP) return true;
            float x=e.getX()/scale, y=e.getY()/scale;
            if(y>vh-75){ tab=Math.max(0,Math.min(2,(int)(x/(W/3f)))); invalidate(); return true; }
            if(tab==0){
                if(y>430 && y<520 && x>300){
                    soundEnabled=!soundEnabled;
                    Toast.makeText(MainActivity.this,soundEnabled?"Signature sonore activée":"Signature sonore désactivée",Toast.LENGTH_SHORT).show();
                    invalidate(); return true;
                }
                if(y>500 && y<650){
                    boolean before=checkedIn; checkedIn=!checkedIn;
                    if(!before && checkedIn && soundEnabled) playSflSignature();
                    Toast.makeText(MainActivity.this,checkedIn?"Pointage automatique effectué":"Démonstration réinitialisée",Toast.LENGTH_SHORT).show();
                    invalidate(); return true;
                }
            }
            return true;
        }
    }
}
