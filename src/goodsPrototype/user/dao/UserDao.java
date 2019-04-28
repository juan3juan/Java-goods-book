package goodsPrototype.user.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.jdbc.TxQueryRunner;
import goodsPrototype.user.domain.User;

/*
 *  persistence 
 */
public class UserDao {
	QueryRunner qr = new TxQueryRunner();
	
	/**
	 * validate user name
	 * @param loginname
	 * @return
	 * @throws SQLException
	 */
	public boolean ajaxValidateLoginname(String loginname) throws SQLException {
		String sql = "select count(1) from t_user where loginname=?";
		Number number = (Number) qr.query(sql, new ScalarHandler(), loginname);		
		return number.intValue()==0;		
	}
	
	/**
	 * validate email
	 * @param email
	 * @return
	 * @throws SQLException
	 */
	public boolean ajaxValidateEmail(String email) throws SQLException {
		String sql = "select count(1) from t_user where loginname=?";
		Number number = (Number) qr.query(sql, new ScalarHandler(), email);		
		return number.intValue()==0;		
	}
	
	public void add(User user) throws SQLException {
		String sql = "insert into t_user values(?,?,?,?,?,?)";
		Object[] params = {user.getUid(), user.getLoginname(), user.getLoginpass(),
				user.getEmail(), user.isStatus(), user.getActivationCode()};
		qr.update(sql, params);
	}
	
	public User findByCode(String code) throws SQLException {
		String sql = "select * from t_user where activationCode=?";
		// return User type
		return qr.query(sql, new BeanHandler<User>(User.class), code);
	}
	
	public void updateStatus(String uid, boolean status) throws SQLException {
		String sql = "update t_user set status=? where uid=?";
		qr.update(sql, status, uid);
	}
	
	public User findByLoginnameAndLoginpass(String loginname, String loginpass) throws SQLException {
		String sql = "select * from t_user where loginname=? and loginpass=?";
		return qr.query(sql, new BeanHandler<User>(User.class), loginname, loginpass);
	}
	
	public boolean findByUidAndPassword(String uid, String oldPassword) throws SQLException {
		String sql = "select * from t_user where uid=? and loginpass=?";
		Number number = (Number) qr.query(sql, new ScalarHandler(), uid, oldPassword);
		return number.intValue() > 0;
	}
	
	public void updatePassword(String uid, String newPassword) throws SQLException {
		String sql = "update t_user set loginpass=? where uid=?";
		qr.update(sql, newPassword, uid);
	}
	
}
