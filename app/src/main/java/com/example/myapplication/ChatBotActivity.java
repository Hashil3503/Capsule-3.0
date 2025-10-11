package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.TimeUnit;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatBotActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList = new ArrayList<>();
    private EditText messageEditText;
    private Button sendButton;
    private ProgressBar progressBar;

    private ChatService chatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        progressBar = findViewById(R.id.progressBar);

        chatAdapter = new ChatAdapter(messageList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        String apiKey = getString(R.string.openai_api_key);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("Authorization", "Bearer " + apiKey)
                            .build();
                    return chain.proceed(request);
                })
                .connectTimeout(60, TimeUnit.SECONDS)  // 연결 시도 제한시간
                .readTimeout(300, TimeUnit.SECONDS)    // 서버 응답 대기시간
                .writeTimeout(120, TimeUnit.SECONDS)   // 요청 전송 제한시간
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openai.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        chatService = retrofit.create(ChatService.class);

        sendButton.setOnClickListener(v -> {
            String userMessage = messageEditText.getText().toString().trim();
            if (userMessage.isEmpty()) return;

            addMessage(userMessage, true);
            sendMessageToGPT(userMessage);
            messageEditText.setText("");
        });
    }

    private void addMessage(String text, boolean isUser) {
        messageList.add(new ChatMessage(text, isUser));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        chatRecyclerView.scrollToPosition(messageList.size() - 1);
    }

    private void sendMessageToGPT(String userInput) {
        progressBar.setVisibility(android.view.View.VISIBLE);

        List<ChatRequest.Message> messages = new ArrayList<>();
        messages.add(new ChatRequest.Message("system", "You are a helpful assistant."));
        messages.add(new ChatRequest.Message("user", userInput));

        ChatRequest request = new ChatRequest("gpt-4o-mini", messages);

        Call<ChatResponse> call = chatService.getChatCompletion(request);
        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                progressBar.setVisibility(android.view.View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    ChatResponse chatResponse = response.body();

                    // 응답 구조와 내용 안전하게 확인
                    if (chatResponse.getChoices() != null &&
                            !chatResponse.getChoices().isEmpty() &&
                            chatResponse.getChoices().get(0).getMessage() != null &&
                            chatResponse.getChoices().get(0).getMessage().getContent() != null) {

                        String reply = chatResponse.getChoices().get(0).getMessage().getContent().trim();
                        addMessage(reply.isEmpty() ? "⚠️ 응답이 비어 있습니다." : reply, false);

                    } else {
                        // 응답 구조가 비정상일 때 로그 출력
                        try {
                            String rawJson = new com.google.gson.Gson().toJson(chatResponse);
                            Log.e("ChatBotActivity", "⚠️ 응답 구조 오류: " + rawJson);
                        } catch (Exception e) {
                            Log.e("ChatBotActivity", "⚠️ JSON 직렬화 오류", e);
                        }
                        addMessage("⚠️ 응답이 비어 있거나 구조가 예상과 다릅니다.", false);
                    }
                } else {
                    // 400, 401 등 서버 오류 처리
                    try {
                        String errorBody = response.errorBody() != null
                                ? response.errorBody().string()
                                : "no error body";

                        Log.e("ChatBotActivity", "❌ 오류 코드: " + response.code());
                        Log.e("ChatBotActivity", "❌ 오류 내용: " + errorBody);

                        addMessage("🚫 요청 실패 (" + response.code() + "): " + errorBody, false);
                    } catch (IOException e) {
                        Log.e("ChatBotActivity", "❌ errorBody 읽기 중 IOException 발생", e);
                        addMessage("⚠️ 오류 본문 읽기 실패", false);
                    }
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                progressBar.setVisibility(android.view.View.GONE);
                Log.e("ChatBotActivity", "통신 실패: " + t);
                addMessage("🚫 통신 실패: " + t.getMessage(), false);
            }
        });
    }
}
