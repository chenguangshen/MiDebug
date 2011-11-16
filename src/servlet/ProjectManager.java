package servlet;

import core.*;
import java.io.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ProjectManager
 */
@WebServlet("/ProjectManager")
public class ProjectManager extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProjectManager() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    protected File locateProject(String projectName) {
    	return new File(Path.pjPath + projectName);
    }
    
    protected File locateFile(String projectName, String fileName) {
    	//System.out.println(basePath + projectName + "/" + fileName);
    	return new File(Path.pjPath + projectName + "/" + fileName);
    }
    
    protected boolean createProject(String projectName) {
    	File project = this.locateProject(projectName);
    	if(project.exists() && project.isDirectory()){
    		return false;
    	}
    	return project.mkdirs();
    }
    
    protected boolean createFile(String projectName, String fileName) {
    	File file = this.locateFile(projectName, fileName);
    	System.out.println(file.getPath());
    	if(file.exists() && !file.isDirectory()){
    		return false;
    	}
    	try {
			return file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
    }
    
    protected ArrayList<String> listProject(String name){
    	ArrayList<String> res = null;
    	File dir = new File(Path.pjPath + name);
    	String[] subDir = dir.list();
    	if(subDir != null){
    		 res = new ArrayList<String>();
    		 for(String str:subDir){
    			 res.add(str);
    		 }
    	}
    	 return res;
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter output = response.getWriter();
		boolean flag = false;
		String type = request.getParameter("type");
		
		if(type.equals("newpj")){
			String name = request.getParameter("name");
			flag = createProject(name);
			if(flag){
				output.println(0);
				System.out.println(0);
			}
			else{
				output.println(1);
				System.out.println(1);
			}
		}
		
		if(type.equals("newfile")){
			String projectname = request.getParameter("pjname");
			String filename = request.getParameter("fname");
			//System.out.println(projectname + " " + filename);
			flag = createFile(projectname, filename);
			if(flag){
				output.println(0);
				System.out.println(0);
			}
			else{
				output.println(1);
				System.out.println(1);
			}
		}
		
		if(type.equals("listpj")){
			String prefix = "";
			String name = request.getParameter("name");
			if(!name.equals("all")){
				prefix += name;
			}
			System.out.println("list requested");
			String str = "";
			ArrayList<String> res = listProject(prefix);
			if(res != null){
				for(String s:res){
					str += s + " ";
				}
				if(!str.equals("")){
					str = str.substring(0, str.length() - 1);
					output.print(str);
					System.out.println(str);
				}
			}else{
				output.println("");
			}
		}
		
		if(type.equals("getfile")){
			System.out.println("file requested");
			String projectname = request.getParameter("pjname");
			String filename = request.getParameter("fname");
			File file = locateFile(projectname, filename);
			
			//System.out.println(file.getPath());
			
			if(file.exists()){
				Scanner scanner = new Scanner(file);
				String str = "";
				while(scanner.hasNext()){
					str += scanner.nextLine() + "\n";
				}
				scanner.close();
				//System.out.println("*" + str + "*");
				if(!str.equals("")){
					output.println(str);
				}
			}
			else{
				output.println("-1");
				System.out.println("file not exist");
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter output = response.getWriter();
		String type = request.getParameter("type");
		System.out.println(type);
		if(type.equals("savefile")){
			System.out.println("save requested");
			String projectname = request.getParameter("pjname");
			String filename = request.getParameter("fname");
			File file = locateFile(projectname, filename);
			String str = request.getParameter("content");
			//System.out.println(str);

			PrintWriter fout = new PrintWriter(file);			
			fout.print(str);
			fout.close();
			
			output.println("0");
		}
	}
}