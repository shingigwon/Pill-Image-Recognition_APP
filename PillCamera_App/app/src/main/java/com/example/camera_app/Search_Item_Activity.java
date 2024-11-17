package com.example.camera_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.camera_app.Service.ListViewAdapterResult;
import com.example.camera_app.Service.ServiceAPI;
import com.example.camera_app.Service.ServiceAPIInstance;
import com.example.camera_app.Service.ServiceAPIResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.ByteArrayOutputStream;
import java.util.List;


public class Search_Item_Activity extends AppCompatActivity {

    private ServiceAPI service = ServiceAPIInstance.getInstance();

    ProgressDialog progressDialog;
    ListView mlistView;
    ListViewAdapterResult Result_adapter;

    String userid, filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        userid = getIntent().getStringExtra("UserID");
        filename = getIntent().getStringExtra("FileName");
        Result_adapter = new  ListViewAdapterResult();
        mlistView = (ListView) findViewById(R.id.result_listView);

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bitmap image  = Result_adapter.getPosImage(position);
                String item_name= Result_adapter.getPosItem_Name(position);
                String company = Result_adapter.getPosCompony(position);
                String cn = Result_adapter.getPosClassification_Name(position);
                String EE = Result_adapter.getPosEE(position);
                String UD = Result_adapter.getPosUD(position);
                String NB = Result_adapter.getPosNB(position);
                show_Detail(image, item_name, company, cn, EE, UD, NB);
            }
        });

        OnSetting(userid, filename);
    }

    private void OnSetting(String userid, String filename){
        // 작업을 시작하기 전에 프로그레스 다이얼로그를 표시합니다.
        progressDialog = ProgressDialog.show(Search_Item_Activity.this, "Please Wait.", "검색중입니다", true, true);
        ResultPillList(userid, filename);
    }


    private void ResultPillList(String userId, String fileName) {

        Call<List<ServiceAPIResponse>> call = service.downloadImage(userId, fileName);
        call.enqueue(new Callback<List<ServiceAPIResponse>>() {
            @Override
            public void onResponse(Call<List<ServiceAPIResponse>> call, Response<List<ServiceAPIResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("Seearch_ResultPillList", "성공 ");
                    showResult(response.body());
                } else {
                    Log.e("Seearch_ResultPillList", "실패");
                    Toast.makeText(Search_Item_Activity.this, "응답 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ServiceAPIResponse>> call, Throwable t) {
                // 요청 실패 처리
                Log.e("Seearch_ResultPillList", "요청 실패");
                Toast.makeText(Search_Item_Activity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showResult(List<ServiceAPIResponse> list){

        for (ServiceAPIResponse result : list) {
            String base64Image = result.getImage();
            // Base64 디코딩하여 비트맵으로 변환
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap pillimage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            String item_name = result.getFileName();
            String company = result.getCompany();
            String cn = result.getClassification_name();
            String EE = result.getEE();
            String UD = result.getUD();
            String NB = result.getNB();
//            String serialNumber = data.getFileName();
            Result_adapter.addItem(pillimage, item_name, company, cn, EE, UD, NB);
        }
        Result_adapter.notifyDataSetChanged();  // 어댑터 데이터 변경 알림


        mlistView.setAdapter(Result_adapter);

        // 작업이 완료되었으므로 ProgressDialog를 제거합니다.
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }



    public String encodeBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void show_Detail (Bitmap image, String item_name, String company, String cn, String EE, String UD, String NB){
        Intent intent = new Intent(getApplicationContext(), Detail_Item_Activity.class);
        String encodedBitmap = encodeBitmapToBase64(image);

        intent.putExtra("Image", encodedBitmap);
        intent.putExtra("item_name", item_name);
        intent.putExtra("company", company);
        intent.putExtra("cn", cn);
        intent.putExtra("EE", EE);
        intent.putExtra("UD", UD);
        intent.putExtra("NB", NB);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.transition.anim_slide_in_left, R.transition.anim_slide_out_right);

        Toast.makeText(getApplicationContext()," 자세히 보기가 눌렸습니다.", Toast.LENGTH_SHORT).show();

    }
//        Call<List<ServiceAPIResponse>> call = service.downloadImage(userId, fileName);
//        call.enqueue(new Callback<ServiceAPIResponse>() {
//            @Override
//            public void onResponse(Call<List<ServiceAPIResponse>> call, Response<List<ServiceAPIResponse>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ServiceAPIResponse result = response.body();
//                    if (result != null) {
//                        String base64Image = result.getImage();
//                        String item_name = result.getFileName();
//
//                        String EE = result.getEE();
//                        String UD = result.getUD();
//                        String NB = result.getNB();
//
//                        // Base64 디코딩하여 비트맵으로 변환
//                        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
//                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//
//                        showResult(bitmap, item_name);
//
//                        Log.d("Search_Activity : ", "해당 이미지 찾음!");
//                        Toast.makeText(Search_Item_Activity.this, "성공!", Toast.LENGTH_SHORT).show();
//
//                        // 작업이 완료되었으므로 ProgressDialog를 제거합니다.
//                        if (progressDialog != null && progressDialog.isShowing()) {
//                            progressDialog.dismiss();
//                        }
//
//                    } else
//                        // 응답 데이터가 없는 경우에 대한 처리
//                        Log.e("Search_Activity", "해당 이미지 못 찾음");
//                } else
//                    Log.e("Search_Activity", "ResponseBody null");
//            }
//
//            @Override
//            public void onFailure(Call<List<ServiceAPIResponse>> call, Throwable t) {
//                // Handle failure
//                Log.e("Search_Activity", "에러 = " + t.getMessage());
//            }
//        });
}
