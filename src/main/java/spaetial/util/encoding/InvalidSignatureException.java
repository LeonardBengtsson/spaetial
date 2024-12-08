package spaetial.util.encoding;

public class InvalidSignatureException extends RuntimeException {
    public InvalidSignatureException() { super(); }
    public InvalidSignatureException(Throwable e) { super(e); }
    public InvalidSignatureException(String s) { super(s); }
}
