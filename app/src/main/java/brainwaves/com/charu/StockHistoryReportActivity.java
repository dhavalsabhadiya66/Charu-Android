package brainwaves.com.charu;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.DrawableCompat;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.HtmlTags;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import com.itextpdf.text.pdf.PdfWriter;

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

public class StockHistoryReportActivity extends AppCompatActivity {

    Toolbar toolBar;
    StockHistoryReportAdp adp;
    ImageView imgShare;
    TextView tvAction,tvNoData;
    RecyclerView rvList;
    LinearLayout llNoData, llHeader, llFilter;
    EditText etFromDate, etToDate, etProdGroup, etProdName;
    Button btnSearch, btnClear;
    ArrayList<HashMap<String, String>> arrayhmProdGroup, arrayhmProdName;
    ArrayList<String>  arrayProdGroup, arrayProdName;
    ArrayList<HashMap<String, String>> StockHistoryReportArray = new ArrayList<>();
    String Encoded;
    ArrayAdapter adapter;
    TextView tvTotalInward,tvTotalOutward,tvTotalBal;
    LinearLayout rvTotal;
    double totalInward= 0;
    double totalOutward= 0;
    double totalBalance= 0;
    String type = "";
    Context context = StockHistoryReportActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_history_report);

        Toolbar toolBar = findViewById(R.id.toolBar);
        toolBar.setTitle("");
        setSupportActionBar(toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        etProdGroup = findViewById(R.id.etProdGroup);
        etProdName = findViewById(R.id.etProdName);
        etFromDate = findViewById(R.id.etFromDate);
        etToDate = findViewById(R.id.etToDate);
        btnSearch = findViewById(R.id.btnSearch);
        btnClear = findViewById(R.id.btnClear);
        imgShare = findViewById(R.id.imgShare);
        tvTotalInward = findViewById(R.id.tvTotalInward);
        tvTotalOutward = findViewById(R.id.tvTotalOutward);
        tvTotalBal = findViewById(R.id.tvTotalBal);
        rvTotal = findViewById(R.id.rvTotal);

        llNoData = findViewById(R.id.llNoData);
        llHeader = findViewById(R.id.llHeader);
        llFilter = findViewById(R.id.llFilter);
        tvNoData = findViewById(R.id.tvNoData);
        rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(this));

        GetProdGroupList();
        GetProductList();

        etFromDate.setText(Pref.getStringValue(context, Const.loginFromDate, ""));
        etToDate.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));

        etProdGroup.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    etProdGroup.performClick();
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

        etFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFromDatePickerDialog();
            }
        });

        etToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openToDatePickerDialog();
            }
        });


        etFromDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    openFromDatePickerDialog();
                }
            }
        });
        etToDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    openToDatePickerDialog();
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Const.isEmpty(etFromDate)) {
                    Const.showErrorDialog(context, "Please Select From Date");
                } else if (Const.isEmpty(etToDate)) {
                    Const.showErrorDialog(context, "Please Select To Date");
                } else if (Const.isEmpty(etProdGroup)) {
                    Const.showErrorDialog(context, "Please Select Product Group Name");
                } else {
                    getStockHistoryReport();
                }
            }
        });

        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    File newFile = CreatePdf();
                    Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                    if (newFile.exists()) {
                        intentShareFile.setType("application/pdf");
                        Uri uri1 = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", newFile);
                        intentShareFile.putExtra(Intent.EXTRA_STREAM, uri1);

                        intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                                "Sharing File...");
                        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

                        startActivity(Intent.createChooser(intentShareFile, "Share File"));
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // GetReportIntoArrayList();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearForm();
            }
        });
    }

    private void getStockHistoryReport() {
        Const.showProgress(this);
        StockHistoryReportArray = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put(Const.CompCode, Pref.getStringValue(context, Const.loginCompCode, ""));
        map.put(Const.FromDate,Const.isEmpty(etFromDate) ? "" : Const.dateForChange(etFromDate.getText().toString()));
        map.put(Const.ToDate,Const.isEmpty(etToDate) ? "" : Const.dateForChange(etToDate.getText().toString()));
        map.put(Const.YearCode, Pref.getStringValue(context, Const.loginYearCode, ""));
        map.put("ProdGrpCode", Const.isEmpty(etProdGroup) ? "" : Const.notNullString(arrayhmProdGroup.get(arrayProdGroup.indexOf(etProdGroup.getText().toString())).get("p_grp_code"),""));
        map.put("ProdCode", Const.isEmpty(etProdName) ? "" : Const.notNullString(arrayhmProdName.get(arrayProdName.indexOf(etProdName.getText().toString())).get("product_code"),""));

        Const.callPostApi(context, Const.BaseUrl + "GetStockHistoryReport?", map, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                Const.dismissProgress();
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optString("STATUS").equalsIgnoreCase("1")) {
                        JSONArray array = object.optJSONArray("Data");
                        if (array.length() != 0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object1 = array.optJSONObject(i);
                                Log.d("arrayDetails", array.toString());
                                Iterator<String> keys = object1.keys();
                                HashMap<String, String> hm = new HashMap<>();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    hm.put(key.toLowerCase(), object1.optString(key).trim());
                                }
                                StockHistoryReportArray.add(hm);
                            }
                            adp = new StockHistoryReportAdp(context, StockHistoryReportArray);
                            rvList.setAdapter(adp);
                            adp.notifyDataSetChanged();
                            llHeader.setVisibility(View.VISIBLE);
                            rvTotal.setVisibility(View.VISIBLE);
                            llNoData.setVisibility(View.GONE);
                        } else {
                            llHeader.setVisibility(View.GONE);
                            rvTotal.setVisibility(View.GONE);
                            llNoData.setVisibility(View.VISIBLE);
                            tvNoData.setText("Data Not Available");
                            rvList.setAdapter(null);
                        }
                    }else {
                        llHeader.setVisibility(View.GONE);
                        rvTotal.setVisibility(View.GONE);
                        llNoData.setVisibility(View.VISIBLE);
                        tvNoData.setText("Data Not Available");
                        rvList.setAdapter(null);
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
                        getStockHistoryReport();
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

    private void openFromDatePickerDialog() {
        int dd, mm, yy;
        if (Const.isEmpty(etFromDate)) {
            dd = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            mm = Calendar.getInstance().get(Calendar.MONTH);
            yy = Calendar.getInstance().get(Calendar.YEAR);
        } else {
            String[] ddmmyy = etFromDate.getText().toString().trim().split("-");
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
                etFromDate.setText(d + "-" + m + "-" + datePicker.getYear());
            }
        }, yy, mm, dd);
        // date.getDatePicker().setMinDate(new Date().getTime() - 10000);
        date.show();
    }

    private void openToDatePickerDialog() {
        int dd, mm, yy;
        if (Const.isEmpty(etToDate)) {
            dd = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            mm = Calendar.getInstance().get(Calendar.MONTH);
            yy = Calendar.getInstance().get(Calendar.YEAR);
        } else {
            String[] ddmmyy = etToDate.getText().toString().trim().split("-");
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
                etToDate.setText(d + "-" + m + "-" + datePicker.getYear());
            }
        }, yy, mm, dd);
        // date.getDatePicker().setMinDate(new Date().getTime() - 10000);
        date.show();
    }

    private void clearForm() {
        etProdGroup.setText("");
        etProdName.setText("");
        etFromDate.setText("");
        etToDate.setText("");
    }

    private class StockHistoryReportAdp extends RecyclerView.Adapter<StockHistoryReportAdp.RecyclerViewHolder> {

        ArrayList<HashMap<String, String>> arraylist;
        Context context;

        public StockHistoryReportAdp(Context context, ArrayList<HashMap<String, String>> arraylist) {
            this.arraylist = arraylist;
            this.context = context;
        }

        @Override
        public StockHistoryReportAdp.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_stock_history_report, parent, false);
            StockHistoryReportAdp.RecyclerViewHolder viewHolder = new StockHistoryReportAdp.RecyclerViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final StockHistoryReportAdp.RecyclerViewHolder holder, final int position) {

            final HashMap<String, String> map = arraylist.get(position);

            holder.tvSerialNo.setText((position + 1) + ".");

            holder.tvInvoiceDate.setText(Const.notNullString(map.get("invoice_date"), "-"));
            holder.tvType.setText(Const.notNullString(map.get("proc_name"), "-"));
            holder.tvAccountName.setText(Const.notNullString(map.get("ac_description"), "-"));
            holder.tvProductGroup.setText(Const.notNullString(map.get("stone_type_name"), "-"));
            holder.tvProductName.setText(Const.notNullString(map.get("cate_name"), "-"));
            holder.tvInQty.setText(Const.notNullString(map.get("plus_qty"), "0"));
            holder.tvOutQty.setText(Const.notNullString(map.get("less_qty"), "0"));
            holder.tvBalQty.setText(Const.notNullString(map.get("r_bal_qty"), "0"));

            DecimalFormat df = new DecimalFormat("#,##,###.00");

            if (!map.get("plus_rate").equalsIgnoreCase("null") && !map.get("plus_rate").equalsIgnoreCase("0")) {
                holder.tvInRate.setText(String.valueOf(df.format(Double.parseDouble(map.get("plus_rate")))));
            }else{
                holder.tvInRate.setText("-");
            }

            if (!map.get("less_rate").equalsIgnoreCase("null") && !map.get("less_rate").equalsIgnoreCase("0")) {
                holder.tvOutRate.setText(String.valueOf(df.format(Double.parseDouble(map.get("less_rate")))));
            }else{
                holder.tvOutRate.setText("-");
            }

            if (!map.get("r_bal_stock_rate").equalsIgnoreCase("null") && !map.get("r_bal_stock_rate").equalsIgnoreCase("0")) {
                holder.tvBalRate.setText(String.valueOf(df.format(Double.parseDouble(map.get("r_bal_stock_rate")))));
            }else{
                holder.tvBalRate.setText("-");
            }

            totalInward =0;
            totalOutward =0;
            totalBalance =0;

            for (int i = 0; i < arraylist.size(); i++) {
                if (!arraylist.get(i).get("plus_qty").equalsIgnoreCase("null")) {
                    totalInward += Double.parseDouble(arraylist.get(i).get("plus_qty"));
                }
                if (!arraylist.get(i).get("less_qty").equalsIgnoreCase("null")) {
                    totalOutward += Double.parseDouble(arraylist.get(i).get("less_qty"));
                }
                if (!arraylist.get(i).get("r_bal_qty").equalsIgnoreCase("null")) {
                    totalBalance += Double.parseDouble(arraylist.get(i).get("r_bal_qty"));
                }
            }
            tvTotalInward.setText("In.Qty " + new DecimalFormat("#,##,###").format(totalInward));
            tvTotalOutward.setText("Out.Qty " + new DecimalFormat("#,##,###").format(totalOutward));
            tvTotalBal.setText("Bal.Qty " + new DecimalFormat("#,##,###").format(totalBalance));
        }

        @Override
        public int getItemCount() {
            return arraylist.size();
        }

        public class RecyclerViewHolder extends RecyclerView.ViewHolder {

            TextView tvSerialNo, tvInvoiceDate, tvType, tvAccountName, tvProductGroup, tvProductName,
                    tvInQty, tvInRate, tvOutQty, tvOutRate, tvBalQty, tvBalRate;
            public RecyclerViewHolder(View v) {
                super(v);

                tvSerialNo = v.findViewById(R.id.tvSerialNo);
                tvInvoiceDate = v.findViewById(R.id.tvInvoiceDate);
                tvType = v.findViewById(R.id.tvType);
                tvAccountName = v.findViewById(R.id.tvAccountName);
                tvProductGroup = v.findViewById(R.id.tvProductGroup);
                tvProductName = v.findViewById(R.id.tvProductName);
                tvInQty = v.findViewById(R.id.tvInQty);
                tvInRate = v.findViewById(R.id.tvInRate);
                tvOutQty = v.findViewById(R.id.tvOutQty);
                tvOutRate = v.findViewById(R.id.tvOutRate);
                tvBalQty = v.findViewById(R.id.tvBalQty);
                tvBalRate = v.findViewById(R.id.tvBalRate);
            }
        }
    }

    private File CreatePdf() throws FileNotFoundException, DocumentException {

        try {
            String root = context.getExternalFilesDir(null).toString();
            File myDir = new File(root + "/SPARKLE_Report");
            if (!myDir.exists()) {
                myDir.mkdirs();
            }

            SimpleDateFormat s = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
            String format = s.format(new Date());

            String fname = format + ".pdf";
            final File file = new File(root, fname);

            String Header = "Stock History Report";

            String data = "";
            data+="<TABLE border=\"1\">";
            data +="<TR>";
            data +="<TD>";
            data+="<TABLE border=\"0\">";
            data +="<TR>";
            data+="<TH align='center' style=\"font-size:18pt;font-family:'Open Sans';\">" +  " CHARU INDUSTRIES " + "</TH>";
            data +="</TR>";
            data +="<TR>";
            data +="<TH align='center' style=\"font-size:10pt;font-family:'Open Sans';\"> P-890, <br/> New GIDC, Old GIDC, <br/> Surat-395008.Gujarat-India.<br /></TH>";
            data +="</TR>";
            // data += "<TR> <TH align='center' style=\"white-space: nowrap;\">" + Header + "</TH> </TR>";
            data+="</TABLE>";
            data +="</TD>";
            data +="</TR>";
            data+="</TABLE>";
            data += "<TABLE border=\"1\">";

            data += "<TR><td align='center' style=\"font-size:15pt;font-family:'Open Sans';\" colspan=\"12\"><b>" + Header + "</b></td>";
            data += "</TR>";

            data += "<TR bgcolor=\"#E4DDC2\">";

            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Sr.No</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Invoice Date</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Type</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Account Name</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Product Group</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Product Name</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Inward Qty</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Inward Rate(₹)</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Outward Qty</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Outward Rate(₹)</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Balance Qty</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Balance Rate(₹)</b></TH>";
            data += "</TR>";

            for (int i = 0; i < StockHistoryReportArray.size(); i++) {
                String SrNo = "";
                String InvoiceDate = "";
                String Type = "";
                String AccountName = "";
                String ProductGroup = "";
                String ProductName = "";
                String InwardQty = "";
                String InwardRate = "";
                String OutwardQty = "";
                String OutwardRate = "";
                String BalanceQty = "";
                String BalanceRate = "";

                final HashMap<String, String> map = StockHistoryReportArray.get(i);

                SrNo = ((i + 1) + ".");
                InvoiceDate = (Const.notNullString(map.get("invoice_date"), "-"));
                Type = (Const.notNullString(map.get("proc_name"), "-"));
                AccountName = (Const.notNullString(map.get("ac_description"), "-"));
                ProductGroup = (Const.notNullString(map.get("stone_type_name"), "-"));
                ProductName = (Const.notNullString(map.get("cate_name"), "-"));
                InwardQty = (Const.notNullString(map.get("plus_qty"),"-"));
                InwardRate = (Const.notNullString(map.get("plus_rate"), "-"));
                OutwardQty = (Const.notNullString(map.get("less_qty"), "-"));
                OutwardRate = (Const.notNullString(map.get("less_rate"), "-"));
                BalanceQty = (Const.notNullString(map.get("r_bal_qty"), "-"));
                BalanceRate = (Const.notNullString(map.get("r_bal_stock_rate"), "-"));

                data += "<TR>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + SrNo + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + InvoiceDate + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + Type + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + AccountName + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + ProductGroup + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + ProductName + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + InwardQty + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + InwardRate + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + OutwardQty + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + OutwardRate + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + BalanceQty + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + BalanceRate + "</TD>";

            }
            data += "<TR>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>Total</b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>" + tvTotalInward.getText().toString() + "</b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>" + tvTotalOutward.getText().toString() + "</b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>" + tvTotalBal.getText().toString() + "</b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>/TR>";

            data += "</TABLE>";


            final String rpt = "<html>\n" +
                    "\n" +
                    "<head>\n" +
                    "\n" +
                    "</head>\n" +
                    "\n" +
                    "<body style=\"font-family:'Open Sans';\">\n" +
                    "\n" + data + "\n" +

                    "</body>\n" +
                    "</html>";

            OutputStream file1 = new FileOutputStream(file);

            com.itextpdf.text.Document document = new com.itextpdf.text.Document(PageSize.ARCH_D, 5f, 5f, 5f, 5f);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, file1);
            document.open();

            StyleSheet styles = new StyleSheet();

            styles.loadTagStyle(HtmlTags.TD, HtmlTags.FONTSIZE, "12");

            HTMLWorker htmlWorker = new HTMLWorker(document);

            StyleSheet css = new StyleSheet();

            css.loadStyle("body", "font-family", "verdana");
            css.loadStyle("body", "font-size", "3px");
            HTMLWorker.parseToList(new StringReader(rpt.toString()), css);
            htmlWorker.setStyleSheet(css);
            //XMLWorkerHelper.getInstance().parseXHtml(pdfWriter, document, new StringReader(rpt.toString()));
            htmlWorker.parse(new StringReader(rpt.toString()));
            document.close();
            file1.close();

            return file;

        } catch (IOException e) {
            Log.d("error", "testPdf: " + e);
        } catch (DocumentException e) {
            Log.d("error", "testPdf: " + e);
        }
        return null;
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


