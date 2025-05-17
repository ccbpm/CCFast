package bp.da;

import bp.difference.SystemConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;


public class Log {

	private static Logger logger = LoggerFactory.getLogger(Log.class);
	/**空数组*/
	private static final Object[] EMPTY_ARRAY = new Object[] {};
	/**全类名*/
	private static final String FQCN = Log.class.getName();

	/**
	 * 获取栈中类信息
	 * param stackDepth 栈深（下标） 2：调用者类信息
	 * @return org.slf4j.spi.LocationAwareLogger
	 * @author wxt
	 * @date 2019/7/10 8:40
	 */
	private static LocationAwareLogger getLocationAwareLogger(final int stackDepth) {
		/**通过堆栈信息获取调用当前方法的类名和方法名*/
		JavaLangAccess access = SharedSecrets.getJavaLangAccess();
		Throwable throwable = new Throwable();
		StackTraceElement frame = access.getStackTraceElement(throwable, stackDepth);
		return (LocationAwareLogger) LoggerFactory.getLogger(frame.getClassName());
	}


	public static void DebugWriteInfo(String msg) {
		DefaultLogWriteLine(LogType.Info, msg);
	}

	public static void DebugWriteWarning(String msg) {
		DefaultLogWriteLine(LogType.Warning, msg);
	}

	public static void DebugWriteError(String msg) {
		DefaultLogWriteLine(LogType.Error, msg);
	}

	public static void DebugWriteError(Exception ex) {
		DefaultLogWriteLine(LogType.Error, ex.getMessage());
	}

	public static boolean isLoggerDebugEnabled() {
		
	    return SystemConfig.isDebug();

	}

	public static void DefaultLogWriteLineDebug(String msg) {
		DefaultLogWriteLine(LogType.Debug, msg);
	}



	public static void DefaultLogWriteLineInfo(String msg) {
		DefaultLogWriteLine(LogType.Info, msg);
	}


	public static void DefaultLogWriteLineWarning(String msg) {
		DefaultLogWriteLine(LogType.Warning, msg);
	}


	public static void DefaultLogWriteLineError(String msg) {
		DefaultLogWriteLine(LogType.Error, msg);
	}
	
	public static void DefaultLogWriteLineError(String msg, Throwable ex) {
		logger.error(msg, ex);
	}


	public static void DefaultLogWriteLine(LogType type, String info) {
		switch (type) {
		case Debug:
			getLocationAwareLogger(2).log(null, FQCN, LocationAwareLogger.DEBUG_INT, info, EMPTY_ARRAY, null);
			break;
		case Info:
			getLocationAwareLogger(2).log(null, FQCN, LocationAwareLogger.INFO_INT, info, EMPTY_ARRAY, null);
			break;
	case Warning:
			getLocationAwareLogger(2).log(null, FQCN, LocationAwareLogger.WARN_INT, info, EMPTY_ARRAY, null);
			break;
		case Error:
			getLocationAwareLogger(2).log(null, FQCN, LocationAwareLogger.ERROR_INT, info, EMPTY_ARRAY, null);
			break;
		}
	}


}
