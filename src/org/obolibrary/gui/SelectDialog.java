package org.obolibrary.gui;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

public abstract class SelectDialog {
	
	private final static Logger LOGGER = Logger.getLogger(SelectDialog.class);

	public abstract void show();
	
	public abstract File getSelected();
	
	public String getSelectedCanonicalPath() {
		File selected = getSelected();
		if (selected != null) {
			try {
				return selected.getCanonicalPath();
			} catch (IOException e) {
				LOGGER.error("Unable to get canonical path for file: "+selected.getAbsolutePath(), e);
			}
		}
		return null;
	}
	
	public static SelectDialog getFileSelector(final Frame frame, String title, String description, String...extensions) {
		if (isUnix()) {
			// due to a bug, which is fixed in JDK 7, the native AWT dialog 
			// does not use the correct renderer: meaning it looks very ugly.
			// http://bugs.sun.com/view_bug.do?bug_id=6913179
			// work around: use the built-in java swing version
			final JFileChooser fc = new JFileChooser();
			fc.setDialogTitle(title);
			fc.setFileFilter(new SuffixFileFilter(description, extensions));
			return new SelectDialog() {
				File selected = null;
				
				@Override
				public void show() {
					selected = null;
					int returnVal = fc.showOpenDialog(frame);
					
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						selected = fc.getSelectedFile();
					}
					
				}
				
				@Override
				public File getSelected() {
					return selected;
				}
			};
		}
		else {
			// try native
			final FileDialog dialog = new FileDialog(frame, title, FileDialog.LOAD);
			/*
			 * Extracted from the javadoc:
			 * 
			 * Filename filters do not function in Sun's reference
			 * implementation for Microsoft Windows.
			 */
			dialog.setFilenameFilter(new SuffixFilenameFilter(extensions));
			
			return new SelectDialog() {
				File selected = null;
				
				@Override
				public void show() {
					selected = null;
					dialog.setVisible(true);
					String fileName = dialog.getFile();
					String dirName = dialog.getDirectory();
					if (fileName != null && dirName != null) {
						selected = new File(dirName, fileName);
					}
				}
				
				@Override
				public File getSelected() {
					return selected;
				}
			};
		}
	}
	
	
	public static SelectDialog getFolderSelector(final Frame frame, String title) {
		if (isUnix()) {
			final JFileChooser folderFC = new JFileChooser();
			folderFC.setDialogTitle(title);
			folderFC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			folderFC.setAcceptAllFileFilterUsed(false);
			return new SelectDialog() {
				File selected = null;

				@Override
				public void show() {
					selected = null;
					int returnVal = folderFC.showOpenDialog(frame);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						selected = folderFC.getSelectedFile();
					}
				}

				@Override
				public File getSelected() {
					return selected;
				}
			};
		}
		else {
			// try native
			final FileDialog dialog = new FileDialog(frame, title, FileDialog.LOAD);
			dialog.setFilenameFilter(new FilenameFilter() {
				
				public boolean accept(File dir, String name) {
					// only show directories
					return false;
				}
			});
			
			return new SelectDialog() {
				File selected = null;
				
				@Override
				public void show() {
					selected = null;
					dialog.setVisible(true);
					String fileName = dialog.getFile();
					String dirName = dialog.getDirectory();
					if (fileName != null && dirName != null) {
						File file = new File(dirName, fileName);
						if (file.isDirectory()) {
							selected = file;
						}
						else if (file.isFile()) {
							selected = file.getParentFile();
						}
					}
					else if (dirName != null) {
						selected = new File(dirName);
					}
				}
				
				@Override
				public File getSelected() {
					return selected;
				}
			};
		}
	}
	
	
	private static class SuffixFileFilter extends FileFilter {
		
		private final String description; 
		private final Set<String> extensions;
		
		public SuffixFileFilter(String description, String...sufixes) {
			super();
			this.description = description;
			this.extensions = new HashSet<String>(Arrays.asList(sufixes));
		}

		public String getDescription() {
			return description;
		}
		
		public boolean accept(File f) {
			if (f != null) {
	            if (f.isDirectory()) {
	                return true;
	            }
	            String fileName = f.getName();
	            int i = fileName.lastIndexOf('.');
	            if (i > 0 && i < fileName.length() - 1) {
	                String extension = fileName.substring(i + 1).toLowerCase();
	                return extensions.contains(extension);
	            }
	        }
	        return false;
		}
	};
	
	private static class SuffixFilenameFilter implements FilenameFilter {
		
		private final Set<String> extensions;
		
		public SuffixFilenameFilter(String...suffixes) {
			extensions = new HashSet<String>(Arrays.asList(suffixes));
		}
		
		public boolean accept(File dir, String fileName) {
			if (fileName != null && fileName.length() > 0) {
				int i = fileName.lastIndexOf('.');
	            if (i > 0 && i < fileName.length() - 1) {
	                String extension = fileName.substring(i + 1).toLowerCase();
	                return extensions.contains(extension);
	            }
			}
			return false;
		}
	};
	
	private static boolean isUnix(){
		String os = System.getProperty("os.name").toLowerCase();
		//linux or unix
	    return (os.indexOf( "nix") >=0 || os.indexOf( "nux") >=0);
 
	}
}
