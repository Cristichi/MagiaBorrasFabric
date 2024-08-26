package es.cristichi.mod.magiaborras.exc;

public class SpellPacketDecoderException extends RuntimeException {
    public SpellPacketDecoderException(String msg) {
        super(msg);
    }
    public SpellPacketDecoderException(String msg, Exception e) {
        super(msg, e);
    }
}
