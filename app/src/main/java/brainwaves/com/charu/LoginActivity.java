package brainwaves.com.charu;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import brainwaves.com.charu.Class.CustomExceptionHandler;
import brainwaves.com.charu.Class.VolleyCallback;
import brainwaves.com.charu.Utils.Const;
import brainwaves.com.charu.Utils.Pref;

public class LoginActivity extends AppCompatActivity {

    TextView tvSignup, tvForgot, tvSkipLogin;
    EditText etUserName,etPassword,etCompany,etYear;
    ImageView imgShowHide;
    Button btnLogin;
    boolean isshow = false;
    ArrayList<HashMap<String, String>> arrayhmCompany, arrayhmYear, arrayLogin;
    ArrayList<String> arrayCompany, arrayYear;
    ArrayAdapter adapter;

    Context context = LoginActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this));
        }
        setContentView(R.layout.activity_login);

        tvForgot = (TextView) findViewById(R.id.tvForgot);
        tvSignup = (TextView) findViewById(R.id.tvSignup);
        tvSkipLogin = (TextView) findViewById(R.id.tvSkipLogin);
        etCompany = (EditText) findViewById(R.id.etCompany);
        etUserName = (EditText) findViewById(R.id.etUserName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etYear = (EditText) findViewById(R.id.etYear);
        imgShowHide = (ImageView) findViewById(R.id.imgShowHide);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        isStoragePermissionGranted();

        tvSignup.setText(Html.fromHtml("<font color='#336dff'><b><u>Register now</u></b></font>"));

        tvSkipLogin.setPaintFlags(tvSkipLogin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        imgShowHide.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!etPassword.getText().toString().equalsIgnoreCase("")) {
                    if (isshow) {
                        isshow = false;
                        etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                        imgShowHide.setImageResource(R.drawable.ic_hide);
                        etPassword.setSelection(etPassword.getText().length());
                    } else {
                        isshow = true;
                        etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        imgShowHide.setImageResource(R.drawable.ic_show);
                        etPassword.setSelection(etPassword.getText().length());
                    }
                } else {
                    if (isshow) {
                        isshow = false;
                        imgShowHide.setImageResource(R.drawable.ic_hide);
                    } else {
                        isshow = true;
                        imgShowHide.setImageResource(R.drawable.ic_show);
                    }
                }
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equalsIgnoreCase("")) {
                    etPassword.setTypeface(Const.setFontTypeface(LoginActivity.this));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tvSkipLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        etCompany.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    etCompany.performClick();
                }
            }
        });

        etCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrayCompany.size() != 0) {
                    openSelectionCompany(etCompany, arrayCompany);
                } else {
                    Const.showErrorDialog(context, "Company Not Found!");
                }
            }
        });

        etYear.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    etYear.performClick();
                }
            }
        });

        etYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrayYear.size() != 0) {
                    openSelectionYear(etYear, arrayYear);
                } else {
                    Const.showErrorDialog(context, "Year Not Found!");
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Const.isEmpty(etUserName)) {
                    Snackbar sb = Snackbar.make(view, "Please Enter User Name", Snackbar.LENGTH_LONG);
                    TextView tv = (TextView) (sb.getView()).findViewById(R.id.snackbar_text);
                    tv.setTypeface(ResourcesCompat.getFont(LoginActivity.this, R.font.average_sans));
                    sb.show();
                } else if (Const.isEmpty(etPassword)) {
                    Snackbar sb = Snackbar.make(view, "Please Enter Valid Password", Snackbar.LENGTH_LONG);
                    TextView tv = (TextView) (sb.getView()).findViewById(R.id.snackbar_text);
                    tv.setTypeface(ResourcesCompat.getFont(LoginActivity.this, R.font.average_sans));
                    sb.show();
                } else if (Const.isEmpty(etCompany)) {
                    Snackbar sb = Snackbar.make(view, "Please Select Company", Snackbar.LENGTH_LONG);
                    TextView tv = (TextView) (sb.getView()).findViewById(R.id.snackbar_text);
                    tv.setTypeface(ResourcesCompat.getFont(LoginActivity.this, R.font.average_sans));
                    sb.show();
                } else if (Const.isEmpty(etYear)) {
                    Snackbar sb = Snackbar.make(view, "Please Select Year", Snackbar.LENGTH_LONG);
                    TextView tv = (TextView) (sb.getView()).findViewById(R.id.snackbar_text);
                    tv.setTypeface(ResourcesCompat.getFont(LoginActivity.this, R.font.average_sans));
                    sb.show();
                } else {
                    Login();
                }
            }
        });

        getCompanyList();
        getYearList();
    }

    private void Login() {
        Const.showProgress(context);
        arrayLogin = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put(Const.UserName, etUserName.getText().toString().trim());
        map.put(Const.Password, etPassword.getText().toString().trim());
        map.put(Const.UDID, "");
        map.put(Const.DeviceType, "ANDROID");
        map.put(Const.CompCode,  Const.notNullString(arrayhmCompany.get(arrayCompany.indexOf(etCompany.getText().toString())).get("comp_code"),""));
        Const.callPostApi(context, Const.BaseUrl + "CheckLogin?", map, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                Const.dismissProgress();
                try {
                    JSONObject json = new JSONObject(result);
                    if (json.optString("STATUS").equalsIgnoreCase("1")) {
                        JSONArray array = json.optJSONArray("Data");
                        JSONObject object = array.optJSONObject(0);
                        Iterator<String> keys = object.keys();
                        HashMap<String, String> hm = new HashMap<>();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            hm.put(key.toLowerCase(), object.optString(key).trim());
                        }
                        Pref.setStringValue(context, Const.loginID, Const.notNullString(hm.get("userid"), ""));
                        Pref.setStringValue(context, Const.loginName, Const.notNullString(hm.get("username"), ""));
                        Pref.setStringValue(context, Const.loginPassword,  etPassword.getText().toString().trim());
                        Pref.setStringValue(context, Const.loginCompCode, Const.notNullString(hm.get("compcode"), ""));
                        Pref.setStringValue(context, Const.loginYearCode, Const.notNullString(arrayhmYear.get(arrayYear.indexOf(etYear.getText().toString())).get("year_code"),""));
                        Pref.setStringValue(context, Const.loginFromDate, Const.notNullString(arrayhmYear.get(arrayYear.indexOf(etYear.getText().toString())).get("from_date"),""));
                        Pref.setStringValue(context, Const.loginToDate, Const.notNullString(arrayhmYear.get(arrayYear.indexOf(etYear.getText().toString())).get("to_date"),""));
                        Pref.setStringValue(context, Const.loginRoleCode, Const.notNullString(hm.get("role_code"), ""));
                        Pref.setStringValue(context, Const.loginRoleName, Const.notNullString(hm.get("role_name"), ""));
                        Pref.setStringValue(context, Const.loginMobile, Const.notNullString(hm.get("mobile"), ""));
                        Pref.setStringValue(context, Const.loginAddress, Const.notNullString(hm.get("address"), ""));
                        Pref.setStringValue(context, Const.loginEmail, Const.notNullString(hm.get("email"), ""));
                        Pref.setStringValue(context, Const.loginToken, Const.notNullString(hm.get("tokenno"), ""));
                        startActivity(new Intent(context, HomeActivity.class));
                        finishAffinity();
                    } else {
                        Const.showErrorDialog(context, Const.notNullString(json.optString("Message"), "Something want wrong"));
                    }
                } catch (JSONException e) {
                    Const.showErrorDialog(context, "Something want wrong");
                }
            }

            @Override
            public void onFailerResponse(String error) {
                Const.dismissProgress();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        Login();
                    }
                };
                Const.showErrorApiDialog(context, runnable);
            }
        });
    }

    private void getCompanyList() {
        Const.showProgress(context);
        arrayhmCompany = new ArrayList<>();
        arrayCompany = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put(Const.CompCode, "");
        map.put(Const.CompGrpCode, "");
        map.put(Const.ShortName, "");
        map.put(Const.Status,"Y");
        Const.callPostApi(context, Const.BaseUrl + "CompanyList?", map, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optString("STATUS").equalsIgnoreCase("1")) {
                        JSONArray array = object.optJSONArray("Data");
                        if (array.length() != 0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object1 = array.optJSONObject(i);
                                Iterator<String> keys = object1.keys();
                                HashMap<String, String> hm = new HashMap<>();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    hm.put(key.toLowerCase(), object1.optString(key).trim());
                                }
                                arrayhmCompany.add(hm);
                            }
                            for (int i = 0; i < arrayhmCompany.size(); i++) {
                                arrayCompany.add(arrayhmCompany.get(i).get("comp_name"));
                                if (Const.notNullString(arrayhmCompany.get(i).get("comp_code"), "").equalsIgnoreCase("2")) {
                                    etCompany.setText(arrayhmCompany.get(i).get("comp_name"));
                                    /*// for disable selection
                                    etCompany.setOnClickListener(null);*/
                                }
                            }
                        } else {
                            Const.showErrorDialog(context, object.optString("Message"));
                        }
                    } else {
                        Const.showErrorDialog(context, object.optString("Message"));
                    }
                } catch (JSONException e) {
                    Const.showErrorDialog(context, "Something want wrong");
                }
                Const.dismissProgress();
            }

            @Override
            public void onFailerResponse(String error) {
                Const.dismissProgress();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        getCompanyList();
                    }
                };
                Const.showErrorApiDialog(context, runnable);
            }
        });
    }

    private void getYearList() {
        Const.showProgress(context);
        arrayhmYear = new ArrayList<>();
        arrayYear = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put(Const.YearCode, "");
        Const.callPostApi(context, Const.BaseUrl + "YearList?", map, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optString("STATUS").equalsIgnoreCase("1")) {
                        JSONArray array = object.optJSONArray("Data");
                        if (array.length() != 0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object1 = array.optJSONObject(i);
                                Iterator<String> keys = object1.keys();
                                HashMap<String, String> hm = new HashMap<>();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    hm.put(key.toLowerCase(), object1.optString(key).trim());
                                }
                                arrayhmYear.add(hm);
                            }
                            for (int i = 0; i < arrayhmYear.size(); i++) {
                                arrayYear.add(arrayhmYear.get(i).get("year_name"));
                                if (Const.notNullString(arrayhmYear.get(i).get("year_code"), "").equalsIgnoreCase("20")) {
                                    etYear.setText(arrayhmYear.get(i).get("year_name"));
                                    /*// for disable selection
                                    etCompany.setOnClickListener(null);*/
                                }
                            }
                        } else {
                            Const.showErrorDialog(context, object.optString("Message"));
                        }
                    } else {
                        Const.showErrorDialog(context, object.optString("Message"));
                    }
                } catch (JSONException e) {
                    Const.showErrorDialog(context, "Something want wrong");
                }
                Const.dismissProgress();
            }

            @Override
            public void onFailerResponse(String error) {
                Const.dismissProgress();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        getYearList();
                    }
                };
                Const.showErrorApiDialog(context, runnable);
            }
        });
    }

    private void openSelectionCompany(final EditText editText, ArrayList<String> array) {
        final Dialog dialog = new Dialog(context, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.getWindow().setBackgroundDrawable(Const.setBackgoundBorder(0, 20, Color.WHITE, Color.WHITE));
        dialog.setContentView(R.layout.layout_selection_dialog);

        LinearLayout llNoData = dialog.findViewById(R.id.llNoData);
        TextView tvNoData = dialog.findViewById(R.id.tvNoData);
        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        final EditText etSearch = dialog.findViewById(R.id.etSearch);
        Button btnClear = dialog.findViewById(R.id.btnClear);
        final ListView listView = dialog.findViewById(R.id.listView);
        tvTitle.setText("Select Company");
        if (array.size() != 0) {
            adapter = new ArrayAdapter<String>(context, R.layout.layout_spinner_item, array);
            listView.setAdapter(adapter);
        } else {
            adapter = new ArrayAdapter<String>(context, R.layout.layout_spinner_item, new ArrayList<String>());
            llNoData.setVisibility(View.VISIBLE);
            tvNoData.setText("Company Not Available");
        }

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
                dialog.dismiss();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                editText.setText(listView.getItemAtPosition(i).toString());
                dialog.dismiss();
            }
        });

        etSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (etSearch.getCompoundDrawables()[2] != null) {
                        if (event.getRawX() >= (etSearch.getRight() - etSearch.getCompoundDrawables()[2].getBounds().width())) {
                            etSearch.setText("");
                            etSearch.setCompoundDrawables(null, null, null, null);
                            return true;
                        }
                    }
                }
                return false;
            }
        });


        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().equalsIgnoreCase("")) {
                    Drawable wrappedDrawable = DrawableCompat.wrap(AppCompatResources.getDrawable(context, R.drawable.ic_close));
                    DrawableCompat.setTint(wrappedDrawable, Color.BLACK);
                    etSearch.setCompoundDrawablesWithIntrinsicBounds(null, null, wrappedDrawable, null);
                } else {
                    etSearch.setCompoundDrawables(null, null, null, null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                adapter.getFilter().filter(editable.toString());
            }
        });

        dialog.show();
    }

    private void openSelectionYear(final EditText editText, ArrayList<String> array) {
        final Dialog dialog = new Dialog(context, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.getWindow().setBackgroundDrawable(Const.setBackgoundBorder(0, 20, Color.WHITE, Color.WHITE));
        dialog.setContentView(R.layout.layout_selection_dialog);

        LinearLayout llNoData = dialog.findViewById(R.id.llNoData);
        TextView tvNoData = dialog.findViewById(R.id.tvNoData);
        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        final EditText etSearch = dialog.findViewById(R.id.etSearch);
        Button btnClear = dialog.findViewById(R.id.btnClear);
        final ListView listView = dialog.findViewById(R.id.listView);
        tvTitle.setText("Select Year");
        if (array.size() != 0) {
            adapter = new ArrayAdapter<String>(context, R.layout.layout_spinner_item, array);
            listView.setAdapter(adapter);
        } else {
            adapter = new ArrayAdapter<String>(context, R.layout.layout_spinner_item, new ArrayList<String>());
            llNoData.setVisibility(View.VISIBLE);
            tvNoData.setText("Year Not Available");
        }

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
                dialog.dismiss();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                editText.setText(listView.getItemAtPosition(i).toString());
                dialog.dismiss();
            }
        });

        etSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (etSearch.getCompoundDrawables()[2] != null) {
                        if (event.getRawX() >= (etSearch.getRight() - etSearch.getCompoundDrawables()[2].getBounds().width())) {
                            etSearch.setText("");
                            etSearch.setCompoundDrawables(null, null, null, null);
                            return true;
                        }
                    }
                }
                return false;
            }
        });


        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().equalsIgnoreCase("")) {
                    Drawable wrappedDrawable = DrawableCompat.wrap(AppCompatResources.getDrawable(context, R.drawable.ic_close));
                    DrawableCompat.setTint(wrappedDrawable, Color.BLACK);
                    etSearch.setCompoundDrawablesWithIntrinsicBounds(null, null, wrappedDrawable, null);
                } else {
                    etSearch.setCompoundDrawables(null, null, null, null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                adapter.getFilter().filter(editable.toString());
            }
        });

        dialog.show();
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE )
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED){

                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation

            return true;
        }
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
