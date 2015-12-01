import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.annotations.JsProperty;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface Constants {
    public final static boolean DEBUG = false;
    public final static int BIT_MASK = 0x0000fc00;
    public final static float AVOGADRO = 6.022e23;
}
