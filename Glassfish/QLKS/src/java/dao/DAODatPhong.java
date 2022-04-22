package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import model.DatPhong;

public class DAODatPhong {

    private static Connection con;
   
    public static ArrayList<DatPhong> getAll() { // Lấy tất cả từ sql ra array 
        ArrayList<DatPhong> list = new ArrayList();
        try {
            con = SQLConnection.getConnection(); //kết nối sql
            Statement stmt = con.createStatement(); //stmt là biến dùng để truy vấn sql
            ResultSet rs = stmt.executeQuery("select * from DatPhong"); //duy trì một con trỏ trỏ đến một hàng của một bảng. Ban đầu, con trỏ trỏ đến hàng đầu tiên.
            while (rs.next()) { //Khi rs vẫn còn, chưa phải là cuối cùng
                DatPhong tmp = new DatPhong();
                tmp.setId(rs.getInt("Id")); // rs.getInt("Id") sẽ trả về dữ liệu tại cột id và đưa dữ liệu đó vào tmp qua hàm setId().
                tmp.setTaiKhoan(rs.getString("TaiKhoan"));
                tmp.setIdPhong(rs.getInt("IdPhong"));
                tmp.setNgayDat(rs.getDate("NgayDat"));
                tmp.setNgayDen(rs.getDate("NgayDen"));
                tmp.setNgayTra(rs.getDate("NgayTra"));
                tmp.setDichVu(rs.getString("DichVu"));
                tmp.setGhiChu(rs.getString("GhiChu"));
                tmp.setThanhTien(rs.getInt("ThanhTien"));
                tmp.setDaHuy(rs.getBoolean("DaHuy"));
                list.add(tmp);//Sau khi đưa tất cả các thuộc tính vào tmp, ta thêm tmp vào list
            }
            con.close();
        } catch (Exception e) {
        }
        return list;
    }

    public static boolean insert(DatPhong tmp) { // Thêm vào sql
        try {
            con = SQLConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement("insert into DatPhong output inserted.Id values(?,?,?,?,?,?,?,?,?)"); //PreparedStatement thực hiện truy vấn tham số
            stmt.setString(1, tmp.getTaiKhoan()); // getTK và đưa vào tham số ? thứ 1
            stmt.setInt(2, tmp.getIdPhong());
            stmt.setDate(3, new java.sql.Date(tmp.getNgayDat().getTime()));//Chuyển dữ liêuj date sang date bên sql
            stmt.setDate(4, new java.sql.Date(tmp.getNgayDen().getTime()));
            stmt.setDate(5, new java.sql.Date(tmp.getNgayTra().getTime()));
            stmt.setString(6, tmp.getDichVu());
            stmt.setString(7, tmp.getGhiChu());
            stmt.setInt(8, tmp.getThanhTien());
            stmt.setBoolean(9, tmp.isDaHuy());
            ResultSet rs = stmt.executeQuery(); //Thực hiện truy vấn
            if (rs.next()) { 
                tmp.setId(rs.getInt("Id"));
            }
            con.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean update(int id) { //Set về trạng thái đã hủy đặt phòng với id là tham số
        try {
            con = SQLConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement("update DatPhong set DaHuy=? where Id=?");
            stmt.setBoolean(1, true);
            stmt.setInt(2, id);
            stmt.executeUpdate(); //update 
            con.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean delete(int id) {
        try {
           
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
}
