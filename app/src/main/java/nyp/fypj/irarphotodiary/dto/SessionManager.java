package nyp.fypj.irarphotodiary.dto;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

import nyp.fypj.irarphotodiary.activity.LoginActivity;

public class SessionManager {
	SharedPreferences pref;
	Editor editor;	
	Context context;

    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "Darts";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String REMEMBERME = "IsRememberMe";
    public static final String SERVERNAME = "serverName";
    public static final String SESSION = "sessionID";
    public static final String ID = "none";

    @SuppressLint("CommitPrefEdits")
	public SessionManager(Context context){
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    
    public void createLoginSession(String username, String password, String id, String serverName, Boolean rememberMe, String sessionID){
    	editor.putBoolean(REMEMBERME, rememberMe);
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(USERNAME, username);
        editor.putString(PASSWORD, password);
        editor.putString(ID, id);
        editor.putString(SERVERNAME, serverName);
        editor.putString(SESSION, sessionID);      
        editor.commit();
    }   

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
    
    
    public boolean isRememberMe(){
        return pref.getBoolean(REMEMBERME, false);
    }
    
    public String getServer(){
    	return pref.getString(SERVERNAME, null);
    }
    
    public String getSessionID(){
    	return pref.getString(SESSION, null);
    }
    
    public void checkLogin(){
        if(!this.isLoggedIn()){
            Intent i = new Intent(context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }        
    }
	 
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(USERNAME, pref.getString(USERNAME, null));
        user.put(PASSWORD, pref.getString(PASSWORD, null));
        user.put(ID, pref.getString(ID, null));
        return user;
    }
	
    public void logoutUser(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
