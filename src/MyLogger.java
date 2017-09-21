
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.nio.file.Path ;
import java.nio.file.Paths;

public class MyLogger {
	
    private static final Logger logger = Logger.getLogger(MyLogger.class
            .getName());
    private FileHandler fh = null;
    private static final Level precision=Level.FINE ;

    public MyLogger() {
    	System.out.println("Configuring Logger");
    	Path currentRelativePath = Paths.get("");
    	String s = currentRelativePath.toAbsolutePath().toString();
    	System.out.println(s);
        //just to make our log file nicer :)
        SimpleDateFormat format = new SimpleDateFormat("M-d_HHmmss");
        try {
            fh = new FileHandler(s+"/logs/MyLogFile_" + format.format(Calendar.getInstance().getTime()) + ".log");
        } catch (Exception e) {
            e.printStackTrace();
        }

        fh.setFormatter(new SimpleFormatter());
        logger.addHandler(fh);
        logger.setLevel(precision);  //level of log
        System.out.println("Logger configured");
    }

    public void doLogging() {
        logger.info("info msg");
        logger.severe("error message");
        logger.fine("fine message"); //won't show because to high level of logging
    }

    private static Logger getLogger(){
        if(logger == null){
            new MyLogger();
        }
        return logger;
    }
    public static void log(Level level, String msg){
        getLogger().log(level, msg);
//        System.out.println(msg);
    }
    public static void info(String msg){
        getLogger().log(Level.INFO, msg);
//        System.out.println(msg);
    }
    public static void warning(String msg){
        getLogger().log(Level.WARNING, msg);
//        System.out.println(msg);
    }
    public static void severe(String msg){
        getLogger().log(Level.SEVERE, msg);
//        System.out.println(msg);
    }
    public static void config(String msg){
        getLogger().log(Level.CONFIG, msg);
//        System.out.println(msg);
    }
    public static void fine(String msg){
        getLogger().log(Level.FINE, msg);
//        System.out.println(msg);
    }
    public static void finer(String msg){
        getLogger().log(Level.FINER, msg);
//        System.out.println(msg);
    }
    
    //For testing
    public static void main(String[] args){
    Path currentRelativePath = Paths.get("");
	String s = currentRelativePath.toAbsolutePath().toString();
	System.out.println(s);
    }
}  