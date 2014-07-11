package com.anagorny.chaters.chaterspda;

import android.util.Log;
import android.widget.Toast;

import com.anagorny.chaters.messege.MSG;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sosnov on 04.02.14.
 */
public class ParseInputMsg {
    private ChatersAcitity main;
    private JSONObject msg;

    public ParseInputMsg(ChatersAcitity obj, JSONObject m) {
        this.main = obj;
        this.msg = m;
        parse();
    }

    public void parse() {
        if (this.msg != null) {
            int operation = 0;
            try {
                operation = (Integer) this.msg.get(MSG.TYPE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            switch (operation) {
                case MSG.ROOM_ONLINE.TYPE_ID: {
                    try {
                        ArrayList<String> ar = JSONArrayTOArrayList(this.msg.getJSONArray(MSG.ARRAY));
                        main.setUnknowList(ar, ChatersAcitity.ROOMS_ONLINE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(main.getTag(), "msg - получение списка комнат");
                }
                ;
                break;
                case MSG.USRS_ONLINE.TYPE_ID: {
                /*    try {
                        main.makeAllUsrsList(JSONArrayTOArrayList(this.msg.getJSONArray(MSG.ARRAY)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                    try {
                        main.setUnknowList(JSONArrayTOArrayList(this.msg.getJSONArray(MSG.ARRAY)), ChatersAcitity.USRS_ONLINE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(main.getTag(), "msg - получение пользователей онлайн");
                }
                ;
                break;
                case MSG.ERR_MSG.TYPE_ID: {
                    try {
                        main.makeUserMsg((String) this.msg.get(MSG.ERR_TEXT), Toast.LENGTH_LONG);
                        Log.d(main.getTag(), "msg - сообщение об ошибке:\n" + (String) this.msg.get(MSG.ERR_TEXT));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ;
                break;
                case MSG.USERS_IN_THE_ROOM.TYPE_ID: {

                    try {
                        main.setUnknowList(JSONArrayTOArrayList(this.msg.getJSONArray(MSG.ARRAY)), ChatersAcitity.USRS_IN_THE_ROOM);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(main.getTag(), "msg - получение списка полбзователей в комнате");
                }
                ;
                break;
            }
        }

    }

    public static ArrayList<String> JSONArrayTOArrayList(JSONArray arr) {
        ArrayList<String> res = new ArrayList<String>();
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                try {
                    res.add(arr.get(i).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }
}
