package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;

public class ContactUsFragment extends Fragment {

    private static final String SUBMIT_CONTACT_REQ = Constants.ROOT_URL+"contact_us_submit.php";

    private String DeviceId, version, model, product;
    private Context mContext;
    private Button btnSubmit;
    private Spinner contactUsSpinner;
    private EditText etSubject, etDescription;
    private ProgressBar contactProgressBar;
    private LinearLayout contactUsDetails;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contactUsRootView = inflater.inflate(R.layout.fragment_contact_us, null);

        mContext = getActivity();
        btnSubmit = contactUsRootView.findViewById(R.id.btnSubmit);
        contactUsSpinner = contactUsRootView.findViewById(R.id.contactUsSpinner);
        etSubject = contactUsRootView.findViewById(R.id.etSubject);
        etDescription = contactUsRootView.findViewById(R.id.etDescription);
        contactProgressBar = contactUsRootView.findViewById(R.id.contactProgressBar);
        contactUsDetails = contactUsRootView.findViewById(R.id.contactUsDetails);

        if (getArguments()!=null){
            if (getArguments().getString("newpublics")!=null) {
                contactUsSpinner.setSelection(2);
                etSubject.setText(getArguments().getString("newpublics"));
                etSubject.requestFocus();
                if (etSubject.hasFocus()) {
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            }
        }

        btnSubmit.setOnClickListener(v -> {
            version = android.os.Build.VERSION.RELEASE; // API Level
            DeviceId = android.os.Build.DEVICE;         // Device
            model = android.os.Build.MODEL;             // Model
            product = android.os.Build.PRODUCT;         // Product
            String spinnerText = String.valueOf(contactUsSpinner.getSelectedItem());
            String body = etDescription.getText().toString();
            String added_by = SharedPrefManager.getInstance(mContext).getUsername();
            String subject = etSubject.getText().toString();
            String added_by_id = SharedPrefManager.getInstance(mContext).getUserID();
            if(!body.equals("")||!subject.equals("")){
                SubmitContactRequest(DeviceId,version,model,product,spinnerText,body,added_by,subject,added_by_id);
            }else{
                Toast.makeText(mContext,"Please fill in each field!", Toast.LENGTH_SHORT).show();
            }
        });

        return contactUsRootView;
    }

    private void SubmitContactRequest(final String deviceId, final String version, final String model, final String product, final String spinnerText, final String body, final String added_by, final String subject, final String added_by_id){
        contactUsDetails.setVisibility(GONE);
        contactProgressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, SUBMIT_CONTACT_REQ, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString("error").equals("false")){
                    Toast.makeText(mContext,"Thank you for your concern! We will look into your request very soon.", Toast.LENGTH_LONG).show();
                    requireActivity().getSupportFragmentManager().popBackStackImmediate();
                }else{
                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    contactProgressBar.setVisibility(GONE);
                    contactUsDetails.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            contactProgressBar.setVisibility(GONE);
            Toast.makeText(mContext,"Error on Response, please try again later...",Toast.LENGTH_LONG).show();
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("deviceId",deviceId);
                parms.put("version",version);
                parms.put("model",model);
                parms.put("product",product);
                parms.put("spinnerText",spinnerText);
                parms.put("body",body);
                parms.put("added_by",added_by);
                parms.put("subject",subject);
                parms.put("added_by_id",added_by_id);
                parms.put("email",SharedPrefManager.getInstance(mContext).getEmail());
                return parms;
            }
        };
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

}
