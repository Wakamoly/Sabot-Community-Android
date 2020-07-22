package com.lucidsoftworksllc.sabotcommunity;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.CountDownTimer;

import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class LoginActivity extends FragmentActivity implements View.OnClickListener {

    private EditText editTextEmail, editTextPassword;
    private ProgressDialog progressDialog;
    private ImageView bookIconImageView;
    private TextView bookITextView, buttonLogin, registerButton, forgotPassButton;
    private ProgressBar loadingProgressBar;
    private RelativeLayout rootView;
    private LinearLayout threeminutewalkthrough,afterAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, FragmentContainer.class));
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        initViews();

        if(getIntent().hasExtra("email")&&getIntent().hasExtra("password")&&getIntent().hasExtra("registered")) {
            String email, password, registered;
            email = getIntent().getStringExtra("email");
            password = getIntent().getStringExtra("password");
            registered = getIntent().getStringExtra("registered");
            if (!email.isEmpty() && !password.isEmpty() && registered.equals("yes")) {
                userLoginRegistered(email, password);
            }
        }
        new CountDownTimer(10000, 5000) {
            @Override
            public void onTick(long millisUntilFinished) {
                bookITextView.setVisibility(GONE);
                loadingProgressBar.setVisibility(GONE);
                rootView.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.colorPrimary));
               // logoImageView.setImageResource(R.drawable.logo);
                startAnimation();
            }
            @Override public void onFinish() { }
        }.start();
    }

    private void initViews() {
        bookIconImageView = findViewById(R.id.logoImageView);
        bookITextView = findViewById(R.id.loadingText);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        rootView = findViewById(R.id.activity_login_view);
        afterAnimationView = findViewById(R.id.afterAnimationView);

        forgotPassButton = findViewById(R.id.forgotPassButton);
        editTextEmail = findViewById(R.id.emailEditText);
        editTextPassword = findViewById(R.id.passwordEditText);
        buttonLogin = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        threeminutewalkthrough = findViewById(R.id.threeminutewalkthrough);
        threeminutewalkthrough.setVisibility(GONE);
        //TODO Causes OOM crash on some devices
        //threeminutewalkthrough.setOnClickListener(this);

        buttonLogin.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        forgotPassButton.setOnClickListener(this);
    }

    private void startAnimation() {
        ViewPropertyAnimator viewPropertyAnimator = bookIconImageView.animate();
        viewPropertyAnimator.x(50f);
        viewPropertyAnimator.y(100f);
        viewPropertyAnimator.setDuration(1500);
        viewPropertyAnimator.setListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationEnd(Animator animation) { afterAnimationView.setVisibility(VISIBLE); }
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });
    }

    private void userLoginRegistered(final String email, final String password){
        progressDialog.show();
        if(isValidEmail(email)) {
            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST,
                    Constants.URL_LOGIN,
                    response -> {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                SharedPrefManager.getInstance(getApplicationContext())
                                        .userLogin(
                                                obj.getString("id"),
                                                obj.getString("username"),
                                                obj.getString("nickname"),
                                                obj.getString("email"),
                                                obj.getString("profilepic"),
                                                obj.getString("usersfollowed"),
                                                obj.getString("gamesfollowed"),
                                                obj.getString("usersfriends"),
                                                obj.getString("blockedarray")
                                        );

                                Intent toDash = new Intent(LoginActivity.this, FragmentContainer.class).putExtra("registered", "yes");
                                toDash.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                finish();
                                startActivity(toDash);
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("password", password);
                    return params;
                }
            };
            RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(getApplicationContext(), "E-mail not valid!", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }

    private void userLogin(final String email, final String password){
        progressDialog.show();
        if(isValidEmail(email)) {
            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST,
                    Constants.URL_LOGIN,
                    response -> {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                SharedPrefManager.getInstance(getApplicationContext())
                                        .userLogin(
                                                obj.getString("id"),
                                                obj.getString("username"),
                                                obj.getString("nickname"),
                                                obj.getString("email"),
                                                obj.getString("profilepic"),
                                                obj.getString("usersfollowed"),
                                                obj.getString("gamesfollowed"),
                                                obj.getString("usersfriends"),
                                                obj.getString("blockedarray")
                                        );
                                Intent toDash = new Intent(LoginActivity.this, FragmentContainer.class);
                                toDash.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                finish();
                                startActivity(toDash);
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("password", password);
                    return params;
                }
            };
            RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(getApplicationContext(), "E-mail not valid!", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
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

    @Override
    public void onClick(View view) {
        if(view == buttonLogin){
            final String email = editTextEmail.getText().toString().trim();
            final String pass = editTextPassword.getText().toString().trim();
            if((email.length()>1)||(pass.length()>1)) {
                userLogin(email, pass);
            }else{
                Toast.makeText(getApplicationContext(), "E-mail or pass not valid!", Toast.LENGTH_LONG).show();
            }
        }
        /*if (view == threeminutewalkthrough) {
            startActivity(new Intent(this, Walkthrough.class));
        }*/
        if(view == registerButton) {
            startActivity(new Intent(this, MainActivity.class));
        }
        if(view == forgotPassButton) {
            startActivity(new Intent(this, ForgotPassActivity.class));
        }
    }

}
