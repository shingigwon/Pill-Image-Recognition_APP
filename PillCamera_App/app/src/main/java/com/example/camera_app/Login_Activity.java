package com.example.camera_app;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.camera_app.Service.ServiceAPIInstance;
import com.example.camera_app.Service.ServiceAPIResponse;
import com.example.camera_app.Service.ServiceAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login_Activity extends AppCompatActivity {

    //Server
    private ServiceAPI service = ServiceAPIInstance.getInstance();

    //widget
    private EditText login_id, login_password;
    private Button login_button, join_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //edit
        login_id = findViewById(R.id.login_id);
        login_password = findViewById(R.id.login_password);

        //btn
        join_button = findViewById(R.id.join_button);
        login_button = findViewById(R.id.login_button);

        //회원가입 폼 event
        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent joinintent = new Intent(getApplicationContext(), Join_Activity.class);
                startActivity(joinintent);
                login_id.setText("");
                login_password.setText("");
            }
        });

        //로그인 event -> main
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = login_id.getText().toString();
                String pw = login_password.getText().toString();
                login(id, pw);
                login_id.setText("");
                login_password.setText("");
            }
        });
    }

    //server login post
    private void login(String id, String password) {
        service.postLogin(id, password).enqueue(new Callback<ServiceAPIResponse>() {
            @Override
            public void onResponse(Call<ServiceAPIResponse> call, Response<ServiceAPIResponse> response) {
                ServiceAPIResponse result = response.body();
                boolean success = result.getSuccess();
                String userName = result.getName();
                if (success) {
                    Log.d("LoginSuccess 이름 : ", userName);
                    Toast.makeText(Login_Activity.this,"로그인 성공!",Toast.LENGTH_SHORT).show();

                    Intent mainintent = new Intent(getApplicationContext(), Main_Activity.class);
                    mainintent.putExtra("UserID", id);
                    mainintent.putExtra("UserName", userName);
                    startActivity(mainintent);

                }
                else{
                    Log.e("LoginActivity login", "에러 = " + userName);
                    Toast.makeText(Login_Activity.this,"로그인 실패!",Toast.LENGTH_SHORT).show();
                }



            }

            @Override
            public void onFailure(Call<ServiceAPIResponse> call, Throwable t) {
                // Handle failure
                Log.e("LoginActivity login server", "에러 = " + t.getMessage());
            }
        });
    }
}