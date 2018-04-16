import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;
public class Evault {
	static final String installLinkFolderName = "exodus_install";
	static final String dataLinkFolderName = "exodus_data";

	static Path installDir;
	static Path installTarg;
	static Path userDataDir;
	static Path userDataTarg;
	public static void main(String[] args) { 
		// TODO Auto-generated method stub
		URL url = getLocation(Evault.class);
		File jarFile = urlToFile(url);
		String jarDir = jarFile.getParent();
		String homeDir = System.getProperty("user.home");
		System.out.println("jar directory is: " + jarDir.toString());
		System.out.println("Home directory is: " + homeDir.toString());
		userDataTarg= Paths.get(jarDir,dataLinkFolderName);
		installTarg = Paths.get(jarDir,installLinkFolderName);

		switch (OsCheck.getOperatingSystemType()) {
		case Linux:
			userDataDir=  Paths.get(homeDir,".config","exodus");
			break;
		case Windows:
			installDir = Paths.get(homeDir, "AppData","Roaming", "Exodus");
			userDataDir=  Paths.get(homeDir,"AppData", "Local","exodus");
			break;
		case MacOS:
			
			break;
		default:
		     System.err.println("Operating system is unknown! Guessing linux/unix-like?");
			userDataDir=  Paths.get(homeDir,".config","exodus");
			break;
		} 
		if (installTarg!= null) {
		//Check if install exsists on removeable drive
		if (Files.notExists(installTarg)) {
			//if not Check if install exsists on computer
			if (Files.exists(installDir)) {
				if (Files.isSymbolicLink(installDir)) {
			        System.err.println("Error! No install, but symlink detected on either the target computer, or the true install directory!");
			        JOptionPane.showMessageDialog(null, "No install detected on either the target computer, or the true install directory. A symlink was detected though, and will be deleted.", "No Install!", JOptionPane.ERROR_MESSAGE);
			        try {
						Files.deleteIfExists(installDir);
					} catch (IOException e) {
						System.err.println("failed to delete file due to IO exception!");
						e.printStackTrace();
					}
			        System.exit(1);
				}
				//if so, offer migration & prompt for setup
				Object[] options = {"Yes",
	                    "No",
	                    "Cancel"};
				int choice = JOptionPane.showOptionDialog(null,"There is a local install on this computer, but not in the target location. Should we move the install to the target?","Migrate Install?",
	    		JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[2]);
				switch (choice){
				case 0:
					try {
						Files.move(installDir,installTarg);
					} catch (IOException e) {
						System.err.println("failed to move install folder due to IO exception!");
						e.printStackTrace();
					}catch (SecurityException e) {
							System.err.println("failed to move install folder due to security problems! Check your permissions!");
					        JOptionPane.showMessageDialog(null, "failed to move install folder due to security problems! Check your permissions!", "Security Error", JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
					}
					break;
				case 1:
					break;
				default:
			        System.exit(0);
					break;
			
				}
			}else {
				//if not, request to install? terminate?
				System.err.println("Error! No install detected on either the target computer, or the true install directory!");
		        JOptionPane.showMessageDialog(null, "No install detected on either the target computer, or the true install directory. If your goal is to import your install to a removeable drive, make sure you have installed it first.", "No Install!", JOptionPane.ERROR_MESSAGE);
		        System.exit(1);
			}
		}
		//purge old symlink
		if (Files.isSymbolicLink(installDir)) {
			System.out.println("Found an old Symlink. Deleting.");
			try {
				Files.deleteIfExists(installDir);
			} catch (IOException e) {
				System.err.println("failed to delete file due to IO exception!");
				e.printStackTrace();
			}catch (SecurityException e) {
				System.err.println("failed to delete old Symlink Check your permissions!");
		        JOptionPane.showMessageDialog(null, "failed to delete symlink due to security problems! Check your permissions!", "Security Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}	
		}else if(Files.exists(installDir)) {
			//TODO: We have 2 diffrent installs!
	        JOptionPane.showMessageDialog(null, "Please note that an install is located in both on the removeable drive, and also locally on this computer. We will be running the one that is on the removeable drive!", "Warning", JOptionPane.WARNING_MESSAGE);

		}
		
		//Make links to our removeable install

		if (Files.notExists(installDir)) {
			try {
				Files.createSymbolicLink(installDir, installTarg);
			} catch (IOException e) {
				System.err.println("failed to make symlink due to IO exception!");
				e.printStackTrace();
			} catch (UnsupportedOperationException x) {
				System.err.println("failed to make symlink! Filesystem does not support this. Sorry!");
		        JOptionPane.showMessageDialog(null, "failed to make symlink! Filesystem does not support symlinks. Sorry, there is no way around this.", "Not Supported", JOptionPane.ERROR_MESSAGE);
				x.printStackTrace();
				System.exit(1);
			}
		}
		}
		
		//Check if udata exsists on removeable drive
		if (Files.notExists(userDataTarg)) {
			//if not, Check if udata exsists on computer			
			if (Files.exists(userDataDir)) {
				if (Files.isSymbolicLink(userDataDir)) {
			        System.err.println("Error! No user wallet data found, but symlink detected on the target computer!");
			        JOptionPane.showMessageDialog(null, "No user wallet data was detected. A symlink was detected though, and will be deleted.", "No Data!", JOptionPane.ERROR_MESSAGE);
			        try {
						Files.deleteIfExists(userDataDir);
					} catch (IOException e) {
						System.err.println("failed to delete file due to IO exception!");
						e.printStackTrace();
					}
			        System.exit(1);
				}
				//if so, offer migration
				Object[] options = {"Yes",
	                    "No",
	                    "Cancel"};
				int choice = JOptionPane.showOptionDialog(null,"There is a wallet on this computer, but not in the target location. Should we migrate the wallet to the target? No backup will be made.","Migrate Wallet?",
	    		JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[2]);
				switch (choice){
				case 0:
					try {
						Files.move(userDataDir,userDataTarg);
					} catch (IOException e) {
						System.err.println("failed to move data folder due to IO exception!");
						e.printStackTrace();
					}catch (SecurityException e) {
							System.err.println("failed to move data folder due to security problems! Check your permissions!");
					        JOptionPane.showMessageDialog(null, "failed to move data folder due to security problems! Check your permissions!", "Security Error", JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
					}
					break;
				case 1:
					break;
				default:
			        System.exit(0);
					break;
			
				}
			}else {
				//if not, continue
				
			}
		}
		//Purge Old Symlinks
		if (Files.isSymbolicLink(installDir)) {
			System.out.println("Found an old Symlink. Deleting.");
			try {
				Files.deleteIfExists(installDir);
			} catch (IOException e) {
				System.err.println("failed to delete file due to IO exception!");
				e.printStackTrace();
			}catch (SecurityException e) {
				System.err.println("failed to delete old Symlink Check your permissions!");
		        JOptionPane.showMessageDialog(null, "failed to delete symlink due to security problems! Check your permissions!", "Security Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}else if(Files.exists(installDir)) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
			Date date = new Date();

			Path backupLocation = Paths.get(userDataDir.getParent().toString(), "ExodusDataBackup-" + dateFormat.format(date));
			//TODO: We have 2 diffrent wallets!!!---------------------------------------------------------------------------
			Object[] options = {"Yes",
                    "Cancel"};
			int choice = JOptionPane.showOptionDialog(null,"There is a wallet on this computer, but also one in the target location. Should we overwrite the wallet on this computer? A backup will be made at " + backupLocation.toString(),"Migrate Wallet?",
    		JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[1]);
			switch (choice){
			case 0:
				try {
					Files.move(userDataDir,backupLocation);
				} catch (IOException e) {
					System.err.println("failed to move data folder due to IO exception!");
					e.printStackTrace();
			        System.exit(0);

				}catch (SecurityException e) {
						System.err.println("failed to move data folder due to security problems! Check your permissions!");
				        JOptionPane.showMessageDialog(null, "failed to move data folder due to security problems! Check your permissions!", "Security Error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
				        System.exit(0);

				}
				break;
			default:
		        System.exit(0);
				break;
		
			}
		}	
		//Make links to our removeable Udata
		if (Files.notExists(userDataDir)) {
			try {
				Files.createSymbolicLink(userDataDir, userDataTarg);
			} catch (IOException e) {
				System.err.println("failed to make symlink due to IO exception!");
				e.printStackTrace();
			} catch (UnsupportedOperationException x) {
				System.err.println("failed to make symlink! Filesystem does not support this. Sorry!");
		        JOptionPane.showMessageDialog(null, "failed to make symlink! Filesystem does not support symlinks. Sorry, there is no way around this.", "Not Supported", JOptionPane.ERROR_MESSAGE);
				x.printStackTrace();
				System.exit(1);
			}
		}
		
		//Run program
		try {
			Runtime.getRuntime().exec("Exodus.exe", null, installTarg.toFile());
		} catch (IOException e) {
			System.err.println("failed to run Exodus!");
	        JOptionPane.showMessageDialog(null, "Failed to run Exodus! Check your permissions!", "Failed to run Exodus", JOptionPane.ERROR_MESSAGE);
	    	e.printStackTrace();
		}

	}
	/**
	 * Gets the base location of the given class.
	 * <p>
	 * If the class is directly on the file system (e.g.,
	 * "/path/to/my/package/MyClass.class") then it will return the base directory
	 * (e.g., "file:/path/to").
	 * </p>
	 * <p>
	 * If the class is within a JAR file (e.g.,
	 * "/path/to/my-jar.jar!/my/package/MyClass.class") then it will return the
	 * path to the JAR (e.g., "file:/path/to/my-jar.jar").
	 * </p>
	 *
	 * @param c The class whose location is desired.
	 * @see FileUtils#urlToFile(URL) to convert the result to a {@link File}.
	 */
	public static URL getLocation(final Class<?> c) {
	    if (c == null) return null; // could not load the class

	    // try the easy way first
	    try {
	        final URL codeSourceLocation =
	            c.getProtectionDomain().getCodeSource().getLocation();
	        if (codeSourceLocation != null) return codeSourceLocation;
	    }
	    catch (final SecurityException e) {
	        System.err.println("Cannot access protection domain.");
	    }
	    catch (final NullPointerException e) {
	        System.err.println("Protection domain or code source is null.");
	    }

	    // NB: The easy way failed, so we try the hard way. We ask for the class
	    // itself as a resource, then strip the class's path from the URL string,
	    // leaving the base path.

	    // get the class's raw resource path
	    final URL classResource = c.getResource(c.getSimpleName() + ".class");
	    if (classResource == null) return null; // cannot find class resource

	    final String url = classResource.toString();
	    final String suffix = c.getCanonicalName().replace('.', '/') + ".class";
	    if (!url.endsWith(suffix)) return null; // weird URL

	    // strip the class's path from the URL string
	    final String base = url.substring(0, url.length() - suffix.length());

	    String path = base;

	    // remove the "jar:" prefix and "!/" suffix, if present
	    if (path.startsWith("jar:")) path = path.substring(4, path.length() - 2);

	    try {
	        return new URL(path);
	    }
	    catch (final MalformedURLException e) {
	        e.printStackTrace();
	        return null;
	    }
	} 

	/**
	 * Converts the given {@link URL} to its corresponding {@link File}.
	 * <p>
	 * This method is similar to calling {@code new File(url.toURI())} except that
	 * it also handles "jar:file:" URLs, returning the path to the JAR file.
	 * </p>
	 * 
	 * @param url The URL to convert.
	 * @return A file path suitable for use with e.g. {@link FileInputStream}
	 * @throws IllegalArgumentException if the URL does not correspond to a file.
	 */
	public static File urlToFile(final URL url) {
	    return url == null ? null : urlToFile(url.toString());
	}

	/**
	 * Converts the given URL string to its corresponding {@link File}.
	 * 
	 * @param url The URL to convert.
	 * @return A file path suitable for use with e.g. {@link FileInputStream}
	 * @throws IllegalArgumentException if the URL does not correspond to a file.
	 */
	public static File urlToFile(final String url) {
	    String path = url;
	    if (path.startsWith("jar:")) {
	        // remove "jar:" prefix and "!/" suffix
	        final int index = path.indexOf("!/");
	        path = path.substring(4, index);
	    }
	    try {
	        if (OsCheck.getOperatingSystemType()==OsCheck.OSType.Windows && path.matches("file:[A-Za-z]:.*")) {
	            path = "file:/" + path.substring(5);
	        }
	        return new File(new URL(path).toURI());
	    }
	    catch (final MalformedURLException e) {
	       System.err.println("URL is not completely well-formed.");
	    }
	    catch (final URISyntaxException e) {
		     System.err.println("URL is not completely well-formed, and threw a syntax error.");
	    }
	    if (path.startsWith("file:")) {
	        // pass through the URL as-is, minus "file:" prefix
	        path = path.substring(5);
	        return new File(path);
	    }
	    throw new IllegalArgumentException("Invalid URL: " + url);
	}
	
}
