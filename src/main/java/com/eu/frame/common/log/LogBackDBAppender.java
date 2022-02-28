package com.eu.frame.common.log;

import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.db.DBAppenderBase;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * 自定义 LogBack 数据库 Appender
 * 用于向数据库中打印 log 日志
 * 以下为创建数据库表的 SQL 语句
 * <p>
 * DROP TABLE IF EXISTS `log`;
 * CREATE TABLE `log`  (
 * `id` int(11) NOT NULL AUTO_INCREMENT,
 * `level` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '日志级别, FATAL, ERROR, WARN, INFO',
 * `time` timestamp(0) NOT NULL COMMENT '时间',
 * `class` varchar(254) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '打印日志的类名称',
 * `method` varchar(254) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '打印日志的方法名称',
 * `line` char(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '打印日志位于当前类的第几行',
 * `thread` varchar(254) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '打印日志的线程名称',
 * `message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '日志内容',
 * PRIMARY KEY (`id`) USING BTREE
 * ) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;
 */
public class LogBackDBAppender extends DBAppenderBase<ILoggingEvent> {

    /**
     * 定义插入 SQL
     * 本项目定义, 将全部项目的日志打印到另一个数据库中
     * 日志表的名称以项目名为准
     * TODO 此处修改日志表名称为项目名
     */
    private final String insertSQL = "INSERT INTO log" +
            "(level, time, class, method, line, thread, message)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

    /**
     * 以下为插入 SQL 中各个参数对应的位置
     */
    private static final int LEVEL_STRING_INDEX = 1;
    private static final int TIMESTMP_INDEX = 2;
    private static final int CALLER_CLASS_INDEX = 3;
    private static final int CALLER_METHOD_INDEX = 4;
    private static final int CALLER_LINE_INDEX = 5;
    private static final int THREAD_NAME_INDEX = 6;
    private static final int MESSAGE_INDEX = 7;

    /**
     * 此处设定一个空的堆栈节点, 用于 logger 找不到堆栈节点的时候使用
     */
    private static final StackTraceElement EMPTY_CALLER_DATA = CallerData.naInstance();

    /**
     * 为 SQL 中的通配参数赋值
     *
     * @param event
     * @param connection
     * @param insertStatement
     * @throws Throwable
     */
    @Override
    protected void subAppend(ILoggingEvent event, Connection connection, PreparedStatement insertStatement) throws Throwable {

        insertStatement.setString(LEVEL_STRING_INDEX, event.getLevel().toString());
        insertStatement.setTimestamp(TIMESTMP_INDEX, new Timestamp(event.getTimeStamp()));
        insertStatement.setString(THREAD_NAME_INDEX, event.getThreadName());
        insertStatement.setString(MESSAGE_INDEX, event.getFormattedMessage());

        // This is expensive... should we do it every time?
        StackTraceElement caller = extractFirstCaller(event.getCallerData());
        insertStatement.setString(CALLER_CLASS_INDEX, caller.getClassName());
        insertStatement.setString(CALLER_METHOD_INDEX, caller.getMethodName());
        insertStatement.setString(CALLER_LINE_INDEX, Integer.toString(caller.getLineNumber()));

        int updateCount = 0;
        try {
            updateCount = insertStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if (updateCount != 1) {
            addWarn("Failed to insert loggingEvent");
        }
    }

    /**
     * 得到堆栈节点数组中的第一个节点, 若不存在则返回一个空节点
     *
     * @param callerDataArray
     * @return
     */
    private StackTraceElement extractFirstCaller(StackTraceElement[] callerDataArray) {
        StackTraceElement caller = EMPTY_CALLER_DATA;
        if (callerDataArray != null && callerDataArray.length > 0 && callerDataArray[0] != null)
            caller = callerDataArray[0];
        return caller;
    }

    /**
     * 次要插入, 感觉没什么卵用的样子
     *
     * @param event
     * @param connection
     * @param eventId
     * @throws Throwable
     */
    protected void secondarySubAppend(ILoggingEvent event, Connection connection, long eventId) throws Throwable {
    }

    /**
     * 初始化方法
     * 不需要做任何处理
     */
    @Override
    public void start() {
        super.start();
    }

    /**
     * 因为主键用的是自增主键, 通过 getGeneratedKeys 可以在 SQL 插入之后得到这个主键
     * 该方法用来获取这个方法
     * 但是我们实际并不需要这个主键所以直接返回 null
     *
     * @return
     */
    @Override
    protected Method getGeneratedKeysMethod() {

        // JDK 1.4 中添加了 PreparedStatement.getGeneratedKeys() 方法
        // 此处我们通过反射得到这个方法, 然后直接返回,
        // 当然, 最好的方式应该是定义一个全局的变量来接收这个方法,
        // 在初始化的时候执行这段代码来为这个全局变量赋值, 在本方法中直接返回这个全局变量即可
        // LogBack 的原作者其实就是这么做的, 详见: ch.qos.logback.classic.db.DBAppender
        /*Method getGeneratedKeysMethod;
        try {
            // the
            getGeneratedKeysMethod = PreparedStatement.class.getMethod("getGeneratedKeys", (Class[]) null);
        } catch (Exception ex) {
            getGeneratedKeysMethod = null;
        }
        return getGeneratedKeysMethod;*/

        return null;
    }

    /**
     * 获取插入语句
     *
     * @return
     */
    @Override
    protected String getInsertSQL() {
        return insertSQL;
    }

}
