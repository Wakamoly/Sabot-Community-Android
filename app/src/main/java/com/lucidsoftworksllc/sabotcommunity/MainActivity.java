package com.lucidsoftworksllc.sabotcommunity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
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
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextUsername, editTextEmail, editTextPassword, editTextPassword2, howdidyoufindus;
    private ProgressDialog progressDialog;
    private TextView textViewLogin, buttonRegister, termsAndConditionsLinkTV, privacyLinkTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, FragmentContainer.class));
            return;
        }
        setContentView(R.layout.activity_main);
        editTextEmail = findViewById(R.id.emailEditText);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.passwordEditText);
        editTextPassword2 = findViewById(R.id.password2EditText);
        howdidyoufindus = findViewById(R.id.howdidyoufindus);
        textViewLogin = findViewById(R.id.goToLoginButton);
        termsAndConditionsLinkTV = findViewById(R.id.termsAndConditionsLinkTV);
        privacyLinkTV = findViewById(R.id.privacyLinkTV);
        buttonRegister = findViewById(R.id.buttonRegister);
        progressDialog = new ProgressDialog(this);
        buttonRegister.setOnClickListener(this);
        textViewLogin.setOnClickListener(this);
        termsAndConditionsLinkTV.setOnClickListener(this);
        privacyLinkTV.setOnClickListener(this);
    }

    private void registerUser() {
        final String email = editTextEmail.getText().toString().trim();
        final String username = editTextUsername.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String password2 = editTextPassword2.getText().toString().trim();
        final String howdidyoufindustext = howdidyoufindus.getText().toString().trim();
        if (username.length() < 4 || username.length() > 20) {
            Toast.makeText(getApplicationContext(), "Username must be 4 to 20 characters long!", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        } else {
            if (password.equals(password2) && !(password.length() < 3)){
                if (isValidEmail(email)) {
                        progressDialog.setMessage("Registering user...");
                        progressDialog.show();
                        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                Constants.URL_REGISTER,
                                response -> {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        if (jsonObject.getString("error").equals("false")){
                                            sendToLogin(email, password);
                                        }else{
                                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                },
                                error -> {
                                    progressDialog.hide();
                                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                params.put("username", username);
                                params.put("email", email);
                                params.put("password", password);
                                params.put("howdidyoufindus", howdidyoufindustext);
                                return params;
                            }
                        };
                        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
                }else{
                    Toast.makeText(getApplicationContext(), "E-mail not valid!", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }else{
                Toast.makeText(getApplicationContext(), "Passwords do not match or not long enough!", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view == privacyLinkTV) startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://app.termly.io/document/privacy-policy/96bf5c01-d39b-496c-a30e-57353b49877c")));
        if(view == buttonRegister) registerUser();
        if(view == textViewLogin) startActivity(new Intent(this, LoginActivity.class));
        if(view == termsAndConditionsLinkTV) startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://sabotcommunity.com/termsandconditions.php")));
    }

    public void sendToLogin(String email, String password){
        startActivity(new Intent(this, LoginActivity.class).putExtra("email", email).putExtra("password", password).putExtra("registered", "yes"));
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(target).matches();
    }
}
