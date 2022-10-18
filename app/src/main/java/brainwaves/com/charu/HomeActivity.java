package brainwaves.com.charu;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import brainwaves.com.charu.Class.VolleyCallback;
import brainwaves.com.charu.Utils.Const;
import brainwaves.com.charu.Utils.Pref;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

   // ImageView imgMenu;
 //   ScrollView svScroll;
 //   Button rlLogout;
//    RelativeLayout rlHome, rlProfile, rlLedgerReport, rlBookReport, rlStockReport, rlStockHistoryReport, rlSalesReport, rlPurchaseReport, rlTargetReport, rlRegisterReport, rlTransCloseReport, rlContactUs;
    ImageView  imgProfile, imgLedger, imgBook, imgStock, imgStockHistory, imgSales, imgPurchase, imgTarget, imgRegister, imgTrans, imgContactUs, imgLogout;
 //   NestedScrollView nestedscrollview;
 //   DrawerLayout drawer_layout;
//    TextView tvBtnLogin,tvUserName;
 //   CircleImageView imgSetProfile;
    TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        tvWelcome = (TextView) findViewById(R.id.tvWelcome);

       // imgMenu = (ImageView) findViewById(R.id.imgMenu);
       // svScroll = (ScrollView) findViewById(R.id.svScroll);

//        rlHome = (RelativeLayout) findViewById(R.id.rlHome);
//        rlProfile = (RelativeLayout) findViewById(R.id.rlProfile);
//        rlLogout = (Button) findViewById(R.id.rlLogout);

        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        imgLedger = (ImageView) findViewById(R.id.imgLedger);
        imgBook = (ImageView) findViewById(R.id.imgBook);
        imgStock = (ImageView) findViewById(R.id.imgStock);
        imgStockHistory = (ImageView) findViewById(R.id.imgStockHistory);
        imgSales = (ImageView) findViewById(R.id.imgSales);
        imgPurchase = (ImageView) findViewById(R.id.imgPurchase);
        imgTarget = (ImageView) findViewById(R.id.imgTarget);
        imgRegister = (ImageView) findViewById(R.id.imgRegister);
        imgTrans = (ImageView) findViewById(R.id.imgTrans);
        imgContactUs = (ImageView) findViewById(R.id.imgContactUs);
        imgLogout = (ImageView) findViewById(R.id.imgLogout);

//        rlLedgerReport = (RelativeLayout) findViewById(R.id.rlLedgerReport);
//        rlBookReport = (RelativeLayout) findViewById(R.id.rlBookReport);
//        rlStockReport = (RelativeLayout) findViewById(R.id.rlStockReport);
//        rlStockHistoryReport = (RelativeLayout) findViewById(R.id.rlStockHistoryReport);
//        rlSalesReport = (RelativeLayout) findViewById(R.id.rlSalesReport);
//        rlPurchaseReport = (RelativeLayout) findViewById(R.id.rlPurchaseReport);
//        rlTargetReport = (RelativeLayout) findViewById(R.id.rlTargetReport);
//        rlRegisterReport = (RelativeLayout) findViewById(R.id.rlRegisterReport);
//        rlTransCloseReport = (RelativeLayout) findViewById(R.id.rlTransCloseReport);
//        rlContactUs = (RelativeLayout) findViewById(R.id.rlContactUs);
//        tvBtnLogin = (TextView) findViewById(R.id.tvBtnLogin);
//        tvUserName = (TextView) findViewById(R.id.tvUserName);
//
//        nestedscrollview = (NestedScrollView) findViewById(R.id.nestedscrollview);
//        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        imgSetProfile = (CircleImageView) findViewById(R.id.imgSetProfile);
//        imgLogout = (ImageView) findViewById(R.id.imgLogout);

        Animation an2 = AnimationUtils.loadAnimation(this, R.anim.blink);
        tvWelcome.startAnimation(an2);

//        if (Pref.getStringValue(HomeActivity.this, Const.loginID, "").equalsIgnoreCase("")) {
//            rlLogout.setVisibility(View.GONE);
//        }

//        if (!Pref.getStringValue(HomeActivity.this, Const.loginID, "").equalsIgnoreCase("")) {
//            tvBtnLogin.setText("Profile");
//            tvBtnLogin.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
//                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//
//                }
//            });
//        } else {
//            tvBtnLogin.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
//                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//
//                }
//            });
//        }

//        tvUserName.setText(Const.notNullString(Pref.getStringValue(this, Const.loginName, ""), getString(R.string.app_name)));
//
//        rlHome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                drawer_layout.closeDrawers();
//            }
//        });

//        imgMenu.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("WrongConstant")
//            @Override
//            public void onClick(View v) {
//                drawer_layout.openDrawer(Gravity.START);
//            }
//        });

//        rlProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawer_layout.closeDrawers();
//                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//
//            }
//        });

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

//        rlLedgerReport.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawer_layout.closeDrawers();
//                startActivity(new Intent(HomeActivity.this, LedgerReportActivity.class));
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//
//            }
//        });

        imgLedger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, LedgerReportActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

//        rlBookReport.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawer_layout.closeDrawers();
//                startActivity(new Intent(HomeActivity.this, BookReportActivity.class));
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//
//            }
//        });

        imgBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, BookReportActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

//        rlStockReport.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawer_layout.closeDrawers();
//                startActivity(new Intent(HomeActivity.this, StockReportActivity.class));
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//
//            }
//        });

        imgStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, StockReportActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

//        rlStockHistoryReport.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawer_layout.closeDrawers();
//                startActivity(new Intent(HomeActivity.this, StockHistoryReportActivity.class));
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//            }
//        });

        imgStockHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, StockHistoryReportActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

//        rlSalesReport.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawer_layout.closeDrawers();
//                startActivity(new Intent(HomeActivity.this, SalesReportActivity.class));
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//
//            }
//        });

        imgSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, SalesReportActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

//        rlPurchaseReport.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawer_layout.closeDrawers();
//                startActivity(new Intent(HomeActivity.this, PurchaseReportActivity.class));
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//
//            }
//        });

        imgPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, PurchaseReportActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

//        rlTargetReport.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawer_layout.closeDrawers();
//                startActivity(new Intent(HomeActivity.this, TargetReportActivity.class));
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//
//            }
//        });

        imgTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, TargetReportActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });


//        rlRegisterReport.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawer_layout.closeDrawers();
//                startActivity(new Intent(HomeActivity.this, RegisterReportActivity.class));
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//
//            }
//        });

        imgRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, RegisterReportActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

//        rlTransCloseReport.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawer_layout.closeDrawers();
//                startActivity(new Intent(HomeActivity.this, TransClosingTrialReportActivity.class));
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//
//            }
//        });

        imgTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, TransClosingTrialReportActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

//        rlContactUs.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawer_layout.closeDrawers();
//                startActivity(new Intent(HomeActivity.this, ContactUsActivity.class));
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//
//            }
//        });

        imgContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ContactUsActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

//        rlLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawer_layout.closeDrawers();
//                openLogoutDialog();
//            }
//        });

        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLogoutDialog();
            }
        });
    }
    private void openLogoutDialog() {
        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.layout_logout_dialog);

        Button btnYes = (Button) dialog.findViewById(R.id.btnYes);
        Button btnNo = (Button) dialog.findViewById(R.id.btnNo);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Pref.removeValue(HomeActivity.this, Const.Islogin);
                Pref.removeValue(HomeActivity.this, Const.loginID);
                Pref.removeValue(HomeActivity.this, Const.UserName);
                Pref.removeValue(HomeActivity.this, Const.Password);
                Pref.removeValue(HomeActivity.this, Const.loginRoleName);
                Pref.removeValue(HomeActivity.this, Const.loginRoleCode);
                Pref.removeValue(HomeActivity.this, Const.loginPassword);
                Pref.removeValue(HomeActivity.this, Const.loginMobile);
                Pref.removeValue(HomeActivity.this, Const.loginEmail);
                Pref.removeValue(HomeActivity.this, Const.loginName);
                Pref.removeValue(HomeActivity.this, Const.loginToken);
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                }
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        this.recreate();
    }
}

