package model;

import java.util.ArrayList;
//class BuaAn phải @Override tất cả các phương thức trừu tượng của interface Serializable
public class BuaAn {
    
    
    
    public static ArrayList<BuaAn> listBuaAn = new ArrayList(){
        {
            add(new BuaAn(0, "Không có"));
            add(new BuaAn(1, "Bữa sáng"));
            add(new BuaAn(2, "Bữa sáng và bữa trưa"));
            add(new BuaAn(1, "Bữa sáng và bữa tối"));
            add(new BuaAn(1, "Cả ba bữa"));
        }
    };

    int id;
    String ten;
    
    public BuaAn(){
        
    }
    
    public BuaAn(int id, String ten){
        this.id = id;
        this.ten = ten;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

}