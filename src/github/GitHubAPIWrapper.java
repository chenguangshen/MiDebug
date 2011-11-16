package github;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import org.eclipse.egit.github.core.*;
import org.eclipse.egit.github.core.client.*;
import org.eclipse.egit.github.core.service.*;

import core.Path;

public class GitHubAPIWrapper {

	public static void forkFile(String username) {
		com.github.api.v2.services.GitHubServiceFactory factory = com.github.api.v2.services.GitHubServiceFactory.newInstance();
		com.github.api.v2.services.RepositoryService service = factory.createRepositoryService();
		List<com.github.api.v2.schema.Repository> repos = service.getRepositories(username);

		try {
			for (com.github.api.v2.schema.Repository repo : repos) {
				String name = repo.getName();
				
				if(name.charAt(0) != 'C'){
					System.out.println(name);
					byte[] buf = new byte[1024];
					
					File file = new File(Path.pjPath + repo.getName());
					file.mkdirs();
					String path = Path.pjPath + repo.getName() + "/";

					ZipInputStream zip = service.getRepositoryArchive(username, name, com.github.api.v2.schema.Repository.MASTER);
					ZipEntry entry = null;

					entry = zip.getNextEntry();
		            while (entry != null) { 
		                //for each entry to be extracted
		                String entryName = entry.getName();
		                entryName = entryName.substring(entryName.lastIndexOf('/') + 1);
		                System.out.println(entryName);
		                if (entryName.length() == 0){
		                	zip.closeEntry();
			                entry = zip.getNextEntry();
		                	continue;
		                }
		                int n;
		                FileOutputStream fileoutputstream;
		                
		                fileoutputstream = new FileOutputStream(path + entryName);             

		                while ((n = zip.read(buf, 0, 1024)) > -1)
		                    fileoutputstream.write(buf, 0, n);

		                fileoutputstream.close(); 
		                zip.closeEntry();
		                entry = zip.getNextEntry();
		            }

		            zip.close();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int commitFile(String authcode, String username, String password, String projectName, String fileName) {
		// User log in
		GitHubClient client = new GitHubClient();
		client.setOAuth2Token(authcode);
		client.setCredentials(username, password);

		// Get the target repository
		RepositoryService rservice = new RepositoryService(client);
		Repository repo = null;
		
		try {
			
			repo = rservice.getRepository(username, projectName);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		File file = new File(Path.pjPath + projectName + "/" + fileName);
		Scanner scanner;
		String res = "";
		try {
			scanner = new Scanner(file);
			while(scanner.hasNext()){
				res += scanner.nextLine() + "\n"; 
			}
			scanner.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Create the new blob, which contains the file to be commit
		Blob blob = new Blob();
		blob.setContent(res);
		blob.setEncoding("utf8");

		try {
			// Get the master branch and its sha
			DataService service = new DataService(client);
			Reference ref = service.getReference(repo, "heads/master");
			String sha = ref.getObject().getSha();
			System.out.println(sha);

			// Create the new blob and get its sha
			String blobSha = service.createBlob(repo, blob);
			System.out.println(blobSha);

			// Get the current commit and its sha as the base tree.
			CommitService cservice = new CommitService(client);
			RepositoryCommit commit = cservice.getCommit(repo, sha);
			String baseTreeSha = commit.getCommit().getTree().getSha();
			System.out.println(baseTreeSha);

			// Create a new TreeEntry which contains the blob
			TreeEntry newEntry = new TreeEntry();
			newEntry.setMode(TreeEntry.MODE_BLOB);
			newEntry.setPath(fileName);

			newEntry.setSha(blobSha);
			newEntry.setType(TreeEntry.MODE_BLOB_EXECUTABLE);
			ArrayList<TreeEntry> newTreeList = new ArrayList<TreeEntry>();
			newTreeList.add(newEntry);

			// Create the new tree which contains the new TreeEntry
			Tree newTree = service.createTree(repo, newTreeList, baseTreeSha);
			for (TreeEntry te : newTree.getTree()) {
				System.out.println(te.getUrl() + " + " + te.getPath());
			}

			// Initialize a new Commit object
			Commit newCommit = new Commit();
			newCommit.setMessage("Commit of file " + " filename");
			List<Commit> parents = new ArrayList<Commit>();
			System.out.println("Parents:");
			System.out.println(commit.getUrl());
			System.out.println(commit.getSha());
			Commit temp = new Commit();
			temp = commit.getCommit();
			temp.setSha(commit.getSha());
			temp.setUrl(commit.getUrl());
			parents.add(temp);
			newCommit.setParents(parents);
			newCommit.setTree(newTree);

			// Do the commit
			Commit createdCommit = service.createCommit(repo, newCommit);
			String createdCommitSha = createdCommit.getSha();
			System.out.println(createdCommitSha);
			System.out.println(createdCommit.getUrl());

			// Update the Reference
			TypedResource newObj = new TypedResource();
			newObj.setSha(createdCommitSha);
			newObj.setUrl(createdCommit.getUrl());
			newObj.setType(TypedResource.TYPE_COMMIT);

			Reference newRef = new Reference();
			newRef.setRef("heads/master");
			newRef.setObject(newObj);

			service.editReference(repo, newRef, true);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public static void main(String[] args) {
		commitFile("37a1935a4061901cded733d8659b6b140c2e18d4", "X1O1", "scenery123", "robovero", "test.c");
	}
}
