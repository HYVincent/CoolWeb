package com.vincent.lwx.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.vincent.lwx.bean.ServiceStatus;
import com.vincent.lwx.bean.User;
import com.vincent.lwx.config.Config;
import com.vincent.lwx.db.MyBatisUtils;
import com.vincent.lwx.util.FileUtils;
import com.vincent.lwx.util.ResponseUtils;
import sun.awt.RepaintArea;

/**
 * @Title: CommonFileUpload.java
 * @Package com.vincent.lwx.controller
 * @Description: TODO(用一句话描述该文件做什么)
 * @author A18ccms A18ccms_gmail_com
 * @date 2017年3月21日 上午8:32:03
 * @version V1.0
 */
@Controller
public class CommonFileUpload {

	private static final String commonFileDir = "D:" + File.separator + "CoolWebFile" + File.separator + "userImg"
			+ File.separator;// 文件保存路径
	// 这里是公共目录
	private static final String tempFileDir = "D:" + File.separator + "CoolWebFile" + File.separator + "userImgTemp"
			+ File.separator;// 临时目录

	private static final String exceptionFileDir = "D:" + File.separator + "CoolWebFile" + File.separator
			+ "exception_file" + File.separator;
	
	private static final String roomImgFileDir = "D:" + File.separator + "CoolWebFile" + File.separator 
			+ "roomImg" + File.separator;

	/**
	 * 没有在applicationContent.xml中配置文件上传的配置，需要我们手动解析
	 *  多文件，单文件，都可以上传
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws FileUploadException
	 */
	@RequestMapping(value = "/upload.do.userHead")
	public void upload(@RequestParam("phone") String phone, HttpServletRequest request, HttpServletResponse response)
			throws IOException, FileUploadException {
		User users = UserUtils.selectUserOne(phone);
		String oldUrl = users.getHead();
		String oldImgPath = commonFileDir + oldUrl.substring(36);
		FileUtils.deleteFile(oldImgPath);
		String filePath = null;
		String tempPath = null;
		String name = null;
		FileUtils.createDir(commonFileDir + phone);
		filePath = commonFileDir + phone;
		FileUtils.createDir(tempFileDir + phone);
		tempPath = tempFileDir + phone;
		System.out.println("文件存放目录、临时文件目录准备完毕 ...");
		System.out.println("filePath-->:" + filePath);
		System.out.println("tempPath-->:" + tempPath);
		response.setContentType("text/plain;charset=utf-8");
		PrintWriter pw = response.getWriter();
		// 磁盘文件工厂，创建缓存文件
		DiskFileItemFactory diskFactory = new DiskFileItemFactory();
		// threshold 极限、临界值，即磁盘缓存大小设置
		diskFactory.setSizeThreshold(10 * 4 * 1024);
		// repository 贮藏室，即临时文件目录 ，设置文件的缓存路径
		diskFactory.setRepository(new File(tempPath));
		ServletFileUpload upload = new ServletFileUpload(diskFactory);
		// 设置允许上传的最大文件大小 ，如果是-1表示没有限制
		upload.setSizeMax(10 * 1024 * 1024);
		// 解决上传的文件名乱码
		upload.setHeaderEncoding("UTF-8");
		// 3、判断用户的表单提交方式是不是multipart/form-data,这也是我们在form中设置的那个属性的值 是不是……
		boolean bb = upload.isMultipartContent(request);
		if (!bb) {
			return;
		}
		// 解析HTTP请求消息头 ，也就是调用方法解析提交的内容并将其组装成一个个的FileItem对象
		// 而其中上传的一个文件就是一个FileItem对象
		List<FileItem> fileItems = (List<FileItem>) upload.parseRequest(new ServletRequestContext(request));
		// List<FileItem> fileItems = upload.parseRequest(request);
		try {
			Iterator<FileItem> iter = fileItems.iterator();
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				// 按照给定的编码格式获取上传文件内容
				String fieldValue = item.getString("UTF-8");
				// 获取标签名称
				String tagName = item.getFieldName();
				// 获取文件名称
				String fileName = item.getName();
				// 上传文件输入流，也就是整个上传文件的流
				InputStream input = item.getInputStream();
				System.out.println("tagName--->:" + tagName + "fileName--->" + fileName);
				// 判断这个FileItem是不是表单属性（他能判断是上传的文件还是表单属性）
				if (item.isFormField()) {
					System.out.println("处理表单内容 ...");
					processFormField(item, pw);
				} else {
					System.out.println("处理上传的文件 ...");
					name = processUploadFile(filePath, item);
				}
			}
			String imageUrl = Config.ImgUrlHead + phone + "/" + name;
			System.out.println("保存的用户头像网络url:" + imageUrl);
			// 保存头像地址到数据库
			String sql = "com.vincent.lwx.mapping.UserMapping.updateUserHead";
			Map<String, String> map = new HashMap<>();
			map.put("phone", phone);
			map.put("imageUrl", imageUrl);
			SqlSession sqlSession = MyBatisUtils.getSqlSession();
			sqlSession.update(sql, map);
			MyBatisUtils.commitTask(sqlSession);
			User user = UserUtils.selectUserOne(phone);
			if (user != null) {
				if (user.getHead().equals(imageUrl)) {
					// 保存成功
					ResponseUtils.renderJsonDataSuccess(response, "头像已上传", user);
				} else {
					// 保存失败
					ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "上传失败");
				}
			} else {
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "没有获取到User实体对象");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ResponseUtils.renderJsonDataFail(response, ServiceStatus.SERVICE_EXCEPTION,
					ServiceStatus.SERVICE_EXCEPTION_TEXT);
		} finally {
			pw.close();
		}
	}

	/**
	 * 上传APP异常日志
	 * 
	 * @param phone
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "uploadExecption", method = RequestMethod.POST)
	public void uploadExceptionFile(@RequestParam("phone") String phone, HttpServletRequest request,
			HttpServletResponse response) throws IOException, FileUploadException {
		FileUtils.createDir(exceptionFileDir + phone);
		String filePath = exceptionFileDir + phone;
		FileUtils.createDir(tempFileDir + phone);
		String tempPath = tempFileDir + phone;
		System.out.println("文件存放目录、临时文件目录准备完毕 ...");
		System.out.println("filePath-->:" + filePath);
		System.out.println("tempPath-->:" + tempPath);
		response.setContentType("text/plain;charset=utf-8");
		PrintWriter pw = response.getWriter();
		// 磁盘文件工厂，创建缓存文件
		DiskFileItemFactory diskFactory = new DiskFileItemFactory();
		// threshold 极限、临界值，即磁盘缓存大小设置
		diskFactory.setSizeThreshold(10 * 4 * 1024);
		// repository 贮藏室，即临时文件目录 ，设置文件的缓存路径
		diskFactory.setRepository(new File(tempPath));
		ServletFileUpload upload = new ServletFileUpload(diskFactory);
		// 设置允许上传的最大文件大小 ，如果是-1表示没有限制
		upload.setSizeMax(10 * 1024 * 1024);
		// 解决上传的文件名乱码
		upload.setHeaderEncoding("UTF-8");
		// 3、判断用户的表单提交方式是不是multipart/form-data,这也是我们在form中设置的那个属性的值 是不是……
		boolean bb = upload.isMultipartContent(request);
		if (!bb) {
			return;
		}
		// 解析HTTP请求消息头 ，也就是调用方法解析提交的内容并将其组装成一个个的FileItem对象
		// 而其中上传的一个文件就是一个FileItem对象
		List<FileItem> fileItems = (List<FileItem>) upload.parseRequest(new ServletRequestContext(request));
		// List<FileItem> fileItems = upload.parseRequest(request);
		try {
			Iterator<FileItem> iter = fileItems.iterator();
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				// 按照给定的编码格式获取上传文件内容
				String fieldValue = item.getString("UTF-8");
				// 获取标签名称
				String tagName = item.getFieldName();
				// 获取文件名称
				String fileName = item.getName();
				// 上传文件输入流，也就是整个上传文件的流
				InputStream input = item.getInputStream();
				System.out.println("tagName--->:" + tagName + "fileName--->" + fileName);
				// 判断这个FileItem是不是表单属性（他能判断是上传的文件还是表单属性）
				if (item.isFormField()) {
					System.out.println("处理表单内容 ...");
					processFormField(item, pw);
				} else {
					System.out.println("处理上传的文件 ...");
					processUploadFile(filePath, item);
				}
			}
			ResponseUtils.renderJsonDataSuccess(response, "已上传");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION,
					ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}

	
	
	/**
	 * 在spring的配置文件中配置了这个bean 则不在需要我们自己调用一个fileupload 的方法再去解析一次，而是直接使用： spring
	 * mvc MultipartFile 多文件上传
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/upload1.do.userHead")
	public void upload1(@RequestParam("phone") String phone, HttpServletRequest request, HttpServletResponse response)
			throws IllegalStateException,IOException {
		//http://weinan.iteye.com/blog/2338241
		  //创建一个通用的多部分解析器    
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());    
        //判断 request 是否有文件上传,即多部分请求    
        if(multipartResolver.isMultipart(request)){    
            //转换成多部分request      
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;    
            //取得request中的所有文件名    
            Iterator<String> iter = multiRequest.getFileNames();    
            while(iter.hasNext()){    
                //记录上传过程起始时的时间，用来计算上传时间    
                int pre = (int) System.currentTimeMillis();    
                //取得上传文件    
                MultipartFile file = multiRequest.getFile(iter.next());    
                if(file != null){    
                    //取得当前上传文件的文件名称    
                    String myFileName = file.getOriginalFilename();    
                    //如果名称不为“”,说明该文件存在，否则说明该文件不存在    
                    if(myFileName.trim() !=""){    
                        System.out.println(myFileName);    
                        //重命名上传后的文件名    
                        String fileName = "demoUpload" + file.getOriginalFilename();    
                        //定义上传路径    
                        String path = roomImgFileDir + phone + File.separator + fileName;    
                        File localFile = new File(path);    
                        file.transferTo(localFile);    
                    }    
                }    
                //记录上传该文件后的时间    
                int finaltime = (int) System.currentTimeMillis();    
                System.out.println(finaltime - pre);    
            }    
                
        }    
		
	}

	

	/**
	 * 处理表单内容
	 * 
	 * @param item
	 * @param pw
	 * @throws Exception
	 */
	private void processFormField(FileItem item, PrintWriter pw) throws Exception {
		String tagName = item.getFieldName();
		String fileName = item.getString("utf-8");
		pw.println(tagName + " : " + fileName + "\r\n");
	}

	/**
	 * 处理上传的文件
	 * 
	 * @param item
	 * @param pw
	 * @throws Exception
	 */
	private String processUploadFile(String filePath, FileItem item) throws Exception {
		// 文件名称
		String filename = item.getName();
		System.out.println("完整的文件名：" + filename);
		// 上传文件大小（byte）
		long fileSize = item.getSize();
		if ("".equals(filename) && fileSize == 0) {
			System.out.println("文件名为空 ...");
			return null;
		}
		System.out.println("filePath:-->" + filePath);
		System.out.println("filename:--->" + filename);
		// 创建保存文件路径
		File uploadFile = new File(filePath + File.separator + filename);
		if (!uploadFile.getParentFile().exists()) {
			uploadFile.getParentFile().mkdirs();
		}
		uploadFile.createNewFile();
		// 将上传上来的文件内容写到指定的文件
		item.write(uploadFile);
		// 向浏览器打印
		// pw.println(filename + " 文件保存完毕 ...");
		// pw.println("文件大小为 ：" + fileSize + "\r\n");
		return filename;
	}

}
