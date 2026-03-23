package utils;
import java.io.FileInputStream;
import java.util.Properties;
public class ConfigReader {
static Properties p=new Properties();
static{
try{
p.load(new FileInputStream("resources/config.properties"));
}catch(Exception e){}
}
public static String get(String k){return p.getProperty(k);}
}
