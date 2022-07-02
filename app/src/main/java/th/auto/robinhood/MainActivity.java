package th.auto.robinhood;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static int money_high;
    public static int money_low;
    public static int best_high;
    public static boolean is_click_job;
    public static boolean is_print_job;
    public static boolean is_shop_ban;
    public static boolean is_shop_like;
    public static boolean is_start_lock;
    public static boolean is_end_lock;
    public static String[] ban_shop;
    public static String[] like_shop;
    public static String[] start_lock;
    public static String[] end_lock;
    private boolean is_login_loading = false;
    private boolean is_login = false;
    private String device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (check_network()) {
                login_database();
            } else {
                finish();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 1);
            }
            finish();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (is_login) {
            set_accessibility_text();
            if (!check_network()) {
                finish();
            }
        }
    }

    @SuppressLint("HardwareIds")
    private void login_database() {
        device = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toString();

        if (load_content("login")[0].equals("true") && Objects.equals(load_content("login")[2], device) && load_content("login").length == 4) {
            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    "https://dbphpasdas.000webhostapp.com/login.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.toString().split(" ").length > 1) {
                                create_alert_ui("ตอนนี้คุณเหลือเวลา " + response.toString().split(" ")[1] + " วัน");
                                open_def_lay();
                            } else {
                                save_content("login", new String[] { "false", null, null, null });
                                create_alert_ui(response.toString().split(" ")[0].toString());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            create_alert_ui("ERROR โปรดแจ้งแอดมิน");
                        }
                    }
            ) {
                @NonNull
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> param = new HashMap<String, String>();

                    param.put("time_name_id", load_content("login")[2].toString());
                    param.put("time_password", load_content("login")[3].toString());
                    return param;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.add(request);
        } else {
            setContentView(R.layout.login);
            EditText pass = (EditText) findViewById(R.id.password_text);
            TextView de = (TextView) findViewById(R.id.device_text);
            Button copy = (Button) findViewById(R.id.copy);
            Button ok = (Button) findViewById(R.id.login_button);
            de.setText(device);

            ok.setOnClickListener(view -> {
                if (!is_login_loading) {
                    is_login_loading = true;
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(MainActivity.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        StringRequest request = new StringRequest(Request.Method.POST,
                                "https://dbphpasdas.000webhostapp.com/login.php",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if (response.toString().split(" ").length > 1) {
                                            create_alert_ui("ตอนนี้คุณเหลือเวลา " + response.toString().split(" ")[1] + " วัน");
                                            save_content("login", new String[]{"true", response.toString().split(" ")[1], device, pass.getText().toString()});
                                            open_def_lay();
                                        } else {
                                            save_content("login", new String[]{"false", null, null, null});
                                            create_alert_ui(response.toString().split(" ")[0].toString());
                                        }

                                        pass.setText("");
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                create_alert_ui("ERROR โปรดแจ้งแอดมิน");
                                is_login_loading = false;
                            }
                        }) {
                            @NonNull
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> param = new HashMap<String, String>();

                                param.put("user_name_id", device.toString());
                                param.put("user_password", pass.getText().toString());
                                return param;
                            }
                        };

                        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                        requestQueue.add(request);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            }, 1);
                        }
                    }
                }
            });

            copy.setOnClickListener(view -> {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(device, device);
                clipboardManager.setPrimaryClip(clip);

                Toast.makeText(MainActivity.this, "คัดลอกไอดีเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();
            });

            Button add_line = (Button) findViewById(R.id.add_line);

            add_line.setOnClickListener(view -> {
                Intent test = new Intent(Intent.ACTION_VIEW);
                test.setData(Uri.parse("https://page.line.me/867aisvl"));
                test.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(test);
            });
        }
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private void open_def_lay() {
        setContentView(R.layout.setting_main);

        Button get_accessibility = (Button) findViewById(R.id.get_accessibility);
        Switch switch_run_click = (Switch) findViewById(R.id.switch_run_click);
        Switch switch_run_print = (Switch) findViewById(R.id.switch_run_print);
        Switch switch_ban_shop = (Switch) findViewById(R.id.switch_ban_shop);
        Switch switch_like_shop = (Switch) findViewById(R.id.switch_like_shop);
        Switch switch_start_lock = (Switch) findViewById(R.id.switch_start_lock);
        Switch switch_end_lock = (Switch) findViewById(R.id.switch_end_lock);
        TextView money_low_text = (TextView) findViewById(R.id.money_low_text);
        TextView money_high_text = (TextView) findViewById(R.id.money_high_text);
        TextView best_high_text = (TextView) findViewById(R.id.best_high_text);
        TextView ban_shop_text = (TextView) findViewById(R.id.ban_shop_text);
        TextView like_shop_text = (TextView) findViewById(R.id.like_shop_text);
        TextView start_lock_text = (TextView) findViewById(R.id.start_lock_text);
        TextView end_lock_text = (TextView) findViewById(R.id.end_lock_text);
        Button money_low_value = (Button) findViewById(R.id.money_low_value);
        Button money_high_value = (Button) findViewById(R.id.money_high_value);
        Button best_high_value = (Button) findViewById(R.id.best_high_value);
        Button ban_shop_value = (Button) findViewById(R.id.ban_shop_value);
        Button like_shop_value = (Button) findViewById(R.id.like_shop_value);
        Button start_lock_value = (Button) findViewById(R.id.start_lock_value);
        Button end_lock_value = (Button) findViewById(R.id.end_lock_value);

        is_login = true;
        set_accessibility_text();

        if (load_content("data").length > 0) {
            String[] data = load_content("data");
            money_low = Integer.parseInt(data[0]);
            money_low_value.setText(data[0]);
            money_high = Integer.parseInt(data[1]);
            money_high_value.setText(data[1]);
            best_high = Integer.parseInt(data[2]);
            best_high_value.setText(data[2]);
            is_click_job = Boolean.parseBoolean(data[3]);
            switch_run_click.setChecked(Boolean.parseBoolean(data[3]));
            is_print_job = Boolean.parseBoolean(data[4]);
            switch_run_print.setChecked(Boolean.parseBoolean(data[4]));
            is_shop_ban = Boolean.parseBoolean(data[5]);
            switch_ban_shop.setChecked(Boolean.parseBoolean(data[5]));
            is_shop_like = Boolean.parseBoolean(data[6]);
            switch_like_shop.setChecked(Boolean.parseBoolean(data[6]));
            is_start_lock = Boolean.parseBoolean(data[7]);
            switch_start_lock.setChecked(Boolean.parseBoolean(data[7]));
            is_end_lock = Boolean.parseBoolean(data[8]);
            switch_end_lock.setChecked(Boolean.parseBoolean(data[8]));
            if (!data[9].equals("null")) {
                ban_shop = data[9].substring(1, data[9].length() - 1).split(",");
                ban_shop_value.setText(data[9].substring(1, data[9].length() - 1));
            }
            if (!data[10].equals("null")) {
                like_shop = data[10].substring(1, data[10].length() - 1).split(",");
                like_shop_value.setText(data[10].substring(1, data[10].length() - 1));
            }
            if (!data[11].equals("null")) {
                start_lock = data[11].substring(1, data[11].length() - 1).split(",");
                start_lock_value.setText(data[11].substring(1, data[11].length() - 1));
            }
            if (!data[12].equals("null")) {
                end_lock = data[12].substring(1, data[12].length() - 1).split(",");
                end_lock_value.setText(data[12].substring(1, data[12].length() - 1));
            }
        } else {
            money_low = 43;
            money_high = 43;
            best_high = 3;
            is_click_job = false;
            is_print_job = false;
            is_shop_ban = false;
            is_shop_like = false;
            is_start_lock = false;
            is_end_lock = false;
            ban_shop = new String[] {};
            like_shop = new String[] {};
            start_lock = new String[] {};
            end_lock = new String[] {};
        }

        get_accessibility.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 1);
                }
            }
        });

        switch_run_click.setOnClickListener(view -> {
            is_click_job = switch_run_click.isChecked();
            switch_run_click.setChecked(switch_run_click.isChecked());
        });

        switch_run_print.setOnClickListener(view -> {
            is_print_job = switch_run_print.isChecked();
            switch_run_print.setChecked(switch_run_print.isChecked());
        });

        switch_ban_shop.setOnClickListener(view -> {
            is_shop_ban = switch_ban_shop.isChecked();
            switch_ban_shop.setChecked(switch_ban_shop.isChecked());
            is_shop_like = switch_ban_shop.isChecked();
            switch_like_shop.setChecked(switch_ban_shop.isChecked());
        });

        switch_like_shop.setOnClickListener(view -> {
            is_shop_like = switch_like_shop.isChecked();
            switch_like_shop.setChecked(switch_like_shop.isChecked());
            is_shop_ban = switch_like_shop.isChecked();
            switch_ban_shop.setChecked(switch_like_shop.isChecked());
        });

        switch_start_lock.setOnClickListener(view -> {
            is_start_lock = switch_start_lock.isChecked();
            System.out.println(is_start_lock);
            switch_start_lock.setChecked(switch_start_lock.isChecked());
        });

        switch_end_lock.setOnClickListener(view -> {
            is_end_lock = switch_end_lock.isChecked();
            switch_end_lock.setChecked(switch_end_lock.isChecked());
        });

        money_low_value.setOnClickListener(view -> {
            create_new_contact_dialog(money_low_text, money_low_value);
        });
        money_high_value.setOnClickListener(view -> {
            create_new_contact_dialog(money_high_text, money_high_value);
        });
        best_high_value.setOnClickListener(view -> {
            create_new_contact_dialog(best_high_text, best_high_value);
        });
        ban_shop_value.setOnClickListener(view -> {
            create_new_contact_dialog(ban_shop_text, ban_shop_value);
        });
        like_shop_value.setOnClickListener(view -> {
            create_new_contact_dialog(like_shop_text, like_shop_value);
        });
        start_lock_value.setOnClickListener(view -> {
            create_new_contact_dialog(start_lock_text, start_lock_value);
        });
        end_lock_value.setOnClickListener(view -> {
            create_new_contact_dialog(end_lock_text, end_lock_value);
        });
    }

    private String[] load_content(String data_name) {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            File path = getApplicationContext().getFilesDir();
            File read_from = new File(path, data_name + ".txt");
            byte[] content = new byte[(int) read_from.length()];

            if (Objects.equals(data_name, "login")) {
                if (!read_from.canRead()) {
                    return new String[] {"false", null, null, null};
                }
            }
            try {
                FileInputStream stream = new FileInputStream(read_from);
                stream.read(content);

                String s = new String(content);

                return s.split("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 1);
            }
        }
        return new String[] {};
    }

    private void save_content(String data_name, String[] value) {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            File path = getApplicationContext().getFilesDir();
            try {
                FileOutputStream writer = new FileOutputStream(new File(path, data_name + ".txt"));
                writer.flush();
                String t = "";
                for (int i = 0; i < value.length; i++) {
                    if (i == value.length) {
                        t += value[i];
                    } else {
                        t += value[i] + "\n";
                    }
                }
                writer.write((t).getBytes());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void create_new_contact_dialog(TextView head, Button value) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.popup_ui, null);
        TextView h = (TextView) contactPopupView.findViewById(R.id.alert_text);
        TextView e = (TextView) contactPopupView.findViewById(R.id.text_error_ui);
        TextView v = (TextView) contactPopupView.findViewById(R.id.text_value_default);
        Button confirm = (Button) contactPopupView.findViewById(R.id.confirm);
        Button cancel = (Button) contactPopupView.findViewById(R.id.cancel);

        h.setText(head.getText().toString());
        if (value == findViewById(R.id.ban_shop_value) || value == findViewById(R.id.like_shop_value) || value == findViewById(R.id.start_lock_value) || value == findViewById(R.id.end_lock_value)) {
            v.setInputType(InputType.TYPE_CLASS_TEXT);
            if (value.getText().toString().equals("ไม่มี")) {
                v.setText("");
            } else {
                v.setText(value.getText().toString());
            }
        } else {
            v.setInputType(InputType.TYPE_CLASS_NUMBER);
            v.setText(value.getText().toString());
        }

        dialogBuilder.setView(contactPopupView);
        AlertDialog dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        confirm.setOnClickListener(view -> {
            Button money_low_value = (Button) findViewById(R.id.money_low_value);
            Button money_high_value = (Button) findViewById(R.id.money_high_value);
            Button ban_shop_value = (Button) findViewById(R.id.ban_shop_value);
            Button like_shop_value = (Button) findViewById(R.id.like_shop_value);
            Button start_lock_value = (Button) findViewById(R.id.start_lock_value);
            Button end_lock_value = (Button) findViewById(R.id.end_lock_value);

            if (value == ban_shop_value) {
                if (v.getText().toString().equals("")) {
                    v.setTextColor(Color.BLACK);
                    ban_shop = new String[] {};
                    value.setText("ไม่มี");
                    e.setText("");
                    dialog.dismiss();
                } else {
                    v.setTextColor(Color.BLACK);
                    ban_shop = v.getText().toString().split(",");
                    value.setText(v.getText().toString());
                    e.setText("");
                    dialog.dismiss();
                }
            } else if (value == like_shop_value) {
                if (v.getText().toString().equals("")) {
                    v.setTextColor(Color.BLACK);
                    like_shop = new String[] {};
                    value.setText("ไม่มี");
                    e.setText("");
                    dialog.dismiss();
                } else {
                    v.setTextColor(Color.BLACK);
                    like_shop = v.getText().toString().split(",");
                    value.setText(v.getText().toString());
                    e.setText("");
                    dialog.dismiss();
                }
            } else if (value == start_lock_value) {
                if (v.getText().toString().equals("")) {
                    v.setTextColor(Color.BLACK);
                    start_lock = new String[] {};
                    value.setText("ไม่มี");
                    e.setText("");
                    dialog.dismiss();
                } else {
                    v.setTextColor(Color.BLACK);
                    start_lock = v.getText().toString().split(",");
                    value.setText(v.getText().toString());
                    e.setText("");
                    dialog.dismiss();
                }
            } else if (value == end_lock_value) {
                if (v.getText().toString().equals("")) {
                    v.setTextColor(Color.BLACK);
                    end_lock = new String[] {};
                    value.setText("ไม่มี");
                    e.setText("");
                    dialog.dismiss();
                } else {
                    v.setTextColor(Color.BLACK);
                    end_lock = v.getText().toString().split(",");
                    value.setText(v.getText().toString());
                    e.setText("");
                    dialog.dismiss();
                }
            } else {
                if (!v.getText().toString().equals("")) {
                    if (value == money_low_value) {
                        if (Integer.parseInt(v.getText().toString()) <= Integer.parseInt(money_high_value.getText().toString())) {
                            v.setTextColor(Color.BLACK);
                            money_low = Integer.parseInt(v.getText().toString());
                            value.setText(String.valueOf(Integer.parseInt(v.getText().toString())));
                            e.setText("");
                            dialog.dismiss();
                        } else {
                            e.setText("ราคาต่ำสุดมีค่ามากกว่าราคาสูงสุด");
                            v.setTextColor(Color.RED);
                        }
                    } else if (value == money_high_value) {
                        if (Integer.parseInt(v.getText().toString()) >= Integer.parseInt(money_low_value.getText().toString())) {
                            v.setTextColor(Color.BLACK);
                            money_high = Integer.parseInt(v.getText().toString());
                            value.setText(String.valueOf(Integer.parseInt(v.getText().toString())));
                            e.setText("");
                            dialog.dismiss();
                        } else {
                            e.setText("ราคาสูงสุดมีค่าน้อยกว่าราคาต่ำสุด");
                            v.setTextColor(Color.RED);
                        }
                    } else {
                        if (Integer.parseInt(v.getText().toString()) > 0) {
                            v.setTextColor(Color.BLACK);
                            best_high = Integer.parseInt(v.getText().toString());
                            value.setText(String.valueOf(Integer.parseInt(v.getText().toString())));
                            e.setText("");
                            dialog.dismiss();
                        } else {
                            e.setText(v.getText().toString() + "ต้องมีค่ามากกว่า 0");
                            v.setTextColor(Color.RED);
                        }
                    }
                } else {
                    e.setText("กรุณาใส่ตัวเลขอย่างน้อย 1 ตัว");
                    v.setTextColor(Color.RED);
                }
            }
        });
        cancel.setOnClickListener(view -> {
            e.setText("");
            v.setTextColor(Color.BLACK);
            dialog.dismiss();
        });
    }

    private void create_alert_ui(String value) {
        AlertDialog.Builder dialogAlertBuilder = new AlertDialog.Builder(this);
        View contactPopupView = getLayoutInflater().inflate(R.layout.alert_ui, null);
        TextView h = (TextView) contactPopupView.findViewById(R.id.alert_text);
        TextView v = (TextView) contactPopupView.findViewById(R.id.alert_text_value);
        Button ok = (Button) contactPopupView.findViewById(R.id.alert_ok);

        dialogAlertBuilder.setView(contactPopupView);
        AlertDialog dialogAlert = dialogAlertBuilder.create();
        dialogAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAlert.show();

        h.setText("แจ้งเตือน !");
        v.setText(value);
        ok.setOnClickListener(view -> {
            dialogAlert.dismiss();
        });
    }

    private void set_accessibility_text() {
        TextView ast = (TextView) findViewById(R.id.has_accessibility);
        AccessibilityManager manager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);

        if (manager.isEnabled()) {
            ast.setText("กำลังทำงานอยู่");
        } else {
            ast.setText("หยุดทำงาน");
        }
    }

    private boolean check_network() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("network notification", "network notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi == null || mobile == null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this);
            builder.setContentTitle("แจ้งเตือนอินเทอร์เน็ต");
            builder.setContentText("โปรดเชื่อมต่ออินเทอร์เน็ตของคุณ");
            builder.setSmallIcon(R.drawable.ic_launcher_background);
            builder.setAutoCancel(true);
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
            managerCompat.notify(1, builder.build());
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        save_content("data", new String[] {
                String.valueOf(money_low),
                String.valueOf(money_high),
                String.valueOf(best_high),
                String.valueOf(is_click_job),
                String.valueOf(is_print_job),
                String.valueOf(is_shop_ban),
                String.valueOf(is_shop_like),
                String.valueOf(is_start_lock),
                String.valueOf(is_end_lock),
                Arrays.toString(ban_shop),
                Arrays.toString(like_shop),
                Arrays.toString(start_lock),
                Arrays.toString(end_lock)
        });

        super.onDestroy();
    }
}