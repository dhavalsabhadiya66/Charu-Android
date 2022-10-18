package brainwaves.com.charu;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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

public class TransClosingTrialReportActivity extends AppCompatActivity {

    Toolbar toolBar;
    TrialReportAdp adp;
    ImageView imgShare;
    TextView tvAction,tvNoData;
    RecyclerView rvList;
    LinearLayout llNoData, llHeader, FlHeader, llFilter;
    EditText etFromDate, etToDate, etProdGroup;
    Button btnSearch, btnClear;
    ArrayList<HashMap<String, String>> arrayhmProdGroup;
    ArrayList<String> arrayProdGroup;
    ArrayList<HashMap<String, String>> TrialReportArray = new ArrayList<>();
    String Encoded;
    ArrayAdapter adapter;
    TextView tvTotalOpDebit,tvTotalOpCredit, tvTotalTransDebit, tvTotalTransCredit, tvTotalCloseDebit, tvTotalCloseCredit;
    LinearLayout rvTotal;
    double totalopdebit= 0, totalopcredit = 0, totaltransdebit = 0, totaltranscredit = 0, totalclosedebit = 0, totalclosecredit=0;
    Context context = TransClosingTrialReportActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_closing_trial_report);

        Toolbar toolBar = findViewById(R.id.toolBar);
        toolBar.setTitle("");
        setSupportActionBar(toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        etFromDate = findViewById(R.id.etFromDate);
        etToDate = findViewById(R.id.etToDate);
        etProdGroup = findViewById(R.id.etProdGroup);
        btnSearch = findViewById(R.id.btnSearch);
        btnClear = findViewById(R.id.btnClear);
        imgShare = findViewById(R.id.imgShare);

        tvTotalOpDebit = findViewById(R.id.tvTotalOpDebit);
        tvTotalOpCredit = findViewById(R.id.tvTotalOpCredit);
        tvTotalTransDebit = findViewById(R.id.tvTotalTransDebit);
        tvTotalTransCredit = findViewById(R.id.tvTotalTransCredit);
        tvTotalCloseDebit = findViewById(R.id.tvTotalCloseDebit);
        tvTotalCloseCredit = findViewById(R.id.tvTotalCloseCredit);
        rvTotal = findViewById(R.id.rvTotal);

        llNoData = findViewById(R.id.llNoData);
        llHeader = findViewById(R.id.llHeader);
        FlHeader = findViewById(R.id.FlHeader);
        llFilter = findViewById(R.id.llFilter);
        tvNoData = findViewById(R.id.tvNoData);
        rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(this));

        GetProdGroupList();

        etFromDate.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
        etToDate.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));

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
                } else {
                    Const.showErrorDialog(context, "Product Group Name Not Available");
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
                } else {
                    getTrialReport();
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

    private void getTrialReport() {
        Const.showProgress(this);
        TrialReportArray = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put(Const.CompCode, Pref.getStringValue(context, Const.loginCompCode, ""));
        map.put(Const.YearCode, Pref.getStringValue(context, Const.loginYearCode, ""));
        map.put(Const.FromDate,Const.isEmpty(etFromDate) ? "" : Const.dateForChange(etFromDate.getText().toString()));
        map.put(Const.ToDate,Const.isEmpty(etToDate) ? "" : Const.dateForChange(etToDate.getText().toString()));
        map.put("ProdGrpCode", Const.isEmpty(etProdGroup) ? "" : Const.notNullString(arrayhmProdGroup.get(arrayProdGroup.indexOf(etProdGroup.getText().toString())).get("group_det_code"),""));

        Const.callPostApi(context, Const.BaseUrl + "GetTransClosingTrialReport?", map, new VolleyCallback() {
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
                                TrialReportArray.add(hm);
                            }
                            adp = new TrialReportAdp(context, TrialReportArray);
                            rvList.setAdapter(adp);
                            adp.notifyDataSetChanged();
                            llHeader.setVisibility(View.VISIBLE);
                            FlHeader.setVisibility(View.VISIBLE);
                            rvTotal.setVisibility(View.VISIBLE);
                            llNoData.setVisibility(View.GONE);
                        } else {
                            llHeader.setVisibility(View.GONE);
                            FlHeader.setVisibility(View.GONE);
                            rvTotal.setVisibility(View.GONE);
                            llNoData.setVisibility(View.VISIBLE);
                            tvNoData.setText("Data Not Available");
                            rvList.setAdapter(null);
                        }
                    }else {
                        llHeader.setVisibility(View.GONE);
                        FlHeader.setVisibility(View.GONE);
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
                        getTrialReport();
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

        Const.callPostApi(context, Const.BaseUrl + "TrialGroupList?", map, new VolleyCallback() {
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
                                arrayProdGroup.add(arrayhmProdGroup.get(i).get("group_det_name"));
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

    private void openSelectionEmployee(final EditText editText, ArrayList<String> array) {
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
        tvTitle.setText("Select Employee Name");
        if (array.size() != 0) {
            adapter = new ArrayAdapter<String>(context, R.layout.layout_spinner_item, array);
            listView.setAdapter(adapter);
        } else {
            adapter = new ArrayAdapter<String>(context, R.layout.layout_spinner_item, new ArrayList<String>());
            llNoData.setVisibility(View.VISIBLE);
            tvNoData.setText("Employee Name Not Available");
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
        tvTitle.setText("Select Product Group Name");
        if (array.size() != 0) {
            adapter = new ArrayAdapter<String>(context, R.layout.layout_spinner_item, array);
            listView.setAdapter(adapter);
        } else {
            adapter = new ArrayAdapter<String>(context, R.layout.layout_spinner_item, new ArrayList<String>());
            llNoData.setVisibility(View.VISIBLE);
            tvNoData.setText("Product Group Name Not Available");
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
        etFromDate.setText("");
        etToDate.setText("");
    }

    private class TrialReportAdp extends RecyclerView.Adapter<TrialReportAdp.RecyclerViewHolder> {

        ArrayList<HashMap<String, String>> arraylist;
        Context context;

        public TrialReportAdp(Context context, ArrayList<HashMap<String, String>> arraylist) {
            this.arraylist = arraylist;
            this.context = context;
        }

        @Override
        public TrialReportAdp.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_trial_report, parent, false);
            TrialReportAdp.RecyclerViewHolder viewHolder = new TrialReportAdp.RecyclerViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final TrialReportAdp.RecyclerViewHolder holder, final int position) {

            final HashMap<String, String> map = arraylist.get(position);

            if (map.get("srno").equalsIgnoreCase("null")) {
                holder.tvSerialNo.setText(Const.notNullString(map.get("srno"), ""));
                holder.tvAccountName.setTypeface(holder.tvAccountName.getTypeface(), Typeface.BOLD);
            }else{
                holder.tvSerialNo.setText(Const.notNullString(map.get("srno"), "") + ".");
            }

            holder.tvAccountName.setText(Const.notNullString(map.get("ac_name"), "-"));
            holder.tvShortName.setText(Const.notNullString(map.get("short_name"), "-"));
            holder.tvMobileNo.setText(Const.notNullString(map.get("mobile_no"), "-"));

            DecimalFormat df = new DecimalFormat("#,##,###.00");

            if (!map.get("dr_amt").equalsIgnoreCase("null") && !map.get("dr_amt").equalsIgnoreCase("0")) {
                holder.tvOpDebit.setText(String.valueOf(df.format(Double.parseDouble(map.get("dr_amt")))));
            }else{
                holder.tvOpDebit.setText("-");
            }
            if (!map.get("cr_amt").equalsIgnoreCase("null") && !map.get("cr_amt").equalsIgnoreCase("0")) {
                holder.tvOpCredit.setText(String.valueOf(df.format(Double.parseDouble(map.get("cr_amt")))));
            }else{
                holder.tvOpCredit.setText("-");
            }
            if (!map.get("dr_trans_amt").equalsIgnoreCase("null") && !map.get("dr_trans_amt").equalsIgnoreCase("0")) {
                holder.tvTransDebit.setText(String.valueOf(df.format(Double.parseDouble(map.get("dr_trans_amt")))));
            }else{
                holder.tvTransDebit.setText("-");
            }
            if (!map.get("cr_trans_amt").equalsIgnoreCase("null") && !map.get("cr_trans_amt").equalsIgnoreCase("0")) {
                holder.tvTransCredit.setText(String.valueOf(df.format(Double.parseDouble(map.get("cr_trans_amt")))));
            }else{
                holder.tvTransCredit.setText("-");
            }
            if (!map.get("cls_dr_trans_amt").equalsIgnoreCase("null") && !map.get("cls_dr_trans_amt").equalsIgnoreCase("0")) {
                holder.tvCloseDebit.setText(String.valueOf(df.format(Double.parseDouble(map.get("cls_dr_trans_amt")))));
            }else{
                holder.tvCloseDebit.setText("-");
            }
            if (!map.get("cls_cr_trans_amt").equalsIgnoreCase("null") && !map.get("cls_cr_trans_amt").equalsIgnoreCase("0")) {
                holder.tvCloseCredit.setText(String.valueOf(df.format(Double.parseDouble(map.get("cls_cr_trans_amt")))));
            }else{
                holder.tvCloseCredit.setText("-");
            }
            totalopdebit=0;
            totalopcredit =0;
            totaltransdebit =0;
            totaltranscredit =0;
            totalclosedebit =0;
            totalclosecredit =0;

            for (int i = 0; i < arraylist.size(); i++) {
                if (!arraylist.get(i).get("dr_amt").equalsIgnoreCase("null")) {
                    totalopdebit += Double.parseDouble(arraylist.get(i).get("dr_amt"));
                }
                if (!arraylist.get(i).get("cr_amt").equalsIgnoreCase("null")) {
                    totalopcredit += Double.parseDouble(arraylist.get(i).get("cr_amt"));
                }
                if (!arraylist.get(i).get("dr_trans_amt").equalsIgnoreCase("null")) {
                    totaltransdebit += Double.parseDouble(arraylist.get(i).get("dr_trans_amt"));
                }
                if (!arraylist.get(i).get("cr_trans_amt").equalsIgnoreCase("null")) {
                    totaltranscredit += Double.parseDouble(arraylist.get(i).get("cr_trans_amt"));
                }
                if (!arraylist.get(i).get("cls_dr_trans_amt").equalsIgnoreCase("null")) {
                    totalclosedebit += Double.parseDouble(arraylist.get(i).get("cls_dr_trans_amt"));
                }
                if (!arraylist.get(i).get("cls_cr_trans_amt").equalsIgnoreCase("null")) {
                    totalclosecredit += Double.parseDouble(arraylist.get(i).get("cls_cr_trans_amt"));
                }
            }
            tvTotalOpDebit.setText(new DecimalFormat("#,##,###.00").format(totalopdebit));
            tvTotalOpCredit.setText(new DecimalFormat("#,##,###.00").format(totalopcredit));
            tvTotalTransDebit.setText(new DecimalFormat("#,##,###.00").format(totaltransdebit));
            tvTotalTransCredit.setText(new DecimalFormat("#,##,###.00").format(totaltranscredit));
            tvTotalCloseDebit.setText(new DecimalFormat("#,##,###.00").format(totalclosedebit));
            tvTotalCloseCredit.setText(new DecimalFormat("#,##,###.00").format(totalclosecredit));
        }

        @Override
        public int getItemCount() {
            return arraylist.size();
        }

        public class RecyclerViewHolder extends RecyclerView.ViewHolder {

            TextView tvSerialNo, tvAccountName, tvShortName, tvMobileNo, tvOpDebit, tvOpCredit, tvTransDebit, tvTransCredit, tvCloseDebit,
                    tvCloseCredit;
            public RecyclerViewHolder(View v) {
                super(v);

                tvSerialNo = v.findViewById(R.id.tvSerialNo);
                tvAccountName = v.findViewById(R.id.tvAccountName);
                tvShortName = v.findViewById(R.id.tvShortName);
                tvMobileNo = v.findViewById(R.id.tvMobileNo);
                tvOpDebit = v.findViewById(R.id.tvOpDebit);
                tvOpCredit = v.findViewById(R.id.tvOpCredit);
                tvTransDebit = v.findViewById(R.id.tvTransDebit);
                tvTransCredit = v.findViewById(R.id.tvTransCredit);
                tvCloseDebit = v.findViewById(R.id.tvCloseDebit);
                tvCloseCredit = v.findViewById(R.id.tvCloseCredit);
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

            String Header = "Transational Closing Trial Balance Report";

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

            data += "<TR><td align='center' style=\"font-size:15pt;font-family:'Open Sans';\" colspan=\"10\"><b>" + Header + "</b></td>";
            data += "</TR>";

            data += "<TR bgcolor=\"#E4DDC2\">";

            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Sr.No</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Account Name</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Short Name</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Mobile No</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap; \" colspan=\"2\"><b>Opening</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap; \" colspan=\"2\"><b>Transaction</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap; \" colspan=\"2\"><b>Closing</b></TH>";
            data += "</TR>";

            data += "<TR bgcolor=\"#E4DDC2\">";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b></b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b></b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b></b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b></b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Debit</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Credit</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Debit</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Credit</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Debit</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Credit</b></TH>";
            data += "</TR>";

            for (int i = 0; i < TrialReportArray.size(); i++) {
                String SrNo = "";
                String AccountName = "";
                String ShortName = "";
                String MobileNo = "";
                String OpDebit = "";
                String OpCredit = "";
                String TransDebit = "";
                String TransCredit = "";
                String CloseDebit = "";
                String CloseCredit = "";

                final HashMap<String, String> map = TrialReportArray.get(i);

                SrNo = (Const.notNullString(map.get("srno"), "."));
                AccountName = (Const.notNullString(map.get("ac_name"), "-"));
                ShortName = (Const.notNullString(map.get("short_name"), "-"));
                MobileNo = (Const.notNullString(map.get("mobile_no"), "-"));
                OpDebit = (Const.notNullString(map.get("dr_amt"), "-"));
                OpCredit = (Const.notNullString(map.get("cr_amt"), "-"));
                TransDebit = (Const.notNullString(map.get("dr_trans_amt"), "-"));
                TransCredit = (Const.notNullString(map.get("cr_trans_amt"), "-"));
                CloseDebit = (Const.notNullString(map.get("cls_dr_trans_amt"), "-"));
                CloseCredit = (Const.notNullString(map.get("cls_cr_trans_amt"), "-"));

                data += "<TR>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + SrNo + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + AccountName + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + ShortName + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + MobileNo + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + OpDebit + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + OpCredit + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + TransDebit + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + TransCredit + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + CloseDebit + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + CloseCredit + "</TD>";
            }

            data += "<TR>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>Total</b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>" + tvTotalOpDebit.getText().toString() + "</b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>" + tvTotalOpCredit.getText().toString() + "</b></TD>/TR>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>" + tvTotalTransDebit.getText().toString() + "</b></TD>/TR>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>" + tvTotalTransCredit.getText().toString() + "</b></TD>/TR>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>" + tvTotalCloseDebit.getText().toString() + "</b></TD>/TR>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>" + tvTotalCloseCredit.getText().toString() + "</b></TD>/TR>";
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



