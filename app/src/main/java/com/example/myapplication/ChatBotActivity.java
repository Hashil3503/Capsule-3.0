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

        new Thread(() -> {
            String apiKey = getString(R.string.med_search_api_key);
            List<String> potentialMeds = extractPotentialDrugNames(userInput);
            List<Medication> foundMeds = new ArrayList<>();

            DB db = DB.getInstance(ChatBotActivity.this);
            // 1. 로컬 DB에 저장되어 있는 모든 약품 리스트 조회
            List<Medication> localMeds = db.medicationDao().getAllMedications();

            for (String queryName : potentialMeds) {
                boolean foundLocally = false;
                String cleanedQuery = queryName.toLowerCase().replaceAll("\\s+", "");
                if (cleanedQuery.length() < 2) continue;

                // 로컬 DB 검색 (부분 일치 검사)
                for (Medication med : localMeds) {
                    String dbName = med.getItemName().toLowerCase().replaceAll("\\s+", "");
                    if (dbName.contains(cleanedQuery) || cleanedQuery.contains(dbName)) {
                        foundMeds.add(med);
                        foundLocally = true;
                        Log.d("ChatBotActivity", "로컬 DB에서 약품 정보 발견: " + med.getItemName());
                        break;
                    }
                }

                // 로컬 DB에 없을 경우 실시간 공공데이터 API("e약은요") 호출
                if (!foundLocally) {
                    Log.d("ChatBotActivity", "로컬 DB에 없어 API 검색 시도: " + queryName);
                    Medication apiMed = CommonMethod.getDrugInfo(apiKey, queryName);
                    if (apiMed != null && apiMed.getEfcyQesitm() != null && !"효능 정보 없음".equals(apiMed.getEfcyQesitm())) {
                        foundMeds.add(apiMed);
                        // 다음 검색 성능 향상을 위해 로컬 DB에 캐싱 저장
                        try {
                            db.medicationDao().insert(apiMed);
                            Log.d("ChatBotActivity", "API 검색 결과를 로컬 DB에 캐싱함: " + apiMed.getItemName());
                        } catch (Exception e) {
                            Log.e("ChatBotActivity", "DB 캐싱 실패", e);
                        }
                    }
                }
            }

            // 검색된 약품 팩트 기반의 시스템 프롬프트 구성
            StringBuilder systemPromptBuilder = new StringBuilder();
            systemPromptBuilder.append("당신은 의약품 정보와 일반적인 건강 상식에 대해 설명하는 친절한 AI 상담사입니다.\n");
            systemPromptBuilder.append("답변의 길이는 6줄 내외로 간략하게 요약하여 설명하세요. 답변 내용의 출처는 표기하지 마세요.\n");
            systemPromptBuilder.append("만약 사용자가 문의한 약물 정보가 아래 [참고 의약품 정보]에 기재되어 있다면, 반드시 제공된 정보의 팩트(Fact)에만 기반하여 안전하게 답변하세요. 효능, 사용법, 주의사항, 부작용을 절대로 임의로 지어내지 마십시오. 만약 해당 약물의 정보가 목록에 없거나 모르는 내용이라면, 사실과 다를 수 있는 조언을 피하고 반드시 전문 의사나 약사와 상담할 것을 안내하십시오.\n\n");

            if (!foundMeds.isEmpty()) {
                systemPromptBuilder.append("[참고 의약품 정보]\n");
                for (Medication med : foundMeds) {
                    systemPromptBuilder.append("- 제품명: ").append(med.getItemName()).append("\n");
                    systemPromptBuilder.append("  * 제조업체: ").append(med.getEntpName()).append("\n");
                    systemPromptBuilder.append("  * 효능: ").append(med.getEfcyQesitm()).append("\n");
                    systemPromptBuilder.append("  * 사용법: ").append(med.getUseMethodQesitm()).append("\n");
                    if (med.getAtpnWarnQesitm() != null && !med.getAtpnWarnQesitm().isEmpty() && !"경고 정보 없음".equals(med.getAtpnWarnQesitm())) {
                        systemPromptBuilder.append("  * 경고: ").append(med.getAtpnWarnQesitm()).append("\n");
                    }
                    systemPromptBuilder.append("  * 주의사항: ").append(med.getAtpnQesitm()).append("\n");
                    systemPromptBuilder.append("  * 상호작용(병용금기): ").append(med.getIntrcQesitm()).append("\n");
                    systemPromptBuilder.append("  * 부작용: ").append(med.getSeQesitm()).append("\n");
                    systemPromptBuilder.append("  * 보관방법: ").append(med.getDepositMethodQesitm()).append("\n\n");
                }
            }

            String finalSystemPrompt = systemPromptBuilder.toString();

            // Retrofit 네트워크 호출은 UI 스레드에서 시작
            runOnUiThread(() -> {
                List<ChatRequest.Message> messages = new ArrayList<>();
                messages.add(new ChatRequest.Message("system", "You are a helpful assistant."));
                messages.add(new ChatRequest.Message("user", userInput));

                // 커스텀 프롬프트를 사용하는 overloaded 생성자 호출
                ChatRequest request = new ChatRequest("gpt-4o-mini", messages, finalSystemPrompt);

                Call<ChatResponse> call = chatService.getChatCompletion(request);
                call.enqueue(new Callback<ChatResponse>() {
                    @Override
                    public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                        progressBar.setVisibility(android.view.View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            ChatResponse chatResponse = response.body();

                            if (chatResponse.getChoices() != null &&
                                    !chatResponse.getChoices().isEmpty() &&
                                    chatResponse.getChoices().get(0).getMessage() != null &&
                                    chatResponse.getChoices().get(0).getMessage().getContent() != null) {

                                String reply = chatResponse.getChoices().get(0).getMessage().getContent().trim();
                                addMessage(reply.isEmpty() ? "⚠️ 응답이 비어 있습니다." : reply, false);

                            } else {
                                try {
                                    String rawJson = new com.google.gson.Gson().toJson(chatResponse);
                                    Log.e("ChatBotActivity", "⚠️ 응답 구조 오류: " + rawJson);
                                } catch (Exception e) {
                                    Log.e("ChatBotActivity", "⚠️ JSON 직렬화 오류", e);
                                }
                                addMessage("⚠️ 응답이 비어 있거나 구조가 예상과 다릅니다.", false);
                            }
                        } else {
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
            });
        }).start();
    }

    private List<String> extractPotentialDrugNames(String text) {
        List<String> names = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) return names;

        // 공백 및 문장 부호 기준으로 분할
        String[] words = text.split("[\\s,?.!~()]+");
        for (String word : words) {
            word = word.trim();
            if (word.length() < 2) continue;

            // 흔한 한국어 조사 및 약품 접미사 제거
            String cleaned = word;
            if (cleaned.endsWith("은") || cleaned.endsWith("는") || cleaned.endsWith("이") || cleaned.endsWith("가") ||
                cleaned.endsWith("을") || cleaned.endsWith("를") || cleaned.endsWith("의") || cleaned.endsWith("와") ||
                cleaned.endsWith("과") || cleaned.endsWith("에") || cleaned.endsWith("도") || cleaned.endsWith("만") ||
                cleaned.endsWith("정") || cleaned.endsWith("약")) {
                cleaned = cleaned.substring(0, cleaned.length() - 1);
            }
            if (cleaned.length() >= 2) {
                names.add(cleaned);
            }
        }
        return names;
    }
}
