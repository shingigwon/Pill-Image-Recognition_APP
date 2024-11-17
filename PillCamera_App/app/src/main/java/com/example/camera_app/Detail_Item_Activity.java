package com.example.camera_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.camera_app.Service.ListViewAdapterDetail;


public class Detail_Item_Activity extends AppCompatActivity {

//    private ServiceAPI service = ServiceAPIInstance.getInstance();

    ListView taplistView;
    ListViewAdapterDetail Detail_adapter;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_item);

//        String userid = getIntent().getStringExtra("UserID");
//        String filename = getIntent().getStringExtra("FileName");
        // 추가
        Intent intent = getIntent();
        String encodedImage = intent.getStringExtra("Image");
        byte[] decodedByte = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);

        String item_name = intent.getStringExtra("item_name");
        String company = intent.getStringExtra("company");
        String cn = intent.getStringExtra("cn");
        String EE = intent.getStringExtra("EE");
        String UD = intent.getStringExtra("UD");
        String NB = intent.getStringExtra("NB");
        //

        TabHost tabHost1 = (TabHost) findViewById(R.id.tabHost1) ;
        tabHost1.setup() ;


        // 첫 번째 Tab. (탭 표시 텍스트:"TAB 1"), (페이지 뷰:"content1")
        TabHost.TabSpec ts1 = tabHost1.newTabSpec("Tab Spec 1") ;
        ts1.setContent(R.id.content1) ;
        ts1.setIndicator("기본 정보") ;
        tabHost1.addTab(ts1) ;


        // 두 번째 Tab. (탭 표시 텍스트:"TAB 2"), (페이지 뷰:"content2")
        TabHost.TabSpec ts2 = tabHost1.newTabSpec("Tab Spec 2") ;
        ts2.setContent(R.id.content2) ;
        ts2.setIndicator("효능 효과") ;
        tabHost1.addTab(ts2) ;
        TextView tab2 = (TextView)findViewById(R.id.tab2);
//        tab2.setText("효능 효과"); //여기에다가 정보 입력
        // 추가
        tab2.setText(EE); //여기에다가 정보 입력


        // 세 번째 Tab. (탭 표시 텍스트:"TAB 3"), (페이지 뷰:"content3")
        TabHost.TabSpec ts3 = tabHost1.newTabSpec("Tab Spec 3") ;
        ts3.setContent(R.id.content3) ;
        ts3.setIndicator("주의 사항") ;
        tabHost1.addTab(ts3) ;
        TextView tab3 = (TextView)findViewById(R.id.tab3);
//        tab3.setText("주의 사항"); //여기에다가 정보 입력항
        // 추가
        tab3.setText(NB); //여기에다가 정보 입력항


        Detail_adapter = new ListViewAdapterDetail();
        taplistView = (ListView) findViewById(R.id.tab1_listView);

        // 추가
        Detail_adapter.addItem(bitmap, item_name, company, cn, UD);
        taplistView.setAdapter(Detail_adapter);
        //

//        OnSetting(userid, filename);
    }

    public void show_detail_back(View v) {
        finish();
        Intent intent = new Intent(getApplicationContext(), Main_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.transition.anim_slide_a, R.transition.anim_slide_b);

    }
//    @Override
//    public void onBackPressed() {
//        finish();
//        overridePendingTransition(R.transition.anim_slide_a, R.transition.anim_slide_b);
//    }

//    public void add_pill(View v) {
//        Intent intent = new Intent(getApplicationContext(), newpage.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        overridePendingTransition(R.transition.anim_slide_in_left, R.transition.anim_slide_out_right);
//
//    }
    private void OnSetting(String userid, String filename){
        // 작업을 시작하기 전에 프로그레스 다이얼로그를 표시합니다.
        progressDialog = ProgressDialog.show(Detail_Item_Activity.this, "Please Wait", null, true, true);
//        showDetails(userid, filename);
    }

//    private void showDetails(String userId, String fileName) {
//
//        Call<ServiceAPIResponse> call = service.downloadImage(userId, fileName);
//        call.enqueue(new Callback<ServiceAPIResponse>() {
//            @Override
//            public void onResponse(Call<ServiceAPIResponse> call, Response<ServiceAPIResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ServiceAPIResponse result = response.body();
//
//                    if (result != null) {
//                        String base64Image = result.getImage();
//                        String item_name = result.getFileName();
//                        String company = result.getCompany();
//                        String cn = result.getClassification_name();
//                        String EE = result.getEE();
//                        String UD = result.getUD();
//                        String NB = result.getNB();
//
//                        // Base64 디코딩하여 비트맵으로 변환
//                        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
//                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//
//
//                        Detail_adapter.addItem(bitmap, item_name, company, cn, UD);
//
//                        TextView tab2 = (TextView) findViewById(R.id.tab2);
//                        tab2.setText(EE);
//
//                        TextView tab3 = (TextView) findViewById(R.id.tab3);
//                        tab3.setText(NB);
//
//                        taplistView.setAdapter(Detail_adapter);
//
//                        Log.d("Detail_Item_Activity : ", "해당 이미지 찾음!");
//                        Toast.makeText(Detail_Item_Activity.this, "성공!", Toast.LENGTH_SHORT).show();
//
//                        // 작업이 완료되었으므로 ProgressDialog를 제거합니다.
//                        if (progressDialog != null && progressDialog.isShowing()) {
//                            progressDialog.dismiss();
//                        }
//
//                    } else
//                        // 응답 데이터가 없는 경우에 대한 처리
//                        Log.e("Detail_Item_Activity", "해당 이미지 못 찾음");
//                } else
//                    Log.e("Detail_Item_Activity", "ResponseBody null");
//            }
//
//            @Override
//            public void onFailure(Call<ServiceAPIResponse> call, Throwable t) {
//                // Handle failure
//                Log.e("Detail_Item_Activity", "에러 = " + t.getMessage());
//            }
//        });
//    }

}