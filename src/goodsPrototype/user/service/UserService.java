package goodsPrototype.user.service;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;

import cn.itcast.commons.CommonUtils;
import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;
import goodsPrototype.user.dao.UserDao;
import goodsPrototype.user.domain.User;

/*
 *  user logic
 */
public class UserService {
	private UserDao userDao = new UserDao();
	
	/**
	 * validate user name
	 * @param loginname
	 * @return
	 */
	public boolean ajaxValidateLoginname(String loginname) {
		try {
			return userDao.ajaxValidateLoginname(loginname);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} 
	}
	
	/**
	 * validate email
	 * @param loginname
	 * @return
	 */
	public boolean ajaxValidateEmail(String email) {
		try {
			return userDao.ajaxValidateEmail(email);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} 
	}
	
	public void register(User user) {
		/*
		 *  1. data complete
		 */
		user.setUid(CommonUtils.uuid());
		user.setStatus(false);
		user.setActivationCode(CommonUtils.uuid()+CommonUtils.uuid());
		/*
		 *  2. insert into database
		 */
		try {
			userDao.add(user);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		/*
		 *  3. send email
		 */
		Properties prop = new Properties();
		try {
			prop.load(this.getClass().getClassLoader().getResourceAsStream("email_template.properties"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e1);
		}
		//login in the mail server
		String host = prop.getProperty("host");
		String name = prop.getProperty("username");
		String pass = prop.getProperty("password");
		Session session = MailUtils.createSession(host, name, pass);
		// create mail object
		String from = prop.getProperty("from");
		String to = user.getEmail();
		String subject = prop.getProperty("subject");
		// MessageFormat.format, first params{0} instead with params 1
		String content = MessageFormat.format(prop.getProperty("content"), user.getActivationCode());
		Mail mail = new Mail(from, to, subject, content);

		// send email
		try {
			MailUtils.send(session, mail);
		} catch (MessagingException | IOException e) {
			throw new RuntimeException(e);
		}		
	}
	/**
	 * activation service
	 * @param code
	 * @throws UserException 
	 * @throws SQLException 
	 */
	public void activation(String code) throws UserException {
		/*
		 * 1. use code query user
		 * 2. is null, throw useless code
		 * 3. not null, query status. status is true, throw already activate 
		 * 4. status is false, change status to true
		 */
		try {
			User user = userDao.findByCode(code);
			if(user==null)
				throw new UserException("useless activation code!");
			if(!user.isStatus())
				throw new UserException("already activated!");
			userDao.updateStatus(user.getUid(), true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}		
	}
	
	public User login(User user) {
		
		try {
			return userDao.findByLoginnameAndLoginpass(user.getLoginname(), user.getLoginpass());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void updatePassword(String uid, String oldPassword, String newPassword) throws UserException {
		try {
			boolean bool = userDao.findByUidAndPassword(uid, oldPassword);
			if(!bool) {
				throw new UserException("old password wrong!");
			}
			userDao.updatePassword(uid, newPassword);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		
		
	}
}
