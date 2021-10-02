package com.redant.codeland.bmob;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.redant.codeland.R;
import com.redant.codeland.entity.Bmob.User;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterAndLoginActivity extends AppCompatActivity {

    EditText editTextAccount;
    EditText editTextPassword;
    Button buttonLogin;
    Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_and_login);

        editTextAccount=findViewById(R.id.edit_text_account);
        editTextPassword=findViewById(R.id.edit_text_password);
        buttonLogin=findViewById(R.id.button_login);
        buttonRegister=findViewById(R.id.button_register);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(editTextAccount.getText().toString().trim())||TextUtils.isEmpty(editTextPassword.getText().toString().trim())){
                    Toast.makeText(RegisterAndLoginActivity.this,"账号和密码不能为空哦", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user=new User();
                user.setUsername(editTextAccount.getText().toString().trim());
                user.setPassword(editTextPassword.getText().toString().trim());
                user.login(new SaveListener<Object>() {//登陆校验
                    @Override
                    public void done(Object o, BmobException e) {
                        if(e==null){
                            Toast.makeText(RegisterAndLoginActivity.this,"登录成功", Toast.LENGTH_SHORT).show();

                            Intent intent=new Intent(RegisterAndLoginActivity.this,SectionActivity.class);
                            startActivity(intent);

                        }else {
                            Toast.makeText(RegisterAndLoginActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(editTextAccount.getText().toString().trim())||TextUtils.isEmpty(editTextPassword.getText().toString().trim())){
                    Toast.makeText(RegisterAndLoginActivity.this,"账号和密码不能为空哦", Toast.LENGTH_SHORT).show();
                    return;
                }
                //注册，添加到云端数据库
                User user=new User();
                user.setUsername(editTextAccount.getText().toString().trim());
                user.setPassword(editTextPassword.getText().toString().trim());
                user.signUp(new SaveListener<Object>() {
                    @Override
                    public void done(Object o, BmobException e) {
                        if(e==null){
                            Toast.makeText(RegisterAndLoginActivity.this,"注册成功", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(RegisterAndLoginActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}
