
import org.apache.hadoop.hive.ql.exec.UDAF;
import org.apache.hadoop.hive.ql.exec.UDAFEvaluator;

public class MutiStringConcat extends UDAF {
    public static class SumState {
        private String sumStr;
    }

    public static class SumEvaluator implements UDAFEvaluator {
        SumState sumState;

        public SumEvaluator() {
            super();
            sumState = new SumState();
            init();
        }

        @Override
        public void init() {
            sumState.sumStr = "";
        }

        /**
         * 来了一行数据
         *
         * @param s
         * @return
         */
        public boolean iterate(String s) {
            if (s.isEmpty()) {
                sumState.sumStr += s;
            }
            return true;
        }

        /**
         * 状态传递
         *
         * @return
         */
        public SumState terminatePartial() {
            return sumState;
        }

        /**
         * 子任务合并
         *
         * @param state
         * @return
         */
        public boolean merge(SumState state) {
            if (state != null) {
                sumState.sumStr += state.sumStr;
            }
            return true;
        }

        /**
         * 返回最终结果
         *
         * @return
         */
        public String terminate() {
            return sumState.sumStr;
        }
    }
}
