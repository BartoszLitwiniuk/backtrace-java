package backtrace.io.http;

/**
 * Exception which represents a HTTP fault
 */
public class HttpException extends Exception {

    /**
     * HTTP status code
     */
    private Integer httpStatus;

    /**
     * Create new instance with HTTP status
     *
     * @param httpStatus received HTTP status code
     */
    @SuppressWarnings("unused")
    public HttpException(Integer httpStatus) {
        this(httpStatus, null);
    }

    /**
     * Create new instance with HTTP status and without message
     *
     * @param httpStatus received HTTP status code
     * @param message    received exception message
     */
    HttpException(Integer httpStatus, String message) {
        super(message);
        setHttpStatus(httpStatus);
    }

    /**
     * Set HTTP status code
     *
     * @param httpStatus received HTTP status code
     */
    private void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    /**
     * Return HTTP status code
     *
     * @return HTTP status code
     */
    @SuppressWarnings("unused")
    public int getHttpStatus() {
        return httpStatus;
    }
}