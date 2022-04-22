package model;

public class LopCha{
    protected int idKhachSan;
    protected String tenKhachSan;
    
    public LopCha(){
        
    }
    
    public LopCha(int idKhachSan, String tenKhachSan){
        this.idKhachSan = idKhachSan;
        this.tenKhachSan = tenKhachSan;
    }
    
    public LopCha(LopCha ps){
        this.idKhachSan = ps.idKhachSan;
        this.tenKhachSan = ps.tenKhachSan;
    }
    
    public int getIdKhachSan(){
        return idKhachSan;
    }
    
    public void setIdKhachSan(int idKhachSan){
        this.idKhachSan = idKhachSan;
    }
    
    public String getTenKhachSan(){
        return tenKhachSan;
    }
    
    public void setTenKhachSan(String tenKhachSan){
        this.tenKhachSan = tenKhachSan;
    }
}