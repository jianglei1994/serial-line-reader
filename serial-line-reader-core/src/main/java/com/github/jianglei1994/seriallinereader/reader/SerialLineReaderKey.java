package com.github.jianglei1994.seriallinereader.reader;


/**
 * Reader的Key，通过filePath和charsetName来唯一指向一个SerialLineReader
 *
 * @author jianglei43
 * @date 2019/10/17
 */
public class SerialLineReaderKey {
    private final String filePath;
    private final String charsetName;

    public SerialLineReaderKey(String filePath, String charsetName){
        this.filePath = filePath;
        this.charsetName = charsetName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getCharsetName() {
        return charsetName;
    }
}
