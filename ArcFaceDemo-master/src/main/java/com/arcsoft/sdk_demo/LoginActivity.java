package com.arcsoft.sdk_demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.arcsoft.sdk_demo.model.BusModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername;
    private EditText etPassword;
    private Button btGo;
    private CardView cv;
    private FloatingActionButton fab;
    private Application myApp;
    private String filesPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myApp=(Application)this.getApplication();
        filesPath=myApp.mPath;
        initView();
        setListener();

    }

    private void initView() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btGo = findViewById(R.id.bt_go);
        cv = findViewById(R.id.cv);
    }

    private void setListener() {
        btGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BusModel busModel=new BusModel(LoginActivity.this);
                busModel.Login(etUsername.getText().toString());

                //buslogin(etUsername.getText().toString());
            }
        });
        /*
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, fab, fab.getTransitionName());
                startActivity(new Intent(LoginActivity.this, LoginActivity.class), options.toBundle());
            }
        });
        */
    }

    @Override
    protected void onRestart() {
        super.onRestart();
       // fab.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //fab.setVisibility(View.VISIBLE);
    }


}
