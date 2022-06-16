import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

import java.util.ArrayList;

// 继承自GenericUDTF
public class HiveUDTF extends GenericUDTF {

    //初始化完成后，会调用process方法,真正的处理过程在process函数中，在process中，每一次forward()调用产生一行；如果产生多列可以将多个列的值放在一个数组中，然后将该数组传入到forward()函数。
    @Override
    public void process(Object[] objects) throws HiveException {
        String s = objects[0].toString();
        String[] split = s.split(";");
        for (int i = 0; i < split.length; i++) {
            String[] result = split[i].split(":");
            forward(result);
        }
    }

    //关闭信息
    @Override
    public void close() throws HiveException {

    }

    //UDTF首先会调用initialize方法，此方法返回UDTF的返回行的信息（返回个数，类型）
    @Override
    public StructObjectInspector initialize(ObjectInspector[] argOIs) throws UDFArgumentException {
        if (argOIs.length != 1) {
            throw new UDFArgumentLengthException("ExplodeMap takes only one argument");
        }
        if (argOIs[0].getCategory() != ObjectInspector.Category.PRIMITIVE) {
            throw new UDFArgumentException("ExplodeMap takes string as a parameter");

        }
        ArrayList<String> filedName = new ArrayList<>();
        ArrayList<ObjectInspector> filedIOs = new ArrayList<>();
        filedName.add("col1");
        filedIOs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        filedName.add("col2");
        filedIOs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        return ObjectInspectorFactory.getStandardStructObjectInspector(filedName, filedIOs);
    }
}
