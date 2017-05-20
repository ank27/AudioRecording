package com.audioapplication.Models;

public class SignInRequest {

        public String login_type;
        public String user_id;
        public String auth;

        public SignInRequest(String login_type, String user_id, String auth) {
            this.login_type = login_type;
            this.user_id = user_id;
            this.auth = auth;
        }
}
