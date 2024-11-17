package com.example.camera_app;

import java.util.regex.*;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.camera_app.Service.ServiceAPI;
import com.example.camera_app.Service.ServiceAPIInstance;
import com.example.camera_app.Service.ServiceAPIResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Join_Activity extends AppCompatActivity {


    private ServiceAPI service = ServiceAPIInstance.getInstance();

    private EditText join_name, join_id, join_pw, join_pw2;
    private ImageButton backbutton;
    private Button checkid_button, join_button, cancel_button;

    boolean ischeckid = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        join_name = findViewById(R.id.join_name);
        join_id = findViewById(R.id.join_id);
        join_pw = findViewById(R.id.join_pw);
        join_pw2 = findViewById(R.id.join_pw2);

        //btn
        backbutton = findViewById(R.id.backbutton);

        checkid_button = findViewById(R.id.checkid_button);
        join_button = findViewById(R.id.join_button);
        cancel_button = findViewById(R.id.cancel_button);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // id중복체크 -> select
        checkid_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = join_id.getText().toString();
                checkID(id);
            }
        });
        //회원가입 btn -> insert
        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = join_name.getText().toString();
                String id = join_id.getText().toString();
                String pw = join_pw.getText().toString();
                String pw2 = join_pw2.getText().toString();

                if(ischeckid){
                    if(pw.equals(pw2)) {
//                        isValidRegistration()
                        Join(name,id, pw);
                        finish();
                    }
                    else{
                        Toast.makeText(Join_Activity.this, "비밀번호 일치여부 확인", Toast.LENGTH_SHORT).show();
                        Log.e("Check PassWord : ", "비밀번호 일치여부 확인");}
                }
                else {
                    Toast.makeText(Join_Activity.this, "아이디 중복체크 버튼 클릭", Toast.LENGTH_SHORT).show();
                    Log.e("Click CheckID Button : ", "아이디 중복체크 버튼 클릭");
                }
            }
        });
        //취소 btn -> -1
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    //server login post
    private void checkID(String id) {
        service.postCheckID(id).enqueue(new Callback<ServiceAPIResponse>() {
            @Override
            public void onResponse(Call<ServiceAPIResponse> call, Response<ServiceAPIResponse> response) {
                ServiceAPIResponse result = response.body();
                ischeckid = result.getSuccess();
                if (ischeckid) {
                    Log.e("isCheckID True : ", "ID중복");
                    Toast.makeText(Join_Activity.this, "ID중복", Toast.LENGTH_SHORT).show();
                    checkid_button.setEnabled(true);
                } else {
                    Log.d("isCheckID False", "사용가능");
                    //btn readonly
                    ischeckid=true;
                    checkid_button.setEnabled(false);
                }
            }
            @Override
            public void onFailure(Call<ServiceAPIResponse> call, Throwable t) {
                // Handle failure
                Log.e("JoinActivity", "에러 = " + t.getMessage());
            }
        });
    }

    //server join post
    private void Join(String name, String id, String pw) {
        service.postJoin(name, id, pw).enqueue(new Callback<ServiceAPIResponse>() {
            @Override
            public void onResponse(Call<ServiceAPIResponse> call, Response<ServiceAPIResponse> response) {
                ServiceAPIResponse result = response.body();
                boolean success = result.getSuccess();
                if (success) {
                    Toast.makeText(Join_Activity.this, "가입 완료!", Toast.LENGTH_SHORT).show();
                    Log.d("Insert[DB]", "DB Insert 성공");
                } else {
                    Log.e("Error! Not Insert[DB]", "DB Insert 실패");
                }
            }
            @Override
            public void onFailure(Call<ServiceAPIResponse> call, Throwable t) {
                // Handle failure
                Log.e("JoinActivity", "에러 = " + t.getMessage());
            }
        });
    }

    public boolean isValidId(String id) {
        // 4글자 이상 20글자 이하, 영어 또는 숫자만 가능한지 확인
        String regex = "^[a-zA-Z0-9]{4,20}$";
        return Pattern.matches(regex, id);
    }

    public boolean isValidPassword(String password) {
        // 8글자 이상, 30글자 이하, 영문, 숫자, 특수문자 사용 여부 확인
        String regex = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()-_=+\\\\|\\[{\\]};:'\",<.>/?]).{8,30}$";
        return Pattern.matches(regex, password);
    }
    public boolean isValidName(String name) {
        // 1문자라도 있어야 함
        return !name.isEmpty();
    }
    public boolean isValidRegistration(String id, String password, String name) {
        return isValidId(id) && isValidPassword(password) && isValidName(name);
    }

}