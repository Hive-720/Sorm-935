import org.junit.jupiter.api.Test;

//public class SormApplicationTests {
//
//    @Test
//    public void test() {
//        String str = "Москва, Тверская";
//
//        String res = preparationFullTextSearch(str);
//
//        System.out.println();
//    }
//
//    private String preparationFullTextSearch(String data) {
//        if (data == null) {
//            return null;
//        }
//        StringBuilder result = new StringBuilder();
//        for (String str : data.split("[ ,;._]")) {
//            result.append("'").append(str.replace("'", "\\'")).append("':*&");
//        }
//        return result.toString().replaceAll("&$", "");
//    }
//}