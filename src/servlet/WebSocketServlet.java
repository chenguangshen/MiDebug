package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import core.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.grizzly.websockets.WebSocketEngine;

/**
 * Servlet implementation class WebSocketServlet
 */
@WebServlet("/WebSocketServlet")
public class WebSocketServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final GDBDebugApplication app = new GDBDebugApplication();
	private static int count = 0;

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WebSocketServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException {
    	super.init(config);
    	count = 1;
    	System.out.println("In INIT!!!!!");
    	System.out.println(config.getServletContext().getContextPath() + "/localdebug");
    	WebSocketEngine.getEngine().register(config.getServletContext().getContextPath() + "/localdebug", app);
    }


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter output = response.getWriter();
		output.println(count);
		output.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
