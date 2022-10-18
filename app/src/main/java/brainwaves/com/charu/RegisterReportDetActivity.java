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
import android.view.WindowManager;
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

public class RegisterReportDetActivity extends AppCompatActivity {

    Toolbar toolBar;
    RegisterReportAdp adp;
    ImageView imgShare;
    TextView tvAction,tvNoData;
    RecyclerView rvList;
    LinearLayout llNoData, llHeader, llFilter;
    EditText etType, etFromDate, etToDate, etPartyName, etBookName;
    Button btnSearch, btnClear;
    ArrayList<HashMap<String, String>> arrayhmType, arrayhmParty, arrayhmBookName;
    ArrayList<String> arrayType, arrayParty, arrayBookName;
    ArrayList<HashMap<String, String>> RegisterReportArray = new ArrayList<>();
    String Encoded;
    ArrayAdapter adapter;
    TextView tvTotalQty,tvTotalGoodsAmt,tvTotalTaxAmt,tvTotalAddAmt,tvTotalAmt,tvTotalTaxableAmt,tvTotalCGSTAmt,tvTotalSGSTAmt,tvTotalIGSTAmt;
    LinearLayout rvTotal;
    double totalQty=0, totalGoodsAmt=0, totalTaxAmt=0, totalAddAmt=0, totalAmt=0, totalTaxableAmt=0, totalCGSTAmt=0, totalSGSTAmt=0, totalIGSTAmt =0;
    String ProcessCode = "", FromDate = "", ToDate = "", PartyCode = "", BookCode = "";
    int pastVisiblesItems, visibleItemCount, totalItemCount, pageCount = 1, totalSize = 0;
    LinearLayoutManager lm;
    Context context = RegisterReportDetActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_report_det);

        Window window = this.getWindow();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar toolBar = findViewById(R.id.toolBar);
        toolBar.setTitle("");
        setSupportActionBar(toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        etType = findViewById(R.id.etType);
        etPartyName = findViewById(R.id.etPartyName);
        etBookName = findViewById(R.id.etBookName);
        etFromDate = findViewById(R.id.etFromDate);
        etToDate = findViewById(R.id.etToDate);
        btnSearch = findViewById(R.id.btnSearch);
        btnClear = findViewById(R.id.btnClear);
        imgShare = findViewById(R.id.imgShare);

        tvTotalQty = findViewById(R.id.tvTotalQty);
        tvTotalGoodsAmt = findViewById(R.id.tvTotalGoodsAmt);
        tvTotalTaxAmt = findViewById(R.id.tvTotalTaxAmt);
        tvTotalAddAmt = findViewById(R.id.tvTotalAddAmt);
        tvTotalAmt = findViewById(R.id.tvTotalAmt);
        tvTotalTaxableAmt = findViewById(R.id.tvTotalTaxableAmt);
        tvTotalCGSTAmt = findViewById(R.id.tvTotalCGSTAmt);
        tvTotalSGSTAmt = findViewById(R.id.tvTotalSGSTAmt);
        tvTotalIGSTAmt = findViewById(R.id.tvTotalIGSTAmt);
        rvTotal = findViewById(R.id.rvTotal);

        llNoData = findViewById(R.id.llNoData);
        llHeader = findViewById(R.id.llHeader);
        llFilter = findViewById(R.id.llFilter);
        tvNoData = findViewById(R.id.tvNoData);
        rvList = findViewById(R.id.rvList);
        // rvList.setLayoutManager(new LinearLayoutManager(this));

        lm = new LinearLayoutManager(this);
        rvList.setLayoutManager(lm);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            FromDate = bundle.getString("FromDate");
            ToDate = bundle.getString("ToDate");
            PartyCode = bundle.getString("PartyCode");
            BookCode = bundle.getString("BookCode");
            ProcessCode = bundle.getString("ProcessCode");
        }

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

        rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = lm.getChildCount();
                    totalItemCount = lm.getItemCount();
                    pastVisiblesItems = lm.findFirstVisibleItemPosition();
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        if (totalSize != RegisterReportArray.size()) {
                            pageCount++;
                            getRegisterReport();
                        }
                    }
                }
            }
        });

        pageCount = 1;
        RegisterReportArray = new ArrayList<>();
        getRegisterReport();
    }

    private void getRegisterReport() {
        Const.showProgress(this);
        // PurchaseReportArray = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put(Const.CompCode, Pref.getStringValue(context, Const.loginCompCode, ""));
        map.put(Const.FromDate, Const.notNullString(FromDate,""));
        map.put(Const.ToDate, Const.notNullString(ToDate,""));
        map.put("ProcessCode", Const.notNullString(ProcessCode,""));
        map.put("PartyCode", Const.notNullString(PartyCode,""));
        map.put(Const.PageNo, pageCount + "");

        Const.callPostApi(context, Const.BaseUrl + "GetRegisterReport?", map, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optString("STATUS").equalsIgnoreCase("1")) {
                        totalSize = object.optInt("TOT_RECORD");
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
                                RegisterReportArray.add(hm);
                            }
                            if (pageCount == 1) {
                                adp = new RegisterReportAdp(context, RegisterReportArray);
                                rvList.setAdapter(adp);
                            }
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
                Const.dismissProgress();
            }

            @Override
            public void onFailerResponse(String error) {
                Const.dismissProgress();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        getRegisterReport();
                    }
                };
                Const.showErrorApiDialog(context, runnable);
            }
        });
    }

    private class RegisterReportAdp extends RecyclerView.Adapter<RegisterReportAdp.RecyclerViewHolder> {

        ArrayList<HashMap<String, String>> arraylist;
        Context context;

        public RegisterReportAdp(Context context, ArrayList<HashMap<String, String>> arraylist) {
            this.arraylist = arraylist;
            this.context = context;
        }

        @Override
        public RegisterReportAdp.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_register_report, parent, false);
            RegisterReportAdp.RecyclerViewHolder viewHolder = new RegisterReportAdp.RecyclerViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final RegisterReportAdp.RecyclerViewHolder holder, final int position) {

            final HashMap<String, String> map = arraylist.get(position);

            holder.tvSerialNo.setText((position + 1) + ".");

            holder.tvInvoiceDate.setText(Const.notNullString(map.get("invoice_date"), "-"));
            holder.tvInvoiceNo.setText(Const.notNullString(map.get("invoice_no"), "-"));
            holder.tvSuplrInvoiceNo.setText(Const.notNullString(map.get("suplr_inv_no"), "-"));
            holder.tvBookName.setText(Const.notNullString(map.get("trans_ac_name"), "-"));
            holder.tvCustName.setText(Const.notNullString(map.get("party_name"), "-"));
            holder.tvQTY.setText(Const.notNullString(map.get("qty"), "0"));
            holder.tvTerms.setText(Const.notNullString(map.get("terms"), "-"));
            holder.tvGSTINNo.setText(Const.notNullString(map.get("gstin_no"), "-"));

            DecimalFormat df = new DecimalFormat("#,##,###.00");

            if (!map.get("trans_amount").equalsIgnoreCase("null") && !map.get("trans_amount").equalsIgnoreCase("0")) {
                holder.tvGoodsAmt.setText(String.valueOf(df.format(Double.parseDouble(map.get("trans_amount")))));
            }else{
                holder.tvGoodsAmt.setText("-");
            }
            if (!map.get("tax_trans_amount").equalsIgnoreCase("null") && !map.get("tax_trans_amount").equalsIgnoreCase("0")) {
                holder.tvTaxAmt.setText(String.valueOf(df.format(Double.parseDouble(map.get("tax_trans_amount")))));
            }else{
                holder.tvTaxAmt.setText("-");
            }
            if (!map.get("exp_trans_amount").equalsIgnoreCase("null") && !map.get("exp_trans_amount").equalsIgnoreCase("0")) {
                holder.tvAddAmt.setText(String.valueOf(df.format(Double.parseDouble(map.get("exp_trans_amount")))));
            }else{
                holder.tvAddAmt.setText("-");
            }
            if (!map.get("amount").equalsIgnoreCase("null") && !map.get("amount").equalsIgnoreCase("0")) {
                holder.tvTotalAmt.setText(String.valueOf(df.format(Double.parseDouble(map.get("amount")))));
            }else{
                holder.tvTotalAmt.setText("-");
            }
            if (!map.get("taxable_amount").equalsIgnoreCase("null") && !map.get("taxable_amount").equalsIgnoreCase("0")) {
                holder.tvTaxableAmt.setText(String.valueOf(df.format(Double.parseDouble(map.get("taxable_amount")))));
            }else{
                holder.tvTaxableAmt.setText("-");
            }
            if (!map.get("cgst_amount").equalsIgnoreCase("null") && !map.get("cgst_amount").equalsIgnoreCase("0")) {
                holder.tvCGSTAmt.setText(String.valueOf(df.format(Double.parseDouble(map.get("cgst_amount")))));
            }else{
                holder.tvCGSTAmt.setText("-");
            }
            if (!map.get("sgst_amount").equalsIgnoreCase("null") && !map.get("sgst_amount").equalsIgnoreCase("0")) {
                holder.tvSGSTAmt.setText(String.valueOf(df.format(Double.parseDouble(map.get("sgst_amount")))));
            }else{
                holder.tvSGSTAmt.setText("-");
            }
            if (!map.get("igst_amount").equalsIgnoreCase("null") && !map.get("igst_amount").equalsIgnoreCase("0")) {
                holder.tvIGSTAmt.setText(String.valueOf(df.format(Double.parseDouble(map.get("igst_amount")))));
            }else{
                holder.tvIGSTAmt.setText("-");
            }
            totalQty=0;
            totalGoodsAmt=0;
            totalTaxAmt=0;
            totalAddAmt=0;
            totalAmt=0;
            totalTaxableAmt=0;
            totalCGSTAmt=0;
            totalSGSTAmt=0;
//            totalIGSTAmt =0;

            for (int i = 0; i < arraylist.size(); i++) {
                if (!arraylist.get(i).get("qty").equalsIgnoreCase("null")) {
                    totalQty += Double.parseDouble(arraylist.get(i).get("qty"));
                }
                if (!arraylist.get(i).get("trans_amount").equalsIgnoreCase("null")) {
                    totalGoodsAmt += Double.parseDouble(arraylist.get(i).get("trans_amount"));
                }
                if (!arraylist.get(i).get("tax_trans_amount").equalsIgnoreCase("null")) {
                    totalTaxAmt += Double.parseDouble(arraylist.get(i).get("tax_trans_amount"));
                }
                if (!arraylist.get(i).get("exp_trans_amount").equalsIgnoreCase("null")) {
                    totalAddAmt += Double.parseDouble(arraylist.get(i).get("exp_trans_amount"));
                }
                if (!arraylist.get(i).get("amount").equalsIgnoreCase("null")) {
                    totalAmt += Double.parseDouble(arraylist.get(i).get("amount"));
                }
                if (!arraylist.get(i).get("taxable_amount").equalsIgnoreCase("null")) {
                    totalTaxableAmt += Double.parseDouble(arraylist.get(i).get("taxable_amount"));
                }
                if (!arraylist.get(i).get("cgst_amount").equalsIgnoreCase("null")) {
                    totalCGSTAmt += Double.parseDouble(arraylist.get(i).get("cgst_amount"));
                }
                if (!arraylist.get(i).get("sgst_amount").equalsIgnoreCase("null")) {
                    totalSGSTAmt += Double.parseDouble(arraylist.get(i).get("sgst_amount"));
                }
//                if (!arraylist.get(i).get("igst_amount").equalsIgnoreCase("null")) {
//                    totalIGSTAmt += Double.parseDouble(arraylist.get(i).get("igst_amount"));
//                }
            }
            tvTotalQty.setText(new DecimalFormat("#,##,###.00").format(totalQty));
            tvTotalGoodsAmt.setText("₹ " + new DecimalFormat("#,##,###.00").format(totalGoodsAmt));
            tvTotalTaxAmt.setText("₹ " + new DecimalFormat("#,##,###.00").format(totalTaxAmt));
            tvTotalAddAmt.setText("₹ " + new DecimalFormat("#,##,###.00").format(totalAddAmt));
            tvTotalAmt.setText("₹ " + new DecimalFormat("#,##,###.00").format(totalAmt));
            tvTotalTaxableAmt.setText("₹ " + new DecimalFormat("#,##,###.00").format(totalTaxableAmt));
            tvTotalCGSTAmt.setText("₹ " + new DecimalFormat("#,##,###.00").format(totalCGSTAmt));
            tvTotalSGSTAmt.setText("₹ " + new DecimalFormat("#,##,###.00").format(totalSGSTAmt));
//            tvTotalIGSTAmt.setText(" " + new DecimalFormat("#,##,###.00").format(totalIGSTAmt));
        }

        @Override
        public int getItemCount() {
            return arraylist.size();
        }

        public class RecyclerViewHolder extends RecyclerView.ViewHolder {

            TextView tvSerialNo, tvInvoiceDate, tvInvoiceNo, tvSuplrInvoiceNo, tvBookName, tvCustName,
                    tvQTY, tvGoodsAmt, tvTaxAmt, tvAddAmt, tvTotalAmt, tvTaxableAmt, tvCGSTAmt, tvSGSTAmt,
                    tvIGSTAmt, tvTerms, tvGSTINNo;
            public RecyclerViewHolder(View v) {
                super(v);

                tvSerialNo = v.findViewById(R.id.tvSerialNo);
                tvInvoiceDate = v.findViewById(R.id.tvInvoiceDate);
                tvInvoiceNo = v.findViewById(R.id.tvInvoiceNo);
                tvSuplrInvoiceNo = v.findViewById(R.id.tvSuplrInvoiceNo);
                tvBookName = v.findViewById(R.id.tvBookName);
                tvCustName = v.findViewById(R.id.tvCustName);
                tvQTY = v.findViewById(R.id.tvQTY);
                tvGoodsAmt = v.findViewById(R.id.tvGoodsAmt);
                tvTaxAmt = v.findViewById(R.id.tvTaxAmt);
                tvAddAmt = v.findViewById(R.id.tvAddAmt);
                tvTotalAmt = v.findViewById(R.id.tvTotalAmt);
                tvTaxableAmt = v.findViewById(R.id.tvTaxableAmt);
                tvCGSTAmt = v.findViewById(R.id.tvCGSTAmt);
                tvSGSTAmt = v.findViewById(R.id.tvSGSTAmt);
                tvIGSTAmt = v.findViewById(R.id.tvIGSTAmt);
                tvTerms = v.findViewById(R.id.tvTerms);
                tvGSTINNo = v.findViewById(R.id.tvGSTINNo);
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

            String Header = "Register Report";

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

            data += "<TR><td align='center' style=\"font-size:15pt;font-family:'Open Sans';\" colspan=\"17\"><b>" + Header + "</b></td>";
            data += "</TR>";

            data += "<TR bgcolor=\"#E4DDC2\">";

            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Sr.No</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Invoice Date</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Invoice No</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Suplr InvoiceNo</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Book Name</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Customer Name</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>QTY</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Goods Amount(₹)</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Tax Amount(₹)</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Additional Amount(₹)</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Total Amount(₹)</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Taxable Amount(₹)</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>CGST Amount(₹)</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>SGST Amount(₹)</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>IGST Amount(₹)</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Terms</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>GSTIN No/b></TH>";
            data += "</TR>";

            for (int i = 0; i < RegisterReportArray.size(); i++) {
                String SrNo = "";
                String InvoiceDate = "";
                String InvoiceNo = "";
                String SuplrInvoiceNo = "";
                String BookName = "";
                String CustName = "";
                String QTY = "";
                String GoodsAmt = "";
                String TaxAmt = "";
                String AddAmt = "";
                String TotalAmt = "";
                String TaxableAmt = "";
                String CGSTAmt = "";
                String SGSTAmt = "";
                String IGSTAmt = "";
                String Terms = "";
                String GSTINNo = "";

                final HashMap<String, String> map = RegisterReportArray.get(i);

                SrNo = ((i + 1) + ".");
                InvoiceDate = (Const.notNullString(map.get("invoice_date"), "-"));
                InvoiceNo = (Const.notNullString(map.get("invoice_no"), "-"));
                SuplrInvoiceNo = (Const.notNullString(map.get("suplr_inv_no"), "-"));
                BookName = (Const.notNullString(map.get("trans_ac_name"), "-"));
                CustName = (Const.notNullString(map.get("party_name"), "-"));
                QTY = (Const.notNullString(map.get("qty"), "-"));
                GoodsAmt = (Const.notNullString(map.get("trans_amount"), "-"));
                TaxAmt = (Const.notNullString(map.get("tax_trans_amount"), "-"));
                AddAmt = (Const.notNullString(map.get("exp_trans_amount"), "-"));
                TotalAmt = (Const.notNullString(map.get("amount"), "-"));
                TaxableAmt = (Const.notNullString(map.get("taxable_amount"), "-"));
                CGSTAmt = (Const.notNullString(map.get("cgst_amount"), "-"));
                SGSTAmt = (Const.notNullString(map.get("sgst_amount"), "-"));
                IGSTAmt = (Const.notNullString(map.get("igst_amount"), "-"));
                Terms = (Const.notNullString(map.get("terms"), "-"));
                GSTINNo = (Const.notNullString(map.get("amount"), "-"));

                data += "<TR>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + SrNo + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + InvoiceDate + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + InvoiceNo + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + SuplrInvoiceNo + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + BookName + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + CustName + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + QTY + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + GoodsAmt + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + TaxAmt + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + AddAmt + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + TotalAmt + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + TaxableAmt + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + CGSTAmt + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + SGSTAmt + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + IGSTAmt + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + Terms + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + GSTINNo + "</TD>";

            }
            data += "<TR>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>Total</b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>" + tvTotalQty.getText().toString() + "</b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>" + tvTotalGoodsAmt.getText().toString() + "</b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>" + tvTotalTaxAmt.getText().toString() + "</b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>" + tvTotalAddAmt.getText().toString() + "</b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>" + tvTotalAmt.getText().toString() + "</b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>" + tvTotalTaxableAmt.getText().toString() + "</b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>" + tvTotalCGSTAmt.getText().toString() + "</b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>" + tvTotalSGSTAmt.getText().toString() + "</b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
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
