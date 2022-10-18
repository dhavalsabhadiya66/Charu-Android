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

public class TargetReportActivity extends AppCompatActivity {

    Toolbar toolBar;
    TargetReportAdp adp;
    ImageView imgShare;
    TextView tvAction,tvNoData;
    RecyclerView rvList;
    LinearLayout llNoData, llHeader, llFilter;
    EditText etFromDate, etToDate, etEmployeeName, etPartyName;
    Button btnSearch, btnClear;
    ArrayList<HashMap<String, String>> arrayhmEmployee, arrayhmParty;
    ArrayList<String> arrayEmployee, arrayParty;
    ArrayList<HashMap<String, String>> TargetReportArray = new ArrayList<>();
    String Encoded;
    ArrayAdapter adapter;
    TextView tvTotalTarget,tvTotalAchieve;
    LinearLayout rvTotal;
    double totaltargetvalue= 0;
    double totalAchivevalue= 0;
    String type = "";
    Context context = TargetReportActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_report);

        Toolbar toolBar = findViewById(R.id.toolBar);
        toolBar.setTitle("");
        setSupportActionBar(toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        etFromDate = findViewById(R.id.etFromDate);
        etToDate = findViewById(R.id.etToDate);
        etEmployeeName = findViewById(R.id.etEmployeeName);
        etPartyName = findViewById(R.id.etPartyName);
        btnSearch = findViewById(R.id.btnSearch);
        btnClear = findViewById(R.id.btnClear);
        imgShare = findViewById(R.id.imgShare);
        tvTotalTarget = findViewById(R.id.tvTotalTarget);
        tvTotalAchieve = findViewById(R.id.tvTotalAchieve);
        rvTotal = findViewById(R.id.rvTotal);

        llNoData = findViewById(R.id.llNoData);
        llHeader = findViewById(R.id.llHeader);
        llFilter = findViewById(R.id.llFilter);
        tvNoData = findViewById(R.id.tvNoData);
        rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(this));

        GetEmployeeList();
        GetPartyList();

        etFromDate.setText(Pref.getStringValue(context, Const.loginFromDate, ""));
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

        etEmployeeName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    etEmployeeName.performClick();
                }
            }
        });

        etEmployeeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrayEmployee.size() != 0) {
                    openSelectionEmployee(etEmployeeName, arrayEmployee);
                } else {
                    Const.showErrorDialog(context, "Employee Name Not Available");
                }
            }
        });

        etPartyName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    etPartyName.performClick();
                }
            }
        });

        etPartyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrayParty.size() != 0) {
                    openSelectionParty(etPartyName, arrayParty);
                } else {
                    Const.showErrorDialog(context, "Party Name Not Available");
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
                    getTargetReport();
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

    private void getTargetReport() {
        Const.showProgress(this);
        TargetReportArray = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put(Const.CompCode, Pref.getStringValue(context, Const.loginCompCode, ""));
        map.put(Const.FromDate,Const.isEmpty(etFromDate) ? "" : Const.dateForChange(etFromDate.getText().toString()));
        map.put(Const.ToDate,Const.isEmpty(etToDate) ? "" : Const.dateForChange(etToDate.getText().toString()));
        map.put(Const.EmpCode, Const.isEmpty(etEmployeeName) ? "" : Const.notNullString(arrayhmEmployee.get(arrayEmployee.indexOf(etEmployeeName.getText().toString())).get("emp_code"),""));
        map.put(Const.PartyCode, Const.isEmpty(etPartyName) ? "" : Const.notNullString(arrayhmParty.get(arrayParty.indexOf(etPartyName.getText().toString())).get("party_code"),""));

        Const.callPostApi(context, Const.BaseUrl + "GetTargetReport?", map, new VolleyCallback() {
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
                                TargetReportArray.add(hm);
                            }
                            adp = new TargetReportAdp(context, TargetReportArray);
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
                        getTargetReport();
                    }
                };
                Const.showErrorApiDialog(context, runnable);
            }
        });
    }

    private void GetEmployeeList() {
        Const.showProgress(context);
        arrayhmEmployee = new ArrayList<>();
        arrayEmployee = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put(Const.EmpCode, "");
        map.put(Const.CompCode, Pref.getStringValue(context, Const.loginCompCode, ""));

        Const.callPostApi(context, Const.BaseUrl + "EmpMasList?", map, new VolleyCallback() {
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
                                arrayhmEmployee.add(hm);
                            }
                            for (int i = 0; i < arrayhmEmployee.size(); i++) {
                                arrayEmployee.add(arrayhmEmployee.get(i).get("emp_name"));
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
                        GetEmployeeList();
                    }
                };
                Const.showErrorApiDialog(context, runnable);
            }
        });
    }

    private void GetPartyList() {
        Const.showProgress(context);
        arrayhmParty = new ArrayList<>();
        arrayParty = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put(Const.PartyCode, "");
        map.put(Const.CompCode, Pref.getStringValue(context, Const.loginCompCode, ""));

        Const.callPostApi(context, Const.BaseUrl + "PartyMasList?", map, new VolleyCallback() {
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
                                arrayhmParty.add(hm);
                            }
                            for (int i = 0; i < arrayhmParty.size(); i++) {
                                arrayParty.add(arrayhmParty.get(i).get("party_name"));
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
                        GetPartyList();
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

    private void openSelectionParty(final EditText editText, ArrayList<String> array) {
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
        tvTitle.setText("Select Party Name");
        if (array.size() != 0) {
            adapter = new ArrayAdapter<String>(context, R.layout.layout_spinner_item, array);
            listView.setAdapter(adapter);
        } else {
            adapter = new ArrayAdapter<String>(context, R.layout.layout_spinner_item, new ArrayList<String>());
            llNoData.setVisibility(View.VISIBLE);
            tvNoData.setText("Party Name Not Available");
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
        etEmployeeName.setText("");
        etPartyName.setText("");
        etFromDate.setText("");
        etToDate.setText("");
    }

    private class TargetReportAdp extends RecyclerView.Adapter<TargetReportAdp.RecyclerViewHolder> {

        ArrayList<HashMap<String, String>> arraylist;
        Context context;

        public TargetReportAdp(Context context, ArrayList<HashMap<String, String>> arraylist) {
            this.arraylist = arraylist;
            this.context = context;
        }

        @Override
        public TargetReportAdp.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_target_report, parent, false);
            TargetReportAdp.RecyclerViewHolder viewHolder = new TargetReportAdp.RecyclerViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final TargetReportAdp.RecyclerViewHolder holder, final int position) {

            final HashMap<String, String> map = arraylist.get(position);

            holder.tvSerialNo.setText((position + 1) + ".");

            holder.tvPartyName.setText(Const.notNullString(map.get("party_name"), "-"));
            holder.tvEmployeeName.setText(Const.notNullString(map.get("emp_name"), "-"));

            DecimalFormat df = new DecimalFormat("#,##,###.00");

            if (!map.get("target_val").equalsIgnoreCase("null") && !map.get("target_val").equalsIgnoreCase("0")) {
                holder.tvTargetValue.setText(String.valueOf(df.format(Double.parseDouble(map.get("target_val")))));
            }else{
                holder.tvTargetValue.setText("-");
            }

            if (!map.get("inv_value").equalsIgnoreCase("null") && !map.get("inv_value").equalsIgnoreCase("0")) {
                holder.tvAchieveValue.setText(String.valueOf(df.format(Double.parseDouble(map.get("inv_value")))));
            }else{
                holder.tvAchieveValue.setText("-");
            }

            totaltargetvalue =0;
            totalAchivevalue =0;

            for (int i = 0; i < arraylist.size(); i++) {
                if (!arraylist.get(i).get("target_val").equalsIgnoreCase("null")) {
                    totaltargetvalue += Double.parseDouble(arraylist.get(i).get("target_val"));
                }
                if (!arraylist.get(i).get("inv_value").equalsIgnoreCase("null")) {
                    totalAchivevalue += Double.parseDouble(arraylist.get(i).get("inv_value"));
                }
            }
            tvTotalTarget.setText("Target:" + new DecimalFormat("#,##,###.00").format(totaltargetvalue));
            tvTotalAchieve.setText("Achieve:" + new DecimalFormat("#,##,###.00").format(totalAchivevalue));
        }

        @Override
        public int getItemCount() {
            return arraylist.size();
        }

        public class RecyclerViewHolder extends RecyclerView.ViewHolder {

            TextView tvSerialNo, tvPartyName, tvEmployeeName, tvTargetValue, tvAchieveValue;
            public RecyclerViewHolder(View v) {
                super(v);

                tvSerialNo = v.findViewById(R.id.tvSerialNo);
                tvPartyName = v.findViewById(R.id.tvPartyName);
                tvEmployeeName = v.findViewById(R.id.tvEmployeeName);
                tvTargetValue = v.findViewById(R.id.tvTargetValue);
                tvAchieveValue = v.findViewById(R.id.tvAchieveValue);
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

            String Header = "Target Report";

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

            data += "<TR><td align='center' style=\"font-size:15pt;font-family:'Open Sans';\" colspan=\"5\"><b>" + Header + "</b></td>";
            data += "</TR>";

            data += "<TR bgcolor=\"#E4DDC2\">";

            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Sr.No</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Party Name</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Employee Name</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Target Value(₹)</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Achieve Value(₹)</b></TH>";
            data += "</TR>";

            for (int i = 0; i < TargetReportArray.size(); i++) {
                String SrNo = "";
                String PartyName = "";
                String EmpName = "";
                String TargetValue = "";
                String AchieveValue = "";

                final HashMap<String, String> map = TargetReportArray.get(i);

                SrNo = ((i + 1) + ".");
                PartyName = (Const.notNullString(map.get("party_name"), "-"));
                EmpName = (Const.notNullString(map.get("emp_name"), "-"));
                TargetValue = (Const.notNullString(map.get("target_val"), "-"));
                AchieveValue = (Const.notNullString(map.get("inv_value"), "-"));

                data += "<TR>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + SrNo + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + PartyName + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + EmpName + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + TargetValue + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + AchieveValue + "</TD>";
            }

            data += "<TR>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>Total</b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>" + tvTotalTarget.getText().toString() + "</b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>" + tvTotalAchieve.getText().toString() + "</b></TD>/TR>";
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


