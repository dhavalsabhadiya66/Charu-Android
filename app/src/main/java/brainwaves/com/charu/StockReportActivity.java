package brainwaves.com.charu;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.HtmlTags;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import brainwaves.com.charu.Class.VolleyCallback;
import brainwaves.com.charu.Utils.Const;
import brainwaves.com.charu.Utils.Pref;
import de.hdodenhof.circleimageview.CircleImageView;

public class StockReportActivity extends AppCompatActivity {

    Toolbar toolBar;
    ImageView imgShare;
    TextView tvAction,tvNoData;
    RecyclerView rvList;
    LinearLayout llNoData, llHeader, llFilter;
    EditText etDate, etMainGroupName, etProdGroup, etProdName;
    Button btnSearch, btnClear;
    ArrayAdapter adapter;
    ArrayList<HashMap<String, String>> arrayhmMainGroup, arrayhmProdGroup, arrayhmProdName;
    ArrayList<String> arrayMainGroup, arrayProdGroup, arrayProdName;
    ArrayList<HashMap<String, String>> StockReportArray = new ArrayList<>();
    LinearLayoutManager lm;
    String Date = "", MainGroupName = "", ProdGrpCode = "", ProdCode = "";
    Context context = StockReportActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_report);

        Window window = this.getWindow();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar toolBar = findViewById(R.id.toolBar);
        toolBar.setTitle("");
        setSupportActionBar(toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        etDate = findViewById(R.id.etDate);
        etMainGroupName = findViewById(R.id.etMainGroupName);
        etProdName = findViewById(R.id.etProdName);
        etProdGroup = findViewById(R.id.etProdGroup);
        btnSearch = findViewById(R.id.btnSearch);
        btnClear = findViewById(R.id.btnClear);
        imgShare = findViewById(R.id.imgShare);

        llNoData = findViewById(R.id.llNoData);
        llHeader = findViewById(R.id.llHeader);
        llFilter = findViewById(R.id.llFilter);
        tvNoData = findViewById(R.id.tvNoData);
        rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(this));

        GetMainGroupList();
        GetProdGroupList();
        GetProductList();

        etDate.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePickerDialog();
            }
        });

        etDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    openDatePickerDialog();
                }
            }
        });

        etMainGroupName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    etMainGroupName.performClick();
                }
            }
        });

        etMainGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrayMainGroup.size() != 0) {
                    openSelectionMainGroup(etMainGroupName, arrayMainGroup);
                    etProdGroup.setText("");
                    etProdName.setText("");
                } else {
                    Const.showErrorDialog(context, "Main Group Name Not Available");
                }
            }
        });

        etProdGroup.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    etProdGroup.performClick();
                }
            }
        });

        etProdGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrayProdGroup.size() != 0) {
                    openSelectionProdGroup(etProdGroup, arrayProdGroup);
                    etProdName.setText("");
                } else {
                    Const.showErrorDialog(context, "Product Group Name Not Available");
                }
            }
        });

        etProdName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    etProdName.performClick();
                }
            }
        });

        etProdName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrayProdGroup.size() != 0) {
                    openSelectionProdName(etProdName, arrayProdName);
                } else {
                    Const.showErrorDialog(context, "Product Name Not Available");
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Const.isEmpty(etDate)) {
                    Const.showErrorDialog(context, "Please Select Date");
                } else {
                    if (isNetworkAvailable()) {
                        Intent i = new Intent(context, StockReportDetActivity.class);

                        Date = Const.isEmpty(etDate) ? "" : Const.dateForChange(etDate.getText().toString());

                        MainGroupName = Const.isEmpty(etMainGroupName) ? "" : Const.notNullString(etMainGroupName.getText().toString(),"");
                        ProdGrpCode = Const.isEmpty(etProdGroup) ? "" : Const.notNullString(arrayhmProdGroup.get(arrayProdGroup.indexOf(etProdGroup.getText().toString())).get("p_grp_code"),"");
                        ProdCode = Const.isEmpty(etProdName) ? "" : Const.notNullString(arrayhmProdName.get(arrayProdName.indexOf(etProdName.getText().toString())).get("product_code"),"");

                        i.putExtra("Date", Date);
                        i.putExtra("MainGroupName", MainGroupName);
                        i.putExtra("ProdGrpCode", ProdGrpCode);
                        i.putExtra("ProdCode", ProdCode);
                        startActivity(i);
                    }
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearForm();
            }
        });
    }

    private void openDatePickerDialog() {
        int dd, mm, yy;
        if (Const.isEmpty(etDate)) {
            dd = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            mm = Calendar.getInstance().get(Calendar.MONTH);
            yy = Calendar.getInstance().get(Calendar.YEAR);
        } else {
            String[] ddmmyy = etDate.getText().toString().trim().split("-");
            dd = Integer.parseInt(ddmmyy[0]);
            mm = Integer.parseInt(ddmmyy[1]) - 1;
            yy = Integer.parseInt(ddmmyy[2]);
        }
        DatePickerDialog date = new DatePickerDialog(context, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                Integer dd = datePicker.getDayOfMonth();
                String d = dd.toString().length() == 1 ? "0" + dd : dd + "";
                Integer mm = datePicker.getMonth() + 1;
                String m = mm.toString().length() == 1 ? "0" + mm : mm + "";
                etDate.setText(d + "-" + m + "-" + datePicker.getYear());
            }
        }, yy, mm, dd);
        // date.getDatePicker().setMinDate(new Date().getTime() - 10000);
        date.show();
    }

    private void GetMainGroupList() {
        Const.showProgress(context);
        arrayhmMainGroup = new ArrayList<>();
        arrayMainGroup = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put(Const.CompCode, Pref.getStringValue(context, Const.loginCompCode, ""));
        map.put("MainGrpName", "");

        Const.callPostApi(context, Const.BaseUrl + "MainGrpList?", map, new VolleyCallback() {
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
                                arrayhmMainGroup.add(hm);
                            }
                            for (int i = 0; i < arrayhmMainGroup.size(); i++) {
                                arrayMainGroup.add(arrayhmMainGroup.get(i).get("main_group"));
//                                if (Const.notNullString(arrayhmAccount.get(i).get("comp_code"), "").equalsIgnoreCase("1")) {
//                                    etAccountName.setText(arrayhmA.get(i).get("comp_name"));
//                                    /*// for disable selection
//                                    etCompany.setOnClickListener(null);*/
//                                }
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
                        GetMainGroupList();
                    }
                };
                Const.showErrorApiDialog(context, runnable);
            }
        });
    }

    private void GetProdGroupList() {
        Const.showProgress(context);
        arrayhmProdGroup = new ArrayList<>();
        arrayProdGroup = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("ProdGrpCode", "");
        map.put(Const.Status, "Y");
        Const.callPostApi(context, Const.BaseUrl + "ProductGrpList?", map, new VolleyCallback() {
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
                                arrayhmProdGroup.add(hm);
                            }
                            for (int i = 0; i < arrayhmProdGroup.size(); i++) {
                                arrayProdGroup.add(arrayhmProdGroup.get(i).get("pgrpcname"));
//                                if (Const.notNullString(arrayhmAccount.get(i).get("comp_code"), "").equalsIgnoreCase("1")) {
//                                    etAccountName.setText(arrayhmA.get(i).get("comp_name"));
//                                    /*// for disable selection
//                                    etCompany.setOnClickListener(null);*/
//                                }
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
                        GetProdGroupList();
                    }
                };
                Const.showErrorApiDialog(context, runnable);
            }
        });
    }

    private void GetProductList() {
        Const.showProgress(context);
        arrayhmProdName = new ArrayList<>();
        arrayProdName = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("ProductCode", "");
        map.put("ProdGrpCode", Const.isEmpty(etProdGroup) ? "" : Const.notNullString(arrayhmProdGroup.get(arrayProdGroup.indexOf(etProdGroup.getText().toString())).get("p_grp_code"),""));
        map.put("MainGrpName", "");
        map.put(Const.CompCode,  Pref.getStringValue(context, Const.loginCompCode, ""));
        map.put(Const.Status, "Y");
        Const.callPostApi(context, Const.BaseUrl + "ProductList?", map, new VolleyCallback() {
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
                                arrayhmProdName.add(hm);
                            }
                            for (int i = 0; i < arrayhmProdName.size(); i++) {
                                arrayProdName.add(arrayhmProdName.get(i).get("product_name"));
//                                if (Const.notNullString(arrayhmAccount.get(i).get("comp_code"), "").equalsIgnoreCase("1")) {
//                                    etAccountName.setText(arrayhmA.get(i).get("comp_name"));
//                                    /*// for disable selection
//                                    etCompany.setOnClickListener(null);*/
//                                }
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
                        GetProductList();
                    }
                };
                Const.showErrorApiDialog(context, runnable);
            }
        });
    }

    private void openSelectionMainGroup(final EditText editText, ArrayList<String> array) {
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
        tvTitle.setText("Select Main Group Name");
        if (array.size() != 0) {
            adapter = new ArrayAdapter<String>(context, R.layout.layout_spinner_item, array);
            listView.setAdapter(adapter);
        } else {
            adapter = new ArrayAdapter<String>(context, R.layout.layout_spinner_item, new ArrayList<String>());
            llNoData.setVisibility(View.VISIBLE);
            tvNoData.setText("Main Group Name Not Available");
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
                GetProdGroupList();
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

    private void openSelectionProdGroup(final EditText editText, ArrayList<String> array) {
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
        tvTitle.setText("Select Product Group");
        if (array.size() != 0) {
            adapter = new ArrayAdapter<String>(context, R.layout.layout_spinner_item, array);
            listView.setAdapter(adapter);
        } else {
            adapter = new ArrayAdapter<String>(context, R.layout.layout_spinner_item, new ArrayList<String>());
            llNoData.setVisibility(View.VISIBLE);
            tvNoData.setText("Product Group Not Available");
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
                GetProductList();
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

    private void openSelectionProdName(final EditText editText, ArrayList<String> array) {
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
        tvTitle.setText("Select Product Name");
        if (array.size() != 0) {
            adapter = new ArrayAdapter<String>(context, R.layout.layout_spinner_item, array);
            listView.setAdapter(adapter);
        } else {
            adapter = new ArrayAdapter<String>(context, R.layout.layout_spinner_item, new ArrayList<String>());
            llNoData.setVisibility(View.VISIBLE);
            tvNoData.setText("Product Name Not Available");
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
                GetProductList();
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

    private void clearForm() {
        etDate.setText("");
        etMainGroupName.setText("");
        etProdGroup.setText("");
        etProdName.setText("");
    }

    public Boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

