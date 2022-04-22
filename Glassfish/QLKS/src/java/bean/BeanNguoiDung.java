package bean;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.*;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "beanNguoiDung", eager = true)
@SessionScoped //tồn tại tương ứng với HTTP session được tạo khi có một HTTP request đầu tiên gửi tới bean và bị mất đi khi HTTP Session bị vô hiệu, mỗi thông tin người dùng khác nhau 
public class BeanNguoiDung implements Serializable
//. Mục đích của biến này là để chắc chắn trước và sau khi chuyển đổi, lớp BeanNguoiDung  là một 
{
    
    private static final long serialVersionUID = 1437123L;
    private static final DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    
    @ManagedProperty(value = "#{beanKhachSan.listKhachSan}")
//    Đặt giá trị khả dụng trong thời gian @PostConstruct cho phép dễ dàng khởi tạo/tải trước các thuộc tính khác dựa trên giá trị đã đặt.
//   Thuộc tính được quản lý của #{beanKhachSan.listKhachSan} Không được phép trên các bean có phạm vi rộng hơn phạm vi yêu cầu
    private ArrayList<KhachSan> lstKS;
    @ManagedProperty(value = "#{beanNavigation.listDatPhong}")
    private ArrayList<DatPhong> lstDP;
    @ManagedProperty(value = "#{beanTaiKhoan.listTaiKhoan}")
    private ArrayList<TaiKhoan> lstTK;
    private TaiKhoan taiKhoanDangNhap;
    private String nhapLaiMatKhau;
    private KhachSan khachSanGoiY;
    private ArrayList<LichSu> listLichSu;
    
    public BeanNguoiDung() {
        taiKhoanDangNhap = new TaiKhoan();
    }

    public void dangNhap() {
        
        if (taiKhoanDangNhap.getTenTaiKhoan().isEmpty() || taiKhoanDangNhap.getMatKhau().isEmpty()) {
            msg.Message.errorMessage("Thất Bại", "Không được để trống Tên tài khoản hoặc Mật khẩu!");
            return;
//            msg.Message.errorMessage thông báo lỗi
        }
        TaiKhoan tk = dao.DAOTaiKhoan.getByDangNhap(taiKhoanDangNhap.getTenTaiKhoan(), taiKhoanDangNhap.getMatKhau()); 
        //tài khoản trong database nếu có trong database trả về giá trị khác null
        if (tk != null)
        {
            // PrimeFaces current để lấy giá trị các thành phần trong view
            // ở đây để lấy form đăng nhập và bật form đó lên
            PrimeFaces current = PrimeFaces.current();
            current.executeScript("PF('dialog_dangnhap').hide();"); 
            msg.Message.addMessage("Thành Công", "Đăng nhập thành công!");
            FacesContext facesContext = FacesContext.getCurrentInstance();
            // FacesContext là nơi lưu trữ các thông tin trạng thái mà JSF cần để quản lý trạng thái của thành phần GUI cho các request hiện tại.
            HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            // Sau khi đăng nhập thì thêm cookie với thời gian là 36000 giây
            Cookie cookie = new Cookie("TenTaiKhoan", tk.getTenTaiKhoan()); // tạo đối tượng cookie chứa tên tài khoản đăng  nhập vào (key, value)
             cookie.setMaxAge(36000);  
             cookie.setPath("/");
            response.addCookie(cookie); // add cookie (tên tài khoản) vào response để trả về cho client
            cookie = new Cookie("MatKhau", tk.getMatKhau());
            cookie.setMaxAge(36000);
            cookie.setPath("/");
            response.addCookie(cookie);
            
            HttpSession session = request.getSession(); //khởi tạo 1 session
            session.setAttribute("TaiKhoan", tk); //gán giá trị của session 
            if (tk.isIsAdmin()) {
                // Đăng nhập nếu là admin thì chuyển sang trang quản trị
                ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                try {
                    ec.redirect(ec.getRequestContextPath() + "/faces/Admin/adminTaiKhoan.xhtml");
                } catch (IOException e) {
                }
                
            }
        } 
        else {
            msg.Message.errorMessage("Thất Bại", "Sai tên tài khoản hoặc mật khẩu!");
        }
    }
    
    public boolean checkMail(String gmail){
            int viTriA = 0;
            int checkA = 0; // check @
            char str[] = {'g','m','a','i','l','.','c','o','m'};
            int t = 0;
            int checkRong = 0;
            // Kiểm tra chuỗi có @ hay không, và trước @ có phải rỗng hay không
            for(int i=0;i<gmail.length();i++){               
                if(gmail.charAt(i) == '@'){
                    checkA = 1;
                    break;
                }
                checkRong++;          
            }
            if(checkA == 1 && checkRong != 0){
                for(int i=0;i<gmail.length();i++){
                    if(gmail.charAt(i) == '@'){                   
                        viTriA = i;
                        if(gmail.length()-1 - viTriA != 9) // length của gmail - cho vi Trí @ = 9 (gmail.com)
                            return false;
                        else{
                            for(int j=i+1;j<gmail.length();j++){
                                if(gmail.charAt(j)==str[t]){
                                t++; 
                                }
                                else{                              
                                    return false;
                                }
                            }
                        }
                    } 
                }
            }
            else
                return false;
            
        return true;
     }
    
    public void dangKy() {
        
        if (taiKhoanDangNhap.getTenTaiKhoan().isEmpty() || taiKhoanDangNhap.getMatKhau().isEmpty() || taiKhoanDangNhap.getSoDienThoai().isEmpty() || taiKhoanDangNhap.getHoTen().isEmpty()) {
            msg.Message.errorMessage("Thất Bại", "Làm ơn điền hết các trường đi bạn ơi!");
            return;
        }
        
        if(!checkMail(taiKhoanDangNhap.getEmail())){
            msg.Message.errorMessage("Thất Bại", "Email khong hop le!\nEx: xxxxxxxx@gmail.com");
            return;
        }
        
        if (!taiKhoanDangNhap.getMatKhau().equals(nhapLaiMatKhau)) {
            msg.Message.errorMessage("Thất Bại", "Mật khẩu không khớp!");
            return;
        }
        
        if(!(taiKhoanDangNhap.getSoDienThoai().length() == 10)){
             msg.Message.errorMessage("Thất Bại", "Số điện thoại sai!");
             return;
        }
        for(int i=0 ; i<taiKhoanDangNhap.getSoDienThoai().length();i++){
            if(!(taiKhoanDangNhap.getSoDienThoai().charAt(i) >='0' && taiKhoanDangNhap.getSoDienThoai().charAt(i) <= '9')){
                msg.Message.errorMessage("Thất Bại", "Số điện thoại là số!");
                return;
            }
        }
        
        taiKhoanDangNhap.setIsAdmin(false);
        if (dao.DAOTaiKhoan.insert(taiKhoanDangNhap))//kiem tra insert vào sql
        {
            // Đăng ký xong phải cho vào list tài khoản thì mới thấy tài khoản này bên trang quản trị
            lstTK.add(taiKhoanDangNhap);
            msg.Message.addMessage("Thành Công", "Đăng ký thành công!");
            PrimeFaces current = PrimeFaces.current();
            current.executeScript("PF('dialog_dangky').hide();");
            //đóng form sau khi đăng nhập thành công
        }else {
            msg.Message.errorMessage("Thất Bại", "Tên tài khoản đã được sử dụng!");
        }
    }
    
    public void dangXuat() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
        // Đăng xuất thì xóa session và cookie
        HttpSession session = request.getSession();
        session.invalidate();//hủy tất cả session
        Cookie cookie = new Cookie("TenTaiKhoan", "");//gán cookie bằng rỗng
        cookie.setMaxAge(-1);
        cookie.setPath("/");
        response.addCookie(cookie);  // add cookie (tên tài khoản) vào response để trả về cho client
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        try {
            // Đồng thời đưa về trang chủ
            ec.redirect(ec.getRequestContextPath() + "/faces/index.xhtml");
        } catch (IOException e) {
        }
    }
    
    public void caNhan() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession();
        TaiKhoan tk = (TaiKhoan) session.getAttribute("TaiKhoan");
        if (tk == null) {
            // Bấm vào trang cá nhân mà chưa đăng nhập thì bắt đăng nhập
            PrimeFaces current = PrimeFaces.current();
            current.executeScript("PF('dialog_dangnhap').show();");// hiện trang đăng nhập 
            msg.Message.addMessage("Thông Báo", "Bạn cần đăng nhập trước!");
            return;
        }
        taiKhoanDangNhap = new TaiKhoan(tk);
        nhapLaiMatKhau = tk.getMatKhau(); //lấy mk trong TaiKhoan để hiện lên trong trang cá nhân
        // Trong trang cá nhân ở bên phải có mục khách sạn gợi ý, khách sạn ở
        // đây là ngẫu nhiên nên dùng 1 biến để lấy ngẫu nhiên khách sạn trong
        // danh sách
        Random rand = new Random();
        khachSanGoiY = lstKS.get(rand.nextInt(lstKS.size()));
        
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        try {
            // Và rồi chuyển hướng sang trang cá nhân
            ec.redirect(ec.getRequestContextPath() + "/faces/caNhan.xhtml");
        } catch (IOException e) {
        }
    }
    
    public void capNhatThongTin() {
        if (taiKhoanDangNhap.getMatKhau().isEmpty()) {
            msg.Message.errorMessage("Thất Bại", "Không được để trống mật khẩu!");
            return;
        }
        if (!taiKhoanDangNhap.getMatKhau().equals(nhapLaiMatKhau)) {
            msg.Message.errorMessage("Thất Bại", "Mật khẩu không khớp!");
            return;
        }
        if(!(taiKhoanDangNhap.getSoDienThoai().length() == 10)){
             msg.Message.errorMessage("Thất Bại", "Số điện thoại sai!");
             return;
        }
        for(int i=0 ; i<taiKhoanDangNhap.getSoDienThoai().length();i++){
            if(!(taiKhoanDangNhap.getSoDienThoai().charAt(i) >='0' && taiKhoanDangNhap.getSoDienThoai().charAt(i) <= '9')){
                msg.Message.errorMessage("Thất Bại", "Số điện thoại là số!");
                return;
            }
        }
        if (dao.DAOTaiKhoan.update(taiKhoanDangNhap)) 
        {
            msg.Message.addMessage("Thành Công", "Cập nhật thành công!");
        } else {
            msg.Message.errorMessage("Thất Bại", "Cập nhật thất bại!");
        }
        // Cập nhật thông tin trong csdl xong thì cũng phải cập nhật trong session
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession();
        TaiKhoan tk = new TaiKhoan(taiKhoanDangNhap);
        session.setAttribute("TaiKhoan", tk);
    }
    
    public String lichSu() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession();
        TaiKhoan tk = (TaiKhoan) session.getAttribute("TaiKhoan");
        listLichSu = new ArrayList();
        KhachSan ks;
        String thanhTien;
        Date homNay = new Date();
        // Có 3 trạng thái: 0: Có thể hủy, 1: Quá hạn, 2: Đã hủy
        int trangThai;
        String strNgayDat, strNgayDen, strNgayTra;
        for (DatPhong tmp : lstDP) {
            if (tmp.getTaiKhoan().equals(tk.getTenTaiKhoan()))
//                lấy user so sánh với tên đăng nhập
            {
                ks = BeanPhong.hashPhongKhachSan.get(tmp.getIdPhong());// getIdPhong(get id) trả về value ở đây là 1  đối tượng ks mà bên bea phong khai báo
               
                thanhTien = String.format("%,d", tmp.getThanhTien() * 1000); //chuyển int trong DatPhong thành String trong LichSu
               
                if (tmp.isDaHuy()) {
                    // Trong csdl, nếu đã hủy => trạng thái = 2 (đã hủy)
                    trangThai = 2;
                } else {
                    
                    trangThai = (util.CompareDate.compareNoTime(tmp.getNgayDen(), homNay) == -1) ? 1 : 0;//so sánh ngày không so sánh thời gian
                    // getNgayDen < homNay  trả về -1 ->đúng-> =1  quá hạn
                    // getNgayDen > homNay trả về -1 ->sai-> =0 chưa quá hạn
                }
                strNgayDat = formatter.format(tmp.getNgayDat()); //chuyển định dạng date về chuỗi String
                strNgayDen = formatter.format(tmp.getNgayDen());
                strNgayTra = formatter.format(tmp.getNgayTra());
                
                LichSu ls = new LichSu(tmp.getId(), BeanPhong.hashPhong.get(tmp.getIdPhong()), ks.getIdKhachSan(),
                        ks.getTenKhachSan(), strNgayDat, strNgayDen, strNgayTra,
                        tmp.getDichVu(), tmp.getGhiChu(), thanhTien, trangThai);
//                getIdPhong sẽ trả về tên phòng
                
                // Add vào 0, để cái nào mới đặt được cho lên đầu
                listLichSu.add(0, ls);
            }
        }
        
        return "lichSu";
        
    }
    
    public void huyDatPhong(int id) {
        if (dao.DAODatPhong.update(id)) // update là đã hủy trong SQL (hủy đặt phòng  không phải  là xóa)
        {            
            for (LichSu tmp : listLichSu)
            {
                if (tmp.getId() == id) 
                {
                    tmp.setTrangThai(2);//lichsu.xhtml
                    break;
                }
            }
            for (DatPhong tmp : lstDP) 
            {
                if (tmp.getId() == id) 
                {
                    tmp.setDaHuy(true); //boolean daHuy;
                    break;
                }
            }
            msg.Message.addMessage("Thành Công", "Hủy đặt phòng thành công!");
        } 
        else 
        {
            msg.Message.errorMessage("Thất Bại", "Hủy đặt phòng thất bại!");
        }
    }

    
    public TaiKhoan getTaiKhoanDangNhap() {
        return taiKhoanDangNhap;
    }
    
    public void setTaiKhoanDangNhap(TaiKhoan taiKhoanDangNhap) {
        this.taiKhoanDangNhap = taiKhoanDangNhap;
    }
    
    public String getNhapLaiMatKhau() {
        return nhapLaiMatKhau;
    }
    
    public void setNhapLaiMatKhau(String nhapLaiMatKhau) {
        this.nhapLaiMatKhau = nhapLaiMatKhau;
    }
    
    public KhachSan getKhachSanGoiY() {
        return khachSanGoiY;
    }
    
    public void setKhachSanGoiY(KhachSan khachSanGoiY) {
        this.khachSanGoiY = khachSanGoiY;
    }
    
    public ArrayList<KhachSan> getLstKS() {
        return lstKS;
    }
    
    public void setLstKS(ArrayList<KhachSan> lstKS) {
        this.lstKS = lstKS;
    }
    
    public ArrayList<DatPhong> getLstDP() {
        return lstDP;
    }
    
    public void setLstDP(ArrayList<DatPhong> lstDP) {
        this.lstDP = lstDP;
    }
    
    public ArrayList<LichSu> getListLichSu() {
        return listLichSu;
    }
    
    public void setListLichSu(ArrayList<LichSu> listLichSu) {
        this.listLichSu = listLichSu;
    }
    
    public ArrayList<TaiKhoan> getLstTK() {
        return lstTK;
    }
    
    public void setLstTK(ArrayList<TaiKhoan> lstTK) {
        this.lstTK = lstTK;
    }
    
}
