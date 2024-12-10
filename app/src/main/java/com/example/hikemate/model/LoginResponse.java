//package com.example.hikemate.model;
//
//public class LoginResponse {
//    private String token;
//
//    public String getToken() {
//        return token;
//    }
//
//    public void setToken(String token) {
//        this.token = token;
//    }
//}

package com.example.hikemate.model;

public class LoginResponse {
    private boolean success;
    private int status;
    private String message;
    private Data data;

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        private String access_token;

        public String getAccessToken() {
            return access_token;
        }
    }
}
