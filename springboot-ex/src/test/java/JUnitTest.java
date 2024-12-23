import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JUnitTest {

    @Test
    @DisplayName("1 + 2 는 3이다")
    public void junitTest(){
        int a = 1;
        int b = 2;
        int sum = 3;

        Assertions.assertEquals(a + b, sum);
    }

    @Test
    @DisplayName("1 + 2 는 4이다")
    public void junitFailedTest(){
        int a = 1;
        int b = 2;
        int sum = 4;

        Assertions.assertEquals(a + b, sum);
    }
}
