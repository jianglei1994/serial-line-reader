package com.github.jianglei1994.seriallinereader.reader;

import com.github.jianglei1994.seriallinereader.exception.CloseReaderException;
import com.github.jianglei1994.seriallinereader.exception.CreateReaderException;
import com.github.jianglei1994.seriallinereader.exception.ReadFrontException;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 顺序文件行读取器。把文件的行从头到尾读取一遍
 * 不能进行前向读取，例如，当已经读了0-10行之后，不能再去读取第5行，只能从11行开始读。
 *
 * @author jianglei43
 * @date 2019/10/17
 */
public class SerialLineReader implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerialLineReader.class);

    /**
     * 第一行的下标
     */
    public static final int FIRST_LINE_INDEX = 0;

    /**
     * 实际读取文件的reader
     */
    private BufferedReader reader;

    /**
     * 当前指向的文件行下标
     */
    private int lineIndex;

    /**
     * reader关闭的标志
     */
    private boolean closeFlag = false;

    public int getLineIndex() {
        return lineIndex;
    }

    public boolean isCloseFlag() {
        return closeFlag;
    }

    public SerialLineReader(String filePath, String charsetName) throws CreateReaderException{
        try{
            FileInputStream fileInputStream = new FileInputStream(filePath);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, charsetName);
            reader = new BufferedReader(inputStreamReader);

            lineIndex = FIRST_LINE_INDEX;

            LOGGER.info("创建SerialLineReader成功 :{}", this);
        }catch (Exception e){
            LOGGER.error("创建SerialLineReader失败",e);
            throw new CreateReaderException(e);
        }
    }

    @Override
    public void close() throws IOException {
        LOGGER.info("关闭SerialLineReader :{}", this);
        if(reader != null){
            reader.close();
            reader = null;
            closeFlag = true;
        }
    }

    /**
     * 从start行开始，读取size数量的行。文件的第一行下标为FIRST_LINE_INDEX。
     * 例如，read(0,5); 表示读取1-5行。
     *
     *
     * @param start
     * @param size
     * @return
     */
    public List<String> read(int start, int size){
        LOGGER.info("参数 start {}, size {}", start, size);
        
        if(closeFlag){
            throw new CloseReaderException("该SerialLineReader已关闭");
        }

        //1，前向读，则抛出异常
        if(isFrontRead(start)){
            throw new ReadFrontException("SerialLineReader不能前向读取");
        }

        //2，跳到开始处
        while (start > lineIndex){
            Optional<String> nextLineOptional = readNextLineAndIncreaseLineIndex();
            if(! nextLineOptional.isPresent()){
                break;
            }
        }

        int readSize = size < 0 ? Integer.MAX_VALUE : size;

        List<String> resultList = new LinkedList<>();

        //3，读取readSize大小的数据
        for(int i = 0; i < readSize; i++){
            Optional<String> nextLineOptional = readNextLineAndIncreaseLineIndex();
            if(nextLineOptional.isPresent()){
                resultList.add(nextLineOptional.get());
            }else {
                break;
            }
        }

        LOGGER.info("读取到的数据条数 {}", resultList.size());
        return resultList;
    }

    public boolean isFrontRead(int start){
        return start < lineIndex;
    }

    protected Optional<String> readNextLineAndIncreaseLineIndex(){
        try{
            String line = reader.readLine();
            if(line != null){
                lineIndex ++;
            }
            return Optional.ofNullable(line);
        }catch (IOException e){
            LOGGER.error("顺序读取器读取文件出错",e);
            return Optional.empty();
        }
    }
}
