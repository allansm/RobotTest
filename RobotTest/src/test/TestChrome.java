package test;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
 
public class TestChrome {
	public static void download(String link,String path) {
		System.out.println("downloading:"+link);
		try {
			BufferedInputStream inputStream = new BufferedInputStream(new URL(link).openStream());
			System.out.println("download stream opened:"+link);
			if(!new File(path).exists()) {
				OutputStream os = new FileOutputStream(new File(path));
				//Thread.sleep(2000);
				FileOutputStream fileOS = new FileOutputStream(path); 
			    byte data[] = new byte[1024];
			    int byteContent;
			    System.out.println("writing download file..");
			    int counter = 0;
			    while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
			    	Thread.sleep(2);
			    	System.err.println(link+" "+(++counter/1024.0)+"/mb");
			        fileOS.write(data, 0, byteContent);
			    }
			    System.out.println("finished : "+link);
			    System.gc();
			}else {
				System.out.println("skipped file:"+path);
			}
		} catch (Exception e) {
		    // handles IO exceptions
			e.printStackTrace();
			System.out.println("download fail");
		}
	}
	public List<String> findTxt(String start,String end,String html){
		List<String> tag = new ArrayList();
		try {
			Pattern pattern = Pattern.compile(start+"(.*?)"+end);
			//System.out.println("compile<-------------");
			Matcher matcher = pattern.matcher(html);
			while(matcher.find()) {
				Thread.sleep(5);
				//System.out.println("searching txt..");
				try {
					tag.add(matcher.group(1));
					//System.out.println("found txt"+matcher.group(1));
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			return tag;
		}catch(Exception e) {
			//e.printStackTrace();
			return null;
		}
	}
	public List<String> findVideoLink(String html) {
		System.out.println("searching video...");
		List<String> list = findTxt("<video ", "</video>", html);
		List<String> link = new ArrayList();
		try {
			for(String s:list) {
				Thread.sleep(5);
				System.out.println("<video "+s+"</video>");
				for(String ss:findTxt("http", "\"", s)) {
					Thread.sleep(5);
					System.out.println("found video : http"+ss);
					link.add("http"+ss);
				}
			}
			return link;
		}catch(Exception e) {
			System.out.println("vazio");
			return null;
		}
	}
	public List<String> findLinks(String html) {
		List<String> list = findTxt("href=\"","\"",html);
		List<String> links = new ArrayList();
		try {
			for(String s:list) {
				Thread.sleep(5);
				links.add(s);
			}
			return links;
		}catch(Exception e) {
			return null;
		}
	}
	public void browseAndFindVideo(String url) {
		System.out.println("current page:"+url);
		String page = findPage(url);
		List<String> videosLinks = findVideoLink(page);
		for(String s:videosLinks) {
			try {
				//OutputStream os = null;
				//os = new FileOutputStream(new File("c:/Users/allan/downloads/os.txt"));
				//FileInputStream in = new FileInputStream(new File("c:/Users/allan/downloads/os.txt"));
				//os.write((Files.readAllBytes(Paths.get("c:/Users/allan/downloads/os.txt")).toString()+"\n"+s).getBytes());
				String fn = s.replaceAll("/", "").replaceAll(":", "");
				fn = "E:\\nsfwtest\\"+fn+".mp4";
				System.out.println(fn);
				download(s,fn);
			}catch(Exception e) {
				
			}
		}
		List<String> links = findLinks(page);
		Collections.shuffle(links);
		String prev = "";
		for(String s:links) {
			try {
				if(s.charAt(0) == '/' && s.charAt(1) != '/' && !s.equals(prev)) {
					prev = s;
					System.out.println("redirecting to :"+url+s);
					for(String ss:findVideoLink(findPage(url+s))) {
						Thread.sleep(5);
						try {
							//OutputStream os = null;
							//os = new FileOutputStream(new File("c:/Users/allan/downloads/os.txt"));
							//FileInputStream in = new FileInputStream(new File("c:/Users/allan/downloads/os.txt"));
							//os.write((Files.rea(Paths.get("c:/Users/allan/downloads/os.txt"))+"\n"+ss).getBytes());
							String fn = ss.replaceAll("/", "").replaceAll(":", "");
							fn = "E:\\robot_data\\"+fn+".mp4";
							System.out.println(fn);
							download(ss,fn);
						}catch(Exception e) {
							
						}
						Collections.shuffle(links);
					}
				}else {
					System.out.println("skip:"+s);
				}
			}catch(Exception e) {
				
			}
		}
	}
	public String findPage(String page) {
		//change this
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\Allan\\Downloads\\chromedriver\\chromedriver.exe");
		String html = "";
		ChromeOptions op = new ChromeOptions();
		op.setHeadless(true);
		WebDriver driver = new ChromeDriver(op);
		driver.get(page);
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		html = driver.getPageSource();
		
		driver.close();
		try {
			Thread.sleep(5);
			//Runtime.getRuntime().exec("taskkill /f /im chrome*");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return html;
	}
}
 