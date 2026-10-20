package com.kaku.socialreport;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class MainActivity extends Activity {
    private EditText accountInput;
    private Spinner platformSpinner;
    private TextView attemptsView, confirmedView, statusView;
    private Button startButton, stopButton;
    private boolean tracking = false;
    private int attempts = 0, confirmed = 0;

    private final int ink = Color.rgb(25, 32, 56);
    private final int muted = Color.rgb(111, 120, 143);
    private final int purple = Color.rgb(91, 72, 220);
    private final int page = Color.rgb(246, 247, 253);

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(35, 34, 92));
        getWindow().setNavigationBarColor(Color.rgb(35, 34, 92));

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(page);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(16), dp(20), dp(28));
        scroll.addView(root);

        // Hero header
        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(dp(22), dp(24), dp(22), dp(24));
        hero.setBackground(gradient(new int[]{Color.rgb(43, 43, 112), Color.rgb(99, 69, 224)}, dp(26)));
        root.addView(hero, params(-1, -2, 0, 0, 0, 18));

        TextView badge = text("●  SOCIAL REPORT MANAGER", 11, true, Color.rgb(225, 222, 255));
        badge.setLetterSpacing(0.07f);
        hero.addView(badge, params(-1, -2, 0, 0, 0, 14));

        TextView title = text("مركز إدارة البلاغات", 27, true, Color.WHITE);
        title.setGravity(Gravity.RIGHT);
        hero.addView(title, params(-1, -2, 0, 0, 0, 8));

        TextView desc = text("أدوات منظمة لمراجعة الحسابات والوصول إلى قنوات الإبلاغ الرسمية.", 14, false, Color.rgb(232, 230, 255));
        desc.setGravity(Gravity.RIGHT);
        desc.setLineSpacing(dp(3), 1f);
        hero.addView(desc, params(-1, -2, 0, 0, 0, 18));

        LinearLayout chips = new LinearLayout(this);
        chips.setOrientation(LinearLayout.HORIZONTAL);
        chips.setGravity(Gravity.RIGHT);
        hero.addView(chips, params(-1, -2, 0, 0, 0, 0));
        chips.addView(chip("TikTok"), params(-2, dp(34), 0, 0, dp(7), 0));
        chips.addView(chip("Instagram"), params(-2, dp(34), 0, 0, dp(7), 0));
        chips.addView(chip("Facebook"), params(-2, dp(34), 0, 0, 0, 0));

        // Form card
        LinearLayout form = card();
        root.addView(form, params(-1, -2, 0, 0, 0, 16));
        form.addView(sectionTitle("01", "بيانات الحساب"), params(-1, -2, 0, 0, 0, 16));

        form.addView(label("اختر المنصة"), params(-1, -2, 0, 0, 0, 7));
        platformSpinner = new Spinner(this);
        String[] platforms = {"TikTok", "Instagram", "Facebook"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, platforms) {
            @Override public View getView(int position, View convertView, ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTextColor(ink); v.setTextSize(16); v.setPadding(dp(12), dp(8), dp(12), dp(8));
                return v;
            }
            @Override public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView v = (TextView) super.getDropDownView(position, convertView, parent);
                v.setTextColor(ink); v.setPadding(dp(12), dp(12), dp(12), dp(12));
                return v;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        platformSpinner.setAdapter(adapter);
        platformSpinner.setBackground(round(Color.rgb(247, 248, 252), dp(14), Color.rgb(230, 232, 243), dp(1)));
        form.addView(platformSpinner, params(-1, dp(52), 0, 0, 0, 16));

        form.addView(label("رابط الحساب أو اسم المستخدم"), params(-1, -2, 0, 0, 0, 7));
        accountInput = new EditText(this);
        accountInput.setSingleLine(true);
        accountInput.setTextSize(15);
        accountInput.setTextColor(ink);
        accountInput.setHintTextColor(Color.rgb(156, 162, 180));
        accountInput.setHint("مثال: @username أو رابط الحساب");
        accountInput.setPadding(dp(14), 0, dp(14), 0);
        accountInput.setBackground(round(Color.rgb(247, 248, 252), dp(14), Color.rgb(230, 232, 243), dp(1)));
        form.addView(accountInput, params(-1, dp(54), 0, 0, 0, 18));

        startButton = button("▶  بدء الإبلاغ الرسمي", purple, Color.WHITE);
        form.addView(startButton, params(-1, dp(54), 0, 0, 0, 9));
        startButton.setOnClickListener(v -> startReportingSession());

        stopButton = button("■  إيقاف المتابعة", Color.rgb(255, 235, 238), Color.rgb(190, 45, 65));
        form.addView(stopButton, params(-1, dp(50), 0, 0, 0, 10));
        stopButton.setEnabled(false);
        stopButton.setAlpha(0.55f);
        stopButton.setOnClickListener(v -> stopReportingSession());

        statusView = text("الحالة: متوقف", 13, true, muted);
        statusView.setGravity(Gravity.RIGHT);
        form.addView(statusView, params(-1, -2, 0, dp(5), 0, 8));

        TextView hint = text("ⓘ  يبدأ الزر جلسة متابعة محلية ويفتح قناة الإبلاغ الرسمية مرة واحدة. لا يتم إرسال بلاغات تلقائية.", 12, false, muted);
        hint.setGravity(Gravity.RIGHT);
        hint.setLineSpacing(dp(2), 1f);
        form.addView(hint, params(-1, -2, 0, dp(3), 0, 0));

        // Counters
        LinearLayout statsCard = card();
        root.addView(statsCard, params(-1, -2, 0, 0, 0, 16));
        statsCard.addView(sectionTitle("02", "ملخص النشاط"), params(-1, -2, 0, 0, 0, 14));

        LinearLayout stats = new LinearLayout(this);
        stats.setOrientation(LinearLayout.HORIZONTAL);
        statsCard.addView(stats, params(-1, -2, 0, 0, 0, 14));
        LinearLayout attemptBox = statBox("محاولات فتح الصفحة", "0", Color.rgb(91, 72, 220));
        attemptsView = (TextView) attemptBox.getTag();
        LinearLayout confirmedBox = statBox("تأكيدات يدوية", "0", Color.rgb(18, 145, 100));
        confirmedView = (TextView) confirmedBox.getTag();
        stats.addView(attemptBox, new LinearLayout.LayoutParams(0, dp(112), 1));
        LinearLayout.LayoutParams second = new LinearLayout.LayoutParams(0, dp(112), 1);
        second.setMargins(dp(10), 0, 0, 0);
        stats.addView(confirmedBox, second);

        Button confirmButton = button("＋  تسجيل تأكيد يدوي", Color.rgb(232, 249, 242), Color.rgb(18, 125, 88));
        statsCard.addView(confirmButton, params(-1, dp(48), 0, 0, 0, 8));
        confirmButton.setOnClickListener(v -> {
            confirmed++;
            confirmedView.setText(String.valueOf(confirmed));
            Toast.makeText(this, "تم تحديث التأكيد اليدوي", Toast.LENGTH_SHORT).show();
        });

        Button resetButton = button("إعادة ضبط العدادات", Color.rgb(246, 247, 252), muted);
        statsCard.addView(resetButton, params(-1, dp(46), 0, 0, 0, 0));
        resetButton.setOnClickListener(v -> {
            attempts = 0; confirmed = 0;
            attemptsView.setText("0"); confirmedView.setText("0");
        });

        TextView footer = text("تنبيه: فتح صفحة الإبلاغ لا يعني أن البلاغ أُرسل أو قُبل. استخدم هذه الأدوات للإبلاغ عن مخالفات حقيقية فقط.", 12, false, muted);
        footer.setGravity(Gravity.CENTER);
        footer.setLineSpacing(dp(3), 1f);
        root.addView(footer, params(-1, -2, 0, 0, 0, 0));

        setContentView(scroll);
    }

    private LinearLayout statBox(String title, String value, int accent) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setPadding(dp(8), dp(12), dp(8), dp(12));
        box.setBackground(round(Color.rgb(248, 249, 253), dp(16), Color.rgb(235, 237, 246), dp(1)));
        TextView number = text(value, 28, true, accent);
        box.addView(number);
        TextView caption = text(title, 11, true, muted);
        caption.setGravity(Gravity.CENTER);
        box.addView(caption, params(-1, -2, 0, dp(5), 0, 0));
        box.setTag(number);
        return box;
    }

    private LinearLayout card() {
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setPadding(dp(18), dp(18), dp(18), dp(18));
        c.setBackground(round(Color.WHITE, dp(22), Color.rgb(237, 239, 247), dp(1)));
        return c;
    }

    private LinearLayout sectionTitle(String number, String title) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        TextView name = text(title, 18, true, ink);
        row.addView(name);
        TextView no = text(number, 11, true, purple);
        no.setGravity(Gravity.CENTER);
        no.setBackground(round(Color.rgb(240, 237, 255), dp(9), Color.TRANSPARENT, 0));
        LinearLayout.LayoutParams p = params(dp(34), dp(30), dp(9), 0, 0, 0);
        row.addView(no, p);
        return row;
    }

    private TextView chip(String label) {
        TextView t = text(label, 11, true, Color.WHITE);
        t.setGravity(Gravity.CENTER);
        t.setPadding(dp(10), 0, dp(10), 0);
        t.setBackground(round(0x33FFFFFF, dp(18), 0x55FFFFFF, dp(1)));
        return t;
    }

    private TextView label(String value) {
        return text(value, 14, true, ink);
    }

    private Button button(String value, int bg, int fg) {
        Button b = new Button(this);
        b.setText(value);
        b.setTextSize(14);
        b.setTypeface(null, Typeface.BOLD);
        b.setAllCaps(false);
        b.setTextColor(fg);
        b.setBackground(round(bg, dp(15), Color.TRANSPARENT, 0));
        return b;
    }

    private TextView text(String value, int size, boolean bold, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(null, Typeface.BOLD);
        return t;
    }

    private GradientDrawable round(int color, int radius, int strokeColor, int strokeWidth) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        if (strokeWidth > 0) d.setStroke(strokeWidth, strokeColor);
        return d;
    }

    private GradientDrawable gradient(int[] colors, int radius) {
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
        d.setCornerRadius(radius);
        return d;
    }

    private LinearLayout.LayoutParams params(int w, int h, int l, int t, int r, int b) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(w, h);
        p.setMargins(l, t, r, b);
        return p;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void startReportingSession() {
        String account = accountInput.getText().toString().trim();
        if (account.isEmpty()) {
            accountInput.setError("أدخل رابط الحساب أو اسم المستخدم");
            return;
        }
        if (tracking) {
            Toast.makeText(this, "المتابعة تعمل بالفعل", Toast.LENGTH_SHORT).show();
            return;
        }

        tracking = true;
        attempts++;
        attemptsView.setText(String.valueOf(attempts));
        statusView.setText("الحالة: بدأت المتابعة — أكمل البلاغ داخل المنصة");
        statusView.setTextColor(Color.rgb(18, 145, 100));
        startButton.setEnabled(false);
        startButton.setAlpha(0.55f);
        stopButton.setEnabled(true);
        stopButton.setAlpha(1f);

        String platform = platformSpinner.getSelectedItem().toString();
        String url;
        if (platform.equals("TikTok")) {
            url = "https://support.tiktok.com/en/safety-hc/report-a-problem";
        } else if (platform.equals("Instagram")) {
            url = "https://help.instagram.com/";
        } else {
            url = "https://www.facebook.com/help/";
        }

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(this, "تعذر فتح المتصفح على هذا الجهاز", Toast.LENGTH_LONG).show();
        }
        Toast.makeText(this, "تم تسجيل محاولة فتح قناة الإبلاغ", Toast.LENGTH_SHORT).show();
    }

    private void stopReportingSession() {
        tracking = false;
        statusView.setText("الحالة: متوقف");
        statusView.setTextColor(muted);
        startButton.setEnabled(true);
        startButton.setAlpha(1f);
        stopButton.setEnabled(false);
        stopButton.setAlpha(0.55f);
        Toast.makeText(this, "تم إيقاف المتابعة داخل التطبيق", Toast.LENGTH_SHORT).show();
    }
}