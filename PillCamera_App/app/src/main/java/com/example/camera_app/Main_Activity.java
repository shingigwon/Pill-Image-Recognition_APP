package com.example.camera_app;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Bitmap;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.camera_app.Service.ServiceAPI;
import com.example.camera_app.Service.ServiceAPIInstance;
import com.example.camera_app.Service.ServiceAPIResponse;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Main_Activity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_REQUEST_CODE = 101;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 200;
    private static final int GALLERY_OPENIMAGE_REQUEST_CODE = 201;


    private ServiceAPI service = ServiceAPIInstance.getInstance();

    ImageView imageView;
    Button camerabutton, gallerybutton, sendbutton;
    String userid, username;
    Bitmap getimage;
    boolean isimage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userid = getIntent().getStringExtra("UserID");
        username = getIntent().getStringExtra("UserName");

        imageView = findViewById(R.id.imageView);
        camerabutton = findViewById(R.id.cameraButton);
        gallerybutton = findViewById(R.id.galleryButton);
        sendbutton = findViewById(R.id.sendButton);

        sendbutton.setEnabled(false);

        //btn onclick
        camerabutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });
        gallerybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGalleryPermission();
            }
        });
        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isimage) {
                    uploadImage(getimage);
                }
            }
        });
    }


    //카메라 권한 체크
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(Main_Activity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // 카메라 권한이 없는 경우 권한 요청
            ActivityCompat.requestPermissions(Main_Activity.this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // 카메라 권한이 있는 경우 카메라 앱 열기
            openCamera();
        }
    }

    // 갤러리 권한 체크
    private void checkGalleryPermission() {
        if (ContextCompat.checkSelfPermission(Main_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 갤러리 읽기 권한이 없는 경우 권한 요청
            ActivityCompat.requestPermissions(Main_Activity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    GALLERY_PERMISSION_REQUEST_CODE);
        } else {
            // 갤러리 권한이 있는 경우 갤러리 열기
            openGallery();
        }
    }

    //IsPermission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 카메라 권한이 승인된 경우 카메라 열기
                openCamera();
            } else {
                // 카메라 권한이 거부된 경우 메시지 표시
                Toast.makeText(Main_Activity.this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 갤러리 읽기 권한이 승인된 경우 갤러리 열기
                openGallery();
            } else {
                // 갤러리 읽기 권한이 거부된 경우 메시지 표시
                Toast.makeText(Main_Activity.this, "갤러리 읽기 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //카메라 앱
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_CAPTURE_REQUEST_CODE);
    }

    // 갤러리 열기
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_OPENIMAGE_REQUEST_CODE);
    }

    //동작 result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAMERA_CAPTURE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    getimage = (Bitmap) data.getParcelableExtra("data");
                    imageView.setImageBitmap(getimage);
                    isimage = true;
                    sendbutton.setEnabled(true);

                    //갤러리 저장
                    saveImageToGallery(getimage);

                }
                break;
            case GALLERY_OPENIMAGE_REQUEST_CODE:
                // 갤러리 앱 결과 처리
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    // 선택한 이미지의 URI 가져오기
                    Uri uri = data.getData();
                    try {
                        // URI에서 비트맵으로 이미지 가져오기
                        getimage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        imageView.setImageBitmap(getimage);
                        isimage = true;
                        sendbutton.setEnabled(true);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    // 카메라로 찍은 이미지를 갤러리에 저장하는 메서드
    private void saveImageToGallery(Bitmap bitmap) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Image from Camera");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image taken from Camera App");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraApp");

        // 이미지를 갤러리에 추가
        Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try {
            // 이미지 데이터를 OutputStream으로 가져와서 저장
            if (imageUri != null) {
                getContentResolver().openOutputStream(imageUri).write(bitmapToByteArray(bitmap));
                Toast.makeText(Main_Activity.this, "이미지가 갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(Main_Activity.this, "이미지 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // Bitmap을 byte 배열로 변환하는 메서드
    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }


    public void uploadImage(Bitmap bitmap) {
        // 이미지 파일을 RequestBody로 변환
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), bitmapToByteArray(bitmap));

        // 파일 이름을 설정
//        String fileName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
        String uploadfileName = "image.jpg";
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", uploadfileName, fileBody);

        // 이미지 업로드 API 호출
        Call<ServiceAPIResponse> call = service.uploadImage(userid, filePart);
        call.enqueue(new Callback<ServiceAPIResponse>() {
            @Override
            public void onResponse(Call<ServiceAPIResponse> call, Response<ServiceAPIResponse> response) {
                ServiceAPIResponse result = response.body();
                boolean success = result.getSuccess();
                if (success) {
                    String filename = result.getFileName();
//                  String filepath = result.getDestination();


                    Intent detailintent = new Intent(getApplicationContext(), Search_Item_Activity.class);
//                    Intent detailintent = new Intent(getApplicationContext(), Detail_Item_Activity.class);
                    detailintent.putExtra("UserID", userid);
                    detailintent.putExtra("FileName", filename);
                    startActivity(detailintent);

//                    Intent searchintent = new Intent(getApplicationContext(), Search_Activity.class);
//                    searchintent.putExtra("UserID", userid);
//                    searchintent.putExtra("FileName", filename);
//                    startActivity(searchintent);

//                    Log.d("MainActivity_uploadImage", "이미지 전송 성공 path = " +filepath+"/"+filename);
                    Log.d("MainActivity_uploadImage", "이미지 전송 성공 path = " + filename);
                    Toast.makeText(Main_Activity.this, "이미지 전송 성공" + filename, Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("MainActivity_uploadImage: ", "이미지 전송 실패");
                    Toast.makeText(Main_Activity.this, "이미지 전송 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServiceAPIResponse> call, Throwable t) {
                // 네트워크 오류 처리
                Log.e("MainActivity_uploadImage", "에러 = " + t.getMessage());
            }
        });
    }
}