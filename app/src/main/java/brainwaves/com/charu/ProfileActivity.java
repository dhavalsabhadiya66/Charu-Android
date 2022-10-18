package brainwaves.com.charu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.HashMap;

import brainwaves.com.charu.Utils.Const;
import brainwaves.com.charu.Utils.Pref;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    TextView tvName, tvEmail, tvMobile, tvAddress, tvPassword;
    CircleImageView profilepic;
    ImageView tvEdit;
    LinearLayout llLogout;

    ArrayList<HashMap<String, String>> MemberReportArray = new ArrayList<>();

    Context context = ProfileActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolBar = findViewById(R.id.toolBar);
        toolBar.setTitle("");
        setSupportActionBar(toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        llLogout = findViewById(R.id.llLogout);
        tvEmail = findViewById(R.id.tvEmail);
        tvMobile = findViewById(R.id.tvMobile);
        tvEdit = findViewById(R.id.tvEdit);
        tvName = findViewById(R.id.tvName);
        tvAddress = findViewById(R.id.tvAddress);
        tvPassword = findViewById(R.id.tvPassword);
        profilepic = findViewById(R.id.profilepic);

        //getUserReport();

        if (Pref.getStringValue(ProfileActivity.this, Const.loginID, "").equalsIgnoreCase("")) {
            llLogout.setVisibility(View.GONE);
        }

        tvName.setText(Const.notNullString(Pref.getStringValue(this, Const.loginName, ""), ""));
        tvEmail.setText(Const.notNullString(Pref.getStringValue(this, Const.loginEmail, ""), ""));
        tvAddress.setText(Const.notNullString(Pref.getStringValue(this, Const.loginAddress, ""), ""));
        tvMobile.setText("+91 " + Const.notNullString(Pref.getStringValue(this, Const.loginMobile, ""), ""));
        tvPassword.setText(Const.notNullString(Pref.getStringValue(this, Const.loginPassword, ""), ""));

        tvEdit.setVisibility(View.GONE);

        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLogoutDialog();
            }
        });
    }

//    private void getUserReport() {
//        Const.showProgress(this);
//        MemberReportArray = new ArrayList<>();
//        Map<String, String> map = new HashMap<>();
//        map.put("UserCode", Pref.getStringValue(this, Const.loginID, ""));
//        map.put("Status", "");
//        map.put("TokenNo", "");
//        Const.callPostApi(ProfileActivity.this, Const.BaseUrl + "UserMasList?", map, new VolleyCallback() {
//            @Override
//            public void onSuccessResponse(String result) {
//                Const.dismissProgress();
//                try {
//                    JSONObject object = new JSONObject(result);
//                    if (object.optString("STATUS").equalsIgnoreCase("1")) {
//                        JSONArray array = object.optJSONArray("Data");
//                        if (array.length() != 0) {
//                            for (int i = 0; i < array.length(); i++) {
//                                JSONObject object1 = array.optJSONObject(i);
//                                Log.d("arrayDetails", array.toString());
//                                Iterator<String> keys = object1.keys();
//                                HashMap<String, String> hm = new HashMap<>();
//                                while (keys.hasNext()) {
//                                    String key = keys.next();
//                                    hm.put(key.toLowerCase(), object1.optString(key).trim());
//                                }
//                                MemberReportArray.add(hm);
//                                tvEdit.setVisibility(View.VISIBLE);
//                                final HashMap<String, String> map = MemberReportArray.get(0);
//                            }
//                        }
//                    }
//                } catch (JSONException e) {
//                    Const.showErrorDialog(ProfileActivity.this, "Something want wrong");
//                }
//            }
//
//            @Override
//            public void onFailerResponse(String error) {
//                Const.dismissProgress();
//                Runnable runnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        getUserReport();
//                    }
//                };
//                Const.showErrorApiDialog(ProfileActivity.this, runnable);
//            }
//        });
//    }

    private void openLogoutDialog() {
        final Dialog dialog = new Dialog(ProfileActivity.this);
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
                Pref.removeValue(ProfileActivity.this, Const.loginID);
                Pref.removeValue(ProfileActivity.this, Const.UserName);
                Pref.removeValue(ProfileActivity.this, Const.Password);
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
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
    public Boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) ProfileActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
