package servlet;

import github.GitHubAPIWrapper;
import github.GitHubAuth;

import java.io.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * Servlet implementation class Redirect
 */
public class Redirect extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Redirect() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String code = request.getParameter("code");
		System.out.println("Code: " + code);
		
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("client_id", "b5bb4b5f001929ffe04c"));
		formparams.add(new BasicNameValuePair("code", code));
		formparams.add(new BasicNameValuePair("client_secret", "a996dfef024b5a8c57dfb542b775e3c7496851d4"));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("https://github.com/login/oauth/access_token");
		httppost.setEntity(entity);
		HttpResponse res = httpclient.execute(httppost);
		String str = EntityUtils.toString(res.getEntity());
		String token = str.substring(str.indexOf('=') + 1, str.indexOf('&'));
		GitHubAuth.auth_token = token;
		
		GitHubAPIWrapper.forkFile(GitHubAuth.username);
		
		response.sendRedirect("http://127.0.0.1:8080/CloudCompiler/editor.html");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}

}
