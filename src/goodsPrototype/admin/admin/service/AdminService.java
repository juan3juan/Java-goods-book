package goodsPrototype.admin.admin.service;

import java.sql.SQLException;

import goodsPrototype.admin.admin.dao.AdminDao;
import goodsPrototype.admin.admin.domain.Admin;

public class AdminService {

	AdminDao adminDao = new AdminDao();
	
	public Admin login(Admin admin) {
		try {
			return adminDao.find(admin.getAdminname(), admin.getAdminpwd());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
