package backtrace.io;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.UUID;


/**
 * Backtrace stack frame
 */
public class StackFrame implements Serializable {

    /**
     * Function where exception occurs
     */
    @SerializedName("funcName")
    @SuppressWarnings("FieldCanBeLocal")
    private String functionName;


    /**
     * Line number in source code where exception occurs
     */
    @SerializedName("line")
    @SuppressWarnings("FieldCanBeLocal")
    private Integer line = null;

    /**
     * Source code file name where exception occurs
     */
    @SerializedName("sourceCode")
    @SuppressWarnings("FieldCanBeLocal")
    private String sourceCode;

    /**
     * Source code file name where exception occurs
     */
    @SuppressWarnings("FieldCanBeLocal")
    private transient String sourceCodeFileName;

    /**
     * Create new instance of BacktraceStackFrame
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public StackFrame() {
    }

    /**
     * Create new instance of BacktraceStackFrame
     *
     * @param frame single stacktrace element
     */
    StackFrame(StackTraceElement frame) {
        if (frame == null || frame.getMethodName() == null) {
            return;
        }
        this.functionName = frame.getClassName() + "." + frame.getMethodName();
        this.sourceCodeFileName = frame.getFileName();
        this.sourceCode = UUID.randomUUID().toString();
        this.line = frame.getLineNumber() > 0 ? frame.getLineNumber() : null;
    }

    public String getFunctionName() {
        return functionName;
    }

    public Integer getLine() {
        return line;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public String getSourceCodeFileName() {
        return sourceCodeFileName;
    }
}