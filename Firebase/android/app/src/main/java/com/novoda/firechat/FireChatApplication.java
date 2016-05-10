package com.novoda.firechat;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.novoda.firechat.chat.service.ChatService;
import com.novoda.firechat.chat.service.FirebaseChatService;
import com.novoda.firechat.login.service.FirebaseLoginService;
import com.novoda.firechat.login.service.LoginService;
import com.novoda.notils.logger.simple.Log;

public class FireChatApplication extends Application {

    private LoginService loginService;
    private ChatService chatService;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.setShowLogs(BuildConfig.DEBUG);
        FirebaseApp firebaseApp = FirebaseApp.initializeApp(this, FirebaseOptions.fromResource(this), "FireChat");
        chatService = new FirebaseChatService(firebaseApp);
        loginService = new FirebaseLoginService(firebaseApp);
    }

    public ChatService getChatService() {
        return chatService;
    }

    public LoginService getLoginService() {
        return loginService;
    }
}
