package com.github.jianglei1994.seriallinereader.reader;

import com.github.jianglei1994.seriallinereader.exception.CloseReaderException;
import com.github.jianglei1994.seriallinereader.exception.CreateReaderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 保存多个SerialLineReader的实例
 *
 * @author jianglei43
 * @date 2019/10/17
 */
public class SerialLineReaderHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerialLineReaderHolder.class);

    private Map<SerialLineReaderKey, SerialLineReader> filePathAndReaderMap = new HashMap<>();

    public Optional<SerialLineReader> getReader(String filePath, String charsetName, int start){
        Optional<String> filePathOptional = Optional.ofNullable(filePath);
        Optional<String> charsetNameOptional = Optional.ofNullable(charsetName);
        return filePathOptional.flatMap(fp -> charsetNameOptional.map(cn -> doGetReader(fp, cn, start)));
    }

    private SerialLineReader doGetReader(String filePath, String charsetName, int start) {
        SerialLineReader serialLineReader = null;
        try{
            SerialLineReaderKey readerKey = new SerialLineReaderKey(filePath, charsetName);

            //1，从map中获取
            serialLineReader = filePathAndReaderMap.get(readerKey);

            //2，为空则创建，并添加到map
            if(serialLineReader == null){
                serialLineReader = new SerialLineReader(filePath, charsetName);
                filePathAndReaderMap.put(readerKey, serialLineReader);
            }else {
                LOGGER.info("获取到已创建的Reader {}", serialLineReader);
            }

            //3，lineIndex > start，则重新创建
            if(serialLineReader.isFrontRead(start)){
                LOGGER.info("serialLineReader [{}] 的 lineIndex [{}] > start[{}]，开始重新创建Reader", serialLineReader, serialLineReader.getLineIndex(), start);
                try{
                    serialLineReader.close();
                    serialLineReader = new SerialLineReader(filePath, charsetName);
                    filePathAndReaderMap.put(readerKey, serialLineReader);
                }catch (IOException e){
                    throw new CloseReaderException(e);
                }
            }


        }catch (CreateReaderException e){
            LOGGER.error("创建SerialLineReader出错",e);
            serialLineReader = null;
        }catch (CloseReaderException e){
            LOGGER.error("关闭SerialLineReader出错",e);
            serialLineReader = null;
        }catch (RuntimeException e){
            LOGGER.error("获取SerialLineReader出错，未知RuntimeException",e);
            serialLineReader = null;
        }

        return serialLineReader;
    }

    public void destory(){
        filePathAndReaderMap.entrySet().stream().forEach(
                entry -> {
                    try{
                        entry.getValue().close();
                    }catch (IOException e){
                        LOGGER.error("销毁ReaderHolder时，关闭SerialLineReader出错",e);
                    }
                    File file = new File(entry.getKey().getFilePath());
                    file.delete();
                }
        );
    }
}
