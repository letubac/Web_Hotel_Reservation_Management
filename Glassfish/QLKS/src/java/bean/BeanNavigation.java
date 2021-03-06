//BeanNavigation để lưu các thông tin chuyển hướng trang 
package bean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import model.*;
import org.primefaces.PrimeFaces;


//Thực thi tiến trình để xác định trang tiếp theo mà ứng dụng phải điều hướng tới.
@ManagedBean(name = "beanNavigation")
@SessionScoped 
//class BeanTaiKhoan phải @Override tất cả các phương thức trừu tượng của interface Serializable
public class BeanNavigation implements Serializable {

    private static final long serialVersionUID = 16533786L;
    private static final DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

    // @ManagedProperty để lấy giá trị bean khác sang bean này cụ thể lấy
    // beanKhachSan.listKhachSan và truyền vào biến lstKS, tương tự bên dưới
    @ManagedProperty(value = "#{beanKhachSan.listKhachSan}")
    private ArrayList<KhachSan> lstKS;
    @ManagedProperty(value = "#{beanPhong.listPhong}")
    private ArrayList<Phong> lstP;
    private ArrayList<KhachSan> listKhachSan;
    private ArrayList<KhachSan> listKhachSanSave;
    private KhachSan khachSan;
    private ArrayList<Phong> listPhong;
    private ArrayList<Phong> listPhongSave;
    private String tenThanhPhoTimKiem;
    private Date minDate;
    private Date ngayDat;
    private Date ngayDen;
    private Date ngayTra;
    private String strNgayDat;
    private String strNgayDen;
    private String strNgayTra;
    private String strThanhTien;
    private int soNgayDatPhong;
    private boolean daKiemTraPhongTrong;
    private ArrayList<DatPhong> listDatPhong;
    private Phong phongDangDat;
    private DatPhong datPhong;
    private boolean daDatPhong;

    // Các danh sách lọc
    private ArrayList<Checkbox> listXepHang;
    private ArrayList<Checkbox> listLoaiKhachSan;

    // Khởi tạo, khi có session mới, tất cả các Bean SessionScoped sẽ được gọi bao gồm cả hàm này
    // Lí do Bean này là Session vì mỗi người (trình duyệt) sẽ có một trạng thái checkbox khác nhau
    // Nếu để ApplicationScoped thì bên máy này tích vào một ô thì máy khác cũng thấy thế
    public BeanNavigation() {
        // Ngày tìm kiếm nhập vào nhỏ nhất là hôm nay (new Date return hôm nay)
        minDate = new Date();
        ngayDat = new Date();
        ngayDen = new Date();
        ngayTra = new Date();
        datPhong = new DatPhong();
        // Khởi tạo danh sách lọc
        listXepHang = new ArrayList();
        listXepHang.add(new Checkbox("Không xếp hạng"));
        listXepHang.add(new Checkbox("1 Sao"));
        listXepHang.add(new Checkbox("2 Sao"));
        listXepHang.add(new Checkbox("3 Sao"));
        listXepHang.add(new Checkbox("4 Sao"));
        listXepHang.add(new Checkbox("5 Sao"));
        
        listLoaiKhachSan = new ArrayList();
        //lọc dữ liệu trong map hashLoaiKhachSan
        //Sử dụng một vòng lặp for để iterator qua toàn bộ Entry vừa được lưu vào Set
        for (Entry<Integer, String> entry : BeanLoaiKhachSan.hashLoaiKhachSan.entrySet()) {
            listLoaiKhachSan.add(new Checkbox(entry.getValue()));
        }
        //Khởi tạo Đặt phòng
        listDatPhong = dao.DAODatPhong.getAll();
    }

    // Reset Các thông tin tìm kiếm: xóa trạng thái đã tích của check box, nếu hàm này ko được
    // gọi thì tích vào checkbox nào, load lại trang checkbox vẫn còn, đồng thời hàm lưu lại danh sách
    // khách sạn đã tìm thấy listKhachSanSave, về sau sẽ lọc trong danh sách này và hiển thị bằng listKhachSan
    private void resetTimKiem() {
        listXepHang.forEach((tmp) -> {
            tmp.setChecked(false);
        });
        listLoaiKhachSan.forEach((tmp) -> {
            tmp.setChecked(false);
        });
        listKhachSanSave = listKhachSan;
    }

    // Các link trên Header
    public String trangChu() {
        return "index";
    }

    public String toanBoKhachSan() {
        listKhachSan = new ArrayList();
        for (KhachSan tmp : lstKS) {
            listKhachSan.add(tmp);
        }
        resetTimKiem();
        return "dsKhachSan";
    }

    public String tinTuc() {
        return "tinTuc";
    }

    public String lienHe() {
        return "lienHe";
    }

    // Tìm kiếm ở Trang chủ, tìm theo Thành phố, 
    // Trước tiên đưa hết từ khóa tìm kiếm về chữ thường, sau đó loại bỏ dấu rồi mới
    // so sánh với các khách sạn trong danh sách
    public String timKiem() {
        ArrayList<Integer> listKSTrong = new ArrayList();
        for (KhachSan tmp : lstKS) {
                listKSTrong.add(tmp.getIdKhachSan());
            }
        
        String tenThanhPhoKoDau = util.VNCharacterUtils.removeAccent(tenThanhPhoTimKiem.toLowerCase());
        String s;
        listKhachSan = new ArrayList();
        for (KhachSan tmp : lstKS) {
            s = util.VNCharacterUtils.removeAccent(tmp.getTenThanhPho().toLowerCase());
            if (s.contains(tenThanhPhoKoDau) && listKSTrong.contains(tmp.getIdKhachSan())) {
                listKhachSan.add(tmp);
            }
        }
        resetTimKiem();
        return "dsKhachSan";
    }

    // Các link khi bấm chọn một Thành phố, Loại khách sạn, Khách sạn
    // Khởi tạo một danh sách, check trong danh sách toàn bộ khách sạn, khách sạn nào có Id thành phố
    // đúng bằng pageId thì cho vào danh sách, pageId chính là Id thành phố được truyền vào hàm
    // danh sách khách sạn theo thành phố được chọn
    public String dsTheoThanhPho(int pageId) {
        listKhachSan = new ArrayList();
        for (KhachSan tmp : lstKS) {
            if (tmp.getIdThanhPho() == pageId) {
                listKhachSan.add(tmp);
            }
        }
        resetTimKiem();
        return "dsKhachSan";
    }

    public String dsTheoLoaiKhachSan(int pageId) {
        listKhachSan = new ArrayList();
        for (KhachSan tmp : lstKS) {
            if (tmp.getIdLoaiKhachSan() == pageId) {
                listKhachSan.add(tmp);
            }
        }
        resetTimKiem();
        return "dsKhachSan";
    }
    
    
    //lọc khách sạn
    public void locKhachSan() {
        ArrayList<KhachSan> lst = new ArrayList();
        for (KhachSan tmp : listKhachSanSave) {
            if (locXepHang(tmp) && locLoaiKhachSan(tmp) ) {
                lst.add(tmp);
            }
        }
        listKhachSan = lst;
    }

    private boolean locXepHang(KhachSan ks) {
        boolean check = false;
        for (int i = 0; i <= 5; i++) {
            if (listXepHang.get(i).isChecked()) {
                check = true;
                if (ks.getDanhGia() == i) {
                    return true;
                }
            }
        }
        return !check;
    }

    private boolean locLoaiKhachSan(KhachSan ks) {
        boolean check = false;
        for (Checkbox tmp : listLoaiKhachSan) {
            if (tmp.isChecked()) {
                check = true;
                if (ks.getTenLoaiKhachSan().equals(tmp.getLabel())) {
                    return true;
                }
            }
        }
        return !check;
    }

    // Khi chọn một khách sạn, lấy các thông tin của khách sạn đó bằng cách tìm trong
    // danh sách khách sạn khách sạn có id trùng với id đã chọn, đồng thời tìm ra
    // danh sách các phòng thuộc khách sạn đó, gán đã kiểm tra phòng trống = false
    public String thongTinKhachSan(int pageId) {
        for (KhachSan tmp : lstKS) {
            if (tmp.getIdKhachSan() == pageId) {
                khachSan = tmp;
                break;
            }
        }
        listPhong = new ArrayList();
        for (Phong tmp : lstP) {
            if (tmp.getIdKhachSan() == pageId) {
                listPhong.add(tmp);
            }
        }
        daKiemTraPhongTrong = false;
        listPhongSave = listPhong;
        daDatPhong = false;
        return "khachSan";
    }

    
    // Gán lại phòng đang đặt bằng phòng được chọn
    public void thongTinPhong(Phong p) {
        phongDangDat = p;
        strNgayDat = formatter.format(ngayDat);
        strNgayDen = formatter.format(ngayDen);
        strNgayTra = formatter.format(ngayTra);
        datPhong.setIdPhong(phongDangDat.getId());
        datPhong.setNgayDat(ngayDat);
        datPhong.setNgayDen(ngayDen);
        datPhong.setNgayTra(ngayTra);
        soNgayDatPhong = util.CompareDate.daysBetweenNoTime(ngayDen, ngayTra);
        datPhong.setDichVu(BuaAn.listBuaAn.get(khachSan.getBuaAn()).getTen());
        datPhong.setGhiChu("");
        datPhong.setThanhTien(phongDangDat.getGiaThue() * soNgayDatPhong);
        strThanhTien = String.format("%,d", datPhong.getThanhTien() * 1000);
        datPhong.setDaHuy(false);
    }

    // Tìm ra tất cả các phòng trống, đồng thời cho phép đặt phòng (do đã kiểm tra phòng trống)
    public void timPhongTrong() {
        Date homNay = new Date();
        if (util.CompareDate.compareNoTime(ngayDen, homNay) == -1
                || util.CompareDate.compareNoTime(ngayTra, homNay) == -1
                || util.CompareDate.compareNoTime(ngayTra, ngayDen) == -1) {
            msg.Message.errorMessage("Thông Báo", "Ngày nhập vào sai, vui lòng nhập lại!!!");
            daKiemTraPhongTrong = false;
            return;
        }
        daKiemTraPhongTrong = true;
        listPhong = new ArrayList();
        for (Phong tmp : listPhongSave) {
            if (kiemTraPhongTrong(tmp, ngayDen, ngayTra)) {
                listPhong.add(tmp);
            }
        }
    }

    // Check một phòng đã bị đặt trong một khoảng thời gian cho trước chưa
    // nếu khoảng thời gian ngày đến->ngày trả của phòng đó không giao với
    // tất cả các phòng bị đặt mà chưa hủy thì nghĩa là phòng đó trống
    public boolean kiemTraPhongTrong(Phong p, Date ngayDen, Date ngayTra) {
        if (listDatPhong != null) {
            for (DatPhong tmp : listDatPhong) {
                if (!tmp.isDaHuy() && tmp.getIdPhong() == p.getId()) {
                    if (!(ngayDen.after(tmp.getNgayTra()) || ngayTra.before(tmp.getNgayDen()))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Chọn Đặt phòng
    public void chonDatPhong() {
        if (!daKiemTraPhongTrong) {
            msg.Message.errorMessage("Thất Bại", "Bạn vẫn chưa kiểm tra phòng trống!");
            PrimeFaces current = PrimeFaces.current();
            current.executeScript("PF('dialog_datphong').hide();");
            return;
        }
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        //servlet cung cấp HttpSession interface cung cấp cách để xác định người dùng ghé thăm trang web và lưu trữ thông tin về người dùng đó.
        HttpSession session = request.getSession();
        TaiKhoan tk = (TaiKhoan) session.getAttribute("TaiKhoan");
        if (tk == null) {
            // tk null thì nghĩa là chưa đăng nhập, hiện msg thông báo và bật form đăng nhập
            PrimeFaces current = PrimeFaces.current();
            current.executeScript("PF('dialog_dangnhap').show();");
            msg.Message.errorMessage("Thất Bại", "Bạn cần đăng nhập trước!");
            return;
        }
        datPhong.setDaHuy(false);
        datPhong.setTaiKhoan(tk.getTenTaiKhoan());
        if (dao.DAODatPhong.insert(datPhong)) {
            // phòng vừa bị đặt phải được xóa khỏi danh sách phòng được đặt và
            // phải được thêm vào danh sách các phòng đã đặt
            listPhong.remove(phongDangDat);
            listDatPhong.add(new DatPhong(datPhong));
            daDatPhong = true;
            msg.Message.addMessage("Thành Công", "Đặt phòng thành công, vui lòng vào Lịch sử trong Trang cá nhân để xem thông tin Đặt phòng!");
        } else {
            msg.Message.errorMessage("Thất Bại", "Đặt phòng thất bại!");
        }
        PrimeFaces current = PrimeFaces.current();
        current.executeScript("PF('dialog_datphong').hide();");
    }

    //
    // Get - Set, Don't care
    //
    public ArrayList<KhachSan> getLstKS() {
        return lstKS;
    }

    public void setLstKS(ArrayList<KhachSan> lstKS) {
        this.lstKS = lstKS;
    }

    public ArrayList<KhachSan> getListKhachSan() {
        return listKhachSan;
    }

    public void setListKhachSan(ArrayList<KhachSan> listKhachSan) {
        this.listKhachSan = listKhachSan;
    }

    public String getTenThanhPhoTimKiem() {
        return tenThanhPhoTimKiem;
    }

    public void setTenThanhPhoTimKiem(String tenThanhPhoTimKiem) {
        this.tenThanhPhoTimKiem = tenThanhPhoTimKiem;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    public ArrayList<Checkbox> getListXepHang() {
        return listXepHang;
    }

    public void setListXepHang(ArrayList<Checkbox> listXepHang) {
        this.listXepHang = listXepHang;
    }

    public ArrayList<Checkbox> getListLoaiKhachSan() {
        return listLoaiKhachSan;
    }

    public void setListLoaiKhachSan(ArrayList<Checkbox> listLoaiKhachSan) {
        this.listLoaiKhachSan = listLoaiKhachSan;
    }

    public KhachSan getKhachSan() {
        return khachSan;
    }

    public void setKhachSan(KhachSan khachSan) {
        this.khachSan = khachSan;
    }

    public ArrayList<Phong> getListPhong() {
        return listPhong;
    }

    public void setListPhong(ArrayList<Phong> listPhong) {
        this.listPhong = listPhong;
    }

    public ArrayList<Phong> getLstP() {
        return lstP;
    }

    public void setLstP(ArrayList<Phong> lstP) {
        this.lstP = lstP;
    }

    public Date getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(Date ngayDat) {
        this.ngayDat = ngayDat;
    }

    public Date getNgayDen() {
        return ngayDen;
    }

    public void setNgayDen(Date ngayDen) {
        this.ngayDen = ngayDen;
    }

    public Date getNgayTra() {
        return ngayTra;
    }

    public void setNgayTra(Date ngayTra) {
        this.ngayTra = ngayTra;
    }

    public boolean isDaKiemTraPhongTrong() {
        return daKiemTraPhongTrong;
    }

    public void setDaKiemTraPhongTrong(boolean daKiemTraPhongTrong) {
        this.daKiemTraPhongTrong = daKiemTraPhongTrong;
    }

    public ArrayList<DatPhong> getListDatPhong() {
        return listDatPhong;
    }

    public void setListDatPhong(ArrayList<DatPhong> listDatPhong) {
        this.listDatPhong = listDatPhong;
    }

    public Phong getPhongDangDat() {
        return phongDangDat;
    }

    public void setPhongDangDat(Phong phongDangDat) {
        this.phongDangDat = phongDangDat;
    }

    public DatPhong getDatPhong() {
        return datPhong;
    }

    public void setDatPhong(DatPhong datPhong) {
        this.datPhong = datPhong;
    }

    public String getStrNgayDat() {
        return strNgayDat;
    }

    public void setStrNgayDat(String strNgayDat) {
        this.strNgayDat = strNgayDat;
    }

    public String getStrNgayDen() {
        return strNgayDen;
    }

    public void setStrNgayDen(String strNgayDen) {
        this.strNgayDen = strNgayDen;
    }

    public String getStrNgayTra() {
        return strNgayTra;
    }

    public void setStrNgayTra(String strNgayTra) {
        this.strNgayTra = strNgayTra;
    }

    public int getSoNgayDatPhong() {
        return soNgayDatPhong;
    }

    public void setSoNgayDatPhong(int soNgayDatPhong) {
        this.soNgayDatPhong = soNgayDatPhong;
    }

    public String getStrThanhTien() {
        return strThanhTien;
    }

    public void setStrThanhTien(String strThanhTien) {
        this.strThanhTien = strThanhTien;
    }

    public boolean isDaDatPhong() {
        return daDatPhong;
    }

    public void setDaDatPhong(boolean daDatPhong) {
        this.daDatPhong = daDatPhong;
    }

}
