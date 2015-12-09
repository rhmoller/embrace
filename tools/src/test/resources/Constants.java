import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public abstract class Constants {
    public static boolean DEBUG; // = false
    public static int BIT_MASK; // = 0x0000fc00
    public static float AVOGADRO; // = 6.022e23
}
