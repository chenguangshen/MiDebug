package servlet;

import github.GitHubAPIWrapper;
import github.GitHubAuth;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GitHubConnector
 */
public class GitHubConnector extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GitHubConnector() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter output = response.getWriter();
		String type = request.getParameter("type");
		
		if(type.equals("login")){
			GitHubAuth.username = request.getParameter("username");
			GitHubAuth.password = request.getParameter("password");
			output.println(GitHubAuth.username);
			output.flush();
		}
		
		if(type.equals("commit")){
			String projectName = request.getParameter("pjname");
			String fileName = request.getParameter("filename");
			
			GitHubAPIWrapper.commitFile(GitHubAuth.auth_token, GitHubAuth.username, GitHubAuth.password, projectName, fileName);
			output.println("OK");
			output.flush();
			
		}
	}

}
