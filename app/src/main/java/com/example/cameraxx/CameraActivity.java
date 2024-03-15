package com.example.cameraxx;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "CameraActivity";
    private static final int REQUEST_CODE_PERMISSION = 10;
    private static final String[] REQUEST_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO
    };

    PreviewView pv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        pv = findViewById(R.id.pv);
        if(allPermissionsGranted()){
            startCamera();
        }else{
            ActivityCompat.requestPermissions(this, REQUEST_PERMISSIONS,REQUEST_CODE_PERMISSION);
        }
    }

    private void startCamera(){
        ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderListenableFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderListenableFuture.get();
                    Preview preview = new Preview.Builder().build();
                    preview.setSurfaceProvider(pv.getSurfaceProvider());
                    cameraProvider.unbind();
//                    cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA,preView,);
                    CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                    cameraProvider.bindToLifecycle(CameraActivity.this, cameraSelector, preview);

                } catch (ExecutionException e) {
                    Log.e(TAG, "相机执行时失败");
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    Log.e(TAG, "相机失败");
                    throw new RuntimeException(e);
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }


    private boolean allPermissionsGranted() {
        for (String permission : REQUEST_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_PERMISSION){
            if(allPermissionsGranted()){
                startCamera();
            }else{
                Toast.makeText(this, "用户有未授予的权限",Toast.LENGTH_LONG);
                finish();
            }
        }
    }
}
