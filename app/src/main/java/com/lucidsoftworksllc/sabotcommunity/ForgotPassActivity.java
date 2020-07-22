package com.lucidsoftworksllc.sabotcommunity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.fragment.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.lucidsoftworksllc.sabotcommunity.Constants.ROOT_URL;

public class ForgotPassActivity extends FragmentActivity {

    public static final String URL_FORGOT_PASS = ROOT_URL+"password_recovery.php";

    private TextInputEditText emailEditText, usernameEditText;
    private ProgressDialog progressDialog;
    private TextView forgotButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_forgot_pass);
        progressDialog = new ProgressDialog(this);
        emailEditText = findViewById(R.id.emailForgotEditText);
        usernameEditText = findViewById(R.id.usernameForgotEditText);
        forgotButton = findViewById(R.id.forgotButton);
        forgotButton.setOnClickListener(v -> {
            final String email = Objects.requireNonNull(emailEditText.getText()).toString().trim();
            final String username = Objects.requireNonNull(usernameEditText.getText()).toString().trim();
            ForgotPassword(email,username);
        });
    }

    public void ForgotPassword(final String email, final String username){
        if (username.length() < 4 || username.length() > 20) {
            Toast.makeText(getApplicationContext(), "Username must be 4 to 20 characters long!", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        } else {
            if (isValidEmail(email)) {
                progressDialog.setMessage("Checking credentials...");
                progressDialog.show();
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        URL_FORGOT_PASS,
                        response -> {
                            progressDialog.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (!jsonObject.getBoolean("error")) {
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    finish();
                                    Toast.makeText(getApplicationContext(), "Success, check your emails!", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> {
                            progressDialog.hide();
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", username);
                        params.put("email", email);
                        return params;
                    }
                };
                RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
            } else {
                Toast.makeText(getApplicationContext(), "E-mail not valid!", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(target).matches();
    }

}
