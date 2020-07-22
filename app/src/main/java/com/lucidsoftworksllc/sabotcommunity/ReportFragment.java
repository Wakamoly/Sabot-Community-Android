package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class ReportFragment extends Fragment {

    private static final String SUBMIT_REPORT_REQ = Constants.ROOT_URL+"report_submit.php";

    private String type, id_of_reported, reason, reported_by, context_reported, reported_by_id,finalReason;
    private Context mContext;
    private Button btnSubmit;
    private Spinner reportSpinner;
    private EditText etSubject, etDescription, etOther;
    private ProgressBar reportProgressBar;
    private LinearLayout reportDetails;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contactUsRootView = inflater.inflate(R.layout.fragment_report, null);

        mContext = getActivity();
        btnSubmit = contactUsRootView.findViewById(R.id.btnSubmit);
        reportSpinner = contactUsRootView.findViewById(R.id.reportSpinner);
        etSubject = contactUsRootView.findViewById(R.id.etSubject);
        etDescription = contactUsRootView.findViewById(R.id.etDescription);
        reportProgressBar = contactUsRootView.findViewById(R.id.reportProgressBar);
        reportDetails = contactUsRootView.findViewById(R.id.reportDetails);
        etOther = contactUsRootView.findViewById(R.id.etOther);

        assert getArguments() != null;
        context_reported = getArguments().getString("context");
        type = getArguments().getString("type");
        id_of_reported = getArguments().getString("id");
        reportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reason = String.valueOf(reportSpinner.getSelectedItem());
                if (reason.equals("Other"))
                    etOther.setVisibility(View.VISIBLE);
                btnSubmit.setOnClickListener(v -> {
                    String body = etDescription.getText().toString();
                    reported_by = SharedPrefManager.getInstance(mContext).getUsername();
                    reported_by_id = SharedPrefManager.getInstance(mContext).getUserID();
                    String subject = etSubject.getText().toString();
                    if (reason.equals("Other")){
                        finalReason = etOther.getText().toString();
                    }else{
                        finalReason = reason;
                    }
                    if(!body.equals("")&&!subject.equals("")&&!finalReason.equals("")){
                        SubmitReportRequest(type, id_of_reported, context_reported, finalReason, reported_by, body, subject, reported_by_id);
                    }else{
                        Toast.makeText(mContext,"Please fill in each field!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
        return contactUsRootView;
    }

    private void SubmitReportRequest(final String type, final String id_of_reported, final String context_reported, final String finalReason, final String reported_by, final String body, final String subject, final String reported_by_id){
        reportDetails.setVisibility(GONE);
        reportProgressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, SUBMIT_REPORT_REQ, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString("error").equals("false")){
                    Toast.makeText(mContext,"Thank you for your concern! We will look into your request very soon.", Toast.LENGTH_LONG).show();
                    getFragmentManager().popBackStackImmediate();
                }else{
                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    reportProgressBar.setVisibility(GONE);
                    reportDetails.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            reportProgressBar.setVisibility(GONE);
            Toast.makeText(mContext,"Error on Response, please try again later...",Toast.LENGTH_LONG).show();
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("type",type);
                parms.put("id_of_reported",id_of_reported);
                parms.put("context_reported",context_reported);
                parms.put("finalReason",finalReason);
                parms.put("reported_by",reported_by);
                parms.put("body",body);
                parms.put("subject",subject);
                parms.put("reported_by_id",reported_by_id);
                parms.put("email",SharedPrefManager.getInstance(mContext).getEmail());
                return parms;
            }
        };
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

}
