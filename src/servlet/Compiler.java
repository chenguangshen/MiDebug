package servlet;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.*;

/**
 * Servlet implementation class RoboVeroCompiler
 */
@WebServlet("/Compiler")
public class Compiler extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Compiler() {
        super();
        // TODO Auto-generated constructor stub
    }
    
	protected void copyfile(File f1, File f2) {
		try {
			InputStream in = new FileInputStream(f1);
			OutputStream out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			System.out.println("File copied.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	protected File locateProject(String projectName) {
    	return new File(Path.pjPath + projectName);
    }
    
    protected File locateFile(String projectName, String fileName) {
    	//System.out.println(basePath + projectName + "/" + fileName);
    	return new File(Path.pjPath + projectName + "/" + fileName);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter output = response.getWriter();
		String type = request.getParameter("type");
		if(type.equals("compile")){
			System.out.println("compile requested");
			String projectname = request.getParameter("pjname");
			
			int platform = Integer.parseInt(request.getParameter("platform"));
			
			//String filename = request.getParameter("fname");
			File project = locateProject(projectname);
			
			Path.currentBinary = projectname;
			
			String[] files = project.list();
			for(String filename:files){
				File file = locateFile(projectname, filename);
				String newfilename = null;
				if(platform == 0){
					newfilename = Path.buildPath + "src/" + filename;
				}
				else{
					newfilename = Path.buildPath_stm + filename;
				}
				File newFile = new File(newfilename);
				if(newFile.exists()){
					newFile.delete();
				}
				copyfile(file, newFile);
			}
			
			String buildfilename = null;
			if(platform == 0){
				buildfilename = "bash " + Path.scriptPath + "build";
			}
			else{
				buildfilename = "bash " + Path.scriptPath + "build_stm";
			}
			
			int status = new ExecCmd(output).execShellScript(buildfilename);
			
			if(status == -1){
				Path.compileFlag = true;
				Path.currentBinary = projectname;
			}
			System.out.println(status);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		// TODO Auto-generated method stub
//		String str = request.getParameter("code");
//		
//		File file = new File("/home/nesl/robovero/firmware/src/main.c");
//	    if(file.createNewFile()){
//	    	PrintWriter out = new PrintWriter(file);
//	    	out.println(str);
//	    	out.flush();
//	    	out.close();
//	    }
//	    
//	    str.replace("\r\n", "<br>");
//		str.replace("\n", "<br>");
//		response.setContentType("text/html");
//	    PrintWriter output = response.getWriter();
//	    output.println("<html><body>Servlet works!<br>");
//	    output.println(str + "<br>");
//	    output.println(file.getAbsolutePath() + "<br>");
//	    output.println(request.getServletContext());
//	    output.println("</body></html>");
//	    output.flush();
//	    output.close();
//	    
//	    try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	    
//	    new BuildProject();
//	    
//	    try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	    
//	    new RunRoboVero();
	}
}
