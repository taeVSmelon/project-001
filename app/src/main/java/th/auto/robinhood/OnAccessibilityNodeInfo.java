package th.auto.robinhood;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

public class OnAccessibilityNodeInfo extends AccessibilityService {
    private String package_name = "th.in.robinhood.rider";
    private boolean is_run = false;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (getRootInActiveWindow() == null) {
                return;
            }

            /*if (!is_run) {
                is_run = true;*/
                /*List<AccessibilityNodeInfo> text = new ArrayList<>();
                List<AccessibilityNodeInfo> button = new ArrayList<>();*/
            StringBuilder value = new StringBuilder();

                /*try {
                    //get_value1(getRootInActiveWindow(), 0, text, button);
                    get_value2(getRootInActiveWindow(), 0, value);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                test1(getRootInActiveWindow(), 0, value);
            }

            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(value.toString(), value.toString());
            clipboardManager.setPrimaryClip(clip);
                /*is_run = false;
            }*/
        }
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = 2048;
        info.packageNames = new String[] {
                getPackageName(),
                package_name
        };
        info.feedbackType = -1;
        info.notificationTimeout = 100;

        this.setServiceInfo(info);
    }

    private void test1(AccessibilityNodeInfo accessibilityNodeInfo, int n, StringBuilder value) {
        if (accessibilityNodeInfo.getParent() != null) {
            test1(accessibilityNodeInfo.getParent(), n, value);
        } else {
            test2(accessibilityNodeInfo, n, value);
        }
    }

    private void test2(AccessibilityNodeInfo accessibilityNodeInfo, int n, StringBuilder value) {
        for (int r = 0; r < accessibilityNodeInfo.getChildCount(); r++) {
            if (accessibilityNodeInfo.getChild(r).getContentDescription() != null) {
                value.append(accessibilityNodeInfo.getChild(r).getContentDescription().toString()).append("\n").append("getContentDescription").append("\n");
            }
            if (accessibilityNodeInfo.getChild(r).getText() != null) {
                value.append(accessibilityNodeInfo.getChild(r).getText().toString()).append("\n").append("getText").append("\n");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (accessibilityNodeInfo.getChild(r).getPaneTitle() != null) {
                    value.append(accessibilityNodeInfo.getChild(r).getPaneTitle().toString()).append("\n").append("getPaneTitle").append("\n");
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (accessibilityNodeInfo.getChild(r).getStateDescription() != null) {
                    value.append(accessibilityNodeInfo.getChild(r).getStateDescription().toString()).append("\n").append("getStateDescription").append("\n");
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (accessibilityNodeInfo.getChild(r).getError() != null) {
                    value.append(accessibilityNodeInfo.getChild(r).getError().toString()).append("\n").append("getError").append("\n");
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (accessibilityNodeInfo.getChild(r).getTooltipText() != null) {
                    value.append(accessibilityNodeInfo.getChild(r).getTooltipText().toString()).append("\n").append("getTooltipText").append("\n");
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (accessibilityNodeInfo.getChild(r).getHintText() != null) {
                    value.append(accessibilityNodeInfo.getChild(r).getHintText().toString()).append("\n").append("getHintText").append("\n");
                }
            }
            //value.append(accessibilityNodeInfo.getChild(r).toString().contains("0")).append(accessibilityNodeInfo.getChild(r).toString().contains("1")).append(accessibilityNodeInfo.getChild(r).toString().contains("2")).append(accessibilityNodeInfo.getChild(r).toString().contains("3")).append(accessibilityNodeInfo.getChild(r).toString().contains("4")).append(accessibilityNodeInfo.getChild(r).toString().contains("5")).append(accessibilityNodeInfo.getChild(r).toString().contains("6")).append(accessibilityNodeInfo.getChild(r).toString().contains("7")).append(accessibilityNodeInfo.getChild(r).toString().contains("8")).append(accessibilityNodeInfo.getChild(r).toString().contains("9")).append("\n");
            value.append(accessibilityNodeInfo.getChild(r).getClassName().toString()).append("\n");
            value.append("--------------------\n");
            test2(accessibilityNodeInfo.getChild(r), n + 1, value);
        }
    }

    /*boolean click = false;
    boolean scroll = false;
    boolean finish = false;

    private void get_value1(AccessibilityNodeInfo accessibilityNodeInfo, int n, List<AccessibilityNodeInfo> text, List<AccessibilityNodeInfo> button) throws InterruptedException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            for (int r = 0; r < accessibilityNodeInfo.getChildCount(); r++) {
                if (accessibilityNodeInfo.getChild(r).getContentDescription() != null && accessibilityNodeInfo.getChild(r).getClassName().toString().equals("android.view.View") && !click) {
                    if (accessibilityNodeInfo.getChild(r).getContentDescription().toString().contains("อยู่ห่าง") &&
                            accessibilityNodeInfo.getChild(r).getContentDescription().toString().contains("กม.") &&
                            accessibilityNodeInfo.getChild(r).getContentDescription().toString().contains("฿")) {
                        text.add(accessibilityNodeInfo.getChild(r));
                    }
                    if (accessibilityNodeInfo.getChild(r).getContentDescription().toString().equals("รับงาน")) {
                        button.add(accessibilityNodeInfo.getChild(r));
                    }
                }
                get_value1(accessibilityNodeInfo.getChild(r), n + 1, text, button);
            }
        }
    }

    private void get_value2(AccessibilityNodeInfo accessibilityNodeInfo, int n, StringBuilder value) throws InterruptedException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            for (int r = 0; r < accessibilityNodeInfo.getChildCount(); r++) {
                if (accessibilityNodeInfo.getChild(r).getContentDescription() != null) {
                    value.append(accessibilityNodeInfo.getChild(r).getContentDescription().toString()).append("\n");
                }
                if (accessibilityNodeInfo.getChild(r).isScrollable()) {
                    value.append("#is scroll# ").append(accessibilityNodeInfo.getChild(r).isScrollable()).append("\n");
                }
                value.append(accessibilityNodeInfo.getChild(r).getClassName().toString()).append("\n");
                value.append("-------------------------").append("\n");

                get_value2(accessibilityNodeInfo.getChild(r), n + 1, value);
            }
        }
    }

    private void to_check(List<AccessibilityNodeInfo> text, List<AccessibilityNodeInfo> button) {
        if (text.size() == button.size() && text.size() > 0) {
            boolean[] check = {true};
            for (int child = 0; child < text.size(); child++) {
                String[] text_t = text.get(child).getContentDescription().toString().split("\n");
                AccessibilityNodeInfo button_b = button.get(child);
                if (is_shop_ban) {
                    for (String s : ban_shop) {
                        if (Objects.equals(s, text_t[0])) {
                            check[0] = false;
                            break;
                        }
                    }
                } else if (is_shop_like) {
                    for (String s : like_shop) {
                        if (Objects.equals(s, text_t[0])) {
                            break;
                        }
                    }
                }
                if (is_start_lock) {

                }
                if (is_end_lock) {

                }
                if (check[0]) {
                    to_click(is_click_job, button_b);
                    if (is_scroll_job) {
                        if (!scroll) {
                            scroll = true;
                            Rect rect = new Rect();
                            int i9 = Resources.getSystem().getDisplayMetrics().widthPixels;
                            rect.centerX();
                        }
                    }
                }
            }
        }
    }

    private void to_click(boolean is_click_job, AccessibilityNodeInfo button) {
        if (is_click_job) {
            if (!click) {
                click = true;
                Rect rect = new Rect();
                button.getBoundsInScreen(rect);
                String str = rect.toString();
                String r = str.split("]\\[")[0].replace("[", "").replace(",", " ");
                String r2 = str.split("-")[1].replace(")", "").replace(",", " ");
                String r3 = r.split("-")[0].replace("Rect(", "");
                int parseInt = Integer.parseInt(r3.split(" ")[0].trim());
                int parseInt2 = Integer.parseInt(r3.split("  ")[1].trim());
                str.split("-")[1].replace(")", "").replace(",", " ");
                String trim = r2.trim();
                int move_x = parseInt + new Random().nextInt((((Integer.parseInt(trim.split(" {2}")[0].trim()) - 20) - parseInt)) + 1);
                int move_y = parseInt2 + new Random().nextInt((((Integer.parseInt(trim.split(" {2}")[1].trim()) - 20) - parseInt2)) + 1);
                set_click(move_x, move_y);
                try {
                    sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void set_click(int x,int y) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Path path = new Path();
            path.moveTo(x, y);
            GestureDescription.Builder builder = new GestureDescription.Builder();
            GestureDescription gestureDescription = builder.addStroke(new GestureDescription.StrokeDescription(path, 0, 1)).build();
            dispatchGesture(gestureDescription, null, null);
        }
    }

    private void to_scroll() {
        List<AccessibilityNodeInfo> text = new ArrayList<>();
        List<AccessibilityNodeInfo> button = new ArrayList<>();
    }*/
}
