import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.After;
import org.junit.Test;

@RunWith(Parameterized.class)
public class CreateOrderTest {
    static private String endPointOrder = "/api/v1/orders";
    static private String endPointOrderCancel = "/api/v1/orders/cancel";
    private String[] color;
    private int track;
    private String firstName = "Jin Woo";
    private String lastName = "Sung";
    private String address = "Seoul, 1 apt.";
    private String metroStation = "Goyang";
    private String phone = "+7 999 888 66 55";
    private int rentTime = 5;
    private String deliveryDate = "2023-03-27";
    private String comment = "Jin Woo, save the world from the Rulers";

    public CreateOrderTest(String[] color) {
        this.color = color;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
    }

    @After
    public void tearDown() {
        String cancelTrack = "{\"track\":" + track + "}";
        given()
                .body(cancelTrack)
                .when()
                .put(endPointOrderCancel);
    }

    @Parameterized.Parameters
    public static Object[][] getOrderData() {
        return new Object[][]{
                { new String[] {"BLACK"}},
                { new String[] {"GREY"}},
                { new String[] {"BLACK", "GREY"}},
                { new String[] {}}
        };
    }

    @Test
    @DisplayName("Указать цвет, получить track")
    // После теста удалить этот заказ
    @Description("/api/v1/courier/login post: Parameterized color")
    public void createOrderTest() {
        Order order = new Order(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color);
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post(endPointOrder);
        response.then()
                .assertThat()
                .statusCode(201)
                .and()
                .body("track", notNullValue());

        String responseAsString = response.asString();
        JsonPath jsonPath = new JsonPath(responseAsString);
        track = jsonPath.getInt("track");
    }
}
