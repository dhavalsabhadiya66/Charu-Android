package brainwaves.com.charu;

import android.app.AlertDialog;
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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import java.util.Map;

import brainwaves.com.charu.Class.VolleyCallback;
import brainwaves.com.charu.Utils.Const;
import brainwaves.com.charu.Utils.Pref;

public class LedgerReportActivity extends AppCompatActivity {

    Toolbar toolBar;
    LedgerReportAdp adp;
    ImageView imgShare;
    TextView tvAction,tvNoData;
    RecyclerView rvList;
    LinearLayout llNoData, llHeader, llFilter;
    EditText etFromDate, etToDate, etAccountName;
    Button btnSearch, btnClear;
    ArrayList<HashMap<String, String>> arrayhmAccount, arrayhmYear;
    ArrayList<String> arrayAccount, arrayYear;
    ArrayList<HashMap<String, String>> LedgerReportArray = new ArrayList<>();
    String Encoded;
    ArrayAdapter adapter;
    TextView tvTotalDr,tvTotalCr, tvTotalBal;
    LinearLayout rvTotal;
    double totalAmountDr= 0;
    double totalAmountCr= 0;
    double totalAmountBal= 0;
    String type = "";
    Context context = LedgerReportActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledger_report);

        Toolbar toolBar = findViewById(R.id.toolBar);
        toolBar.setTitle("");
        setSupportActionBar(toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        etAccountName = findViewById(R.id.etAccountName);
        etFromDate = findViewById(R.id.etFromDate);
        etToDate = findViewById(R.id.etToDate);
        btnSearch = findViewById(R.id.btnSearch);
        btnClear = findViewById(R.id.btnClear);
        imgShare = findViewById(R.id.imgShare);
        tvTotalDr = findViewById(R.id.tvTotalDr);
        tvTotalCr = findViewById(R.id.tvTotalCr);
        tvTotalBal = findViewById(R.id.tvTotalBal);
        rvTotal = findViewById(R.id.rvTotal);

        llNoData = findViewById(R.id.llNoData);
        llHeader = findViewById(R.id.llHeader);
        llFilter = findViewById(R.id.llFilter);
        tvNoData = findViewById(R.id.tvNoData);
        rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(this));

        GetAccountList();

        etFromDate.setText(Pref.getStringValue(context, Const.loginFromDate, ""));
        etToDate.setText(Pref.getStringValue(context, Const.loginToDate, ""));

        etAccountName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    etAccountName.performClick();
                }
            }
        });

        etAccountName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrayAccount.size() != 0) {
                    openSelectionAccount(etAccountName, arrayAccount);
                } else {
                    Const.showErrorDialog(context, "Account Name Not Available");
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
                } else if (Const.isEmpty(etAccountName)) {
                    Const.showErrorDialog(context, "Please Select Account Name");
                } else {
                    getLedgerReport();
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

    private void getLedgerReport() {
        Const.showProgress(this);
        LedgerReportArray = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put(Const.CompCode, Pref.getStringValue(context, Const.loginCompCode, ""));
        map.put(Const.YearCode, Pref.getStringValue(context, Const.loginYearCode, ""));
        map.put(Const.FromDate,Const.isEmpty(etFromDate) ? "" : Const.dateForChange(etFromDate.getText().toString()));
        map.put(Const.ToDate,Const.isEmpty(etToDate) ? "" : Const.dateForChange(etToDate.getText().toString()));
        map.put("BasicCode", "");
        map.put("GroupCode", "");
        map.put("GroupDetCode", "");
        map.put("AcCode", Const.notNullString(arrayhmAccount.get(arrayAccount.indexOf(etAccountName.getText().toString())).get("ac_code"),""));
        map.put("Currency", "");
        map.put(Const.PageNo, "");
        Const.callPostApi(context, Const.BaseUrl + "GetLedgerReport?", map, new VolleyCallback() {
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
                                LedgerReportArray.add(hm);
                            }
                            adp = new LedgerReportAdp(context, LedgerReportArray);
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
                        getLedgerReport();
                    }
                };
                Const.showErrorApiDialog(context, runnable);
            }
        });
    }

    private void GetAccountList() {
        Const.showProgress(context);
        arrayhmAccount = new ArrayList<>();
        arrayAccount = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put(Const.CompCode, Pref.getStringValue(context, Const.loginCompCode, ""));
        map.put(Const.AccountCode, "");
        map.put(Const.AccountName, "");
        Const.callPostApi(context, Const.BaseUrl + "AccountList?", map, new VolleyCallback() {
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
                                arrayhmAccount.add(hm);
                            }
                            for (int i = 0; i < arrayhmAccount.size(); i++) {
                                arrayAccount.add(arrayhmAccount.get(i).get("ac_name"));
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
                        GetAccountList();
                    }
                };
                Const.showErrorApiDialog(context, runnable);
            }
        });
    }

    private void openSelectionAccount(final EditText editText, ArrayList<String> array) {
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
        tvTitle.setText("Select Account");
        if (array.size() != 0) {
            adapter = new ArrayAdapter<String>(context, R.layout.layout_spinner_item, array);
            listView.setAdapter(adapter);
        } else {
            adapter = new ArrayAdapter<String>(context, R.layout.layout_spinner_item, new ArrayList<String>());
            llNoData.setVisibility(View.VISIBLE);
            tvNoData.setText("Account Name Not Available");
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
        etAccountName.setText("");
        etFromDate.setText("");
        etToDate.setText("");
    }

    private class LedgerReportAdp extends RecyclerView.Adapter<LedgerReportAdp.RecyclerViewHolder> {

        ArrayList<HashMap<String, String>> arraylist;
        Context context;

        public LedgerReportAdp(Context context, ArrayList<HashMap<String, String>> arraylist) {
            this.arraylist = arraylist;
            this.context = context;
        }

        @Override
        public LedgerReportAdp.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ledger_report, parent, false);
            LedgerReportAdp.RecyclerViewHolder viewHolder = new LedgerReportAdp.RecyclerViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final LedgerReportAdp.RecyclerViewHolder holder, final int position) {

            final HashMap<String, String> map = arraylist.get(position);

            holder.tvSerialNo.setText((position + 1) + ".");

            holder.tvDate.setText(Const.notNullString(map.get("trans_date"), "-"));
            holder.tvTransType.setText(Const.notNullString(map.get("trans_type"), "-"));
            holder.tvChequeNo.setText(Const.notNullString(map.get("cheque_no"), "-"));
            holder.tvTransMode.setText(Const.notNullString(map.get("trans_mode"), "-"));
            holder.tvVoucherNo.setText(Const.notNullString(map.get("voucher_no"), "-"));
            holder.tvAccountName.setText(Const.notNullString(map.get("ac_name"), "-"));

            DecimalFormat df = new DecimalFormat("#,##,###.00");

            if (!map.get("rec_amt").equalsIgnoreCase("null") && !map.get("rec_amt").equalsIgnoreCase("0")) {
                holder.tvDebit.setText(String.valueOf(df.format(Double.parseDouble(map.get("rec_amt")))));
            }else{
                holder.tvDebit.setText("-");
            }

            if (!map.get("pay_amt").equalsIgnoreCase("null") && !map.get("pay_amt").equalsIgnoreCase("0")) {
                holder.tvCredit.setText(String.valueOf(df.format(Double.parseDouble(map.get("pay_amt")))));
            }else{
                holder.tvCredit.setText("-");
            }

            if (!map.get("bal_amt").equalsIgnoreCase("null") && !map.get("bal_amt").equalsIgnoreCase("0")) {
                holder.tvBalanceAc.setText(String.valueOf(df.format(Double.parseDouble(map.get("bal_amt")))));
            }else{
                holder.tvBalanceAc.setText("-");
            }

            holder.tvType.setText(Const.notNullString(map.get("type"), "-"));

            totalAmountDr =0;
            totalAmountCr =0;
            type = "";

                for (int i = 0; i < arraylist.size(); i++) {
                    if (!arraylist.get(i).get("rec_amt").equalsIgnoreCase("null")) {
                        totalAmountDr += Double.parseDouble(arraylist.get(i).get("rec_amt"));
                    }
                    if (!arraylist.get(i).get("pay_amt").equalsIgnoreCase("null")) {
                        totalAmountCr += Double.parseDouble(arraylist.get(i).get("pay_amt"));
                    }
                    if (!arraylist.get(arraylist.size()-1).get("bal_amt").equalsIgnoreCase("null") && !map.get("bal_amt").equalsIgnoreCase("0")) {
                        totalAmountBal = Double.parseDouble(arraylist.get(arraylist.size()-1).get("bal_amt"));
                    }else{
                       // totalAmountBal = 0.00;
                    }
                    if (!arraylist.get(arraylist.size()-1).get("type").equalsIgnoreCase("null") && !map.get("type").equalsIgnoreCase("0")) {
                        type = (arraylist.get(arraylist.size()-1).get("type"));
                    }else {
                        type = "";
                    }
            }
            tvTotalDr.setText("Dr. ₹ " + new DecimalFormat("#,##,###.00").format(totalAmountDr));
            tvTotalCr.setText("Cr. ₹ " + new DecimalFormat("#,##,###.00").format(totalAmountCr));
            tvTotalBal.setText("Bal. ₹ " + new DecimalFormat("#,##,###.00").format(totalAmountBal) + " " + type);
        }

        @Override
        public int getItemCount() {
            return arraylist.size();
        }

        public class RecyclerViewHolder extends RecyclerView.ViewHolder {

            TextView tvSerialNo, tvDate, tvTransType, tvChequeNo, tvTransMode, tvVoucherNo, tvAccountName, tvDebit, tvCredit,
                    tvBalanceAc, tvType;
            public RecyclerViewHolder(View v) {
                super(v);

                tvSerialNo = v.findViewById(R.id.tvSerialNo);
                tvDate = v.findViewById(R.id.tvDate);
                tvTransType = v.findViewById(R.id.tvTransType);
                tvChequeNo = v.findViewById(R.id.tvChequeNo);
                tvTransMode = v.findViewById(R.id.tvTransMode);
                tvVoucherNo = v.findViewById(R.id.tvVoucherNo);
                tvAccountName = v.findViewById(R.id.tvAccountName);
                tvDebit = v.findViewById(R.id.tvDebit);
                tvCredit = v.findViewById(R.id.tvCredit);
                tvBalanceAc = v.findViewById(R.id.tvBalanceAc);
                tvType = v.findViewById(R.id.tvType);
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

            String Header = "Ledger Report";

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

            data += "<TR><td align='center' style=\"font-size:15pt;font-family:'Open Sans';\" colspan=\"11\"><b>" + Header + "</b></td>";
            data += "</TR>";

            data += "<TR bgcolor=\"#E4DDC2\">";

            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Sr.No</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Date</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Trans Type</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Cheque No</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Trans Mode</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Voucher No</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Account/Narration</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Debit(₹)</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Credit(₹)</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Balance Amount(₹)</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Type</b></TH>";
            data += "</TR>";

            for (int i = 0; i < LedgerReportArray.size(); i++) {
                String SrNo = "";
                String Date = "";
                String TransType = "";
                String ChequeNo = "";
                String TransMode = "";
                String VoucherNo = "";
                String AccountName = "";
                String Debit = "";
                String Credit = "";
                String BalanceAmount = "";
                String Type = "";

                final HashMap<String, String> map = LedgerReportArray.get(i);

                SrNo = ((i + 1) + ".");
                Date = (Const.notNullString(map.get("trans_date"), "-"));
                TransType = (Const.notNullString(map.get("trans_type"), "-"));
                ChequeNo = (Const.notNullString(map.get("cheque_no"), "-"));
                TransMode = (Const.notNullString(map.get("trans_mode"), "-"));
                VoucherNo = (Const.notNullString(map.get("voucher_no"), "-"));
                AccountName = (Const.notNullString(map.get("ac_name"), "-"));
                Debit = (Const.notNullString(map.get("rec_amt"), "-"));
                Credit = (Const.notNullString(map.get("pay_amt"), "-"));
                BalanceAmount = (Const.notNullString(map.get("bal_amt"), "-"));
                Type = (Const.notNullString(map.get("type"), "-"));

                data += "<TR>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + SrNo + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + Date + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + TransType + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + ChequeNo + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + TransMode + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + VoucherNo + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + AccountName + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + Debit + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + Credit + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + BalanceAmount + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + Type + "</TD>";

            }

            data += "<TR>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>Total</b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>" + tvTotalDr.getText().toString() + "</b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>" + tvTotalCr.getText().toString() + "</b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
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

