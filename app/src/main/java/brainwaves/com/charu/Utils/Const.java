package brainwaves.com.charu.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ImageSpan;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import brainwaves.com.charu.Class.CustomDialog;
import brainwaves.com.charu.Class.MySingleton;
import brainwaves.com.charu.Class.VolleyCallback;
import brainwaves.com.charu.ModelClass.ModelGallery;
import brainwaves.com.charu.R;


public class Const {

    public static String APP_NAME = "SUNRISE!";
    public static final String PREF_FILE = APP_NAME + "_PREF";
    public static CustomDialog custDailog = null;
    public static HashMap<String, Activity> screen_al;
    public static List<ModelGallery> fullscreenImage;

    public static String loginID = "loginID", loginName = "loginName";

    // WebService :-
           public static String BaseUrl = "http://43.240.9.153:1522/webservice.asmx/";
//    public static String BaseUrl = "http://103.218.110.11:1522/webservice.asmx/";


    // Parameters :-
    public static String UserName = "UserName", Password = "Password", UDID = "UDID", DeviceType = "DeviceType", CompCode = "CompCode",
            loginPassword = "loginPassword", loginCompCode = "loginCompCode", loginToken="loginToken",AccountCode = "AccountCode",
            CompGrpCode = "CompGrpCode",loginRoleName = "loginRoleName", loginRoleCode = "loginRoleCode", loginAddress = "loginAddress",
            loginMobile = "loginMobile", loginEmail = "loginEmail", loginCategory = "loginCategory", loginBusiness = "loginBusiness",
            loginMemberCode = "loginMemberCode",  Islogin = "islogin", TokenNo = "TokenNo", loginGroupName = "loginGroupName",
            loginAadharNo = "loginAadharNo", loginAadharImage = "loginAadharImage", AboutName = "AboutName", AboutMobile = "AboutMobile",
            AboutEmail = "AboutEmail", Status ="Status", TransType ="TransType", PlanCode="PlanCode", SuppCode="SuppCode",
            CateCode="CateCode",WorkCode="WorkCode",EmpCode="EmpCode",HospitalCode="HospitalCode",CustDetCode="CustDetCode",
            FromDate="FromDate", ToDate="ToDate", ShortName = "ShortName", YearCode = "YearCode", AccountName = "AccountName", Date = "Date",
            loginYearCode = "loginYearCode", loginFromDate = "loginFromDate", loginToDate = "loginToDate", GroupDetCode = "GroupDetCode",
            PartyCode = "PartyCode", PageNo = "PageNo";

    public static boolean isValidEmail(EditText email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email.getText().toString());
        return !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches();
    }

    public static boolean isValidMobile(EditText view) {
        return view == null || view.getText().toString().trim().length() <= 9;
    }

    public static boolean isEmpty(EditText view) {
        return view == null || view.getText().toString().trim().length() == 0;
    }

    public static void showErrorApiDialog(final Context context, final Runnable runnable) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Network connection error, Required Network");
        alertDialog.setButton("RETRY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                new Handler().post(runnable);
            }
        });
        alertDialog.show();
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dialogInterface.dismiss();
                ((Activity) context).finish();
            }
        });
        alertDialog.getButton(alertDialog.BUTTON1).setTextColor(Color.BLACK);
    }

    public static void connectionErrorCustomDialog(Context context, final Runnable function) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_error_dialog);

        TextView tvMessage = (TextView) dialog.findViewById(R.id.tvMessage);
        tvMessage.setText("Network connection error, Required Network");

        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
        btnOk.setText("Retry");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().post(function);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void errorCustomDialog(Context context, String message) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_error_dialog);

        TextView tvMessage = (TextView) dialog.findViewById(R.id.tvMessage);
        tvMessage.setText(message);

        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void showErrorDialog(final Context context, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.Theme_Dialog).create();
        alertDialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        alertDialog.getWindow().setBackgroundDrawable(setBackgoundBorder(0, 20, Color.WHITE, Color.WHITE));
        alertDialog.setTitle(R.string.app_name_home);
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
        ((TextView) alertDialog.findViewById(android.R.id.message)).setTypeface(ResourcesCompat.getFont(context, R.font.average_sans));
        alertDialog.getButton(alertDialog.BUTTON1).setTextColor(Color.BLACK);
    }


    public static String notNullString(String fieldName, String hintValue) {
        String filanlValue = "";
        filanlValue = fieldName == null ? hintValue : fieldName.equalsIgnoreCase("null") ? hintValue : fieldName.equalsIgnoreCase("") ? hintValue : fieldName;
        return filanlValue;
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Typeface setFontTypeface(Context context) {
        return ResourcesCompat.getFont(context, R.font.average_sans);
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public static GradientDrawable setBackgoundRightBorder(int width, int radius, int backcolor, int colorcorner) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setColor(backcolor);
        gd.setStroke(width, colorcorner);
        gd.setCornerRadii(new float[]{0, radius, radius, radius, radius, radius, radius, 0});
        return gd;
    }

    public static GradientDrawable setBackgoundLeftBorder(int width, int radius, int backcolor, int colorcorner) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setColor(backcolor);
        gd.setStroke(width, colorcorner);
        gd.setCornerRadii(new float[]{radius, radius, radius, 0, 0, radius, radius, radius});
        return gd;
    }

    public static void closeAllScreens(String key) {

        if (screen_al == null || screen_al.size() <= 0)
            return;
        if (key != null && !key.equalsIgnoreCase("")) {
            if (screen_al.get(key) != null)
                screen_al.get(key).finish();
        } else {
            for (Iterator<Map.Entry<String, Activity>> it = screen_al
                    .entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Activity> entry = it.next();

                if (entry.getValue() != null) {
                    entry.getValue().finish();
                    it.remove();
                }
            }
        }
    }

    public static void addActivities(String key, Activity _activity) {
        if (screen_al == null)
            screen_al = new HashMap<String, Activity>();
        if (_activity != null)
            screen_al.put(key, _activity);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED || connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
                return true;
            } else {
            }
        } catch (Exception e) {
            ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting())
                return true;
            else
                return false;
        }
        return false;
    }

    public static void clearForm(ViewGroup group) {
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText) view).setText("");
            }
            if (view instanceof ViewGroup && (((ViewGroup) view).getChildCount() > 0))
                clearForm((ViewGroup) view);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static class InputFilterMinMax implements InputFilter {
        private int min = 0, max = 0;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public InputFilterMinMax(String min, String max) {
            this.min = Integer.parseInt(min);
            this.max = Integer.parseInt(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) {
            }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }

    public static void justify(final TextView textView) {
        //textView.setText(new SpannableString(textView.getText()));
        //Const.justify(textView);
        final AtomicBoolean isJustify = new AtomicBoolean(false);
        final String textString = textView.getText().toString();
        final TextPaint textPaint = textView.getPaint();
        final SpannableStringBuilder builder = new SpannableStringBuilder();
        textView.post(new Runnable() {
            @Override
            public void run() {
                if (!isJustify.get()) {
                    final int lineCount = textView.getLineCount();
                    final int textViewWidth = textView.getWidth();
                    for (int i = 0; i < lineCount; i++) {
                        int lineStart = textView.getLayout().getLineStart(i);
                        int lineEnd = textView.getLayout().getLineEnd(i);
                        String lineString = textString.substring(lineStart, lineEnd);
                        if (i == lineCount - 1) {
                            builder.append(new SpannableString(lineString));
                            break;
                        }
                        String trimSpaceText = lineString.trim();
                        String removeSpaceText = lineString.replaceAll(" ", "");
                        float removeSpaceWidth = textPaint.measureText(removeSpaceText);
                        float spaceCount = trimSpaceText.length() - removeSpaceText.length();
                        float eachSpaceWidth = (textViewWidth - removeSpaceWidth) / spaceCount;
                        SpannableString spannableString = new SpannableString(lineString);
                        for (int j = 0; j < trimSpaceText.length(); j++) {
                            char c = trimSpaceText.charAt(j);
                            if (c == ' ') {
                                Drawable drawable = new ColorDrawable(0x00ffffff);
                                drawable.setBounds(0, 0, (int) eachSpaceWidth, 0);
                                ImageSpan span = new ImageSpan(drawable);
                                spannableString.setSpan(span, j, j + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                        builder.append(spannableString);
                    }
                    textView.setText(builder);
                    isJustify.set(true);
                }
            }
        });
    }

    public static String dateForChange(String ddmmyyyy) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String str = "";
        try {
            Date date = inputFormat.parse(ddmmyyyy);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static boolean compareDate(String selectedDate, String otherDate) {
        boolean b = false;
        Date strDate = null;
        try {
            strDate = new SimpleDateFormat("dd-MM-yyyy").parse(selectedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (timeTomss(otherDate) > strDate.getTime()) {
            b = true;
        } else {
            b = false;
        }
        return b;
    }

    public static long timeTomss(String ddmmmyy) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        long timeInMilliseconds = 0;
        try {
            Date mDate = sdf.parse(ddmmmyy);
            timeInMilliseconds = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }


    public static GradientDrawable setBackgoundBorder(int width, int radius, int backcolor, int colorcorner) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setColor(backcolor);
        gd.setStroke(width, colorcorner);
        String radiuss = radius + 0f + "";
        gd.setCornerRadius(Float.parseFloat(radiuss));
        return gd;
    }

    public static void showProgress(Context context) {
        try {
            if (Const.custDailog != null && Const.custDailog.isShowing())
                Const.custDailog.dismiss();

            if (Const.custDailog == null)
                Const.custDailog = new CustomDialog(context);
            Const.custDailog.setCancelable(false);
            Const.custDailog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismissProgress() {
        if (Const.custDailog != null && Const.custDailog.isShowing())
            Const.custDailog.dismiss();
        Const.custDailog = null;
    }

    public static void callGetApi(Context context, String url, final VolleyCallback callback) {
        final StringRequest stringReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = response.replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();
                response = Html.fromHtml(response).toString();
                callback.onSuccessResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailerResponse("Connection Error");
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };
        stringReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(context).addToRequestQueue(stringReq);
    }

    public static void callPostApi(Context context, String url, final Map<String, String> params, final VolleyCallback callback) {
        final StringRequest stringReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = response.replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();
                response = Html.fromHtml(response).toString();
                callback.onSuccessResponse(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailerResponse("Connection Error");
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

        };
        Volley.newRequestQueue(context).add(stringReq.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));

    }

}