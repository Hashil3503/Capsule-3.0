package com.example.myapplication;

import java.util.List;

public class ChatRequest {
    private String model;
    private List<Message> messages;

    public ChatRequest(String model, List<Message> messages) {
        this.model = model;
        this.messages = messages;
        messages.add(new ChatRequest.Message(
                "system",
                "당신은 의약품 정보와 일반적인 건강 상식에 대해 설명하는 친절한 AI 상담사입니다." +
                "답변의 길이는 6줄 내외로 간략하게 요약하여 설명하세요. " +
                "답변 내용의 출처는 표기하지 마세요."
                ));
    }

    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }
}
