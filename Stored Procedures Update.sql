USE [QuanLyKhachSanDb]
GO
/****** Object:  StoredProcedure [dbo].[doanhThuTheoKhoangNam]    Script Date: 06/04/2020 12:00:23 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

create   procedure [dbo].[doanhThuTheoKhoangNam](
	@NamLay int,
	@KhoangNam int 
)
as begin
	;WITH LoatNam AS
	(
		SELECT
			i = 0,
			nam = @NamLay
		UNION ALL
		SELECT
			i = g.i+1,
			nam = g.nam-1
		FROM
			LoatNam AS G
		WHERE
		   g.i+1 < @KhoangNam
	)
	SELECT LoatNam.nam as Nam ,COALESCE( Temp.SoDatPhong , 0) as SoDatPhong ,COALESCE( Temp.TongTien , 0) as TongTien   FROM LoatNam
	left join (
	select top 5 YEAR(DatPhong.NgayDat) as Nam , COUNT(*) as SoDatPhong , Sum(DatPhong.ThanhTien) as TongTien from DatPhong 
	where DatPhong.NgayDat <= GETDATE()
	and YEAR(DatPhong.NgayDat)  <= @NamLay 
	and YEAR(DatPhong.NgayDat) >= @NamLay - @KhoangNam
	and DatPhong.DaHuy = 0
	group by YEAR(DatPhong.NgayDat) 
	) as Temp 
	on LoatNam.nam = Temp.Nam
	order by Nam asc
end
GO
/****** Object:  StoredProcedure [dbo].[laySoDatPhongCuaNam]    Script Date: 06/04/2020 12:00:23 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create   procedure [dbo].[laySoDatPhongCuaNam] (
	@nam int 
)
as begin
	;WITH Thang AS
	(
		SELECT
			i = 0,
			Thang = 1
		UNION ALL
		SELECT
			i = T.i+1,
			nam = T.Thang+1
		FROM
			Thang AS T
		WHERE
		   T.i+1 < 12
	)
	select T.Thang , COALESCE( SoDatPhong , 0) as SoDatPhong from Thang as T
	left join
	(select Thang , COUNT(Thang) as SoDatPhong from Thang , KhachSan
	left join Phong on KhachSan.Id = Phong.IdKhachSan
	left join DatPhong on Phong.Id = DatPhong.IdPhong
	where  dbo.kiemTraNgayTrongThang(DatPhong.NgayDen ,DatPhong.NgayTra , Thang , @nam) =1

	group by Thang 

	) as Temp on T.Thang = Temp.Thang 
end
GO
/****** Object:  StoredProcedure [dbo].[laySoDatPhongThangCuaTinh]    Script Date: 06/04/2020 12:00:23 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- Dat phong cua thang cua cac tinh 
create   procedure [dbo].[laySoDatPhongThangCuaTinh] (
	@thang int ,
	@nam int 
)
as begin

	select ThanhPho.Ten as ThanhPho , COALESCE( SoDatPhong , 0) as SoDatPhong  from ThanhPho 
	left join (
		select  ThanhPho.Ten , count(ThanhPho.Ten) as SoDatPhong from DatPhong , ThanhPho, Phong, KhachSan
		where dbo.kiemTraNgayTrongThang(DatPhong.NgayDen , DatPhong.NgayTra , @thang, @nam) =1
			and KhachSan.IdThanhPho = ThanhPho.Id
			and Phong.IdKhachSan = KhachSan.Id
			and DatPhong.IdPhong = Phong.Id
		group by ThanhPho.Ten
	) as temp on temp.Ten = ThanhPho.Ten
	order by SoDatPhong desc
end
GO
/****** Object:  StoredProcedure [dbo].[laySoPhongTheoThanhPho]    Script Date: 06/04/2020 12:00:23 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create   procedure [dbo].[laySoPhongTheoThanhPho] 
as begin
	select ThanhPho.Ten as ThanhPho , COUNT(Phong.Id) as SoPhong from ThanhPho 
	left join KhachSan on ThanhPho.Id = KhachSan.IdThanhPho
	left join Phong on Phong.IdKhachSan = KhachSan.Id
	group by ThanhPho.Ten
	order by SoPhong desc
end
GO
/****** Object:  StoredProcedure [dbo].[laySoPhongTrong]    Script Date: 06/04/2020 12:00:23 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create   procedure [dbo].[laySoPhongTrong]
(
	@ngaylay date
)
as begin
select (select COUNT(*) from Phong)-count(*) Tong from DatPhong , Phong
where DatPhong.NgayDen <= @ngaylay
	and DatPhong.NgayTra >= @ngaylay
	and DatPhong.IdPhong = Phong.Id
	and DatPhong.DaHuy = 0
end
GO
/****** Object:  StoredProcedure [dbo].[layTongSoKhachDangThue]    Script Date: 06/04/2020 12:00:23 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create   procedure [dbo].[layTongSoKhachDangThue]
(
	@ngaylay date
)
as begin
select count(*) Tong from DatPhong , Phong 
where DatPhong.NgayDen <= @ngaylay
	and DatPhong.NgayTra >= @ngaylay
	and DatPhong.IdPhong = Phong.Id
	and DatPhong.DaHuy = 0
end 
GO
/****** Object:  StoredProcedure [dbo].[phongDuocThueTrongNgay]    Script Date: 06/04/2020 12:00:23 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE   procedure [dbo].[phongDuocThueTrongNgay](
	@ngaylay date 
)
as begin
select distinct TaiKhoan.HoTen , TaiKhoan.TenTaiKhoan , TaiKhoan.GioiTinh , TaiKhoan.SoDienThoai , TaiKhoan.Email
, DatPhong.NgayDat , DatPhong.ThanhTien, DatPhong.NgayDen , DatPhong.NgayTra ,Phong.Id , Phong.Ten, Phong.DienTich, Phong.MoTa 
 , Phong.TienNghi,Phong.GiaThue  from Phong ,KhachSan ,DatPhong ,TaiKhoan
where 
	Phong.IdKhachSan = KhachSan.Id 
	and Phong.Id = DatPhong.IdPhong
	and TaiKhoan.TenTaiKhoan = DatPhong.TaiKhoan
	and NgayDen <= @ngaylay and NgayTra >= @ngaylay
	and DatPhong.DaHuy = 0
end 
GO
/****** Object:  StoredProcedure [dbo].[phongTrongTrongNgay]    Script Date: 06/04/2020 12:00:23 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE   procedure [dbo].[phongTrongTrongNgay](
	@ngaylay date 
)
as begin
select Phong.* ,KhachSan.Ten KhachSan , ThanhPho.Ten ThanhPho  from Phong ,KhachSan ,ThanhPho
where Phong.Id not in (
			select DatPhong.IdPhong from  DatPhong
			where DatPhong.NgayDen <= @ngaylay 
				and DatPhong.NgayTra >= @ngaylay
				and DatPhong.DaHuy = 1
			)
	and Phong.IdKhachSan = KhachSan.Id 
	and KhachSan.IdThanhPho = ThanhPho.Id
end 
GO
/****** Object:  StoredProcedure [dbo].[thongTinDatPhongNgay]    Script Date: 06/04/2020 12:00:23 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create   procedure [dbo].[thongTinDatPhongNgay](
	@ngaylay date 
)
as begin
	select * ,KhachSan.Ten as TenKhachSan from DatPhong , TaiKhoan , Phong , KhachSan
	where DatPhong.IdPhong = Phong.Id
		and TaiKhoan.TenTaiKhoan = DatPhong.TaiKhoan 
		and KhachSan.Id = Phong.IdKhachSan
		and DatPhong.NgayDat = @ngaylay
end
GO
