package com.github.jianglei1994.seriallinereader;

import com.github.jianglei1994.seriallinereader.reader.SerialLineReaderHolder;
import com.github.jianglei1994.seriallinereader.reader.SerialLineReader;
import com.github.jianglei1994.seriallinereader.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * SerialFileReaderUtil的资源通过SerialReadOpenAspect切面进行初始化和销毁。
 * 使用时，请在对应的bean的方法加上SerialReadOpenAnnotation注解，然后替换调用链中的FileReaderUtil方法为SerialFileReaderUtil方法
 * 例如，在PromotionSmsFilterWorker的work方法上添加SerialReadOpenAnnotation注解，然后在调用链的CrowdFileReadServiceImpl.readSmsNormal方法中，替换FileReaderUtil.getDataFromFile为SerialFileReaderUtil.getDataFromFile
 *
 * @author jianglei
 * @date 2019/10/17
 */
public class SerialLineReaderUtil {

    public static final int FIRST_LINE_INDEX = SerialLineReader.FIRST_LINE_INDEX;

    private static final Logger LOGGER = LoggerFactory.getLogger(SerialLineReaderUtil.class);

    private static ThreadLocal<Boolean> serialFlagThreadLocal = new ThreadLocal<>();

    private static ThreadLocal<SerialLineReaderHolder> readerHolderThreadLocal = new ThreadLocal<>();

    /**
     * 初始化方法
     *
     * 建议使用SerialReadOpenAspect切面调用
     */
    public static void init(){
        try {
            checkInitState();

            serialFlagThreadLocal.set(true);
            readerHolderThreadLocal.set(new SerialLineReaderHolder());
            LOGGER.info("初始化ThreadLocal变量成功");
        } catch (RuntimeException e){
            LOGGER.error("初始化ThreadLocal变量异常", e);
            serialFlagThreadLocal.remove();
            readerHolderThreadLocal.remove();
        }
    }

    /**
     * 初始化时，校验两个ThreadLocal的值必须为空
     * @throws RuntimeException
     */
    private static void checkInitState() throws RuntimeException{
        if(serialFlagThreadLocal.get() != null){
            throw new IllegalStateException();
        }
        if(readerHolderThreadLocal.get() != null){
            throw new IllegalStateException();
        }
    }

    /**
     * 销毁方法。
     * 必须调用该方法，否则会造成资源泄露。
     *
     * 建议使用SerialReadOpenAspect切面调用
     */
    public static void destroy(){
        try{
            //1，清理标志位
            serialFlagThreadLocal.remove();

            //2，清理ReaderHolder
            SerialLineReaderHolder readerHolder = readerHolderThreadLocal.get();
            readerHolderThreadLocal.remove();
            if(readerHolder != null){
                readerHolder.destory();
            }
            LOGGER.info("销毁ThreadLocal变量成功");
        }catch (RuntimeException e){
            LOGGER.error("销毁ThreadLocal变量失败", e);
        }
    }

    /**
     * 判断是否进行了合理的初始化
     *
     * @return true 表示初始化ok
     */
    private static boolean initOk(){
        boolean serialOpen = Boolean.TRUE.equals(serialFlagThreadLocal.get());
        boolean readerHolderInit = readerHolderThreadLocal.get() != null;

        LOGGER.info("判断是否使用顺序读取器, serialFlagThreadLocal {}, readerHolderInit {}", serialOpen, readerHolderInit);
        return serialOpen && readerHolderInit;
    }

    /**
     * 读取文本行
     *
     * @param filePath 文本文件路径
     * @param charsetName 读取时，使用的字符编码
     * @param lineNumberIndex 开始下标，see FIRST_LINE_INDEX
     * @param readSize 读取数量
     * @return
     * @throws RuntimeException
     */
    public static List<String> readLines(String filePath, String charsetName, int lineNumberIndex, int readSize) throws RuntimeException {
        if(! initOk()){
            throw new IllegalStateException();
        }
        if(StringUtil.isBlank(filePath)){
            throw new IllegalArgumentException("filePath参数为空");
        }
        if(StringUtil.isBlank(charsetName)){
            throw new IllegalArgumentException("charsetName参数为空");
        }
        return readLinesViaSerialLineReader(filePath, charsetName, lineNumberIndex, readSize);
    }

    private static List<String> readLinesViaSerialLineReader(String filePath, String charsetName, int lineNumberIndex, int readSize) throws RuntimeException {
        SerialLineReaderHolder readerHolder = readerHolderThreadLocal.get();

        Optional<SerialLineReader> serialLineReaderOptional = readerHolder.getReader(filePath, charsetName, lineNumberIndex);

        if(! serialLineReaderOptional.isPresent()){
            throw new RuntimeException("获取SerialLineReader失败");
        }

        SerialLineReader serialLineReader = serialLineReaderOptional.get();

        List<String> list = serialLineReader.read(lineNumberIndex, readSize);

        return list;
    }

}
