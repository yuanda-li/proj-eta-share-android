package com.project.first.myfirstapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class PostMessage extends AppCompatActivity {
    EditText ET_MESSAGE;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ET_MESSAGE = (EditText)findViewById(R.id.post_message);
    }

    public void postMessage(View view) {
        message = ET_MESSAGE.getText().toString();
        String method = "post";
        BackendTask backendTask = new BackendTask(this);
        backendTask.execute(method, message);
        finish();
    }

}
