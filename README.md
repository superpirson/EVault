# EVault
A small launcher program for Exodus wallet

Usage: Place the executable jar file or the .exe file in any directory or removable drive. 
When run, it will prompt for migration of the wallet to the location the executable is at. 
If an exodus install is detected on the computer but not in the directory the executable is, it will offer to move the install.


Windows: Just run the .exe file, it will prompt you if there are issues
Mac: run the jar file using the java launcher. If you want to migrate the install, you will need to run as a super-user, since mac does not allow access to the applications folder.
Linux: Install your Exodus-linux-x64 folder into your home directory if you want to move it over. Run the jar with java -jar EVault.jar
