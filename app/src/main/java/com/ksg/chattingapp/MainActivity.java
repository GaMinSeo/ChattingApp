package com.ksg.chattingapp;


import android.annotation.SuppressLint;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.ksg.chattingapp.adapter.ChatAdapter;

import com.ksg.chattingapp.model.ChatData;


import java.util.ArrayList;

import java.util.List;



public class MainActivity extends AppCompatActivity {

    ImageView addImage;
    RecyclerView recyclerView;
    ChatAdapter adapter;
    List<ChatData> chatList;
    String nick = "대민서"; // 닉네임 설정

    EditText editChat;
    Button btnSend;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addImage = findViewById(R.id.addImage);
        btnSend = findViewById(R.id.btnSend);
        editChat = findViewById(R.id.editChat);


        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = editChat.getText().toString();
                if (msg != null && !msg.isEmpty()) {
                    sendMessage(msg);
                }
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatList = new ArrayList<>();
        adapter = new ChatAdapter(chatList, nick); // 어댑터 초기화
        recyclerView.setAdapter(adapter);

        // Firebase 초기화 및 데이터베이스 참조 설정
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("chats");

        // 채팅 데이터 실시간 업데이트를 위한 리스너 설정
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatData chat = dataSnapshot.getValue(ChatData.class);
                if (chat != null) {
                    chatList.add(chat);
                    adapter.notifyDataSetChanged(); // 데이터셋 변경 알림
                    scrollToBottom();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void sendMessage(String msg) {
        ChatData chat = new ChatData();
        chat.setSenderNickname(nick);
        chat.setMessageContent(msg);
        chat.setTimestamp(System.currentTimeMillis());
        String messageId = myRef.push().getKey(); // 메시지 ID 생성
        chat.setMessageId(messageId);
        myRef.child(messageId).setValue(chat); // 데이터베이스에 메시지 저장
        editChat.setText(""); // 메시지 입력란 초기화
    }

    private void scrollToBottom() {
        recyclerView.scrollToPosition(chatList.size() - 1); // 맨 아래로 스크롤
    }
}