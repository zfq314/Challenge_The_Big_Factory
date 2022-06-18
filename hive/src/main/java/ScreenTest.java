import java.awt.*;

//获取屏幕个数
//复制模式下是一个屏幕
//拓展模式下是两个屏幕
public class ScreenTest {

    public static void main(String[] args) {
        // 创建GraphicsEnvironment对象
        GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();

        // 获取所有屏幕对象
        GraphicsDevice[] devices = g.getScreenDevices();

        for (int i = 0; i < devices.length; i++) {

            GraphicsDevice device = devices[i];
            String str = String.format("第%s个屏幕信息-->宽:%s,高:%s,ID:%s", i + 1, device.getDisplayMode().getWidth(),
                    device.getDisplayMode().getHeight(), device.getIDstring());
            System.out.println(str);
        }
    }

}
