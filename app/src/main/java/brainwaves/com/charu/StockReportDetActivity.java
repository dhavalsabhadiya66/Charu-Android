package brainwaves.com.charu;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

public class StockReportDetActivity extends AppCompatActivity {

    Toolbar toolBar;
    StockReportAdp adp;
    ImageView imgShare;
    TextView tvAction,tvNoData;
    RecyclerView rvList;
    LinearLayout llNoData, llHeader, llFilter;
    EditText etDate;
    Button btnSearch, btnClear;
    ArrayList<HashMap<String, String>> StockReportArray = new ArrayList<>();
    int pastVisiblesItems, visibleItemCount, totalItemCount, pageCount = 1, totalSize = 0;
    LinearLayoutManager lm;
    String Date = "", MainGroupName = "", ProdGrpCode = "", ProdCode = "";
    Context context = StockReportDetActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_report_det);

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
        btnSearch = findViewById(R.id.btnSearch);
        btnClear = findViewById(R.id.btnClear);
        imgShare = findViewById(R.id.imgShare);

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
            Date = bundle.getString("Date");
            MainGroupName = bundle.getString("MainGroupName");
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
                        if (totalSize != StockReportArray.size()) {
                            pageCount++;
                            getStockReport();
                        }
                    }
                }
            }
        });

        pageCount = 1;
        StockReportArray = new ArrayList<>();
        getStockReport();

    }

    private void getStockReport() {
        Const.showProgress(this);
        // StockReportArray = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put(Const.CompCode, Pref.getStringValue(context, Const.loginCompCode, ""));
        map.put(Const.Date,  Const.notNullString(Date,""));
        map.put("MainGrpName",  Const.notNullString(MainGroupName,""));
        map.put("ProdGrpCode",  Const.notNullString(ProdGrpCode,""));
        map.put("ProdCode",  Const.notNullString(ProdCode,""));
        map.put(Const.PageNo, pageCount + "");
        Const.callPostApi(context, Const.BaseUrl + "GetStockReport?", map, new VolleyCallback() {
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
                                StockReportArray.add(hm);
                            }
                            if (pageCount == 1) {
                                adp = new StockReportAdp(context, StockReportArray);
                                rvList.setAdapter(adp);
                            }
//                            adp = new StockReportAdp(context, StockReportArray);
//                            rvList.setAdapter(adp);
                            adp.notifyDataSetChanged();
                            llHeader.setVisibility(View.VISIBLE);
                            llNoData.setVisibility(View.GONE);
                        } else {
                            llHeader.setVisibility(View.GONE);
                            llNoData.setVisibility(View.VISIBLE);
                            tvNoData.setText("Data Not Available");
                            rvList.setAdapter(null);
                        }
                    }else {
                        llHeader.setVisibility(View.GONE);
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
                        getStockReport();
                    }
                };
                Const.showErrorApiDialog(context, runnable);
            }
        });
    }

    private class StockReportAdp extends RecyclerView.Adapter<StockReportAdp.RecyclerViewHolder> {

        ArrayList<HashMap<String, String>> arraylist;
        Context context;

        public StockReportAdp(Context context, ArrayList<HashMap<String, String>> arraylist) {
            this.arraylist = arraylist;
            this.context = context;
        }

        @Override
        public StockReportAdp.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_stock_report, parent, false);
            StockReportAdp.RecyclerViewHolder viewHolder = new StockReportAdp.RecyclerViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final StockReportAdp.RecyclerViewHolder holder, final int position) {

            final HashMap<String, String> map = arraylist.get(position);

            holder.tvSerialNo.setText((position + 1) + ".");

            holder.tvSKU.setText(Const.notNullString(map.get("short_code"), "-"));
            holder.tvMainGroup.setText(Const.notNullString(map.get("main_group_name"), "-"));
            holder.tvSubGroup.setText(Const.notNullString(map.get("stone_type_name"), "-"));
            holder.tvCarMake.setText(Const.notNullString(map.get("car_make"), "-"));
            holder.tvCarModel.setText(Const.notNullString(map.get("model_no"), "-"));
            holder.tvYear.setText(Const.notNullString(map.get("car_year"), "-"));
            holder.tvItemName.setText(Const.notNullString(map.get("cate_name"), "-"));
            holder.tvStock.setText(Const.notNullString(map.get("qty"), "-"));
            holder.tvPurchase.setText(Const.notNullString(map.get("pur_value"), "-"));
            holder.tvSpPrice.setText(Const.notNullString(map.get("spacial_value"), "-"));
            holder.tvDealerPrice.setText(Const.notNullString(map.get("dealer_rate"), "-"));
            holder.tvCustPrice.setText(Const.notNullString(map.get("customer_value"), "-"));
            holder.tvIncShip.setText(Const.notNullString(map.get("included_ship_value"), "-"));
            holder.tvMRP.setText(Const.notNullString(map.get("mrp"), "-"));
            holder.tvWebProdName.setText(Const.notNullString(map.get("web_prod_name"), "-"));
            holder.tvBrand.setText(Const.notNullString(map.get("brand_name"), "-"));
            holder.tvMaterial.setText(Const.notNullString(map.get("material"), "-"));
            holder.tvColor.setText(Const.notNullString(map.get("color"), "-"));
            holder.tvPackDetail.setText(Const.notNullString(map.get("pack_det"), "-"));
            holder.tvWarranty.setText(Const.notNullString(map.get("warranty"), "-"));
            holder.tvWeight.setText(Const.notNullString(map.get("weight"), "-"));
            holder.tvWidth.setText(Const.notNullString(map.get("width"), "-"));
            holder.tvLength.setText(Const.notNullString(map.get("length"), "-"));
            holder.tvHeight.setText(Const.notNullString(map.get("height"), "-"));

            Glide.with(context)
                    .load(map.get("image_link"))
                    .thumbnail(0.5f)
                    .crossFade()
                    .fitCenter().override(200, 200)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.ic_no_image)
                    .into(holder.ivImage);

            holder.ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(context, R.style.Theme_Dialog);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
                    dialog.getWindow().setBackgroundDrawable(Const.setBackgoundBorder(0, 20, Color.WHITE, Color.WHITE));
                    dialog.setContentView(R.layout.layout_full_screen);

                    final ImageView vpImage = dialog.findViewById(R.id.vpImage);
                    final ImageView imgClose = dialog.findViewById(R.id.imgClose);

                    Animation anim = AnimationUtils.loadAnimation(context, R.anim.rotate);
                    imgClose.startAnimation(anim);

                    Glide.with(context)
                            .load(map.get("image_link"))
                            .thumbnail(0.5f)
                            .crossFade()
                            .fitCenter().override(800, 800)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(R.drawable.ic_no_image)
                            .into(vpImage);

                    imgClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });

        }

        @Override
        public int getItemCount() {
            return arraylist.size();
        }

        public class RecyclerViewHolder extends RecyclerView.ViewHolder {

            TextView tvSerialNo, tvSKU, tvMainGroup, tvSubGroup, tvCarMake, tvCarModel, tvYear, tvItemName, tvStock,
                    tvPurchase, tvSpPrice, tvDealerPrice, tvCustPrice, tvIncShip, tvMRP, tvWebProdName, tvBrand, tvMaterial,
                    tvColor, tvPackDetail, tvWarranty, tvWeight, tvWidth, tvLength, tvHeight;
            ImageView ivImage;
            public RecyclerViewHolder(View v) {
                super(v);

                tvSerialNo = v.findViewById(R.id.tvSerialNo);
                tvSKU = v.findViewById(R.id.tvSKU);
                ivImage = v.findViewById(R.id.ivImage);
                tvMainGroup = v.findViewById(R.id.tvMainGroup);
                tvSubGroup = v.findViewById(R.id.tvSubGroup);
                tvCarMake = v.findViewById(R.id.tvCarMake);
                tvCarModel = v.findViewById(R.id.tvCarModel);
                tvYear = v.findViewById(R.id.tvYear);
                tvItemName = v.findViewById(R.id.tvItemName);
                tvStock = v.findViewById(R.id.tvStock);
                tvPurchase = v.findViewById(R.id.tvPurchase);
                tvSpPrice = v.findViewById(R.id.tvSpPrice);
                tvDealerPrice = v.findViewById(R.id.tvDealerPrice);
                tvCustPrice = v.findViewById(R.id.tvCustPrice);
                tvIncShip = v.findViewById(R.id.tvIncShip);
                tvMRP = v.findViewById(R.id.tvMRP);
                tvWebProdName = v.findViewById(R.id.tvWebProdName);
                tvBrand = v.findViewById(R.id.tvBrand);
                tvMaterial = v.findViewById(R.id.tvMaterial);
                tvColor = v.findViewById(R.id.tvColor);
                tvPackDetail = v.findViewById(R.id.tvPackDetail);
                tvWarranty = v.findViewById(R.id.tvWarranty);
                tvWeight = v.findViewById(R.id.tvWeight);
                tvWidth = v.findViewById(R.id.tvWidth);
                tvLength = v.findViewById(R.id.tvLength);
                tvHeight = v.findViewById(R.id.tvHeight);
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

            String Header = "Stock Report";

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

            data += "<TR><td align='center' style=\"font-size:15pt;font-family:'Open Sans';\" colspan=\"25\"><b>" + Header + "</b></td>";
            data += "</TR>";

            data += "<TR bgcolor=\"#E4DDC2\">";

            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Sr.No</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>SKU</b></TH>";
            // data += "<TH align='center' style=\"white-space: nowrap;\"><b>Image</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Main roup</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Sub Group</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Car Make</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Car Model</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Year</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Item Name</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Stock</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Purchase</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Special Price(₹)</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Dealer Price(₹)</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Customer Price(₹)</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Included Ship(₹)</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>MRP(₹)</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Web ProdName</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Brand</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Material</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Color</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>PackDetail</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Warranty</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Weight</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Width</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Length</b></TH>";
            data += "<TH align='center' style=\"white-space: nowrap;\"><b>Height</b></TH>";
            data += "</TR>";

            for (int i = 0; i < StockReportArray.size(); i++) {
                String SrNo = "";
                String SKU = "";
                //String Image = "";
                String MainGroup = "";
                String SubGroup = "";
                String CarMake = "";
                String CarModel = "";
                String Year = "";
                String ItemName = "";
                String Stock = "";
                String Purchase = "";
                String SpPrice  = "";
                String DealerPrice = "";
                String CustPrice = "";
                String IncShip = "";
                String MRP = "";
                String WebProdName = "";
                String Brand = "";
                String Material = "";
                String Color = "";
                String PackDetail = "";
                String Warranty = "";
                String Weight = "";
                String Width = "";
                String Length = "";
                String Height = "";


                final HashMap<String, String> map = StockReportArray.get(i);

                SrNo = ((i + 1) + ".");
                SKU = (Const.notNullString(map.get("short_code"), "-"));
                MainGroup = (Const.notNullString(map.get("main_group_name"), "-"));
                SubGroup = (Const.notNullString(map.get("stone_type_name"), "-"));
                CarMake = (Const.notNullString(map.get("car_make"), "-"));
                CarModel = (Const.notNullString(map.get("model_no"), "-"));
                Year = (Const.notNullString(map.get("car_year"), "-"));
                ItemName = (Const.notNullString(map.get("cate_name"), "-"));
                Stock = (Const.notNullString(map.get("qty"), "-"));
                Purchase = (Const.notNullString(map.get("pur_value"), "-"));
                SpPrice = (Const.notNullString(map.get("spacial_value"), "-"));
                DealerPrice = (Const.notNullString(map.get("dealer_rate"), "-"));
                CustPrice = (Const.notNullString(map.get("customer_value"), "-"));
                IncShip = (Const.notNullString(map.get("included_ship_value"), "-"));
                MRP = (Const.notNullString(map.get("mrp"), "-"));
                WebProdName = (Const.notNullString(map.get("web_prod_name"), "-"));
                Brand = (Const.notNullString(map.get("brand_name"), "-"));
                Material = (Const.notNullString(map.get("material"), "-"));
                Color = (Const.notNullString(map.get("color"), "-"));
                PackDetail = (Const.notNullString(map.get("pack_det"), "-"));
                Warranty = (Const.notNullString(map.get("warranty"), "-"));
                Weight = (Const.notNullString(map.get("weight"), "-"));
                Width = (Const.notNullString(map.get("width"), "-"));
                Length = (Const.notNullString(map.get("length"), "-"));
                Height = (Const.notNullString(map.get("height"), "-"));

//                if (!map.get("image_name").equalsIgnoreCase("")) {
//                    Image = map.get("image_name").toLowerCase();
//                }

                data += "<TR>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + SrNo + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + SKU  + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + MainGroup + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + SubGroup + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + CarMake + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + CarModel + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + Year  + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + ItemName + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + Stock  + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + Purchase + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + SpPrice + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + DealerPrice + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + CustPrice + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + IncShip + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + MRP   + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + WebProdName + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + Brand  + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + Material + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + Color  + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + PackDetail + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + Warranty + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + Weight + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + Width  + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + Length  + "</TD>";
                data += "<TD align='center' style =\"white-space: nowrap;font-size:12px;\">" + Height  + "</TD>";


//                if (!Image.equalsIgnoreCase("")) {
//                    data += "<TD align='center'> <img src='" + Image + "' alt=\"\" width=\"80%\" height=\"100\"  /> </TD></TR>";
//                } else {
//                    data += "<TD>  </TD></TR>";
//                }
            }
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


