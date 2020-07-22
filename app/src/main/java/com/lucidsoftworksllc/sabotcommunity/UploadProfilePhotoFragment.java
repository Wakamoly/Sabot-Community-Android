package com.lucidsoftworksllc.sabotcommunity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class UploadProfilePhotoFragment extends Fragment {

    public static final String UPLOAD_URL = Constants.ROOT_URL+"uploadProfilePhoto.php";
    private Button buttonUpload;
    private ImageView imageView;
    private LinearLayout uploadProgress;
    JSONObject jsonObject;
    RequestQueue rQueue;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View uploadCoverView = inflater.inflate(R.layout.fragment_upload_profile_photo, container, false);
        requestMultiplePermissions();
        Button buttonChoose = uploadCoverView.findViewById(R.id.btnChoose);
        uploadProgress = uploadCoverView.findViewById(R.id.uploadProgress);
        buttonUpload = uploadCoverView.findViewById(R.id.btnUpload);
        imageView = uploadCoverView.findViewById(R.id.imageView);
        buttonChoose.setOnClickListener(v -> openCropper());
        openCropper();
        return uploadCoverView;
    }

    private void openCropper(){
        requestMultiplePermissions();
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setAspectRatio(1, 1) //You can skip this for free form aspect ratio)
                .start(requireContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    final Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), resultUri);
                    imageView.setImageBitmap(bitmap);
                    buttonUpload.setVisibility(View.VISIBLE);
                    buttonUpload.setOnClickListener(v -> {
                        uploadImage(bitmap);
                        uploadProgress.setVisibility(View.VISIBLE);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getActivity(), "Failed! Error: "+error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImage(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        try {
            jsonObject = new JSONObject();
            String imgname = String.valueOf(Calendar.getInstance().getTimeInMillis());
            String userID = SharedPrefManager.getInstance(getActivity()).getUserID();
            String username = SharedPrefManager.getInstance(getActivity()).getUsername();
            jsonObject.put("name", imgname);
            jsonObject.put("userid", userID);
            jsonObject.put("username", username);
            jsonObject.put("image", encodedImage);
        } catch (JSONException ignored) {
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, UPLOAD_URL, jsonObject,
                jsonObject -> {
                    rQueue.getCache().clear();
                    Fragment asf = new FragmentProfile();
                    FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, asf);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    Toast.makeText(requireActivity(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                }, volleyError -> Log.e("UploadCoverFragment", volleyError.toString()));
        rQueue = Volley.newRequestQueue(requireActivity());
        rQueue.add(jsonObjectRequest);
    }

    private void requestMultiplePermissions(){
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Toast.makeText(requireActivity(), "No permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(error -> Toast.makeText(requireActivity(), "Some Error! ", Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }

}
