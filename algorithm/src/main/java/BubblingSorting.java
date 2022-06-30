/**
 * @ClassName BubblingSorting
 * @Description TODO 冒泡排序
 * @Author ZFQ
 * @Date 2022/6/30 17:12
 * @Version 1.0
 **/
public class BubblingSorting {
    public static void main(String[] args) {
        //数组
        //链表
        int[] arr = {9, 3, 5, 11, 0};
        //数组排序
        //数组下标从零开始
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
                System.out.println();
            }
            for (int i1 : arr) {
                System.out.print(i1 + "\t");
            }
        }
    }
}
