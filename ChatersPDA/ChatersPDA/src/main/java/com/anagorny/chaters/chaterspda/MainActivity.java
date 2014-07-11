package com.anagorny.chaters.chaterspda;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.anagorny.chaters.messege.MSG;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener, ChatersAcitity {
    public static final String HANDLER_KEY = "handler";
    public static final String CONNECT_THRREAD_KEY = "connect_thread";
    private static Intent ChatActiv;
    private Button conBut;
    private EditText serv;
    private EditText port;
    private Socket client;
    private CheckBox reg;
    private LinearLayout RegArea;
    private Button Auth;
    private final int AuthID = 1000;
    private Button Reg;
    private final int RegID = 1001;
    private EditText nick;
    private EditText pass;
    private EditText uName;
    private EditText uSurName;
    private EditText uDescript;
    private ListView lRooms;
    private ListView lUsrInRooms;
    private ListView lAllUsrs;
    public static final String tag = "MainActivity_logs:";
    public static final String BROADCAST_ACTION = "send_messege";
    BroadcastReceiver Reciever;
    public final static int RECIEVE_MESSAGE = 1;
    private Button registerRoom;
    private SocketService service;
    private ServiceConnection sConn;
    private Intent intent;
    boolean bound = false;
    private ArrayList<Integer> RoomsIDList;
    private ArrayList<String> RoomsNameList;
    private String USR_NICK;
    private String USR_PASS;
    private String SRV_IP;
    private String SRV_PORT;
    private ArrayList<String> Rooms;
    private ArrayList<String> UsersOnline;
     private ArrayList<String> UsersInRoom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Log.d(tag, "Args is " + savedInstanceState.toString());
        }


        // h = new ParseInputMsg(this);
        RoomsIDList = new ArrayList<Integer>();
        RoomsNameList = new ArrayList<String>();
        Rooms=new ArrayList<String>();
        UsersOnline=new ArrayList<String>();
        UsersInRoom= new ArrayList<String>();
        List<View> pages = new ArrayList<View>();
        LayoutInflater inflater = LayoutInflater.from(this);/*(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);*/
        View page = inflater.inflate(R.layout.one, null);
        nick = new EditText(this);
        nick.setHint("Nick");
        nick.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        pass = new EditText(this);
        pass.setHint("Password");
        pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        uName = new EditText(this);
        uName.setHint("User Name");
        uName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        uSurName = new EditText(this);
        uSurName.setHint("User SurName");
        uSurName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        uDescript = new EditText(this);
        uDescript.setHint("User's Descript");
        serv = (EditText) page.findViewById(R.id.serv);
        port = (EditText) page.findViewById(R.id.port);
        conBut = (Button) page.findViewById(R.id.conBut2);
        Log.d(tag, (conBut.getText().toString()));
        conBut.setOnClickListener(this);
        reg = (CheckBox) page.findViewById(R.id.regRad);
        reg.setOnClickListener(this);
        RegArea = (LinearLayout) page.findViewById(R.id.regArea);
        Reg = new Button(this);
        Reg.setText("Registration");
        Reg.setOnClickListener(this);
        Reg.setId(RegID);
        Auth = new Button(this);
        Auth.setText("Authorization");
        Auth.setOnClickListener(this);
        Auth.setId(AuthID);

        ConPageToStart();
        pages.add(page);

        page = inflater.inflate(R.layout.two, null);
        lRooms = (ListView) page.findViewById(R.id.Rooms);
        lRooms.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                MainActivity.ChatActiv = new Intent(MainActivity.this, ChatActivity.class);
                //ChatActiv.putExtra(MSG.ROOM_ID,MainActivity.this.getRoomsIDList().get(position));
                ChatActiv.putExtra(MSG.ROOM_NAME, MainActivity.this.getRoomsNameList().get(position));
                startActivity(ChatActiv);

                return false;
            }
        });
        lRooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                JSONObject msg = new JSONObject();
                try {
                    msg.put(MSG.TYPE, MSG.USERS_IN_THE_ROOM_REQUEST.TYPE_ID);
                    msg.put(MSG.ROOM_NAME, RoomsNameList.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                service.WriteMsg(msg.toString());

            }
        });
        lUsrInRooms = (ListView) page.findViewById(R.id.UsrInRoom);
        lAllUsrs = (ListView) page.findViewById(R.id.AllUsrs);
        registerRoom = (Button) page.findViewById(R.id.crtRmsBut);
        registerRoom.setOnClickListener(this);
        pages.add(page);

        page = inflater.inflate(R.layout.three, null);
        pages.add(page);


        SamplePagerAdapter pagerAdapter = new SamplePagerAdapter(pages);
        ViewPager viewPager = new ViewPager(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);

        setContentView(viewPager);

        Reciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(SocketService.TYPE, 0);
                Log.d(tag, "Получено msg - id: " + status);
                switch (status) {
                    case SocketService.RECIEVE_MSG: {
                        try {
                            JSONObject msg = new JSONObject(intent.getStringExtra(SocketService.TEXT));
                            new ParseInputMsg(MainActivity.this, msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(Reciever, intFilt);
        intent = new Intent(getApplicationContext(), SocketService.class);
        sConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.d(tag, "MainActivity onServiceConnected");
                service = ((SocketService.LocalBinder) binder).getService();
                bound = true;
            }

            public void onServiceDisconnected(ComponentName name) {
                Log.d(tag, "MainActivity onServiceDisconnected");
                bound = false;
            }
        };

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        } else {

        }
    }

    public ArrayList<Integer> getRoomsIDList() {
        return RoomsIDList;
    }

    public ArrayList<String> getRoomsNameList() {
        return RoomsNameList;
    }

    public void setRoomsIDList(ArrayList<Integer> roomsIDList) {
        RoomsIDList = roomsIDList;
    }

    public void setRoomsNameList(ArrayList<String> roomsNameList) {
        RoomsNameList = roomsNameList;
    }


    @Override
    protected void onResume() {
        super.onResume();
        getApplicationContext().bindService(intent, sConn, Context.BIND_AUTO_CREATE);
        getApplicationContext().startService(intent);
    }

    @Override
    protected void onPause() {
        super.onStop();
        if (!bound) return;
        Log.d(tag, "MainActivity Paused");
        getApplicationContext().unbindService(sConn);
        bound = false;
    }


    private void ConPageToStart() {
        conBut.setEnabled(true);
        port.setEnabled(true);
        serv.setEnabled(true);
        reg.setEnabled(false);
    }

    private void AuthPageToStart() {
        conBut.setEnabled(true);
        port.setEnabled(true);
        serv.setEnabled(true);
        reg.setEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void RegOrAuthViewes(boolean status) {
        if (!status) {
            RegArea.removeAllViews();
            RegArea.addView(nick);
            RegArea.addView(pass);

            RegArea.addView(Auth);

        } else {
            RegArea.removeAllViews();
            RegArea.addView(nick);
            RegArea.addView(pass);
            RegArea.addView(uName);
            RegArea.addView(uSurName);
            RegArea.addView(uDescript);

            RegArea.addView(Reg);

        }
    }

    @Override
    public void onClick(View view) {
        Log.d(tag, "Я вызван!!");
        switch (view.getId()) {
            case R.id.conBut2: {
                if (port.getText().toString().equals("") || serv.getText().toString().equals("")) {
                    makeUserMsg("Заполните все поля!", Toast.LENGTH_LONG);
                    return;
                } else {
                    conBut.setEnabled(false);
                    port.setEnabled(false);
                    serv.setEnabled(false);
                    int res = service.onConnect(port.getText().toString(), serv.getText().toString());
                    if (res == 0) {
                        makeUserMsg("Соединение не установлено!", Toast.LENGTH_SHORT);
                        ConPageToStart();
                        return;
                    } else {
                        makeUserMsg("Соединение установлено!", Toast.LENGTH_SHORT);
                        SRV_IP = serv.getText().toString();
                        SRV_PORT = port.getText().toString();
                        reg.setEnabled(true);
                        RegOrAuthViewes(reg.isSelected());
                    }
                }
            }
            break;
            case R.id.regRad: {
                RegOrAuthViewes(reg.isChecked());
            }
            ;
            break;

            case AuthID: {
                if (nick.getText().toString().equals("") || pass.getText().toString().equals("")) {
                    makeUserMsg("Заполните все поля!", Toast.LENGTH_LONG);
                    return;
                } else {
                    Log.d(tag, "NicK: " + nick.getText() + " Pass: " + pass.getText());
                    JSONObject auth = new JSONObject();
                    try {
                        auth.put(MSG.TYPE, MSG.AUTH_MSG.TYPE_ID);
                        auth.put(MSG.USR_NICK, nick.getText().toString());
                        auth.put(MSG.USR_PASS, pass.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    service.WriteMsg(auth.toString());
                    USR_NICK = nick.getText().toString();
                    USR_PASS = pass.getText().toString();
                    findViewById(AuthID).setEnabled(false);
                    makeUserMsg("Авторизация удалась!", Toast.LENGTH_SHORT);

                }
            }
            ;
            break;
            case RegID: {
                if (nick.getText().toString().equals("") || pass.getText().toString().equals("")) {
                    makeUserMsg("\"Nick\" и \"Password\"\n обязательны для заполнения!", Toast.LENGTH_LONG);
                    return;
                } else {
                    Log.d(tag, "NicK: " + nick.getText() + " Pass: " + pass.getText());
                    JSONObject reg = new JSONObject();
                    try {
                        reg.put(MSG.TYPE, MSG.REG_MSG.TYPE_ID);
                        reg.put(MSG.USR_NICK, nick.getText().toString());
                        reg.put(MSG.USR_PASS, pass.getText().toString());
                        reg.put(MSG.USR_NAME, uName.getText().toString());
                        reg.put(MSG.USR_SURNAME, uSurName.getText().toString());
                        reg.put(MSG.USR_DESCRIPT, uDescript.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    service.WriteMsg(reg.toString());
                    USR_NICK = nick.getText().toString();
                    USR_PASS = pass.getText().toString();
                    nick.setEnabled(false);
                    pass.setEnabled(false);
                    uName.setEnabled(false);
                    uSurName.setEnabled(false);
                    uDescript.setEnabled(false);
                    findViewById(RegID).setEnabled(false);
                    makeUserMsg("Регистрация удалась!", Toast.LENGTH_SHORT);

                }
            }
            ;
            break;
            case R.id.crtRmsBut: {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Создание новой комнаты");
                alert.setMessage("Введите имя комнаты:");
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        JSONObject nm = new JSONObject();
                        try {
                            nm.put(MSG.TYPE, MSG.CRT_ROOM.TYPE_ID);
                            nm.put(MSG.ROOM_NAME, value);
                            nm.put(MSG.ROOM_DESCRIPT, "test");
                            nm.put(MSG.ROOM_IS_OPENED, true);
                            service.WriteMsg(nm.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
            break;
         /*   case R.id.Rooms: {
                ChatActiv=new Intent(this,ChatActivity.class);


                startActivity(ChatActiv);
            }*/


        }
    }


    @Override
    public void setUnknowList(ArrayList<? extends Object> ar, int type) {
        switch (type) {
            case ChatersAcitity.USRS_ONLINE: {
                UsersOnline = new ArrayList<String>();
                for (Object i : ar) {
                    UsersOnline.add(String.valueOf(i));
                }
                makeAllUsrsList(UsersOnline);
            }
            ;
            break;
            case ChatersAcitity.ROOMS_ONLINE: {
                Rooms = new ArrayList<String>();
                for (Object i : ar) {
                    Rooms.add(String.valueOf(i));
                }
                setRoomsNameList(Rooms);
                makeRoomsList(Rooms);
            }
            ;
            break;
            case ChatersAcitity.USRS_IN_THE_ROOM: {
                UsersInRoom = new ArrayList<String>();
                for (Object i : ar) {
                    UsersInRoom.add(String.valueOf(i));
                }
                makeUsrsinRoomList(UsersInRoom);
            }
            ;
            break;

        }

    }

    @Override
    public void makeUserMsg(String txt, int dur) {
        Toast.makeText(this, txt, dur).show();
    }

    @Override
    public String getTag() {
        return tag;
    }

    protected void makeRoomsList(ArrayList<String> arr) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr);
        lRooms.setAdapter(adapter);
    }


    protected void makeAllUsrsList(ArrayList<String> arr) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr);
        lAllUsrs.setAdapter(adapter);
    }

    protected void makeUsrsinRoomList(ArrayList<String> arr) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr);
        lUsrInRooms.setAdapter(adapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // дерегистрируем (выключаем) BroadcastReceiver
        unregisterReceiver(Reciever);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("ip", SRV_IP);
        outState.putString("port", SRV_PORT);
        outState.putString("user_nick", USR_NICK);
        outState.putString("user_pass", USR_PASS);
        outState.putString("ip-view", serv.getText().toString());
        outState.putString("port-view", port.getText().toString());
        outState.putBoolean("conBut-enabled", conBut.isEnabled());
        outState.putBoolean("regBut-enabled", reg.isEnabled());
        outState.putBoolean("regBut-checked", reg.isChecked());

        outState.putBoolean("nick-enabled",nick.isEnabled());
        outState.putString("nick-val",nick.getText().toString());
        outState.putBoolean("pass-enabled",pass.isEnabled());
        outState.putString("pass-val",pass.getText().toString());
        if (reg.isChecked()) {
            outState.putBoolean("uName-enabled",uName.isEnabled());
            outState.putString("uName-val", uName.getText().toString());
            outState.putBoolean("uSurName-enabled",uSurName.isEnabled());
            outState.putString("uSurName-val",uSurName.getText().toString());
            outState.putBoolean("uDescript-enabled",uDescript.isEnabled());
            outState.putString("uDescript-val",uDescript.getText().toString());
            if(Reg!=null){
                outState.putBoolean("Reg-enabled",Reg.isEnabled());
            }
        }else{
            if (Auth!=null){
                outState.putBoolean("Auth-enabled",Auth.isEnabled());
            }
        }
        outState.putSerializable("rooms", Rooms);
        outState.putSerializable("usersOnline", UsersOnline);
        outState.putSerializable("usersInRoom", UsersInRoom);
        Log.d(tag, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        SRV_IP = outState.getString("ip");
        SRV_PORT = outState.getString("port");
        USR_NICK = outState.getString("user_nick");
        USR_PASS = outState.getString("user_pass");
        serv.setText(outState.getString("ip-view"));
        port.setText(outState.getString("port-view"));
        conBut.setEnabled(outState.getBoolean("conBut-enabled"));
        serv.setEnabled(conBut.isEnabled());
        port.setEnabled(conBut.isEnabled());
        reg.setEnabled(outState.getBoolean("regBut-enabled"));
        reg.setChecked(outState.getBoolean("regBut-checked"));

        nick.setEnabled(outState.getBoolean("nick-enabled"));
        nick.setText(outState.getString("nick-val"));
        pass.setEnabled(outState.getBoolean("pass-enabled"));
        pass.setText(outState.getString("pass-val"));
        RegOrAuthViewes(reg.isChecked());
        if (reg.isChecked()) {

            uName.setEnabled(outState.getBoolean("uName-enabled"));
            uName.setText(outState.getString("uName-val"));
            uSurName.setEnabled(outState.getBoolean("uSurName-enabled"));
            uSurName.setText(outState.getString("uSurName-val"));
            uDescript.setEnabled(outState.getBoolean("uDescript-enabled"));
            uDescript.setText(outState.getString("uDescript-val"));
            Reg.setEnabled(outState.getBoolean("Reg-enabled"));
        }else{
            Auth.setEnabled(outState.getBoolean("Auth-enabled"));
        }

        Rooms= (ArrayList<String>) outState.getSerializable("rooms");
        UsersOnline= (ArrayList<String>) outState.getSerializable("usersOnline");
        UsersInRoom= (ArrayList<String>) outState.getSerializable("usersInRoom");

        Log.d(tag, "onRestoreInstanceState");
    }

}



