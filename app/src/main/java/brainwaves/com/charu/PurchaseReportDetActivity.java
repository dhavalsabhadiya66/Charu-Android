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
import java.util.Map;

import brainwaves.com.charu.Class.VolleyCallback;
import brainwaves.com.charu.Utils.Const;
import brainwaves.com.charu.Utils.Pref;

public class PurchaseReportDetActivity extends AppCompatActivity {

    Toolbar toolBar;
    PurchaseReportAdp adp;
    ImageView imgShare;
    TextView tvAction,tvNoData;
    RecyclerView rvList;
    LinearLayout llNoData, llHeader, llFilter;
    EditText etFromDate, etToDate, etPartyName, etProdGroup, etProdName;
    Button btnSearch, btnClear;
    ArrayList<HashMap<String, String>> arrayhmParty, arrayhmProdGroup, arrayhmProdName;
    ArrayList<String> arrayParty, arrayProdGroup, arrayProdName;
    ArrayList<HashMap<String, String>> PurchaseReportArray = new ArrayList<>();
    String Encoded;
    ArrayAdapter adapter;
    TextView tvTotalPcs,tvTotalQty,tvTotalAmt;
    LinearLayout rvTotal;
    int totalPcs= 0;
    int totalQty= 0;
    double totalAmount= 0;
    String FromDate = "", ToDate = "", PartyCode = "", ProdGrpCode = "", ProdCode = "";
    int pastVisiblesItems, visibleItemCount, totalItemCount, pageCount = 1, totalSize = 0;
    LinearLayoutManager lm;
    Context context = PurchaseReportDetActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_report_det);

        Window window = this.getWindow();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar toolBar = findViewById(R.id.toolBar);
        toolBar.setTitle("");
        setSupportActionBar(toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        etPartyName = findViewById(R.id.etPartyName);
        etProdName = findViewById(R.id.etProdName);
        etProdGroup = findViewById(R.id.etProdGroup);
        etFromDate = findViewById(R.id.etFromDate);
        etToDate = findViewById(R.id.etToDate);
        btnSearch = findViewById(R.id.btnSearch);
        btnClear = findViewById(R.id.btnClear);
        imgShare = findViewById(R.id.imgShare);

        tvTotalPcs = findViewById(R.id.tvTotalPcs);
        tvTotalQty = findViewById(R.id.tvTotalQty);
        tvTotalAmt = findViewById(R.id.tvTotalAmt);
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
            ProdGrpCode = bundle.getString("ProdGrpCode");
            ProdCode = bundle.getString("ProdCode");
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
                        if (totalSize != PurchaseReportArray.size()) {
                            pageCount++;
                            getPurchaseReport();
                        }
                    }
                }
            }
        });

        pageCount = 1;
        PurchaseReportArray = new ArrayList<>();
        getPurchaseReport();
    }

    private void getPurchaseReport() {
        Const.showProgress(this);
       // PurchaseReportArray = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put(Const.CompCode, Pref.getStringValue(context, Const.loginCompCode, ""));
        map.put(Const.FromDate, Const.notNullString(FromDate,""));
        map.put(Const.ToDate, Const.notNullString(ToDate,""));
        map.put("ProdCode", Const.notNullString(ProdCode,""));
        map.put("PartyCode", Const.notNullString(PartyCode,""));
        map.put("ProdGrpCode", Const.notNullString(ProdGrpCode,""));
        map.put(Const.PageNo, pageCount + "");

        Const.callPostApi(context, Const.BaseUrl + "GetPurchaseReport?", map, new VolleyCallback() {
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
                                PurchaseReportArray.add(hm);
                            }
                            if (pageCount == 1) {
                                adp = new PurchaseReportAdp(context, PurchaseReportArray);
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
                        getPurchaseReport();
                    }
                };
                Const.showErrorApiDialog(context, runnable);
            }
        });
    }

    private class PurchaseReportAdp extends RecyclerView.Adapter<PurchaseReportAdp.RecyclerViewHolder> {

        ArrayList<HashMap<String, String>> arraylist;
        Context context;

        public PurchaseReportAdp(Context context, ArrayList<HashMap<String, String>> arraylist) {
            this.arraylist = arraylist;
            this.context = context;
        }

        @Override
        public PurchaseReportAdp.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_purchase_report, parent, false);
            PurchaseReportAdp.RecyclerViewHolder viewHolder = new PurchaseReportAdp.RecyclerViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final PurchaseReportAdp.RecyclerViewHolder holder, final int position) {

            final HashMap<String, String> map = arraylist.get(position);

            holder.tvSerialNo.setText((position + 1) + ".");

            holder.tvInvoiceDate.setText(Const.notNullString(map.get("invoice_date"), "-"));
            holder.tvInvoiceNo.setText(Const.notNullString(map.get("invoice_no"), "-"));
            holder.tvBookName.setText(Const.notNullString(map.get("trans_ac_name"), "-"));
            holder.tvPartyName.setText(Const.notNullString(map.get("party_name"), "-"));
            holder.tvProdGrp.setText(Const.notNullString(map.get("stone_type"), "-"));
            holder.tvProdName.setText(Const.notNullString(map.get("stone_category"), "-"));
            holder.tvPcs.setText(Const.notNullString(map.get("pcs"), "-"));
            holder.tvQty.setText(Const.notNullString(map.get("qty"), "-"));
            holder.tvUnit.setText(Const.notNullString(map.get("unit"), "-"));

            DecimalFormat df = new DecimalFormat("#,##,###.00");

            if (!map.get("rate").equalsIgnoreCase("null") && !map.get("rate").equalsIgnoreCase("0")) {
                holder.tvSaleRate.setText(String.valueOf(df.format(Double.parseDouble(map.get("rate")))));
            }else{
                holder.tvSaleRate.setText("-");
            }

            if (!map.get("amount").equalsIgnoreCase("null") && !map.get("amount").equalsIgnoreCase("0")) {
                holder.tvSaleAmt.setText(String.valueOf(df.format(Double.parseDouble(map.get("amount")))));
            }else{
                holder.tvSaleAmt.setText("-");
            }

            totalPcs =0;
            totalQty =0;
            totalAmount =0;

            for (int i = 0; i < arraylist.size(); i++) {
                if (!arraylist.get(i).get("pcs").equalsIgnoreCase("null")) {
                    totalPcs += Integer.parseInt(arraylist.get(i).get("pcs"));
                }
                if (!arraylist.get(i).get("qty").equalsIgnoreCase("null")) {
                    totalQty += Double.parseDouble(arraylist.get(i).get("qty"));
                }
                if (!arraylist.get(i).get("amount").equalsIgnoreCase("null")) {
                    totalAmount += Double.parseDouble(arraylist.get(i).get("amount"));
                }
            }
            tvTotalPcs.setText("Pcs: " + String.valueOf(totalPcs));
            tvTotalQty.setText("Qty: " + String.valueOf(totalQty));
            tvTotalAmt.setText("₹ " + new DecimalFormat("#,##,###.00").format(totalAmount));
        }

        @Override
        public int getItemCount() {
            return arraylist.size();
        }

        public class RecyclerViewHolder extends RecyclerView.ViewHolder {

            TextView tvSerialNo, tvInvoiceDate, tvInvoiceNo, tvBookName, tvPartyName, tvProdGrp,
                    tvProdName, tvPcs, tvQty, tvUnit, tvSaleRate, tvSaleAmt;
            public RecyclerViewHolder(View v) {
                super(v);

                tvSerialNo = v.findViewById(R.id.tvSerialNo);
                tvInvoiceDate = v.findViewById(R.id.tvInvoiceDate);
                tvInvoiceNo = v.findViewById(R.id.tvInvoiceNo);
                tvBookName = v.findViewById(R.id.tvBookName);
                tvPartyName = v.findViewById(R.id.tvPartyName);
                tvProdGrp = v.findViewById(R.id.tvProdGrp);
                tvProdName = v.findViewById(R.id.tvProdName);
                tvPcs = v.findViewById(R.id.tvPcs);
                tvQty = v.findViewById(R.id.tvQty);
                tvUnit = v.findViewById(R.id.tvUnit);
                tvSaleRate = v.findViewById(R.id.tvSaleRate);
                tvSaleAmt = v.findViewById(R.id.tvSaleAmt);
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

            String Header = "Purchase Report";

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
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Invoice No</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Book Name</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Party Name</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Product Group Name</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Product Name</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Pcs</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Qty</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Unit</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Purchase Rate(₹)</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Purchase Amount(₹)</b></TH>";
            data += "</TR>";

            for (int i = 0; i < PurchaseReportArray.size(); i++) {
                String SrNo = "";
                String InvoiceDate = "";
                String InvoiceNo = "";
                String BookName = "";
                String PartyName = "";
                String ProdGrpName = "";
                String ProdName = "";
                String Pcs = "";
                String Qty = "";
                String Unit = "";
                String SaleRate = "";
                String SaleAmt = "";

                final HashMap<String, String> map = PurchaseReportArray.get(i);

                SrNo = ((i + 1) + ".");
                InvoiceDate = (Const.notNullString(map.get("invoice_date"), "-"));
                InvoiceNo = (Const.notNullString(map.get("invoice_no"), "-"));
                BookName = (Const.notNullString(map.get("trans_ac_name"), "-"));
                PartyName = (Const.notNullString(map.get("party_name"), "-"));
                ProdGrpName = (Const.notNullString(map.get("stone_type"), "-"));
                ProdName = (Const.notNullString(map.get("stone_category"), "-"));
                Pcs = (Const.notNullString(map.get("pcs"), "-"));
                Qty = (Const.notNullString(map.get("qty"), "-"));
                Unit = (Const.notNullString(map.get("unit"), "-"));
                SaleRate = (Const.notNullString(map.get("rate"), "-"));
                SaleAmt = (Const.notNullString(map.get("amount"), "-"));

                data += "<TR>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + SrNo + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + InvoiceDate + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + InvoiceNo + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + BookName + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + PartyName + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + ProdGrpName + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + ProdName + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + Pcs + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + Qty + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + Unit + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + SaleRate + "</TD>";
                data += "<TD align='center' style =\"word-wrap: break-word;font-size:12px;\">" + SaleAmt + "</TD>";

            }
            data += "<TR>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>Total</b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>" + tvTotalPcs.getText().toString() + "</b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>" + tvTotalQty.getText().toString() + "</b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b></b></TD>";
            data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\"><b>" + tvTotalAmt.getText().toString() + "</b></TD>/TR>";
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


