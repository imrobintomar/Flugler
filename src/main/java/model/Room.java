package model;

import dao.GenericDAO;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Entity
@NamedNativeQuery(name = "countAll", query = "SELECT COUNT(id) FROM room :args")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer idRoomType;
    @ManyToOne
    private RoomType roomType;
    private Integer number;
    private static GenericDAO DAO = new GenericDAO(Room.class);

    public Room(Integer idRoomType, Integer number) {
        this.idRoomType = idRoomType;
        this.number = number;
    }

    public Room() {
    }

    public Room(String[] data){
        for(String x : data){
            String[] add = x.split("=");
            if(add.length == 1){
                continue;
            }
            if(add[0].equals("id_room_type")){
                this.idRoomType = Integer.valueOf(add[1]);
            }
            if(add[0].equals("number")){
                this.number = Integer.valueOf(add[1]);
            }
        }
    }

    public Room(HttpServletRequest request){
        if(request.getParameter("id_room_type").isEmpty()){
            this.idRoomType = null;
        }else{
            this.idRoomType = Integer.valueOf(request.getParameter("id_room_type"));
        }

        if(request.getParameter("number").isEmpty()){
            this.number = null;
        }else {
            this.number = Integer.valueOf(request.getParameter("number"));
        }
    }

    public Room(ResultSet resultSet){
        try{
            this.id = resultSet.getInt("id");
            this.idRoomType = resultSet.getInt("room_type_fk");
            this.number = resultSet.getInt("number");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public Integer getId() {
        return id;
    }

    public Room setId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getIdRoomType() {
        return idRoomType;
    }

    public Room setIdRoomType(Integer idRoomType) {
        this.idRoomType = idRoomType;
        return this;
    }

    public RoomType getRoomType() {
        if(roomType == null){
            roomType = RoomType.find(idRoomType);
        }
        return roomType;
    }

    public String getAvailability() {
        if(!DAO.findAll("WHERE id = "+this.getId()+ " AND id IN (SELECT room_fk FROM booking WHERE id IN (SELECT booking_fk FROM `check` WHERE status = 1) AND id NOT IN (SELECT booking_fk FROM `check` WHERE status = 0))").isEmpty()){
            return "Occupied";
        }else if(!DAO.findAll("WHERE id=" + this.getId() + " AND id IN (SELECT room_fk FROM booking WHERE id NOT IN (SELECT booking_fk FROM `check`))").isEmpty()){
            return "Booked";
        }
        return "Available";
    }

    public Room setRoomType(RoomType roomType) {
        this.roomType = roomType;
        return this;
    }

    public Integer getNumber() {
        return number;
    }

    public Room setNumber(Integer number) {
        this.number = number;
        return this;
    }

    public static Room save(Room room){
        return (Room) DAO.save(room);
    }

    public static Room find(Integer id){
        return (Room) DAO.find(id);
    }

    public static List<Room> findAll(){
        return DAO.findAll();
    }
    public static Integer countAll(String args){
        HashMap<String, Object> params = new HashMap<>();
        params.put("args", args);

        return (Integer) DAO.executeNamedQuery("countAll",params);
    }
    public static List<Room> findAll(String args){
        return DAO.findAll(args);
    }

    public static void update(Room room){
        DAO.update(room);
    }

    public static void delete(Integer id){
        DAO.delete(id);
    }

    public Room save(){
        return (Room) DAO.save(this);
    }

    public void update(){
        DAO.update(this);
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", idRoomType=" + idRoomType +
                ", roomType=" + roomType +
                ", number=" + number +
                '}';
    }

    public String toJSON(){
        return "{" +
                "\"id\":\"" + id + "\""+
                ", \"idRoomType\":\"" + idRoomType +"\""+
                ", \"number\":\"" + number + "\""+
                "}";
    }
}
