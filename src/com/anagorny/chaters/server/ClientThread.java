package com.anagorny.chaters.server;

import com.anagorny.chaters.DAO.Connector;
import com.anagorny.chaters.messege.MSG;
import com.anagorny.chaters.mapObj.Messege;
import com.anagorny.chaters.room.MainRoom;
import com.anagorny.chaters.mapObj.Room;
import com.anagorny.chaters.mapObj.User;
import com.anagorny.chaters.user.UserInstance;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: sosnov
 * Date: 04.01.14
 * Time: 19:54
 * To change this template use File | Settings | File Templates.
 */
public class ClientThread extends Thread {
    private Socket cliSock;
    private UserInstance usr;
    private DataInputStream In;
    private DataOutputStream Out;

    public ClientThread(Socket client) {
        this.cliSock = client;
        this.start();

    }

    public void run() {
        try {
            this.Out = new DataOutputStream(this.cliSock.getOutputStream());
            this.In = new DataInputStream(this.cliSock.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.err.println("Client has connected from " + cliSock.getInetAddress().getAddress());
        while (true) {
            try {
                String Msg = this.In.readUTF();
                JSONParser prsr = new JSONParser();
                JSONObject jsonObj = (JSONObject) prsr.parse(Msg);
                int operation = ((Long) jsonObj.get(MSG.TYPE)).intValue();
                switch (operation) {
                    case MSG.USR_MSG.TYPE_ID: {
                        System.err.println("Recieved msg");
                        System.err.println(jsonObj.toString());
                        try {
                            Connector.getInstance().getMsgDAO().addObj(new Messege(jsonObj));
                        } catch (SQLException e) {
                            System.err.println("Error while add Msg: ");
                            e.printStackTrace();
                        }
                        SendToRoom(jsonObj);
                    }
                    ;
                    break;
                    case MSG.AUTH_MSG.TYPE_ID: {
                        authProcess(jsonObj);
                    }
                    ;
                    break;
                    case MSG.REG_MSG.TYPE_ID: {
                        regProcess(jsonObj);
                    }
                    ;
                    break;
                    case MSG.CRT_ROOM.TYPE_ID: {
                        regRoomProcess(jsonObj);
                    }
                    ;
                    break;
                    case MSG.USERS_IN_THE_ROOM_REQUEST.TYPE_ID: {
                        String RoomName = (String) jsonObj.get(MSG.ROOM_NAME);
                        JSONObject msg = new JSONObject();
                        msg.put(MSG.TYPE, MSG.USERS_IN_THE_ROOM.TYPE_ID);
                        JSONArray ar = new JSONArray();
                        try {
                            Room rooms = Server.getRoomFromName(RoomName);
                            if (rooms != null) {
                                ar.addAll(Server.GetUsrsName((ArrayList<User>) Connector.getInstance()
                                        .getRoomDAO().getUsersByRoom(rooms)));
                            }
                            //ar.addAll(Server.GetUsrsName(Server.getUserInTheRoom(Server.getRoomFromName(RoomName))));

                        } catch (SQLException esq) {
                            esq.printStackTrace();
                        }
                        msg.put(MSG.ARRAY, ar);
                        sendToThisUser(msg);
                    }
                    ;
                    break;
                    case MSG.CONNECTED_TO_ROOM.TYPE_ID: {
                        String RoomName = (String) jsonObj.get(MSG.ROOM_NAME);
                        Room room = Server.getRoomFromName(RoomName);
                        System.err.println("usr in room: "+usr.getUser().getRooms().contains(room)) ;
                        if (room != null && !usr.getUser().getRooms().contains(room)) {
                            usr.getUser().getRooms().add(room);
                            System.err.print("Clients rooms: "+usr.getUser().getRooms());
                            try {
                                Connector.getInstance().getUserDAO().updateObj(usr.getUser());
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            JSONObject msg = new JSONObject();
                            msg.put(MSG.TYPE, MSG.HAS_CONNECTED_TO_ROOM.TYPE_ID);
                            msg.put(MSG.ROOM_ID, room.getRoom_id());
                            msg.put(MSG.USR_NICK, this.usr.getUser().getUser_nick());
                            sendToThisUser(msg);
                            msg = new JSONObject();
                            msg.put(MSG.TYPE, MSG.SRV_MSG.TYPE_ID);


                             System.err.println( usr.getUser());

                            // Server.addUsrToRoom(Server.getRoomFromName(RoomName).getRoom_id(), usr.getUser().getUser_id());
                            msg.put(MSG.SRV_MSG.TEXT, Server.getDate() + "Client " + usr.getUser().getUser_name() + " has be connected to room.");
                            msg.put(MSG.ROOM_ID, Server.getRoomFromName(RoomName).getRoom_id());
                            System.err.println(Server.getDate() + "Client " + usr.getUser().getUser_name() + " has be connected to room.");
                            System.err.println("msg ready to post!");
                            SendToRoom(msg);
                        }
                    }
                    ;
                    break;
                }

            } catch (java.io.EOFException eofe) {
                System.err.println("Clients Socket " + cliSock.getInetAddress().getAddress().toString() + " has disconected!");
                if (usr != null) {
                    //Server.UsrSock.put(usr.getUser(), null);
                    //Server.ClientsSockets.remove(this);
                    Server.getUsrsInstc().remove(usr);
                    try {
                        Connector.getInstance().getUserDAO().updateObj(usr.getUser());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    try {
                        this.In.close();
                        this.Out.close();
                        cliSock.close();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.


            } catch (IOException ioe) {
                System.err.println("Error in Clients Socket");
                ioe.printStackTrace();

                return;

            }

        }
    }

    private void regRoomProcess(JSONObject msg) {
        //To change body of created methods use File | Settings | File Templates.
        Room room = null;
        room = Server.getRoomFromName((String) msg.get(MSG.ROOM_NAME));
        if (room != null) {
            System.err.println("Неверное имя комнаты!");
            JSONObject m = new JSONObject();
            m.put(MSG.TYPE, MSG.ERR_MSG.TYPE_ID);
            m.put(MSG.ERR_ID, MSG.ERR_MSG.UNCORRECT_ROOM_NAME);
            m.put(MSG.ERR_TEXT, "Комната с таким именем уже существует!");
            try {
                this.Out.writeUTF(m.toJSONString());

            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else {


            Room nr = new Room((String) msg.get(MSG.ROOM_NAME),
                    (String) msg.get(MSG.ROOM_DESCRIPT),
                    (boolean) msg.get(MSG.ROOM_IS_OPENED));
          //  Server.createRoom(nr, usr.getUser());
            try {
                Connector.getInstance().getRoomDAO().addObj(nr);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            sendRoomList();
            System.err.println("Rooms list sended!");
        }
    }

    /*
       private void mesProcess(JSONObject msg) {
           for (long i : Server.RoomUsrs.get((Long) (msg.get(MSG.ROOM_ID)))) {
               try {
                   this.Out.writeUTF(msg.toJSONString());
                   this.Out.flush();
                   //out.close();
               } catch (IOException e) {
                   e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               }
           }
       }
       */
    private void regProcess(JSONObject msg) {
       /* for (User i : Server.UsersList) {
            if ((msg.get(MSG.USR_NICK)).equals(i.getUser_nick())) {
                System.err.println("ERR in regProcess: Данное имя пользователя уже используется");
                JSONObject m = new JSONObject();
                m.put(MSG.TYPE, MSG.ERR_MSG.TYPE_ID);
                m.put(MSG.ERR_ID, MSG.ERR_MSG.UNCORRECT_USER);
                m.put(MSG.ERR_TEXT, "Данное имя пользователя уже используется!");
                try {
                    this.Out.writeUTF(m.toJSONString());
                    // out.close();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                return;
            }
        }      */
        User new_usr = new User((String) msg.get(MSG.USR_NICK), (String) msg.get(MSG.USR_PASS)/*, this*/);
        new_usr.setUser_name((String) msg.get(MSG.USR_NAME));
        new_usr.setUser_surname((String) msg.get(MSG.USR_SURNAME));
        new_usr.setUser_descript((String) msg.get(MSG.USR_DESCRIPT));
        System.err.println("USrID is: " + ((new_usr.getUser_id() != null) ? new_usr.getUser_id() : "null"));
        new_usr.getRooms().add(Server.mainRoom);
        try {
            Connector.getInstance().getUserDAO().addObj(new_usr);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.err.println("USrID is: " + ((new_usr.getUser_id() != null) ? new_usr.getUser_id() : "null"));
        //Server.createUser(new_usr, cliSock);

        this.usr = Server.newUsrOnLine(new_usr, this);
        System.err.println(new_usr);
        System.err.println( usr.getUser());
        sendRoomList();
        sendUserList();
    }

    private void authProcess(JSONObject msg) {
       /* if (Server.UsersList.size() == 0) {
            JSONObject m = new JSONObject();
            m.put(MSG.TYPE, MSG.ERR_MSG.TYPE_ID);
            m.put(MSG.ERR_ID, MSG.ERR_MSG.UNCORRECT_USER);
            m.put(MSG.ERR_TEXT, "Неверное имя пользователя!");
            try {
                this.Out.writeUTF(m.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }*/
        User Usr = null;
        try {
            Usr = Connector.getInstance().getUserDAO().getUsrFromNick((String) msg.get(MSG.USR_NICK));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (Usr != null ) {
                if (Usr.getPass().equals((String) msg.get(MSG.USR_PASS))) {
                    this.usr = Server.newUsrOnLine(Usr, this);
                    //Server.UsrSock.put(i, cliSock);
                    sendRoomList();
                    sendUserList();
                } else {
                    JSONObject m = new JSONObject();
                    m.put(MSG.TYPE, MSG.ERR_MSG.TYPE_ID);
                    m.put(MSG.ERR_ID, MSG.ERR_MSG.UNCORRECT_PASS);
                    m.put(MSG.ERR_TEXT, "Неверный пароль!");
                    try {
                        this.Out.writeUTF(m.toJSONString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
            }
        } else {
            JSONObject m = new JSONObject();
            m.put(MSG.TYPE, MSG.ERR_MSG.TYPE_ID);
            m.put(MSG.ERR_ID, MSG.ERR_MSG.UNCORRECT_USER);
            m.put(MSG.ERR_TEXT, "Неверное имя пользователя!");
            try {
                this.Out.writeUTF(m.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return;
        }
            /*if (i.getUser_nick().equals((String) msg.get(MSG.USR_NICK))) {
                if (i.getPass().equals((String) msg.get(MSG.USR_PASS))) {
                    this.usr = Server.newUsrOnLine(i, this);
                    Server.UsrSock.put(i, cliSock);
                    sendRoomList();
                    sendUserList();
                    break;
                } else {
                    JSONObject m = new JSONObject();
                    m.put(MSG.TYPE, MSG.ERR_MSG.TYPE_ID);
                    m.put(MSG.ERR_ID, MSG.ERR_MSG.UNCORRECT_PASS);
                    m.put(MSG.ERR_TEXT, "Неверный пароль!");
                    try {
                        this.Out.writeUTF(m.toJSONString());
                        // AppendingObjectOutputStream out = new AppendingObjectOutputStream(cliSock.getOutputStream());
                        //out.writeObject(m);
                        // out.flush();
                        // out.close();
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    return;
                }
            } else if(Users.indexOf(i)!=Users.size()-1){
                JSONObject m = new JSONObject();
                m.put(MSG.TYPE, MSG.ERR_MSG.TYPE_ID);
                m.put(MSG.ERR_ID, MSG.ERR_MSG.UNCORRECT_USER);
                m.put(MSG.ERR_TEXT, "Неверное имя пользователя!");
                try {
                    this.Out.writeUTF(m.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                return;
            }*/


        // }

    }

    private void sendRoomList() {
        JSONObject m = new JSONObject();
        m.put(MSG.TYPE, MSG.ROOM_ONLINE.TYPE_ID);
        JSONArray arr = new JSONArray();
        HashSet<Room> tmp = null;
        ArrayList<String> name = null;
        try {
            tmp = new HashSet<>();
            tmp.addAll((ArrayList<Room>)Connector.getInstance().getRoomDAO().getAllObj());
            for (Room i : tmp) {
                System.err.println(i.getRoom_id()+" "+i.getRoom_name());
                arr.add(i.getRoom_name());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        arr.remove(MainRoom.MAIN_ROOM_NAME);
        m.put(MSG.ARRAY, arr);
        //this.Out.writeUTF(m.toJSONString());
        SendToAll(m);
    }

    private void sendUserList() {
        JSONObject m = new JSONObject();
        m.put(MSG.TYPE, MSG.USRS_ONLINE.TYPE_ID);
        JSONArray arr = new JSONArray();
        arr.addAll(Server.GetUsrsName(Server.GetOnLineUsers()));
        m.put(MSG.ARRAY, arr);
        // this.Out.writeUTF(m.toJSONString());
        SendToAll(m);
    }

    private void sendToThisUser(JSONObject m) {
        try {
            this.Out.writeUTF(m.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void SendToRoom(JSONObject m) {
        System.err.println("SendToRoom is called!");
        long r_id = ((Long) m.get(MSG.ROOM_ID)).longValue();
        HashSet<User> usrs = null;
        try {
            Room rm = Connector.getInstance().getRoomDAO().getObjById(r_id);
            usrs = new HashSet<User>(Connector.getInstance().getRoomDAO().getUsersByRoom(rm));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        HashSet<UserInstance> UsrInstc = Server.getUsrsInstc();

         System.err.println("All actives users: ");
        for (UserInstance i : UsrInstc) {

            System.err.println("\t " + i.getUser().getUser_nick()+" "+i.getUser().hashCode());
        }
        System.err.println("All usrs in room from db: ");
        for (User i : usrs) {

            System.err.println("\t " + i.getUser_nick()+" "+i.hashCode());
        }
        UsrInstc.retainAll(usrs);
        System.err.println("\nActives users in the room: ");
        for (UserInstance i : UsrInstc) {
            System.err.print(" " + i.getUser().getUser_nick());
        }

        for (UserInstance i : UsrInstc) {
            try {
                i.getSock().Out.writeUTF(m.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    /*    if (Server.RoomUsrs.get(r_id) != null) {
            for (Long i : Server.RoomUsrs.get(r_id)) {
                try {
                    Server.getUserFromId(i).getSock().Out.writeUTF(m.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }*/
    }

    public void SendToAll(JSONObject m) {
        for (UserInstance i : Server.getUsrsInstc()) {
            try {
                i.getSock().Out.writeUTF(m.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public Socket getCliSock() {
        return cliSock;
    }

    /*public User getUsr() {
        return usr;
    }  */

    public DataInputStream getIn() {
        return In;
    }

    public DataOutputStream getOut() {
        return Out;
    }
}

