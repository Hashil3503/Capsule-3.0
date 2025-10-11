package com.example.myapplication;

import java.util.List;

public class ChatResponse {

    private String id;
    private String object;
    private long created;
    private List<Choice> choices;

    public List<Choice> getChoices() {
        return choices;
    }

    public static class Choice {
        private int index;
        private Message message;
        private String finish_reason;

        // 일부 모델(gpt-4o, gpt-5-mini)은 message가 아닌 delta로 내려올 수 있음
        private Message delta;

        public Message getMessage() {
            if (message != null) return message;
            return delta;
        }
    }

    public static class Message {
        private String role;
        private String content;

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }
}
