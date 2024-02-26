package com.xk.servlet;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.xk.dao.ManagerDao;
import com.xk.dao.StudentDao;
import com.xk.dao.TeacherDao;
import com.xk.model.User;
import com.xk.util.DbUtil;
import com.xk.util.StringUtil;

/**
 * �û�Servlet��
 * @author Administrator
 *
 */
public class UserServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private DbUtil dbUtil=new DbUtil();
	private ManagerDao managerDao=new ManagerDao();
	private TeacherDao teacherDao=new TeacherDao();
	private StudentDao studentDao=new StudentDao();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String action=request.getParameter("action"); // ��ȡ��������
		if("login".equals(action)){
			this.login(request, response);
		}else if("logout".equals(action)){
			this.logout(request, response);
		}
	}
	
	/**
	 * ��¼��֤
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void login(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		String userName=request.getParameter("userName");
		String password=request.getParameter("password");
		String userType=request.getParameter("userType");
		String error="";
		if(StringUtil.isEmpty(userName)){
			error="�û�������Ϊ�գ�";
		}else if(StringUtil.isEmpty(password)){
			error="���벻��Ϊ�գ�";
		}else if(StringUtil.isEmpty(userType)){
			error="��ѡ���û����ͣ�";
		}
		User user=new User(userName,password,userType);
		if(StringUtil.isNotEmpty(error)){
			request.setAttribute("user", user);
			request.setAttribute("error", error);
			request.getRequestDispatcher("login.jsp").forward(request, response);
			return;
		}
		Connection con=null;
		User currentUser=null;
		try{
			con=dbUtil.getCon();
			if("����Ա".equals(userType)){
				currentUser=managerDao.login(con, user);
			}else if("��ʦ".equals(userType)){
				currentUser=teacherDao.login(con, user);
			}else if("ѧ��".equals(userType)){
				currentUser=studentDao.login(con, user);
			}
			
			if(currentUser==null){
				error="�û������������";
				request.setAttribute("user", user);
				request.setAttribute("error", error);
				request.getRequestDispatcher("login.jsp").forward(request, response);
				return;
			}else{
				HttpSession session=request.getSession();
				session.setAttribute("currentUser", currentUser);
				response.sendRedirect("main.jsp");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				dbUtil.closeCon(con);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ��ȫ�˳�
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void logout(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		request.getSession().invalidate();
		response.sendRedirect("login.jsp");
	}
	
	

}
