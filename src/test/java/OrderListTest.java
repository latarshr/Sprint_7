import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import jdk.jfr.Description;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class OrderListTest {

    static private String endPointOrder = "/api/v1/orders";

    @Before
    public void setUp() throws IOException {
        Properties prop = new Properties();
        InputStream input = new FileInputStream("src/main/resources/config.properties");
        prop.load(input);
        RestAssured.baseURI = prop.getProperty("baseURI");
    }

    @Test
    @DisplayName("Проверяем список заказов - не пустой - код 200")
    @Description("get(/api/v1/orders)")
    public void checkStatusCodeAndOrderListNotNull() {
        given()
                .when()
                .get(endPointOrder)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("orders", notNullValue());
    }
}

