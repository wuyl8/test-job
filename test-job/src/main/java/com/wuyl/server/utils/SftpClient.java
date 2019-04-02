package com.wuyl.server.utils;

/**
 * 描述：sftp服务器文件上传下载类
 * @version 1.0, 2012-12-03
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
 
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SftpClient {
	
	private ChannelSftp sftp = null;
	
	/**
	 * 连接sftp服务器
	 * 
	 * @param host
	 *            主机
	 * @param port
	 *            端口
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 * @return
	 */
	public ChannelSftp connect(String host, int port, String username, String password) {
		try {
			JSch jsch = new JSch();
			jsch.getSession(username, host, port);
			Session sshSession = jsch.getSession(username, host, port);
			System.out.println("Session created.");
			sshSession.setPassword(password);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(sshConfig);
			sshSession.connect();
			System.out.println("Session connected.");
			System.out.println("Opening Channel.");
			Channel channel = sshSession.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
			System.out.println("Connected to " + host + ".");
			// System.out.println("登录成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sftp;
	}

	/**
	 * 上传文件
	 * 
	 * @param directory
	 *            上传的目录
	 * @param uploadFile
	 *            要上传的文件
	 * @throws IOException 
	 */
	public boolean upload(String directory, String uploadFile) throws IOException {
		boolean flag = false;
		File file = new File(uploadFile);
		FileInputStream inStream = null;
		try {
			sftp.cd(directory);
			inStream = new FileInputStream(file);
			sftp.put(inStream, file.getName());
			System.out.println("上传成功！");
		} catch (Exception e) {
			flag = false;
			return flag;
		} finally {
			if(null != inStream){
				inStream.close();
			}
		}
		return flag;
	}

	/**
	 * 下载文件
	 * 
	 * @param directory
	 *            下载目录
	 * @param downloadFile
	 *            下载的文件
	 * @param saveFile
	 *            存在本地的路径
	 * @throws IOException 
	 */
	public boolean download(String directory, String downloadFile, String saveFile) throws IOException {
		boolean flag = false;
		File outfile = new File(saveFile);
		OutputStream oStream = null;
		try {
			sftp.cd(directory);
			oStream = new FileOutputStream(outfile);
			sftp.get(downloadFile, oStream);
		} catch (Exception e) {
			flag = false;
			return flag;
		} finally {
			if(null != oStream){
				oStream.close();
			}
		}
		return flag;
	}

	/**
	 * 删除文件
	 * 
	 * @param directory
	 *            要删除文件所在目录
	 * @param deleteFile
	 *            要删除的文件
	 */
	public void delete(String directory, String deleteFile) {
		try {
			sftp.cd(directory);
			sftp.rm(deleteFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 列出目录下的文件
	 * 
	 * @param directory
	 *            要列出的目录
	 * @return
	 * @throws SftpException
	 */
	public List<String> listFiles(String directory) throws SftpException {
		Vector fileVector = sftp.ls(directory);
        List<String> fileNameList = new ArrayList<String>();
        Iterator it = fileVector.iterator();
        while (it.hasNext()) {
            String fileName = ((ChannelSftp.LsEntry) it.next()).getFilename();
            if (".".equals(fileName) || "..".equals(fileName)) {
                continue;
            }
            fileNameList.add(fileName);
        }
		return fileNameList;
	}
	
	/**
	 * 重命名文件
	 * @param directory
	 * 			要命名文件所在目录
	 * @param oldFile
	 * 			老文件名
	 * @param newFile
	 * 			新文件名
	 *
	 */
	public void rename(String directory, String oldFile, String newFile) {
		try {
			sftp.cd(directory);
			sftp.rename(oldFile, newFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 关闭远程连接
	 * @Title: close  
	 * @return void
	 *
	 */
	public void close(){
		if(sftp != null){
			try {
				sftp.getSession().disconnect();
			} catch (JSchException e) {
			}
			try {
				sftp.quit();
			} catch (Exception e) {
			}
			try {
				sftp.disconnect();
			} catch (Exception e) {
			}
		}
	}
	public static void main(String[] args) {
		SftpClient sf = new SftpClient();
		String host = "133.64.177.72";
		int port = 22;
		String username = "mktftpuser";
		String password = "mkt^dev30!@";
		String directory = "/data1/mktftpuser/cpc";
		String saveFile = "D:\\test";
		String deleteFile = "delete.txt";
		String flie = "DW_LABEL_OFFER_HZ_M.20181105.861.00.DAT";
		ChannelSftp sftp = sf.connect(host, port, username, password);
//		sf.upload(directory, uploadFile, sftp);
//		sf.delete(directory, deleteFile, sftp);
		try {
			sftp.cd(directory);
			sftp.rename("ALREADY_DOWNLOADED_zhangpengtest.txt", "zhangpengtest.txt");
			System.out.println(sftp.ls(directory).toString());
//			sf.download(directory, flie, saveFile+"\\"+flie);
//			List<String> list  = sf.listFiles(directory);
//			for(String obj : list){
//				System.out.println(obj.toString());
//			}
//			sf.close();
//			sftp.cd(directory);
//			sftp.mkdir("ss");
//			System.out.println("finished");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
